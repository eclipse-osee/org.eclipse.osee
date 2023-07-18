/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;

/**
 * @author Jaden W. Puckett
 */
public class MarkdownOutlineExtractor extends AbstractArtifactExtractor {
   private static final String HEADER_REGEX = "^(\\d+(?:\\.\\d+)*|\\d+\\.)\\s+(.*)$";
   private static final String NEWLINE_STRING = "\n";
   private static final String NEWLINE_STRING2 = "\n\n";
   private String currentHeadingRoughArtifactMdContent = "";

   @Override
   public String getName() {
      return "Markdown Outline Extractor";
   }

   @Override
   public String getDescription() {
      return "Extract data from a Markdown file with an outline, making an artifact for each outline numbered section.";
   }

   @Override
   public boolean isDelegateRequired() {
      return false;
   }

   @Override
   public FileFilter getFileFilter() {
      return new FileFilter() {
         @Override
         public boolean accept(File file) {
            return file.isDirectory() || file.isFile() && file.getName().endsWith(".md");
         }
      };
   }

   @Override
   protected void extractFromSource(OperationLogger logger, URI source, RoughArtifactCollector collector)
      throws Exception {
      BufferedReader reader = null;
      try {
         reader = new BufferedReader(new InputStreamReader(source.toURL().openStream()));
         StringBuilder contentBuilder = new StringBuilder();
         String line;

         while ((line = reader.readLine()) != null) {
            contentBuilder.append(line).append("\n");
         }

         String markdown = contentBuilder.toString();

         // Create a Flexmark parser (with extensions)
         MutableDataSet options = new MutableDataSet();
         options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create()));
         Parser parser = Parser.builder(options).build();

         // Parse the Markdown document
         Node document = parser.parse(markdown);

         // Traverse the children of the document (AST) and generate heading rough artifacts with md content
         RoughArtifact currentHeadingRoughArtifact = null;
         for (Node child : document.getChildren()) {

            if (child instanceof Heading) {
               currentHeadingRoughArtifactMdContent = "";
               Heading heading = (Heading) child;
               currentHeadingRoughArtifact = setupHeadingRoughArtifact(heading);
               collector.addRoughArtifact(currentHeadingRoughArtifact);
            } else if (!(child instanceof Heading)) {
               currentHeadingRoughArtifactMdContent += child.getChars().toString() + NEWLINE_STRING;
            }

            if (currentHeadingRoughArtifact != null) {
               currentHeadingRoughArtifact.setAttribute(CoreAttributeTypes.MarkdownContent.getName(),
                  currentHeadingRoughArtifactMdContent);
            }
         }

      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         if (reader != null) {
            reader.close();
         }
      }

   }

   // Create the header rough artifact and add critical attributes to the artifact
   private RoughArtifact setupHeadingRoughArtifact(Heading heading) {
      RoughArtifact roughArtifact = new RoughArtifact();

      String headingText = heading.getText().toString();
      // Split heading text into number and name
      Pattern pattern = Pattern.compile(HEADER_REGEX);
      Matcher matcher = pattern.matcher(headingText);
      if (matcher.matches()) {
         String headerNum = matcher.group(1).trim();
         String headerName = matcher.group(2).trim();

         // Set base attributes for heading rough artifact
         roughArtifact.setSectionNumber(headerNum);
         roughArtifact.setAttribute(CoreAttributeTypes.ParagraphNumber.getName(), headerNum);
         roughArtifact.setName(headerName);

         // Add the header name to the beginning of the current Md content
         for (int i = 0; i < heading.getLevel(); i++) {
            currentHeadingRoughArtifactMdContent += "#";
         }
         currentHeadingRoughArtifactMdContent += " " + headingText + NEWLINE_STRING2;
      }

      return roughArtifact;
   }

}
