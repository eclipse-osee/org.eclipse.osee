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
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import java.io.BufferedReader;
import org.eclipse.osee.framework.core.data.MicrosoftOfficeApplicationEnum;

/**
 * Extracts the value of the "mso-application" processing instruction from an XML document. This helps identify the
 * associated Microsoft Office application (e.g., Word, Excel).
 * <p>
 * The main method, {@link #findMsoApplicationValue(BufferedReader)}, retrieves the "progid" value of the
 * "mso-application" processing instruction if present.
 * </p>
 * <p>
 * Note: This implementation scans the XML content for the specific processing instruction rather than parsing the
 * entire XML structure.
 * </p>
 *
 * @author Jaden W. Puckett
 */
public class MsoApplicationExtractor {

   /**
    * Extracts the "mso-application" progid value from the provided XML content.
    * <p>
    * This method scans the XML content for a processing instruction like {@code <?mso-application progid="...">}. If
    * found, the "progid" value is returned as a {@link MicrosoftOfficeApplicationEnum}. If no such instruction exists,
    * an {@code IllegalArgumentException} is thrown.
    * </p>
    *
    * @param xmlContent a {@link BufferedReader} containing the XML content to process; must not be {@code null}.
    * @return the {@link MicrosoftOfficeApplicationEnum} corresponding to the "progid" value of the "mso-application"
    * processing instruction.
    * @throws IllegalArgumentException if {@code xmlContent} is {@code null} or the application is invalid.
    * @throws Exception if an error occurs while reading the XML content.
    */
   public static MicrosoftOfficeApplicationEnum findMsoApplicationValue(BufferedReader xmlContent) throws Exception {
      if (xmlContent == null) {
         throw new IllegalArgumentException("BufferedReader is null");
      }
      return extractMsoApplicationValue(xmlContent);
   }

   /**
    * Searches for the "mso-application" processing instruction and extracts the "progid" value.
    * <p>
    * This method is called internally by {@link #findMsoApplicationValue(BufferedReader)} and performs the actual
    * scanning and string extraction.
    * </p>
    *
    * @param xmlContent a {@link BufferedReader} containing the XML content to process.
    * @return the {@link MicrosoftOfficeApplicationEnum} corresponding to the "progid" value of the "mso-application"
    * processing instruction.
    * @throws Exception if an error occurs while reading the XML content.
    */
   private static MicrosoftOfficeApplicationEnum extractMsoApplicationValue(BufferedReader xmlContent)
      throws Exception {
      String line;
      try {
         while ((line = xmlContent.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("<?mso-application") && line.endsWith("?>")) {
               String progId = extractProgid(line);
               if (!progId.isEmpty()) {
                  MicrosoftOfficeApplicationEnum applicationEnum = getApplicationEnum(progId);
                  if (applicationEnum != null) {
                     return applicationEnum;
                  } else {
                     return MicrosoftOfficeApplicationEnum.SENTINEL;
                  }
               }
            }
         }
      } catch (Exception e) {
         throw new Exception(
            "Failed to scan XML content and extract 'mso-application' progid in MsoApplicationExtractor.extractMsoApplicationValue",
            e);
      }
      return MicrosoftOfficeApplicationEnum.SENTINEL;
   }

   /**
    * Extracts the "progid" value from the processing instruction.
    *
    * @param line the processing instruction line (e.g., {@code <?mso-application progid="Excel.Sheet"?>}).
    * @return the extracted progid value (e.g., "Excel.Sheet"), or an empty string if parsing fails.
    */
   private static String extractProgid(String line) {
      String prefix = "progid=\"";
      int start = line.indexOf(prefix);
      if (start != -1) {
         start += prefix.length();
         int end = line.indexOf('"', start);
         if (end != -1) {
            return line.substring(start, end);
         }
      }
      return ""; // Return empty string if parsing fails
   }

   /**
    * Retrieves the corresponding MicrosoftOfficeApplicationEnum for the extracted progid value.
    *
    * @param progId the progid value to match.
    * @return the corresponding MicrosoftOfficeApplicationEnum, or {@code SENTINEL} if no match is found.
    */
   private static MicrosoftOfficeApplicationEnum getApplicationEnum(String progId) {
      for (MicrosoftOfficeApplicationEnum app : MicrosoftOfficeApplicationEnum.values()) {
         if (app.getApplicationName().equals(progId)) {
            return app;
         }
      }
      return MicrosoftOfficeApplicationEnum.SENTINEL; // Return SENTINEL if no matching enum is found
   }
}
