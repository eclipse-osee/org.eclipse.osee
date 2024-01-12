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

package org.eclipse.osee.framework.dev.help.ui;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageExtractor {
   public static void main(String[] args) {

      Path currentRelativePath = Paths.get("");
      String s = currentRelativePath.toAbsolutePath().toString();

      String htmlDirectory = "docs";
      String imageDirectory = "../../docs/images";
      String destinationDirectory = "docs/images";

      extractImageNames(htmlDirectory, imageDirectory, destinationDirectory);
   }

   private static void extractImageNames(String htmlDirectory, String imageDirectory, String destinationDirectory) {

      File htmlDir = new File(htmlDirectory);
      File[] htmlFiles = htmlDir.listFiles((dir, name) -> name.endsWith(".html"));

      if (htmlFiles != null) {
         for (File htmlFile : htmlFiles) {
            try {
               String content = Files.readString(htmlFile.toPath());
               Pattern pattern = Pattern.compile("(<img[^>]+src=\")/[^\"]+/([^\"]+\\.(?:gif|png|jpg))(\"[^>]*/>)");
               Matcher matcher = pattern.matcher(content);

               boolean foundMatch = false;

               while (matcher.find()) {
                  foundMatch = true;

                  String imageName = matcher.group(2);

                  Path sourcePath = Paths.get(imageDirectory, imageName);
                  Path destinationPath = Paths.get(destinationDirectory, imageName);

                  Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
               }

               if (foundMatch) {
                  // Replace all matches with the proper image source format
                  String replacedContent = matcher.replaceAll("$1images/$2$3");

                  // Write the replaced content back to the file
                  Files.write(htmlFile.toPath(), replacedContent.getBytes(StandardCharsets.UTF_8));
               }
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
   }
}
