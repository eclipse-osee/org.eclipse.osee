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
import java.util.Arrays;
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
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.rest.model.transaction.Attribute;
import org.eclipse.osee.orcs.rest.model.transaction.CreateArtifact;

public class TransportType extends ArtifactAccessorResult {

   public static final TransportType SENTINEL = new TransportType();

   private boolean byteAlignValidation;
   private boolean messageGeneration;
   private int byteAlignValidationSize;
   private String messageGenerationType;
   private String messageGenerationPosition;
   private int minimumSubscriberMultiplicity = 0;
   private int maximumSubscriberMultiplicity = 0;
   private int minimumPublisherMultiplicity = 0;
   private int maximumPublisherMultiplicity = 0;
   private boolean isDashed = false;
   private boolean spareAutoNumbering = false;
   private List<String> interfaceLevelsToUse = new LinkedList<String>();
   private List<String> availableMessageHeaders = new LinkedList<String>();
   private List<String> availableSubmessageHeaders = new LinkedList<String>();
   private List<String> availableStructureHeaders = new LinkedList<String>();
   private List<String> availableElementHeaders = new LinkedList<String>();
   private ApplicabilityToken applicability;

   public TransportType(ArtifactToken art) {
      super(art);
   }

   public TransportType(ArtifactReadable art) {
      super(art);
      if (art.isValid()) {
         this.setByteAlignValidation(art.getSoleAttributeValue(CoreAttributeTypes.ByteAlignValidation, false));
         this.setMessageGeneration(art.getSoleAttributeValue(CoreAttributeTypes.MessageGeneration, false));
         this.setByteAlignValidationSize(this.isByteAlignValidation() ? art.getSoleAttributeValue(
            CoreAttributeTypes.ByteAlignValidationSize, 0) : 0);
         this.setMessageGenerationType(
            this.isMessageGeneration() ? art.getSoleAttributeValue(CoreAttributeTypes.MessageGenerationType, "") : "");
         this.setMessageGenerationPosition(this.isMessageGeneration() ? art.getSoleAttributeValue(
            CoreAttributeTypes.MessageGenerationPosition, "") : "");
         /**
          * Note the following 4 properties do not have to be a number, if it is "n", that means it should be 0
          */
         String minSubMultiplicity = art.getSoleAttributeAsString(CoreAttributeTypes.MinimumSubscriberMultiplicity, "");
         String maxSubMultiplicity = art.getSoleAttributeValue(CoreAttributeTypes.MaximumSubscriberMultiplicity, "");
         String minPubMultiplicity = art.getSoleAttributeValue(CoreAttributeTypes.MinimumPublisherMultiplicity, "");
         String maxPubMultiplicity = art.getSoleAttributeValue(CoreAttributeTypes.MaximumPublisherMultiplicity, "");
         if (Strings.isNumeric(minSubMultiplicity)) {
            this.setMinimumSubscriberMultiplicity(Integer.valueOf(minSubMultiplicity));
         } else {
            this.setMinimumSubscriberMultiplicity(0);
         }
         if (Strings.isNumeric(maxSubMultiplicity)) {
            this.setMaximumSubscriberMultiplicity(Integer.valueOf(maxSubMultiplicity));
         } else {
            this.setMaximumSubscriberMultiplicity(0);
         }
         if (Strings.isNumeric(minPubMultiplicity)) {
            this.setMinimumPublisherMultiplicity(Integer.valueOf(minPubMultiplicity));
         } else {
            this.setMinimumPublisherMultiplicity(0);
         }
         if (Strings.isNumeric(maxPubMultiplicity)) {
            this.setMaximumPublisherMultiplicity(Integer.valueOf(maxPubMultiplicity));
         } else {
            this.setMaximumPublisherMultiplicity(0);
         }
         /**
          * the following attributes are json arrays
          */
         this.setInterfaceLevelsToUse(
            this.getJSONContents(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceLevelsToUse, "[]")));
         this.setAvailableMessageHeaders(
            this.getJSONContents(art.getSoleAttributeValue(CoreAttributeTypes.AvailableMessageHeaders, "[]")));
         this.setAvailableSubmessageHeaders(
            this.getJSONContents(art.getSoleAttributeValue(CoreAttributeTypes.AvailableSubmessageHeaders, "[]")));
         this.setAvailableStructureHeaders(
            this.getJSONContents(art.getSoleAttributeValue(CoreAttributeTypes.AvailableStructureHeaders, "[]")));
         this.setAvailableElementHeaders(
            this.getJSONContents(art.getSoleAttributeValue(CoreAttributeTypes.AvailableElementHeaders, "[]")));
         this.setDashedPresentation(art.getSoleAttributeValue(CoreAttributeTypes.DashedPresentation, false));
         this.setSpareAutoNumbering(art.getSoleAttributeValue(CoreAttributeTypes.SpareAutoNumbering, false));
         this.setApplicability(!art.getApplicabilityToken().getId().equals(
            -1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
      } else {
         this.setByteAlignValidation(false);
         this.setMessageGeneration(false);
         this.setByteAlignValidationSize(0);
         this.setMessageGenerationType("");
         this.setMessageGenerationPosition("");
         this.setMinimumPublisherMultiplicity(0);
         this.setMinimumSubscriberMultiplicity(0);
         this.setMaximumPublisherMultiplicity(0);
         this.setMaximumSubscriberMultiplicity(0);
         this.setDashedPresentation(false);
         this.setSpareAutoNumbering(false);
         this.setApplicability(ApplicabilityToken.SENTINEL);
      }
   }

   public TransportType(Long id, String name) {
      super(id, name);
   }

   public TransportType() {
   }

   /**
    * @return the messageGenerationType
    */
   public String getMessageGenerationType() {
      return messageGenerationType;
   }

   /**
    * @param messageGenerationType the messageGenerationType to set
    */
   public void setMessageGenerationType(String messageGenerationType) {
      this.messageGenerationType = messageGenerationType;
   }

   /**
    * @return the messageGenerationPosition
    */
   public String getMessageGenerationPosition() {
      return messageGenerationPosition;
   }

   /**
    * @param messageGenerationPosition the messageGenerationPosition to set
    */
   public void setMessageGenerationPosition(String messageGenerationPosition) {
      this.messageGenerationPosition = messageGenerationPosition;
   }

   /**
    * @return the byteAlignValidationSize
    */
   public int getByteAlignValidationSize() {
      return byteAlignValidationSize;
   }

   /**
    * @param byteAlignValidationSize the byteAlignValidationSize to set
    */
   public void setByteAlignValidationSize(int byteAlignValidationSize) {
      this.byteAlignValidationSize = byteAlignValidationSize;
   }

   /**
    * @return the messageGeneration
    */
   public boolean isMessageGeneration() {
      return messageGeneration;
   }

   /**
    * @param messageGeneration the messageGeneration to set
    */
   public void setMessageGeneration(boolean messageGeneration) {
      this.messageGeneration = messageGeneration;
   }

   /**
    * @return the byteAlignValidation
    */
   public boolean isByteAlignValidation() {
      return byteAlignValidation;
   }

   /**
    * @param byteAlignValidation the byteAlignValidation to set
    */
   public void setByteAlignValidation(boolean byteAlignValidation) {
      this.byteAlignValidation = byteAlignValidation;
   }

   public int getMinimumSubscriberMultiplicity() {
      return minimumSubscriberMultiplicity;
   }

   public void setMinimumSubscriberMultiplicity(int minimumSubscriberMultiplicity) {
      this.minimumSubscriberMultiplicity = minimumSubscriberMultiplicity;
   }

   public int getMaximumSubscriberMultiplicity() {
      return maximumSubscriberMultiplicity;
   }

   public void setMaximumSubscriberMultiplicity(int maximumSubscriberMultiplicity) {
      this.maximumSubscriberMultiplicity = maximumSubscriberMultiplicity;
   }

   public int getMinimumPublisherMultiplicity() {
      return minimumPublisherMultiplicity;
   }

   public void setMinimumPublisherMultiplicity(int minimumPublisherMultiplicity) {
      this.minimumPublisherMultiplicity = minimumPublisherMultiplicity;
   }

   public int getMaximumPublisherMultiplicity() {
      return maximumPublisherMultiplicity;
   }

   public void setMaximumPublisherMultiplicity(int maximumPublisherMultiplicity) {
      this.maximumPublisherMultiplicity = maximumPublisherMultiplicity;
   }

   public List<String> getInterfaceLevelsToUse() {
      return interfaceLevelsToUse;
   }

   public void setInterfaceLevelsToUse(List<String> interfaceLevelsToUse) {
      this.interfaceLevelsToUse = interfaceLevelsToUse;
   }

   public List<String> getAvailableMessageHeaders() {
      return availableMessageHeaders;
   }

   public void setAvailableMessageHeaders(List<String> availableMessageHeaders) {
      this.availableMessageHeaders = availableMessageHeaders;
   }

   public List<String> getAvailableSubmessageHeaders() {
      return availableSubmessageHeaders;
   }

   public void setAvailableSubmessageHeaders(List<String> availableSubmessageHeaders) {
      this.availableSubmessageHeaders = availableSubmessageHeaders;
   }

   public List<String> getAvailableStructureHeaders() {
      return availableStructureHeaders;
   }

   public void setAvailableStructureHeaders(List<String> availableStructureHeaders) {
      this.availableStructureHeaders = availableStructureHeaders;
   }

   public List<String> getAvailableElementHeaders() {
      return availableElementHeaders;
   }

   public void setAvailableElementHeaders(List<String> availableElementHeaders) {
      this.availableElementHeaders = availableElementHeaders;
   }

   public boolean isDirectConnection() {
      return getMinimumPublisherMultiplicity() == 1 && getMaximumPublisherMultiplicity() == 1 && getMinimumSubscriberMultiplicity() == 1 && getMaximumSubscriberMultiplicity() == 1;
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
   public boolean getDashedPresentation() {
      return isDashed;
   }

   /**
    * @param isDashed the isDashed to set
    */
   public void setDashedPresentation(boolean isDashed) {
      this.isDashed = isDashed;
   }

   public boolean isSpareAutoNumbering() {
      return spareAutoNumbering;
   }

   public void setSpareAutoNumbering(boolean spareAutoNumbering) {
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
      values.put(CoreAttributeTypes.ByteAlignValidation, Boolean.toString(this.isByteAlignValidation()));
      values.put(CoreAttributeTypes.ByteAlignValidationSize, Integer.toString(this.getByteAlignValidationSize()));
      values.put(CoreAttributeTypes.MessageGeneration, Boolean.toString(this.isMessageGeneration()));
      values.put(CoreAttributeTypes.MessageGenerationType, this.getMessageGenerationType());
      values.put(CoreAttributeTypes.MinimumSubscriberMultiplicity, Integer.toString(this.getMinimumPublisherMultiplicity()));
      values.put(CoreAttributeTypes.MaximumSubscriberMultiplicity, Integer.toString(this.getMaximumSubscriberMultiplicity()));
      values.put(CoreAttributeTypes.MinimumPublisherMultiplicity, Integer.toString(this.getMinimumPublisherMultiplicity()));
      values.put(CoreAttributeTypes.MaximumPublisherMultiplicity, Integer.toString(this.getMaximumPublisherMultiplicity()));
      values.put(CoreAttributeTypes.DashedPresentation, Boolean.toString(this.getDashedPresentation()));
      values.put(CoreAttributeTypes.SpareAutoNumbering, Boolean.toString(this.isSpareAutoNumbering()));
      // @formatter:on

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName());
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
      if (this.getInterfaceLevelsToUse().size() > 0) {
         Attribute attr = new Attribute(CoreAttributeTypes.InterfaceLevelsToUse.getIdString());
         attr.setValue(this.getInterfaceLevelsToUse());
         attrs.add(attr);
      }
      if (this.getAvailableMessageHeaders().size() > 0) {
         Attribute attr = new Attribute(CoreAttributeTypes.AvailableMessageHeaders.getIdString());
         attr.setValue(this.getAvailableMessageHeaders());
         attrs.add(attr);
      }
      if (this.getAvailableSubmessageHeaders().size() > 0) {
         Attribute attr = new Attribute(CoreAttributeTypes.AvailableSubmessageHeaders.getIdString());
         attr.setValue(this.getAvailableSubmessageHeaders());
         attrs.add(attr);
      }
      if (this.getAvailableStructureHeaders().size() > 0) {
         Attribute attr = new Attribute(CoreAttributeTypes.AvailableStructureHeaders.getIdString());
         attr.setValue(this.getAvailableStructureHeaders());
         attrs.add(attr);
      }
      if (this.getAvailableElementHeaders().size() > 0) {
         Attribute attr = new Attribute(CoreAttributeTypes.AvailableElementHeaders.getIdString());
         attr.setValue(this.getAvailableElementHeaders());
         attrs.add(attr);
      }

      art.setAttributes(attrs);
      art.setApplicabilityId(applicId.getIdString());
      art.setkey(key);
      return art;
   }

}
