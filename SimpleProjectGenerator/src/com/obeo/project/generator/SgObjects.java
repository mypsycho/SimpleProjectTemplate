/*******************************************************************************
 * Copyright (c) 2023 IRT SystemX. All rights reserved.
 * 
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package com.obeo.project.generator;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * @author nperansin
 *
 */
public class SgObjects {

    public static String getRequiredProperty(Properties config, String key) {
        String result = config.getProperty(key);
        verify(result != null && !result.isBlank(), "No such property + " + key);
        return result;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes"}) // handle legacy
    public static Stream<Map.Entry<String, String>> toStream(Properties it) {
        return ((Stream) it.entrySet().stream());
    }
    
    
    public static void verify(boolean condition, String message) {
        if (condition) {
            throw new IllegalArgumentException(message);
        }
    }
    
    public static Properties getCfgContent(Path file, boolean optional) {
        if (!Files.exists(file)) {
            verify(optional, "No such file: " + file);
            return new Properties();
        }
        
        Properties configContent = new Properties();
        try(InputStream in = Files.newInputStream(file)) {
            configContent.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        verify(optional || !configContent.isEmpty(), "No content in properties : " + file);
        return configContent;
    }
    
    public static interface UnsafeCall {
        void run() throws Exception;
    }
    public static void unsafe(UnsafeCall task) {
        try {
            task.run();
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        } catch(RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
