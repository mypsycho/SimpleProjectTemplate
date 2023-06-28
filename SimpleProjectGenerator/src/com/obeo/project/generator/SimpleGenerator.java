package com.obeo.project.generator;

import static com.obeo.project.generator.SgConstants.CFG_EXTENSION;
import static com.obeo.project.generator.SgConstants.DEFAULT_BINARIES;
import static com.obeo.project.generator.SgConstants.FOLDER_PARAMS_SEPARATOR_IN_TEMPLATE_DESC;
import static com.obeo.project.generator.SgConstants.PARAMS_SEPARATOR_IN_TEMPLATE_DESC_ENTRY;
import static com.obeo.project.generator.SgConstants.PATH_SEPARATOR;
import static com.obeo.project.generator.SgConstants.PATTERN_PARAM_VALUE_IN_TEMPLATE_DESC_ENTRY;
import static com.obeo.project.generator.SgConstants.PARAM_PREFIX;
import static com.obeo.project.generator.SgConstants.TEMPLATE_PROP;
import static com.obeo.project.generator.SgConstants.P0;
import static com.obeo.project.generator.SgConstants.P1;
import static com.obeo.project.generator.SgObjects.getCfgContent;
import static com.obeo.project.generator.SgObjects.verify;
import static com.obeo.project.generator.SgSubstitutions.substitue;
import static com.obeo.project.generator.SgSubstitutions.substitueValue;
import static com.obeo.project.generator.SgSubstitutions.canSubstitute;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

		Properties current0 = new Properties(configuration);
		
		current0.put("project.root", targetLocation.toAbsolutePath().normalize().toString());
		current0.put("project.year", ""+Calendar.getInstance().get(Calendar.YEAR));
		current0.put("user.name", System.getProperty("user.name"));

		templates.forEach(it -> {
			Properties current = new Properties(current0);
			
			System.out.println("\nInjecting properties from " + it.id);
			SgSubstitutions.inject(it.config, current);
            
            System.out.println("\n----------Applying properties from " + it.id + "----------");
			it.apply(current);
		});
		
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

		void apply(Properties current) {
			substitue(content, targetLocation, current);
			// copy all files
			// from templateContent to targetLocation
			// applying substitution
			// TODO-later see how it works in practice
			// Create text for xml, properties, URI ??
			// Apply rule from templateDescriptor
			for (Object key : config.keySet()) {
			
				if (!SgSubstitutions.isInject(key)) {
					if(SgSubstitutions.isDeleteCommand(config.getProperty((String)key))) {
						String finalTarget = (String)key;
						if (canSubstitute((String)key,P0,P1)) {
							finalTarget = substitueValue((String)key, current, P0, P1, new ArrayList<>());
							SgObjects.verify(finalTarget != null, "Cannot substitute Path:" + key);
						}
						SgSubstitutions.deleteFileOrDirectory(targetLocation.resolve(finalTarget).normalize().toAbsolutePath().toString());
					} else {	
						TemplateEntryDescription templateEntryDesciption = new TemplateEntryDescription(key.toString(),
								config.get(key).toString());
						Properties currentPlus = new Properties(current);
						currentPlus.putAll(templateEntryDesciption.entryConfig);
						substitue(resources.resolve(templateEntryDesciption.sourceRelativePath),
								targetLocation.resolve(templateEntryDesciption.entryKey), currentPlus);
					}
				}
				
				
			}
		}
	}

	class TemplateEntryDescription {
		// Folder/project to create
		final String entryKey;
		// Where to locate files to create in the Folder to create
		final String sourceRelativePath;
		// Parameters to substitute
		final Properties entryConfig;

		TemplateEntryDescription(String entryKey, String entryValue) {
			verify(!SgObjects.isNullOrEmptyOrBlank(entryKey), "Invalid template entry description key : [" + entryKey + "]");
			verify(!SgObjects.isNullOrEmptyOrBlank(entryValue),
					"Invalid template entry description value mapped to key=[" + entryKey + "]: [" + entryValue + "]");
			this.entryKey = entryKey.trim();
			entryConfig = new Properties();
			if (entryValue.contains(FOLDER_PARAMS_SEPARATOR_IN_TEMPLATE_DESC)) {
				String[] values = entryValue.split(Pattern.quote(FOLDER_PARAMS_SEPARATOR_IN_TEMPLATE_DESC));
				sourceRelativePath = values[0].trim();
				for (int i = 1; i < values.length; i++) {
					if (values[i].contains(PARAMS_SEPARATOR_IN_TEMPLATE_DESC_ENTRY)) {
						Matcher matcher = PATTERN_PARAM_VALUE_IN_TEMPLATE_DESC_ENTRY.matcher(values[i]);
						if (matcher.matches()) {
							entryConfig.put(PARAM_PREFIX
									+ (matcher.group(1).trim()), matcher.group(2).trim());
						}
					}
				}
			} else {
				sourceRelativePath = entryValue.trim();
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
