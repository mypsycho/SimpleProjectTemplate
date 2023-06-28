package com.obeo.project.generator;

import static com.obeo.project.generator.SgConstants.CFG_EXTENSION;
import static com.obeo.project.generator.SgConstants.DEFAULT_BINARIES;
import static com.obeo.project.generator.SgConstants.INJECT_PREFIX;
import static com.obeo.project.generator.SgConstants.P0;
import static com.obeo.project.generator.SgConstants.P1;
import static com.obeo.project.generator.SgConstants.PARAMS_SEPARATOR;
import static com.obeo.project.generator.SgConstants.PARAM_VALUE_TAG;
import static com.obeo.project.generator.SgConstants.PATH_SEPARATOR;
import static com.obeo.project.generator.SgConstants.TEMPLATE_PROP;
import static com.obeo.project.generator.SgObjects.getCfgContent;
import static com.obeo.project.generator.SgObjects.toStream;
import static com.obeo.project.generator.SgObjects.verify;
import static com.obeo.project.generator.SgSubstitutions.substitue;
import static com.obeo.project.generator.SgSubstitutions.substitueValue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleGenerator {

	static Map<String, Function<String, String>> PROPERTIES_FORMAT = Map.of("inject", it -> it
	// XML use CDATA
	// Properties is touchy
	);

	List<String> binExtensions = List
			.of(System.getProperty("template.binaries", DEFAULT_BINARIES).split(PATH_SEPARATOR));

	Path resources = Path.of(System.getProperty("template.resources", "resources"));
	
	
	Properties configuration;
	List<TemplateDescription> templates;
	Path targetLocation;

	SimpleGenerator(Path locationProperties) {
		this(locationProperties.getParent(), getCfgContent(locationProperties, true));
	}

	SimpleGenerator(Path location, Properties cfg) {
		configuration = cfg;
		targetLocation = location;

		templates = Stream.of(cfg.getProperty(TEMPLATE_PROP, "").split(PATH_SEPARATOR))
				.filter(it -> !SgObjects.isNullOrEmptyOrBlank(it)).map(it -> new TemplateDescription(it)).toList();

		verify(!templates.isEmpty(), "No 'template' in properties");
	}

	void perform() {
		System.out.println("\nGenerating project '" + targetLocation + "' using templates '"
				+ templates.stream().map(it -> it.id).collect(Collectors.joining(",")) + "'");

		Properties current = new Properties(configuration);
		
		// Built-in
		current.put("project.root", targetLocation.toAbsolutePath().normalize().toString());
		current.put("project.year", String.valueOf( Calendar.getInstance().get(Calendar.YEAR)));
		current.put("user.name", System.getProperty("user.name"));
		
		// Explicit user value can override
		current.putAll(configuration);
		
        System.out.println("\nInjecting default properties");
        templates.forEach(it -> it.inject(current));

        System.out.println("\nApplying templates");
        templates.forEach(it -> it.apply(current));
		
	}

	class TemplateDescription {

		final String id;
		final Properties config;
		final Path content;

		TemplateDescription(String templateId) {
			verify(!SgObjects.isNullOrEmptyOrBlank(templateId), "Invalid template value : [" + templateId + "]");
			id = templateId;
			config = getCfgContent(resources.resolve(id + CFG_EXTENSION), false);
			content = resources.resolve(id);
			verify(!config.isEmpty() && Files.exists(content), "No generation content for template : " + id);
		}


	    public void inject(Properties target) {
	        toStream(config)
                .filter(it -> SgSubstitutions.isInject(it.getKey()))
                // The inject_prefix based key name must not exist in root (or base, or user)
                // properties
                .forEach(it -> {
                    String key = it.getKey().substring(INJECT_PREFIX.length()).trim();
                    if (!target.containsKey(key)) {
                        target.setProperty(key, it.getValue());
                    }
                });
	    }

		
		void apply(Properties current) {
			substitue(content, targetLocation, current);
			// copy all files
			// from templateContent to targetLocation
			// applying substitution
			// TODO-later see how it works in practice
			// Create text for xml, properties, URI ??
			// Apply rule from templateDescriptor
			for (String key : config.stringPropertyNames()) {
			
				if (SgSubstitutions.isInject(key)) {
				    continue; // skip
				}

				if (isDeleteEntry(key)) {
					String path = substitueValue(key, current, P0, P1, new ArrayList<>());
					SgObjects.unsafe(() -> SgObjects.delete(targetLocation.resolve(path)));

				} else {	
					TemplateEntryDescription descr = new TemplateEntryDescription(key,
							config.getProperty(key), current);

					substitue(resources.resolve(descr.template),
							targetLocation.resolve(descr.target), 
							descr.context);
				}
				
			}
		}

		boolean isDeleteEntry(String key) {
		    String cfg = config.getProperty(key);
		    return cfg != null 
		            && SgConstants.DELETE_COMMAND.equals(cfg.trim());
		}
	}

	class TemplateEntryDescription {
		// Folder/project to create
		final String target;
		// Where to locate files to create in the Folder to create
		final String template;
		// Parameters to substitute
		final Properties context;

		TemplateEntryDescription(String key, String value, Properties parentContext) {

			verify(!SgObjects.isNullOrEmptyOrBlank(value), "No value for template entry:" + key);
			target = key.trim();
			context = new Properties(parentContext);
			
			List<String> args = Stream.of(value.split(PARAMS_SEPARATOR))
			        .map(it -> it.trim())
			        .collect(Collectors.toList());
			template = args.get(0); // TODO better, 
			args.remove(0);
			for (String arg : args) {
			    int valuePos = arg.indexOf(PARAM_VALUE_TAG);
			    if (valuePos == -1) {
			        System.out.println("Invalid argument is ignored for: " + target);
			    } else {
			        context.setProperty(arg.substring(0, valuePos).trim(), 
			            arg.substring(valuePos + 1, arg.length()).trim());
			    }
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
