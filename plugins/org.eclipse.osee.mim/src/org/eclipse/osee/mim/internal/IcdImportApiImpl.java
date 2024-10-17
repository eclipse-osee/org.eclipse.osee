/*********************************************************************
 * Copyright (c) 2022 Boeing
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

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.io.excel.ExcelWorkbookReader;
import org.eclipse.osee.framework.jdk.core.util.io.excel.ExcelWorkbookWriter.WorkbookFormat;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.MimImportApi;
import org.eclipse.osee.mim.types.IcdElementIndex;
import org.eclipse.osee.mim.types.InterfaceConnection;
import org.eclipse.osee.mim.types.InterfaceEnumOrdinalType;
import org.eclipse.osee.mim.types.InterfaceEnumeration;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceNode;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.mim.types.MimImportSummary;
import org.eclipse.osee.mim.types.PlatformTypeToken;
import org.eclipse.osee.mim.types.TransportType;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Ryan T. Baldwin
 */
public class IcdImportApiImpl implements MimImportApi {

   private final ExcelWorkbookReader reader;
   private final MimApi mimApi;
   private final BranchId branch;
   private final ArtifactId transportTypeId;
   private MimImportSummary summary;

   private Long id = 1L;

   public IcdImportApiImpl(BranchId branch, ArtifactId transportTypeId, String fileName, InputStream inputStream, MimApi mimApi) {
      this.reader = new ExcelWorkbookReader(inputStream,
         fileName != null && fileName.toLowerCase().endsWith("xls") ? WorkbookFormat.XLS : WorkbookFormat.XLSX);
      this.mimApi = mimApi;
      this.branch = branch;
      this.transportTypeId = transportTypeId == null ? ArtifactId.SENTINEL : transportTypeId;
   }

