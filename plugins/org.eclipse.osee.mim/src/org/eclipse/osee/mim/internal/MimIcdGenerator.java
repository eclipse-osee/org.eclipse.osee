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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.model.dto.ChangeReportRowDto;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.excel.ExcelWorkbookWriter;
import org.eclipse.osee.framework.jdk.core.util.io.excel.ExcelWorkbookWriter.CELLSTYLE;
import org.eclipse.osee.framework.jdk.core.util.io.excel.ExcelWorkbookWriter.HyperLinkType;
import org.eclipse.osee.framework.jdk.core.util.io.excel.ExcelWorkbookWriter.WorkbookFormat;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.mim.InterfaceDifferenceReportApi;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceStructureApi;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.types.ElementArrayIndexOrder;
import org.eclipse.osee.mim.types.InterfaceConnection;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceNode;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.mim.types.MimChangeSummary;
import org.eclipse.osee.mim.types.MimChangeSummaryItem;
import org.eclipse.osee.mim.types.PlatformTypeToken;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.ds.FollowRelation;
import org.eclipse.osee.orcs.data.TransactionReadable;

/**
 * @author Audrey E. Denk
 * @author Ryan T. Baldwin
 */
public class MimIcdGenerator {

   private final MimApi mimApi;
   private final OrcsApi orcsApi;
   private final InterfaceMessageApi interfaceMessageApi;
   private final InterfaceStructureApi interfaceStructureApi;
   private final InterfaceDifferenceReportApi interfaceDifferenceReportApi;

   private final Set<String> logicalTypes;
   private final Set<String> units;
   private final Set<String> txRates;
   private final Set<String> categories;
   private final Map<ArtifactId, StructureInfo> structuresList;
   private final Map<String, StructureInfo> headersList;
   private final Map<ArtifactId, InterfaceSubMessageToken> messageHeaders;
   private final Map<String, InterfaceStructureToken> messageHeaderStructures;
   private final Map<ArtifactId, MimChangeSummaryItem> diffs;
   private final Map<String, String> logicalTypeMaxRange;
   private final Map<ArtifactId, Pair<String, String>> releaseArtifacts =
      new HashMap<ArtifactId, Pair<String, String>>();

   public MimIcdGenerator(MimApi mimApi) {
      this.mimApi = mimApi;
      this.orcsApi = mimApi.getOrcsApi();
      this.structuresList = new HashMap<>();
      this.headersList = new HashMap<>();
      this.messageHeaders = new HashMap<>();
      this.messageHeaderStructures = new HashMap<>();
      this.diffs = new HashMap<>();

      this.logicalTypes = new HashSet<>();
      this.units = new HashSet<>();
      this.txRates = new HashSet<>();
      this.categories = new HashSet<>();
      this.interfaceMessageApi = mimApi.getInterfaceMessageApi();
      this.interfaceStructureApi = mimApi.getInterfaceStructureApi();
      this.interfaceDifferenceReportApi = mimApi.getInterfaceDifferenceReportApi();

      this.logicalTypeMaxRange = new HashMap<>();
      this.logicalTypeMaxRange.put("boolean", "0 to 1");
      this.logicalTypeMaxRange.put("character", "0 to 255");
      this.logicalTypeMaxRange.put("sShort", "-32,768 to 32,767");
      this.logicalTypeMaxRange.put("uShort", "0 to 2^16-1");
      this.logicalTypeMaxRange.put("enumeration", "See Enumerated Literals");
      this.logicalTypeMaxRange.put("float", "-Inf to Inf");
      this.logicalTypeMaxRange.put("sInteger", "-2^31 to 2^31-1");
      this.logicalTypeMaxRange.put("uInteger", "0 to 2^32-1");
      this.logicalTypeMaxRange.put("double", "0.0 to 604800.0");
      this.logicalTypeMaxRange.put("uLong", "0 to 2^64-1");
   }

