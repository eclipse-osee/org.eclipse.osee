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
import java.io.FileInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;

/**
 * @author Marc A. Potter
 */
public class DoorsArtifactExtractor extends AbstractArtifactExtractor {

   private final Vector<String> postProcessGuids = new Vector<String>();
   private final Map<Integer, RowTypeEnum> rowIndexToRowTypeMap = new HashMap<Integer, RowTypeEnum>();
   private String[] headerRow;
   RoughArtifactCollector collector;
   private static final String imageBaseName = "Image Content_";
   private static int READ_BUFFER_LEN = 4096;

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

   @Override
   public void extractFromSource(OperationLogger logger, URI source, RoughArtifactCollector collector) throws Exception {

      /**************************************************************
       * DOORS uses non standard HTML. Read in the file and standardize it
       */
      this.collector = collector;
      String fileName = source.getAuthority();
      if (fileName == null) {
         fileName = "";
      }
      fileName += source.getPath();

      String standardHTML = standardizeDOORS(fileName);

      XMLInputFactory factory = XMLInputFactory.newInstance();
      Reader myStringReader = new StringReader(standardHTML);
      XMLEventReader reader = factory.createXMLEventReader(myStringReader);
      XMLEvent event = null;

      boolean tableFound = false, inHeaderCell = false;
      int embededTableCount = 0;
      String cell = "";
      Vector<String> currentRow = new Vector<String>();
      while (reader.hasNext()) {
         event = reader.nextEvent();
         if (event.isStartElement()) {
            StartElement startElement = (StartElement) event;
            String qName = startElement.getName().toString().trim();
            if (qName.equalsIgnoreCase("title")) {
               cell = "";
            } else if (qName.equalsIgnoreCase("table")) {
               if (tableFound) {
                  // table within the table
                  cell += event.toString();
                  embededTableCount++;
               } else {
                  tableFound = true;
               }
            } else if (qName.equalsIgnoreCase("tr")) {
               // Do nothing here -- no processing needed
            } else if (qName.equalsIgnoreCase("th")) {
               if (embededTableCount > 0) {
                  // table within the table
                  cell += event.toString();
               } else {
                  inHeaderCell = true;
                  cell = "";
               }
            } else if (qName.equalsIgnoreCase("td")) {
               if (embededTableCount > 0) {
                  // table within the table
                  cell += event.toString();
               } else {
                  cell = "";
               }
            } else {
               cell += event.toString();
            }
         } else if (event.isEndElement()) {
            EndElement endElement = (EndElement) event;
            String qName = endElement.getName().toString().trim();
            if (qName.equalsIgnoreCase("title")) {
               foundStartOfWorksheet(cell);
               cell = "";
            } else if (qName.equalsIgnoreCase("table")) {
               if (embededTableCount > 0) {
                  // end of table within the table
                  cell += event.toString();
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
                     processRow(row);
                  }
               }
            } else if (qName.equalsIgnoreCase("th")) {
               if (embededTableCount > 0) {
                  // table within the table
                  cell += event.toString();
               } else {
                  /***********************************************
                   * In order to parse the DOORS import, ='xXx' had to be added to some keywords. This is because the
                   * parser does not support simple keywords (e.g.
                   * <table nowrap>
                   * ) remove the additional code before adding it to the row
                   */

                  String StripCell = cell.replaceAll("=\'xXx\'", " ");
                  currentRow.add(StripCell);
                  cell = "";
               }
            } else if (qName.equalsIgnoreCase("td")) {
               if (embededTableCount > 0) {
                  // table within the table
                  cell += event.toString();
               } else {
                  /***********************************************
                   * In order to parse the DOORS import, ='xXx' had to be added to some keywords. This is because the
                   * parser does not support simple keywords (e.g.
                   * <table nowrap>
                   * ) remove the additional code before adding it to the row
                   */

                  String StripCell = cell.replaceAll("=\'xXx\'", " ");
                  currentRow.add(StripCell);
                  cell = "";
               }
            } else {
               cell += event.toString();
            }

         } else if (event.isCharacters()) {
            Characters characters = (Characters) event;
            cell += characters.toString();
         }
      }
      myStringReader.close();
      // Do last artifact
      processArtifact();

   }

   private String getToken(String input, int startChar) {
      String returnValue = input.substring(startChar);
      int iPos = 0;
      boolean inSingleQuote = false, inDoubleQuote = false;
      while (iPos < returnValue.length()) {
         char theChar = returnValue.charAt(iPos);
         switch (theChar) {

            case '\'':
               if (inSingleQuote) {
                  // have to include closing ' (iPos + 1);
                  returnValue = returnValue.substring(0, iPos + 1);
                  iPos++;
                  inSingleQuote = false;
               } else {
                  inSingleQuote = true;
               }
               break;

            case '\"':
               if (inDoubleQuote) {
                  // have to include closing " (iPos + 1);
                  returnValue = returnValue.substring(0, iPos + 1);
                  iPos++;
                  inDoubleQuote = false;
               } else {
                  inDoubleQuote = true;
               }
               break;

            case '<':
            case '>':
               inDoubleQuote = inSingleQuote = false;
            case '=':
            case ' ':
               if ((!inSingleQuote) && (!inDoubleQuote)) {
                  // end of token
                  if (iPos == 0) {
                     // starts with a terminator, token is 1 char
                     returnValue = String.valueOf(theChar);
                  } else {
                     returnValue = returnValue.substring(0, iPos);
                     iPos++;
                  }
               }
               break;
         }
         iPos++;
      }
      return returnValue;
   }

   /**********************************************************************
    * @param file name of the DOORS export file
    * @return HTML representation of the DOORS export in standard HTML format DOORS export uses nonstandard HTML, this
    * method standardizes it for use with the parser Known issues The opening <META> tab is not terminated (no </META>). <br>
    * tags are not terminated (no </br>) <img> tags are not terminated (no </img>)
    * <p>
    * not terminated (no
    * </p>
    * ). This is a problem because it would take a lot to determine the point where the </p> needs to go. However, the
    * way
    * <p>
    * is used it looks like it can simply be ignored (converted to
    * </p>
    * ) attributes within tags are not given values (<td border>instead of <td border='small'>) attributes values not
    * quoted (
    * <th width=50>instead of
    * <th width='50'>)
    */
   private String standardizeDOORS(String input) throws OseeArgumentException {
      StringBuilder rawValue = new StringBuilder(""), returnValue = new StringBuilder("");
      int iLastSlash = input.lastIndexOf('/'), iLastBackslash = input.lastIndexOf('\\');
      int iLast = (iLastBackslash > iLastSlash) ? iLastBackslash : iLastSlash;
      String filePath = input.substring(0, iLast + 1);
      try {
         FileInputStream readStream = new FileInputStream(input);
         int iRead = 0;
         byte[] readBytes = new byte[READ_BUFFER_LEN];
         iRead = READ_BUFFER_LEN;
         while (iRead == READ_BUFFER_LEN) {
            iRead = readStream.read(readBytes);
            String readString = new String(readBytes, 0, iRead);
            rawValue.append(readString);
         }
         readStream.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
      // We now have the whole file as a string --
      // walk through it one token at a time
      int iStart = 0;
      boolean inTag = false, inMeta = false, inBr = false, inImg = false, inP = false, tagName = false, equalFound =
         false, attributeFound = false, LiteralTag = false, isSrcTag = false;
      while (iStart < rawValue.length()) {
         String token = getToken(rawValue.toString(), iStart);
         iStart += token.length();
         if (token.length() == 0) {
            // do nothing, we are done
            inTag = false; // breakpoint
         } else if (token.equals("<")) {
            if (inTag) {
               throw (new OseeArgumentException("< within a tag in HTML"));
            }
            inTag = true;
         } else if (token.equals(">")) {
            if (!inTag) {
               throw (new OseeArgumentException("> outside a tag in HTML"));
            }
            inTag = false;
            if (inMeta) {
               token = " />";
               inMeta = false;
            } else if (inBr) {
               token = " />";
               inBr = false;
            } else if (inImg) {
               token += "</img>";
               inImg = false;
            } else if (inP) {
               token = "/>";
               inP = false;
            } else if (attributeFound) {
               token = "='xXx'>";
            }
            tagName = false;
            attributeFound = false;
            LiteralTag = false;
            equalFound = false;
         } else if (token.equalsIgnoreCase("META") && inTag) {
            inMeta = true;
            tagName = true;
         } else if (token.equalsIgnoreCase("br") && inTag) {
            inBr = true;
            tagName = true;
         } else if (token.equalsIgnoreCase("img") && inTag) {
            inImg = true;
            tagName = true;
         } else if (token.equalsIgnoreCase("p") && inTag) {
            inP = true;
            tagName = true;
         } else if (token.equalsIgnoreCase("!DOCTYPE") && inTag) {
            LiteralTag = true;
         } else if (token.equalsIgnoreCase("BODY") && inTag) {
            // This is a parser issue has to be same case?
            token = "body";
         } else if (token.equalsIgnoreCase("/BODY") && inTag) {
            // This is a parser issue has to be same case?
            token = "/body";
         } else if (token.equalsIgnoreCase("!--") && inTag) {
            LiteralTag = true;
         } else if (token.equalsIgnoreCase("&nbsp") && !inTag) {
            // no closing semicolon
            token = "&nbsp;";
         } else if (!LiteralTag) {
            if (inTag) {
               /***************************************************
                * If this is an attribute verify that the value is of the form 'value' or "value"
                */
               if (!tagName) {
                  tagName = true;
               } else if (!attributeFound) {
                  attributeFound = !token.equals(" ");
                  /************************************
                   * for images, DOORS exports the file to the same directory as the HTML file and does not qualify the
                   * src= keyword. This is fine for rendering in a browser, but in order to import the file later, it
                   * must be qualified
                   */
                  if (attributeFound && token.equalsIgnoreCase("src")) {
                     isSrcTag = true;
                  } else {
                     isSrcTag = false;
                  }
               } else if (!equalFound) {
                  if (token.equals("=")) {
                     equalFound = true;
                  } else if (!token.equals(" ")) {
                     // this is just an attribute no =
                     token = "='xXx' " + token;
                  }
               } else if (!token.equals(" ")) {
                  // is the value quoted?
                  if (!((token.charAt(0) == '\'') || (token.charAt(0) == '"'))) {
                     // add quotes
                     token = "'" + token + "'";
                  }
                  if (isSrcTag) {
                     // if not qualified, qualify it
                     if ((token.indexOf('/') == -1) && (token.indexOf('\\') == -1) && token.indexOf("://") == -1) {
                        token = token.substring(0, 1) + "file:///" + filePath + token.substring(1);
                     }
                  }
                  attributeFound = false;
                  equalFound = false;
               }
            }
         }
         returnValue.append(token);
      }
      return returnValue.toString();
   }

   @Override
   public void artifactCreated(Artifact theArtifact) {
      String artifactGuid = theArtifact.getGuid();
      if (postProcessGuids.contains(artifactGuid)) {
         // need to modify the HTML so the image references the data stored in the
         // artifact.
         System.out.println(theArtifact.getGuid());
         try {
            List<Integer> Ids = theArtifact.getAttributeIds(CoreAttributeTypes.ImageContent);
            List<String> HTML = theArtifact.getAttributeValues(CoreAttributeTypes.HTMLContent);
            theArtifact.deleteAttributes(CoreAttributeTypes.HTMLContent);
            for (String htmlVal : HTML) {
               int iCount = 0;
               for (Integer imageNumber : Ids) {
                  htmlVal = htmlVal.replaceAll(imageBaseName + Integer.toString(iCount), imageNumber.toString());
                  iCount++;
               }
               theArtifact.addAttribute(CoreAttributeTypes.HTMLContent, htmlVal);
            }
         } catch (OseeCoreException e) {
            e.printStackTrace();
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

   private boolean inArtifact = false;
   private final Vector<String> theArtifact = new Vector<String>();
   private String paragraphNumber = "", paragraphName = "";

   public void processRow(String[] row) throws OseeCoreException {
      /***************************************************************
       * First check the document applicability box, if it is empty this is a header row
       */
      boolean isHeaderRow = false;
      int rowIndex;
      for (rowIndex = 0; rowIndex < row.length; rowIndex++) {
         RowTypeEnum rowType = rowIndexToRowTypeMap.get(rowIndex);
         if (rowType == RowTypeEnum.DOCUMENT_APPLICABILITY) {
            String rowValue = row[rowIndex];
            if (rowValue.trim().equals("") || rowValue.trim().equals("<br></br>")) {
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

               case CHANGE_STATUS:
               case OBJECT_HEADING:
               case OBJECT_TEXT:
               case CHANGE_RATIONALE:
               case LINKS:
               case OBJECT_NUMBER:
               case IS_REQ:
               case PARAGRAPH_HEADING:
               case OTHER:
                  break;

            }

            if (inArtifact) {
               ListIterator<String> iter = theArtifact.listIterator(rowIndex);
               String theColumnValue = iter.next();
               theColumnValue += "\n" + rowValue;
               iter.set(theColumnValue);
            } else {
               theArtifact.add(rowValue);
            }

         }

      }
      inArtifact = true;
   }

   private void processArtifact() throws OseeCoreException {
      RoughArtifact roughArtifact = new RoughArtifact(RoughArtifactKind.PRIMARY);
      roughArtifact.setSectionNumber(paragraphNumber);
      roughArtifact.addAttribute(CoreAttributeTypes.ParagraphNumber, paragraphNumber);
      roughArtifact.addAttribute(CoreAttributeTypes.Name, paragraphName.trim());
      String guid = GUID.create();
      roughArtifact.setGuid(guid);
      for (int rowIndex = 0; rowIndex < theArtifact.size(); rowIndex++) {
         RowTypeEnum rowType = rowIndexToRowTypeMap.get(rowIndex);

         String rowValue = theArtifact.get(rowIndex);

         switch (rowType) {

            case REQUIREMENTS:
               StringBuffer imageFileList = new StringBuffer("");
               rowValue = translateRequirements(rowValue, imageFileList);
               String imageFile = imageFileList.toString();
               if (!imageFile.isEmpty()) {
                  String theImage;
                  int comma = 0;
                  int imageNumber = 0;
                  postProcessGuids.add(guid);
                  do {
                     comma = imageFile.indexOf(',');
                     if (comma == -1) {
                        theImage = imageFile;
                        imageFile = " ";
                     } else {
                        theImage = imageFile.substring(0, comma);
                        imageFile = imageFile.substring(comma + 1);
                     }
                     try {
                        URI imageURI = new URI(theImage);
                        roughArtifact.addAttribute("Image Content", imageURI);
                        rowValue = rowValue.replace(theImage, imageBaseName + Integer.toString(imageNumber));
                        imageNumber++;
                     } catch (URISyntaxException e) {
                        e.printStackTrace();
                     }
                  } while (comma != -1);
               }
               roughArtifact.addAttribute(CoreAttributeTypes.HTMLContent, rowValue);
               break;

            case ID:
               rowValue = rowValue.replaceAll("\n", ",");
               roughArtifact.addAttribute(CoreAttributeTypes.LegacyId, rowValue);
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
      String[] keywords =
         {"Effectivity:", "Verf Method:", "Verf Level:", "Verf Location:", "Verf Type:", "Verified By:", "Criteria:"};
      IAttributeType[] FieldType =
         {
            null,
            CoreAttributeTypes.QualificationMethod,
            CoreAttributeTypes.VerificationLevel,
            CoreAttributeTypes.VerificationEvent,
            null,
            null,
            null}; // Last one is actually a string
      String trimmed = clearHTML(column);
      if (trimmed.trim().isEmpty()) {
         // empty
         return;
      }
      /*****************************************************************
       * There are some keywords that do not map-- need them in the list to check if there is data
       */
      for (int i = 0; i < keywords.length; i++) {
         // special case Criteria is a string attribute
         if ((FieldType[i] == null) && (!keywords[i].equals("Criteria:"))) {
            continue;
         }
         int iStart = trimmed.indexOf(keywords[i]);
         if (iStart != -1) {
            boolean dataFound = true;
            // any data?
            String rest = trimmed.substring(iStart + keywords[i].length());
            rest = rest.trim();
            // is it empty?
            dataFound = !rest.isEmpty();
            for (int j = 0; (j < keywords.length) && dataFound; j++) {
               dataFound = !rest.startsWith(keywords[j]);
            }
            if (dataFound) {
               // find the data 
               int colon = rest.indexOf(':');
               if (colon == -1) {
                  if (keywords[i].equals("Criteria:")) {
                     // special case Criteria is a string attribute
                     roughArtifact.addAttribute("Verification Acceptance Criteria", rest);
                  } else {
                     roughArtifact.addAttribute(FieldType[i], rest);
                  }
               } else {
                  // find the start of the keyword
                  boolean foundKeyword = false;
                  for (int j = 0; (j < keywords.length); j++) {
                     if (rest.indexOf(keywords[j]) == (colon - keywords[j].length() + 1)) {
                        roughArtifact.addAttribute(FieldType[i], rest.substring(0, rest.indexOf(keywords[j]) - 1));
                        foundKeyword = true;
                        break;
                     }
                  }
                  if (!foundKeyword) {
                     roughArtifact.addAttribute(FieldType[i], rest);
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
    * @param imageFileList is a comma separated list of image file names in the HTML
    * @return HTML translated to common format Following transformations will be performed <code>
    * DOORS format Common format
    * <i></i>        <em></em> 
    * <b></b>        <strong></strong> 
    * &#9            &nbsp;&nbsp;&nbsp;&nbsp;
    * <br></br>      <p>&nbsp;</p>
    * 
    * DOORS also puts <div tags within other tags, For example 
    * <b>xxx<div ... >yyy</div></b>. 
    * Translate this to
    * <b>xxx</b><div ...><b>yyy</b></div>
    * 
    * If there is an <img tag, add the file name of the image file in imageFile
    * </code>
    */
   private String translateRequirements(String inputHTML, StringBuffer imageFileList) {
      String outputHTML = inputHTML;
      String imageFiles = "";
      outputHTML = outputHTML.replaceAll("<i>", "<em>");
      outputHTML = outputHTML.replaceAll("</i>", "</em>");
      outputHTML = outputHTML.replaceAll("<b>", "<strong>");
      outputHTML = outputHTML.replaceAll("</b>", "</strong>");
      outputHTML = outputHTML.replaceAll("&#9", "&nbsp;&nbsp;&nbsp;&nbsp;");
      outputHTML = outputHTML.replaceAll("<br></br>", "<p>&nbsp;</p>");

      String Lower = outputHTML.toLowerCase();
      if (Lower.indexOf("<div") != -1) {
         // is the DIV inside of a <strong> or <i>
         int div = Lower.indexOf("<div"), bold = Lower.indexOf("<strong>"), italic = Lower.indexOf("<em>");
         if ((bold != -1) && (bold < div)) {
            // find the </b>
            int boldEnd = Lower.indexOf("</strong>");
            if (boldEnd > div) {
               int divEND = Lower.indexOf(">", div), divClose = Lower.indexOf("</div>", div);
               Lower =
                  outputHTML.substring(0, div - 1) + "</strong>" + outputHTML.substring(div - 1, divEND + 1) + "<strong>" + outputHTML.substring(
                     divEND + 1, divClose) + "</strong>" + outputHTML.substring(divClose, boldEnd) + outputHTML.substring(boldEnd + "</strong>".length());
               outputHTML = Lower;
               // Set up in case there is also an <em>
               Lower = outputHTML.toLowerCase();
               italic = Lower.indexOf("<em>");
               div = Lower.indexOf("<div");
            }
         }
         if ((italic != -1) && (italic < div)) {
            // find the </b>
            int italicEnd = Lower.indexOf("</em>");
            if (italicEnd > div) {
               int divEND = Lower.indexOf(">", div), divClose = Lower.indexOf("</div>", div);
               Lower =
                  outputHTML.substring(0, div - 1) + "</em>" + outputHTML.substring(div - 1, divEND + 1) + "<em>" + outputHTML.substring(
                     divEND + 1, divClose) + "</em>" + outputHTML.substring(divClose, italicEnd) + outputHTML.substring(italicEnd + "</em>".length());
               outputHTML = Lower;
            }
         }

      }
      Lower = outputHTML.toLowerCase();
      int img = Lower.indexOf("img ");
      while (img != -1) {
         int src = Lower.indexOf("src=", img);
         if (src != -1) {
            src += 4;
            char qte = Lower.charAt(src);
            int iEnd = Lower.indexOf(qte, src + 1);
            if (imageFiles.isEmpty()) {
               imageFiles = outputHTML.substring(src + 1, iEnd);
            } else {
               imageFiles += "," + outputHTML.substring(src + 1, iEnd);
            }
            img = Lower.indexOf("img ", src);
         }
      }
      imageFileList.append(imageFiles);

      return outputHTML;
   }

   /************************************************************
    * @param input input string with HTML tags
    * @return a string with the HTML tags removed
    */
   private String clearHTML(String input) {
      String returnValue = "", processString = input;
      int openBracket = processString.indexOf('<'), closeBracket;
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
}
