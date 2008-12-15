package org.eclipse.osee.framework.ui.skynet.render.word;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;

/**
 * P
 * 
 * @author Theron Virgin
 */
public class ButtonWordMlCreator {
   public static final String[] ORDERER_ATTRIBUTE_TYPES =
         {"Format (Location)", "Rename", "Local Data Definition", "Index", "Display If", "Barrier If", "Top Mode",
               "Bottom Mode", "Color Mode", "Upon Crew Selection", "KU Label", "KU Validation", "OIP If", "SF If",
               "Current Page Mode", "Total Page Mode", "Controls and Display Variance", "Note"};

   private static String paragraphEnd = "</w:t></w:r></w:p>";
   private static String start = "<w:p><w:pPr><w:pStyle w:val=\"dlbody\"/></w:pPr><w:r><w:t>";
   private static String logicMessageStart =
         "</w:t></w:r><w:pPr><w:pStyle w:val=\"dlbody\"/><w:rPr><w:b/></w:rPr></w:pPr><w:r><w:rPr><w:b/><w:caps/><w:color w:val=\"81391B\"/></w:rPr><w:t>";
   private static String logicMessageEnd =
         "</w:t></w:r><w:r><w:rPr><w:b/><w:caps/><w:color w:val=\"81391B\"/><w:vertAlign w:val=\"subscript\"/></w:rPr><w:t>LM</w:t></w:r><w:r><w:rPr><w:b/><w:vertAlign w:val=\"subscript\"/></w:rPr><w:t></w:t></w:r><w:r><w:t>";
   private static String regMessageStart =
         "</w:t></w:r><w:pPr><w:pStyle w:val=\"dlbody\"/><w:rPr><w:b/></w:rPr></w:pPr><w:r><w:rPr><w:b/><w:caps/></w:rPr><w:t>";
   private static String regMessageEnd = "</w:t></w:r><w:r><w:t>";
   private static String headerStart = "<w:p><w:pPr><w:pStyle w:val=\"dlheader\"/></w:pPr><w:r><w:t>";
   private static String headerEnd = ":</w:t></w:r></w:p>";
   private static String curlyStart = "</w:t></w:r><w:r><w:rPr><w:b/></w:rPr><w:t>{";
   private static String curlyEnd = "}</w:t></w:r><w:r><w:t>";
   private static String commentColor = "</w:t></w:r><w:r><w:rPr><w:color w:val=\"FF0000\"/></w:rPr><w:t>//";
   private static String commentPr = "<w:pPr><w:pStyle w:val=\"comment1\"/></w:pPr>";
   private static final String START_TEXT_CHAR = new String(new byte[] {0x002});
   private static final String END_TEXT_CHAR = new String(new byte[] {0x003});

   private static final String lmRegEx = "(\\[[^\\s]+?\\])LM";
   private static final String ldRegEx = "(\\[[^\\s]+?\\])(?!LM)";
   private static final String eBRegEx = "(\\002[^\\002]*?)\\003([^\\002]*?\\003)";
   private static final String sBRegEx = "(\\002[^\\003]*?)\\002([^\\002]*?\\003)";

   public static String getWordMlFromAttribute(Attribute<?> attribute) throws OseeCoreException {
      for (String string : ORDERER_ATTRIBUTE_TYPES)
         if (attribute.getAttributeType().getName().equals(string)) {
            return (headerStart + string + headerEnd + getIfWordMl(attribute));
         }
      String wordml =
            "<w:p><w:r><w:t> " + attribute.getAttributeType().getName() + ": " + (attribute.getValue() != null ? attribute.getValue().toString() : " ") + " </w:t></w:r></w:p>";
      return wordml;
   }

   private static String getIfWordMl(Attribute<?> attribute) throws OseeCoreException {
      if (attribute.getValue() == null) {
         return "";
      }
      String wordml = attribute.getValue().toString();
      wordml = Xml.escape(wordml).toString();
      wordml = wordml.replaceAll("\\{", START_TEXT_CHAR + "{");
      wordml = wordml.replaceAll("\\[", START_TEXT_CHAR + "[");
      wordml = wordml.replaceAll("\\}", "}" + END_TEXT_CHAR);
      wordml = wordml.replaceAll("\\]", "]" + END_TEXT_CHAR);
      wordml = wordml.replaceAll(eBRegEx, "$1$2");
      wordml = wordml.replaceAll(sBRegEx, "$1$2");
      wordml = wordml.replaceAll("\\002\\{", curlyStart);
      wordml = wordml.replaceAll("\\}\\003", curlyEnd);
      wordml = wordml.replaceAll("\\002", "");
      wordml = wordml.replaceAll("\\003", "");
      //Order matters for the next two replaceAll's
      wordml = wordml.replaceAll(ldRegEx, regMessageStart + "$1" + regMessageEnd);
      wordml = wordml.replaceAll(lmRegEx, logicMessageStart + "$1" + logicMessageEnd);
      wordml = wordml.replace("\n", "");
      wordml = wordml.replace("\r", paragraphEnd + start);
      wordml = start + wordml;
      wordml = wordml + paragraphEnd;
      wordml = processComments(wordml);

      return wordml;
   }

   private static String processComments(String wordml) {
      wordml = wordml.replaceAll("//", commentColor);
      int index = 0;
      while (wordml.indexOf("//", index) != -1) {
         String subString =
               wordml.substring(wordml.indexOf("//", index), wordml.indexOf("</w:p>", wordml.indexOf("//", index)));
         String newSubString = subString.replaceAll("\\<.*?\\>", "");
         wordml = wordml.replace(subString, newSubString + "</w:t></w:r>");
         index = wordml.indexOf("//", index) + 2;
      }
      return wordml;
   }
}