   public void runOperation(OutputStream outputStream, BranchId branch, ArtifactId view, ArtifactId connectionId,
      boolean diff) {
      InterfaceConnection conn = mimApi.getInterfaceConnectionViewApi().get(branch, view, connectionId,
         Arrays.asList(FollowRelation.fork(CoreRelationTypes.InterfaceConnectionNode_Node),
            FollowRelation.follow(CoreRelationTypes.InterfaceConnectionMessage_Message),
            FollowRelation.follow(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage),
            FollowRelation.follow(CoreRelationTypes.InterfaceSubMessageContent_Structure),
            FollowRelation.follow(CoreRelationTypes.InterfaceStructureContent_DataElement),
            FollowRelation.fork(CoreRelationTypes.InterfaceElementArrayElement_ArrayElement,
               FollowRelation.follow(CoreRelationTypes.InterfaceElementPlatformType_PlatformType),
               FollowRelation.follow(CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet),
               FollowRelation.follow(CoreRelationTypes.InterfaceEnumeration_EnumerationState)),
            FollowRelation.follow(CoreRelationTypes.InterfaceElementPlatformType_PlatformType),
            FollowRelation.follow(CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet),
            FollowRelation.follow(CoreRelationTypes.InterfaceEnumeration_EnumerationState)));

      Branch currentBranch =
         orcsApi.getQueryFactory().branchQuery().andId(branch).getResults().getAtMostOneOrDefault(Branch.SENTINEL);

      BranchId parentBranch = currentBranch.getParentBranch();

      MimChangeSummary summary = new MimChangeSummary(new HashMap<>());
      if (diff) {
         summary = interfaceDifferenceReportApi.getChangeSummary(branch, parentBranch, view);
         diffs.putAll(summary.getAll());
      }

      InterfaceNode primaryNode = InterfaceNode.SENTINEL;
      InterfaceNode secondaryNode = InterfaceNode.SENTINEL;

      List<InterfaceNode> nodes = conn.getNodes();
      if (nodes.size() == 2) {
         primaryNode = nodes.get(0);
         secondaryNode = nodes.get(1);
      }

      List<ArtifactReadable> messages = new LinkedList<>();
      List<ArtifactReadable> subMessages = new LinkedList<>();
      List<InterfaceSubMessageToken> subMessagesWithHeaders = new LinkedList<>();
      List<InterfaceStructureToken> structures = new LinkedList<>();
      SortedMap<InterfaceStructureToken, String> structureLinks = new TreeMap<InterfaceStructureToken, String>();

      if (conn.isValid() && primaryNode.isValid() && secondaryNode.isValid()) {
         // Write sheets
         ExcelWorkbookWriter writer = new ExcelWorkbookWriter(outputStream, WorkbookFormat.XLSX);
         writer.setDefaultZoom(80);
         createChangeHistory(writer, currentBranch, conn.getId());
         messages =
            conn.getArtifactReadable().getRelated(CoreRelationTypes.InterfaceConnectionMessage_Message).getList();

         for (ArtifactReadable message : messages) {
            InterfaceMessageToken msg = new InterfaceMessageToken(message);
            mimApi.getInterfaceMessageApi().setUpMessage(branch, msg);

            List<ArtifactReadable> messageSubMessages =
               message.getRelated(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage).getList();
            List<InterfaceSubMessageToken> smsgTokens =
               messageSubMessages.stream().map(art -> new InterfaceSubMessageToken(art)).collect(Collectors.toList());

            if (message.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageType, "error").equals(
               "Operational")) {
               InterfaceSubMessageToken header = interfaceMessageApi.getMessageHeader(msg);
               messageHeaders.put(message.getArtifactId(), header);
               smsgTokens.add(0, header);

               InterfaceStructureToken headerStruct = interfaceStructureApi.getMessageHeaderStructure(branch,
                  ArtifactId.valueOf(connectionId), ArtifactId.valueOf(message.getId()), view);
               messageHeaderStructures.put(header.getName(), headerStruct);
            }
            subMessages.addAll(messageSubMessages);
            subMessagesWithHeaders.addAll(smsgTokens);
            for (InterfaceSubMessageToken smsg : smsgTokens) {
               List<InterfaceStructureToken> structs = new LinkedList<>();
               if (smsg.getId() == 0) {
                  structs.add(0, interfaceStructureApi.getMessageHeaderStructure(branch,
                     ArtifactId.valueOf(connectionId), message.getArtifactId(), view));
               } else {
                  structs.addAll(smsg.getArtifactReadable().getRelated(
                     CoreRelationTypes.InterfaceSubMessageContent_Structure).getList().stream().map(
                        art -> new InterfaceStructureToken(art)).collect(Collectors.toList()));
               }
               structures.addAll(structs);
            }
         }

         for (InterfaceStructureToken structure : structures) {
            String sheetName =
               structure.getNameAbbrev().isEmpty() ? structure.getName().replace("Command Taskfile", "CT").replace(
                  "Status Taskfile", "ST") : structure.getNameAbbrev();
            if (structure.getArtifactReadable() != null && structure.getArtifactReadable().isValid()) {
               String abbrevName = structure.getArtifactReadable().getSoleAttributeAsString(
                  CoreAttributeTypes.GeneralStringData, "null");
               if (!abbrevName.equals("null")) {
                  sheetName = abbrevName;
               }
            }
            structureLinks.put(structure, sheetName);
         }

         createStructureInfo(branch, view, messages);

         if (diff) {
            createChangeSummary(writer, summary);
         }

         createMessageSubMessageSummary(writer, branch, view, conn.getArtifactReadable(),
            primaryNode.getArtifactReadable(), secondaryNode.getArtifactReadable(), messages, subMessages);
         createUnitsAndTypesSheet(writer); // Create sheet but do not write until the end to allow for list population
         createStructureNamesSheet(writer, structureLinks);
         createStructureSummarySheet(writer, branch, messages, structureLinks);
         createStructureSheets(writer, subMessagesWithHeaders, structureLinks);
         writeUnitsAndTypesSheet(writer, branch, view, primaryNode, secondaryNode);
         writer.writeWorkbook();
         writer.closeWorkbook();

      }
   }

   private void createStructureInfo(BranchId branch, ArtifactId view, List<ArtifactReadable> messages) {
      for (ArtifactReadable message : messages) {
         ArtifactReadable sendingNode =
            message.getRelated(CoreRelationTypes.InterfaceMessagePubNode_Node).getOneOrDefault(
               ArtifactReadable.SENTINEL);
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
               List<InterfaceStructureElementToken> flatElements = new LinkedList<>();
               for (InterfaceStructureElementToken element : struct.getElements()) {
                  if (element.getInterfaceElementArrayHeader()) {
                     int startIndex = element.getInterfaceElementIndexStart();
                     int endIndex = element.getInterfaceElementIndexEnd();
                     for (int i = startIndex; i <= endIndex; i++) {
                        for (InterfaceStructureElementToken arrayElement : element.getArrayElements()) {
                           InterfaceStructureElementToken arrayElementCopy =
                              new InterfaceStructureElementToken(arrayElement.getArtifactReadable());
                           String arrayElementName =
                              element.getInterfaceElementWriteArrayHeaderName() ? element.getName() + " " + i + " " + arrayElement.getName() : arrayElement.getName() + " " + i;
                           arrayElementCopy.setName(arrayElementName);
                           // Inherit some properties from the header in order to print the indices correctly later
                           arrayElementCopy.setInterfaceElementWriteArrayHeaderName(
                              element.getInterfaceElementWriteArrayHeaderName());
                           arrayElementCopy.setInterfaceElementArrayIndexOrder(
                              element.getInterfaceElementArrayIndexOrder());
                           arrayElementCopy.setInterfaceElementArrayIndexDelimiterOne(
                              element.getInterfaceElementArrayIndexDelimiterOne());
                           arrayElementCopy.setInterfaceElementArrayIndexDelimiterTwo(
                              element.getInterfaceElementArrayIndexDelimiterTwo());
                           arrayElementCopy.setInterfaceElementArrayHeader(true);
                           flatElements.add(arrayElementCopy);
                        }
                     }
                  } else {
                     flatElements.add(element);
                  }
               }
               MimChangeSummaryItem msgDiffItem = diffs.get(message.getArtifactId());
               MimChangeSummaryItem structDiffItem = diffs.get(struct.getArtifactId());

               Integer sizeInBytes = 0;
               int elementCount = 0;
               boolean structureSizeChanged = false;
               boolean structureChanged = false;
               int byteChangeIndex = flatElements.size(); // Used to track which element first changes the structure size in order to display diff coloring for every element after.

               for (int i = 0; i < flatElements.size(); i++) {
                  InterfaceStructureElementToken element = flatElements.get(i);
                  elementCount = struct.isAutogenerated() ? elementCount + 1 : elementCount + element.getArrayLength();
                  sizeInBytes = sizeInBytes + (int) element.getElementSizeInBytes();

                  MimChangeSummaryItem elementDiffItem = diffs.get(element.getArtifactId());
                  MimChangeSummaryItem pTypeDiffItem = diffs.get(element.getPlatformType().getArtifactId());
                  structureSizeChanged =
                     structureSizeChanged || (pTypeDiffItem != null && pTypeDiffItem.hasAttributeChanges(
                        CoreAttributeTypes.InterfacePlatformTypeBitSize.getId())) || (elementDiffItem != null && (elementDiffItem.isAdded() || !elementDiffItem.getRelationChanges().isEmpty()));
                  Optional<MimChangeSummaryItem> structElementDiffItem =
                     structDiffItem == null ? Optional.empty() : structDiffItem.getChild(element.getArtifactId());
                  boolean elementAdded = structElementDiffItem.isPresent() && structElementDiffItem.get().isAdded();
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
               String subMsgNumber = subMessage.getInterfaceSubMessageNumber();
               String taskFileType = struct.getInterfaceTaskFileType() + "";
               String desc = struct.getDescription();
               boolean txRateChanged = msgDiffItem != null && msgDiffItem.hasAttributeChanges(
                  CoreAttributeTypes.InterfaceMessageRate.getId());
               boolean msgNumChanged = msgDiffItem != null && msgDiffItem.hasAttributeChanges(
                  CoreAttributeTypes.InterfaceMessageNumber.getId());
               boolean numElementsChanged = structDiffItem != null && !structDiffItem.getRelationChanges().isEmpty();
               boolean sizeInBytesChanged = numElementsChanged || structureSizeChanged;
               structureChanged = structureChanged || structDiffItem != null || txRateChanged || msgNumChanged;
               String icdcn = structDiffItem != null ? icdcn = getRelatedWorkFlow(structDiffItem.getItemTxIds()) : "";

               txRates.add(msgRateText);
               categories.add(cat);

               StructureInfo structureInfo = new StructureInfo(struct.getName(), struct.getNameAbbrev(), cat,
                  msgRateText, minSim, maxSim, minBps, maxBps, elementCount, sizeInBytes, sendingNode.getName(),
                  msgNumber, subMsgNumber, taskFileType, desc, message, subMessage.getArtifactReadable(), flatElements,
                  structureChanged, txRateChanged, numElementsChanged, sizeInBytesChanged, byteChangeIndex, icdcn);
               if (struct.getId() == 0) {
                  headersList.put(struct.getName(), structureInfo);
               } else {
                  structuresList.put(ArtifactId.valueOf(struct.getId()), structureInfo);
               }
            }
         }
      }
   }

   private void createChangeHistory(ExcelWorkbookWriter writer, Branch branch, long connectionId) {
      String[] headers = {"Team Workflow", "Date", "Change Description", "Related Workflow"};

      writer.createSheet("Change History");
      writer.setColumnWidth(0, 5000);
      writer.setColumnWidth(1, 3000);
      writer.setColumnWidth(2, 10000);
      writer.setColumnWidth(3, 10000);
      writer.writeRow(0, headers, CELLSTYLE.BOLD);

      boolean working = branch.getBranchType().equals(BranchType.WORKING);

      Long queryBranch = working ? branch.getParentBranch().getId() : branch.getId();

      String ordering =
         orcsApi.getJdbcService().getClient().getDbType().isPaginationOrderingSupported() ? "ORDER BY time DESC" : "";
      //@formatter:off
      String query =
         "WITH"+orcsApi.getJdbcService().getClient().getDbType().getPostgresRecurse()+" gammas (transaction_id, gamma_id, commit_art_id,time) AS "
         + "(SELECT txd1.transaction_id, changedTxs.gamma_id,txd1.commit_art_id,txd1.time "
         + "FROM osee_tx_details txd1, osee_txs attrTxs, osee_attribute attr, osee_txs changedTxs "
         + "WHERE txd1.branch_id in (?,?) AND attrTxs.branch_id = ? AND attrTxs.tx_current = 1 AND "
         + "attrTxs.gamma_id = attr.gamma_id AND attr.art_id = txd1.commit_art_id AND attr.attr_type_id = ? AND "
         + "attr.value IN "
         + "(SELECT DISTINCT "+orcsApi.getJdbcService().getClient().getDbType().getPostgresCastStart()+ " art.art_id "+ orcsApi.getJdbcService().getClient().getDbType().getPostgresCastVarCharEnd()
         + "FROM osee_txs artTxs, osee_artifact art, osee_txs attrTxs, osee_attribute attr "
         + "WHERE artTxs.branch_id = ? AND artTxs.tx_current = 1 AND art.art_type_id = ? "
         + "AND artTxs.gamma_id = art.gamma_id AND attrTxs.branch_id = ? AND attrTxs.tx_current = 1 "
         + "AND attrTxs.gamma_id = attr.gamma_id AND attr.art_id = art.art_id AND attr.attr_type_id = ? "
         + "AND attr.value = 'MIM') AND txd1.transaction_id = changedTxs.transaction_id AND changedTxs.branch_id  in (?,?)), "
         + "arts (commit_art_id, transaction_id, art_id,time) AS (SELECT commit_art_id, transaction_id, art_id,time FROM gammas, "
         + "osee_artifact art WHERE gammas.gamma_id = art.gamma_id UNION SELECT commit_art_id, transaction_id,art_id, time "
         + "FROM gammas, osee_attribute attr WHERE gammas.gamma_id = attr.gamma_id UNION SELECT commit_art_id, transaction_id, "
         + "a_art_id,time FROM gammas, osee_relation rel WHERE gammas.gamma_id = rel.gamma_id UNION SELECT commit_art_id, "
         + "transaction_id,b_art_id,time FROM gammas, osee_relation rel WHERE gammas.gamma_id = rel.gamma_id ), "
         + "allRels (a_art_id, b_art_id, gamma_id, rel_type) AS ( SELECT a_art_id, b_art_id, txs.gamma_id, rel_type "
         + "FROM osee_txs txs, osee_relation rel WHERE txs.branch_id in (?,?) AND txs.tx_current = 1 AND "
         + "txs.gamma_id = rel.gamma_id UNION SELECT a_art_id, b_art_id, txs.gamma_id, rel_link_type_id rel_type FROM "
         + "osee_txs txs, osee_relation_link rel WHERE txs.branch_id in (?,?) AND txs.tx_current = 1 AND "
         + "txs.gamma_id = rel.gamma_id), cte_query(a_art_id, b_art_id, rel_type) AS "
         + "(SELECT a_art_id, b_art_id, rel_type FROM allRels WHERE a_art_id = ? "
         + "UNION ALL SELECT e.a_art_id, e.b_art_id, e.rel_type FROM allRels e "
         + "INNER JOIN cte_query c ON c.b_art_id = e.a_art_id) SELECT name,commit_art_id, atsid,time,row_number() over ("+ordering+") rn, relTw FROM (SELECT DISTINCT "
         + "attr.value name, attr2.value atsid, arts.time time, arts.commit_art_id FROM cte_query, "
         + "arts, osee_txs attrTxs, osee_attribute attr, osee_txs attrTxs2, osee_attribute attr2 "
         + "WHERE b_art_id = arts.art_id AND attrTxs.branch_id = ? AND attrTxs.tx_current = 1 "
         + "AND attrTxs.gamma_id = attr.gamma_id AND attr.art_id = arts.commit_art_id AND attr.attr_type_id = ? "
         + "AND attrTxs2.branch_id = ? AND attrTxs2.tx_current = 1 AND attrTxs2.gamma_id = attr2.gamma_id "
         + "AND attr2.art_id = arts.commit_art_id AND attr2.attr_type_id = ?) t1 "
         + "left outer join "
         + "(select rel_link.a_art_id, attr3.value relTw ,  rel_link.b_art_id "
         + "from osee_txs relTxs,osee_relation_link rel_link,osee_txs attrTxs3,osee_attribute attr3 "
         + "where rel_link.rel_link_type_id = ? and relTxs.branch_id = ? "
         + " and relTxs.gamma_id = rel_link.gamma_id and rel_link.b_art_id = attr3.art_id and relTxs.tx_current = 1 "
         + "and attrTxs3.branch_id = ? and attrTxs3.gamma_id = attr3.gamma_id and attrTxs3.tx_current = 1 "
         + "and attr3.attr_type_id = ?) t2 on t2.a_art_id = t1.commit_art_id";
      //@formatter:on
      Consumer<JdbcStatement> consumer = stmt -> {

         int row = stmt.getInt("rn");
         String atsId = stmt.getString("atsId");
         java.sql.Timestamp timestamp = stmt.getTimestamp("time");
         String name = stmt.getString("name");
         String relTw = stmt.getString("relTw");
         ArtifactId commitArtId = ArtifactId.valueOf(stmt.getLong("commit_art_id"));
         releaseArtifacts.put(commitArtId, new Pair<String, String>(atsId, relTw));
         writer.writeCell(row, 0, atsId);
         writer.writeCell(row, 1, timestamp);
         writer.writeCell(row, 2, name);
         writer.writeCell(row, 3, relTw);
      };

      orcsApi.getJdbcService().getClient().runQuery(consumer, query, branch, queryBranch, CoreBranches.COMMON.getId(),
         AtsAttributeTypes.TeamDefinitionReference, CoreBranches.COMMON.getId(), AtsArtifactTypes.TeamDefinition,
         CoreBranches.COMMON.getId(), AtsAttributeTypes.WorkType, branch, queryBranch, branch, queryBranch, branch,
         queryBranch, connectionId, CoreBranches.COMMON.getId(), CoreAttributeTypes.Name, CoreBranches.COMMON.getId(),
         AtsAttributeTypes.AtsId, CoreRelationTypes.Dependency_Dependency, CoreBranches.COMMON, CoreBranches.COMMON,
         AtsAttributeTypes.AtsId);

   }

   private void createChangeSummary(ExcelWorkbookWriter writer, MimChangeSummary summary) {
      writer.createSheet("Change Summary");

      String[] headers = {
         "Item Type",
         "Sub Item Type",
         "Item Name",
         "Change Type",
         "Modification Type",
         "What Changed",
         "Is Value",
         "Was Value",
         "ICDCN"};
      writer.writeRow(0, headers, CELLSTYLE.BOLD);

      int rowIndex = 1;

      for (MimChangeSummaryItem message : summary.getMessages().values()) {
         rowIndex = writeArtifactChanges(writer, message, rowIndex, false);
         rowIndex++;
      }

      for (MimChangeSummaryItem submessage : summary.getSubMessages().values()) {
         rowIndex = writeArtifactChanges(writer, submessage, rowIndex, false);
         rowIndex++;
      }

      for (MimChangeSummaryItem structure : summary.getStructures().values()) {
         rowIndex = writeArtifactChanges(writer, structure, rowIndex, false);
         rowIndex++;
      }

      writer.autoSizeAllColumns(headers.length);
      writer.setColumnWidth(6, 20000);
      writer.setColumnWidth(7, 20000);

   }

   private int writeArtifactChanges(ExcelWorkbookWriter writer, MimChangeSummaryItem item, int rowIndex,
      boolean subArtifact) {
      int startRow = rowIndex;
      writer.writeCell(rowIndex, subArtifact ? 1 : 0, formatArtifactType(item.getArtType()), CELLSTYLE.CENTERV);
      writer.writeCell(rowIndex, 2, item.getName(), CELLSTYLE.CENTERV);
      if (item.isAdded()) {
         writer.writeCell(rowIndex, 3, "Added", CELLSTYLE.GREEN);
         rowIndex++;
         return rowIndex;
      }
      if (item.isDeleted()) {
         writer.writeCell(rowIndex, 3, "Deleted", CELLSTYLE.LIGHT_RED);
         rowIndex++;
         return rowIndex;
      }
      if (!item.getAttributeChanges().isEmpty()) {
         int firstRow = rowIndex;
         writer.writeCell(rowIndex, 3, "Modified", CELLSTYLE.YELLOW, CELLSTYLE.CENTERV);
         writer.writeCell(rowIndex, 4, "Attributes", CELLSTYLE.CENTERV);
         for (ChangeReportRowDto change : item.getAttributeChanges()) {
            writer.writeCell(rowIndex, 5, change.getItemType());
            writer.writeCell(rowIndex, 6, change.getIsValue(), CELLSTYLE.WRAP);
            writer.writeCell(rowIndex, 7, change.getWasValue(), CELLSTYLE.WRAP);
            writer.writeCell(rowIndex, 8, getRelatedWorkFlow(Collections.singleton(change.getTxId())));
            rowIndex++;
         }
         if (firstRow < rowIndex - 1) {
            writer.addMergedRegion(firstRow, rowIndex - 1, 3, 3);
            writer.addMergedRegion(firstRow, rowIndex - 1, 4, 4);
         }
      }

      if (startRow < rowIndex - 1) {
         writer.addMergedRegion(startRow, rowIndex - 1, 0, 0);
         writer.addMergedRegion(startRow, rowIndex - 1, 1, 1);
         writer.addMergedRegion(startRow, rowIndex - 1, 2, 2);
      }

      if (!subArtifact) {
         if (!item.getChildren().isEmpty()) {
            if (item.getAttributeChanges().isEmpty()) {
               rowIndex++;
            }
            for (MimChangeSummaryItem child : item.getChildren()) {
               rowIndex = writeArtifactChanges(writer, child, rowIndex, true);
            }
         }
      }

      return rowIndex;
   }

   private String formatArtifactType(ArtifactTypeToken artType) {
      return artType.getName().replace("Interface ", "").replace("DataElement", "Element");
   }

   private void createStructureNamesSheet(ExcelWorkbookWriter writer,
      SortedMap<InterfaceStructureToken, String> structures) {
      writer.createSheet("Structure Names");
      String[] headers = {"Structure Name", "Structure Name", "Structure Name"};
      writer.writeRow(0, headers, CELLSTYLE.BOLD);

      List<InterfaceStructureToken> structureList = new LinkedList<>(structures.keySet());
      structureList.sort(new Comparator<InterfaceStructureToken>() {
         @Override
         public int compare(InterfaceStructureToken o1, InterfaceStructureToken o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
         }
      });

      int colLength = (int) Math.ceil(structureList.size() / 3.0);
      for (int i = 0; i < structureList.size(); i++) {
         int rowNum = i + 1;
         int colNum = 0;
         InterfaceStructureToken structure = structureList.get(i);
         if (i >= colLength * 2) {
            colNum = 2;
            rowNum -= (colLength * 2);
         } else if (i >= colLength) {
            colNum = 1;
            rowNum -= colLength;
         }

         writer.writeCell(rowNum, colNum, structure.getName(), "'" + structures.get(structure) + "'!A1",
            HyperLinkType.SHEET, CELLSTYLE.HYPERLINK, getStructureNameColor(structure));
         rowNum++;
      }

      writer.autoSizeAllColumns(headers.length);
   }

   private CELLSTYLE getStructureNameColor(InterfaceStructureToken struct) {
      StructureInfo info = getStructureInfo(struct);
      MimChangeSummaryItem diffItem = diffs.get(struct.getArtifactId());
      if (diffItem != null && diffItem.isAdded()) {
         return CELLSTYLE.GREEN;
      } else if (info.structureChanged) {
         return CELLSTYLE.YELLOW;
      } else {
         return CELLSTYLE.NONE;
      }
   }

   private void createStructureSummarySheet(ExcelWorkbookWriter writer, BranchId branch,
      List<ArtifactReadable> messages, SortedMap<InterfaceStructureToken, String> structureLinks) {
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
      writer.createSheet("Structure Summary");
      writer.writeRow(0, headers, CELLSTYLE.BOLD);
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

               writer.writeCell(rowIndex, 0, structureInfo.category,
                  getCellColor(structReadable, CoreAttributeTypes.InterfaceStructureCategory.getId()));
               writer.writeCell(rowIndex, 1, struct.getName(), "'" + sheetName + "'!A1", HyperLinkType.SHEET,
                  getCellColor(structReadable, CoreAttributeTypes.Name.getId()), CELLSTYLE.HYPERLINK);
               writer.writeCell(rowIndex, 2, structureInfo.txRate,
                  getCellColor(structReadable, message, CoreAttributeTypes.InterfaceMessageRate.getId()));
               writer.writeCell(rowIndex, 3, structureInfo.minSim,
                  getCellColor(structReadable, CoreAttributeTypes.InterfaceMinSimultaneity.getId()));
               writer.writeCell(rowIndex, 4, structureInfo.maxSim,
                  getCellColor(structReadable, CoreAttributeTypes.InterfaceMaxSimultaneity.getId()));
               writer.writeCell(rowIndex, 5, structureInfo.numAttributes,
                  getCellColor(structReadable, structureInfo.numElementsChanged));
               writer.writeCell(rowIndex, 6, structureInfo.sizeInBytes,
                  getCellColor(structReadable, structureInfo.structureSizeChanged));
               writer.writeCell(rowIndex, 7, structureInfo.minBps,
                  getCellColor(structReadable, structureInfo.txRateChanged || structureInfo.structureSizeChanged));
               writer.writeCell(rowIndex, 8, structureInfo.maxBps,
                  getCellColor(structReadable, structureInfo.txRateChanged || structureInfo.structureSizeChanged));
               writer.writeCell(rowIndex, 9, structureInfo.initiator, getCellColor(structReadable, false));
               writer.writeCell(rowIndex, 10, structureInfo.msgNum,
                  getCellColor(structReadable, message, CoreAttributeTypes.InterfaceMessageNumber.getId()));
               writer.writeCell(rowIndex, 11, structureInfo.subMsgNum, getCellColor(structReadable,
                  subMessage.getArtifactReadable(), CoreAttributeTypes.InterfaceSubMessageNumber.getId()));
               writer.writeCell(rowIndex, 12, structureInfo.taskfile,
                  getCellColor(structReadable, CoreAttributeTypes.InterfaceTaskFileType.getId()));
               writer.writeCell(rowIndex, 13, structureInfo.description,
                  getCellColor(structReadable, CoreAttributeTypes.Description.getId()), CELLSTYLE.WRAP);
               rowIndex++;
            }
         }
      }

      String[] totalsHeaders = {" ", " ", " ", "Sim Min", "Sim Max", "# of Attributes", " ", "Min", "Max"};
      rowIndex++; // Add extra empty row
      writer.writeRow(rowIndex, totalsHeaders, CELLSTYLE.BOLD);
      rowIndex++;

      writer.writeCell(rowIndex, 2, "Totals:", CELLSTYLE.BOLD);
      writer.writeCell(rowIndex, 3, totalMinSim);
      writer.writeCell(rowIndex, 4, totalMaxSim);
      writer.writeCell(rowIndex, 5, totalNumAttrs);
      writer.writeCell(rowIndex, 6, "B/s:", CELLSTYLE.BOLD);
      writer.writeCell(rowIndex, 7, totalMinBps);
      writer.writeCell(rowIndex, 8, totalMaxBps);
      rowIndex++;

      writer.writeCell(rowIndex, 3, "Unique Structures:", CELLSTYLE.BOLD);
      writer.writeCell(rowIndex, 4, totalStructs);
      writer.writeCell(rowIndex, 6, "MB/s:", CELLSTYLE.BOLD);
      writer.writeCell(rowIndex, 7, totalMinBps / 1048576.0);
      writer.writeCell(rowIndex, 8, totalMaxBps / 1048576.0);
      rowIndex++;

      writer.writeCell(rowIndex, 3, "Max # Attributes:", CELLSTYLE.BOLD);
      writer.writeCell(rowIndex, 4, maxAttrs);
      writer.writeCell(rowIndex, 6, "Mb/s:", CELLSTYLE.BOLD);
      writer.writeCell(rowIndex, 7, totalMinBps * 8.0 / 1048576.0);
      writer.writeCell(rowIndex, 8, totalMaxBps * 8.0 / 1048576.0);

      writer.autoSizeAllColumns(headers.length);
      writer.setColumnWidth(13, 15000);
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

   /**
    * @return array of string lengths of each column
    */
   private int[] printFirstRowInStructureSheet(ExcelWorkbookWriter writer, InterfaceStructureToken structure,
      StructureInfo info) {
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
         "Description",
         "ICDCN"};

      writer.writeRow(0, headers, CELLSTYLE.BOLD);

      Object date =
         structure.getId() == 0 ? Strings.EMPTY_STRING : orcsApi.getQueryFactory().transactionQuery().andTxId(
            structure.getArtifactReadable().getTransaction()).getResults().getAtMostOneOrDefault(
               TransactionReadable.SENTINEL).getDate();

      Object[] values = new Object[] {
         "Functional",
         info.name,
         info.nameAbbrev,
         date,
         info.category,
         info.txRate,
         info.minSim,
         info.maxSim,
         info.numAttributes,
         info.sizeInBytes,
         info.initiator,
         info.msgNum,
         info.subMsgNum,
         info.taskfile,
         info.description,
         info.icdcn};
      //@formatter:off
      writer.writeCell(1, 0,  values[0], getCellColor(structReadable, false));
      writer.writeCell(1, 1,  values[1],  getCellColor(structReadable, CoreAttributeTypes.Name.getId()));
      writer.writeCell(1, 2,  values[2], getCellColor(structReadable, false));
      writer.writeCell(1, 3,  values[3], getCellColor(structReadable, false));
      writer.writeCell(1, 4,  values[4], getCellColor(structReadable, CoreAttributeTypes.Name.getId()));
      writer.writeCell(1, 5,  values[5], getCellColor(structReadable, info.message, CoreAttributeTypes.InterfaceMessageRate.getId()));
      writer.writeCell(1, 6,  values[6], getCellColor(structReadable, CoreAttributeTypes.InterfaceMinSimultaneity.getId()));
      writer.writeCell(1, 7,  values[7], getCellColor(structReadable, CoreAttributeTypes.InterfaceMaxSimultaneity.getId()));
      writer.writeCell(1, 8,  values[8], getCellColor(structReadable, info.numElementsChanged));
      writer.writeCell(1, 9,  values[9], getCellColor(structReadable, info.structureSizeChanged));
      writer.writeCell(1, 10, values[10], getCellColor(structReadable, false));
      writer.writeCell(1, 11, values[11], getCellColor(structReadable, info.message, CoreAttributeTypes.InterfaceMessageNumber.getId()));
      writer.writeCell(1, 12, values[12], getCellColor(structReadable, info.submessage, CoreAttributeTypes.InterfaceSubMessageNumber.getId()));
      writer.writeCell(1, 13, values[13], getCellColor(structReadable, CoreAttributeTypes.InterfaceTaskFileType.getId()));
      writer.writeCell(1, 14, values[14], getCellColor(structReadable, CoreAttributeTypes.Description.getId()), CELLSTYLE.WRAP);
      writer.writeCell(1, 15, values[15], getCellColor(structReadable, false));
      //@formatter:on

      return getMaxLengthsArray(createStringLengthArray(headers), createStringLengthArray(values));
   }

   private int[] createStringLengthArray(Object[] objs) {
      int[] lengths = new int[objs.length];
      for (int i = 0; i < objs.length; i++) {
         lengths[i] = objs[i] == null ? 0 : objs[i].toString().length();
      }
      return lengths;
   }

   private int[] getMaxLengthsArray(int[] arr1, int[] arr2) {
      int[] larger = arr1.length > arr2.length ? arr1 : arr2;
      int[] res = new int[larger.length];
      for (int i = 0; i < larger.length; i++) {
         res[i] = larger[i];
      }
      for (int i = 0; i < Math.min(arr1.length, arr2.length); i++) {
         res[i] = Math.max(arr1[i], arr2[i]);
      }
      return res;
   }

   private void createStructureSheets(ExcelWorkbookWriter writer, List<InterfaceSubMessageToken> subMessages,
      SortedMap<InterfaceStructureToken, String> structureLinks) {
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
         "Notes",
         "Default Value",
         "ICDCN",};
      for (InterfaceSubMessageToken subMessage : subMessages) {

         List<InterfaceStructureToken> structs = new LinkedList<>();
         if (subMessage.getId() == 0) {
            structs.add(messageHeaderStructures.get(subMessage.getName()));
         } else {
            structs.addAll(subMessage.getArtifactReadable().getRelated(
               CoreRelationTypes.InterfaceSubMessageContent_Structure).getList().stream().map(
                  art -> new InterfaceStructureToken(art)).collect(Collectors.toList()));
         }
         for (InterfaceStructureToken struct : structs) {

            // Need to calculate the width of each column manually because Apache POI's autoSizeAllColumns is
            // relatively slow and really adds up when there are a lot of structures.
            int[] columnWidths = createStringLengthArray(elementHeaders);
            int[] resultWidths = new int[15];

            AtomicInteger rowIndex = new AtomicInteger(4); // Start elements on 4th row
            int byteLocation = 0;
            writer.createSheet(structureLinks.get(struct));

            StructureInfo info =
               struct.getId() == 0 ? headersList.get(struct.getName()) : structuresList.get(struct.getArtifactId());

            //print structure row info
            resultWidths = printFirstRowInStructureSheet(writer, struct, info);
            columnWidths = getMaxLengthsArray(columnWidths, resultWidths);

            writer.writeRow(3, elementHeaders, CELLSTYLE.BOLD);
            if (struct.getId() == 0) {
               for (InterfaceStructureElementToken element : struct.getElements()) {

                  PlatformTypeToken platformType = element.getPlatformType();
                  Integer byteSize = (int) element.getElementSizeInBytes();
                  resultWidths =
                     printHeaderStructureRow(writer, rowIndex, element, byteLocation, byteSize, platformType);
                  columnWidths = getMaxLengthsArray(columnWidths, resultWidths);
                  byteLocation = byteLocation + byteSize;
                  rowIndex.getAndAdd(1);
               }
            } else {

               for (int i = 0; i < info.elements.size(); i++) {
                  InterfaceStructureElementToken element = info.elements.get(i);
                  Integer byteSize = (int) element.getInterfacePlatformTypeByteSize();

                  resultWidths = printDataElementRow(writer, rowIndex, struct.getArtifactReadable(), element,
                     byteLocation, byteSize, i >= info.byteChangeIndex);
                  columnWidths = getMaxLengthsArray(columnWidths, resultWidths);
                  int startIndex = element.getInterfaceElementIndexStart();
                  int endIndex = element.getInterfaceElementIndexEnd();
                  if (startIndex < endIndex) {
                     byteSize = byteSize * (endIndex - startIndex + 1);
                  }
                  byteLocation = byteLocation + byteSize;
               }
            }

            for (int i = 0; i < columnWidths.length; i++) {
               writer.setColumnWidthInCharacters(i, columnWidths[i], 15000);
            }

            writer.setColumnWidthInCharacters(1, 20, 10000); // Full Sheet Name/Begin Byte
            writer.setColumnWidthInCharacters(3, 20, 10000); // Most Recent Update/Ending Word
            writer.setColumnWidth(10, Math.max(writer.getColumnWidth(10), 15000)); // Element Description
            writer.setColumnWidth(10, Math.max(writer.getColumnWidth(10), 15000)); // Element Description
            writer.setColumnWidth(12, Math.max(writer.getColumnWidth(12), 10000)); // Element Notes
            writer.setColumnWidth(14, Math.max(writer.getColumnWidth(14), 15000)); // Structure Description
         }
      }
   }

   private String getRelatedWorkFlow(Set<TransactionId> txIds) {
      String rtn = Strings.EMPTY_STRING;
      if (!releaseArtifacts.isEmpty() && !txIds.isEmpty()) {
         List<TransactionReadable> txRs =
            orcsApi.getQueryFactory().transactionQuery().andTxIds(txIds).getResults().getList();

         if (!txRs.isEmpty()) {

            List<ArtifactReadable> commitArts = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andIds(
               txRs.stream().map(a -> a.getCommitArt()).collect(Collectors.toList())).asArtifacts();

            for (ArtifactId artifactId : releaseArtifacts.keySet().stream().filter(a -> commitArts.contains(a)).collect(
               Collectors.toList())) {
               String tw = (releaseArtifacts.get(artifactId).getSecond()) != null ? releaseArtifacts.get(
                  artifactId).getSecond() : releaseArtifacts.get(artifactId).getFirst();
               rtn = rtn + "," + tw;
            }
         }
      }
      return rtn.replaceAll("^,+", "");
   }

   private int[] printHeaderStructureRow(ExcelWorkbookWriter writer, AtomicInteger rowIndex,
      InterfaceStructureElementToken element, Integer byteLocation, Integer byteSize, PlatformTypeToken platformType) {
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
      String validRange = getValidRangeString(element, platformType, dataType);

      String alterable = element.getInterfaceElementAlterable() ? "Yes" : "No";
      String description = element.getDescription() == Strings.EMPTY_STRING ? "n/a" : element.getDescription();
      String notes = element.getNotes();

      Object[] values = new Object[] {
         beginWord,
         beginByte,
         byteSize,
         endWord,
         endByte,
         dataType,
         elementName,
         units,
         validRange,
         alterable,
         description,
         "n/a",
         notes};

      writer.writeCell(rowIndex.get(), 0, values[0]);
      writer.writeCell(rowIndex.get(), 1, values[1]);
      writer.writeCell(rowIndex.get(), 2, values[2]);
      writer.writeCell(rowIndex.get(), 3, values[3]);
      writer.writeCell(rowIndex.get(), 4, values[4]);
      writer.writeCell(rowIndex.get(), 5, values[5]);
      writer.writeCell(rowIndex.get(), 6, values[6]);
      writer.writeCell(rowIndex.get(), 7, values[7]);
      writer.writeCell(rowIndex.get(), 8, values[8]);
      writer.writeCell(rowIndex.get(), 9, values[9]);
      writer.writeCell(rowIndex.get(), 10, values[10], CELLSTYLE.WRAP);
      writer.writeCell(rowIndex.get(), 11, values[11]);
      writer.writeCell(rowIndex.get(), 12, values[12]);

      this.logicalTypes.add(dataType + ";" + byteSize);
      this.units.add(units);

      return createStringLengthArray(values);
   }

   private int[] printDataElementRow(ExcelWorkbookWriter writer, AtomicInteger rowIndex, ArtifactReadable structure,
      InterfaceStructureElementToken elementToken, Integer byteLocation, Integer byteSize, boolean bytesChanged) {

      Set<TransactionId> txIds = new HashSet<>();
      PlatformTypeToken platformType = elementToken.getPlatformType();
      Integer beginWord = Math.floorDiv(byteLocation, 4);
      Integer beginByte = Math.floorMod(byteLocation, 4);
      Integer endWord = Math.floorDiv(byteLocation + byteSize - 1, 4);
      Integer endByte = Math.floorMod(byteLocation + byteSize - 1, 4);
      String enumLiterals = elementToken.getEnumLiteral();
      String dataType = elementToken.getLogicalType().isEmpty() ? "n/a" : elementToken.getLogicalType();
      dataType = dataType.replace("unsigned long", "uLong").replace("unsigned short", "uShort").replace("short",
         "sShort").replace("unsigned integer", "uInteger").replace("integer", "sInteger");
      String units = elementToken.getUnits().isEmpty() ? "n/a" : elementToken.getUnits();
      Integer startIndex = elementToken.getInterfaceElementIndexStart();
      Integer endIndex = elementToken.getInterfaceElementIndexEnd();
      String validRange = getValidRangeString(elementToken, platformType, dataType);

      String alterable = elementToken.getInterfaceElementAlterable() ? "Yes" : "No";

      String description = elementToken.getDescription().isEmpty() ? "n/a" : elementToken.getDescription();
      String notes = elementToken.getNotes().isEmpty() ? "" : elementToken.getNotes();
      String defaultValue = elementToken.getInterfaceDefaultValue();
      boolean enumChanged = false;
      if (diffs.containsKey(platformType.getArtifactId())) {
         txIds.addAll(diffs.get(platformType.getArtifactId()).getAllTxIds());
      }
      if (platformType != null && dataType.equals("enumeration")) {
         ArtifactReadable enumSet = platformType.getArtifactReadable().getRelated(
            CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet).getAtMostOneOrDefault(
               ArtifactReadable.SENTINEL);

         enumLiterals = enumSet.getName() + ":\n";
         enumChanged = enumChanged || diffs.containsKey(enumSet.getArtifactId());
         if (diffs.containsKey(enumSet.getArtifactId())) {
            txIds.addAll(diffs.get(enumSet.getArtifactId()).getAllTxIds());
         }

         for (ArtifactReadable enumState : enumSet.getRelated(
            CoreRelationTypes.InterfaceEnumeration_EnumerationState).getList()) {
            enumLiterals = enumLiterals + enumState.getSoleAttributeAsString(CoreAttributeTypes.InterfaceEnumOrdinal,
               "0") + " = " + enumState.getSoleAttributeAsString(CoreAttributeTypes.Name, "") + "\n";
            enumChanged = enumChanged || diffs.containsKey(enumState.getArtifactId());
            if (diffs.containsKey(enumState.getArtifactId())) {
               txIds.addAll(diffs.get(enumState.getArtifactId()).getAllTxIds());
            }
         }
      }
      if ((dataType.equals("uInteger") || dataType.equals("sInteger")) && enumLiterals.isEmpty()) {
         enumLiterals = "n/a";
      }

      MimChangeSummaryItem structDiff = diffs.get(structure.getArtifactId());
      Optional<MimChangeSummaryItem> elementDiff =
         structDiff == null ? Optional.empty() : structDiff.getChild(elementToken.getArtifactId());
      boolean elementAdded = elementDiff.isPresent() && elementDiff.get().isAdded();
      boolean platformTypeChanged = elementDiff.isPresent() && !elementDiff.get().getRelationChanges(
         CoreRelationTypes.InterfaceElementPlatformType.getId()).isEmpty();
      CELLSTYLE pTypeStyle = elementAdded || getCellColor(elementToken.getArtifactReadable(), false).equals(
         CELLSTYLE.GREEN) ? CELLSTYLE.GREEN : platformTypeChanged ? CELLSTYLE.YELLOW : CELLSTYLE.NONE;

      CELLSTYLE enumStyle = elementAdded || getCellColor(elementToken.getArtifactReadable(), false).equals(
         CELLSTYLE.GREEN) ? CELLSTYLE.GREEN : enumChanged || platformTypeChanged ? CELLSTYLE.YELLOW : CELLSTYLE.NONE;

      CELLSTYLE byteStyle = elementAdded || getCellColor(elementToken.getArtifactReadable(), false).equals(
         CELLSTYLE.GREEN) ? CELLSTYLE.GREEN : bytesChanged ? CELLSTYLE.YELLOW : CELLSTYLE.NONE;
      String icdcns = "";
      if (elementDiff.isPresent()) {
         txIds.addAll(elementDiff.get().getAllTxIds());
      }
      icdcns = getRelatedWorkFlow(txIds);
      Object[] values = new Object[] {
         beginWord, // 0
         beginByte, // 1
         byteSize, // 2
         endWord, // 3
         endByte, // 4
         dataType, // 5
         elementToken.getName(), // 6
         units, // 7
         validRange, // 8
         alterable, // 9
         description, // 10
         enumLiterals.trim(), // 11
         notes, // 12
         defaultValue, //13
         icdcns}; // 14

      if (startIndex < endIndex) {
         for (int i = startIndex; i < endIndex + 1; i++) {
            boolean arrayElementAdded =
               elementAdded || (elementDiff.isPresent() && isNewArrayElement(elementDiff.get(), i));
            CELLSTYLE arrByteStyle = arrayElementAdded ? CELLSTYLE.GREEN : byteStyle;
            CELLSTYLE arrPTypeStyle = arrayElementAdded ? CELLSTYLE.GREEN : pTypeStyle;
            CELLSTYLE arrEnumStyle = arrayElementAdded ? CELLSTYLE.GREEN : enumStyle;

            values[0] = beginWord;
            values[1] = beginByte;
            values[3] = endWord;
            values[4] = endByte;

            // elementToken inherited header information from its parent up in createStructureInfo() if applicable.
            // These values are not set on these elements naturally.
            String elementName = elementToken.getName();
            if (elementToken.getInterfaceElementArrayHeader() && !elementToken.getInterfaceElementWriteArrayHeaderName()) {
               String[] split = elementToken.getName().split(" ");
               String elementIndex = split[split.length - 1];
               if (Strings.isNumeric(elementIndex)) {
                  elementName = elementName.substring(0, elementName.length() - (elementIndex.length() + 1)).trim();
                  String delimiter1 = elementToken.getInterfaceElementArrayIndexDelimiterOne();
                  String delimiter2 = elementToken.getInterfaceElementArrayIndexDelimiterTwo();
                  if (elementToken.getInterfaceElementArrayIndexOrder().equals(
                     ElementArrayIndexOrder.INNER_OUTER.toString())) {
                     elementName += delimiter1 + i + delimiter2 + elementIndex;
                  } else {
                     elementName += delimiter1 + elementIndex + delimiter2 + i;
                  }
               }
            } else {
               elementName += " " + i;
            }

            values[6] = elementName;

            //@formatter:off
            writer.writeCell(rowIndex.get(), 0, values[0], arrByteStyle);
            writer.writeCell(rowIndex.get(), 1, values[1], arrByteStyle);
            writer.writeCell(rowIndex.get(), 2, values[2], arrByteStyle);
            writer.writeCell(rowIndex.get(), 3, values[3], arrByteStyle);
            writer.writeCell(rowIndex.get(), 4, values[4], arrByteStyle);
            writer.writeCell(rowIndex.get(), 5, values[5], arrPTypeStyle);
            writer.writeCell(rowIndex.get(), 6, values[6], getStructureCellColor(structure, elementToken.getArtifactReadable(), CoreAttributeTypes.Name.getId(), i));
            writer.writeCell(rowIndex.get(), 7, values[7], arrPTypeStyle.equals(CELLSTYLE.GREEN) || arrPTypeStyle.equals(CELLSTYLE.YELLOW) ? arrPTypeStyle : getStructureCellColor(structure,elementToken.getArtifactReadable(), CoreAttributeTypes.InterfacePlatformTypeUnits.getId(), i));
            writer.writeCell(rowIndex.get(), 8, values[8], arrPTypeStyle.equals(CELLSTYLE.GREEN) || arrPTypeStyle.equals(CELLSTYLE.YELLOW) ? arrPTypeStyle : getStructureCellColor(structure,elementToken.getArtifactReadable(), CoreAttributeTypes.InterfacePlatformTypeMinval.getId(), i).equals(CELLSTYLE.YELLOW) ? CELLSTYLE.YELLOW : getStructureCellColor(structure,elementToken.getArtifactReadable(), CoreAttributeTypes.InterfacePlatformTypeMaxval.getId(), i));
            writer.writeCell(rowIndex.get(), 9, values[9], getStructureCellColor(structure,elementToken.getArtifactReadable(), CoreAttributeTypes.InterfaceElementAlterable.getId(), i));
            writer.writeCell(rowIndex.get(), 10, values[10], getStructureCellColor(structure,elementToken.getArtifactReadable(), CoreAttributeTypes.Description.getId(), i), CELLSTYLE.WRAP);
            writer.writeCell(rowIndex.get(), 11, values[11], arrEnumStyle, CELLSTYLE.WRAP);
            writer.writeCell(rowIndex.get(), 12, values[12], getStructureCellColor(structure,elementToken.getArtifactReadable(), CoreAttributeTypes.Notes.getId(), i), CELLSTYLE.WRAP);
            writer.writeCell(rowIndex.get(), 13, values[13], getStructureCellColor(structure,elementToken.getArtifactReadable(), CoreAttributeTypes.InterfaceDefaultValue.getId(), i));
            writer.writeCell(rowIndex.get(), 14, values[14]);
            //@formatter:on

            byteLocation = byteLocation + byteSize;
            beginWord = Math.floorDiv(byteLocation, 4);
            beginByte = Math.floorMod(byteLocation, 4);
            endWord = Math.floorDiv(byteLocation + byteSize - 1, 4);
            endByte = Math.floorMod(byteLocation + byteSize - 1, 4);

            rowIndex.getAndAdd(1);
         }
      } else {
         //@formatter:off
         writer.writeCell(rowIndex.get(), 0, values[0], byteStyle);
         writer.writeCell(rowIndex.get(), 1, values[1], byteStyle);
         writer.writeCell(rowIndex.get(), 2, values[2], byteStyle);
         writer.writeCell(rowIndex.get(), 3, values[3], byteStyle);
         writer.writeCell(rowIndex.get(), 4, values[4], byteStyle);
         writer.writeCell(rowIndex.get(), 5, values[5], pTypeStyle);
         writer.writeCell(rowIndex.get(), 6, values[6], getStructureCellColor(structure,elementToken.getArtifactReadable(), CoreAttributeTypes.Name.getId(), 0));
         writer.writeCell(rowIndex.get(), 7, values[7], pTypeStyle.equals(CELLSTYLE.GREEN) || pTypeStyle.equals(CELLSTYLE.YELLOW) ? pTypeStyle : getStructureCellColor(structure,elementToken.getArtifactReadable(), CoreAttributeTypes.InterfacePlatformTypeUnits.getId(), 0));
         writer.writeCell(rowIndex.get(), 8, values[8], pTypeStyle.equals(CELLSTYLE.GREEN) || pTypeStyle.equals(CELLSTYLE.YELLOW) ? pTypeStyle : getStructureCellColor(structure,elementToken.getArtifactReadable(), CoreAttributeTypes.InterfacePlatformTypeMinval.getId(), 0).equals(CELLSTYLE.YELLOW) ? CELLSTYLE.YELLOW : getStructureCellColor(structure,elementToken.getArtifactReadable(), CoreAttributeTypes.InterfacePlatformTypeMaxval.getId(), 0));
         writer.writeCell(rowIndex.get(), 9, values[9], getStructureCellColor(structure,elementToken.getArtifactReadable(), CoreAttributeTypes.InterfaceElementAlterable.getId(), 0));
         writer.writeCell(rowIndex.get(), 10, values[10], getStructureCellColor(structure,elementToken.getArtifactReadable(), CoreAttributeTypes.Description.getId(), 0), CELLSTYLE.WRAP);
         writer.writeCell(rowIndex.get(), 11, values[11], enumStyle, CELLSTYLE.WRAP);
         writer.writeCell(rowIndex.get(), 12, values[12], getStructureCellColor(structure,elementToken.getArtifactReadable(), CoreAttributeTypes.Notes.getId(), 0), CELLSTYLE.WRAP);
         writer.writeCell(rowIndex.get(), 13, values[13], getStructureCellColor(structure,elementToken.getArtifactReadable(), CoreAttributeTypes.InterfaceDefaultValue.getId(), 0));
         writer.writeCell(rowIndex.get(), 14, values[14]);
         //@formatter:on
         rowIndex.getAndAdd(1);
      }

      if (logicalTypeMaxRange.containsKey(dataType)) { // Needed to prevent unwanted logical types making it into the table...
         this.logicalTypes.add(dataType + ";" + byteSize);
      }
      this.units.add(units);

      return createStringLengthArray(values);
   }

   private String getValidRangeString(InterfaceStructureElementToken elementToken, PlatformTypeToken platformType,
      String dataType) {
      String minVal =
         elementToken.getInterfacePlatformTypeMinval().isEmpty() ? "n/a" : elementToken.getInterfacePlatformTypeMinval();
      String maxVal =
         elementToken.getInterfacePlatformTypeMaxval().isEmpty() ? "n/a" : elementToken.getInterfacePlatformTypeMaxval();
      String validRange = "";
      if (dataType.equals("enumeration")) {
         validRange = "see enumerated literals";
      } else if (platformType != null) {
         if (minVal.equals(maxVal)) {
            validRange = minVal;
         } else if (maxVal.equals("n/a")) {
            validRange = minVal;
         } else if (minVal.equals("n/a")) {
            validRange = maxVal;
         } else {
            validRange = minVal + " to " + maxVal;
         }
         if ((validRange.isEmpty() || validRange.equals(
            "n/a")) && !platformType.getInterfacePlatformTypeValidRangeDescription().isEmpty()) {
            validRange = platformType.getInterfacePlatformTypeValidRangeDescription();
         }
      }
      return validRange;
   }

   private void createMessageSubMessageSummary(ExcelWorkbookWriter writer, BranchId branch, ArtifactId view,
      ArtifactReadable connection, ArtifactReadable primaryNode, ArtifactReadable secondaryNode,
      List<ArtifactReadable> messages, List<ArtifactReadable> subMessages) {
      List<ArtifactReadable> connectionMessages = messages.stream().filter(
         e -> !(e.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageType)).equals("Connection")).collect(
            Collectors.toList());
      List<ArtifactReadable> primaryList = connectionMessages.stream().filter(
         e -> primaryNode.equals(e.getRelated(CoreRelationTypes.InterfaceMessagePubNode_Node).getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL))).collect(Collectors.toList());

      List<ArtifactReadable> secondaryList = connectionMessages.stream().filter(
         e -> secondaryNode.equals(e.getRelated(CoreRelationTypes.InterfaceMessagePubNode_Node).getAtMostOneOrDefault(
            ArtifactReadable.SENTINEL))).collect(Collectors.toList());

      writer.createSheet("Message and SubMessage Summary");

      writer.addMergedRegion("A1:E1");
      writer.addMergedRegion("F1:J1");
      writer.addMergedRegion("D2:E2");
      writer.addMergedRegion("I2:J2");
      writer.setRowHeight(0, 300);

      writer.writeCell(0, 0, primaryNode.getName() + " Initiated Message", CELLSTYLE.BOLD, CELLSTYLE.CENTERH,
         CELLSTYLE.CENTERV);
      writer.writeCell(0, 5, secondaryNode.getName() + " Initiated Message", CELLSTYLE.BOLD, CELLSTYLE.CENTERH,
         CELLSTYLE.CENTERV);

      String[] rowValues = {"No.", "Rate", "Read/Write", "Content", "", "No.", "Rate", "Read/Write", "Content"};
      writer.writeRow(1, rowValues, CELLSTYLE.BOLD, CELLSTYLE.CENTERH, CELLSTYLE.CENTERV);

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

         if (primaryMessage.isValid()) {
            writer.writeCell(rowIndex, 0,
               primaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageNumber), CELLSTYLE.CENTERH,
               CELLSTYLE.CENTERV, getCellColor(primaryMessage, CoreAttributeTypes.InterfaceMessageNumber.getId()));
            writer.writeCell(rowIndex, 1,
               primaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate, "Aperiodic"),
               CELLSTYLE.CENTERH, CELLSTYLE.CENTERV,
               getCellColor(primaryMessage, CoreAttributeTypes.InterfaceMessageRate.getId()));
            writer.writeCell(rowIndex, 2,
               primaryMessage.getSoleAttributeValue(CoreAttributeTypes.InterfaceMessageWriteAccess, false) ? "W" : "R",
               CELLSTYLE.CENTERH, CELLSTYLE.CENTERV,
               getCellColor(primaryMessage, CoreAttributeTypes.InterfaceMessageWriteAccess.getId()));
            writer.writeCell(rowIndex, 3, primaryMessage.getName(),
               getCellColor(primaryMessage, CoreAttributeTypes.Name.getId()), CELLSTYLE.BOLD);
            writer.addMergedRegion(rowIndex, rowIndex, 3, 4);
         }
         if (secondaryMessage.isValid()) {
            writer.writeCell(rowIndex, 5,
               secondaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageNumber), CELLSTYLE.CENTERH,
               CELLSTYLE.CENTERV, getCellColor(secondaryMessage, CoreAttributeTypes.InterfaceMessageNumber.getId()));
            writer.writeCell(rowIndex, 6,
               secondaryMessage.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate, "Aperiodic"),
               CELLSTYLE.CENTERH, CELLSTYLE.CENTERV,
               getCellColor(secondaryMessage, CoreAttributeTypes.InterfaceMessageRate.getId()));
            writer.writeCell(rowIndex, 7,
               secondaryMessage.getSoleAttributeValue(CoreAttributeTypes.InterfaceMessageWriteAccess,
                  false) ? "W" : "R",
               CELLSTYLE.CENTERH, CELLSTYLE.CENTERV,
               getCellColor(secondaryMessage, CoreAttributeTypes.InterfaceMessageWriteAccess.getId()));
            writer.writeCell(rowIndex, 8, secondaryMessage.getName(),
               getCellColor(secondaryMessage, CoreAttributeTypes.Name.getId()), CELLSTYLE.BOLD);
            writer.addMergedRegion(rowIndex, rowIndex, 8, 9);
         }
         rowIndex++;

         if (primaryMessage.isValid()) {
            writer.writeCell(rowIndex, 3, "Software Interface Header", getCellColor(primaryMessage, false));
            writer.addMergedRegion(rowIndex, rowIndex, 3, 4);
         }
         if (secondaryMessage.isValid()) {
            writer.writeCell(rowIndex, 8, "Software Interface Header", getCellColor(secondaryMessage, false));
            writer.addMergedRegion(rowIndex, rowIndex, 8, 9);
         }
         rowIndex++;

         int numSubMessages = Math.max(primarySubMessages.size(), secondarySubMessages.size());
         for (int j = 0; j < numSubMessages; j++) {
            if (j < primarySubMessages.size()) {
               ArtifactReadable subMessage = primarySubMessages.get(j);
               writer.writeCell(rowIndex, 3,
                  "Submessage " + subMessage.getSoleAttributeAsString(
                     CoreAttributeTypes.InterfaceSubMessageNumber) + ":",
                  getCellColor(subMessage, CoreAttributeTypes.Name.getId()));
               writer.writeCell(rowIndex, 4, subMessage.getName(),
                  getCellColor(subMessage, CoreAttributeTypes.Name.getId()));
            }
            if (j < secondarySubMessages.size()) {
               ArtifactReadable subMessage = secondarySubMessages.get(j);
               writer.writeCell(rowIndex, 8,
                  "Submessage " + subMessage.getSoleAttributeAsString(
                     CoreAttributeTypes.InterfaceSubMessageNumber) + ":",
                  getCellColor(subMessage, CoreAttributeTypes.Name.getId()));
               writer.writeCell(rowIndex, 9, subMessage.getName(),
                  getCellColor(subMessage, CoreAttributeTypes.Name.getId()));
            }
            rowIndex++;
         }

         if (primaryMessage.isValid()) {
            writer.addMergedRegion(rowIndex - numSubMessages - 2, rowIndex - 1, 0, 0);
            writer.addMergedRegion(rowIndex - numSubMessages - 2, rowIndex - 1, 1, 1);
            writer.addMergedRegion(rowIndex - numSubMessages - 2, rowIndex - 1, 2, 2);
         }
         if (secondaryMessage.isValid()) {
            writer.addMergedRegion(rowIndex - numSubMessages - 2, rowIndex - 1, 5, 5);
            writer.addMergedRegion(rowIndex - numSubMessages - 2, rowIndex - 1, 6, 6);
            writer.addMergedRegion(rowIndex - numSubMessages - 2, rowIndex - 1, 7, 7);
         }

      }

      // Auto size all column widths, then set individual widths
      writer.autoSizeAllColumns(10);
      writer.setColumnWidth(0, 3000);
      writer.setColumnWidth(1, 3000);
      writer.setColumnWidth(4, writer.getColumnWidth(4) + 1200);
      writer.setColumnWidth(5, 3000);
      writer.setColumnWidth(6, 3000);
      writer.setColumnWidth(8, writer.getColumnWidth(8) + 1200);
   }

   private void createUnitsAndTypesSheet(ExcelWorkbookWriter writer) {
      writer.createSheet("Units and Types");
   }

   private void writeUnitsAndTypesSheet(ExcelWorkbookWriter writer, BranchId branch, ArtifactId view,
      InterfaceNode primaryNode, InterfaceNode secondaryNode) {
      // Sort logical types by byte size
      List<String> logicalTypes = new LinkedList<>(this.logicalTypes);
      logicalTypes.sort(new Comparator<String>() {
         @Override
         public int compare(String o1, String o2) {
            String[] split1 = o1.split(";");
            String[] split2 = o2.split(";");
            Integer byteSize1 = Integer.parseInt(split1[1]);
            Integer byteSize2 = Integer.parseInt(split2[1]);
            if (byteSize1.equals(byteSize2)) {
               return split1[0].compareTo(split2[0]);
            }
            return byteSize1.compareTo(byteSize2);
         }
      });

      List<String> units = new LinkedList<>(this.units);
      units.sort(Comparator.comparing(String::toString));
      if (units.remove("n/a")) {
         units.add(0, "n/a");
      }

      List<ArtifactReadable> unitArts = this.orcsApi.getQueryFactory().fromBranch(branch, view).andIsOfType(
         CoreArtifactTypes.InterfaceUnit).asArtifacts();
      Map<String, String> unitsToMeasurementType = new HashMap<>();
      unitArts.stream().forEach(art -> {
         if (units.contains(art.getName())) {
            unitsToMeasurementType.put(art.getName(),
               art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceUnitMeasurement, ""));
         }
      });

      // Async rates go first
      List<String> txRates = new LinkedList<>(this.txRates);
      txRates.sort(new Comparator<String>() {
         @Override
         public int compare(String o1, String o2) {
            String[] split1 = o1.split("-");
            String[] split2 = o2.split("-");
            Integer rate1 = Integer.parseInt(split1[0]);
            Integer rate2 = Integer.parseInt(split2[0]);
            if (split1.length > 1 && split2.length == 1) {
               return -1;
            }
            if (split2.length > 1 && split1.length == 1) {
               return 1;
            }
            return rate1.compareTo(rate2);
         }
      });

      List<String> categories = new LinkedList<>(this.categories);
      categories.sort(Comparator.comparing(String::toString));

      writer.setActiveSheet("Units and Types");

      String[] headers = {"Sheet Type", "Full Sheet Name", "Abbrev. Sheet Name"};

      writer.writeRow(0, headers, CELLSTYLE.BOLD, CELLSTYLE.BORDER_ALL);
      writer.writeCell(1, 0, "Admin", CELLSTYLE.BORDER_ALL);
      writer.writeCell(1, 1, "Units and Types", CELLSTYLE.BORDER_ALL);
      writer.writeCell(1, 2, "Units and Types", CELLSTYLE.BORDER_ALL);

      // Units Table
      writer.addMergedRegion(4, 4, 0, 2);
      writer.writeCell(4, 0, "Unit Name and Size", CELLSTYLE.BOLD, CELLSTYLE.CENTERH, CELLSTYLE.BORDER_ALL);
      writer.writeCell(4, 1, "", CELLSTYLE.BORDER_ALL);
      writer.writeCell(4, 2, "", CELLSTYLE.BORDER_ALL);
      writer.writeCell(5, 0, "Data Types", CELLSTYLE.BOLD, CELLSTYLE.BORDER_ALL);
      writer.writeCell(5, 1, "Size in Bytes", CELLSTYLE.BOLD, CELLSTYLE.BORDER_ALL);
      writer.writeCell(5, 2, "Maximum Valid Range", CELLSTYLE.BOLD, CELLSTYLE.BORDER_ALL);

      int rowIndex = 6;
      for (String logicalType : logicalTypes) {
         String[] typeSplit = logicalType.split(";");
         writer.writeCell(rowIndex, 0, typeSplit[0], CELLSTYLE.BORDER_ALL, CELLSTYLE.BORDER_TOP);
         writer.writeCell(rowIndex, 1, typeSplit[1], CELLSTYLE.BORDER_ALL);
         writer.writeCell(rowIndex, 2, this.logicalTypeMaxRange.getOrDefault(typeSplit[0], ""), CELLSTYLE.BORDER_ALL);
         rowIndex++;
      }

      // Measurements Table
      writer.writeCell(4, 4, "Measurement", CELLSTYLE.BOLD, CELLSTYLE.BORDER_ALL);
      writer.writeCell(4, 5, "Units", CELLSTYLE.BOLD, CELLSTYLE.BORDER_ALL);
      rowIndex = 5;
      for (String unit : units) {
         writer.writeCell(rowIndex, 4, unitsToMeasurementType.get(unit), CELLSTYLE.BORDER_ALL);
         writer.writeCell(rowIndex, 5, unit, CELLSTYLE.BORDER_ALL);
         rowIndex++;
      }

      // Category Table
      writer.writeCell(4, 7, "Category", CELLSTYLE.BOLD, CELLSTYLE.BORDER_ALL);
      rowIndex = 5;
      for (String category : categories) {
         writer.writeCell(rowIndex, 7, category, CELLSTYLE.BORDER_ALL);
         rowIndex++;
      }

      // Tx Rate Table
      writer.writeCell(4, 9, "Transmission Rate (Hz)", CELLSTYLE.BOLD, CELLSTYLE.BORDER_ALL);
      rowIndex = 5;
      for (String rate : txRates) {
         writer.writeCell(rowIndex, 9, rate, CELLSTYLE.CENTERH, CELLSTYLE.BORDER_ALL);
         rowIndex++;
      }

      // Systems Table
      writer.writeCell(4, 11, "Systems", CELLSTYLE.BOLD, CELLSTYLE.CENTERH, CELLSTYLE.BORDER_ALL);
      writer.writeCell(5, 11, primaryNode.getName(), CELLSTYLE.CENTERH, CELLSTYLE.BORDER_ALL);
      writer.writeCell(6, 11, secondaryNode.getName(), CELLSTYLE.CENTERH, CELLSTYLE.BORDER_ALL);

      // Alterable After Table
      writer.writeCell(4, 13, "Alterable After", CELLSTYLE.BOLD, CELLSTYLE.CENTERH, CELLSTYLE.BORDER_ALL);
      writer.writeCell(5, 13, "Yes", CELLSTYLE.CENTERH, CELLSTYLE.BORDER_ALL);
      writer.writeCell(6, 13, "No", CELLSTYLE.CENTERH, CELLSTYLE.BORDER_ALL);

      writer.autoSizeAllColumns(16);
      writer.setColumnWidth(0, writer.getColumnWidth(0) + 500);
      writer.setColumnWidth(1, writer.getColumnWidth(1) + 500);
      writer.setColumnWidth(2, writer.getColumnWidth(2) + 500);
      writer.setColumnWidth(4, writer.getColumnWidth(4) + 500);
      writer.setColumnWidth(5, writer.getColumnWidth(5) + 500);
      writer.setColumnWidth(7, writer.getColumnWidth(7) + 500);

   }

   private CELLSTYLE getCellColor(ArtifactReadable artifact, boolean changed) {
      MimChangeSummaryItem diffItem = artifact == null ? null : diffs.get(artifact.getArtifactId());
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
      MimChangeSummaryItem rowDiffItem = rowArtifact == null ? null : diffs.get(rowArtifact.getArtifactId());
      MimChangeSummaryItem cellDiffItem = cellArtifact == null ? null : diffs.get(cellArtifact.getArtifactId());
      return getCellColor(rowDiffItem, cellDiffItem, attrId);
   }

   private CELLSTYLE getCellColor(MimChangeSummaryItem rowChange, MimChangeSummaryItem cellChange, Long attrId) {
      if (rowChange != null && rowChange.isAdded()) {
         return CELLSTYLE.GREEN;
      }

      CELLSTYLE color = CELLSTYLE.NONE;
      MimChangeSummaryItem diffItem = rowChange != null && rowChange.isAdded() ? rowChange : cellChange;
      if (diffItem != null) {
         color = diffItem.isAdded() ? CELLSTYLE.GREEN : diffItem.hasAttributeChanges(
            attrId) ? CELLSTYLE.YELLOW : CELLSTYLE.NONE;
      }
      return color;
   }

   private CELLSTYLE getStructureCellColor(ArtifactReadable structure, ArtifactReadable element, Long attrId,
      int arrayIndex) {
      MimChangeSummaryItem structureDiffItem = structure == null ? null : diffs.get(structure.getArtifactId());
      Optional<MimChangeSummaryItem> elementDiffItem =
         structureDiffItem == null ? Optional.empty() : structureDiffItem.getChild(element.getArtifactId());

      if (structureDiffItem == null || elementDiffItem.isEmpty()) {
         return CELLSTYLE.NONE;
      }

      // Case where an array was resized
      if (elementDiffItem.isPresent() && isNewArrayElement(elementDiffItem.get(), arrayIndex)) {
         return CELLSTYLE.GREEN;
      }

      return getCellColor(structureDiffItem, elementDiffItem.get(), attrId);
   }

   private boolean isNewArrayElement(MimChangeSummaryItem elementDiff, int arrayIndex) {
      List<ChangeReportRowDto> attributeChanges =
         elementDiff.getAttributeChanges(CoreAttributeTypes.InterfaceElementIndexStart.getId());
      if (attributeChanges.size() > 0) {
         ChangeReportRowDto change = attributeChanges.get(0);
         if (change.getChangeType().equals("Modified") && arrayIndex < Integer.parseInt(change.getWasValue())) {
            return true;
         }
      }
      attributeChanges = elementDiff.getAttributeChanges(CoreAttributeTypes.InterfaceElementIndexEnd.getId());
      if (attributeChanges.size() > 0) {
         ChangeReportRowDto change = attributeChanges.get(0);
         if (change.getChangeType().equals("Modified") && arrayIndex > Integer.parseInt(change.getWasValue())) {
            return true;
         }
      }
      return false;
   }

   private class StructureInfo {
      final String name;
      final String nameAbbrev;
      final String category;
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
      final List<InterfaceStructureElementToken> elements;
      final boolean structureChanged;
      final boolean txRateChanged;
      final boolean numElementsChanged;
      final boolean structureSizeChanged;
      final int byteChangeIndex;
      final String icdcn;
      public StructureInfo(String name, String nameAbbrev, String cat, String msgRateText, String minSim, String maxSim, String minBps, String maxBps, Integer elementCount, Integer sizeInBytes, String sendingNode, String msgNumber, String subMsgNumber, String taskfile, String desc, ArtifactReadable message, ArtifactReadable submessage, List<InterfaceStructureElementToken> elements, boolean structureChanged, boolean txRateChanged, boolean numElementsChanged, boolean structureSizeChanged, int byteChangeIndex, String icdcn) {
         this.name = name;
         this.nameAbbrev = nameAbbrev;
         this.category = cat;
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
         this.icdcn = icdcn;
      }

   }
}
