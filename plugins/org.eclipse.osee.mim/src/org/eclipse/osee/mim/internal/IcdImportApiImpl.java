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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.io.excel.ExcelWorkbookReader;
import org.eclipse.osee.framework.jdk.core.util.io.excel.ExcelWorkbookWriter.WorkbookFormat;
import org.eclipse.osee.mim.MimApi;
import org.eclipse.osee.mim.MimImportApi;
import org.eclipse.osee.mim.types.InterfaceElementImportToken;
import org.eclipse.osee.mim.types.InterfaceEnumOrdinalType;
import org.eclipse.osee.mim.types.InterfaceEnumeration;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceNode;
import org.eclipse.osee.mim.types.InterfaceStructureToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;
import org.eclipse.osee.mim.types.MimImportSummary;
import org.eclipse.osee.mim.types.PlatformTypeImportToken;
import org.eclipse.osee.mim.types.PlatformTypeToken;

/**
 * @author Ryan T. Baldwin
 */
public class IcdImportApiImpl implements MimImportApi {

   private final ExcelWorkbookReader reader;
   private final MimApi mimApi;
   private final BranchId branch;
   private MimImportSummary summary;

   private Long id = 1L;

   public IcdImportApiImpl(BranchId branch, InputStream inputStream, MimApi mimApi) {
      this.reader = new ExcelWorkbookReader(inputStream, WorkbookFormat.XLSX);
      this.mimApi = mimApi;
      this.branch = branch;
   }

   @Override
   public MimImportSummary getSummary() {
      summary = new MimImportSummary();

      reader.setActiveSheet("Message and Submessage Summary");

      // Primary Node
      String nodeName = reader.getCellStringValue(0, 0).split(" ")[0];
      InterfaceNode primaryNode = new InterfaceNode(id, nodeName);
      primaryNode.setApplicability(ApplicabilityToken.BASE);
      primaryNode.setAddress("");
      primaryNode.setColor("");
      primaryNode.setDescription("");
      summary.setPrimaryNode(primaryNode);
      incrementId();

      // Secondary Node
      nodeName = reader.getCellStringValue(0, 5).split(" ")[0];
      InterfaceNode secondaryNode = new InterfaceNode(id, nodeName);
      secondaryNode.setApplicability(ApplicabilityToken.BASE);
      secondaryNode.setAddress("");
      secondaryNode.setColor("");
      secondaryNode.setDescription("");
      summary.setSecondaryNode(secondaryNode);
      incrementId();

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
         readMessage(primaryNode, start, end, 0, submessageIds);
      }

      for (String region : secondaryRegions) {
         String[] split = region.split(":");
         int start = Integer.parseInt(split[0].substring(1)) - 1;
         int end = Integer.parseInt(split[1].substring(1)) - 1;
         readMessage(secondaryNode, start, end, 5, submessageIds);
      }

      // Structures and Elements
      reader.setActiveSheet("Structure Summary");
      List<String> structureSheetNames = new LinkedList<>();
      int rowIndex = 4;
      while (reader.rowExists(rowIndex)) {
         String nameFormula = reader.getCellHyperlinkString(rowIndex, 1);
         String sheetName = nameFormula.split("!")[0].replace("'", "");
         if (!sheetName.contains(" Header")) { // Skip header structures (they're auto-generated)
            structureSheetNames.add(sheetName);
         }
         rowIndex++;
      }

      List<PlatformTypeToken> existingPlatformTypes = mimApi.getInterfacePlatformTypeApi().getAllWithRelations(branch,
         Arrays.asList(CoreRelationTypes.InterfacePlatformTypeEnumeration_EnumerationSet,
            CoreRelationTypes.InterfaceEnumeration_EnumerationState));
      Map<String, InterfaceElementImportToken> elements = new HashMap<>();
      Map<String, PlatformTypeImportToken> platformTypes = new HashMap<>();
      List<PlatformTypeImportToken> platformTypesToCreate = new LinkedList<>();

      for (PlatformTypeToken type : existingPlatformTypes) {
         String typeKey = type.getInterfaceLogicalType().equals("enumeration") ? getEnumNameKey(
            type.getEnumSet().getName()) : mimApi.getInterfacePlatformTypeApi().getUniqueIdentifier(
               type.getInterfaceLogicalType(), type.getInterfacePlatformTypeMinval(),
               type.getInterfacePlatformTypeMaxval(), type.getInterfacePlatformTypeValidRangeDescription(),
               type.getInterfacePlatformTypeUnits(), type.getInterfaceDefaultValue(),
               Integer.parseInt(type.getInterfacePlatformTypeBitSize()) / 8);
         platformTypes.put(typeKey, new PlatformTypeImportToken(type.getId(), type.getName()));
      }

      // Create a boolean type to avoid extra processing for booleans
      if (!platformTypes.containsKey("boolean")) {
         PlatformTypeImportToken boolToken =
            new PlatformTypeImportToken(id, "Boolean", "boolean", "8", "0", "1", "", "", "", "0 to 1");
         platformTypes.put("boolean", boolToken);
         incrementId();
         platformTypesToCreate.add(boolToken);
      }

