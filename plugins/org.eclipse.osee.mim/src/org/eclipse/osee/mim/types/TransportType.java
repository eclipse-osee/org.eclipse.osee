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

import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

public class TransportType extends PLGenericDBObject {

   public static final TransportType SENTINEL = new TransportType();

   private String Name;
   private boolean byteAlignValidation;
   private boolean messageGeneration;
   private int byteAlignValidationSize;
   private String messageGenerationType;
   private String messageGenerationPosition;

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
      } else {
         this.setByteAlignValidation(false);
         this.setMessageGeneration(false);
         this.setByteAlignValidationSize(0);
         this.setMessageGenerationType("");
         this.setMessageGenerationPosition("");
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

}
