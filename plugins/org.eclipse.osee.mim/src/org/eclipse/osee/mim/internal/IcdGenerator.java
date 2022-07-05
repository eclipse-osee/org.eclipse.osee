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

package org.eclipse.osee.mim.internal;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.token.InterfaceStructureCategoryAttribute.InterfaceStructureCategoryEnum;
import org.eclipse.osee.framework.jdk.core.util.CellData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter.STYLE;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceNode;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.mim.types.PlatformTypeToken;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.TransactionReadable;

/**
 * @author Audrey E. Denk
 */
public class IcdGenerator {

   private final OrcsApi orcsApi;
   private final AtsApi atsApi;
   private final InterfaceMessageApi interfaceMessageApi;
   private final InterfaceStructureApi interfaceStructureApi;

   private final Map<ArtifactId, StructureInfo> structuresList;
   private final Map<String, StructureInfo> headersList;
   private final Map<ArtifactId, InterfaceSubMessageToken> messageHeaders;
   private final Map<String, InterfaceStructureToken> messageHeaderStructures;

   public IcdGenerator(MimApi mimApi) {
      this.orcsApi = mimApi.getOrcsApi();
      this.atsApi = mimApi.getAtsApi();
      this.structuresList = new HashMap<>();
      this.headersList = new HashMap<>();
      this.messageHeaders = new HashMap<>();
      this.messageHeaderStructures = new HashMap<>();

      this.interfaceMessageApi = mimApi.getInterfaceMessageApi();
      this.interfaceStructureApi = mimApi.getInterfaceStructureApi();
   }