      for (String sheetName : structureSheetNames) {
         reader.setActiveSheet(sheetName);
         InterfaceStructureToken structure = readStructure(primaryNode, secondaryNode, submessageIds);
         readStructureElements(structure, elements, platformTypes, platformTypesToCreate);
      }

      for (PlatformTypeImportToken pTypeToken : platformTypesToCreate) {
         summary.getPlatformTypes().add(pTypeToken);
      }

      // Sort messages by node and message number
      Collections.sort(summary.getMessages(), new Comparator<InterfaceMessageToken>() {
         @Override
         public int compare(InterfaceMessageToken o1, InterfaceMessageToken o2) {
            if (o1.getInitiatingNode().getName().equals(o2.getInitiatingNode().getName())) {
               if (Integer.parseInt(o1.getInterfaceMessageNumber()) > Integer.parseInt(
                  o2.getInterfaceMessageNumber())) {
                  return 1;
               } else {
                  return -1;
               }
            }
            if (o1.getInitiatingNode().getName().equals(primaryNode.getName())) {
               return -1;
            } else {
               return 1;
            }
         }
      });

      reader.closeWorkbook();
      return summary;
   }

   private void readMessage(InterfaceNode node, int firstRow, int lastRow, int colOffset, Map<String, Long> subMessageIdMap) {
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
      message.setInitiatingNode(node);
      message.setInterfaceMessageType("Operational");
      message.setDescription("");
      incrementId();

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
         subMessageIdMap.put(getSubMessageMapKey(messageNumber, subMsgNum + "", node.getName()), id);
         incrementId();
         summary.getSubMessages().add(subMessage);
         subMsgNum++;

      }
      summary.getMessageSubmessageRelations().put(message.getIdString(), subMessageIds);
      summary.getMessages().add(message);
   }

   private InterfaceStructureToken readStructure(InterfaceNode primaryNode, InterfaceNode secondaryNode, Map<String, Long> subMessageIdMap) {
      int structureRow = 1;
      String name = reader.getCellStringValue(structureRow, 1);
      String category = reader.getCellStringValue(structureRow, 4);
      String txRate = reader.getCellValue(structureRow, 5).toString();
      String minSim = reader.getCellValue(structureRow, 6).toString();
      String maxSim = reader.getCellValue(structureRow, 7).toString();
      String nodeName = reader.getCellStringValue(structureRow, 10);
      String msgNum = reader.getCellStringValue(structureRow, 11);
      String subMsgNum = reader.getCellStringValue(structureRow, 12);
      String taskfileType = reader.getCellValue(structureRow, 13).toString();
      String description = reader.getCellStringValue(structureRow, 14);

      InterfaceStructureToken structure = new InterfaceStructureToken(id, name);
      incrementId();
      structure.setApplicability(ApplicabilityToken.BASE);
      structure.setInterfaceStructureCategory(category);
      structure.setInterfaceMinSimultaneity(toIntString(minSim));
      structure.setInterfaceMaxSimultaneity(toIntString(maxSim));
      structure.setDescription(description);

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
         if (msg.getInterfaceMessageNumber().equals(msgNum + "") && msg.getInitiatingNode().getName().equals(
            nodeName)) {
            message = msg;
            break;
         }
      }

      // If the message does not exist, create a new one
      if (!message.isValid()) {
         message = new InterfaceMessageToken(id, "Message " + msgNum);
         message.setInterfaceMessageNumber("" + msgNum);
         message.setInterfaceMessageWriteAccess(false);
         message.setApplicability(ApplicabilityToken.BASE);
         message.setInitiatingNode(primaryNode.getName().equals(nodeName) ? primaryNode : secondaryNode);
         message.setInterfaceMessageType("Connection");
         message.setDescription("");
         incrementId();
         summary.getMessages().add(message);
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

   private void readStructureElements(InterfaceStructureToken structure, Map<String, InterfaceElementImportToken> elements, Map<String, PlatformTypeImportToken> platformTypes, List<PlatformTypeImportToken> platformTypesToCreate) {
      int rowIndex = 4;
      InterfaceElementImportToken previousElement = InterfaceElementImportToken.SENTINEL;
      PlatformTypeImportToken previousPType = PlatformTypeImportToken.SENTINEL;
      while (reader.rowExists(rowIndex)) {
         int numBytes = (int) reader.getCellNumericValue(rowIndex, 2);
         String logicalType = reader.getCellStringValue(rowIndex, 5);
         String name = reader.getCellStringValue(rowIndex, 6);
         String units = reader.getCellStringValue(rowIndex, 7);
         String validRange = reader.getCellStringValue(rowIndex, 8);
         boolean alterable = reader.getCellStringValue(rowIndex, 9).equals("Yes");
         String description = reader.getCellStringValue(rowIndex, 10);
         String enumDesc = reader.getCellStringValue(rowIndex, 11);
         String notes = reader.getCellStringValue(rowIndex, 12);
         String defaultValue = reader.getCellStringValue(rowIndex, 13);

         // Instrumentation message elements don't have names which will cause issues, so we set a name here.
         // The merged cells containing the instrumentation message text begin on the logicalType cell.
         if (name.isEmpty() && numBytes == 0 && logicalType.toLowerCase().contains("instrumentation message")) {
            name = structure.getName();
         }

         PlatformTypeImportToken pType = PlatformTypeImportToken.SENTINEL;
         if (logicalType.equals("boolean")) {
            pType = platformTypes.get("boolean");
         } else if (logicalType.equals("enumeration")) {
            String enumName = enumDesc.split("\n")[0].split(":")[0].replaceAll("[()]", "").trim();
            enumName = name.equals("Taskfile Type") ? structure.getName().toLowerCase().contains(
               "command taskfile") ? "Command Taskfile" : "Status Taskfile" : enumName;
            enumName = enumName.isEmpty() || (enumName.split("[-=]").length > 1 && enumName.split("[-=]")[0].matches(
               "^\\d+\\s*")) ? name : enumName;
            enumName = enumName.matches("^C\\d+$") ? name : enumName;

            String key = getEnumNameKey(enumName);
            key = key.isEmpty() ? enumName : key;

            if (platformTypes.containsKey(key)) {
               pType = platformTypes.get(key);
            } else {
               pType = new PlatformTypeImportToken(id, enumName, logicalType, (numBytes * 8) + "", "", "", "", "",
                  defaultValue, validRange);
               incrementId();
               platformTypes.put(getEnumNameKey(enumName), pType);
               platformTypesToCreate.add(pType);

               InterfaceEnumerationSet enumSet = new InterfaceEnumerationSet(id, enumName);
               incrementId();
               enumSet.setApplicability(ApplicabilityToken.BASE);
               summary.getEnumSets().add(enumSet);
               summary.getPlatformTypeEnumSetRelations().put(pType.getIdString(),
                  new LinkedList<>(Arrays.asList(enumSet.getIdString())));

               String enumSetDescription = "";
               for (String line : enumDesc.split("\n")) {
                  if (line.trim().matches("^\\s*\\d+\\s*[-=].*") || line.trim().matches("^0x.*\\s*[-=].*")) {
                     String[] splitLine = line.trim().split("\\s*[-=]\\s*", 2);
                     long ordinal =
                        splitLine[0].matches("^0x.*") ? Long.decode(splitLine[0].trim()) : Long.parseLong(splitLine[0]);
                     InterfaceEnumOrdinalType ordinalType =
                        splitLine[0].matches("^0x.*") ? InterfaceEnumOrdinalType.HEX : InterfaceEnumOrdinalType.LONG;
                     InterfaceEnumeration enumeration = new InterfaceEnumeration(id, splitLine[1].trim());
                     incrementId();
                     enumeration.setApplicability(ApplicabilityToken.BASE);
                     enumeration.setOrdinal(ordinal);
                     enumeration.setOrdinalType(ordinalType);
                     summary.getEnums().add(enumeration);
                     List<String> rels =
                        summary.getEnumSetEnumRelations().getOrDefault(enumSet.getIdString(), new LinkedList<>());
                     rels.add(enumeration.getIdString());
                     summary.getEnumSetEnumRelations().put(enumSet.getIdString(), rels);
                  }
               }
               enumSet.setDescription(enumSetDescription.trim());
            }
         } else {
            // Transform data
            units = units.contains("n/a") ? "" : units;
            validRange = validRange.contains("n/a") ? "" : validRange;
            String minVal = "";
            String maxVal = "";
            String[] range = validRange.split(" to ");
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
               pType = new PlatformTypeImportToken(id, pTypeName, logicalType, (numBytes * 8) + "", minVal, maxVal,
                  units, "", defaultValue, validRange);
               incrementId();
               platformTypes.put(pTypeName, pType);
               platformTypesToCreate.add(pType);
            }
         }

         String elementKey = getElementKey(name, alterable, description, notes, pType.getId());
         InterfaceElementImportToken element = InterfaceElementImportToken.SENTINEL;
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
               previousElement.getName(), name, previousPType.getId(), pType.getId())) {
            String[] nameSplit = name.split(" ");
            int arrayNum = Integer.parseInt(nameSplit[nameSplit.length - 1]);
            if (previousElement.getInterfaceElementIndexStart() == 0) {
               String[] prevNameSplit = previousElement.getName().split(" ");
               int prevArrayNum = Integer.parseInt(prevNameSplit[prevNameSplit.length - 1]);
               previousElement.setInterfaceElementIndexStart(prevArrayNum);
            } else if (previousElement.getName().matches(".*\\s\\d+$")) {
               // Remove number from previous element name
               previousElement.setName(previousElement.getName().split("\\s\\d+$")[0].trim());
            }
            previousElement.setInterfaceElementIndexEnd(arrayNum);
            relateElement = false;
         } else {
            element = new InterfaceElementImportToken(id, name);
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

   private String getEnumNameKey(String enumName) {
      return enumName.toUpperCase().replace(" ", "_");
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
