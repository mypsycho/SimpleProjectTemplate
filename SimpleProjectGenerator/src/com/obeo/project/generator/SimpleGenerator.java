package com.obeo.project.generator;

import static com.obeo.project.generator.SgConstants.CFG_EXTENSION;
import static com.obeo.project.generator.SgConstants.DEFAULT_BINARIES;
import static com.obeo.project.generator.SgConstants.PATH_SEPARATOR;
import static com.obeo.project.generator.SgConstants.TEMPLATE_PROP;
import static com.obeo.project.generator.SgObjects.getCfgContent;
import static com.obeo.project.generator.SgObjects.verify;
import static com.obeo.project.generator.SgSubstitutions.*;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SimpleGenerator {
    
    
    static Map<String, Function<String, String>> PROPERTIES_FORMAT = Map.of(
        "inject", it -> it
        // XML use CDATA
        // Properties is touchy
        );
    


    List<String> binExtensions = List.of(
            System.getProperty("template.binaries", DEFAULT_BINARIES)
            .split(PATH_SEPARATOR));
    
    Path resources = Path.of(System.getProperty("template.resources", "resources"));
    Properties configuration;
    List<TemplateDescription> templates;
    Path targetLocation;
    
    SimpleGenerator(Path locationProperties) {
        this(locationProperties.getParent(), 
            getCfgContent(locationProperties, true));
    }
    
    SimpleGenerator(Path location, Properties cfg) {
        configuration = cfg;
        targetLocation = location;
        
        templates = Stream.of(cfg.getProperty(TEMPLATE_PROP, "").split(PATH_SEPARATOR))
                .map(it -> new TemplateDescription(it))
                .toList();
        
        verify(!templates.isEmpty(), "No 'template' in properties");
    }
    
    void perform() {
        System.out.println("Generating project '" + targetLocation 
            + "' using templates '" 
            + templates.stream()
                .map(it -> it.id)
                .collect(Collectors.joining(","))
            + "'"
        );
        
        Properties current = new Properties(configuration);
        
        templates.forEach(it -> {
            System.out.println("Injecting properties from " + it.id);
            SgSubstitutions.inject(it.config, current); 
        });
        
        
        templates.forEach(it -> {
            System.out.println("Applying properties from " + it.id);
            it.apply(current);
        });

    }

    class TemplateDescription {
        
        final String id;
        final Properties config;
        final Path content;
        
        TemplateDescription(String templateId) {
            id = templateId;
            config = getCfgContent(resources.resolve(id + CFG_EXTENSION), false);
            content = resources.resolve(id);
            verify(!config.isEmpty() || Files.exists(content),
                "No generation content for template : " + id);
        }

        
        void apply(Properties current) {
            substitue(content, targetLocation, current);
            // copy all files 
            //   from templateContent to targetLocation
            //   applying substitution
            
            // Create text for xml, properties, URI ?? 
            
            // Apply rule from templateDescriptor
        }
        
    }
    
    
    
    public static void main(String[] args) {
        verify(args.length == 1, "1 parameter is expected: properties-based configuration");
        
        SimpleGenerator gen = new SimpleGenerator(Path.of(args[0]));
        gen.perform();
    }


}
