/*
 * Created on Mar 14, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render.artifactElement;

/**
 * @author Jeff C. Phillips
 */
public class WordExtractorData {
   StringBuilder textBuilder;
   String outputText;
   String guid;

   public WordExtractorData() {
      this.textBuilder = new StringBuilder();
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public void addText(String text) {
      textBuilder.append(text);
   }

   public String getText() {
      return null;
   }

}
