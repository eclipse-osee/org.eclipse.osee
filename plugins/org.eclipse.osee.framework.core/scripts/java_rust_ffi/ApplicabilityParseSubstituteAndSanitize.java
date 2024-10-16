/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package applicability;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * Java-Rust FFI for applicability processing. DO NOT CHANGE THE FILENAME. The Rust JNI method name(s) is required to
 * match the name of the generated c header source file method name(s) (created using a combination of this class name
 * and the native method within this class). If you do decide to change the class name and/or native method name within
 * this class, you must update the Rust JNI method name as well to match the generated c header source file (which you
 * can generate using javac -h . (insert filename here).java). The filename is also used by bash shell script to create
 * the jar.
 * 
 * @author Jaden W. Puckett
 */
public class ApplicabilityParseSubstituteAndSanitize {

   static {
      try {
         // Get os.name and os.arch
         String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

         // Map os.name to simplified form
         if (osName.contains("win")) {
            osName = "win";
         } else if (osName.contains("mac")) {
            osName = "mac";
         } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            osName = "linux";
         }

         // Build the path to the native library inside the JAR
         String libName = "java_rust_ffi";
         String libExtension = (osName.equals("win")) ? ".dll" : (osName.equals("mac")) ? ".dylib" : ".so";
         String resourcePath = String.format("/native/%s/%s%s", osName, libName, libExtension);

         // Try to load the library from the predefined location in the JAR
         System.out.println("Attempting to load native library from JAR: " + resourcePath);
         InputStream libStream = ApplicabilityParseSubstituteAndSanitize.class.getResourceAsStream(resourcePath);

         if (libStream != null) {
            // Extract to a temporary file
            File tempLibFile = File.createTempFile(libName, libExtension);
            tempLibFile.deleteOnExit(); // Ensure the file is deleted on JVM exit
            try (FileOutputStream out = new FileOutputStream(tempLibFile)) {
               byte[] buffer = new byte[1024];
               int bytesRead;
               while ((bytesRead = libStream.read(buffer)) != -1) {
                  out.write(buffer, 0, bytesRead);
               }
            }

            // Load the extracted library
            System.out.println("Loading native library from temporary file: " + tempLibFile.getAbsolutePath());
            System.load(tempLibFile.getAbsolutePath());
         } else {
            // If not found, revert to loading from java.library.path environment path variable
            System.out.println("Native library not found in JAR. Falling back to java.library.path.");
            System.loadLibrary(libName);
         }

      } catch (IOException e) {
         throw new UnsatisfiedLinkError("Failed to extract and load native library: " + e.getMessage());
      } catch (UnsatisfiedLinkError e) {
         throw new UnsatisfiedLinkError("Failed to load native library: " + e.getMessage());
      }
   }

   /**
    * Native method that parses, substitutes, and sanitizes an input string according to an input configuration.
    * <p>
    * The {@code configJson} parameter must be a JSON string that conforms to one of three specific structures, which
    * correspond to Rust types. This JSON structure is necessary for the native Rust code to correctly process.
    * </p>
    * <p>
    * <h3>Supported Config Shapes</h3> The JSON passed as {@code configJson} must match one of the following formats:
    * </p>
    * <ol>
    * <li><b>ApplicabilityConfigElementConfig:</b> Defines a configuration with features and optional
    * substitutions.</li>
    * 
    * <pre>
    *   {
    *       "name": "config_name",
    *       "group": "group_name",
    *       "features": {@code [ list of ApplicabilityTag objects ]},
    *       "substitutions": {@code [ optional list of Substitution objects ]}
    *   }
    * </pre>
    * 
    * <li><b>ApplicabilityConfigElementLegacy:</b> Defines a legacy configuration, similar to the
    * ApplicabilityConfigElementConfig but with a normalized name.</li>
    * 
    * <pre>
    *   {
    *       "normalizedName": "normalized_name",
    *       "features": {@code [ list of ApplicabilityTag objects ]},
    *       "substitutions": {@code [ optional list of Substitution objects ]}
    *   }
    * </pre>
    * 
    * <li><b>ApplicabilityConfigElementConfigGroup:</b> Defines a group of configurations.</li>
    * 
    * <pre>
    *   {
    *       "name": "group_name",
    *       "configs": {@code [ "config1", "config2", ... ]},
    *       "features": {@code [ list of ApplicabilityTag objects ]},
    *       "substitutions": {@code [ optional list of Substitution objects ]}
    *   }
    * </pre>
    * </ol>
    * <p>
    * The Rust native code will deserialize the JSON input into one of these structures based on the shape of the
    * provided JSON, and process the applicability logic accordingly.
    * </p>
    * <p>
    * <b>Note for Markdown processing:</b> When parsing and substituting Markdown content, the file name and file
    * extension are used to determine the appropriate syntax markers for applicability tags.
    * </p>
    * 
    * @param input The input string that needs to be applicability processed.
    * @param fileName The name of the file whose input string content is being processed.
    * @param fileExtension The extension of the file whose input string content is being processed.
    * @param configJson A JSON string that matches one of the 3 defined shapes for configuration.
    * @return The processed string after parsing, substitution, and sanitization.
    */
   public native String parseSubstituteAndSanitizeApplicability(String input, String fileName, String fileExtension,
      String configJson);

}
