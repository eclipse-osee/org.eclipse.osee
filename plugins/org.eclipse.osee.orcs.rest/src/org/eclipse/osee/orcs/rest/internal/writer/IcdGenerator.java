/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.orcs.rest.internal.writer;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.token.InterfaceStructureCategoryAttribute.InterfaceSubMessageCategoryEnum;
import org.eclipse.osee.framework.jdk.core.util.CellData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Audrey E. Denk
 */
public class IcdGenerator {

   private final OrcsApi orcsApi;

   public IcdGenerator(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void runOperation(OrcsApi providedOrcs, Writer providedWriter, BranchId branch, String connectionName) throws IOException {
      ArtifactReadable conn =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.InterfaceConnection).andNameEquals(
            connectionName).getArtifact();
      List<ArtifactReadable> messages = conn.getRelated(CoreRelationTypes.InterfaceConnectionContent_Message).getList();
      List<ArtifactReadable> subMessages = new ArrayList<>();
      List<ArtifactReadable> structures = new ArrayList<>();
      SortedMap<String, String> structureLinks = new TreeMap<String, String>();
      for (ArtifactReadable message : messages) {
         subMessages.addAll(message.getRelated(CoreRelationTypes.InterfaceMessageContent_SubMessage).getList());
      }
      for (ArtifactReadable subMessage : subMessages) {
         structures.addAll(subMessage.getRelated(CoreRelationTypes.InterfaceSubMessageContent_Structure).getList());
      }
      int worksheetIndex = 0;
      for (ArtifactReadable structure : structures) {
         worksheetIndex++;
         String sheetName = structure.getName().replace("Command Taskfile", "CT").replace("Status Taskfile", "ST");
         if (sheetName.length() > 30) {
            sheetName = sheetName.substring(0, 25) + "_" + Integer.toString(worksheetIndex);
         }
         structureLinks.put(structure.getName(), sheetName);
      }
      ISheetWriter writer = new ExcelXmlWriter(providedWriter);
      createMessageSubMessageSummary(writer, branch, conn, messages);
      createStructureNamesSheet(writer, branch, structureLinks);
      createMessageDetailSheet(writer, branch, subMessages, structureLinks);
      createSubMessageSheets(writer, branch, subMessages, structureLinks);
      writer.endWorkbook();
   }

   private void createStructureNamesSheet(ISheetWriter writer, BranchId branch, SortedMap<String, String> structures) throws IOException {
      int columnCount = 3;
      writer.startSheet("Structures Names", columnCount);
      Iterator iterator = structures.keySet().iterator();
      int size = structures.size();
      int firstCol = Math.floorDiv(size, 3);
      int secCol = firstCol * 2;
      int count = 0;
      SortedMap<String, String> col1 = new TreeMap<String, String>();
      SortedMap<String, String> col2 = new TreeMap<String, String>();
      SortedMap<String, String> col3 = new TreeMap<String, String>();
      while (iterator.hasNext()) {
         String sName = (String) iterator.next();
         String sLink = structures.get(sName);
         if (count < firstCol) {
            col1.put(sName, sLink);
         } else if (count < secCol) {
            col2.put(sName, sLink);
         } else {
            col3.put(sName, sLink);
         }
         count++;
      }
      Iterator col1Iterator = col1.keySet().iterator();
      Iterator col2Iterator = col2.keySet().iterator();
      Iterator col3Iterator = col3.keySet().iterator();
      while (col1Iterator.hasNext()) {
         CellData name = new CellData();
         name.setText((String) col1Iterator.next());
         name.setHyperlink(col1.get(name.getText()));
         writer.writeCell(name);
         if (col2Iterator.hasNext()) {
            name.setText((String) col2Iterator.next());
            name.setHyperlink(col2.get(name.getText()));
            writer.writeCell(name);
         }
         if (col3Iterator.hasNext()) {
            name.setText((String) col3Iterator.next());
            name.setHyperlink(col3.get(name.getText()));
            writer.writeCell(name);
         }
         writer.endRow();
      }
      writer.endSheet();
   }

