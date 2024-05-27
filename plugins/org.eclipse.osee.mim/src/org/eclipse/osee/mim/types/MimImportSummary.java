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
   private String txComment;
   private final List<TransportType> transportTypes;
   private final List<InterfaceNode> nodes;
   private final List<InterfaceConnection> connections;
   private final List<InterfaceMessageToken> messages;
   private final List<InterfaceSubMessageToken> subMessages;
   private final List<InterfaceStructureToken> structures;
   private final List<InterfaceStructureElementToken> elements;
   private final List<PlatformTypeToken> platformTypes;
   private final List<InterfaceEnumerationSet> enumSets;
   private final List<InterfaceEnumeration> enums;
   private final List<CrossReference> crossReferences;
   private final Map<String, List<String>> connectionTransportTypeRelations;
   private final Map<String, List<String>> connectionNodeRelations;
   private final Map<String, List<String>> connectionMessageRelations;
   private final Map<String, List<String>> messagePublisherNodeRelations;
   private final Map<String, List<String>> messageSubscriberNodeRelations;
   private final Map<String, List<String>> messageSubmessageRelations;
   private final Map<String, List<String>> subMessageStructureRelations;
   private final Map<String, List<String>> structureElementRelations;
   private final Map<String, List<String>> elementPlatformTypeRelations;
   private final Map<String, List<String>> platformTypeEnumSetRelations;
   private final Map<String, List<String>> enumSetEnumRelations;
   private final Map<String, List<String>> connectionCrossReferenceRelations;
   private final List<String> errors;

   public MimImportSummary() {
      txComment = "MIM Import";
      transportTypes = new LinkedList<>();
      nodes = new LinkedList<>();
      connections = new LinkedList<>();
      messages = new LinkedList<>();
      subMessages = new LinkedList<>();
      structures = new LinkedList<>();
      elements = new LinkedList<>();
      platformTypes = new LinkedList<>();
      enumSets = new LinkedList<>();
      enums = new LinkedList<>();
      crossReferences = new LinkedList<>();
      connectionTransportTypeRelations = new HashMap<>();
      connectionNodeRelations = new HashMap<>();
      connectionMessageRelations = new HashMap<>();
      messagePublisherNodeRelations = new HashMap<>();
      messageSubscriberNodeRelations = new HashMap<>();
      messageSubmessageRelations = new HashMap<>();
      subMessageStructureRelations = new HashMap<>();
      structureElementRelations = new HashMap<>();
      elementPlatformTypeRelations = new HashMap<>();
      platformTypeEnumSetRelations = new HashMap<>();
      enumSetEnumRelations = new HashMap<>();
      connectionCrossReferenceRelations = new HashMap<>();
      errors = new LinkedList<>();
   }

   public String getTxComment() {
      return txComment;
   }

   public void setTxComment(String txComment) {
      this.txComment = txComment;
   }

   public List<TransportType> getTransportTypes() {
      return transportTypes;
   }

   public List<InterfaceNode> getNodes() {
      return nodes;
   }

   public List<InterfaceConnection> getConnections() {
      return connections;
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

   public List<InterfaceStructureElementToken> getElements() {
      return elements;
   }

   public List<PlatformTypeToken> getPlatformTypes() {
      return platformTypes;
   }

   public List<InterfaceEnumerationSet> getEnumSets() {
      return enumSets;
   }

   public List<InterfaceEnumeration> getEnums() {
      return enums;
   }

   public List<CrossReference> getCrossReferences() {
      return crossReferences;
   }

   public Map<String, List<String>> getConnectionTransportTypeRelations() {
      return connectionTransportTypeRelations;
   }

   public Map<String, List<String>> getConnectionNodeRelations() {
      return connectionNodeRelations;
   }

   public Map<String, List<String>> getConnectionMessageRelations() {
      return connectionMessageRelations;
   }

   public Map<String, List<String>> getMessagePublisherNodeRelations() {
      return messagePublisherNodeRelations;
   }

   public Map<String, List<String>> getMessageSubscriberNodeRelations() {
      return messageSubscriberNodeRelations;
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

   public Map<String, List<String>> getConnectionCrossReferenceRelations() {
      return connectionCrossReferenceRelations;
   }

   public boolean isError() {
      return !errors.isEmpty();
   }

   public List<String> getErrors() {
      return errors;
   }

}
