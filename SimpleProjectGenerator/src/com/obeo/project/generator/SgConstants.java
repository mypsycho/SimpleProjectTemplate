/*******************************************************************************
 * Copyright (c) 2023 Siemens EDA. All rights reserved.
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package com.obeo.project.generator;

import java.nio.charset.Charset;

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
	public static final String PATH_SEPARATOR = ";";

	// Tags for path
	public static final String P0 = "${";
	public static final String P1 = "}";

    // Tags for file content
	public static final String F0 = "%inject:";
	public static final String F1 = "%";

	public static final Charset UTF8 = Charset.forName("UTF-8");
	public static final String PARAMS_SEPARATOR = "\\|"; // regex must be separated
	public static final char PARAM_VALUE_TAG = '>';
	
	public static final String PARAM_PREFIX = "param.";

	public static final String DELETE_COMMAND ="<delete>";

}
