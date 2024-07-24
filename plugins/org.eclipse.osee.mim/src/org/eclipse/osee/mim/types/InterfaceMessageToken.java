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
package org.eclipse.osee.mim.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.accessor.types.ArtifactAccessorResult;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceMessageToken extends ArtifactAccessorResult {
   public static final InterfaceMessageToken SENTINEL = new InterfaceMessageToken();

   private String InterfaceMessageNumber; //required
   private String InterfaceMessagePeriodicity; //required
   private String InterfaceMessageRate;
   private Boolean InterfaceMessageWriteAccess; //required
   private String InterfaceMessageType; //required
   private Boolean interfaceMessageExclude;
   private String interfaceMessageIoMode;
   private String interfaceMessageModeCode;
   private String interfaceMessageRateVer;
   private String interfaceMessagePriority;
   private String interfaceMessageProtocol;
   private String interfaceMessageRptWordCount;
   private String interfaceMessageRptCmdWord;
   private Boolean interfaceMessageRunBeforeProc;
   private String interfaceMessageVer;

   private String Description;
   private List<InterfaceSubMessageToken> subMessages = new LinkedList<InterfaceSubMessageToken>();
   private List<InterfaceNode> publisherNodes = new LinkedList<>();
   private List<InterfaceNode> subscriberNodes = new LinkedList<>();
   private ApplicabilityToken applicability;

   public InterfaceMessageToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InterfaceMessageToken(ArtifactReadable art) {
      super(art);
      this.setId(art.getId());
      this.setName(art.getName());
      this.setDescription(art.getSoleAttributeValue(CoreAttributeTypes.Description, ""));
      this.setInterfaceMessageNumber(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceMessageNumber, ""));
      this.setInterfaceMessagePeriodicity(
         art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessagePeriodicity, ""));
      this.setInterfaceMessageRate(art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate, ""));
      this.setInterfaceMessageType(art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageType, ""));
      this.setInterfaceMessageWriteAccess(
         art.getSoleAttributeValue(CoreAttributeTypes.InterfaceMessageWriteAccess, false));
      this.setInterfaceMessageExclude(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceMessageExclude, false));
      this.setInterfaceMessageIoMode(art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageIoMode, ""));
      this.setInterfaceMessageModeCode(art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageModeCode, ""));
      this.setInterfaceMessageRateVer(art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRateVer, ""));
      this.setInterfaceMessagePriority(art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessagePriority, ""));
      this.setInterfaceMessageProtocol(art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageProtocol, ""));
      this.setInterfaceMessageRptWordCount(
         art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRptWordCount, ""));
      this.setInterfaceMessageRptCmdWord(
         art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRptCmdWord, ""));
      this.setInterfaceMessageRunBeforeProc(
         art.getSoleAttributeValue(CoreAttributeTypes.InterfaceMessageRunBeforeProc, false));
      this.setInterfaceMessageVer(art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageVer, ""));
      this.setSubMessages(
         art.getRelated(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage).getList().stream().filter(
            a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new InterfaceSubMessageToken(a)).collect(
               Collectors.toList()));
      this.setPublisherNodes(art.getRelated(CoreRelationTypes.InterfaceMessagePubNode_Node).getList().stream().filter(
         a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new InterfaceNode(a)).collect(Collectors.toList()));
      this.setSubscriberNodes(art.getRelated(CoreRelationTypes.InterfaceMessageSubNode_Node).getList().stream().filter(
         a -> !a.getExistingAttributeTypes().isEmpty()).map(a -> new InterfaceNode(a)).collect(Collectors.toList()));
      this.setApplicability(
         !art.getApplicabilityToken().getId().equals(-1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
   }

   public InterfaceMessageToken(Long id, String name) {
      super(id, name);
      this.setInterfaceMessageNumber("");
      this.setInterfaceMessagePeriodicity("");
      this.setInterfaceMessageRate("");
      this.setInterfaceMessageWriteAccess(false);
      this.setInterfaceMessageType("");
      this.setInterfaceMessageExclude(false);
      this.setInterfaceMessageIoMode("");
      this.setInterfaceMessageModeCode("");
      this.setInterfaceMessageRateVer("");
      this.setInterfaceMessagePriority("");
      this.setInterfaceMessageProtocol("");
      this.setInterfaceMessageRptWordCount("");
      this.setInterfaceMessageRptCmdWord("");
      this.setInterfaceMessageRunBeforeProc(false);
      this.setInterfaceMessageVer("");
   }

   public InterfaceMessageToken() {
      super();
   }

   /**
    * @return the interfaceMessageNumber
    */
   public String getInterfaceMessageNumber() {
      return InterfaceMessageNumber;
   }

   /**
    * @param interfaceMessageNumber the interfaceMessageNumber to set
    */
   public void setInterfaceMessageNumber(String interfaceMessageNumber) {
      InterfaceMessageNumber = interfaceMessageNumber;
   }

   /**
    * @return the interfaceMessagePeriodicity
    */
   public String getInterfaceMessagePeriodicity() {
      return InterfaceMessagePeriodicity;
   }

   /**
    * @param interfaceMessagePeriodicity the interfaceMessagePeriodicity to set
    */
   public void setInterfaceMessagePeriodicity(String interfaceMessagePeriodicity) {
      InterfaceMessagePeriodicity = interfaceMessagePeriodicity;
   }

   /**
    * @return the interfaceMessageRate
    */
   public String getInterfaceMessageRate() {
      return InterfaceMessageRate;
   }

   /**
    * @param interfaceMessageRate the interfaceMessageRate to set
    */
   public void setInterfaceMessageRate(String interfaceMessageRate) {
      InterfaceMessageRate = interfaceMessageRate;
   }

   /**
    * @return the interfaceMessageWriteAccess
    */
   public Boolean getInterfaceMessageWriteAccess() {
      return InterfaceMessageWriteAccess;
   }

   /**
    * @param interfaceMessageWriteAccess the interfaceMessageWriteAccess to set
    */
   public void setInterfaceMessageWriteAccess(Boolean interfaceMessageWriteAccess) {
      InterfaceMessageWriteAccess = interfaceMessageWriteAccess;
   }

   /**
    * @return the interfaceMessageType
    */
   public String getInterfaceMessageType() {
      return InterfaceMessageType;
   }

   /**
    * @param interfaceMessageType the interfaceMessageType to set
    */
   public void setInterfaceMessageType(String interfaceMessageType) {
      InterfaceMessageType = interfaceMessageType;
   }

   /**
    * @return the subMessages
    */
   public Collection<InterfaceSubMessageToken> getSubMessages() {
      return this.subMessages;
   }

   public void setSubMessages(List<InterfaceSubMessageToken> subMessages) {
      this.subMessages = subMessages;
   }

   /**
    * @return the description
    */
   public String getDescription() {
      return Description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description) {
      this.Description = description;
   }

   /**
    * @return the applicability
    */
   public ApplicabilityToken getApplicability() {
      return applicability;
   }

   /**
    * @param applicability the applicability to set
    */
   public void setApplicability(ApplicabilityToken applicability) {
      this.applicability = applicability;
   }

   public List<InterfaceNode> getPublisherNodes() {
      return publisherNodes;
   }

   public void setPublisherNodes(List<InterfaceNode> publisherNodes) {
      this.publisherNodes = publisherNodes;
   }

   public List<InterfaceNode> getSubscriberNodes() {
      return subscriberNodes;
   }

   public void setSubscriberNodes(List<InterfaceNode> subscriberNodes) {
      this.subscriberNodes = subscriberNodes;
   }

   public Boolean getInterfaceMessageExclude() {
      return interfaceMessageExclude;
   }

   public void setInterfaceMessageExclude(Boolean interfaceMessageExclude) {
      this.interfaceMessageExclude = interfaceMessageExclude;
   }

   public String getInterfaceMessageIoMode() {
      return interfaceMessageIoMode;
   }

   public void setInterfaceMessageIoMode(String interfaceMessageIoMode) {
      this.interfaceMessageIoMode = interfaceMessageIoMode;
   }

   public String getInterfaceMessageModeCode() {
      return interfaceMessageModeCode;
   }

   public void setInterfaceMessageModeCode(String interfaceMessageModeCode) {
      this.interfaceMessageModeCode = interfaceMessageModeCode;
   }

   public String getInterfaceMessageRateVer() {
      return interfaceMessageRateVer;
   }

   public void setInterfaceMessageRateVer(String interfaceMessageRateVer) {
      this.interfaceMessageRateVer = interfaceMessageRateVer;
   }

   public String getInterfaceMessagePriority() {
      return interfaceMessagePriority;
   }

   public void setInterfaceMessagePriority(String interfaceMessagePriority) {
      this.interfaceMessagePriority = interfaceMessagePriority;
   }

   public String getInterfaceMessageProtocol() {
      return interfaceMessageProtocol;
   }

   public void setInterfaceMessageProtocol(String interfaceMessageProtocol) {
      this.interfaceMessageProtocol = interfaceMessageProtocol;
   }

   public String getInterfaceMessageRptWordCount() {
      return interfaceMessageRptWordCount;
   }

   public void setInterfaceMessageRptWordCount(String interfaceMessageRptWordCount) {
      this.interfaceMessageRptWordCount = interfaceMessageRptWordCount;
   }

   public String getInterfaceMessageRptCmdWord() {
      return interfaceMessageRptCmdWord;
   }

   public void setInterfaceMessageRptCmdWord(String interfaceMessageRptCmdWord) {
      this.interfaceMessageRptCmdWord = interfaceMessageRptCmdWord;
   }

   public Boolean getInterfaceMessageRunBeforeProc() {
      return interfaceMessageRunBeforeProc;
   }

   public void setInterfaceMessageRunBeforeProc(Boolean interfaceMessageRunBeforeProc) {
      this.interfaceMessageRunBeforeProc = interfaceMessageRunBeforeProc;
   }

   public String getInterfaceMessageVer() {
      return interfaceMessageVer;
   }

   public void setInterfaceMessageVer(String interfaceMessageVer) {
      this.interfaceMessageVer = interfaceMessageVer;
   }

   public CreateArtifact createArtifact(String key, ApplicabilityId applicId) {
      // @formatter:off
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.Description, this.getDescription());
      values.put(CoreAttributeTypes.InterfaceMessageNumber, this.getInterfaceMessageNumber());
      values.put(CoreAttributeTypes.InterfaceMessagePeriodicity, this.getInterfaceMessagePeriodicity());
      values.put(CoreAttributeTypes.InterfaceMessageRate, this.getInterfaceMessageRate());
      values.put(CoreAttributeTypes.InterfaceMessageWriteAccess, Boolean.toString(this.getInterfaceMessageWriteAccess()));
      values.put(CoreAttributeTypes.InterfaceMessageType, this.getInterfaceMessageType());
      values.put(CoreAttributeTypes.InterfaceMessageExclude, Boolean.toString(this.getInterfaceMessageExclude()));
      values.put(CoreAttributeTypes.InterfaceMessageIoMode, this.getInterfaceMessageIoMode());
      values.put(CoreAttributeTypes.InterfaceMessageModeCode, this.getInterfaceMessageModeCode());
      values.put(CoreAttributeTypes.InterfaceMessageRateVer, this.getInterfaceMessageRateVer());
      values.put(CoreAttributeTypes.InterfaceMessagePriority, this.getInterfaceMessagePriority());
      values.put(CoreAttributeTypes.InterfaceMessageProtocol, this.getInterfaceMessageProtocol());
      values.put(CoreAttributeTypes.InterfaceMessageRptWordCount, this.getInterfaceMessageRptWordCount());
      values.put(CoreAttributeTypes.InterfaceMessageRptCmdWord, this.getInterfaceMessageRptCmdWord());
      values.put(CoreAttributeTypes.InterfaceMessageRunBeforeProc, Boolean.toString(this.getInterfaceMessageRunBeforeProc()));
      values.put(CoreAttributeTypes.InterfaceMessageVer, this.getInterfaceMessageVer());
      // @formatter:on

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName());
      art.setTypeId(CoreArtifactTypes.InterfaceMessage.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.InterfaceMessage.getValidAttributeTypes()) {
         String value = values.get(type);
         if (Strings.isInValid(value)) {
            continue;
         }
         Attribute attr = new Attribute(type.getIdString());
         attr.setValue(Arrays.asList(value));
         attrs.add(attr);
      }

      art.setAttributes(attrs);
      art.setApplicabilityId(applicId.getIdString());
      art.setkey(key);
      return art;
   }

}
