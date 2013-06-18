/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.importing.parsers;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;
import org.cyberneko.html.parsers.SAXParser;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.utility.NormalizeHtml;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Marc A. Potter
 */
public class DoorsArtifactExtractor extends AbstractArtifactExtractor {

   private final Vector<String> postProcessGuids = new Vector<String>();
   private final Map<Integer, RowTypeEnum> rowIndexToRowTypeMap = new HashMap<Integer, RowTypeEnum>();
   private String[] headerRow;
   private RoughArtifactCollector collector;
   private boolean inArtifact = false;
   private final Vector<String> theArtifact = new Vector<String>();
   private String paragraphNumber = "", paragraphName = "";
   private String uriDirectoryName = "";
   private final static String NAME_TAG = "<a name=";
   private final static String CLOSING_A_TAG = "</a>";
   private final static String BR_TAG = "<br />";
   private final static String BODY_START_TAG = "<body>";
   private final static String BODY_END_TAG = "</body>";
   private final static String IMAGE_BASE_NAME = "Image Content_";
   private final static String[] VERIFICATION_KEYWORDS = {
      "Effectivity:",
      "Verf Method:",
      "Verf Level:",
      "Verf Location:",
      "Verf Type:",
      "Verified By:",
      "Criteria:"};
   private final static IAttributeType[] FIELD_TYPE = {
      null,
      CoreAttributeTypes.QualificationMethod,
      CoreAttributeTypes.VerificationLevel,
      CoreAttributeTypes.VerificationEvent,
      null,
      null,
      null}; // Last one is actually a string
   private String guidString = "";
   private boolean isRequirement;
   private String subsystem;

   @Override
   public String getDescription() {
      return "Extract artifacts from a DOORS HTML table export file ";
   }

   @Override
   public FileFilter getFileFilter() {
      return new FileFilter() {
         @Override
         public boolean accept(File file) {
            return file.isDirectory() || file.isFile() && file.getName().endsWith(".htm");
         }
      };
   }

   @Override
   public String getName() {
      return "DOORS HTML Table export file";
   }

   @Override
   public boolean usesTypeList() {
      return false;
   }

   public class Handler extends AbstractSaxHandler {

      private boolean isTitle = false;
      private final Vector<String> currentRow = new Vector<String>();
      private final StringBuilder cell = new StringBuilder("");
      private boolean tableFound = false;
      private int embededTableCount = 0;
      private boolean inHeaderCell = false;

      public Handler() {
      }

