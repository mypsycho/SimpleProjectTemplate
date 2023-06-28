/*******************************************************************************
 * Copyright (c) 2023 Siemens EDA. All rights reserved.
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package com.obeo.project.generator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * SgConstants of SimpleProjectGenerator.
 * <p>
 * 
 * </p>
 *
 * @author nperansin
 */
public class SgConstants {

	public static final String DEFAULT_BINARIES = "jpg,png,gif";
	public static final String CFG_EXTENSION = ".properties";
	public static final String TEMPLATE_PROP = "template";

	public static final String INJECT_PREFIX = "<inject>";
	public static final String PATH_SEPARATOR = ",";

	public static final String P0 = "${";
	public static final String P1 = "}";

	public static final String F0 = "%inject:";
	public static final String F1 = "%";

	public static final String UTF8 = "UTF-8";
	public static final String FOLDER_PARAMS_SEPARATOR_IN_TEMPLATE_DESC = "|";
	public static final String PARAMS_SEPARATOR_IN_TEMPLATE_DESC_ENTRY = ">";
	
	public static final String PARAM_PREFIX = "param.";

	public static final String REGEX_PARAM_VALUE_IN_TEMPLATE_DESC_ENTRY = "([^" + PARAMS_SEPARATOR_IN_TEMPLATE_DESC_ENTRY
			+ "]+)" + PARAMS_SEPARATOR_IN_TEMPLATE_DESC_ENTRY + "(.*)";
	
	public static final Pattern PATTERN_PARAM_VALUE_IN_TEMPLATE_DESC_ENTRY = Pattern
			.compile(REGEX_PARAM_VALUE_IN_TEMPLATE_DESC_ENTRY);
	
	public static final String DELETE_COMMAND ="<delete>";

}
