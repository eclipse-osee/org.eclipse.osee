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
package org.eclipse.osee.mim.types;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MimImportSummary {
   private boolean createPrimaryNode;
   private boolean createSecondaryNode;
   private InterfaceNode primaryNode;
   private InterfaceNode secondaryNode;
   private final List<InterfaceMessageToken> messages;
   private final List<InterfaceSubMessageToken> subMessages;
   private final List<InterfaceStructureToken> structures;
   private final List<InterfaceElementImportToken> elements;
   private final List<PlatformTypeImportToken> platformTypes;
   private final List<InterfaceEnumerationSet> enumSets;
   private final List<InterfaceEnumeration> enums;
   private final Map<String, List<String>> messageSubmessageRelations;
   private final Map<String, List<String>> subMessageStructureRelations;
   private final Map<String, List<String>> structureElementRelations;
   private final Map<String, List<String>> elementPlatformTypeRelations;
   private final Map<String, List<String>> platformTypeEnumSetRelations;
   private final Map<String, List<String>> enumSetEnumRelations;

   public MimImportSummary() {
      createPrimaryNode = false;
      createSecondaryNode = false;
      messages = new LinkedList<>();
      subMessages = new LinkedList<>();
      structures = new LinkedList<>();
      elements = new LinkedList<>();
      platformTypes = new LinkedList<>();
      enumSets = new LinkedList<>();
      enums = new LinkedList<>();
      messageSubmessageRelations = new HashMap<>();
      subMessageStructureRelations = new HashMap<>();
      structureElementRelations = new HashMap<>();
      elementPlatformTypeRelations = new HashMap<>();
      platformTypeEnumSetRelations = new HashMap<>();
      enumSetEnumRelations = new HashMap<>();
   }

   public boolean isCreatePrimaryNode() {
      return createPrimaryNode;
   }

   public void setCreatePrimaryNode(boolean createPrimaryNode) {
      this.createPrimaryNode = createPrimaryNode;
   }

   public boolean isCreateSecondaryNode() {
      return createSecondaryNode;
   }

   public void setCreateSecondaryNode(boolean createSecondaryNode) {
      this.createSecondaryNode = createSecondaryNode;
   }

   public InterfaceNode getPrimaryNode() {
      return primaryNode;
   }

   public void setPrimaryNode(InterfaceNode primaryNode) {
      this.primaryNode = primaryNode;
   }

   public InterfaceNode getSecondaryNode() {
      return secondaryNode;
   }

   public void setSecondaryNode(InterfaceNode secondaryNode) {
      this.secondaryNode = secondaryNode;
   }

   public List<InterfaceMessageToken> getMessages() {
      return messages;
   }

   public List<InterfaceSubMessageToken> getSubMessages() {
      return subMessages;
   }

   public List<InterfaceStructureToken> getStructures() {
      return structures;
   }

   public List<InterfaceElementImportToken> getElements() {
      return elements;
   }

   public List<PlatformTypeImportToken> getPlatformTypes() {
      return platformTypes;
   }

   public List<InterfaceEnumerationSet> getEnumSets() {
      return enumSets;
   }

   public List<InterfaceEnumeration> getEnums() {
      return enums;
   }

   public Map<String, List<String>> getMessageSubmessageRelations() {
      return messageSubmessageRelations;
   }

   public Map<String, List<String>> getSubMessageStructureRelations() {
      return subMessageStructureRelations;
   }

   public Map<String, List<String>> getStructureElementRelations() {
      return structureElementRelations;
   }

   public Map<String, List<String>> getElementPlatformTypeRelations() {
      return elementPlatformTypeRelations;
   }

   public Map<String, List<String>> getPlatformTypeEnumSetRelations() {
      return platformTypeEnumSetRelations;
   }

   public Map<String, List<String>> getEnumSetEnumRelations() {
      return enumSetEnumRelations;
   }

}
