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
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.token.InterfaceStructureCategoryAttribute.InterfaceStructureCategoryEnum;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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
   private final Map<String, CellStyle> cellStyles;

   public IcdGenerator(MimApi mimApi) {
      this.orcsApi = mimApi.getOrcsApi();
      this.atsApi = mimApi.getAtsApi();
      this.structuresList = new HashMap<>();
      this.headersList = new HashMap<>();
      this.messageHeaders = new HashMap<>();
      this.messageHeaderStructures = new HashMap<>();
      this.diffs = new HashMap<>();
      this.cellStyles = new HashMap<>();

      this.interfaceMessageApi = mimApi.getInterfaceMessageApi();
      this.interfaceStructureApi = mimApi.getInterfaceStructureApi();
      this.interfaceDifferenceReportApi = mimApi.getInterfaceDifferenceReportApi();
   }

   public void runOperation(OutputStream outputStream, BranchId branch, ArtifactId view, ArtifactId connectionId) throws IOException {

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

      createStructureInfo(branch, view, primaryNode, secondaryNode, messages, structureLinks);

      // Write sheets
      Workbook workbook = new HSSFWorkbook();
      createChangeSummary(workbook, branch);
      createMessageSubMessageSummary(workbook, conn, primaryNode, secondaryNode, messages, subMessages);
      createStructureNamesSheet(workbook, structureLinks);
      createStructureSummarySheet(workbook, branch, view, primaryNode, secondaryNode, messages, structureLinks);
      createStructureSheets(workbook, view, subMessagesWithHeaders, structures, structureLinks);

      workbook.write(outputStream);
      workbook.close();
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

   private void createChangeSummary(Workbook workbook, BranchId branch) {
      SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
      String[] headers = {"Team Workflow", "Date", "Change Description"};

      Sheet sheet = workbook.createSheet("Change Summary");
      sheet.setColumnWidth(0, 5000);
      sheet.setColumnWidth(1, 3000);
      sheet.setColumnWidth(2, 10000);
      createHeaderRow(workbook, sheet, 0, headers);

      int rowIndex = 1;
      for (TransactionReadable tx : orcsApi.getQueryFactory().transactionQuery().andBranch(
         branch).getResults().getList()) {
         if (tx.getCommitArt().getId() != -1) {
            IAtsWorkItem change = atsApi.getQueryService().getWorkItem(tx.getCommitArt().getIdString());
            if (change != null && change.getId() != -1) {
               Row row = sheet.createRow(rowIndex);
               rowIndex++;
               createStringCell(workbook, sheet, row, change.getAtsId(), 0);
               createStringCell(workbook, sheet, row, format.format(tx.getDate()), 1);
               createStringCell(workbook, sheet, row, change.getName(), 2);
            }
         }
      }
   }

   private Row createHeaderRow(Workbook workbook, Sheet sheet, int rowIndex, String[] headers) {
      Row headerRow = sheet.createRow(rowIndex);
      CellStyle headerStyle = workbook.createCellStyle();
      HSSFFont font = (HSSFFont) workbook.createFont();
      font.setBold(true);
      headerStyle.setFont(font);

      for (int i = 0; i < headers.length; i++) {
         Cell headerCell = headerRow.createCell(i);
         headerCell.setCellValue(headers[i]);
         headerCell.setCellStyle(headerStyle);
      }

      return headerRow;
   }

   private void createStringCell(Workbook workbook, Sheet sheet, Row row, String value, int cellIndex) {
      Cell cell = row.createCell(cellIndex);
      cell.setCellValue(value);
   }

   private void createStructureNamesSheet(Workbook wb, SortedMap<InterfaceStructureToken, String> structures) {
      Sheet sheet = wb.createSheet("Structure Names");
      String[] headers = {"Structure Name", "Structure Name", "Structure Name"};
      createHeaderRow(wb, sheet, 0, headers);

      Iterator<InterfaceStructureToken> iterator = structures.keySet().iterator();
      int size = structures.size();
      int firstCol = (int) Math.ceil(size / 3.0);
      int secCol = firstCol * 2;
      int count = 0;
      int rowNum = 1;
      SortedMap<InterfaceStructureToken, String> col1 = new TreeMap<InterfaceStructureToken, String>();
      SortedMap<InterfaceStructureToken, String> col2 = new TreeMap<InterfaceStructureToken, String>();
      SortedMap<InterfaceStructureToken, String> col3 = new TreeMap<InterfaceStructureToken, String>();
      while (iterator.hasNext()) {
         InterfaceStructureToken structure = iterator.next();
         String sLink = structures.get(structure);
         if (count < firstCol) {
            col1.put(structure, sLink);
         } else if (count < secCol) {
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
         Row row = sheet.createRow(rowNum);
         InterfaceStructureToken structure = col1Iterator.next();
         CellStyle style = getCellStyle(wb, CELLSTYLE.HYPERLINK, getStructureNameColor(structure));
         createCellWithHyperLink(wb, row, structure.getName(), 0, col1.get(structure), style);
         if (col2Iterator.hasNext()) {
            structure = col2Iterator.next();
            style = getCellStyle(wb, CELLSTYLE.HYPERLINK, getStructureNameColor(structure));
            createCellWithHyperLink(wb, row, structure.getName(), 1, col2.get(structure), style);
         }
         if (col3Iterator.hasNext()) {
            structure = col3Iterator.next();
            style = getCellStyle(wb, CELLSTYLE.HYPERLINK, getStructureNameColor(structure));
            createCellWithHyperLink(wb, row, structure.getName(), 2, col3.get(structure), style);
         }
         rowNum++;
      }

      for (int i = 0; i < headers.length; i++) {
         sheet.autoSizeColumn(i);
      }
   }

   private CELLSTYLE getStructureNameColor(InterfaceStructureToken struct) {
      StructureInfo info = getStructureInfo(struct);
      MimDifferenceItem diffItem = diffs.get(ArtifactId.valueOf(struct.getId()));
      if (diffItem != null && diffItem.isAdded()) {
         return CELLSTYLE.GREEN;
      } else if (info.structureChanged) {
         return CELLSTYLE.YELLOW;
      } else {
         return CELLSTYLE.NONE;
      }
   }

   private void createStructureSummarySheet(Workbook wb, BranchId branch, ArtifactId view, ArtifactReadable primaryNode, ArtifactReadable secondaryNode, List<ArtifactReadable> messages, SortedMap<InterfaceStructureToken, String> structureLinks) {
      int totalMinSim = 0;
      int totalMaxSim = 0;
      int totalNumAttrs = 0;
      int totalMinBps = 0;
      int totalMaxBps = 0;
      int maxAttrs = 0;
      int totalStructs = 0;
      int rowIndex = 1;

      String[] headers = {
         "Category",
         "Structure Name",
         "Tx Rate",
         "Sim Min",
         "Sim Max",
         "# of Attributes",
         "Size in Bytes",
         "BPS Min",
         "BPS Max",
         "Initiator",
         "Msg #",
         "SubMsg #",
         "Taskfile Type",
         "Description"};
      Sheet sheet = wb.createSheet("Structure Summary");
      createHeaderRow(wb, sheet, 0, headers);
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
               ArtifactReadable structReadable = struct.getArtifactReadable();

               totalMinSim += stringToInt(structureInfo.minSim);
               totalMaxSim += stringToInt(structureInfo.maxSim);
               totalNumAttrs += structureInfo.numAttributes;
               totalMinBps += stringToInt(structureInfo.minBps);
               totalMaxBps += stringToInt(structureInfo.maxBps);
               maxAttrs = Math.max(maxAttrs, structureInfo.numAttributes);
               totalStructs++;

               Row row = sheet.createRow(rowIndex);
               rowIndex++;
               createCell(row, structureInfo.categoryFull, 0,
                  getStyle(wb, structReadable, CoreAttributeTypes.InterfaceStructureCategory.getId()));
               createCellWithHyperLink(wb, row, struct.getName(), 1, sheetName,
                  getCellStyle(wb, getCellColor(structReadable, CoreAttributeTypes.Name.getId()), CELLSTYLE.HYPERLINK));
               createCell(row, structureInfo.txRate, 2,
                  getStyle(wb, structReadable, message, CoreAttributeTypes.InterfaceMessageRate.getId()));
               createCell(row, structureInfo.minSim, 3,
                  getStyle(wb, structReadable, CoreAttributeTypes.InterfaceMinSimultaneity.getId()));
               createCell(row, structureInfo.maxSim, 4,
                  getStyle(wb, structReadable, CoreAttributeTypes.InterfaceMaxSimultaneity.getId()));
               createCell(row, structureInfo.numAttributes, 5,
                  getCellStyle(wb, getCellColor(structReadable, structureInfo.numElementsChanged)));
               createCell(row, structureInfo.sizeInBytes, 6,
                  getCellStyle(wb, getCellColor(structReadable, structureInfo.structureSizeChanged)));
               createCell(row, structureInfo.minBps, 7, getCellStyle(wb,
                  getCellColor(structReadable, structureInfo.txRateChanged || structureInfo.structureSizeChanged)));
               createCell(row, structureInfo.maxBps, 8, getCellStyle(wb,
                  getCellColor(structReadable, structureInfo.txRateChanged || structureInfo.structureSizeChanged)));
               createCell(row, structureInfo.initiator, 9, getCellStyle(wb, getCellColor(structReadable, false)));
               createCell(row, structureInfo.msgNum, 10,
                  getStyle(wb, structReadable, message, CoreAttributeTypes.InterfaceMessageNumber.getId()));
               createCell(row, structureInfo.subMsgNum, 11, getStyle(wb, structReadable,
                  subMessage.getArtifactReadable(), CoreAttributeTypes.InterfaceSubMessageNumber.getId()));
               createCell(row, structureInfo.taskfile, 12,
                  getStyle(wb, structReadable, CoreAttributeTypes.InterfaceTaskFileType.getId()));
               createCell(row, structureInfo.description, 13, getCellStyle(wb,
                  getCellColor(structReadable, CoreAttributeTypes.Description.getId()), CELLSTYLE.WRAP));
            }
         }
      }

      String[] totalsHeaders = {" ", " ", " ", "Sim Min", "Sim Max", "# of Attributes", " ", "Min", "Max"};
      rowIndex++; // Add extra empty row
      createHeaderRow(wb, sheet, rowIndex, totalsHeaders);
      rowIndex++;

      Row row = sheet.createRow(rowIndex);
      createCell(row, "Totals:", 2, getCellStyle(wb, CELLSTYLE.BOLD));
      createCell(row, totalMinSim, 3);
      createCell(row, totalMaxSim, 4);
      createCell(row, totalNumAttrs, 5);
      createCell(row, "B/s:", 6, getCellStyle(wb, CELLSTYLE.BOLD));
      createCell(row, totalMinBps, 7);
      createCell(row, totalMaxBps, 8);
      rowIndex++;

      row = sheet.createRow(rowIndex);
      createCell(row, "Unique Structures:", 3, getCellStyle(wb, CELLSTYLE.BOLD));
      createCell(row, maxAttrs, 4);
      createCell(row, "MB/s:", 6, getCellStyle(wb, CELLSTYLE.BOLD));
      createCell(row, totalMinBps / 1048576.0, 7);
      createCell(row, totalMaxBps / 1048576.0, 8);
      rowIndex++;

      row = sheet.createRow(rowIndex);
      createCell(row, "Max # Attributes:", 3, getCellStyle(wb, CELLSTYLE.BOLD));
      createCell(row, totalStructs, 4);
      createCell(row, "Mb/s:", 6, getCellStyle(wb, CELLSTYLE.BOLD));
      createCell(row, totalMinBps * 8.0 / 1048576.0, 7);
      createCell(row, totalMaxBps * 8.0 / 1048576.0, 8);

      for (int i = 0; i < headers.length; i++) {
         sheet.autoSizeColumn(i);
      }
      sheet.setColumnWidth(13, 15000);
   }

   private StructureInfo getStructureInfo(InterfaceStructureToken struct) {
      return struct.getId() == 0 ? headersList.get(struct.getName()) : structuresList.get(
         ArtifactId.valueOf(struct.getId()));
   }

   private int stringToInt(String str) {
      try {
         return Integer.parseInt(str);
      } catch (NumberFormatException ex) {
         return 0;
      }
   }

   private void printFirstRowInStructureSheet(Workbook wb, Sheet sheet, InterfaceStructureToken structure, StructureInfo info) {
      ArtifactReadable structReadable = structure.getArtifactReadable();
      String[] headers = {
         "Sheet Type",
         "Full Sheet Name",
         "Abbrev. Sheet Name",
         "Most Recent Update",
         "Category",
         "Transmission Rate",
         "Min Simultaneity",
         "Max Simultaneity",
         "# of attributes",
         "Size in Bytes",
         "Initiator",
         "Msg #",
         "SubMsg #",
         "Taskfile",
         "Description"};
      createHeaderRow(wb, sheet, 0, headers);
      Row row = sheet.createRow(1);

      Object date =
         structure.getId() == 0 ? Strings.EMPTY_STRING : orcsApi.getQueryFactory().transactionQuery().andTxId(
            structure.getArtifactReadable().getTransaction()).getResults().getAtMostOneOrDefault(
               TransactionReadable.SENTINEL).getDate();

      //@formatter:off
      createCell(row, "Functional", 0, getCellStyle(wb, getCellColor(structReadable, false)));
      createCell(row, info.name, 1, getStyle(wb, structReadable, CoreAttributeTypes.Name.getId()));
      createCell(row, structure.getId() == 0 ? info.name : structReadable.getSoleAttributeAsString(CoreAttributeTypes.GeneralStringData, ""), 2, getCellStyle(wb, getCellColor(structReadable, false)));
      createCell(row, date, 3, getCellStyle(wb, getCellColor(structReadable, false)));
      createCell(row, info.category, 4, getStyle(wb, structReadable, CoreAttributeTypes.Name.getId()));
      createCell(row, info.txRate, 5, getStyle(wb, structReadable, info.message, CoreAttributeTypes.InterfaceMessageRate.getId()));
      createCell(row, info.minSim, 6, getStyle(wb, structReadable, CoreAttributeTypes.InterfaceMinSimultaneity.getId()));
      createCell(row, info.maxSim, 7, getStyle(wb, structReadable, CoreAttributeTypes.InterfaceMaxSimultaneity.getId()));
      createCell(row, info.numAttributes, 8, getCellStyle(wb, getCellColor(structReadable, info.numElementsChanged)));
      createCell(row, info.sizeInBytes, 9, getCellStyle(wb, getCellColor(structReadable, info.structureSizeChanged)));
      createCell(row, info.initiator, 10, getCellStyle(wb, getCellColor(structReadable, false)));
      createCell(row, info.msgNum, 11, getStyle(wb, structReadable, info.message, CoreAttributeTypes.InterfaceMessageNumber.getId()));
      createCell(row, info.subMsgNum, 12, getStyle(wb, structReadable, info.submessage, CoreAttributeTypes.InterfaceSubMessageNumber.getId()));
      createCell(row, info.taskfile, 13, getStyle(wb, structReadable, CoreAttributeTypes.InterfaceTaskFileType.getId()));
      createCell(row, info.description, 14, getCellStyle(wb, getCellColor(structReadable, CoreAttributeTypes.Description.getId()), CELLSTYLE.WRAP));
      //@formatter:on
   }

   private void createStructureSheets(Workbook wb, ArtifactId view, List<InterfaceSubMessageToken> subMessages, List<InterfaceStructureToken> structures, SortedMap<InterfaceStructureToken, String> structureLinks) {
      String[] elementHeaders = {
         "Begin Word",
         "Begin Byte",
         "Number of Bytes",
         "Ending Word",
         "Ending Byte",
         "Type",
         "Element Name",
         "Units",
         "Valid Range",
         "Alterable After Creation",
         "Description",
         "Enumerated Literals",
         "Notes"};
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

            int rowIndex = 4; // Start elements on 4th row
            int byteLocation = 0;
            Sheet sheet = wb.createSheet(structureLinks.get(struct));

            StructureInfo info = struct.getId() == 0 ? headersList.get(struct.getName()) : structuresList.get(
               ArtifactId.valueOf(struct.getId()));

            //print structure row info
            printFirstRowInStructureSheet(wb, sheet, struct, info);

            createHeaderRow(wb, sheet, 3, elementHeaders);

            if (struct.getId() == 0) {
               for (InterfaceStructureElementToken element : struct.getElements()) {
                  PlatformTypeToken platformType = element.getPlatformType();
                  Integer byteSize = (int) element.getElementSizeInBytes();
                  printHeaderStructureRow(wb, sheet, rowIndex, view, element, byteLocation, byteSize, platformType);
                  byteLocation = byteLocation + byteSize;
                  rowIndex++;
               }
            } else {
               for (int i = 0; i < info.elements.size(); i++) {
                  ArtifactReadable element = info.elements.get(i);
                  ArtifactReadable platformType = element.getRelated(
                     CoreRelationTypes.InterfaceElementPlatformType_PlatformType).getAtMostOneOrDefault(
                        ArtifactReadable.SENTINEL);

                  Integer byteSize = Integer.valueOf(
                     platformType.getSoleAttributeAsString(CoreAttributeTypes.InterfacePlatformTypeBitSize, "0")) / 8;
                  printDataElementRow(wb, sheet, rowIndex, view, struct.getArtifactReadable(), element, byteLocation,
                     byteSize, platformType, i >= info.byteChangeIndex);
                  if (element.isOfType(CoreArtifactTypes.InterfaceDataElementArray)) {
                     int startIndex = element.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexStart, 0);
                     int endIndex = element.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexEnd, 0);

                     byteSize = byteSize * (endIndex - startIndex + 1);
                  }
                  byteLocation = byteLocation + byteSize;
                  rowIndex++;
               }
            }

            for (int i = 0; i < 15; i++) {
               sheet.autoSizeColumn(i);
            }
            sheet.setColumnWidth(10, Math.min(sheet.getColumnWidth(10), 15000)); // Element Description
            sheet.setColumnWidth(12, Math.min(sheet.getColumnWidth(12), 10000)); // Element Notes
            sheet.setColumnWidth(14, Math.min(sheet.getColumnWidth(14), 15000)); // Structure Description
         }
      }
   }

   private void printHeaderStructureRow(Workbook wb, Sheet sheet, int rowIndex, ArtifactId view, InterfaceStructureElementToken element, Integer byteLocation, Integer byteSize, PlatformTypeToken platformType) {
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

      Row row = sheet.createRow(rowIndex);
      createCell(row, beginWord, 0);
      createCell(row, beginByte, 1);
      createCell(row, byteSize, 2);
      createCell(row, endWord, 3);
      createCell(row, endByte, 4);
      createCell(row, dataType, 5);
      createCell(row, elementName, 6);
      createCell(row, units, 7);
      createCell(row, validRange, 8);
      createCell(row, alterable, 9);
      createCell(row, description, 10, getCellStyle(wb, CELLSTYLE.WRAP));
      createCell(row, "n/a", 11);
      createCell(row, notes, 12);
   }

   private void printDataElementRow(Workbook wb, Sheet sheet, int rowIndex, ArtifactId view, ArtifactReadable structure, ArtifactReadable element, Integer byteLocation, Integer byteSize, ArtifactReadable platformType, boolean bytesChanged) {
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
               "0") + " = " + enumState.getSoleAttributeAsString(CoreAttributeTypes.Name, "") + "\n";
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
      CELLSTYLE pTypeStyle = elementAdded || getCellColor(element, false).equals(
         CELLSTYLE.GREEN) ? CELLSTYLE.GREEN : platformTypeChanged ? CELLSTYLE.YELLOW : CELLSTYLE.NONE;

      CELLSTYLE enumStyle = elementAdded || getCellColor(element, false).equals(
         CELLSTYLE.GREEN) ? CELLSTYLE.GREEN : enumChanged || platformTypeChanged ? CELLSTYLE.YELLOW : CELLSTYLE.NONE;

      CELLSTYLE byteStyle = elementAdded || getCellColor(element, false).equals(
         CELLSTYLE.GREEN) ? CELLSTYLE.GREEN : bytesChanged ? CELLSTYLE.YELLOW : CELLSTYLE.NONE;

      if (element.isOfType(CoreArtifactTypes.InterfaceDataElementArray)) {
         int startIndex = element.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexStart, 0);
         int endIndex = element.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexEnd, 0);
         for (int i = startIndex; i < endIndex + 1; i++) {
            //@formatter:off
            Row row = sheet.createRow(rowIndex);
            createCell(row, beginWord, 0, getCellStyle(wb, byteStyle));
            createCell(row, beginByte, 1, getCellStyle(wb, byteStyle));
            createCell(row, byteSize, 2, getCellStyle(wb, byteStyle));
            createCell(row, endWord, 3, getCellStyle(wb, byteStyle));
            createCell(row, endByte, 4, getCellStyle(wb, byteStyle));
            createCell(row, dataType, 5, getCellStyle(wb, pTypeStyle));
            createCell(row, elementName + " " + Integer.toString(i), 6, getStyle(wb, element, CoreAttributeTypes.Name.getId()));
            createCell(row, units, 7, getCellStyle(wb, pTypeStyle));
            createCell(row, validRange, 8, getCellStyle(wb, pTypeStyle));
            createCell(row, alterable, 9, getStyle(wb, element, CoreAttributeTypes.InterfaceElementAlterable.getId()));
            createCell(row, description, 10,getCellStyle(wb, getCellColor(element, CoreAttributeTypes.Description.getId()), CELLSTYLE.WRAP));
            createCell(row, enumLiterals.trim(), 11, getCellStyle(wb, enumStyle, CELLSTYLE.WRAP));
            createCell(row, notes, 12, getCellStyle(wb, getCellColor(element, CoreAttributeTypes.Notes.getId()), CELLSTYLE.WRAP));
            //@formatter:on

            byteLocation = byteLocation + byteSize;
            beginWord = Math.floorDiv(byteLocation, 4);
            beginByte = Math.floorMod(byteLocation, 4);
            endWord = Math.floorDiv(byteLocation + byteSize - 1, 4);
            endByte = Math.floorMod(byteLocation + byteSize - 1, 4);
         }

      } else {
         //@formatter:off
         Row row = sheet.createRow(rowIndex);
         createCell(row, beginWord, 0, getCellStyle(wb, byteStyle));
         createCell(row, beginByte, 1, getCellStyle(wb, byteStyle));
         createCell(row, byteSize, 2, getCellStyle(wb, byteStyle));
         createCell(row, endWord, 3, getCellStyle(wb, byteStyle));
         createCell(row, endByte, 4, getCellStyle(wb, byteStyle));
         createCell(row, dataType, 5, getCellStyle(wb, pTypeStyle));
         createCell(row, elementName, 6, getStyle(wb, element, CoreAttributeTypes.Name.getId()));
         createCell(row, units, 7, getCellStyle(wb, pTypeStyle));
         createCell(row, validRange, 8, getCellStyle(wb, pTypeStyle));
         createCell(row, alterable, 9, getStyle(wb, element, CoreAttributeTypes.InterfaceElementAlterable.getId()));
         createCell(row, description, 10,getCellStyle(wb, getCellColor(element, CoreAttributeTypes.Description.getId()), CELLSTYLE.WRAP));
         createCell(row, enumLiterals.trim(), 11, getCellStyle(wb, enumStyle, CELLSTYLE.WRAP));
         createCell(row, notes, 12, getCellStyle(wb, getCellColor(element, CoreAttributeTypes.Notes.getId()), CELLSTYLE.WRAP));
         //@formatter:on
      }

   }

   private void createMessageSubMessageSummary(Workbook wb, ArtifactReadable connection, ArtifactReadable primaryNode, ArtifactReadable secondaryNode, List<ArtifactReadable> messages, List<ArtifactReadable> subMessages) {
      List<ArtifactReadable> connectionMessages = messages.stream().filter(
         e -> (e.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageType)).equals("Operational")).collect(
            Collectors.toList());
      List<ArtifactReadable> primaryList = connectionMessages.stream().filter(
         e -> primaryNode.equals(e.getRelated(CoreRelationTypes.InterfaceMessageSendingNode_Node).getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL))).collect(Collectors.toList());

      List<ArtifactReadable> secondaryList = connectionMessages.stream().filter(e -> secondaryNode.equals(
         e.getRelated(CoreRelationTypes.InterfaceMessageSendingNode_Node).getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL))).collect(Collectors.toList());

      Sheet sheet = wb.createSheet("Message and SubMessage Summary");

      sheet.addMergedRegion(CellRangeAddress.valueOf("A1:E1"));
      sheet.addMergedRegion(CellRangeAddress.valueOf("F1:J1"));
      sheet.addMergedRegion(CellRangeAddress.valueOf("D2:E2"));
      sheet.addMergedRegion(CellRangeAddress.valueOf("I2:J2"));

      Row row = sheet.createRow(0);
      row.setHeight((short) 300);
      CellStyle style = getCellStyle(wb, CELLSTYLE.BOLD, CELLSTYLE.CENTERH, CELLSTYLE.CENTERV);

      createCell(row, primaryNode.getName() + " Initiated Message", 0, style);
      createCell(row, secondaryNode.getName() + " Initiated Message", 5, style);

      row = sheet.createRow(1);
      createCell(row, "No.", 0, style);
      createCell(row, "Rate", 1, style);
      createCell(row, "Read/Write", 2, style);
      createCell(row, "Content", 3, style);
      createCell(row, "No.", 5, style);
      createCell(row, "Rate", 6, style);
      createCell(row, "Read/Write", 7, style);
      createCell(row, "Content", 8, style);

      int rowIndex = 2;

      for (int i = 0; i < Math.max(primaryList.size(), secondaryList.size()); i++) {
         ArtifactReadable primaryMessage = ArtifactReadable.SENTINEL;
         ArtifactReadable secondaryMessage = ArtifactReadable.SENTINEL;
         List<ArtifactReadable> primarySubMessages = new ArrayList<>();
         List<ArtifactReadable> secondarySubMessages = new ArrayList<>();

         if (i < primaryList.size()) {
            primaryMessage = primaryList.get(i);
            final ArtifactReadable pm = primaryMessage;
            primarySubMessages = subMessages.stream().filter(
               a -> a.areRelated(CoreRelationTypes.InterfaceMessageSubMessageContent_Message, pm)).collect(
                  Collectors.toList());
         }
         if (i < secondaryList.size()) {
            secondaryMessage = secondaryList.get(i);
            final ArtifactReadable sm = secondaryMessage;
            secondarySubMessages = subMessages.stream().filter(
               a -> a.areRelated(CoreRelationTypes.InterfaceMessageSubMessageContent_Message, sm)).collect(
                  Collectors.toList());
         }

         row = sheet.createRow(rowIndex);
         if (primaryMessage.isValid()) {
            createCell(row, primaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageNumber), 0,
               getStyle(wb, primaryMessage, CoreAttributeTypes.InterfaceMessageNumber.getId()));
            createCell(row,
               primaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate, "Aperiodic"), 1,
               getStyle(wb, primaryMessage, CoreAttributeTypes.InterfaceMessageRate.getId()));
            createCell(row, primaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageWriteAccess), 2,
               getStyle(wb, primaryMessage, CoreAttributeTypes.InterfaceMessageWriteAccess.getId()));
            createCell(row, primaryMessage.getName(), 3,
               getCellStyle(wb, getCellColor(primaryMessage, CoreAttributeTypes.Name.getId()), CELLSTYLE.BOLD));
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 3, 4));
         }
         if (secondaryMessage.isValid()) {
            createCell(row, secondaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageNumber), 5,
               getStyle(wb, secondaryMessage, CoreAttributeTypes.InterfaceMessageNumber.getId()));
            createCell(row,
               secondaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate, "Aperiodic"), 6,
               getStyle(wb, secondaryMessage, CoreAttributeTypes.InterfaceMessageRate.getId()));
            createCell(row, secondaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageWriteAccess),
               7, getStyle(wb, secondaryMessage, CoreAttributeTypes.InterfaceMessageWriteAccess.getId()));
            createCell(row, secondaryMessage.getName(), 8,
               getCellStyle(wb, getCellColor(secondaryMessage, CoreAttributeTypes.Name.getId()), CELLSTYLE.BOLD));
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 8, 9));
         }
         rowIndex++;

         row = sheet.createRow(rowIndex);
         if (primaryMessage.isValid()) {
            createCell(row, "Software Interface Header", 3, getCellStyle(wb, getCellColor(primaryMessage, false)));
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 3, 4));
         }
         if (secondaryMessage.isValid()) {
            createCell(row, "Software Interface Header", 8, getCellStyle(wb, getCellColor(secondaryMessage, false)));
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 8, 9));
         }
         rowIndex++;

         int numSubMessages = Math.max(primarySubMessages.size(), secondarySubMessages.size());
         for (int j = 0; j < numSubMessages; j++) {
            row = sheet.createRow(rowIndex);
            if (j < primarySubMessages.size()) {
               ArtifactReadable subMessage = primarySubMessages.get(j);
               createCell(row, "Submessage " + (j + 1) + ":", 3,
                  getStyle(wb, subMessage, CoreAttributeTypes.Name.getId()));
               createCell(row, subMessage.getName(), 4, getStyle(wb, subMessage, CoreAttributeTypes.Name.getId()));
            }
            if (j < secondarySubMessages.size()) {
               ArtifactReadable subMessage = secondarySubMessages.get(j);
               createCell(row, "Submessage " + (j + 1) + ":", 8,
                  getStyle(wb, subMessage, CoreAttributeTypes.Name.getId()));
               createCell(row, subMessage.getName(), 9, getStyle(wb, subMessage, CoreAttributeTypes.Name.getId()));
            }
            rowIndex++;
         }

         if (primaryMessage.isValid()) {
            sheet.addMergedRegion(new CellRangeAddress(rowIndex - numSubMessages - 2, rowIndex - 1, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(rowIndex - numSubMessages - 2, rowIndex - 1, 1, 1));
            sheet.addMergedRegion(new CellRangeAddress(rowIndex - numSubMessages - 2, rowIndex - 1, 2, 2));
         }
         if (secondaryMessage.isValid()) {
            sheet.addMergedRegion(new CellRangeAddress(rowIndex - numSubMessages - 2, rowIndex - 1, 5, 5));
            sheet.addMergedRegion(new CellRangeAddress(rowIndex - numSubMessages - 2, rowIndex - 1, 6, 6));
            sheet.addMergedRegion(new CellRangeAddress(rowIndex - numSubMessages - 2, rowIndex - 1, 7, 7));
         }

      }

      // Auto size all column widths, then set individual widths
      for (int i = 0; i <= 10; i++) {
         sheet.autoSizeColumn(i);
      }
      sheet.setColumnWidth(0, 3000);
      sheet.setColumnWidth(1, 3000);
      sheet.setColumnWidth(5, 3000);
      sheet.setColumnWidth(6, 3000);
   }

   private Cell createCellWithHyperLink(Workbook wb, Row row, String value, int index, String hyperlink, CellStyle style) {
      Cell cell = createCell(row, value, index, style);
      CreationHelper helper = wb.getCreationHelper();
      HSSFHyperlink link = (HSSFHyperlink) helper.createHyperlink(HyperlinkType.URL);
      cell.setHyperlink(link);
      return cell;
   }

   private Cell createCell(Row row, Object value, int index) {
      return createCell(row, value, index, null);
   }

   private Cell createCell(Row row, Object value, int index, CellStyle style) {
      Cell cell = row.createCell(index);
      if (style != null) {
         cell.setCellStyle(style);
      }
      if (value instanceof String) {
         cell.setCellValue((String) value);
      } else if (value instanceof Integer) {
         cell.setCellValue((Integer) value);
      } else if (value instanceof Double) {
         cell.setCellValue((Double) value);
      } else if (value instanceof Date) {
         String dateString = DateUtil.get((Date) value, "MM/dd/yyyy");
         cell.setCellValue(dateString);
      }
      return cell;
   }

   private CELLSTYLE getCellColor(ArtifactReadable artifact, boolean changed) {
      MimDifferenceItem diffItem = artifact == null ? null : diffs.get(ArtifactId.valueOf(artifact.getId()));
      CELLSTYLE style = CELLSTYLE.NONE;
      if (diffItem != null) {
         style = diffItem.isAdded() ? CELLSTYLE.GREEN : changed ? CELLSTYLE.YELLOW : CELLSTYLE.NONE;
      }
      return style;
   }

   private CELLSTYLE getCellColor(ArtifactReadable artifact, Long attrId) {
      return getCellColor(artifact, artifact, attrId);
   }

   private CELLSTYLE getCellColor(ArtifactReadable rowArtifact, ArtifactReadable cellArtifact, Long attrId) {
      MimDifferenceItem rowDiffItem = rowArtifact == null ? null : diffs.get(ArtifactId.valueOf(rowArtifact.getId()));
      MimDifferenceItem cellDiffItem =
         cellArtifact == null ? null : diffs.get(ArtifactId.valueOf(cellArtifact.getId()));

      if (rowDiffItem != null && rowDiffItem.isAdded()) {
         return CELLSTYLE.GREEN;
      }

      CELLSTYLE color = CELLSTYLE.NONE;
      MimDifferenceItem diffItem = rowDiffItem != null && rowDiffItem.isAdded() ? rowDiffItem : cellDiffItem;
      if (diffItem != null) {
         color = diffItem.isAdded() ? CELLSTYLE.GREEN : diffItem.getDiffs().keySet().contains(
            attrId) ? CELLSTYLE.YELLOW : CELLSTYLE.NONE;
      }
      return color;
   }

   private CellStyle getStyle(Workbook wb, ArtifactReadable artifact, Long attrId) {
      return getCellStyle(wb, getCellColor(artifact, attrId));
   }

   private CellStyle getStyle(Workbook wb, ArtifactReadable rowArtifact, ArtifactReadable cellArtifact, Long attrId) {
      return getCellStyle(wb, getCellColor(rowArtifact, cellArtifact, attrId));
   }

   private CellStyle getCellStyle(Workbook workbook, CELLSTYLE... styles) {
      String styleString = "";
      for (CELLSTYLE s : styles) {
         styleString += s.toString();
      }

      CellStyle style = cellStyles.get(styleString);
      if (style != null) {
         return style;
      }

      style = workbook.createCellStyle();
      HSSFFont font = (HSSFFont) workbook.createFont();

      for (CELLSTYLE s : styles) {
         switch (s) {
            case BOLD:
               font.setBold(true);
               break;
            case CENTERH:
               style.setAlignment(HorizontalAlignment.CENTER);
               break;
            case CENTERV:
               style.setVerticalAlignment(VerticalAlignment.CENTER);
               break;
            case GREEN:
               style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
               style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
               break;
            case HYPERLINK:
               font.setColor(IndexedColors.BLUE.getIndex());
               font.setUnderline(Font.U_SINGLE);
               break;
            case YELLOW:
               style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
               style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
               break;
            case WRAP:
               style.setWrapText(true);
               break;
            default:
               break;
         }
      }

      style.setFont(font);
      cellStyles.put(styleString, style);

      return style;
   }

   private enum CELLSTYLE {
      BOLD,
      CENTERH,
      CENTERV,
      GREEN,
      HYPERLINK,
      YELLOW,
      NONE,
      WRAP
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
