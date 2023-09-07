package com.obeo.project.generator;

import static com.obeo.project.generator.SgConstants.CFG_EXTENSION;
import static com.obeo.project.generator.SgConstants.INJECT_PREFIX;
import static com.obeo.project.generator.SgConstants.PARAMS_SEPARATOR;
import static com.obeo.project.generator.SgConstants.PARAM_PREFIX;
import static com.obeo.project.generator.SgConstants.PARAM_VALUE_TAG;
import static com.obeo.project.generator.SgConstants.PATH_SEPARATOR;
import static com.obeo.project.generator.SgConstants.TEMPLATE_PROP;
import static com.obeo.project.generator.SgObjects.getCfgContent;
import static com.obeo.project.generator.SgObjects.toStream;
import static com.obeo.project.generator.SgObjects.trimmedSplit;
import static com.obeo.project.generator.SgObjects.verify;
import static com.obeo.project.generator.SgSubstitutions.substitueAll;
import static com.obeo.project.generator.SgSubstitutions.substituePath;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleGenerator {

    private List<String> missingProperties = new ArrayList<>();

    static Map<String, Function<String, String>> PROPERTIES_FORMAT = Map.of("inject", 
        it -> it
        // XML use CDATA
        // Properties is touchy
    );

    Path resources = Path.of(System.getProperty("template.resources", "resources"));

    String id;
    Properties configuration;
   
    Path targetLocation;

    List<TemplateDescription> templates;

    SimpleGenerator(Path cfgFile) {

        id = SgObjects.basename(cfgFile);
        configuration = getCfgContent(cfgFile, true);
        targetLocation = cfgFile.getParent();

        templates = Stream.of(configuration.getProperty(TEMPLATE_PROP, "")
            .split(PATH_SEPARATOR))
            .filter(it -> SgObjects.hasContent(it))
            .map(it -> new TemplateDescription(it.trim()))
            .collect(Collectors.toList());

        verify(!templates.isEmpty(), "No 'template' in properties");
    }


    public class GenContext implements SgSubstitutions.Context {
        final Properties config;

        public GenContext(Properties cfg) {
            config = cfg;
        }

        @Override
        public String getProperty(String name) {
            String result =  config.getProperty(name);
            if (result == null) {
                notifyMissingProp(name);
            }
            return result;
        }
        
        @Override
        public Path getRoot() {
            return targetLocation;
        }

        protected void notifyMissingProp(String name) {
            if (!missingProperties.contains(name)) {
                missingProperties.add(name);
                System.err.println("No such property: " + name);
            }
        }

    }

    void perform() {
        System.out.println("\nGenerating project '" 
                + targetLocation + "' using templates '"
                + configuration.getProperty(TEMPLATE_PROP, "") + "'");

        Properties current = new Properties();

        // Built-in
        current.put("project.root", targetLocation.getFileName().toString());
        current.put("project.id", id);
        
        current.put("project.year", String.valueOf( Calendar.getInstance().get(Calendar.YEAR)));
        current.put("user.name", System.getProperty("user.name"));

        System.out.println("\nInjecting default properties");
        templates.forEach(it -> it.inject(current));

        // Explicit user value can override built-in and injections.
        current.putAll(configuration);
        
        GenContext globalContext = new GenContext(current);
        System.out.println("\n= Applying templates");
        templates.forEach(it -> it.apply(globalContext));
    }

    
    class TemplateDescription {

        final String id;
        final Properties config;
        final Path content;

        TemplateDescription(String templateId) {
            verify(SgObjects.hasContent(templateId), 
                "Invalid template value : [" + templateId + "]");
            id = templateId;
            config = getCfgContent(resources.resolve(id + CFG_EXTENSION), true);
            content = resources.resolve(id);
            verify(!config.isEmpty() || Files.exists(content), 
                "No generation content for template : " + id);
        }


        public void inject(Properties context) {
            toStream(config)
                .filter(it -> SgSubstitutions.isInject(it.getKey()))
                // The inject_prefix based key name must not exist in root (or base, or user)
                // properties
                .forEach(it -> {
                    context.setProperty(it.getKey().substring(INJECT_PREFIX.length()), 
                        it.getValue());
                });
        }


        void apply(GenContext current) {
            System.out.println("== Applying template '" + id + "'");
            
            List<String> paths = new ArrayList<>();
            applyConfig(current, (it, target) -> {
                paths.add(it.getKey());
            });
            
            // Creates files using reusable templates.
            applyConfig(current, (it, target) -> {
                if (!SgConstants.DELETE_COMMAND.equals(it.getValue())
                        && SgObjects.hasContent(it.getValue())) {  
                    TemplateEntryDescription descr = new TemplateEntryDescription(it.getKey(),
                        it.getValue(), current.config);

                    descr.apply(resources, target);
                }
            });
            
            // Creates files overriding reusable templates.
            substitueAll(content, targetLocation, current);

            // Delete unwanted files from templates
            applyConfig(current, (it, target) -> {
                if (SgConstants.DELETE_COMMAND.equals(it.getValue())) {
                    SgObjects.unsafe(() -> SgObjects.delete(target));
                }
            });

        }
        
        private void applyConfig(GenContext current, BiConsumer<Map.Entry<String, String>, Path> task) {
            toStream(config)
                .filter(it -> !SgSubstitutions.isInject(it.getKey()))
                // entry must be sorted to apply container folder before child resource.
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(it -> {
    
                    Path path = substituePath(Path.of(it.getKey()), current);
                    Path target = targetLocation.resolve(path);
    
                    task.accept(it, target);
    
                });
        }
    }

    
    class TemplateEntryDescription extends GenContext {

        // Where to locate files to create in the Folder to create
        final List<String> templates;

        TemplateEntryDescription(String key, String value, Properties parentContext) {
            super(new Properties(parentContext));
            List<String> args = trimmedSplit(value, PARAMS_SEPARATOR);
            templates = trimmedSplit(args.get(0), PATH_SEPARATOR);

            // TODO better use a list ?
            args.remove(0);

            for (String arg : args) {
                int valuePos = arg.indexOf(PARAM_VALUE_TAG);
                if (valuePos == -1) {
                    System.out.println("Invalid argument is ignored for: " + key);
                } else {
                    config.setProperty(PARAM_PREFIX + arg.substring(0, valuePos).trim(), 
                        arg.substring(valuePos + 1, arg.length()).trim());
                }
            }
        }

        /**
         * Does some actions.
         * <p>
         * Details of behavior.
         * </p>
         *
         * @param resources
         * @param target
         */
        public void apply(Path resources, Path target) {
            templates.forEach(it -> substitueAll(resources.resolve(it), target, 
                TemplateEntryDescription.this));
        }

        private List<String> missingParams = new ArrayList<>();

        @Override
        public void notifyMissingProp(String name) {
            if (name.startsWith(PARAM_PREFIX)) {
                if (!missingParams.contains(name)) {
                    missingParams.add(name);
                    System.err.println("No such parameter: " 
                    + name.substring(PARAM_PREFIX.length()));
                }
            } else {
                super.notifyMissingProp(name);
            }
        }

    }	

    public static void main(String[] args) {
        verify(args.length == 1, "1 parameter is expected: properties-based configuration");

        System.out.println("Begin generation");
        SimpleGenerator gen = new SimpleGenerator(Path.of(args[0]));
        gen.perform();
        System.out.println("\nEnd of generation");
    }

}