   @Override
   public MimImportSummary getSummary() {
      summary = new MimImportSummary();

      reader.setActiveSheet("Message and Submessage Summary");

      String primaryNodeName = reader.getCellStringValue(0, 0).split(" ")[0];
      String secondaryNodeName = reader.getCellStringValue(0, 5).split(" ")[0];

      List<InterfaceNode> existingNodes = (List<InterfaceNode>) mimApi.getInterfaceNodeViewApi().getAll(branch);
      InterfaceNode existingPrimaryNode =
         existingNodes.stream().filter(node -> node.getName().getValue().equals(primaryNodeName)).findFirst().orElse(
            InterfaceNode.SENTINEL);
      InterfaceNode existingSecondaryNode =
         existingNodes.stream().filter(node -> node.getName().getValue().equals(secondaryNodeName)).findFirst().orElse(
            InterfaceNode.SENTINEL);
      InterfaceNode primaryNode;
      InterfaceNode secondaryNode;
      if (existingPrimaryNode.isValid()) {
         primaryNode = existingPrimaryNode;
      } else {
         primaryNode = new InterfaceNode(id, primaryNodeName);
         primaryNode.setApplicability(ApplicabilityToken.BASE);
         primaryNode.setInterfaceNodeAddress("");
         primaryNode.setInterfaceNodeBackgroundColor("");
         primaryNode.setDescription("");
         incrementId();
         summary.getNodes().add(primaryNode);
      }
      if (existingSecondaryNode.isValid()) {
         secondaryNode = existingSecondaryNode;
      } else {
         secondaryNode = new InterfaceNode(id, secondaryNodeName);
         secondaryNode.setApplicability(ApplicabilityToken.BASE);
         secondaryNode.setInterfaceNodeAddress("");
         secondaryNode.setInterfaceNodeBackgroundColor("");
         secondaryNode.setDescription("");
         incrementId();
         summary.getNodes().add(secondaryNode);
      }

      InterfaceConnection connection =
         mimApi.getInterfaceConnectionViewApi().getAll(branch).stream().filter(c -> c.getNodes().stream().filter(
            n -> n.getId().equals(existingPrimaryNode.getId())).findAny().isPresent() && c.getNodes().stream().filter(
               n -> n.getId().equals(existingSecondaryNode.getId())).findAny().isPresent()).findFirst().orElse(
                  InterfaceConnection.SENTINEL);
      if (!connection.isValid()) {
         connection = new InterfaceConnection(id, primaryNodeName + "_" + secondaryNodeName);
         incrementId();
         connection.setApplicability(ApplicabilityToken.BASE);
         connection.getNodes().add(primaryNode);
         connection.getNodes().add(secondaryNode);
         summary.getConnections().add(connection);
         List<String> rels =
            summary.getConnectionNodeRelations().getOrDefault(connection.getIdString(), new LinkedList<>());
         rels.add(primaryNode.getIdString());
         rels.add(secondaryNode.getIdString());
         summary.getConnectionNodeRelations().put(connection.getIdString(), rels);
      }

      TransportType transportType = mimApi.getTransportTypeApi().get(branch, transportTypeId);

      if (transportType.isInvalid()) {
         summary.getErrors().add("Invalid Transport Type ID");
         return summary;
      }
      List<String> rels =
         summary.getConnectionTransportTypeRelations().getOrDefault(connection.getIdString(), new LinkedList<>());
      rels.add(transportType.getIdString());
      summary.getConnectionTransportTypeRelations().put(connection.getIdString(), rels);

      // Messages and SubMessages
      List<String> primaryRegions = reader.getMergedRegions().stream().filter(
         r -> r.split(":")[0].charAt(0) == 'A' && r.split(":")[1].charAt(0) == 'A').collect(Collectors.toList());
      List<String> secondaryRegions = reader.getMergedRegions().stream().filter(
         r -> r.split(":")[0].charAt(0) == 'F' && r.split(":")[1].charAt(0) == 'F').collect(Collectors.toList());

      Map<String, Long> submessageIds = new HashMap<>();
      for (String region : primaryRegions) {
         String[] split = region.split(":");
         int start = Integer.parseInt(split[0].substring(1)) - 1; // Subtract 1 due to 0-based indexing
         int end = Integer.parseInt(split[1].substring(1)) - 1;
         readMessage(primaryNode, secondaryNode, start, end, 0, submessageIds);
      }

      for (String region : secondaryRegions) {
         String[] split = region.split(":");
         int start = Integer.parseInt(split[0].substring(1)) - 1;
         int end = Integer.parseInt(split[1].substring(1)) - 1;
         readMessage(secondaryNode, primaryNode, start, end, 5, submessageIds);
      }

      // Structures and Elements
      reader.setActiveSheet("Structure Summary");
      List<String> structureSheetNames = new LinkedList<>();
      int rowIndex = 1;
      while (reader.rowExists(rowIndex)) {
         String nameFormula = reader.getCellHyperlinkString(rowIndex, 1);
         String sheetName = nameFormula.split("!")[0].replace("'", "");
         if (!sheetName.contains(" Header")) { // Skip header structures (they're auto-generated)
            structureSheetNames.add(sheetName);
         }
         rowIndex++;
      }

      List<PlatformTypeToken> existingPlatformTypes = mimApi.getInterfacePlatformTypeApi().getAllWithRelations(branch,
         FollowRelation.followList(CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet,
            CoreRelationTypes.InterfaceEnumeration_EnumerationState));
      Map<String, InterfaceStructureElementToken> elements = new HashMap<>();
      Map<String, PlatformTypeToken> platformTypes = new HashMap<>();
      List<PlatformTypeToken> platformTypesToCreate = new LinkedList<>();
      Map<String, InterfaceEnumerationSet> enumsToUpdate = new HashMap<>();

      for (PlatformTypeToken type : existingPlatformTypes) {
         String typeKey = type.getInterfaceLogicalType().getValue().equals("enumeration") ? getEnumSetKey(
            type.getEnumSet()) : mimApi.getInterfacePlatformTypeApi().getUniqueIdentifier(
               type.getInterfaceLogicalType().getValue(), type.getInterfacePlatformTypeMinval().getValue(),
               type.getInterfacePlatformTypeMaxval().getValue(),
               type.getInterfacePlatformTypeValidRangeDescription().getValue(),
               type.getInterfacePlatformTypeUnits().getValue(), type.getInterfaceDefaultValue().getValue(),
               Integer.parseInt(type.getInterfacePlatformTypeBitSize().getValue()) / 8);
         platformTypes.put(typeKey, type);
      }

      // Create a boolean type to avoid extra processing for booleans
      if (!platformTypes.containsKey("boolean")) {
         PlatformTypeToken boolToken =
            new PlatformTypeToken(id, "Boolean", "boolean", "8", "0", "1", "", "", "", "0 to 1");
         platformTypes.put("boolean", boolToken);
         incrementId();
         platformTypesToCreate.add(boolToken);
      }

      for (String sheetName : structureSheetNames) {
         reader.setActiveSheet(sheetName);
         InterfaceStructureToken structure = readStructure(primaryNode, secondaryNode, submessageIds);
         readStructureElements(structure, elements, platformTypes, platformTypesToCreate, enumsToUpdate,
            primaryNodeName + "_" + secondaryNodeName);
      }

      for (PlatformTypeToken pTypeToken : platformTypesToCreate) {
         summary.getPlatformTypes().add(pTypeToken);
      }

      // Sort messages by node and message number. Messages are grouped by primary node, and within those
      // groups are sorted by message number, with numbers coming before letters.
      Collections.sort(summary.getMessages(), new Comparator<InterfaceMessageToken>() {
         @Override
         public int compare(InterfaceMessageToken o1, InterfaceMessageToken o2) {
            if (o1.getPublisherNodes().get(0).getName().getValue().equals(
               o2.getPublisherNodes().get(0).getName().getValue())) {
               boolean isO1Numeric = isStringNumeric(o1.getInterfaceMessageNumber().getValue());
               boolean isO2Numeric = isStringNumeric(o2.getInterfaceMessageNumber().getValue());
               if (isO1Numeric && isO2Numeric) {
                  return (int) Double.parseDouble(o1.getInterfaceMessageNumber().getValue()) > (int) Double.parseDouble(
                     o2.getInterfaceMessageNumber().getValue()) ? 1 : -1;
               }
               if (!isO1Numeric && !isO2Numeric) {
                  return o1.getInterfaceMessageNumber().getValue().compareTo(o2.getInterfaceMessageNumber().getValue());
               }
               return isO2Numeric ? 1 : -1;
            }
            if (o1.getPublisherNodes().get(0).getName().getValue().equals(primaryNode.getName().getValue())) {
               return -1;
            } else {
               return 1;
            }
         }
      });

      // Add connection <-> message relations after the messages are sorted to get the correct relation order.
      List<String> connectionRels =
         summary.getConnectionMessageRelations().getOrDefault(connection.getIdString(), new LinkedList<>());
      for (InterfaceMessageToken message : summary.getMessages()) {
         connectionRels.add(message.getIdString());
         summary.getConnectionMessageRelations().put(connection.getIdString(), connectionRels);
      }

      reader.closeWorkbook();
      return summary;
   }

