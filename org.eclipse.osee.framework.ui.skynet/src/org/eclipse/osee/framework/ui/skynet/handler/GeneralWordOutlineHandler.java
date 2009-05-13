/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.handler;

import java.util.HashMap;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.Import.RoughArtifact;
import org.eclipse.osee.framework.ui.skynet.Import.RoughArtifactKind;
import org.eclipse.osee.framework.ui.skynet.Import.WordOutlineContentHandler;
import org.eclipse.osee.framework.ui.skynet.Import.WordOutlineExtractor;

/**
 * @author Robert A. Fisher
 */
public class GeneralWordOutlineHandler extends WordOutlineContentHandler {
   private static final Pattern listPrKiller =
         Pattern.compile("<((\\w+:)?listPr)(\\s+.*?)((/>)|(>(.*?)</\\1>))",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private HashMap<String, RoughArtifact> duplicateCatcher;

   private RoughArtifact previousNamedArtifact;
   private RoughArtifact roughArtifact;
   private StringBuilder wordFormattedContent;
   private String lastHeaderNumber;

   /**
    * Subclasses may extend this method to allocate resources
    */
   @Override
   public void init(WordOutlineExtractor extractor) {
      super.init(extractor);

      duplicateCatcher = new HashMap<String, RoughArtifact>();
      lastHeaderNumber = null;
      previousNamedArtifact = null;
      roughArtifact = null;
      wordFormattedContent = new StringBuilder();
   }

   /**
    * Sublcasses may extend this method to dispose resources.
    */
   @Override
   public void dispose() {
      super.dispose();

      duplicateCatcher = null;
      lastHeaderNumber = null;
      previousNamedArtifact = null;
      roughArtifact = null;
   }

   public final void processContent(boolean forceBody, boolean forcePrimaryType, String headerNumber, String listIdentifier, String paragraphStyle, String content, boolean isParagraph, Branch branch) {
      if (!headerNumber.equals("")) {
         lastHeaderNumber = headerNumber;
      }

      if (!headerNumber.equals("") && WordUtil.isHeadingStyle(paragraphStyle) && !WordUtil.textOnly(content).equals("")) {
         setContent();
         roughArtifact = setUpNewArtifact(headerNumber, branch);
         previousNamedArtifact = roughArtifact;

         processHeadingText(roughArtifact, WordUtil.textOnly(content));
      } else if (!listIdentifier.equals("") && !forceBody) {
         String proNumber = lastHeaderNumber + "." + listIdentifier;

         content = listPrKiller.matcher(content).replaceAll("");
         roughArtifact.addAttribute("Name", proNumber);
      } else if (roughArtifact != null) {
         wordFormattedContent.append(content);
      }
   }

   public void setContent() {
      if (roughArtifact != null) {
         roughArtifact.addAttribute(WordAttribute.WORD_TEMPLATE_CONTENT, wordFormattedContent.toString());
         wordFormattedContent.setLength(0);
      }
   }

   /**
    * Subclasses can override this method to handle how heading text is applied to the roughArtifact
    * 
    * @param artifact
    * @param headingText
    */
   public void processHeadingText(RoughArtifact roughArtifact, String headingText) {
      roughArtifact.addAttribute("Name", headingText.trim());
   }

   private RoughArtifact setUpNewArtifact(String parNumber, Branch branch) {
      RoughArtifact duplicateArtifact = duplicateCatcher.get(parNumber);
      if (duplicateArtifact == null) {
         RoughArtifact roughArtifact = new RoughArtifact(RoughArtifactKind.PRIMARY, branch);
         duplicateCatcher.put(parNumber, roughArtifact);

         extractor.addRoughArtifact(roughArtifact);
         roughArtifact.setSectionNumber(parNumber);

         roughArtifact.addAttribute("Imported Paragraph Number", parNumber);

         return roughArtifact;
      } else {
         throw new IllegalStateException(String.format(
               "Paragraph %s found more than once following \"%s\" which is a duplicate of %s", parNumber,
               previousNamedArtifact.getName(), duplicateArtifact.getName()));
      }
   }
}
