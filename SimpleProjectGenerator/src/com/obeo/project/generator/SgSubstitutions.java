/*******************************************************************************
 * Copyright (c) 2023 Siemens EDA. All rights reserved.
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package com.obeo.project.generator;

import static com.obeo.project.generator.SgConstants.DEFAULT_BINARIES;
import static com.obeo.project.generator.SgConstants.F0;
import static com.obeo.project.generator.SgConstants.F1;
import static com.obeo.project.generator.SgConstants.INJECT_PREFIX;
import static com.obeo.project.generator.SgConstants.P0;
import static com.obeo.project.generator.SgConstants.P1;
import static com.obeo.project.generator.SgConstants.UTF8;
import static com.obeo.project.generator.SgObjects.unsafe;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Class for Substitution.
 *
 * @author nperansin
 */
public class SgSubstitutions {

    static final List<String> BIN_EXTENSIONS = List.of(System.getProperty("template.binaries", DEFAULT_BINARIES)
        .split(","));
    
    public interface Context {
        public String getProperty(String name);
        
        public Path getRoot();
    }

    public static boolean isInject(String prop) {
        return prop.startsWith(INJECT_PREFIX)
                && !prop.substring(INJECT_PREFIX.length()).isBlank();
    }

    public static Path substituePath(Path path, Context config) {
        return Path.of(substitueValue(path.toString(), config, P0, P1, new ArrayList<>()));
    }

    public static String substitueValue(String source, Context config, String f0, String f1, List<String> stack) {
        String result = source;

        for (int pos = result.indexOf(f0); 
                pos != -1 && result.length() > pos; 
                pos = result.indexOf(f0, pos)) {
            int end = result.indexOf(f1, pos + 1);
            if (end == -1) {
                break;
            }
            String reference = result.substring(pos + f0.length(), end);

            if (!stack.contains(reference)) {
                try {
                    stack.add(reference);
                    String value = config.getProperty(reference);
                    if (value != null) {
                        String text = substitueValue(value, config, P0, P1, stack);
                        int nextPos = pos + text.length(); // must computed before result update.
                        result = result.substring(0, pos) + text + result.substring(end + f1.length());
                        pos = nextPos;
                    } else { // undefined; skip
                        pos = end + f1.length();
                    }

                } finally {
                    stack.remove(reference);
                }
            } else {
                pos = end + f1.length();
            }
        }
        return result;
    }

    public static void substitueAll(Path src, Path target, Context config) {
        if (!Files.exists(src)) {
            return;
        }
        Path realTarget = substituePath(target, config);
        unsafe(() -> {
            if (Files.isDirectory(src)) {
                Files.createDirectories(realTarget);
                Files.list(src).forEach(child -> {
                    Path targetChild = substituePath(
                        realTarget.resolve(child.getFileName()), config);
                    
                    substitueAll(child, targetChild, config);
                });
            } else {
                Files.createDirectories(realTarget.getParent());
                substitueFile(src, realTarget, config);
            }
        });
    }

    public static void substitueFile(Path src, Path target, Context config) {

        System.out.println("Writing : " + config.getRoot().relativize(target));
        SgObjects.unsafe(() -> {
            if (isBinary(target)) {
                Files.copy(src, target, StandardCopyOption.REPLACE_EXISTING);
            } else {
                // Note: Files.write will always add an ending newline.
                try (BufferedWriter out = Files.newBufferedWriter(target, UTF8)) {
                    Stream<String> lines = Files.lines(src, UTF8)
                            .map(it -> substitueLine(it, config));
                    
                    // Always add an newline at end of file,
                    // but that is better.
                    for (String line : (Iterable<String>) lines::iterator) {
                        out.write(line);
                        out.newLine();
                    }
                }
            }
        });
    }
    
    public static String substitueLine(String line, Context config) {
        return substitueValue(line, config, F0, F1, new ArrayList<>());
    }

    private static boolean isBinary(Path target) {
        String ext = SgObjects.extension(target);
        // List.of(...) does not deal with null !!
        return ext != null && BIN_EXTENSIONS.contains(ext);
    }

    
}
