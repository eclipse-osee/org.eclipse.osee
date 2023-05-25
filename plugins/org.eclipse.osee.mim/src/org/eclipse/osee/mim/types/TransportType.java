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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class TransportType extends PLGenericDBObject {

   public static final TransportType SENTINEL = new TransportType();

   private String Name;
   private boolean byteAlignValidation;
   private boolean messageGeneration;
   private int byteAlignValidationSize;
   private String messageGenerationType;
   private String messageGenerationPosition;
   private int minimumSubscriberMultiplicity = 0;
   private int maximumSubscriberMultiplicity = 0;
   private int minimumPublisherMultiplicity = 0;
   private int maximumPublisherMultiplicity = 0;
   private List<String> interfaceLevelsToUse = new LinkedList<String>();
   private List<String> availableMessageHeaders = new LinkedList<String>();
   private List<String> availableSubmessageHeaders = new LinkedList<String>();
   private List<String> availableStructureHeaders = new LinkedList<String>();
   private List<String> availableElementHeaders = new LinkedList<String>();

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

   @JsonIgnore
   private List<String> getJSONContents(String jsonToParse) {
      String innerString = jsonToParse.substring(jsonToParse.indexOf("[") + 1, jsonToParse.indexOf("]"));
      List<String> innerStrings = Arrays.asList(innerString.split(","));
      return innerStrings.stream().map(s -> s.replaceAll("'", "")).collect(Collectors.toList());

   }

}
