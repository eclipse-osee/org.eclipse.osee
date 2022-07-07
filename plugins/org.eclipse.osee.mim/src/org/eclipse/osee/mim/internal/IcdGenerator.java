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
import org.eclipse.osee.mim.InterfaceDifferenceReportApi;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceNode;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.mim.types.MimDifferenceItem;
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
   private final InterfaceDifferenceReportApi interfaceDifferenceReportApi;

   private final Map<ArtifactId, StructureInfo> structuresList;
   private final Map<String, StructureInfo> headersList;
   private final Map<ArtifactId, InterfaceSubMessageToken> messageHeaders;
   private final Map<String, InterfaceStructureToken> messageHeaderStructures;
   private Map<ArtifactId, MimDifferenceItem> diffs;

   public IcdGenerator(MimApi mimApi) {
      this.orcsApi = mimApi.getOrcsApi();
      this.atsApi = mimApi.getAtsApi();
      this.structuresList = new HashMap<>();
      this.headersList = new HashMap<>();
      this.messageHeaders = new HashMap<>();
      this.messageHeaderStructures = new HashMap<>();
      this.diffs = new HashMap<>();

      this.interfaceMessageApi = mimApi.getInterfaceMessageApi();
      this.interfaceStructureApi = mimApi.getInterfaceStructureApi();
      this.interfaceDifferenceReportApi = mimApi.getInterfaceDifferenceReportApi();
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
      SortedMap<InterfaceStructureToken, String> structureLinks = new TreeMap<InterfaceStructureToken, String>();
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
         structureLinks.put(structure, sheetName);
      }

      BranchId parentBranch =
         orcsApi.getQueryFactory().branchQuery().andId(branch).getResults().getList().get(0).getParentBranch();
      diffs = interfaceDifferenceReportApi.getDifferences(branch, parentBranch);

      ExcelXmlWriter writer = new ExcelXmlWriter(providedWriter);

      createStructureInfo(branch, view, primaryNode, secondaryNode, messages, structureLinks);

      // Write sheets
      createChangeSummary(writer, branch);
      createMessageSubMessageSummary(writer, conn, primaryNode, secondaryNode, messages, subMessages);
      createStructureNamesSheet(writer, structureLinks);
      createStructureSummarySheet(writer, branch, view, primaryNode, secondaryNode, messages, structureLinks);
      createStructureSheets(writer, view, subMessagesWithHeaders, structures, structureLinks);

      writer.endWorkbook();
   }

   private void createStructureInfo(BranchId branch, ArtifactId view, ArtifactReadable primaryNode, ArtifactReadable secondaryNode, List<ArtifactReadable> messages, Map<InterfaceStructureToken, String> structureLinks) {
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

               MimDifferenceItem msgDiffItem = diffs.get(ArtifactId.valueOf(message.getId()));
               MimDifferenceItem diffItem = diffs.get(ArtifactId.valueOf(struct.getId()));
               Integer sizeInBytes = 0;
               int elementCount = 0;
               boolean structureSizeChanged = false;
               boolean structureChanged = false;
               int byteChangeIndex = elements.size(); // Used to track which element first changes the structure size in order to display diff coloring for every element after.

               for (int i = 0; i < elements.size(); i++) {
                  InterfaceStructureElementToken element = elements.get(i);
                  elementCount = struct.isAutogenerated() ? elementCount + 1 : elementCount + element.getArrayLength();
                  sizeInBytes = sizeInBytes + (int) element.getElementSizeInBytes();

                  MimDifferenceItem elementDiffItem = diffs.get(ArtifactId.valueOf(element.getId()));
                  MimDifferenceItem pTypeDiffItem = diffs.get(ArtifactId.valueOf(element.getPlatformTypeId()));
                  structureSizeChanged =
                     structureSizeChanged || (pTypeDiffItem != null && pTypeDiffItem.getDiffs().containsKey(
                        CoreAttributeTypes.InterfacePlatformTypeBitSize.getId())) || (elementDiffItem != null && (elementDiffItem.isAdded() || !elementDiffItem.getRelationChanges().isEmpty()));
                  boolean elementAdded = diffItem != null && diffItem.getRelationChanges().containsKey(
                     ArtifactId.valueOf(element.getId())) && diffItem.getRelationChanges().get(
                        ArtifactId.valueOf(element.getId())).isAdded();
                  if (structureSizeChanged || elementAdded) {
                     byteChangeIndex = Math.min(i, byteChangeIndex);
                  }

                  structureChanged = structureChanged || elementDiffItem != null || pTypeDiffItem != null;
               }

               String minSim = struct.getInterfaceMinSimultaneity();
               String maxSim = struct.getInterfaceMaxSimultaneity();

               String minBps = "Classified";
               String maxBps = "Classified";
               if (msgRate > 0) {
                  try {
                     minBps = Integer.toString(Integer.parseInt(minSim) * sizeInBytes * msgRate);
                  } catch (NumberFormatException nfe) {
                     minBps = "Classified";
                  }
                  try {
                     maxBps = Integer.toString(Integer.parseInt(maxSim) * sizeInBytes * msgRate);
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
               String categoryFull = catEnum.getId() + " " + catEnum.getName();

               boolean txRateChanged = msgDiffItem != null && msgDiffItem.getDiffs().containsKey(
                  CoreAttributeTypes.InterfaceMessageRate.getId());
               boolean msgNumChanged = msgDiffItem != null && msgDiffItem.getDiffs().containsKey(
                  CoreAttributeTypes.InterfaceMessageNumber.getId());
               boolean numElementsChanged = diffItem != null && !diffItem.getRelationChanges().isEmpty();
               boolean sizeInBytesChanged = numElementsChanged || structureSizeChanged;
               structureChanged = structureChanged || diffItem != null || txRateChanged || msgNumChanged;

               StructureInfo structureInfo = new StructureInfo(struct.getName(), cat, categoryFull, msgRateText, minSim,
                  maxSim, minBps, maxBps, elementCount, sizeInBytes, sendingNode.getName(), msgNumber, subMsgNumber,
                  taskFileType, desc, message, subMessage.getArtifactReadable(), elementArts, structureChanged,
                  txRateChanged, numElementsChanged, sizeInBytesChanged, byteChangeIndex);
               if (struct.getId() == 0) {
                  headersList.put(struct.getName(), structureInfo);
               } else {
                  structuresList.put(ArtifactId.valueOf(struct.getId()), structureInfo);
               }
            }
         }
      }
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

   private void createStructureNamesSheet(ExcelXmlWriter writer, SortedMap<InterfaceStructureToken, String> structures) throws IOException {

      List<String> headingsList = new ArrayList<String>();
      headingsList.add("Structure Name");
      headingsList.add("Structure Name");
      headingsList.add("Structure Name");
      Object[] headings = headingsList.toArray();
      int columnCount = 3;
      writer.startSheet("Structures Names", columnCount);
      writer.writeHeaderRow(headings);
      Iterator<InterfaceStructureToken> iterator = structures.keySet().iterator();
      int size = structures.size();
      int firstCol = Math.floorDiv(size, 3);
      int secCol = firstCol * 2;
      int count = 0;
      SortedMap<InterfaceStructureToken, String> col1 = new TreeMap<InterfaceStructureToken, String>();
      SortedMap<InterfaceStructureToken, String> col2 = new TreeMap<InterfaceStructureToken, String>();
      SortedMap<InterfaceStructureToken, String> col3 = new TreeMap<InterfaceStructureToken, String>();
      while (iterator.hasNext()) {
         InterfaceStructureToken structure = iterator.next();
         String sLink = structures.get(structure);
         if (count <= firstCol) {
            col1.put(structure, sLink);
         } else if (count <= secCol) {
            col2.put(structure, sLink);
         } else {
            col3.put(structure, sLink);
         }
         count++;
      }
      Iterator<InterfaceStructureToken> col1Iterator = col1.keySet().iterator();
      Iterator<InterfaceStructureToken> col2Iterator = col2.keySet().iterator();
      Iterator<InterfaceStructureToken> col3Iterator = col3.keySet().iterator();
      while (col1Iterator.hasNext()) {
         InterfaceStructureToken structure = col1Iterator.next();
         CellData name = new CellData();
         name.setText(structure.getName());
         name.setHyperlink(col1.get(structure));
         writeCell(writer, name, getStructureNameStyle(structure), 0);
         if (col2Iterator.hasNext()) {
            structure = col2Iterator.next();
            name = new CellData();
            name.setText(structure.getName());
            name.setHyperlink(col2.get(structure));
            writeCell(writer, name, getStructureNameStyle(structure), 1);
         }
         if (col3Iterator.hasNext()) {
            structure = col3Iterator.next();
            name = new CellData();
            name.setText(structure.getName());
            name.setHyperlink(col3.get(structure));
            writeCell(writer, name, getStructureNameStyle(structure), 2);
         }
         writer.endRow();
      }
      writer.endSheet();
   }

   private STYLE getStructureNameStyle(InterfaceStructureToken struct) {
      StructureInfo info = getStructureInfo(struct);
      MimDifferenceItem diffItem = diffs.get(ArtifactId.valueOf(struct.getId()));
      if (diffItem != null && diffItem.isAdded()) {
         return STYLE.HYPERLINK_GOOD_BG;
      } else if (info.structureChanged) {
         return STYLE.HYPERLINK_NEUTRAL_BG;
      } else {
         return STYLE.NONE;
      }
   }

   private void createStructureSummarySheet(ExcelXmlWriter writer, BranchId branch, ArtifactId view, ArtifactReadable primaryNode, ArtifactReadable secondaryNode, List<ArtifactReadable> messages, SortedMap<InterfaceStructureToken, String> structureLinks) throws IOException {
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
               String sheetName = structureLinks.get(struct);
               StructureInfo structureInfo = getStructureInfo(struct);

               totalMinSim += stringToInt(structureInfo.minSim);
               totalMaxSim += stringToInt(structureInfo.maxSim);
               totalNumAttrs += structureInfo.numAttributes;
               totalMinBps += stringToInt(structureInfo.minBps);
               totalMaxBps += stringToInt(structureInfo.maxBps);
               maxAttrs = Math.max(maxAttrs, structureInfo.numAttributes);
               totalStructs++;

               ArtifactReadable structReadable = struct.getArtifactReadable();
               writeCell(writer, structureInfo.categoryFull, structReadable, 0,
                  CoreAttributeTypes.InterfaceStructureCategory.getId());
               writeCell(writer, new CellData(struct.getName(), sheetName, Strings.EMPTY_STRING, Strings.EMPTY_STRING),
                  structReadable, 1, CoreAttributeTypes.Name.getId());
               writeCell(writer, structureInfo.txRate, struct.getArtifactReadable(), message, 2,
                  CoreAttributeTypes.InterfaceMessageRate.getId());
               writeCell(writer, structureInfo.minSim, structReadable, 3,
                  CoreAttributeTypes.InterfaceMinSimultaneity.getId());
               writeCell(writer, structureInfo.maxSim, structReadable, 4,
                  CoreAttributeTypes.InterfaceMaxSimultaneity.getId());
               writeCell(writer, structureInfo.numAttributes,
                  getStyle(struct.getArtifactReadable(), structureInfo.numElementsChanged), 5);
               writeCell(writer, structureInfo.sizeInBytes,
                  getStyle(struct.getArtifactReadable(), structureInfo.structureSizeChanged), 6);
               writeCell(writer, structureInfo.minBps, getStyle(struct.getArtifactReadable(),
                  structureInfo.txRateChanged || structureInfo.structureSizeChanged), 7);
               writeCell(writer, structureInfo.maxBps, getStyle(struct.getArtifactReadable(),
                  structureInfo.txRateChanged || structureInfo.structureSizeChanged), 8);
               writeCell(writer, structureInfo.initiator, getStyle(struct.getArtifactReadable(), false), 9);
               writeCell(writer, structureInfo.msgNum, struct.getArtifactReadable(), message, 10,
                  CoreAttributeTypes.InterfaceMessageNumber.getId());
               writeCell(writer, structureInfo.subMsgNum, subMessage.getArtifactReadable(), 11,
                  CoreAttributeTypes.InterfaceSubMessageNumber.getId());
               writeCell(writer, structureInfo.taskfile, structReadable, 12,
                  CoreAttributeTypes.InterfaceTaskFileType.getId());
               writeCell(writer, structureInfo.description, structReadable, 13, CoreAttributeTypes.Description.getId());
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

   private StructureInfo getStructureInfo(InterfaceStructureToken struct) {
      return struct.getId() == 0 ? headersList.get(struct.getName()) : structuresList.get(
         ArtifactId.valueOf(struct.getId()));
   }

   private STYLE getStyle(ArtifactReadable artifact, boolean changed) {
      MimDifferenceItem diffItem = artifact == null ? null : diffs.get(ArtifactId.valueOf(artifact.getId()));
      STYLE style = STYLE.NONE;
      if (diffItem != null) {
         style = diffItem.isAdded() ? STYLE.GOOD_BG : changed ? STYLE.NEUTRAL_BG : STYLE.NONE;
      }
      return style;
   }

   private int stringToInt(String str) {
      try {
         return Integer.parseInt(str);
      } catch (NumberFormatException ex) {
         return 0;
      }
   }

   private void printFirstRowInStructureSheet(ExcelXmlWriter writer, InterfaceStructureToken structure, StructureInfo info) throws IOException {
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
      writeCell(writer, "Functional", getStyle(structure.getArtifactReadable(), false), 0);
      writeCell(writer, info.name, structure.getArtifactReadable(), 1, CoreAttributeTypes.Name.getId());
      writeCell(writer, structure.getId() == 0 ? info.name : structure.getArtifactReadable().getSoleAttributeAsString(
         CoreAttributeTypes.GeneralStringData, ""), getStyle(structure.getArtifactReadable(), false), 2);
      // There are special cases in the XML writer for dates and adding additional styles causes the workbook to not load correctly.
      writer.writeCell(
         structure.getId() == 0 ? Strings.EMPTY_STRING : orcsApi.getQueryFactory().transactionQuery().andTxId(
            structure.getArtifactReadable().getTransaction()).getResults().getAtMostOneOrDefault(
               TransactionReadable.SENTINEL).getDate());
      writeCell(writer, info.category, structure.getArtifactReadable(), 4,
         CoreAttributeTypes.InterfaceStructureCategory.getId());
      writeCell(writer, info.txRate, structure.getArtifactReadable(), info.message, 5,
         CoreAttributeTypes.InterfaceMessageRate.getId());
      writeCell(writer, info.minSim, structure.getArtifactReadable(), 6,
         CoreAttributeTypes.InterfaceMinSimultaneity.getId());
      writeCell(writer, info.maxSim, structure.getArtifactReadable(), 7,
         CoreAttributeTypes.InterfaceMaxSimultaneity.getId());
      writeCell(writer, info.numAttributes, getStyle(structure.getArtifactReadable(), info.numElementsChanged), 8);
      writeCell(writer, info.sizeInBytes, getStyle(structure.getArtifactReadable(), info.structureSizeChanged), 9);
      writeCell(writer, info.initiator, getStyle(structure.getArtifactReadable(), false), 10);
      writeCell(writer, info.msgNum, structure.getArtifactReadable(), info.message, 11,
         CoreAttributeTypes.InterfaceMessageNumber.getId());
      writeCell(writer, info.subMsgNum, info.submessage, 12, CoreAttributeTypes.InterfaceSubMessageNumber.getId());
      writeCell(writer, info.taskfile, structure.getArtifactReadable(), 13,
         CoreAttributeTypes.InterfaceTaskFileType.getId());
      writeCell(writer, info.description, structure.getArtifactReadable(), 14, CoreAttributeTypes.Description.getId());
      writer.endRow();
   }

   private void createStructureSheets(ExcelXmlWriter writer, ArtifactId view, List<InterfaceSubMessageToken> subMessages, List<InterfaceStructureToken> structures, SortedMap<InterfaceStructureToken, String> structureLinks) throws IOException {
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
            writer.startSheet(structureLinks.get(struct), columnCount);

            StructureInfo info = struct.getId() == 0 ? headersList.get(struct.getName()) : structuresList.get(
               ArtifactId.valueOf(struct.getId()));

            //print structure row info
            printFirstRowInStructureSheet(writer, struct, info);
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
               for (int i = 0; i < info.elements.size(); i++) {
                  ArtifactReadable element = info.elements.get(i);
                  ArtifactReadable platformType = element.getRelated(
                     CoreRelationTypes.InterfaceElementPlatformType_PlatformType).getAtMostOneOrDefault(
                        ArtifactReadable.SENTINEL);

                  Integer byteSize = Integer.valueOf(
                     platformType.getSoleAttributeAsString(CoreAttributeTypes.InterfacePlatformTypeBitSize, "0")) / 8;
                  printDataElementRow(writer, view, struct.getArtifactReadable(), element, byteLocation, byteSize,
                     platformType, i >= info.byteChangeIndex);
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

   private void printDataElementRow(ExcelXmlWriter writer, ArtifactId view, ArtifactReadable structure, ArtifactReadable element, Integer byteLocation, Integer byteSize, ArtifactReadable platformType, boolean bytesChanged) throws IOException {
      Integer beginWord = Math.floorDiv(byteLocation, 4);
      Integer beginByte = Math.floorMod(byteLocation, 4);
      Integer endWord = Math.floorDiv(byteLocation + byteSize - 1, 4);
      Integer endByte = Math.floorMod(byteLocation + byteSize - 1, 4);
      String enumLiterals = element.getSoleAttributeAsString(CoreAttributeTypes.InterfacePlatformTypeEnumLiteral, "");
      String elementName = element.getSoleAttributeValue(CoreAttributeTypes.Name);
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
      String alterable = element.getSoleAttributeAsString(CoreAttributeTypes.InterfaceElementAlterable, "No");

      if (alterable.equalsIgnoreCase("true")) {
         alterable = "Yes";
      } else {
         alterable = "No";
      }
      String description = element.getSoleAttributeAsString(CoreAttributeTypes.Description, "n/a");
      String notes = element.getSoleAttributeAsString(CoreAttributeTypes.Notes, " ");
      boolean enumChanged = false;

      if (platformType.getExistingRelationTypes().contains(CoreRelationTypes.InterfacePlatformTypeEnumeration)) {
         enumLiterals = "";
         ArtifactReadable enumDef = platformType.getRelated(
            CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet).getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL);
         enumChanged = enumChanged || diffs.containsKey(ArtifactId.valueOf(enumDef.getId()));
         for (ArtifactReadable enumState : enumDef.getRelated(
            CoreRelationTypes.InterfaceEnumeration_EnumerationState).getList()) {
            enumLiterals = enumLiterals + enumState.getSoleAttributeAsString(CoreAttributeTypes.InterfaceEnumOrdinal,
               "0") + " = " + enumState.getSoleAttributeAsString(CoreAttributeTypes.Name, "") + "; ";
            enumChanged = enumChanged || diffs.containsKey(ArtifactId.valueOf(enumState.getId()));
         }
      } else {
         enumLiterals = platformType.getSoleAttributeAsString(CoreAttributeTypes.InterfacePlatformTypeEnumLiteral, " ");
         if (enumLiterals.equals(" ")) {
            enumLiterals = platformType.getSoleAttributeAsString(CoreAttributeTypes.Description, " ");
         }
      }

      MimDifferenceItem structDiff = diffs.get(ArtifactId.valueOf(structure.getId()));
      MimDifferenceItem elementDiff = diffs.get(ArtifactId.valueOf(element.getId()));
      boolean elementAdded = structDiff != null && structDiff.getRelationChanges().containsKey(ArtifactId.valueOf(
         element.getId())) && structDiff.getRelationChanges().get(ArtifactId.valueOf(element.getId())).isAdded();
      boolean platformTypeChanged = elementDiff != null && !elementDiff.getRelationChanges().isEmpty();
      STYLE pTypeStyle = elementAdded || getStyle(element, false).equals(
         STYLE.GOOD_BG) ? STYLE.GOOD_BG : platformTypeChanged ? STYLE.NEUTRAL_BG : STYLE.NONE;

      STYLE enumStyle = elementAdded || getStyle(element, false).equals(
         STYLE.GOOD_BG) ? STYLE.GOOD_BG : enumChanged || platformTypeChanged ? STYLE.NEUTRAL_BG : STYLE.NONE;

      STYLE byteStyle = elementAdded || getStyle(element, false).equals(
         STYLE.GOOD_BG) ? STYLE.GOOD_BG : bytesChanged ? STYLE.NEUTRAL_BG : STYLE.NONE;

      STYLE elementStyle = elementAdded ? STYLE.GOOD_BG : STYLE.NONE;

      if (element.isOfType(CoreArtifactTypes.InterfaceDataElementArray)) {
         int startIndex = element.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexStart, 0);
         int endIndex = element.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexEnd, 0);
         for (int i = startIndex; i < endIndex + 1; i++) {
            writeCell(writer, beginWord, byteStyle, 0);
            writeCell(writer, beginByte, byteStyle, 1);
            writeCell(writer, byteSize, byteStyle, 2);
            writeCell(writer, endWord, byteStyle, 3);
            writeCell(writer, endByte, byteStyle, 4);
            writeCell(writer, dataType, pTypeStyle, 5);
            writeCell(writer, elementName + " " + Integer.toString(i), element, 6, CoreAttributeTypes.Name.getId(),
               elementStyle);
            writeCell(writer, units, pTypeStyle, 7);
            writeCell(writer, validRange, pTypeStyle, 8);
            writeCell(writer, alterable, element, 9, CoreAttributeTypes.InterfaceElementAlterable.getId(),
               elementStyle);
            writeCell(writer, description, element, 10, CoreAttributeTypes.Description.getId(), elementStyle);
            writeCell(writer, enumLiterals, enumStyle, 11);
            writeCell(writer, notes, element, 12, CoreAttributeTypes.Notes.getId(), elementStyle);
            writer.endRow();

            byteLocation = byteLocation + byteSize;
            beginWord = Math.floorDiv(byteLocation, 4);
            beginByte = Math.floorMod(byteLocation, 4);
            endWord = Math.floorDiv(byteLocation + byteSize - 1, 4);
            endByte = Math.floorMod(byteLocation + byteSize - 1, 4);
         }

      } else {
         writeCell(writer, beginWord, byteStyle, 0);
         writeCell(writer, beginByte, byteStyle, 1);
         writeCell(writer, byteSize, byteStyle, 2);
         writeCell(writer, endWord, byteStyle, 3);
         writeCell(writer, endByte, byteStyle, 4);
         writeCell(writer, dataType, pTypeStyle, 5);
         writeCell(writer, elementName, element, 6, CoreAttributeTypes.Name.getId(), elementStyle);
         writeCell(writer, units, pTypeStyle, 7);
         writeCell(writer, validRange, pTypeStyle, 8);
         writeCell(writer, alterable, element, 9, CoreAttributeTypes.InterfaceElementAlterable.getId(), elementStyle);
         writeCell(writer, description, element, 10, CoreAttributeTypes.Description.getId(), elementStyle);
         writeCell(writer, enumLiterals, enumStyle, 11);
         writeCell(writer, notes, element, 12, CoreAttributeTypes.Notes.getId(), elementStyle);
         writer.endRow();
      }

   }

   private void writeCell(ExcelXmlWriter writer, Object data, ArtifactReadable artifact, int cellIndex, Long attrId) throws IOException {
      writeCell(writer, data, artifact, artifact, cellIndex, attrId, STYLE.NONE);
   }

   private void writeCell(ExcelXmlWriter writer, Object data, ArtifactReadable artifact, int cellIndex, Long attrId, STYLE forceStyle) throws IOException {
      writeCell(writer, data, artifact, artifact, cellIndex, attrId, forceStyle);
   }

   private void writeCell(ExcelXmlWriter writer, Object data, ArtifactReadable rowArtifact, ArtifactReadable cellArtifact, int cellIndex, Long attrId) throws IOException {
      writeCell(writer, data, rowArtifact, cellArtifact, cellIndex, attrId, STYLE.NONE);
   }

   private void writeCell(ExcelXmlWriter writer, Object data, ArtifactReadable rowArtifact, ArtifactReadable cellArtifact, int cellIndex, Long attrId, STYLE forceStyle) throws IOException {
      MimDifferenceItem rowDiffItem = rowArtifact == null ? null : diffs.get(ArtifactId.valueOf(rowArtifact.getId()));
      MimDifferenceItem cellDiffItem =
         cellArtifact == null ? null : diffs.get(ArtifactId.valueOf(cellArtifact.getId()));
      MimDifferenceItem diffItem = rowDiffItem != null && rowDiffItem.isAdded() ? rowDiffItem : cellDiffItem;
      if (!forceStyle.equals(STYLE.NONE)) {
         if (data instanceof CellData) {
            ((CellData) data).setStyle(forceStyle.getText());
         } else {
            writer.setCellStyle(forceStyle, cellIndex);
         }
      } else if (diffItem != null) {
         STYLE style = diffItem.isAdded() ? STYLE.GOOD_BG : diffItem.getDiffs().keySet().contains(
            attrId) ? STYLE.NEUTRAL_BG : STYLE.NONE;
         if (data instanceof CellData && !style.equals(STYLE.NONE)) {
            // If there should be a background color on the cell, determine the correct style to use
            if (!((CellData) data).getHyperlink().isEmpty()) {
               style = style == STYLE.GOOD_BG ? STYLE.HYPERLINK_GOOD_BG : STYLE.HYPERLINK_NEUTRAL_BG;
            } else if (((CellData) data).getStyle() == STYLE.BOLD.getText()) {
               style = style == STYLE.GOOD_BG ? STYLE.BOLD_GOOD_BG : STYLE.BOLD_NEUTRAL_BG;
            }
            ((CellData) data).setStyle(style.getText());
         } else {
            writer.setCellStyle(style, cellIndex);
         }
      }
      writer.writeCell(data);
   }

   private void writeCell(ExcelXmlWriter writer, Object data, STYLE style, int cellIndex) throws IOException {
      if (!style.equals(STYLE.NONE)) {
         if (data instanceof CellData) {
            ((CellData) data).setStyle(style.getText());
         } else {
            writer.setCellStyle(style, cellIndex);
         }
      }
      writer.writeCell(data);
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
         Strings.EMPTY_STRING, STYLE.BOLD.getText()));
      writer.writeCell(new CellData(secondaryNode.getName() + " Initiated Message", Strings.EMPTY_STRING, "4",
         Strings.EMPTY_STRING, STYLE.BOLD.getText()));
      writer.endRow();
      writer.writeCell(
         new CellData("No.", Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, STYLE.BOLD.getText()));
      writer.writeCell(
         new CellData("Rate", Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, STYLE.BOLD.getText()));
      writer.writeCell(new CellData("Read/Write", Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING,
         STYLE.BOLD.getText()));
      writer.writeCell(new CellData("Content", Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING, STYLE.BOLD.getText()));
      writer.writeCell(
         new CellData("No.", Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, STYLE.BOLD.getText()));
      writer.writeCell(
         new CellData("Rate", Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, STYLE.BOLD.getText()));
      writer.writeCell(new CellData("Read/Write", Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING,
         STYLE.BOLD.getText()));
      writer.writeCell(new CellData("Content", Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING, STYLE.BOLD.getText()));
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
            writeCell(writer,
               new CellData(primaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageNumber),
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown),
               primaryMessage, 0, CoreAttributeTypes.InterfaceMessageNumber.getId());
            writeCell(writer,
               new CellData(
                  primaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate, "Aperiodic"),
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown),
               primaryMessage, 1, CoreAttributeTypes.InterfaceMessageRate.getId());
            writeCell(writer,
               new CellData(primaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageWriteAccess),
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown),
               primaryMessage, 2, CoreAttributeTypes.InterfaceMessageWriteAccess.getId());
            writeCell(writer, new CellData(primaryMessage.getName(), Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING,
               STYLE.BOLD.getText()), primaryMessage, 3, CoreAttributeTypes.Name.getId());
         } else {
            writer.writeCell(new CellData(Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(new CellData(Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(new CellData(Strings.EMPTY_STRING, Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown));
            writer.writeCell(new CellData("", Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING));
         }
         if (secondaryMessage.isValid()) {
            writeCell(writer,
               new CellData(secondaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageNumber),
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown),
               secondaryMessage, 4, CoreAttributeTypes.InterfaceMessageNumber.getId());
            writeCell(writer,
               new CellData(
                  secondaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate, "Aperiodic"),
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown),
               secondaryMessage, 5, CoreAttributeTypes.InterfaceMessageRate.getId());
            writeCell(writer,
               new CellData(secondaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageWriteAccess),
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING, mergeDown),
               secondaryMessage, 6, CoreAttributeTypes.InterfaceMessageWriteAccess.getId());
            writeCell(writer, new CellData(secondaryMessage.getName(), Strings.EMPTY_STRING, "1", Strings.EMPTY_STRING,
               STYLE.BOLD.getText()), secondaryMessage, 7, CoreAttributeTypes.Name.getId());
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
               writeCell(writer, primarySubMessage.getName(), primarySubMessage, 4, CoreAttributeTypes.Name.getId());
            }
            if (secondarySubMessage.isValid()) {
               writer.writeCell(new CellData("Submessage " + Integer.toString(j + 1) + ":", Strings.EMPTY_STRING,
                  Strings.EMPTY_STRING, Strings.EMPTY_STRING), 8);
               writeCell(writer, secondarySubMessage.getName(), secondarySubMessage, 9,
                  CoreAttributeTypes.Name.getId());
            }
            writer.endRow();
         }
      }
      writer.endSheet();
   }

   private class StructureInfo {
      final String name;
      final String category;
      final String categoryFull;
      final String txRate;
      final String minSim;
      final String maxSim;
      final String minBps;
      final String maxBps;
      final Integer numAttributes;
      final Integer sizeInBytes;
      final String initiator;
      final String msgNum;
      final String subMsgNum;
      final String taskfile;
      final String description;
      final ArtifactReadable message;
      final ArtifactReadable submessage;
      final List<ArtifactReadable> elements;
      final boolean structureChanged;
      final boolean txRateChanged;
      final boolean numElementsChanged;
      final boolean structureSizeChanged;
      final int byteChangeIndex;
      public StructureInfo(String name, String cat, String catFull, String msgRateText, String minSim, String maxSim, String minBps, String maxBps, Integer elementCount, Integer sizeInBytes, String sendingNode, String msgNumber, String subMsgNumber, String taskfile, String desc, ArtifactReadable message, ArtifactReadable submessage, List<ArtifactReadable> elements, boolean structureChanged, boolean txRateChanged, boolean numElementsChanged, boolean structureSizeChanged, int byteChangeIndex) {
         this.name = name;
         this.category = cat;
         this.categoryFull = catFull;
         this.txRate = msgRateText;
         this.minSim = minSim;
         this.maxSim = maxSim;
         this.minBps = minBps;
         this.maxBps = maxBps;
         this.numAttributes = elementCount;
         this.sizeInBytes = sizeInBytes;
         this.initiator = sendingNode;
         this.msgNum = msgNumber;
         this.subMsgNum = subMsgNumber;
         this.taskfile = taskfile;
         this.description = desc;
         this.message = message;
         this.submessage = submessage;
         this.elements = elements;
         this.structureChanged = structureChanged;
         this.txRateChanged = txRateChanged;
         this.numElementsChanged = numElementsChanged;
         this.structureSizeChanged = structureSizeChanged;
         this.byteChangeIndex = byteChangeIndex;
      }

   }
}
