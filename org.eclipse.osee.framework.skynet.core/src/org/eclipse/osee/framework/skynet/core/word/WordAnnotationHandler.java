/*
 * Created on Jan 20, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.word;

/**
 * @author Theron Virgin
 */
public class WordAnnotationHandler {

   public static boolean containsWordAnnotations(String wordml) {
      return (wordml.contains("<w:delText>") || wordml.contains("w:type=\"Word.Insertion\"") || wordml.contains("w:type=\"Word.Formatting\""));
   }

   public static String removeAnnotations(String wordml) {
      String annotation = "<.??aml:annotation.*?>";
      String content = "<.??aml:content.*?>";
      String deletions = "<w:delText>.*?</w:delText>";
      wordml = wordml.replaceAll(annotation, "");
      wordml = wordml.replaceAll(content, "");
      wordml = wordml.replaceAll(deletions, "");
      return wordml;
   }
}
