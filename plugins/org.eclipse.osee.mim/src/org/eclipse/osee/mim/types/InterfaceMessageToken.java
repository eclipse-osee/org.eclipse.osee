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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithGammas;
import org.eclipse.osee.accessor.types.AttributePojo;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceMessageToken extends ArtifactAccessorResultWithGammas {
   public static final InterfaceMessageToken SENTINEL = new InterfaceMessageToken();

   private AttributePojo<String> InterfaceMessageNumber =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageNumber, GammaId.SENTINEL, "", "");
   private AttributePojo<String> InterfaceMessagePeriodicity =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessagePeriodicity, GammaId.SENTINEL, "", "");
   private AttributePojo<String> InterfaceMessageRate =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageRate, GammaId.SENTINEL, "", "");
   private AttributePojo<Boolean> InterfaceMessageWriteAccess =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageWriteAccess, GammaId.SENTINEL, false, "");
   private AttributePojo<String> InterfaceMessageType =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageType, GammaId.SENTINEL, "", "");
   private AttributePojo<Boolean> interfaceMessageExclude =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageExclude, GammaId.SENTINEL, false, "");
   private AttributePojo<String> interfaceMessageIoMode =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageIoMode, GammaId.SENTINEL, "", "");
   private AttributePojo<String> interfaceMessageModeCode =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageModeCode, GammaId.SENTINEL, "", "");
   private AttributePojo<String> interfaceMessageRateVer =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageRateVer, GammaId.SENTINEL, "", "");
   private AttributePojo<String> interfaceMessagePriority =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessagePriority, GammaId.SENTINEL, "", "");
   private AttributePojo<String> interfaceMessageProtocol =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageProtocol, GammaId.SENTINEL, "", "");
   private AttributePojo<String> interfaceMessageRptWordCount =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageRptWordCount, GammaId.SENTINEL, "", "");
   private AttributePojo<String> interfaceMessageRptCmdWord =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageRptCmdWord, GammaId.SENTINEL, "", "");
   private AttributePojo<Boolean> interfaceMessageRunBeforeProc =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageRunBeforeProc, GammaId.SENTINEL, false, "");
   private AttributePojo<String> interfaceMessageVer =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageVer, GammaId.SENTINEL, "", "");

   private AttributePojo<String> Description =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Description, GammaId.SENTINEL, "", "");
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
      this.setName(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.Name, "")));
      this.setDescription(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.Description, "")));
      this.setInterfaceMessageNumber(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceMessageNumber, "")));
      this.setInterfaceMessagePeriodicity(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceMessagePeriodicity, "")));
      this.setInterfaceMessageRate(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceMessageRate, "")));
      this.setInterfaceMessageType(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceMessageType, "")));
      this.setInterfaceMessageWriteAccess(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceMessageWriteAccess, false)));
      this.setInterfaceMessageExclude(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceMessageExclude, false)));
      this.setInterfaceMessageIoMode(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceMessageIoMode, "")));
      this.setInterfaceMessageModeCode(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceMessageModeCode, "")));
      this.setInterfaceMessageRateVer(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceMessageRateVer, "")));
      this.setInterfaceMessagePriority(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceMessagePriority, "")));
      this.setInterfaceMessageProtocol(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceMessageProtocol, "")));
      this.setInterfaceMessageRptWordCount(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceMessageRptWordCount, "")));
      this.setInterfaceMessageRptCmdWord(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceMessageRptCmdWord, "")));
      this.setInterfaceMessageRunBeforeProc(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceMessageRunBeforeProc, false)));
      this.setInterfaceMessageVer(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceMessageVer, "")));
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
      super(id, AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Name, GammaId.SENTINEL, name, ""));
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
   public AttributePojo<String> getInterfaceMessageNumber() {
      return InterfaceMessageNumber;
   }

   /**
    * @param interfaceMessageNumber the interfaceMessageNumber to set
    */
   public void setInterfaceMessageNumber(String interfaceMessageNumber) {
      InterfaceMessageNumber = AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageNumber,
         GammaId.SENTINEL, interfaceMessageNumber, "");
   }

   @JsonProperty
   public void setInterfaceMessageNumber(AttributePojo<String> interfaceMessageNumber) {
      InterfaceMessageNumber = interfaceMessageNumber;
   }

   /**
    * @return the interfaceMessagePeriodicity
    */
   public AttributePojo<String> getInterfaceMessagePeriodicity() {
      return InterfaceMessagePeriodicity;
   }

   /**
    * @param interfaceMessagePeriodicity the interfaceMessagePeriodicity to set
    */
   public void setInterfaceMessagePeriodicity(String interfaceMessagePeriodicity) {
      InterfaceMessagePeriodicity = AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessagePeriodicity,
         GammaId.SENTINEL, interfaceMessagePeriodicity, "");
   }

   @JsonProperty
   public void setInterfaceMessagePeriodicity(AttributePojo<String> interfaceMessagePeriodicity) {
      InterfaceMessagePeriodicity = interfaceMessagePeriodicity;
   }

   /**
    * @return the interfaceMessageRate
    */
   public AttributePojo<String> getInterfaceMessageRate() {
      return InterfaceMessageRate;
   }

   /**
    * @param interfaceMessageRate the interfaceMessageRate to set
    */
   public void setInterfaceMessageRate(String interfaceMessageRate) {
      InterfaceMessageRate = AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageRate,
         GammaId.SENTINEL, interfaceMessageRate, "");
   }

   @JsonProperty
   public void setInterfaceMessageRate(AttributePojo<String> interfaceMessageRate) {
      InterfaceMessageRate = interfaceMessageRate;
   }

   /**
    * @return the interfaceMessageWriteAccess
    */
   public AttributePojo<Boolean> getInterfaceMessageWriteAccess() {
      return InterfaceMessageWriteAccess;
   }

   /**
    * @param interfaceMessageWriteAccess the interfaceMessageWriteAccess to set
    */
   public void setInterfaceMessageWriteAccess(Boolean interfaceMessageWriteAccess) {
      InterfaceMessageWriteAccess = AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageWriteAccess,
         GammaId.SENTINEL, interfaceMessageWriteAccess, "");
   }

   @JsonProperty
   public void setInterfaceMessageWriteAccess(AttributePojo<Boolean> interfaceMessageWriteAccess) {
      InterfaceMessageWriteAccess = interfaceMessageWriteAccess;
   }

   /**
    * @return the interfaceMessageType
    */
   public AttributePojo<String> getInterfaceMessageType() {
      return InterfaceMessageType;
   }

   /**
    * @param interfaceMessageType the interfaceMessageType to set
    */
   public void setInterfaceMessageType(String interfaceMessageType) {
      InterfaceMessageType = AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageType,
         GammaId.SENTINEL, interfaceMessageType, "");
   }

   @JsonProperty
   public void setInterfaceMessageType(AttributePojo<String> interfaceMessageType) {
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
   public AttributePojo<String> getDescription() {
      return Description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description) {
      this.Description =
         AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Description, GammaId.SENTINEL, description, "");
   }

   @JsonProperty
   public void setDescription(AttributePojo<String> description) {
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

   public AttributePojo<Boolean> getInterfaceMessageExclude() {
      return interfaceMessageExclude;
   }

   public void setInterfaceMessageExclude(Boolean interfaceMessageExclude) {
      this.interfaceMessageExclude = AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageExclude,
         GammaId.SENTINEL, interfaceMessageExclude, "");
   }

   @JsonProperty
   public void setInterfaceMessageExclude(AttributePojo<Boolean> interfaceMessageExclude) {
      this.interfaceMessageExclude = interfaceMessageExclude;
   }

   public AttributePojo<String> getInterfaceMessageIoMode() {
      return interfaceMessageIoMode;
   }

   public void setInterfaceMessageIoMode(String interfaceMessageIoMode) {
      this.interfaceMessageIoMode = AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageIoMode,
         GammaId.SENTINEL, interfaceMessageIoMode, "");
   }

   @JsonProperty
   public void setInterfaceMessageIoMode(AttributePojo<String> interfaceMessageIoMode) {
      this.interfaceMessageIoMode = interfaceMessageIoMode;
   }

   public AttributePojo<String> getInterfaceMessageModeCode() {
      return interfaceMessageModeCode;
   }

   public void setInterfaceMessageModeCode(String interfaceMessageModeCode) {
      this.interfaceMessageModeCode = AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageModeCode,
         GammaId.SENTINEL, interfaceMessageModeCode, "");
   }

   @JsonProperty
   public void setInterfaceMessageModeCode(AttributePojo<String> interfaceMessageModeCode) {
      this.interfaceMessageModeCode = interfaceMessageModeCode;
   }

   public AttributePojo<String> getInterfaceMessageRateVer() {
      return interfaceMessageRateVer;
   }

   public void setInterfaceMessageRateVer(String interfaceMessageRateVer) {
      this.interfaceMessageRateVer = AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageRateVer,
         GammaId.SENTINEL, interfaceMessageRateVer, "");
   }

   @JsonProperty
   public void setInterfaceMessageRateVer(AttributePojo<String> interfaceMessageRateVer) {
      this.interfaceMessageRateVer = interfaceMessageRateVer;
   }

   public AttributePojo<String> getInterfaceMessagePriority() {
      return interfaceMessagePriority;
   }

   public void setInterfaceMessagePriority(String interfaceMessagePriority) {
      this.interfaceMessagePriority = AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessagePriority,
         GammaId.SENTINEL, interfaceMessagePriority, "");
   }

   @JsonProperty
   public void setInterfaceMessagePriority(AttributePojo<String> interfaceMessagePriority) {
      this.interfaceMessagePriority = interfaceMessagePriority;
   }

   public AttributePojo<String> getInterfaceMessageProtocol() {
      return interfaceMessageProtocol;
   }

   public void setInterfaceMessageProtocol(String interfaceMessageProtocol) {
      this.interfaceMessageProtocol = AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageProtocol,
         GammaId.SENTINEL, interfaceMessageProtocol, "");
   }

   @JsonProperty
   public void setInterfaceMessageProtocol(AttributePojo<String> interfaceMessageProtocol) {
      this.interfaceMessageProtocol = interfaceMessageProtocol;
   }

   public AttributePojo<String> getInterfaceMessageRptWordCount() {
      return interfaceMessageRptWordCount;
   }

   public void setInterfaceMessageRptWordCount(String interfaceMessageRptWordCount) {
      this.interfaceMessageRptWordCount = AttributePojo.valueOf(Id.SENTINEL,
         CoreAttributeTypes.InterfaceMessageRptWordCount, GammaId.SENTINEL, interfaceMessageRptWordCount, "");
   }

   @JsonProperty
   public void setInterfaceMessageRptWordCount(AttributePojo<String> interfaceMessageRptWordCount) {
      this.interfaceMessageRptWordCount = interfaceMessageRptWordCount;
   }

   public AttributePojo<String> getInterfaceMessageRptCmdWord() {
      return interfaceMessageRptCmdWord;
   }

   public void setInterfaceMessageRptCmdWord(String interfaceMessageRptCmdWord) {
      this.interfaceMessageRptCmdWord = AttributePojo.valueOf(Id.SENTINEL,
         CoreAttributeTypes.InterfaceMessageRptCmdWord, GammaId.SENTINEL, interfaceMessageRptCmdWord, "");
   }

   @JsonProperty
   public void setInterfaceMessageRptCmdWord(AttributePojo<String> interfaceMessageRptCmdWord) {
      this.interfaceMessageRptCmdWord = interfaceMessageRptCmdWord;
   }

   public AttributePojo<Boolean> getInterfaceMessageRunBeforeProc() {
      return interfaceMessageRunBeforeProc;
   }

   public void setInterfaceMessageRunBeforeProc(Boolean interfaceMessageRunBeforeProc) {
      this.interfaceMessageRunBeforeProc = AttributePojo.valueOf(Id.SENTINEL,
         CoreAttributeTypes.InterfaceMessageRunBeforeProc, GammaId.SENTINEL, interfaceMessageRunBeforeProc, "");
   }

   @JsonProperty
   public void setInterfaceMessageRunBeforeProc(AttributePojo<Boolean> interfaceMessageRunBeforeProc) {
      this.interfaceMessageRunBeforeProc = interfaceMessageRunBeforeProc;
   }

   public AttributePojo<String> getInterfaceMessageVer() {
      return interfaceMessageVer;
   }

   public void setInterfaceMessageVer(String interfaceMessageVer) {
      this.interfaceMessageVer = AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceMessageVer,
         GammaId.SENTINEL, interfaceMessageVer, "");
   }

   @JsonProperty
   public void setInterfaceMessageVer(AttributePojo<String> interfaceMessageVer) {
      this.interfaceMessageVer = interfaceMessageVer;
   }

   public CreateArtifact createArtifact(String key, ApplicabilityId applicId) {
      // @formatter:off
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.Description, this.getDescription().getValue());
      values.put(CoreAttributeTypes.InterfaceMessageNumber, this.getInterfaceMessageNumber().getValue());
      values.put(CoreAttributeTypes.InterfaceMessagePeriodicity, this.getInterfaceMessagePeriodicity().getValue());
      values.put(CoreAttributeTypes.InterfaceMessageRate, this.getInterfaceMessageRate().getValue());
      values.put(CoreAttributeTypes.InterfaceMessageWriteAccess, Boolean.toString(this.getInterfaceMessageWriteAccess().getValue()));
      values.put(CoreAttributeTypes.InterfaceMessageType, this.getInterfaceMessageType().getValue());
      values.put(CoreAttributeTypes.InterfaceMessageExclude, Boolean.toString(this.getInterfaceMessageExclude().getValue()));
      values.put(CoreAttributeTypes.InterfaceMessageIoMode, this.getInterfaceMessageIoMode().getValue());
      values.put(CoreAttributeTypes.InterfaceMessageModeCode, this.getInterfaceMessageModeCode().getValue());
      values.put(CoreAttributeTypes.InterfaceMessageRateVer, this.getInterfaceMessageRateVer().getValue());
      values.put(CoreAttributeTypes.InterfaceMessagePriority, this.getInterfaceMessagePriority().getValue());
      values.put(CoreAttributeTypes.InterfaceMessageProtocol, this.getInterfaceMessageProtocol().getValue());
      values.put(CoreAttributeTypes.InterfaceMessageRptWordCount, this.getInterfaceMessageRptWordCount().getValue());
      values.put(CoreAttributeTypes.InterfaceMessageRptCmdWord, this.getInterfaceMessageRptCmdWord().getValue());
      values.put(CoreAttributeTypes.InterfaceMessageRunBeforeProc, Boolean.toString(this.getInterfaceMessageRunBeforeProc().getValue()));
      values.put(CoreAttributeTypes.InterfaceMessageVer, this.getInterfaceMessageVer().getValue());
      // @formatter:on

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName().getValue());
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

   @JsonIgnore
   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }
      if (obj instanceof InterfaceMessageToken) {
         InterfaceMessageToken other = ((InterfaceMessageToken) obj);
         if (!this.getName().valueEquals(other.getName())) {
            return false;
         }
         if (!this.getDescription().valueEquals(other.getDescription())) {
            return false;
         }
         if (!this.getInterfaceMessageNumber().valueEquals(other.getInterfaceMessageNumber())) {
            return false;
         }
         if (!this.getInterfaceMessagePeriodicity().valueEquals(other.getInterfaceMessagePeriodicity())) {
            return false;
         }
         if (!this.getInterfaceMessageRate().valueEquals(other.getInterfaceMessageRate())) {
            return false;
         }
         if (!this.getInterfaceMessageWriteAccess().valueEquals(other.getInterfaceMessageWriteAccess())) {
            return false;
         }
         if (!this.getInterfaceMessageType().valueEquals(other.getInterfaceMessageType())) {
            return false;
         }
         if (!this.getInterfaceMessageExclude().valueEquals(other.getInterfaceMessageExclude())) {
            return false;
         }
         if (!this.getInterfaceMessageIoMode().valueEquals(other.getInterfaceMessageIoMode())) {
            return false;
         }
         if (!this.getInterfaceMessageModeCode().valueEquals(other.getInterfaceMessageModeCode())) {
            return false;
         }
         if (!this.getInterfaceMessageRateVer().valueEquals(other.getInterfaceMessageRateVer())) {
            return false;
         }
         if (!this.getInterfaceMessagePriority().valueEquals(other.getInterfaceMessagePriority())) {
            return false;
         }
         if (!this.getInterfaceMessageProtocol().valueEquals(other.getInterfaceMessageProtocol())) {
            return false;
         }
         if (!this.getInterfaceMessageRptWordCount().valueEquals(other.getInterfaceMessageRptWordCount())) {
            return false;
         }
         if (!this.getInterfaceMessageRptCmdWord().valueEquals(other.getInterfaceMessageRptCmdWord())) {
            return false;
         }
         if (!this.getInterfaceMessageRunBeforeProc().valueEquals(other.getInterfaceMessageRunBeforeProc())) {
            return false;
         }
         if (!this.getInterfaceMessageVer().valueEquals(other.getInterfaceMessageVer())) {
            return false;
         }
         if (!this.getPublisherNodes().equals(other.getPublisherNodes())) {
            return false;
         }
         if (!this.getSubscriberNodes().equals(other.getSubscriberNodes())) {
            return false;
         }
         if (!this.getSubMessages().equals(other.getSubMessages())) {
            return false;
         }
         return true;
      }
      return false;
   }
}
