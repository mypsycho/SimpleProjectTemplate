/*******************************************************************************
 * Copyright (c) 2023 Siemens EDA. All rights reserved.
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package com.obeo.project.generator;

import static com.obeo.project.generator.SgConstants.F0;
import static com.obeo.project.generator.SgConstants.F1;
import static com.obeo.project.generator.SgConstants.INJECT_PREFIX;
import static com.obeo.project.generator.SgConstants.P0;
import static com.obeo.project.generator.SgConstants.P1;
import static com.obeo.project.generator.SgConstants.UTF8;
import static com.obeo.project.generator.SgObjects.unsafe;
import static com.obeo.project.generator.SgObjects.verify;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Class for Substitution.
 *
 * @author nperansin
 */
public class SgSubstitutions {

	public static boolean canSubstitute(Path path) {
		return canSubstitute(path == null ? null : path.toString(), P0, P1);
	}

	public static boolean canSubstitute(String source, String f0, String f1) {
		int before = -1;
		return !SgObjects.isNullOrEmptyOrBlank(source) && (before = source.indexOf(f0)) > -1
				&& source.indexOf(f1, before + f0.length()) > -1;
	}

	public static boolean isInject(String prop) {

		return prop.startsWith(INJECT_PREFIX)
		        && !prop.substring(INJECT_PREFIX.length()).isBlank();
	}

	public static Path substituePath(Path path, Properties config) {
		return Path.of(substitueValue(path.toString(), config, P0, P1, new ArrayList<>()));
	}

	public static String substitueValue(String source, Properties config, String f0, String f1, List<String> seenKeys) {
		String result = source;

		for (int pos = result.indexOf(f0); pos != -1 && result.length() > pos; pos = result.indexOf(f0, pos)) {
			int end = result.indexOf(f1, pos + 1);
			if (end == -1) {
				break;
			}
			String reference = result.substring(pos + f0.length(), end);
			String value = config.getProperty(reference);
			if (value != null && canSubstitute(value, f0, f1)) {
				verify(!seenKeys.contains(reference), "Circular dependences with parameter(s): " + seenKeys);
				seenKeys.add(reference);
				value = substitueValue(value, config, f0, f1, seenKeys);
				seenKeys.remove(reference);
			}

			if (value == null) {
				pos = end + f1.length();
			} else {
				result = result.substring(0, pos) + value + result.substring(end + f1.length());
			}
		}
		return result;
	}

	public static void substitue(Path src, Path target, Properties config) {
		if (!Files.exists(src)) {
			return;
		}
		Path realTarget = substituePath(target, config);
		unsafe(() -> {
			if (Files.isDirectory(src)) {
				Files.createDirectories(realTarget);
				Files.list(src).forEach(child -> {
					substitue(child, target.resolve(child.getFileName()), config);
				});
			} else {
				Files.createDirectories(realTarget.getParent());
				substitueFile(src, target, config);
			}
		});
	}

	public static void substitueFile(Path src, Path target, Properties config) {
		System.out.println("__\nReading  File:\t" + src);
		Path finalTarget = target;
		if (canSubstitute(target)) {
			finalTarget = substituePath(target, config);
			SgObjects.verify(finalTarget != null, "Cannot substitute Path:" + target);
		}

		if (finalTarget.toFile().exists()) {
			finalTarget.toFile().delete();
		}

		String finalContent = substitueFileContent(src, config);

		try {
			System.out.println("Writing File:\t" + finalTarget.toString());
			Files.writeString(finalTarget, finalContent, StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			e.printStackTrace();
			SgObjects.verify(false, "Cannot write file:\t" + finalTarget.toString());
		}
	}

	public static String substitueFileContent(Path src, Properties config) {
		try {
			String content = Files.readString(src, UTF8);
			return substitueValue(content, config, F0, F1, new ArrayList<>());
		} catch (IOException e) {
			e.printStackTrace();
			SgObjects.verify(false, "Cannot read file:" + src.toString());
		}
		return null;
	}

	public static final void deleteFileOrDirectory(String path) {
		System.out.println("__\nDeleting  File or Directory:\t" + path);
		
	}
}