      private String elementToString(String qName, Attributes attributes, boolean isEndElement) {
         StringBuilder returnValue = new StringBuilder("<");
         if (isEndElement) {
            returnValue.append('/');
         }
         returnValue.append(qName);
         if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
               returnValue.append(" ");
               returnValue.append(attributes.getQName(i));
               String value = attributes.getValue(i);
               if (Strings.isValid(value)) {
                  returnValue.append("=\"");
                  returnValue.append(value);
                  returnValue.append('"');
               }
            }
         }
         returnValue.append(">");
         return returnValue.toString();
      }

      @Override
      public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
         isTitle = false;
         if (qName.equalsIgnoreCase("title")) {
            cell.delete(0, cell.length());
            isTitle = true;
         } else if (qName.equalsIgnoreCase("table")) {
            if (tableFound) {
               // table within the table
               cell.append(elementToString(qName, attributes, false));
               embededTableCount++;
            } else {
               tableFound = true;
            }
         } else if (qName.equalsIgnoreCase("tr")) {
            // Do nothing here -- no processing needed
         } else if (qName.equalsIgnoreCase("th")) {
            if (embededTableCount > 0) {
               // table within the table
               cell.append(elementToString(qName, attributes, false));
            } else {
               inHeaderCell = true;
               cell.delete(0, cell.length());
            }
         } else if (qName.equalsIgnoreCase("td")) {
            if (embededTableCount > 0) {
               // table within the table
               cell.append(elementToString(qName, attributes, false));
            } else {
               cell.delete(0, cell.length());
            }
         } else {
            cell.append(elementToString(qName, attributes, false));
         }
      }

      @Override
      public void endElementFound(String uri, String localName, String qName) throws SAXException {
         isTitle = false;
         if (qName.equalsIgnoreCase("title")) {
            foundStartOfWorksheet(cell.toString());
            cell.delete(0, cell.length());
         } else if (qName.equalsIgnoreCase("table")) {
            if (embededTableCount > 0) {
               // end of table within the table
               cell.append(elementToString(qName, null, true));
               embededTableCount--;
            } else {
               // we are done!
               tableFound = false;
            }
         } else if (qName.equalsIgnoreCase("tr")) {
            if (embededTableCount == 0) {
               String[] row = new String[currentRow.size()];
               row = currentRow.toArray(row);
               currentRow.clear();
               if (inHeaderCell) {
                  processHeaderRow(row);
                  inHeaderCell = false;
               } else {
                  try {
                     processRow(row);
                  } catch (OseeCoreException ex) {
                     throw new SAXException(ex);
                  }
               }
            }
         } else if (qName.equalsIgnoreCase("th")) {
            if (embededTableCount > 0) {
               // table within the table
               cell.append(elementToString(qName, null, true));
            } else {
               currentRow.add(cell.toString());
               cell.delete(0, cell.length());
            }
         } else if (qName.equalsIgnoreCase("td")) {
            if (embededTableCount > 0) {
               // table within the table
               cell.append(elementToString(qName, null, true));
            } else {
               currentRow.add(cell.toString());
               cell.delete(0, cell.length());
            }
         } else {
            cell.append(elementToString(qName, null, true));
         }
      }

      @Override
      public void characters(char ch[], int start, int length) {
         for (int i = 0; i < length; i++) {
            cell.append(Character.toString(ch[i + start]));
         }
         String title = "";
         if (isTitle) {
            title = cell.toString();
            title = title.replaceAll("/", "_");
            title = title.replaceAll(" ", "_");
            if (title.equals("")) {
               title = "empty_title";
            }
            try {
               RoughArtifact roughArtifact = new RoughArtifact(RoughArtifactKind.CONTAINER);
               roughArtifact.addAttribute(CoreAttributeTypes.Name, title.trim());
               roughArtifact.setGuid(GUID.create());
               roughArtifact.setSectionNumber("0");
               collector.addRoughArtifact(roughArtifact);
               isTitle = false;
            } catch (OseeCoreException ex) {
               // do nothing
            }
         }
      }

      @Override
      public void endDocument() throws SAXException {
         try {
            processArtifact();
         } catch (OseeCoreException ex) {
            throw new SAXException(ex);
         }
      }
   }

   @Override
   public void extractFromSource(OperationLogger logger, URI source, RoughArtifactCollector collector) throws Exception {

      /**************************************************************
       * DOORS uses non standard HTML. Read in the file and standardize it
       */

      postProcessGuids.clear();
      inArtifact = false;
      theArtifact.clear();
      paragraphNumber = "";
      paragraphName = "";
      isRequirement = false;
      subsystem = "";

      this.collector = collector;
      String fileName = source.getAuthority();
      if (fileName == null) {
         fileName = "";
      }
      fileName += source.getPath();
      fileName = "file://" + fileName;
      uriDirectoryName = fileName.substring(0, fileName.lastIndexOf('/') + 1);

      SAXParser parser = new SAXParser();
      Handler theHandler = new Handler();
      parser.setContentHandler(theHandler);
      parser.parse(fileName);

   }

   @Override
   public void artifactCreated(Artifact theArtifact) {
      String artifactGuid = theArtifact.getGuid();
      if (postProcessGuids.contains(artifactGuid)) {
         // need to modify the HTML so the image references the data stored in the
         // artifact.
         try {
            List<Integer> Ids = theArtifact.getAttributeIds(CoreAttributeTypes.ImageContent);
            List<String> HTML = theArtifact.getAttributeValues(CoreAttributeTypes.HTMLContent);
            theArtifact.deleteAttributes(CoreAttributeTypes.HTMLContent);
            for (String htmlVal : HTML) {
               int iCount = 0;
               for (Integer imageNumber : Ids) {
                  htmlVal = htmlVal.replaceAll(IMAGE_BASE_NAME + Integer.toString(iCount), imageNumber.toString());
                  iCount++;
               }
               theArtifact.addAttribute(CoreAttributeTypes.HTMLContent, htmlVal);
            }
         } catch (OseeCoreException e) {
            // do nothing
         }
      }
   }

   private static enum RowTypeEnum {
      ID("ID"),
      REQUIREMENTS("Requirements"),
      OBJECT_NUMBER("Object Number"),
      IS_REQ("Req?"),
      PARENT_ID("Parent ID"),
      PARAGRAPH_HEADING("Paragraph Heading"),
      DOCUMENT_APPLICABILITY("Document Applicability"),
      VERIFICATION_CRITERIA("Verification Criteria (V-PIDS_Verification)"),
      CHANGE_STATUS("Change Status"),
      OBJECT_HEADING("Proposed Object Heading"),
      OBJECT_TEXT("Proposed Object Text"),
      CHANGE_RATIONALE("Change Rationale"),
      LINKS("Links"),
      GUID("OSEE GUID"),
      SUBSYSTEM("Subsystem"),
      OTHER("");

      private final static Map<String, RowTypeEnum> rawStringToRowType = new HashMap<String, RowTypeEnum>();

      public String _rowType;

      RowTypeEnum(String rowType) {
         _rowType = rowType;
      }

      public static synchronized RowTypeEnum fromString(String value) {
         if (rawStringToRowType.isEmpty()) {
            for (RowTypeEnum enumStatus : RowTypeEnum.values()) {
               RowTypeEnum.rawStringToRowType.put(enumStatus._rowType, enumStatus);
            }
         }
         RowTypeEnum returnVal = rawStringToRowType.get(value);
         if (returnVal == null) {
            if (value.indexOf("Requirements") != -1) {
               returnVal = REQUIREMENTS;
            }
         }
         return returnVal != null ? returnVal : OTHER;
      }

   }

   public void foundStartOfWorksheet(String sheetName) {
      // Nothing to do in DOORS file  Leave in in case this changes (it is called at start)
   }

   public void processHeaderRow(String[] headerRow) {
      this.headerRow = headerRow.clone();
      for (int i = 0; i < this.headerRow.length; i++) {
         String value = headerRow[i];
         if (value != null) {
            value = value.trim();
         }
         if (!Strings.isValid(value)) {
            this.headerRow[i] = null;
         } else {
            RowTypeEnum rowTypeEnum = RowTypeEnum.fromString(value);
            rowIndexToRowTypeMap.put(i, rowTypeEnum);
         }
      }
   }

   public void processRow(String[] row) throws OseeCoreException {
      /***************************************************************
       * First check the document applicability box, if it is empty this is a header row
       */
      boolean isHeaderRow = false;
      int rowIndex;
      for (rowIndex = 0; rowIndex < row.length; rowIndex++) {
         RowTypeEnum rowType = rowIndexToRowTypeMap.get(rowIndex);
         if (rowType == RowTypeEnum.DOCUMENT_APPLICABILITY) {
            String rowValue = row[rowIndex].toLowerCase();
            if (rowValue.trim().equals("") || rowValue.trim().equals("<br></br>") || rowValue.trim().equals("<br>")) {
               if (inArtifact) {
                  processArtifact();
               }
               inArtifact = false;
               isHeaderRow = true;
            }
            break;
         }
      }
      if (!rowIndexToRowTypeMap.isEmpty()) {
         for (rowIndex = 0; rowIndex < row.length; rowIndex++) {
            RowTypeEnum rowType = rowIndexToRowTypeMap.get(rowIndex);

            String rowValue = row[rowIndex];

            switch (rowType) {

               case REQUIREMENTS:

                  if (isHeaderRow) {
                     // parse the row
                     String noHTML = clearHTML(rowValue);
                     String[] parsed = noHTML.split("[\n ]");
                     boolean foundNumber = false;
                     paragraphName = "";
                     for (int i = 0; i < parsed.length; i++) {
                        if (!parsed[i].equals("")) {
                           if (!foundNumber) {
                              paragraphNumber = parsed[i].trim();
                              foundNumber = true;
                           } else {
                              paragraphName += " " + parsed[i].trim();
                           }
                        }
                     }
                     rowValue = "";
                  }

                  break;

               case ID:
                  break;

               case DOCUMENT_APPLICABILITY:
                  break;

               case VERIFICATION_CRITERIA:
                  break;

               case PARENT_ID:
                  /**************
                   * TODO: Requirements trace GUID<-->GUID pair. Need an example
                   */
                  break;

               case GUID:
                  guidString = GUID.checkOrCreate(rowValue.trim());
                  break;

               case IS_REQ:
                  isRequirement = rowValue.trim().equals("True");
                  break;

               case SUBSYSTEM:
                  subsystem = rowValue.trim();
                  break;

               case CHANGE_STATUS:
               case OBJECT_HEADING:
               case OBJECT_TEXT:
               case CHANGE_RATIONALE:
               case LINKS:
               case OBJECT_NUMBER:
               case PARAGRAPH_HEADING:
               case OTHER:
                  break;

            }

            if (inArtifact) {
               ListIterator<String> iter = theArtifact.listIterator(rowIndex);
               String theColumnValue = iter.next();
               theColumnValue += "\n" + rowValue.trim();
               iter.set(theColumnValue);
            } else {
               theArtifact.add(rowValue.trim());
            }

         }

      }
      inArtifact = true;
   }

   private void processArtifact() throws OseeCoreException {
      RoughArtifact roughArtifact = new RoughArtifact(RoughArtifactKind.PRIMARY);
      roughArtifact.setSectionNumber(paragraphNumber.trim());
      roughArtifact.addAttribute(CoreAttributeTypes.ParagraphNumber, paragraphNumber);
      roughArtifact.addAttribute(CoreAttributeTypes.Name, paragraphName.trim());
      if (!isRequirement) {
         roughArtifact.setPrimaryArtifactType(CoreArtifactTypes.HeadingHTML);
         roughArtifact.setRoughArtifactKind(RoughArtifactKind.SECONDARY);
      }
      if (!Strings.isValid(guidString)) {
         guidString = GUID.create();
      }
      roughArtifact.setGuid(guidString);
      guidString = "";
      for (int rowIndex = 0; rowIndex < theArtifact.size(); rowIndex++) {
         RowTypeEnum rowType = rowIndexToRowTypeMap.get(rowIndex);

         String rowValue = theArtifact.get(rowIndex);

         switch (rowType) {

            case REQUIREMENTS:
               StringBuffer imageFileList = new StringBuffer("");
               getImageList(rowValue, imageFileList);
               rowValue = normailizeHtml(rowValue);
               String imageFile = imageFileList.toString();
               if (!imageFile.isEmpty()) {
                  String theImage;
                  int comma = 0;
                  int imageNumber = 0;
                  postProcessGuids.add(guidString);
                  do {
                     comma = imageFile.indexOf(',');
                     if (comma == -1) {
                        theImage = uriDirectoryName + imageFile;
                        imageFile = " ";
                     } else {
                        theImage = uriDirectoryName + imageFile.substring(0, comma);
                        imageFile = imageFile.substring(comma + 1);
                     }
                     try {
                        URI imageURI = new URI(theImage);
                        roughArtifact.addAttribute("Image Content", imageURI);
                        rowValue = rowValue.replace(theImage, IMAGE_BASE_NAME + Integer.toString(imageNumber));
                        imageNumber++;
                     } catch (URISyntaxException e) {
                        e.printStackTrace();
                     }
                  } while (comma != -1);
               }
               if (Strings.isValid(rowValue)) {
                  roughArtifact.addAttribute(CoreAttributeTypes.HTMLContent, rowValue);
               }
               break;

            case ID:
               rowValue = rowValue.replaceAll("\n", ",");
               roughArtifact.addAttribute(CoreAttributeTypes.LegacyId, rowValue);
               break;

            case SUBSYSTEM:
               if (Strings.isValid(subsystem)) {
                  roughArtifact.addAttribute(CoreAttributeTypes.Subsystem, subsystem);
                  subsystem = "";
               }
               break;

            case DOCUMENT_APPLICABILITY:
               break;

            case VERIFICATION_CRITERIA:
               processVerification(rowValue, roughArtifact);
               break;

            case PARENT_ID:
               /**************
                * TODO: Requirements trace GUID<-->GUID pair. Need an example
                */
               break;

            case CHANGE_STATUS:
            case OBJECT_HEADING:
            case OBJECT_TEXT:
            case CHANGE_RATIONALE:
            case OTHER:
            case LINKS:
            default:
               break;
         }
      }
      collector.addRoughArtifact(roughArtifact);
      inArtifact = false;
      theArtifact.clear();
   }

   /**********************************************************************
    * @param column value from the Verification type cell
    * @param roughArtifact the artifact being populated
    * @throws OseeCoreException
    */
   public void processVerification(String column, RoughArtifact roughArtifact) throws OseeCoreException {
      /**************************************************************
       * The followings possibilities exist for this field 1) Field empty 2) Some/all keywords The keywords may not be
       * filled in. In other words a keyword may be followed by a keyword instead of data.
       */

      String trimmed = clearHTML(column);
      if (trimmed.trim().isEmpty()) {
         // empty
         return;
      }
      /*****************************************************************
       * There are some keywords that do not map-- need them in the list to check if there is data
       */
      for (int i = 0; i < VERIFICATION_KEYWORDS.length; i++) {
         // special case Criteria is a string attribute
         if ((FIELD_TYPE[i] == null) && (!VERIFICATION_KEYWORDS[i].equals("Criteria:"))) {
            continue;
         }
         int iStart = trimmed.indexOf(VERIFICATION_KEYWORDS[i]);
         if (iStart != -1) {
            boolean dataFound = true;
            // any data?
            String rest = trimmed.substring(iStart + VERIFICATION_KEYWORDS[i].length());
            rest = rest.trim();
            // is it empty?
            dataFound = !rest.isEmpty();
            for (int j = 0; (j < VERIFICATION_KEYWORDS.length) && dataFound; j++) {
               dataFound = !rest.startsWith(VERIFICATION_KEYWORDS[j]);
            }
            if (dataFound) {
               // find the data 
               int colon = rest.indexOf(':');
               if (colon == -1) {
                  if (VERIFICATION_KEYWORDS[i].equals("Criteria:")) {
                     // special case Criteria is a string attribute
                     roughArtifact.addAttribute("Verification Acceptance Criteria", rest);
                  } else {
                     roughArtifact.addAttribute(FIELD_TYPE[i], rest);
                  }
               } else {
                  // find the start of the keyword
                  boolean foundKeyword = false;
                  for (int j = 0; (j < VERIFICATION_KEYWORDS.length); j++) {
                     if (rest.indexOf(VERIFICATION_KEYWORDS[j]) == (colon - VERIFICATION_KEYWORDS[j].length() + 1)) {
                        roughArtifact.addAttribute(FIELD_TYPE[i],
                           rest.substring(0, rest.indexOf(VERIFICATION_KEYWORDS[j]) - 1));
                        foundKeyword = true;
                        break;
                     }
                  }
                  if (!foundKeyword) {
                     roughArtifact.addAttribute(FIELD_TYPE[i], rest);
                  }

               }
            }
         }
      }
   }

   public void reachedEndOfWorksheet() {
      // do nothing
   }

   /*************************************************************
    * @param inputHTML Input value of the requirements field (as exported from DOORS)
    * @param imageFileList is a comma separated list of image file names in the HTML If there is an <img tag, add the
    * file name of the image file in imageFile
    */
   private void getImageList(String inputHTML, StringBuffer imageFileList) {
      String outputHtml = inputHTML;
      String Lower = outputHtml.toLowerCase();
      int img = Lower.indexOf("img ");
      imageFileList.setLength(0);
      boolean first = true;
      while (img != -1) {
         int src = Lower.indexOf("src=", img);
         if (src != -1) {
            src += 4;
            char qte = Lower.charAt(src);
            int iEnd = Lower.indexOf(qte, src + 1);
            if (first) {
               imageFileList.append(inputHTML.substring(src + 1, iEnd));
               first = false;
            } else {
               imageFileList.append("," + outputHtml.substring(src + 1, iEnd));
            }
            img = Lower.indexOf("img ", src);
         } else {
            img = -1;
         }
      }
   }

   /************************************************************
    * @param input input string with HTML tags
    * @return a string with the HTML tags removed
    */
   private String clearHTML(String input) {
      String returnValue = "", processString = input;
      int openBracket = processString.indexOf('<'), closeBracket;
      if (openBracket == -1) {
         returnValue = input;
      }
      while (openBracket >= 0) {
         /************************************************************
          * if the bracket doesn't start the string, copy the start to the return (plus a space). Find the close bracket
          * and continue until all tags are removed
          */
         if (openBracket != 0) {
            returnValue += processString.substring(0, openBracket) + " ";
         }
         closeBracket = processString.indexOf('>');
         if (closeBracket > 0) {
            processString = processString.substring(closeBracket + 1);
         }
         openBracket = processString.indexOf('<');
      }
      return returnValue;
   }

   private String normailizeHtml(String inputHtml) {

      String returnValue = NormalizeHtml.convertToNormalizedHTML(inputHtml);
      int bodyStart = returnValue.indexOf(BODY_START_TAG);
      int bodyEnd = returnValue.indexOf(BODY_END_TAG);
      if (bodyStart != -1) {
         bodyStart += BODY_START_TAG.length();
         if (bodyEnd == -1) {
            bodyEnd = returnValue.length() - 1;
         } else {
            bodyEnd--;
         }
         if (bodyEnd <= bodyStart) {
            returnValue = ""; // no body 
         } else {
            returnValue = returnValue.substring(bodyStart, bodyEnd);
         }
      }
      /************************************************************************
       * The DOORS HTML starts with a tag "<a name="something"> -- remove this tag and the closing </a>
       */
      int nameTag = returnValue.indexOf(NAME_TAG);
      while (nameTag != -1) {
         int endTag = returnValue.substring(nameTag).indexOf('>') + nameTag;
         if (endTag > nameTag) {
            endTag = returnValue.indexOf(CLOSING_A_TAG);
            returnValue =
               returnValue.substring(0, nameTag - 1) + returnValue.substring(endTag + CLOSING_A_TAG.length() + 1);
            nameTag = returnValue.indexOf(NAME_TAG);
         } else {
            nameTag = -1;
         }
      }
      /******************************************************************
       * remove any closing <br />
       * tags these are not meaningful
       */
      returnValue = returnValue.trim();
      int brTag = returnValue.lastIndexOf(BR_TAG);
      while (brTag == returnValue.length() - BR_TAG.length()) {
         returnValue = returnValue.substring(0, brTag).trim();
         brTag = returnValue.lastIndexOf(BR_TAG);
      }
      return returnValue;
   }
}