   private void readMessage(InterfaceNode pubNode, InterfaceNode subNode, int firstRow, int lastRow, int colOffset,
      Map<String, Long> subMessageIdMap) {
      String messageNumber = reader.getCellStringValue(firstRow, 0 + colOffset);
      String rate = reader.getCellStringValue(firstRow, 1 + colOffset);
      String write = reader.getCellStringValue(firstRow, 2 + colOffset);
      String name = reader.getCellStringValue(firstRow, 3 + colOffset);

      if (messageNumber.equals("0")) {
         return;
      }

      String periodicity = rate.equals("Aperiodic") ? rate : "Periodic";
      rate = rate.equals("Aperiodic") ? "1" : rate.split(" ")[0];

      InterfaceMessageToken message = new InterfaceMessageToken(id, name);
      message.setInterfaceMessageNumber("" + messageNumber);
      message.setInterfaceMessageRate(rate);
      message.setInterfaceMessagePeriodicity(periodicity);
      message.setInterfaceMessageWriteAccess(write.equals("W"));
      message.setApplicability(ApplicabilityToken.BASE);
      message.getPublisherNodes().add(pubNode);
      message.getSubscriberNodes().add(subNode);
      message.setInterfaceMessageType("Operational");
      message.setDescription("");
      incrementId();

      List<String> pubRels =
         summary.getMessagePublisherNodeRelations().getOrDefault(message.getIdString(), new LinkedList<>());
      pubRels.add(pubNode.getIdString());
      summary.getMessagePublisherNodeRelations().put(message.getIdString(), pubRels);

      List<String> subRels =
         summary.getMessageSubscriberNodeRelations().getOrDefault(message.getIdString(), new LinkedList<>());
      subRels.add(subNode.getIdString());
      summary.getMessageSubscriberNodeRelations().put(message.getIdString(), subRels);

      List<String> subMessageIds = new LinkedList<>();
      int subMsgNum = 1;
      for (int i = firstRow + 2; i <= lastRow; i++) {
         String label = reader.getCellStringValue(i, 3 + colOffset);
         if (label == null || !label.contains("Submessage")) {
            break;
         }
         String subMessageName = reader.getCellStringValue(i, 4 + colOffset);
         InterfaceSubMessageToken subMessage = new InterfaceSubMessageToken(id, subMessageName);
         subMessage.setApplicability(ApplicabilityToken.BASE);
         subMessage.setInterfaceSubMessageNumber("" + subMsgNum);
         subMessage.setDescription("");
         subMessageIds.add("" + id);
         subMessageIdMap.put(getSubMessageMapKey(messageNumber + "", subMsgNum + "", pubNode.getName().getValue()), id);
         incrementId();
         summary.getSubMessages().add(subMessage);
         subMsgNum++;

      }
      summary.getMessageSubmessageRelations().put(message.getIdString(), subMessageIds);
      summary.getMessages().add(message);
   }