   private void createMessageDetailSheet(ISheetWriter writer, BranchId branch, List<ArtifactReadable> subMessages, SortedMap<String, String> structureLinks) throws IOException {
      List<String> headingsList = new ArrayList<String>();
      int columnCount = 14;
      headingsList.add("Category");
      headingsList.add("SubMessage Name");
      headingsList.add("Tx Rate");
      headingsList.add("Sim Min");
      headingsList.add("Sim Max");
      headingsList.add("# of Attributes");
      headingsList.add("Size in Bytes");
      headingsList.add("BPS Min");
      headingsList.add("BPS Max");
      headingsList.add("Initiator");
      headingsList.add("Msg #");
      headingsList.add("SubMsg #");
      headingsList.add("Taskfile Type");
      headingsList.add("Description");
      Object[] headings = headingsList.toArray();
      writer.startSheet("SubMessage Summary", columnCount);
      writer.writeRow(headings);

      for (ArtifactReadable artifact : subMessages) {
         for (ArtifactReadable struct : artifact.getRelated(
            CoreRelationTypes.InterfaceSubMessageContent_Structure).getList()) {
            String sheetName = structureLinks.get(struct.getName());

            List<ArtifactReadable> elements =
               struct.getRelated(CoreRelationTypes.InterfaceStructureContent_DataElement).getList();
            Integer sum =
               elements.stream().map(x -> (x.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementByteSize,
                  0) * (x.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexEnd,
                     0) - x.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexStart, 0) + 1))).reduce(0,
                        Integer::sum);
            ArtifactReadable message =
               artifact.getRelated(CoreRelationTypes.InterfaceMessageContent_Message).getAtMostOneOrDefault(
                  ArtifactReadable.SENTINEL);
            String msgNumber = message.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageNumber, "0");
            String minSim = struct.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMinSimultaneity, "n/a");
            String maxSim = struct.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMaxSimultaneity, "n/a");
            String msgRateText = struct.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate, "n/a");
            String minBps = Strings.EMPTY_STRING;
            String maxBps = Strings.EMPTY_STRING;
            try {
               int msgRate = Integer.parseInt(msgRateText.replace("-A", ""));
               minBps = Integer.toString(Integer.parseInt(minSim) * sum * msgRate);
               maxBps = Integer.toString(Integer.parseInt(maxSim) * sum * msgRate);
            } catch (NumberFormatException nfe) {
               minBps = "Classified";
               maxBps = "Classified";
            }
            String cat = struct.getSoleAttributeValue(CoreAttributeTypes.InterfaceStructureCategory);
            InterfaceSubMessageCategoryEnum catEnum =
               CoreAttributeTypes.InterfaceStructureCategory.getEnumValues().stream().filter(
                  e -> cat.equals(e.getName())).findAny().orElse(null);

            writer.writeCell(catEnum.getId() + " " + catEnum.getName());
            CellData name = new CellData(struct.getSoleAttributeValue(CoreAttributeTypes.Name), sheetName,
               Strings.EMPTY_STRING, Strings.EMPTY_STRING);
            writer.writeCell(name);

            writer.writeCell(msgRateText);
            writer.writeCell(minSim);
            writer.writeCell(maxSim);
            writer.writeCell(elements.size());
            writer.writeCell(sum);
            writer.writeCell(minBps);
            writer.writeCell(maxBps);
            writer.writeCell(artifact.getSoleAttributeAsString(CoreAttributeTypes.InterfaceInitiator, "n/a"));
            writer.writeCell(msgNumber);
            writer.writeCell(artifact.getSoleAttributeAsString(CoreAttributeTypes.InterfaceSubMessageNumber, "n/a"));
            writer.writeCell(struct.getSoleAttributeAsString(CoreAttributeTypes.InterfaceTaskFileType, "n/a"));
            writer.writeCell(struct.getSoleAttributeAsString(CoreAttributeTypes.Description, ""));
            writer.endRow();
         }

      }

      writer.endSheet();
   }

   private void createSubMessageSheets(ISheetWriter writer, BranchId branch, List<ArtifactReadable> subMessages, SortedMap<String, String> structureLinks) throws IOException {
      List<String> headingsList = new ArrayList<String>();
      int columnCount = 13;
      headingsList.add("Begin Word");
      headingsList.add("Begin Byte");
      headingsList.add("Number of Bytes");
      headingsList.add("Ending Word");
      headingsList.add("Ending Byte");
      headingsList.add("Element Name");
      headingsList.add("Type");
      headingsList.add("Units");
      headingsList.add("Valid Range");
      headingsList.add("Alterable After Creation");
      headingsList.add("Description");
      headingsList.add("Enumerated Literals");
      headingsList.add("Notes");
      Object[] headings = headingsList.toArray();
      for (ArtifactReadable artifact : subMessages) {
         for (ArtifactReadable struct : artifact.getRelated(
            CoreRelationTypes.InterfaceSubMessageContent_Structure).getList()) {

            int byteLocation = 0;
            writer.startSheet(structureLinks.get(struct.getName()), columnCount);
            writer.writeRow(headings);

            for (ArtifactReadable element : struct.getRelated(
               CoreRelationTypes.InterfaceStructureContent_DataElement).getList()) {

               Integer byteSize = element.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementByteSize, 0);
               printDataElementRow(writer, branch, element, byteLocation, byteSize);
               byteLocation = byteLocation + byteSize;
            }
            writer.endSheet();
         }

      }
   }

   private void printDataElementRow(ISheetWriter writer, BranchId branch, ArtifactReadable artifact, Integer byteLocation, Integer byteSize) throws IOException {

      Integer beginWord = Math.floorDiv(byteLocation, 4);
      Integer beginByte = Math.floorMod(byteLocation, 4);
      Integer endWord = Math.floorDiv(byteLocation + byteSize - 1, 4);
      Integer endByte = Math.floorMod(byteLocation + byteSize - 1, 4);
      String enumLiterals = artifact.getSoleAttributeAsString(CoreAttributeTypes.InterfaceEnumLiteral, "n/a");
      String elementName = artifact.getSoleAttributeValue(CoreAttributeTypes.Name);
      String dataType = artifact.getSoleAttributeAsString(CoreAttributeTypes.InterfaceElementDataType, "n/a");
      String units = artifact.getSoleAttributeAsString(CoreAttributeTypes.InterfaceElementUnits, "n/a");
      String validRange = artifact.getSoleAttributeAsString(CoreAttributeTypes.InterfaceElementValidRange, "n/a");
      String alterable = artifact.getSoleAttributeAsString(CoreAttributeTypes.InterfaceElementAlterable, "No");

      if (alterable.equalsIgnoreCase("true")) {
         alterable = "Yes";
      } else {
         alterable = "No";
      }
      String description = artifact.getSoleAttributeAsString(CoreAttributeTypes.Description, "n/a");
      String notes = artifact.getSoleAttributeAsString(CoreAttributeTypes.Notes, "");

      if (artifact.getExistingRelationTypes().contains(CoreRelationTypes.InterfaceElementEnumeration)) {
         enumLiterals = "";
         ArtifactReadable enumDef =
            artifact.getRelated(CoreRelationTypes.InterfaceElementEnumeration_EnumerationDef).getExactlyOne();

         for (ArtifactReadable enumState : enumDef.getRelated(
            CoreRelationTypes.InterfaceEnumeration_EnumerationState).getList()) {
            enumLiterals = enumLiterals + enumState.getSoleAttributeAsString(CoreAttributeTypes.InterfaceEnumOrdinal,
               "0") + " = " + enumState.getSoleAttributeAsString(CoreAttributeTypes.Name, "") + "; ";
         }
      }

      if (artifact.getSoleAttributeAsString(CoreAttributeTypes.InterfaceElementIsArray, "false").equalsIgnoreCase(
         "true")) {
         int startIndex = artifact.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexStart, 0);
         int endIndex = artifact.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexEnd, 0);
         for (int i = startIndex; i < endIndex + 1; i++) {
            //
            writer.writeCell(beginWord);
            writer.writeCell(beginByte);
            writer.writeCell(byteSize);
            writer.writeCell(endWord);
            writer.writeCell(endByte);
            writer.writeCell(dataType);
            writer.writeCell(elementName.replace("[i]", Integer.toString(i)));
            writer.writeCell(units);
            writer.writeCell(validRange);
            writer.writeCell(alterable);
            writer.writeCell(description);
            writer.writeCell(enumLiterals);
            writer.writeCell(notes);
            byteLocation = byteLocation + byteSize;
            beginWord = Math.floorDiv(byteLocation, 8);
            beginByte = Math.floorMod(byteLocation, 8);
            endWord = Math.floorDiv(byteLocation + byteSize, 8);
            endByte = Math.floorMod(byteLocation + byteSize, 8);
            writer.endRow();
         }

      } else {
         writer.writeCell(beginWord);
         writer.writeCell(beginByte);
         writer.writeCell(byteSize);
         writer.writeCell(endWord);
         writer.writeCell(endByte);
         writer.writeCell(dataType);
         writer.writeCell(elementName);
         writer.writeCell(units);
         writer.writeCell(validRange);
         writer.writeCell(alterable);
         writer.writeCell(description);
         writer.writeCell(enumLiterals);
         writer.writeCell(notes);
         writer.endRow();

      }

   }

   private void createMessageSubMessageSummary(ISheetWriter writer, BranchId branch, ArtifactReadable connection, List<ArtifactReadable> messages) throws IOException {
      int columnCount = 10;
      String primaryNode = connection.getSoleAttributeAsString(CoreAttributeTypes.InterfaceConnectionPrimaryNode);
      String secondaryNode = connection.getSoleAttributeAsString(CoreAttributeTypes.InterfaceConnectionSecondaryNode);
      List<ArtifactReadable> connectionMessages = messages.stream().filter(
         e -> (e.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageType)).equals("Operational")).collect(
            Collectors.toList());
      List<ArtifactReadable> primaryList = connectionMessages.stream().filter(
         e -> primaryNode.equals(e.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageNode))).collect(
            Collectors.toList());

      List<ArtifactReadable> secondaryList = connectionMessages.stream().filter(
         e -> secondaryNode.equals(e.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageNode))).collect(
            Collectors.toList());

      writer.startSheet("Message and SubMessage Summary", columnCount);
      writer.writeCell(
         new CellData(primaryNode + " Initiated Message", Strings.EMPTY_STRING, "4", Strings.EMPTY_STRING));
      writer.writeCell(
         new CellData(secondaryNode + " Initiated Message", Strings.EMPTY_STRING, "4", Strings.EMPTY_STRING));
      writer.endRow();
      writer.writeCell("No.");
      writer.writeCell("Rate");
      writer.writeCell("Read/Write");
      writer.writeCell(new CellData("Content", Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING));
      writer.writeCell("No.");
      writer.writeCell("Rate");
      writer.writeCell("Read/Write");
      writer.writeCell(new CellData("Content", Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING));
      writer.endRow();
      int lastMessageNumber = 0;
      if (primaryList.size() > secondaryList.size()) {
         lastMessageNumber = primaryList.size();
      } else {
         lastMessageNumber = secondaryList.size();
      }
      for (int i = 0; i < lastMessageNumber; i++) {
         ArtifactReadable primaryMessage = ArtifactReadable.SENTINEL;
         ArtifactReadable secondaryMessage = ArtifactReadable.SENTINEL;
         List<ArtifactReadable> primarySubMessages = new ArrayList<>();
         List<ArtifactReadable> secondarySubMessages = new ArrayList<>();
         try {
            primaryMessage = primaryList.get(i);
            primarySubMessages =
               primaryMessage.getRelated(CoreRelationTypes.InterfaceMessageContent_SubMessage).getList();
         } catch (IndexOutOfBoundsException e) {
            //do nothing leave primaryMessage and primarySubMessages empty
         }
         try {
            secondaryMessage = secondaryList.get(i);

            secondarySubMessages =
               secondaryMessage.getRelated(CoreRelationTypes.InterfaceMessageContent_SubMessage).getList();
         } catch (IndexOutOfBoundsException e) {
            //do nothing leave secondaryMessage and secondarySubMessages empty
         }
         int subMessagesCount = Math.max(primarySubMessages.size(), secondarySubMessages.size());
         String mergeDown = Integer.toString(subMessagesCount + 1);
         if (primaryMessage.isValid()) {
            writer.writeCell(
               new CellData(primaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageNumber),
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(
               new CellData(primaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate),
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(
               new CellData(primaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageWriteAccess),
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(new CellData(primaryMessage.getName(), Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING));
         } else {
            writer.writeCell(new CellData(Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(new CellData(Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(new CellData(Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(new CellData("", Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING));
         }
         if (secondaryMessage.isValid()) {
            writer.writeCell(
               new CellData(secondaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageNumber),
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(
               new CellData(secondaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate),
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(
               new CellData(secondaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageWriteAccess),
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(new CellData(secondaryMessage.getName(), Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING));
         } else {
            writer.writeCell(new CellData(Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(new CellData(Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(new CellData(Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(new CellData("", Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING));
         }
         writer.endRow();
         if (primaryMessage.isValid()) {
            writer.writeCell(new CellData("Sofware Interface Header", Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING),
               3);
         } else {
            writer.writeCell(new CellData(Strings.EMPTY_STRING, Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING), 3);
         }
         if (secondaryMessage.isValid()) {
            writer.writeCell(new CellData("Sofware Interface Header", Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING),
               8);
         } else {
            writer.writeCell(new CellData(Strings.EMPTY_STRING, Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING), 8);
         }
         writer.endRow();
         for (int j = 0; j < subMessagesCount; j++) {
            ArtifactReadable primarySubMessage;
            ArtifactReadable secondarySubMessage;
            try {
               primarySubMessage = primarySubMessages.get(j);
            } catch (IndexOutOfBoundsException e) {
               primarySubMessage = ArtifactReadable.SENTINEL;
            }
            try {
               secondarySubMessage = secondarySubMessages.get(j);
            } catch (IndexOutOfBoundsException e) {
               secondarySubMessage = ArtifactReadable.SENTINEL;
            }
            if (primarySubMessage.isValid()) {
               writer.writeCell(new CellData("Submessage " + Integer.toString(j) + ":", Strings.EMPTY_STRING,
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING), 3);
               writer.writeCell(primarySubMessage.getName());
            }
            if (secondarySubMessage.isValid()) {
               writer.writeCell(new CellData("Submessage " + Integer.toString(j) + ":", Strings.EMPTY_STRING,
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING), 8);
               writer.writeCell(secondarySubMessage.getName());
            }
            writer.endRow();
         }
      }
      writer.endSheet();
   }
}
