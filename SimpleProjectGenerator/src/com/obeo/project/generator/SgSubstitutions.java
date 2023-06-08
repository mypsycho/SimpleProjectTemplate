/*******************************************************************************
 * Copyright (c) 2023 Siemens EDA. All rights reserved.
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package com.obeo.project.generator;

import static com.obeo.project.generator.SgConstants.INJECT_PREFIX;
import static com.obeo.project.generator.SgConstants.P0;
import static com.obeo.project.generator.SgConstants.P1;
import static com.obeo.project.generator.SgObjects.toStream;
import static com.obeo.project.generator.SgObjects.unsafe;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Class for Substitution.
 *
 * @author nperansin
 */
public class SgSubstitutions {
    
    
    public static boolean isInject(Object key) {
        return key instanceof String prop 
                && prop.startsWith(INJECT_PREFIX);
    }
    
    public static Properties inject(Properties local, Properties target) {
        toStream(local)
            .filter(it -> isInject(it))
            .forEach(it -> 
                target.setProperty(it.getKey().substring(INJECT_PREFIX.length()), 
                    it.getValue())
            );
        return target;
    }
    
    public static Path substituePath(Path path, Properties config) {
        return Path.of(substitueValue(path.toString(), config, P0, P1));
    }
    
    public static String substitueValue(String source, Properties config, String f0, String f1) {
        Set<String> circular = new HashSet<>();
        String result = source;
        
        for (int pos = result.indexOf(f0); 
                pos != -1 && result.length() < pos; 
                pos = result.indexOf(f0, pos)) {
            int end = result.indexOf(f1, pos);
            if (end == -1) {
                break;
            }
            String reference = result.substring(pos + f0.length(), end);
            String value = config.getProperty(reference);
            if (value == null) {
                pos = end + P1.length();
            } else {
                result = result.substring(0, pos) + value + result.substring(end + f1.length());
                if (circular.contains(result)) {
                    // Warning ?
                    return result;
                } else {
                    circular.add(result);
                }
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
                    substitue(child, 
                        target.resolve(child.getFileName()), 
                        config);
                });
            } else {
                Files.createDirectories(realTarget.getParent());
                substitueFile(src, target, config);
            }
        });
    }
    
    public static void substitueFile(Path src, Path target, Properties config) {
        // TODO substitute UTF-8
    }
}