   private InterfaceStructureToken readStructure(InterfaceNode primaryNode, InterfaceNode secondaryNode,
      Map<String, Long> subMessageIdMap) {
      int structureRow = 1;
      String name = reader.getCellStringValue(structureRow, 1);
      String nameAbbrev = reader.getCellStringValue(structureRow, 2);
      String category = reader.getCellStringValue(structureRow, 4);
      String txRate = reader.getCellValue(structureRow, 5).toString();
      String minSim = reader.getCellValue(structureRow, 6).toString();
      String maxSim = reader.getCellValue(structureRow, 7).toString();
      String nodeName = reader.getCellStringValue(structureRow, 10);
      String msgNum = reader.getCellStringValue(structureRow, 11);
      String subMsgNum = reader.getCellValue(structureRow, 12).toString();
      String taskfileType = reader.getCellValue(structureRow, 13).toString();
      String description = reader.getCellStringValue(structureRow, 14);

      InterfaceStructureToken structure = new InterfaceStructureToken(id, name);
      incrementId();
      structure.setApplicability(ApplicabilityToken.BASE);
      structure.setInterfaceStructureCategory(category);
      structure.setInterfaceMinSimultaneity(toIntString(minSim));
      structure.setInterfaceMaxSimultaneity(toIntString(maxSim));
      structure.setDescription(description);
      structure.setNameAbbrev(!name.equals(nameAbbrev) ? nameAbbrev : "");

      msgNum = isStringNumeric(msgNum) ? ((int) Double.parseDouble(msgNum)) + "" : msgNum;
      subMsgNum = subMsgNum.equals("n/a") ? "0" : subMsgNum;
      subMsgNum = isStringNumeric(subMsgNum) ? ((int) Double.parseDouble(subMsgNum)) + "" : subMsgNum;

      // task file is "n/a" for some structures. In that case, set to 0.
      int taskFile;
      try {
         taskFile = Integer.parseInt(taskfileType);
      } catch (NumberFormatException e) {
         taskFile = 0;
      }
      structure.setInterfaceTaskFileType(taskFile);

      // Find message based on node and number and update with new information
      InterfaceMessageToken message = InterfaceMessageToken.SENTINEL;
      for (InterfaceMessageToken msg : summary.getMessages()) {
         if (msg.getInterfaceMessageNumber().getValue().equals(
            msgNum + "") && msg.getPublisherNodes().size() > 0 && msg.getPublisherNodes().get(
               0).getName().getValue().equals(nodeName)) {
            message = msg;
            break;
         }
      }

      // If the message does not exist, create a new one
      if (!message.isValid()) {
         InterfaceNode pubNode = primaryNode.getName().getValue().equals(nodeName) ? primaryNode : secondaryNode;
         InterfaceNode subNode = pubNode.equals(primaryNode) ? secondaryNode : primaryNode;
         message = new InterfaceMessageToken(id, "Message " + msgNum);
         message.setInterfaceMessageNumber("" + msgNum);
         message.setInterfaceMessageWriteAccess(false);
         message.setApplicability(ApplicabilityToken.BASE);
         message.getPublisherNodes().add(pubNode);
         message.getSubscriberNodes().add(subNode);
         message.setInterfaceMessageType("Connection");
         message.setDescription("");
         incrementId();
         summary.getMessages().add(message);

         List<String> pubRels =
            summary.getMessagePublisherNodeRelations().getOrDefault(message.getIdString(), new LinkedList<>());
         pubRels.add(pubNode.getIdString());
         summary.getMessagePublisherNodeRelations().put(message.getIdString(), pubRels);

         List<String> subRels =
            summary.getMessageSubscriberNodeRelations().getOrDefault(message.getIdString(), new LinkedList<>());
         subRels.add(subNode.getIdString());
         summary.getMessageSubscriberNodeRelations().put(message.getIdString(), subRels);
      }

      // txRate will either be a double like 20.0 if periodic, or 20-A if aperiodic
      String[] txRateSplit = txRate.split("-");
      String txRateInt = txRateSplit[0].split("[.]")[0];
      String periodicity = txRateSplit.length == 2 && txRateSplit[1].equals("A") ? "Aperiodic" : "Periodic";
      message.setInterfaceMessageRate(txRateInt);
      message.setInterfaceMessagePeriodicity(periodicity);

      // If the submessage does not exist, create a new one
      String subMsgKey = getSubMessageMapKey(msgNum, subMsgNum, nodeName);
      if (!subMessageIdMap.containsKey(subMsgKey)) {
         InterfaceSubMessageToken subMessage = new InterfaceSubMessageToken(id, "SubMessage " + subMsgNum);
         incrementId();
         subMessage.setInterfaceSubMessageNumber(subMsgNum + "");
         subMessage.setApplicability(ApplicabilityToken.BASE);
         subMessage.setDescription("");
         summary.getSubMessages().add(subMessage);
         List<String> msgRels =
            summary.getMessageSubmessageRelations().getOrDefault(message.getIdString(), new LinkedList<>());
         msgRels.add(subMessage.getIdString());
         summary.getMessageSubmessageRelations().put(message.getIdString(), msgRels);
         subMessageIdMap.put(getSubMessageMapKey(msgNum, subMsgNum, nodeName), subMessage.getId());
      }

      Long subMsgId = subMessageIdMap.get(subMsgKey);
      List<String> rels =
         summary.getSubMessageStructureRelations().getOrDefault(subMsgId.toString(), new LinkedList<>());
      rels.add(structure.getIdString());
      summary.getSubMessageStructureRelations().put(subMsgId.toString(), rels);

      summary.getStructures().add(structure);

      return structure;
   }

