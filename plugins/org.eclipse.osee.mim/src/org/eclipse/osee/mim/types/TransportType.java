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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
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
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

public class TransportType extends ArtifactAccessorResultWithGammas {

   public static final TransportType SENTINEL = new TransportType();

   private AttributePojo<Boolean> byteAlignValidation =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.ByteAlignValidation, GammaId.SENTINEL, false, "");
   private AttributePojo<Boolean> messageGeneration =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.MessageGeneration, GammaId.SENTINEL, false, "");
   private AttributePojo<Integer> byteAlignValidationSize =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.ByteAlignValidationSize, GammaId.SENTINEL, 0, "");
   private AttributePojo<String> messageGenerationType =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.MessageGenerationType, GammaId.SENTINEL, "", "");
   private AttributePojo<String> messageGenerationPosition =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.MessageGenerationPosition, GammaId.SENTINEL, "", "");
   private AttributePojo<Integer> minimumSubscriberMultiplicity =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.MinimumSubscriberMultiplicity, GammaId.SENTINEL, 0, "");
   private AttributePojo<Integer> maximumSubscriberMultiplicity =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.MaximumSubscriberMultiplicity, GammaId.SENTINEL, 0, "");
   private AttributePojo<Integer> minimumPublisherMultiplicity =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.MinimumPublisherMultiplicity, GammaId.SENTINEL, 0, "");
   private AttributePojo<Integer> maximumPublisherMultiplicity =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.MaximumPublisherMultiplicity, GammaId.SENTINEL, 0, "");
   private AttributePojo<Boolean> isDashed =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.DashedPresentation, GammaId.SENTINEL, false, "");
   private AttributePojo<Boolean> spareAutoNumbering =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.SpareAutoNumbering, GammaId.SENTINEL, false, "");
   private AttributePojo<List<String>> interfaceLevelsToUse = AttributePojo.valueOf(Id.SENTINEL,
      CoreAttributeTypes.InterfaceLevelsToUse, GammaId.SENTINEL, new LinkedList<String>(), "");
   private AttributePojo<List<String>> availableMessageHeaders = AttributePojo.valueOf(Id.SENTINEL,
      CoreAttributeTypes.AvailableMessageHeaders, GammaId.SENTINEL, new LinkedList<String>(), "");
   private AttributePojo<List<String>> availableSubmessageHeaders = AttributePojo.valueOf(Id.SENTINEL,
      CoreAttributeTypes.AvailableSubmessageHeaders, GammaId.SENTINEL, new LinkedList<String>(), "");
   private AttributePojo<List<String>> availableStructureHeaders = AttributePojo.valueOf(Id.SENTINEL,
      CoreAttributeTypes.AvailableStructureHeaders, GammaId.SENTINEL, new LinkedList<String>(), "");
   private AttributePojo<List<String>> availableElementHeaders = AttributePojo.valueOf(Id.SENTINEL,
      CoreAttributeTypes.AvailableElementHeaders, GammaId.SENTINEL, new LinkedList<String>(), "");
   private ApplicabilityToken applicability = ApplicabilityToken.SENTINEL;

   public TransportType(ArtifactToken art) {
      super(art);
   }

   public TransportType(ArtifactReadable art) {
      super(art);
      if (art.isValid()) {
         this.setByteAlignValidation(
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.ByteAlignValidation, false)));
         this.setMessageGeneration(
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.MessageGeneration, false)));
         AttributePojo<Integer> validationSize =
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.ByteAlignValidationSize, 0));
         this.setByteAlignValidationSize(
            this.getByteAlignValidation().getValue() ? validationSize : AttributePojo.valueOf(validationSize.getId(),
               CoreAttributeTypes.ByteAlignValidationSize, validationSize.getGammaId(), 0,
               validationSize.getDisplayableString()));
         AttributePojo<String> messageGenerationType =
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.MessageGenerationType, ""));
         this.setMessageGenerationType(
            this.getMessageGeneration().getValue() ? messageGenerationType : AttributePojo.valueOf(
               messageGenerationType.getId(), CoreAttributeTypes.MessageGenerationType,
               messageGenerationType.getGammaId(), "", messageGenerationType.getDisplayableString()));
         AttributePojo<String> messageGenerationPosition =
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.MessageGenerationPosition, ""));
         this.setMessageGenerationPosition(
            this.getMessageGeneration().getValue() ? messageGenerationPosition : AttributePojo.valueOf(
               messageGenerationPosition.getId(), CoreAttributeTypes.MessageGenerationPosition,
               messageGenerationPosition.getGammaId(), "", messageGenerationPosition.getDisplayableString()));
         /**
          * Note the following 4 properties do not have to be a number, if it is "n", that means it should be 0
          */
         AttributePojo<String> minSubMultiplicity =
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.MinimumSubscriberMultiplicity, ""));
         AttributePojo<String> maxSubMultiplicity =
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.MaximumSubscriberMultiplicity, ""));
         AttributePojo<String> minPubMultiplicity =
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.MinimumPublisherMultiplicity, ""));
         AttributePojo<String> maxPubMultiplicity =
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.MaximumPublisherMultiplicity, ""));
         if (Strings.isNumeric(minSubMultiplicity.getValue())) {
            this.setMinimumSubscriberMultiplicity(AttributePojo.valueOf(minSubMultiplicity.getId(),
               minSubMultiplicity.getTypeId(), minSubMultiplicity.getGammaId(),
               Integer.valueOf(minSubMultiplicity.getValue()), minSubMultiplicity.getDisplayableString()));
         } else {
            this.setMinimumSubscriberMultiplicity(
               AttributePojo.valueOf(minSubMultiplicity.getId(), minSubMultiplicity.getTypeId(),
                  minSubMultiplicity.getGammaId(), 0, minSubMultiplicity.getDisplayableString()));
         }
         if (Strings.isNumeric(maxSubMultiplicity.getValue())) {
            this.setMaximumSubscriberMultiplicity(AttributePojo.valueOf(maxSubMultiplicity.getId(),
               maxSubMultiplicity.getTypeId(), maxSubMultiplicity.getGammaId(),
               Integer.valueOf(maxSubMultiplicity.getValue()), maxSubMultiplicity.getDisplayableString()));
         } else {
            this.setMaximumSubscriberMultiplicity(
               AttributePojo.valueOf(maxSubMultiplicity.getId(), maxSubMultiplicity.getTypeId(),
                  maxSubMultiplicity.getGammaId(), 0, maxSubMultiplicity.getDisplayableString()));
         }
         if (Strings.isNumeric(minPubMultiplicity.getValue())) {
            this.setMinimumPublisherMultiplicity(AttributePojo.valueOf(minPubMultiplicity.getId(),
               minPubMultiplicity.getTypeId(), minPubMultiplicity.getGammaId(),
               Integer.valueOf(minPubMultiplicity.getValue()), minPubMultiplicity.getDisplayableString()));
         } else {
            this.setMinimumPublisherMultiplicity(
               AttributePojo.valueOf(minPubMultiplicity.getId(), minPubMultiplicity.getTypeId(),
                  minPubMultiplicity.getGammaId(), 0, minPubMultiplicity.getDisplayableString()));
         }
         if (Strings.isNumeric(maxPubMultiplicity.getValue())) {
            this.setMaximumPublisherMultiplicity(AttributePojo.valueOf(maxPubMultiplicity.getId(),
               maxPubMultiplicity.getTypeId(), maxPubMultiplicity.getGammaId(),
               Integer.valueOf(maxPubMultiplicity.getValue()), maxPubMultiplicity.getDisplayableString()));
         } else {
            this.setMaximumPublisherMultiplicity(
               AttributePojo.valueOf(maxPubMultiplicity.getId(), maxPubMultiplicity.getTypeId(),
                  maxPubMultiplicity.getGammaId(), 0, maxPubMultiplicity.getDisplayableString()));
         }
         /**
          * the following attributes are json arrays
          */
         AttributePojo<String> levels =
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceLevelsToUse, "[]"));
         this.setInterfaceLevelsToUse(AttributePojo.valueOf(levels.getId(), levels.getTypeId(), levels.getGammaId(),
            this.getJSONContents(levels.getValue()), levels.getDisplayableString()));
         AttributePojo<String> messageHeaders =
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.AvailableMessageHeaders, "[]"));
         this.setAvailableMessageHeaders(
            AttributePojo.valueOf(messageHeaders.getId(), messageHeaders.getTypeId(), messageHeaders.getGammaId(),
               this.getJSONContents(messageHeaders.getValue()), messageHeaders.getDisplayableString()));
         AttributePojo<String> submessageHeaders =
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.AvailableSubmessageHeaders, "[]"));
         this.setAvailableSubmessageHeaders(AttributePojo.valueOf(submessageHeaders.getId(),
            submessageHeaders.getTypeId(), submessageHeaders.getGammaId(),
            this.getJSONContents(submessageHeaders.getValue()), submessageHeaders.getDisplayableString()));
         AttributePojo<String> structureHeaders =
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.AvailableStructureHeaders, "[]"));
         this.setAvailableStructureHeaders(
            AttributePojo.valueOf(structureHeaders.getId(), structureHeaders.getTypeId(), structureHeaders.getGammaId(),
               this.getJSONContents(structureHeaders.getValue()), structureHeaders.getDisplayableString()));
         AttributePojo<String> elementHeaders =
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.AvailableElementHeaders, "[]"));
         this.setAvailableElementHeaders(
            AttributePojo.valueOf(elementHeaders.getId(), elementHeaders.getTypeId(), elementHeaders.getGammaId(),
               this.getJSONContents(elementHeaders.getValue()), elementHeaders.getDisplayableString()));
         this.setDashedPresentation(
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.DashedPresentation, false)));
         this.setSpareAutoNumbering(
            AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.SpareAutoNumbering, false)));
         this.setApplicability(!art.getApplicabilityToken().getId().equals(
            -1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
      } else {
         this.setApplicability(ApplicabilityToken.SENTINEL);
      }
   }

   public TransportType(Long id, String name) {
      super(id, AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Name, GammaId.SENTINEL, name, ""));
   }

   public TransportType() {
   }

   /**
    * @return the messageGenerationType
    */
   public AttributePojo<String> getMessageGenerationType() {
      return messageGenerationType;
   }

   /**
    * @param messageGenerationType the messageGenerationType to set
    */
   @JsonProperty
   public void setMessageGenerationType(AttributePojo<String> messageGenerationType) {
      this.messageGenerationType = messageGenerationType;
   }

   public void setMessageGenerationType(String messageGenerationType) {
      this.messageGenerationType = AttributePojo.valueOf(this.messageGenerationType.getId(),
         this.messageGenerationType.getTypeId(), this.messageGenerationType.getGammaId(), messageGenerationType,
         this.messageGenerationType.getDisplayableString());
   }

   /**
    * @return the messageGenerationPosition
    */
   public AttributePojo<String> getMessageGenerationPosition() {
      return messageGenerationPosition;
   }

   /**
    * @param messageGenerationPosition the messageGenerationPosition to set
    */
   @JsonProperty
   public void setMessageGenerationPosition(AttributePojo<String> messageGenerationPosition) {
      this.messageGenerationPosition = messageGenerationPosition;
   }

   public void setMessageGenerationPosition(String messageGenerationPosition) {
      this.messageGenerationType = AttributePojo.valueOf(this.messageGenerationPosition.getId(),
         this.messageGenerationPosition.getTypeId(), this.messageGenerationPosition.getGammaId(),
         messageGenerationPosition, this.messageGenerationPosition.getDisplayableString());
   }

   /**
    * @return the byteAlignValidationSize
    */
   public AttributePojo<Integer> getByteAlignValidationSize() {
      return byteAlignValidationSize;
   }

   /**
    * @param byteAlignValidationSize the byteAlignValidationSize to set
    */
   @JsonProperty
   public void setByteAlignValidationSize(AttributePojo<Integer> byteAlignValidationSize) {
      this.byteAlignValidationSize = byteAlignValidationSize;
   }

   public void setByteAlignValidationSize(Integer byteAlignValidationSize) {
      this.byteAlignValidationSize = AttributePojo.valueOf(this.byteAlignValidationSize.getId(),
         this.byteAlignValidationSize.getTypeId(), this.byteAlignValidationSize.getGammaId(), byteAlignValidationSize,
         this.byteAlignValidationSize.getDisplayableString());
   }

   /**
    * @return the messageGeneration
    */
   public AttributePojo<Boolean> getMessageGeneration() {
      return messageGeneration;
   }

   /**
    * @param messageGeneration the messageGeneration to set
    */
   @JsonProperty
   public void setMessageGeneration(AttributePojo<Boolean> messageGeneration) {
      this.messageGeneration = messageGeneration;
   }

   public void setMessageGeneration(Boolean messageGeneration) {
      this.messageGeneration = AttributePojo.valueOf(this.messageGeneration.getId(), this.messageGeneration.getTypeId(),
         this.messageGeneration.getGammaId(), messageGeneration, this.messageGeneration.getDisplayableString());
   }

   /**
    * @return the byteAlignValidation
    */
   public AttributePojo<Boolean> getByteAlignValidation() {
      return byteAlignValidation;
   }

   /**
    * @param byteAlignValidation the byteAlignValidation to set
    */
   @JsonProperty
   public void setByteAlignValidation(AttributePojo<Boolean> byteAlignValidation) {
      this.byteAlignValidation = byteAlignValidation;
   }

   public void setByteAlignValidation(Boolean byteAlignValidation) {
      this.byteAlignValidation = AttributePojo.valueOf(this.byteAlignValidation.getId(),
         this.byteAlignValidation.getTypeId(), this.byteAlignValidation.getGammaId(), byteAlignValidation,
         this.byteAlignValidation.getDisplayableString());
   }

   public AttributePojo<Integer> getMinimumSubscriberMultiplicity() {
      return minimumSubscriberMultiplicity;
   }

   @JsonProperty
   public void setMinimumSubscriberMultiplicity(AttributePojo<Integer> minimumSubscriberMultiplicity) {
      this.minimumSubscriberMultiplicity = minimumSubscriberMultiplicity;
   }

   public void setMinimumSubscriberMultiplicity(Integer minimumSubscriberMultiplicity) {
      this.minimumSubscriberMultiplicity = AttributePojo.valueOf(this.minimumSubscriberMultiplicity.getId(),
         this.minimumSubscriberMultiplicity.getTypeId(), this.minimumSubscriberMultiplicity.getGammaId(),
         minimumSubscriberMultiplicity, this.minimumSubscriberMultiplicity.getDisplayableString());
   }

   public AttributePojo<Integer> getMaximumSubscriberMultiplicity() {
      return maximumSubscriberMultiplicity;
   }

   @JsonProperty
   public void setMaximumSubscriberMultiplicity(AttributePojo<Integer> maximumSubscriberMultiplicity) {
      this.maximumSubscriberMultiplicity = maximumSubscriberMultiplicity;
   }

   public void setMaximumSubscriberMultiplicity(Integer maximumSubscriberMultiplicity) {
      this.maximumSubscriberMultiplicity = AttributePojo.valueOf(this.maximumSubscriberMultiplicity.getId(),
         this.maximumSubscriberMultiplicity.getTypeId(), this.maximumSubscriberMultiplicity.getGammaId(),
         maximumSubscriberMultiplicity, this.maximumSubscriberMultiplicity.getDisplayableString());
   }

   public AttributePojo<Integer> getMinimumPublisherMultiplicity() {
      return minimumPublisherMultiplicity;
   }

   @JsonProperty
   public void setMinimumPublisherMultiplicity(AttributePojo<Integer> minimumPublisherMultiplicity) {
      this.minimumPublisherMultiplicity = minimumPublisherMultiplicity;
   }

   public void setMinimumPublisherMultiplicity(Integer minimumPublisherMultiplicity) {
      this.minimumPublisherMultiplicity = AttributePojo.valueOf(this.minimumPublisherMultiplicity.getId(),
         this.minimumPublisherMultiplicity.getTypeId(), this.minimumPublisherMultiplicity.getGammaId(),
         minimumPublisherMultiplicity, this.minimumPublisherMultiplicity.getDisplayableString());
   }

   public AttributePojo<Integer> getMaximumPublisherMultiplicity() {
      return maximumPublisherMultiplicity;
   }

   @JsonProperty
   public void setMaximumPublisherMultiplicity(AttributePojo<Integer> maximumPublisherMultiplicity) {
      this.maximumPublisherMultiplicity = maximumPublisherMultiplicity;
   }

   public void setMaximumPublisherMultiplicity(Integer maximumPublisherMultiplicity) {
      this.maximumPublisherMultiplicity = AttributePojo.valueOf(this.maximumPublisherMultiplicity.getId(),
         this.maximumPublisherMultiplicity.getTypeId(), this.maximumPublisherMultiplicity.getGammaId(),
         maximumPublisherMultiplicity, this.maximumPublisherMultiplicity.getDisplayableString());
   }

   public AttributePojo<List<String>> getInterfaceLevelsToUse() {
      return interfaceLevelsToUse;
   }

   @JsonProperty
   public void setInterfaceLevelsToUse(AttributePojo<List<String>> interfaceLevelsToUse) {
      this.interfaceLevelsToUse = interfaceLevelsToUse;
   }

   public AttributePojo<List<String>> getAvailableMessageHeaders() {
      return availableMessageHeaders;
   }

   @JsonProperty
   public void setAvailableMessageHeaders(AttributePojo<List<String>> availableMessageHeaders) {
      this.availableMessageHeaders = availableMessageHeaders;
   }

   public AttributePojo<List<String>> getAvailableSubmessageHeaders() {
      return availableSubmessageHeaders;
   }

   @JsonProperty
   public void setAvailableSubmessageHeaders(AttributePojo<List<String>> availableSubmessageHeaders) {
      this.availableSubmessageHeaders = availableSubmessageHeaders;
   }

   public AttributePojo<List<String>> getAvailableStructureHeaders() {
      return availableStructureHeaders;
   }

   @JsonProperty
   public void setAvailableStructureHeaders(AttributePojo<List<String>> availableStructureHeaders) {
      this.availableStructureHeaders = availableStructureHeaders;
   }

   public AttributePojo<List<String>> getAvailableElementHeaders() {
      return availableElementHeaders;
   }

   @JsonProperty
   public void setAvailableElementHeaders(AttributePojo<List<String>> availableElementHeaders) {
      this.availableElementHeaders = availableElementHeaders;
   }

   public boolean isDirectConnection() {
      return getMinimumPublisherMultiplicity().getValue() == 1 && getMaximumPublisherMultiplicity().getValue() == 1 && getMinimumSubscriberMultiplicity().getValue() == 1 && getMaximumSubscriberMultiplicity().getValue() == 1;
   }

   @JsonIgnore
   private List<String> getJSONContents(String jsonToParse) {
      String innerString = jsonToParse.substring(jsonToParse.indexOf("[") + 1, jsonToParse.indexOf("]"));
      List<String> innerStrings = Arrays.asList(innerString.split(","));
      return innerStrings.stream().filter(s -> !s.isEmpty()).map(s -> s.replaceAll("'", "")).collect(
         Collectors.toList());

   }

   /**
    * @return the isDashed
    */
   public AttributePojo<Boolean> getDashedPresentation() {
      return isDashed;
   }

   /**
    * @param isDashed the isDashed to set
    */
   @JsonProperty
   public void setDashedPresentation(AttributePojo<Boolean> isDashed) {
      this.isDashed = isDashed;
   }

   public AttributePojo<Boolean> getSpareAutoNumbering() {
      return spareAutoNumbering;
   }

   @JsonProperty
   public void setSpareAutoNumbering(AttributePojo<Boolean> spareAutoNumbering) {
      this.spareAutoNumbering = spareAutoNumbering;
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

   public CreateArtifact createArtifact(String key, ApplicabilityId applicId) {
      // @formatter:off
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.ByteAlignValidation, Boolean.toString(this.getByteAlignValidation().getValue()));
      values.put(CoreAttributeTypes.ByteAlignValidationSize, Integer.toString(this.getByteAlignValidationSize().getValue()));
      values.put(CoreAttributeTypes.MessageGeneration, Boolean.toString(this.getMessageGeneration().getValue()));
      values.put(CoreAttributeTypes.MessageGenerationType, this.getMessageGenerationType().getValue());
      values.put(CoreAttributeTypes.MinimumSubscriberMultiplicity, Integer.toString(this.getMinimumPublisherMultiplicity().getValue()));
      values.put(CoreAttributeTypes.MaximumSubscriberMultiplicity, Integer.toString(this.getMaximumSubscriberMultiplicity().getValue()));
      values.put(CoreAttributeTypes.MinimumPublisherMultiplicity, Integer.toString(this.getMinimumPublisherMultiplicity().getValue()));
      values.put(CoreAttributeTypes.MaximumPublisherMultiplicity, Integer.toString(this.getMaximumPublisherMultiplicity().getValue()));
      values.put(CoreAttributeTypes.DashedPresentation, Boolean.toString(this.getDashedPresentation().getValue()));
      values.put(CoreAttributeTypes.SpareAutoNumbering, Boolean.toString(this.getSpareAutoNumbering().getValue()));
      // @formatter:on

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName().getValue());
      art.setTypeId(CoreArtifactTypes.TransportType.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.TransportType.getValidAttributeTypes()) {
         String value = values.get(type);
         if (Strings.isInValid(value)) {
            continue;
         }
         Attribute attr = new Attribute(type.getIdString());
         attr.setValue(Arrays.asList(value));
         attrs.add(attr);
      }

      // Multi-value attributes
      if (this.getInterfaceLevelsToUse().getValue().size() > 0) {
         Attribute attr = new Attribute(CoreAttributeTypes.InterfaceLevelsToUse.getIdString());
         attr.setValue(this.getInterfaceLevelsToUse().getValue());
         attrs.add(attr);
      }
      if (this.getAvailableMessageHeaders().getValue().size() > 0) {
         Attribute attr = new Attribute(CoreAttributeTypes.AvailableMessageHeaders.getIdString());
         attr.setValue(this.getAvailableMessageHeaders().getValue());
         attrs.add(attr);
      }
      if (this.getAvailableSubmessageHeaders().getValue().size() > 0) {
         Attribute attr = new Attribute(CoreAttributeTypes.AvailableSubmessageHeaders.getIdString());
         attr.setValue(this.getAvailableSubmessageHeaders().getValue());
         attrs.add(attr);
      }
      if (this.getAvailableStructureHeaders().getValue().size() > 0) {
         Attribute attr = new Attribute(CoreAttributeTypes.AvailableStructureHeaders.getIdString());
         attr.setValue(this.getAvailableStructureHeaders().getValue());
         attrs.add(attr);
      }
      if (this.getAvailableElementHeaders().getValue().size() > 0) {
         Attribute attr = new Attribute(CoreAttributeTypes.AvailableElementHeaders.getIdString());
         attr.setValue(this.getAvailableElementHeaders().getValue());
         attrs.add(attr);
      }

      art.setAttributes(attrs);
      art.setApplicabilityId(applicId.getIdString());
      art.setkey(key);
      return art;
   }

}