   public void runOperation(Writer providedWriter, BranchId branch, ArtifactId view, ArtifactId connectionId) throws IOException {

      ArtifactReadable conn =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.InterfaceConnection).andId(
            connectionId).follow(CoreRelationTypes.InterfaceConnectionContent_Message).follow(
               CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage).follow(
                  CoreRelationTypes.InterfaceSubMessageContent_Structure).asArtifact();
      ArtifactReadable primaryNode =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.InterfaceNode).andId(
            conn.getRelated(CoreRelationTypes.InterfaceConnectionPrimary_Node).getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL)).asArtifact();

      ArtifactReadable secondaryNode =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.InterfaceNode).andId(
            conn.getRelated(CoreRelationTypes.InterfaceConnectionSecondary_Node).getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL)).asArtifact();

      List<ArtifactReadable> messages = conn.getRelated(CoreRelationTypes.InterfaceConnectionContent_Message).getList();
      List<ArtifactReadable> subMessages = new ArrayList<>();
      List<InterfaceSubMessageToken> subMessagesWithHeaders = new LinkedList<>();
      List<InterfaceStructureToken> structures = new ArrayList<>();
      SortedMap<String, String> structureLinks = new TreeMap<String, String>();
      for (ArtifactReadable message : messages) {
         ArtifactReadable sendingNode =
            (message.getRelated(CoreRelationTypes.InterfaceMessageSendingNode_Node).getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL).getIdString().equalsIgnoreCase(
                  primaryNode.getIdString())) ? primaryNode : secondaryNode;
         InterfaceMessageToken msg = new InterfaceMessageToken(message);
         msg.setInitiatingNode(new InterfaceNode(sendingNode));
         List<ArtifactReadable> messageSubMessages =
            message.getRelated(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage).getList();
         List<InterfaceSubMessageToken> smsgTokens =
            messageSubMessages.stream().map(art -> new InterfaceSubMessageToken(art)).collect(Collectors.toList());
         if (message.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageType, "error").equals("Operational")) {
            InterfaceSubMessageToken header = interfaceMessageApi.getMessageHeader(msg);
            messageHeaders.put(ArtifactId.valueOf(message.getId()), header);
            smsgTokens.add(0, header);

            InterfaceStructureToken headerStruct =
               interfaceStructureApi.getMessageHeaderStructure(branch, ArtifactId.valueOf(message.getId()));
            messageHeaderStructures.put(header.getName(), headerStruct);
         }
         subMessages.addAll(messageSubMessages);
         subMessagesWithHeaders.addAll(smsgTokens);
         for (InterfaceSubMessageToken smsg : smsgTokens) {
            List<InterfaceStructureToken> structs = new LinkedList<>();
            if (smsg.getId() == 0) {
               structs.add(0,
                  interfaceStructureApi.getMessageHeaderStructure(branch, ArtifactId.valueOf(message.getId())));
            } else {
               structs.addAll(smsg.getArtifactReadable().getRelated(
                  CoreRelationTypes.InterfaceSubMessageContent_Structure).getList().stream().map(
                     art -> new InterfaceStructureToken(art)).collect(Collectors.toList()));
            }
            structures.addAll(structs);
         }
      }

      int worksheetIndex = 0;
      for (InterfaceStructureToken structure : structures) {
         worksheetIndex++;
         String sheetName = structure.getName().replace("Command Taskfile", "CT").replace("Status Taskfile", "ST");
         if (sheetName.length() > 30) {
            sheetName = sheetName.substring(0, 25) + "_" + Integer.toString(worksheetIndex);
         }
         if (structure.getArtifactReadable() != null) {
            String abbrevName =
               structure.getArtifactReadable().getSoleAttributeAsString(CoreAttributeTypes.GeneralStringData, "null");
            if (!abbrevName.equals("null")) {
               sheetName = abbrevName;
            }
         }
         structureLinks.put(structure.getName(), sheetName);
      }
      ExcelXmlWriter writer = new ExcelXmlWriter(providedWriter);

      createChangeSummary(writer, branch);
      createMessageSubMessageSummary(writer, conn, primaryNode, secondaryNode, messages, subMessages);
      createStructureNamesSheet(writer, structureLinks);
      createMessageDetailSheet(writer, branch, view, primaryNode, secondaryNode, messages, structureLinks);
      createSubMessageSheets(writer, view, subMessagesWithHeaders, structures, structureLinks);

      writer.endWorkbook();
   }

   private void createChangeSummary(ExcelXmlWriter writer, BranchId branch) throws IOException {
      SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
      List<String> headingsList = new ArrayList<String>();
      int columnCount = 3;
      headingsList.add("Team Workflow");
      headingsList.add("Date");
      headingsList.add("Change Description");
      Object[] headings = headingsList.toArray();
      writer.startSheet("Change Summary", columnCount);
      writer.writeHeaderRow(headings);

      orcsApi.getQueryFactory().transactionQuery().andBranch(branch).getResults().getList().stream().forEach(tx -> {
         if (tx.getCommitArt().getId() != -1) {
            IAtsWorkItem change = atsApi.getQueryService().getWorkItem(tx.getCommitArt().getIdString());
            if (change != null && change.getId() != -1) {
               try {
                  writer.writeCell(change.getAtsId());
                  writer.writeCell(format.format(tx.getDate()));
                  writer.writeCell(change.getName());
                  writer.endRow();
               } catch (IOException ex) {
                  //
               }
            }
         }
      });

      writer.endSheet();
   }

   private void createStructureNamesSheet(ExcelXmlWriter writer, SortedMap<String, String> structures) throws IOException {

      List<String> headingsList = new ArrayList<String>();
      headingsList.add("Structure Name");
      headingsList.add("Structure Name");
      headingsList.add("Structure Name");
      Object[] headings = headingsList.toArray();
      int columnCount = 3;
      writer.startSheet("Structures Names", columnCount);
      writer.writeHeaderRow(headings);
      Iterator<String> iterator = structures.keySet().iterator();
      int size = structures.size();
      int firstCol = Math.floorDiv(size, 3);
      int secCol = firstCol * 2;
      int count = 0;
      SortedMap<String, String> col1 = new TreeMap<String, String>();
      SortedMap<String, String> col2 = new TreeMap<String, String>();
      SortedMap<String, String> col3 = new TreeMap<String, String>();
      while (iterator.hasNext()) {
         String sName = iterator.next();
         String sLink = structures.get(sName);
         if (count <= firstCol) {
            col1.put(sName, sLink);
         } else if (count <= secCol) {
            col2.put(sName, sLink);
         } else {
            col3.put(sName, sLink);
         }
         count++;
      }
      Iterator<String> col1Iterator = col1.keySet().iterator();
      Iterator<String> col2Iterator = col2.keySet().iterator();
      Iterator<String> col3Iterator = col3.keySet().iterator();
      while (col1Iterator.hasNext()) {
         CellData name = new CellData();
         name.setText(col1Iterator.next());
         name.setHyperlink(col1.get(name.getText()));
         writer.writeCell(name);
         if (col2Iterator.hasNext()) {
            name.setText(col2Iterator.next());
            name.setHyperlink(col2.get(name.getText()));
            writer.writeCell(name);
         }
         if (col3Iterator.hasNext()) {
            name.setText(col3Iterator.next());
            name.setHyperlink(col3.get(name.getText()));
            writer.writeCell(name);
         }
         writer.endRow();
      }
      writer.endSheet();
   }

   private void createMessageDetailSheet(ExcelXmlWriter writer, BranchId branch, ArtifactId view, ArtifactReadable primaryNode, ArtifactReadable secondaryNode, List<ArtifactReadable> messages, SortedMap<String, String> structureLinks) throws IOException {
      int totalMinSim = 0;
      int totalMaxSim = 0;
      int totalNumAttrs = 0;
      int totalMinBps = 0;
      int totalMaxBps = 0;
      int maxAttrs = 0;
      int totalStructs = 0;

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
      writer.startSheet("Structure Summary", columnCount);
      writer.writeHeaderRow(headings);
      for (ArtifactReadable message : messages) {
         ArtifactReadable sendingNode =
            (message.getRelated(CoreRelationTypes.InterfaceMessageSendingNode_Node).getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL).getIdString().equalsIgnoreCase(
                  primaryNode.getIdString())) ? primaryNode : secondaryNode;
         String msgNumber = message.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageNumber, "0");

         String msgRateText;
         int msgRate = -1;
         String msgPeriodicity =
            message.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessagePeriodicity, "Aperiodic");
         if (msgPeriodicity.equals("Periodic")) {
            msgRateText = message.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate, "Error");
            try {
               msgRate = Integer.parseInt(msgRateText);
            } catch (NumberFormatException nfe) {
               //do nothing
            }
         } else {
            msgRateText = msgPeriodicity;
         }
         if (message.getExistingAttributeTypes().contains(CoreAttributeTypes.InterfaceMessageRate)) {
            msgRateText = message.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate, "Error");
            try {
               msgRate = Integer.parseInt(msgRateText);
            } catch (NumberFormatException nfe) {
               //do nothing
            }
            if (msgPeriodicity.equals("Aperiodic")) {
               msgRateText = msgRateText + "-A";
            }
         }

         List<InterfaceSubMessageToken> subMessages =
            message.getRelated(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage).getList().stream().map(
               art -> new InterfaceSubMessageToken(art)).collect(Collectors.toList());
         if (message.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageType, "Error").equals("Operational")) {
            subMessages.add(0, messageHeaders.get(ArtifactId.valueOf(message.getId())));
         }

         for (InterfaceSubMessageToken subMessage : subMessages) {

            List<InterfaceStructureToken> structures = new LinkedList<>();
            if (subMessage.getId() == 0) {
               structures.add(messageHeaderStructures.get(subMessage.getName()));
            } else {
               structures.addAll(subMessage.getArtifactReadable().getRelated(
                  CoreRelationTypes.InterfaceSubMessageContent_Structure).getList().stream().map(
                     art -> new InterfaceStructureToken(art)).collect(Collectors.toList()));
            }

            for (InterfaceStructureToken struct : structures) {
               String sheetName = structureLinks.get(struct.getName());

               List<InterfaceStructureElementToken> elements;
               List<ArtifactReadable> elementArts = new LinkedList<>();
               if (struct.getId() == 0) {
                  elements = struct.getElements();
               } else {
                  elementArts = orcsApi.getQueryFactory().fromBranch(branch, view).andRelatedTo(
                     CoreRelationTypes.InterfaceStructureContent_Structure, struct.getArtifactReadable()).follow(
                        CoreRelationTypes.InterfaceElementPlatformType_PlatformType).follow(
                           CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet).follow(
                              CoreRelationTypes.InterfaceEnumeration_EnumerationState).asArtifacts();
                  elements = elementArts.stream().map(art -> new InterfaceStructureElementToken(art)).collect(
                     Collectors.toList());

               }

               Integer sum = 0;
               int elementCount = 0;

               for (InterfaceStructureElementToken element : elements) {
                  elementCount = struct.isAutogenerated() ? elementCount + 1 : elementCount + element.getArrayLength();
                  sum = sum + (int) element.getElementSizeInBytes();
               }

               String minSim = struct.getInterfaceMinSimultaneity();
               String maxSim = struct.getInterfaceMaxSimultaneity();

               String minBps = "Classified";
               String maxBps = "Classified";
               if (msgRate > 0) {
                  try {
                     minBps = Integer.toString(Integer.parseInt(minSim) * sum * msgRate);
                  } catch (NumberFormatException nfe) {
                     minBps = "Classified";
                  }
                  try {
                     maxBps = Integer.toString(Integer.parseInt(maxSim) * sum * msgRate);
                  } catch (NumberFormatException nfe) {
                     maxBps = "Classified";
                  }
               }
               String cat = struct.getInterfaceStructureCategory();
               InterfaceStructureCategoryEnum catEnum =
                  CoreAttributeTypes.InterfaceStructureCategory.getEnumValues().stream().filter(
                     e -> cat.equals(e.getName())).findAny().orElse(null);

               String subMsgNumber = subMessage.getInterfaceSubMessageNumber();
               String taskFileType = struct.getInterfaceTaskFileType() + "";
               String desc = struct.getDescription();

               StructureInfo structureInfo = new StructureInfo(struct.getName(), cat, msgRateText, minSim, maxSim,
                  elementCount, sum, sendingNode.getName(), msgNumber, subMsgNumber, taskFileType, desc, elementArts);
               if (struct.getId() == 0) {
                  headersList.put(struct.getName(), structureInfo);
               } else {
                  structuresList.put(ArtifactId.valueOf(struct.getId()), structureInfo);
               }

               CellData name = new CellData(struct.getName(), sheetName, Strings.EMPTY_STRING, Strings.EMPTY_STRING);

               totalMinSim += stringToInt(minSim);
               totalMaxSim += stringToInt(maxSim);
               totalNumAttrs += elementCount;
               totalMinBps += stringToInt(minBps);
               totalMaxBps += stringToInt(maxBps);
               maxAttrs = Math.max(maxAttrs, elementCount);
               totalStructs++;

               writer.writeCell(catEnum.getId() + " " + catEnum.getName());
               writer.writeCell(name);
               writer.writeCell(msgRateText);
               writer.writeCell(minSim);
               writer.writeCell(maxSim);
               writer.writeCell(elementCount);
               writer.writeCell(sum);
               writer.writeCell(minBps);
               writer.writeCell(maxBps);
               writer.writeCell(sendingNode.getName());
               writer.writeCell(msgNumber);
               writer.writeCell(subMsgNumber);
               writer.writeCell(taskFileType);
               writer.writeCell(desc);
               writer.endRow();
            }
         }
      }

      writer.endRow(); // Empty row between structures and summary

      Object[] totalsHeaders = {" ", " ", " ", "Sim Min", "Sim Max", "# of Attributes", " ", "Min", "Max"};
      writer.writeHeaderRow(totalsHeaders);

      writer.setCellStyle(STYLE.BOLD, 2);
      writer.setCellStyle(STYLE.BOLD, 6);
      writer.writeEmptyCell();
      writer.writeEmptyCell();
      writer.writeCell("Totals:");
      writer.writeCell(totalMinSim);
      writer.writeCell(totalMaxSim);
      writer.writeCell(totalNumAttrs);
      writer.writeCell("B/s:");
      writer.writeCell(totalMinBps);
      writer.writeCell(totalMaxBps);
      writer.endRow();

      writer.setCellStyle(STYLE.BOLD, 3);
      writer.setCellStyle(STYLE.BOLD, 6);
      writer.writeEmptyCell();
      writer.writeEmptyCell();
      writer.writeEmptyCell();
      writer.writeCell("Max # Attributes:");
      writer.writeCell(maxAttrs);
      writer.writeEmptyCell();
      writer.writeCell("MB/s:");
      writer.writeCell(totalMinBps / 1048576.0);
      writer.writeCell(totalMaxBps / 1048576.0);
      writer.endRow();

      writer.setCellStyle(STYLE.BOLD, 3);
      writer.setCellStyle(STYLE.BOLD, 6);
      writer.writeEmptyCell();
      writer.writeEmptyCell();
      writer.writeEmptyCell();
      writer.writeCell("Unique Structures:");
      writer.writeCell(totalStructs);
      writer.writeEmptyCell();
      writer.writeCell("Mb/s:");
      writer.writeCell(totalMinBps * 8.0 / 1048576.0);
      writer.writeCell(totalMaxBps * 8.0 / 1048576.0);
      writer.endRow();

      writer.endSheet();
   }

   private int stringToInt(String str) {
      try {
         return Integer.parseInt(str);
      } catch (NumberFormatException ex) {
         return 0;
      }
   }

   private void printFirstRowInStructureSheet(ExcelXmlWriter writer, InterfaceStructureToken structure, SortedMap<String, String> structureLinks, StructureInfo info) throws IOException {
      List<String> headingsList = new ArrayList<String>();

      headingsList.add("Sheet Type");
      headingsList.add("Full Sheet Name");
      headingsList.add("Abbrev. Sheet Name");
      headingsList.add("Most Recent Update");
      headingsList.add("Category");
      headingsList.add("Transmission Rate");
      headingsList.add("Min Simultaneity");
      headingsList.add("Max Simultaneity");
      headingsList.add("# of attributes");
      headingsList.add("Size in Bytes");
      headingsList.add("Initiator");
      headingsList.add("Msg #");
      headingsList.add("SubMsg #");
      headingsList.add("Taskfile");
      headingsList.add("Description");
      Object[] headings = headingsList.toArray();
      writer.writeHeaderRow(headings);
      writer.writeCell("Functional");
      writer.writeCell(info.name);
      writer.writeCell(structure.getId() == 0 ? info.name : structure.getArtifactReadable().getSoleAttributeAsString(
         CoreAttributeTypes.GeneralStringData, ""));
      writer.writeCell(
         structure.getId() == 0 ? Strings.EMPTY_STRING : orcsApi.getQueryFactory().transactionQuery().andTxId(
            structure.getArtifactReadable().getTransaction()).getResults().getAtMostOneOrDefault(
               TransactionReadable.SENTINEL).getDate());
      writer.writeCell(info.category);
      writer.writeCell(info.rate);
      writer.writeCell(info.minSim);
      writer.writeCell(info.maxSim);
      writer.writeCell(info.numAttributes);
      writer.writeCell(info.sizeInBytes);
      writer.writeCell(info.initiator);
      writer.writeCell(info.msgNum);
      writer.writeCell(info.subMsgNum);
      writer.writeCell(info.taskfile);
      writer.writeCell(info.description);
      writer.endRow();
   }

   private void createSubMessageSheets(ExcelXmlWriter writer, ArtifactId view, List<InterfaceSubMessageToken> subMessages, List<InterfaceStructureToken> structures, SortedMap<String, String> structureLinks) throws IOException {
      List<String> headingsList = new ArrayList<String>();
      int columnCount = 15;
      headingsList.add("Begin Word");
      headingsList.add("Begin Byte");
      headingsList.add("Number of Bytes");
      headingsList.add("Ending Word");
      headingsList.add("Ending Byte");
      headingsList.add("Type");
      headingsList.add("Element Name");
      headingsList.add("Units");
      headingsList.add("Valid Range");
      headingsList.add("Alterable After Creation");
      headingsList.add("Description");
      headingsList.add("Enumerated Literals");
      headingsList.add("Notes");
      Object[] headings = headingsList.toArray();
      for (InterfaceSubMessageToken subMessage : subMessages) {

         List<InterfaceStructureToken> structs = new LinkedList<>();
         if (subMessage.getId() == 0) {
            structs.add(messageHeaderStructures.get(subMessage.getName()));
         } else {
            structs.addAll(
               structures.stream().filter(a -> a.getArtifactReadable() != null && a.getArtifactReadable().areRelated(
                  CoreRelationTypes.InterfaceSubMessageContent_SubMessage, subMessage.getArtifactReadable())).collect(
                     Collectors.toList()));
         }
         for (InterfaceStructureToken struct : structs) {

            int byteLocation = 0;
            writer.startSheet(structureLinks.get(struct.getName()), columnCount);

            StructureInfo info = struct.getId() == 0 ? headersList.get(struct.getName()) : structuresList.get(
               ArtifactId.valueOf(struct.getId()));

            //print structure row info
            printFirstRowInStructureSheet(writer, struct, structureLinks, info);
            writer.writeEmptyCell();
            writer.endRow();

            writer.writeHeaderRow(headings);

            if (struct.getId() == 0) {
               for (InterfaceStructureElementToken element : struct.getElements()) {
                  PlatformTypeToken platformType = element.getPlatformType();
                  Integer byteSize = (int) element.getElementSizeInBytes();
                  printHeaderStructureRow(writer, view, element, byteLocation, byteSize, platformType);
                  byteLocation = byteLocation + byteSize;
               }
            } else {
               for (ArtifactReadable element : info.elements) {
                  ArtifactReadable platformType = element.getRelated(
                     CoreRelationTypes.InterfaceElementPlatformType_PlatformType).getAtMostOneOrDefault(
                        ArtifactReadable.SENTINEL);

                  Integer byteSize = Integer.valueOf(
                     platformType.getSoleAttributeAsString(CoreAttributeTypes.InterfacePlatformTypeBitSize, "0")) / 8;
                  printDataElementRow(writer, view, element, byteLocation, byteSize, platformType);
                  if (element.isOfType(CoreArtifactTypes.InterfaceDataElementArray)) {
                     int startIndex = element.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexStart, 0);
                     int endIndex = element.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexEnd, 0);

                     byteSize = byteSize * (endIndex - startIndex + 1);
                  }
                  byteLocation = byteLocation + byteSize;
               }
            }

            writer.endSheet();
         }
      }
   }

   private void printHeaderStructureRow(ExcelXmlWriter writer, ArtifactId view, InterfaceStructureElementToken element, Integer byteLocation, Integer byteSize, PlatformTypeToken platformType) throws IOException {
      Integer beginWord = Math.floorDiv(byteLocation, 4);
      Integer beginByte = Math.floorMod(byteLocation, 4);
      Integer endWord = Math.floorDiv(byteLocation + byteSize - 1, 4);
      Integer endByte = Math.floorMod(byteLocation + byteSize - 1, 4);
      String elementName = element.getName();
      String dataType = element.getLogicalType();
      dataType = dataType.replace("unsigned long", "uLong").replace("unsigned short", "uShort").replace("short",
         "sShort").replace("unsigned integer", "uInteger").replace("integer", "sInteger");
      String units =
         (platformType == null || platformType.getInterfacePlatformTypeUnits() == Strings.EMPTY_STRING) ? "n/a" : platformType.getInterfacePlatformTypeUnits();
      String validRange =
         (platformType == null || platformType.getInterfacePlatformTypeValidRangeDescription() == Strings.EMPTY_STRING) ? "n/a" : platformType.getInterfacePlatformTypeValidRangeDescription();

      String alterable = element.getInterfaceElementAlterable() ? "Yes" : "No";
      String description = element.getDescription() == Strings.EMPTY_STRING ? "n/a" : element.getDescription();
      String notes = element.getNotes();

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
      writer.writeCell("n/a");
      writer.writeCell(notes);
      writer.endRow();
   }

   private void printDataElementRow(ExcelXmlWriter writer, ArtifactId view, ArtifactReadable artifact, Integer byteLocation, Integer byteSize, ArtifactReadable platformType) throws IOException {
      Integer beginWord = Math.floorDiv(byteLocation, 4);
      Integer beginByte = Math.floorMod(byteLocation, 4);
      Integer endWord = Math.floorDiv(byteLocation + byteSize - 1, 4);
      Integer endByte = Math.floorMod(byteLocation + byteSize - 1, 4);
      String enumLiterals = artifact.getSoleAttributeAsString(CoreAttributeTypes.InterfacePlatformTypeEnumLiteral, "");
      String elementName = artifact.getSoleAttributeValue(CoreAttributeTypes.Name);
      String dataType = platformType.getSoleAttributeAsString(CoreAttributeTypes.InterfaceLogicalType, "n/a");
      dataType = dataType.replace("unsigned long", "uLong").replace("unsigned short", "uShort").replace("short",
         "sShort").replace("unsigned integer", "uInteger").replace("integer", "sInteger");
      String units = platformType.getSoleAttributeAsString(CoreAttributeTypes.InterfacePlatformTypeUnits, "n/a");
      String validRange;
      if (platformType.getExistingAttributeTypes().contains(CoreAttributeTypes.InterfacePlatformTypeMinval)) {
         validRange = platformType.getSoleAttributeAsString(CoreAttributeTypes.InterfacePlatformTypeMinval,
            "n/a") + "-" + platformType.getSoleAttributeAsString(CoreAttributeTypes.InterfacePlatformTypeMaxval, "n/a");
      } else {
         if (dataType.equals("enumeration")) {
            validRange = "see enumerated literals";
         } else if (dataType.equals("boolean")) {
            validRange = "0 to 1";
         } else {
            validRange = platformType.getSoleAttributeAsString(
               CoreAttributeTypes.InterfacePlatformTypeValidRangeDescription, " ");
         }
      }
      String alterable = artifact.getSoleAttributeAsString(CoreAttributeTypes.InterfaceElementAlterable, "No");

      if (alterable.equalsIgnoreCase("true")) {
         alterable = "Yes";
      } else {
         alterable = "No";
      }
      String description = artifact.getSoleAttributeAsString(CoreAttributeTypes.Description, "n/a");
      String notes = artifact.getSoleAttributeAsString(CoreAttributeTypes.Notes, " ");

      if (platformType.getExistingRelationTypes().contains(CoreRelationTypes.InterfacePlatformTypeEnumeration)) {
         enumLiterals = "";
         ArtifactReadable enumDef = platformType.getRelated(
            CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet).getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL);
         for (ArtifactReadable enumState : enumDef.getRelated(
            CoreRelationTypes.InterfaceEnumeration_EnumerationState).getList()) {
            enumLiterals = enumLiterals + enumState.getSoleAttributeAsString(CoreAttributeTypes.InterfaceEnumOrdinal,
               "0") + " = " + enumState.getSoleAttributeAsString(CoreAttributeTypes.Name, "") + "; ";
         }
      } else {
         enumLiterals = platformType.getSoleAttributeAsString(CoreAttributeTypes.InterfacePlatformTypeEnumLiteral, " ");
         if (enumLiterals.equals(" ")) {
            enumLiterals = platformType.getSoleAttributeAsString(CoreAttributeTypes.Description, " ");
         }
      }

      if (artifact.isOfType(CoreArtifactTypes.InterfaceDataElementArray)) {
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
            writer.writeCell(elementName + " " + Integer.toString(i));
            writer.writeCell(units);
            writer.writeCell(validRange);
            writer.writeCell(alterable);
            writer.writeCell(description);
            writer.writeCell(enumLiterals);
            writer.writeCell(notes);
            byteLocation = byteLocation + byteSize;
            beginWord = Math.floorDiv(byteLocation, 4);
            beginByte = Math.floorMod(byteLocation, 4);
            endWord = Math.floorDiv(byteLocation + byteSize - 1, 4);
            endByte = Math.floorMod(byteLocation + byteSize - 1, 4);
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

   private void createMessageSubMessageSummary(ExcelXmlWriter writer, ArtifactReadable connection, ArtifactReadable primaryNode, ArtifactReadable secondaryNode, List<ArtifactReadable> messages, List<ArtifactReadable> subMessages) throws IOException {
      int columnCount = 10;
      List<ArtifactReadable> connectionMessages = messages.stream().filter(
         e -> (e.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageType)).equals("Operational")).collect(
            Collectors.toList());
      List<ArtifactReadable> primaryList = connectionMessages.stream().filter(
         e -> primaryNode.equals(e.getRelated(CoreRelationTypes.InterfaceMessageSendingNode_Node).getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL))).collect(Collectors.toList());

      List<ArtifactReadable> secondaryList = connectionMessages.stream().filter(e -> secondaryNode.equals(
         e.getRelated(CoreRelationTypes.InterfaceMessageSendingNode_Node).getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL))).collect(Collectors.toList());
      writer.startSheet("Message and SubMessage Summary", columnCount);
      writer.writeCell(new CellData(primaryNode.getName() + " Initiated Message", Strings.EMPTY_STRING, "4",
         Strings.EMPTY_STRING, "OseeBoldStyle"));
      writer.writeCell(new CellData(secondaryNode.getName() + " Initiated Message", Strings.EMPTY_STRING, "4",
         Strings.EMPTY_STRING, "OseeBoldStyle"));
      writer.endRow();
      writer.writeCell(
         new CellData("No.", Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, "OseeBoldStyle"));
      writer.writeCell(
         new CellData("Rate", Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, "OseeBoldStyle"));
      writer.writeCell(
         new CellData("Read/Write", Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, "OseeBoldStyle"));
      writer.writeCell(new CellData("Content", Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING, "OseeBoldStyle"));
      writer.writeCell(
         new CellData("No.", Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, "OseeBoldStyle"));
      writer.writeCell(
         new CellData("Rate", Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, "OseeBoldStyle"));
      writer.writeCell(
         new CellData("Read/Write", Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, "OseeBoldStyle"));
      writer.writeCell(new CellData("Content", Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING, "OseeBoldStyle"));
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
            final ArtifactReadable pMsg = primaryMessage;
            primarySubMessages = subMessages.stream().filter(
               a -> a.areRelated(CoreRelationTypes.InterfaceMessageSubMessageContent_Message, pMsg)).collect(
                  Collectors.toList());
         } catch (IndexOutOfBoundsException e) {
            //do nothing leave primaryMessage and primarySubMessages empty
         }
         try {
            secondaryMessage = secondaryList.get(i);
            final ArtifactReadable sMsg = secondaryMessage;
            secondarySubMessages = subMessages.stream().filter(
               a -> a.areRelated(CoreRelationTypes.InterfaceMessageSubMessageContent_Message, sMsg)).collect(
                  Collectors.toList());
         } catch (IndexOutOfBoundsException e) {
            //do nothing leave secondaryMessage and secondarySubMessages empty
         }
         int subMessagesCount = Math.max(primarySubMessages.size(), secondarySubMessages.size());
         String mergeDown = Integer.toString(subMessagesCount + 1);
         if (primaryMessage.isValid()) {
            writer.writeCell(
               new CellData(primaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageNumber),
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(new CellData(
               primaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate, "Aperiodic"),
               Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(
               new CellData(primaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageWriteAccess),
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(new CellData(primaryMessage.getName(), Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING,
               "OseeBoldStyle"));
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
            writer.writeCell(new CellData(
               secondaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate, "Aperiodic"),
               Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(
               new CellData(secondaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageWriteAccess),
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(new CellData(secondaryMessage.getName(), Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING,
               "OseeBoldStyle"));
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
               writer.writeCell(new CellData("Submessage " + Integer.toString(j + 1) + ":", Strings.EMPTY_STRING,
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING), 3);
               writer.writeCell(primarySubMessage.getName());
            }
            if (secondarySubMessage.isValid()) {
               writer.writeCell(new CellData("Submessage " + Integer.toString(j + 1) + ":", Strings.EMPTY_STRING,
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING), 8);
               writer.writeCell(secondarySubMessage.getName());
            }
            writer.endRow();
         }
      }
      writer.endSheet();
   }

   private class StructureInfo {
      final String name;
      final String category;
      final String rate;
      final String minSim;
      final String maxSim;
      final String numAttributes;
      final String sizeInBytes;
      final String initiator;
      final String msgNum;
      final String subMsgNum;
      final String taskfile;
      final String description;
      final List<ArtifactReadable> elements;
      public StructureInfo(String name, String cat, String msgRateText, String minSim, String maxSim, int elementCount, Integer sum, String sendingNode, String msgNumber, String subMsgNumber, String taskfile, String desc, List<ArtifactReadable> elements) {
         this.name = name;
         this.category = cat;
         this.rate = msgRateText;
         this.minSim = minSim;
         this.maxSim = maxSim;
         this.numAttributes = Integer.toString(elementCount);
         this.sizeInBytes = Integer.toString(sum);
         this.initiator = sendingNode;
         this.msgNum = msgNumber;
         this.subMsgNum = subMsgNumber;
         this.taskfile = taskfile;
         this.description = desc;
         this.elements = elements;
      }

   }
}