   private void readStructureElements(InterfaceStructureToken structure,
      Map<String, InterfaceStructureElementToken> elements, Map<String, PlatformTypeToken> platformTypes,
      List<PlatformTypeToken> platformTypesToCreate, Map<String, InterfaceEnumerationSet> enumsToUpdate,
      String connectionName) {
      Boolean hasDefaultValue = reader.getCellStringValue(3, 18).equals("Default Value");
      int rowIndex = 4;
      InterfaceStructureElementToken previousElement = InterfaceStructureElementToken.SENTINEL;
      PlatformTypeToken previousPType = PlatformTypeToken.SENTINEL;
      while (reader.rowExists(rowIndex)) {
         int numBytes = (int) reader.getCellNumericValue(rowIndex, IcdElementIndex.byteSize.ordinal());
         String logicalType = reader.getCellStringValue(rowIndex, IcdElementIndex.dataType.ordinal()).trim();
         String name = reader.getCellStringValue(rowIndex, IcdElementIndex.name.ordinal()).trim();
         String units = reader.getCellStringValue(rowIndex, IcdElementIndex.units.ordinal()).trim();
         String validRange = reader.getCellStringValue(rowIndex, IcdElementIndex.validRange.ordinal());
         boolean alterable = reader.getCellStringValue(rowIndex, IcdElementIndex.alterable.ordinal()).equals("Yes");
         String description = reader.getCellStringValue(rowIndex, IcdElementIndex.description.ordinal());
         String enumDesc = reader.getCellStringValue(rowIndex, IcdElementIndex.enumLiterals.ordinal());
         String notes = reader.getCellStringValue(rowIndex, IcdElementIndex.notes.ordinal());
         String defaultValue = reader.getCellStringValue(rowIndex, IcdElementIndex.defaultValue.ordinal());

         if (hasDefaultValue) {
            defaultValue = reader.getCellStringValue(rowIndex, 18);
         }

         // Instrumentation message elements don't have names which will cause issues, so we set a name here.
         // The merged cells containing the instrumentation message text begin on the logicalType cell.
         if (name.isEmpty() && logicalType.toLowerCase().contains("instrumentation message")) {
            name = structure.getName().getValue();
         }

         PlatformTypeToken pType = PlatformTypeToken.SENTINEL;
         if (logicalType.equals("boolean")) {
            pType = platformTypes.get("boolean");
         } else if (logicalType.equals("enumeration")) {
            String enumName = enumDesc.split("\n")[0].split(":")[0].replaceAll("[()]", "").trim();
            enumName = name.equals("Taskfile Type") ? structure.getName().getValue().toLowerCase().contains(
               "command taskfile") ? "Command Taskfile" : "Status Taskfile" : enumName;
            enumName = enumName.isEmpty() || (enumName.split("[-=]").length > 1 && enumName.split("[-=]")[0].matches(
               "^\\d+\\s*")) ? name : enumName;

            InterfaceEnumerationSet enumSet = new InterfaceEnumerationSet(id, enumName);
            incrementId();
            enumSet.setApplicability(ApplicabilityToken.BASE);
            if (!enumName.matches("^C\\d+$")) {
               String enumSetDescription = "";
               for (String line : enumDesc.split("\n")) {
                  if (line.trim().matches("^\\s*\\d+\\s*[-=].*") || line.trim().matches("^0x.*\\s*[-=].*")) {
                     String[] splitLine = line.trim().split("\\s*[-=]\\s*", 2);
                     InterfaceEnumeration enumeration =
                        new InterfaceEnumeration(id, splitLine[1].split("//")[0].trim());
                     incrementId();
                     long ordinal =
                        splitLine[0].matches("^0x.*") ? Long.decode(splitLine[0].trim()) : Long.parseLong(splitLine[0]);
                     InterfaceEnumOrdinalType ordinalType =
                        splitLine[0].matches("^0x.*") ? InterfaceEnumOrdinalType.HEX : InterfaceEnumOrdinalType.LONG;
                     enumeration.setApplicability(ApplicabilityToken.BASE);
                     enumeration.setOrdinal(ordinal);
                     enumeration.setOrdinalType(ordinalType);
                     enumSetDescription += enumeration.toString() + "\n";
                     enumSet.getEnumerations().add(enumeration);
                  }
               }
               enumSet.setDescription(enumSetDescription.trim());
            }

            String pTypeKey = getEnumSetKey(enumSet);
            pTypeKey = pTypeKey.isEmpty() ? enumName : pTypeKey;

            boolean create = false;

            // Case 1: The first time the enum set was read it was empty. If the full enum set description is read,
            //         update the existing one with enumerations and set up relations.
            if (enumsToUpdate.containsKey(enumName) && enumSet.getEnumerations().size() > 0) {
               InterfaceEnumerationSet setToUpdate = enumsToUpdate.get(enumName);
               pType = platformTypes.get(getEnumSetKey(setToUpdate));
               platformTypes.remove(getEnumSetKey(setToUpdate));
               setToUpdate.setEnumerations(enumSet.getEnumerations());
               platformTypes.put(getEnumSetKey(setToUpdate), pType);
               enumsToUpdate.remove(enumName);

               for (InterfaceEnumeration enumeration : enumSet.getEnumerations()) {
                  summary.getEnums().add(enumeration);
                  List<String> rels =
                     summary.getEnumSetEnumRelations().getOrDefault(enumSet.getIdString(), new LinkedList<>());
                  rels.add(enumeration.getIdString());
                  summary.getEnumSetEnumRelations().put(enumSet.getIdString(), rels);
               }
            }
            // Case 2: The key exists in the map. Use the type from that key
            else if (platformTypes.containsKey(pTypeKey)) {
               pType = platformTypes.get(pTypeKey);
            }
            // Case 3: The key does not exist in the map and the set is empty. This can happen when the ICD just has the name of a previously defined enum set,
            //         or the defined enum set has not been read from another structure page yet.
            //         Search the platform types to create for that name first to make sure it's from this ICD, or the full list otherwise.
            else if (enumSet.getEnumerations().isEmpty()) {
               pType = platformTypesToCreate.stream().filter(e -> e.getEnumSet() != null).filter(
                  e -> e.getEnumSet().getName().equals(enumSet.getName())).findFirst().orElse(
                     PlatformTypeToken.SENTINEL);
               if (!pType.isValid()) {
                  pType = platformTypes.values().stream().filter(e -> e.getEnumSet() != null).filter(
                     e -> e.getEnumSet().getName().equals(enumSet.getName())).findFirst().orElse(
                        PlatformTypeToken.SENTINEL);
                  if (!pType.isValid()) {
                     create = true;
                     enumsToUpdate.put(enumSet.getName().getValue(), enumSet);
                  }
               }
            } else {
               create = true;
            }

            // Case 4: The enum set does not exist and needs to be created.
            if (create) {
               String pTypeName = enumName;
               // Search the platform types to see if there is already a type with this enum name. If there is, append the connection in parenthesis.
               final String n = pTypeName;
               if (platformTypes.values().stream().filter(t -> t.getName().getValue().equals(n)).findFirst().orElse(
                  PlatformTypeToken.SENTINEL).isValid()) {
                  pTypeName += " (" + connectionName + ")";
               }
               pType = new PlatformTypeToken(id, pTypeName, logicalType, (numBytes * 8) + "", "", "", units, "",
                  defaultValue, validRange);
               pType.setEnumSet(enumSet);
               incrementId();
               platformTypes.put(pTypeKey, pType);
               platformTypesToCreate.add(pType);

               summary.getEnumSets().add(enumSet);
               summary.getPlatformTypeEnumSetRelations().put(pType.getIdString(),
                  new LinkedList<>(Arrays.asList(enumSet.getIdString())));

               for (InterfaceEnumeration enumeration : enumSet.getEnumerations()) {
                  summary.getEnums().add(enumeration);
                  List<String> rels =
                     summary.getEnumSetEnumRelations().getOrDefault(enumSet.getIdString(), new LinkedList<>());
                  rels.add(enumeration.getIdString());
                  summary.getEnumSetEnumRelations().put(enumSet.getIdString(), rels);
               }
            }
         } else {
            // Transform data
            units = units.contains("n/a") ? "" : units;
            validRange = validRange.contains("n/a") ? "" : validRange;
            String minVal = "";
            String maxVal = "";
            String[] range = validRange.replace("--", "").replace("---", "").split(" to ");
            if (range.length == 1 && range[0].split(" ").length == 1) {
               minVal = range[0].trim();
               maxVal = range[0].trim();
               validRange = range[0].trim();
            } else if (range.length == 2) {
               minVal = range[0].trim();
               maxVal = range[1].trim();
               validRange = range[0] + " to " + range[1];
            }

            String pTypeName = mimApi.getInterfacePlatformTypeApi().getUniqueIdentifier(logicalType, minVal, maxVal,
               validRange, units, defaultValue, numBytes);
            if (platformTypes.containsKey(pTypeName)) {
               pType = platformTypes.get(pTypeName);
            } else {
               pType = new PlatformTypeToken(id, pTypeName, logicalType, (numBytes * 8) + "", minVal, maxVal, units, "",
                  defaultValue, validRange);
               incrementId();
               platformTypes.put(pTypeName, pType);
               platformTypesToCreate.add(pType);
            }
         }

         String elementKey = getElementKey(name, alterable, description, notes, pType.getId());
         InterfaceStructureElementToken element = InterfaceStructureElementToken.SENTINEL;
         boolean relateElement = true;
         boolean possibleArray = name.matches(".*\\s\\d+$") && !name.matches(".*\\s(and)\\s\\d+$");

         // If an element is possibly part of an array, do not reuse it.
         // There could be other elements with the same name that are single elements or part of a different size array.
         if (!possibleArray && elements.containsKey(elementKey)) {
            element = elements.get(elementKey);
            previousElement = element;
            previousPType = pType;
         } else if (possibleArray && name.matches(".*\\s\\d+$") && !name.matches(
            ".*\\s(and)\\s\\d+$") && previousElement.isValid() && previousPType.isValid() && compareElementsForArray(
               previousElement.getName().getValue(), name, previousPType.getId(), pType.getId())) {
            String[] nameSplit = name.split(" ");
            int arrayNum = Integer.parseInt(nameSplit[nameSplit.length - 1]);
            if (previousElement.getInterfaceElementIndexStart().getValue() == 0) {
               String[] prevNameSplit = previousElement.getName().getValue().split(" ");
               int prevArrayNum = Integer.parseInt(prevNameSplit[prevNameSplit.length - 1]);
               previousElement.setInterfaceElementIndexStart(prevArrayNum);
            }
            previousElement.setInterfaceElementIndexEnd(arrayNum);
            relateElement = false;
            // Check next row for member of array. If it is not, remove the number from the end of the name.
            if (reader.rowExists(rowIndex + 1) && reader.getCellValue(rowIndex + 1,
               0) != null && !reader.getCellValue(rowIndex + 1, 0).toString().isEmpty()) {
               String nextName = reader.getCellStringValue(rowIndex + 1, 11).trim();
               if (!nextName.split("\\s\\d+$")[0].trim().equals(name.split("\\s\\d+$")[0].trim())) {
                  previousElement.setName(previousElement.getName().getValue().split("\\s\\d+$")[0].trim());
               }
            } else {
               // If on the last row and the last element is part of an array, remove the number from the array element name.
               if (previousElement.getName().getValue().matches(".*\\s\\d+$")) {
                  previousElement.setName(previousElement.getName().getValue().split("\\s\\d+$")[0].trim());
               }
            }
         } else {
            element = new InterfaceStructureElementToken(id, name);
            incrementId();
            element.setInterfaceElementAlterable(alterable);
            element.setDescription(description);
            element.setNotes(notes);
            element.setInterfaceDefaultValue(defaultValue);
            element.setEnumLiteral(logicalType.equals("enumeration") ? "" : enumDesc); // Enumeration elements use the enum set description for the enum literals
            elements.put(elementKey, element);
            summary.getElements().add(element);
            summary.getElementPlatformTypeRelations().put(element.getIdString(),
               new LinkedList<>(Arrays.asList(pType.getIdString())));
            previousElement = element;
            previousPType = pType;
         }

         if (relateElement) {
            List<String> rels =
               summary.getStructureElementRelations().getOrDefault(structure.getIdString(), new LinkedList<>());
            rels.add(element.getIdString());
            summary.getStructureElementRelations().put(structure.getIdString(), rels);
         }

         rowIndex++;
      }
   }

