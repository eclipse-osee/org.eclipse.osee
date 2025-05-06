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
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.publishing.markdown.MarkdownHtmlUtil;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;

/**
 * @author Jaden W. Puckett
 */
public class MarkdownOutlineExtractor extends AbstractArtifactExtractor {
   private static final String HEADER_REGEX = "^(\\d+(?:\\.\\d+)*|\\d+\\.)\\s+(.*)$";
   private static final String NEWLINE_STRING = "\n";
   //private static final String NEWLINE_STRING2 = "\n\n";
   //private String currentHeadingRoughArtifactMdContent = "";
   private final ArtifactTypeToken contentArtifactTypeToken;
   private final ArtifactTypeToken headingArtifactTypeToken;

   public MarkdownOutlineExtractor(ArtifactTypeToken headingArtifactTypeToken, ArtifactTypeToken contentArtifactTypeToken) {
      this.headingArtifactTypeToken = headingArtifactTypeToken;
      this.contentArtifactTypeToken = contentArtifactTypeToken;
   }

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
         Parser parser = Parser.builder(MarkdownHtmlUtil.getMarkdownParserOptions()).build();

         // Parse the Markdown document
         Node document = parser.parse(markdown);

         // Traverse the children of the document (AST) and generate heading rough artifacts with md content

         String lastHeadingNumber = "0";
         int childCount = 0;

         for (Node child : document.getChildren()) {

            if (child instanceof Heading) {
               //currentHeadingRoughArtifactMdContent = "";
               Heading heading = (Heading) child;
               final var pair = setupHeadingRoughArtifact(heading);
               final var headingRoughArtifact = pair.getFirst();
               lastHeadingNumber = pair.getSecond();
               childCount = 0;
               collector.addRoughArtifact(headingRoughArtifact);
               continue;
            } else if (!(child instanceof Heading)) {
               final var contentRoughArtifact = setupContentRoughArtifact(child, lastHeadingNumber, ++childCount);
               collector.addRoughArtifact(contentRoughArtifact);
               //currentHeadingRoughArtifactMdContent += child.getChars().toString() + NEWLINE_STRING;
               continue;
            }

            //if (currentHeadingRoughArtifact != null) {
            //   currentHeadingRoughArtifact.setAttribute(CoreAttributeTypes.MarkdownContent.getName(),
            //      currentHeadingRoughArtifactMdContent);
            //}
         }

      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         if (reader != null) {
            reader.close();
         }
      }

   }

   private RoughArtifact setupContentRoughArtifact(Node child, String headingNumber, int childCount) {
      final RoughArtifact roughArtifact = new RoughArtifact(this.contentArtifactTypeToken);
      final String content = child.getChars().toString() + NEWLINE_STRING;
      final int contentLength = content.length();
      final int nameLength = Math.min(contentLength, 32);
      final String name = content.substring(0, nameLength);
      final String sectionNumber = headingNumber + "-" + Integer.toString(childCount);
      roughArtifact.setName(name);
      roughArtifact.setSectionNumber(sectionNumber);
      roughArtifact.setAttribute(CoreAttributeTypes.MarkdownContent.getName(), content);
      return roughArtifact;
   }

   // Create the header rough artifact and add critical attributes to the artifact
   private Pair<RoughArtifact, String> setupHeadingRoughArtifact(Heading heading) {
      RoughArtifact roughArtifact = new RoughArtifact(this.headingArtifactTypeToken);

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

         return Pair.createNonNullImmutable(roughArtifact, headerNum);

         // Add the header name to the beginning of the current Md content
         //for (int i = 0; i < heading.getLevel(); i++) {
         //   currentHeadingRoughArtifactMdContent += "#";
         //}
         //currentHeadingRoughArtifactMdContent += " " + headingText + NEWLINE_STRING2;
      }

      return Pair.createNonNullImmutable(roughArtifact, "0");
   }

}