   /**
    * Converts a double within a string to an integer and returns as a string. If non-numeric characters exist, return
    * the original string.
    *
    * @param str
    * @return
    */
   private String toIntString(String str) {
      String result = "";
      try {
         int val = (int) Double.parseDouble(str);
         result = val + "";
      } catch (NumberFormatException e) {
         result = str;
      }
      return result;
   }

   private boolean isStringNumeric(String input) {
      return input.matches("^(\\d+.)*\\d+$");
   }

   private String getEnumSetKey(InterfaceEnumerationSet enumSet) {
      String key = String.join(";",
         enumSet.getEnumerations().stream().map(e -> e.getOrdinal().getValue() + "=" + e.getName().getValue()).collect(
            Collectors.toList()));
      return enumSet.getName().getValue().toUpperCase().replace(" ", "_") + "_" + key.hashCode();
   }

   private boolean compareElementsForArray(String name1, String name2, Long pTypeId1, Long pTypeId2) {
      String n1 = name1.split("\\s\\d+$")[0];
      String n2 = name2.split("\\s\\d+$")[0];
      return n1.equals(n2) && pTypeId1 == pTypeId2;
   }

   private String getSubMessageMapKey(String messageNum, String subMessageNum, String nodeName) {
      return nodeName + messageNum + subMessageNum;
   }

   private String getElementKey(String name, boolean alterable, String description, String notes, Long pTypeId) {
      return name + "_" + alterable + "_" + description.hashCode() + "_" + notes.hashCode() + "_" + pTypeId;
   }

   private void incrementId() {
      id++;
   }
}
