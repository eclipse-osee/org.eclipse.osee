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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.mim.annotations.OseeArtifactAttribute;
import org.eclipse.osee.mim.annotations.OseeArtifactRequiredAttribute;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceMessageToken extends PLGenericDBObject {
   public static final InterfaceMessageToken SENTINEL = new InterfaceMessageToken();

   @OseeArtifactRequiredAttribute()
   @OseeArtifactAttribute(attributeId = 1152921504606847088L)
   private String Name; //required

   @OseeArtifactRequiredAttribute()
   @OseeArtifactAttribute(attributeId = 2455059983007225768L)
   private String InterfaceMessageNumber; //required

   @OseeArtifactRequiredAttribute()
   @OseeArtifactAttribute(attributeId = 3899709087455064789L)
   private String InterfaceMessagePeriodicity; //required

   @OseeArtifactAttribute(attributeId = 2455059983007225763L)
   private String InterfaceMessageRate;

   @OseeArtifactRequiredAttribute()
   @OseeArtifactAttribute(attributeId = 2455059983007225754L)
   private Boolean InterfaceMessageWriteAccess; //required

   @OseeArtifactRequiredAttribute()
   @OseeArtifactAttribute(attributeId = 2455059983007225770L)
   private String InterfaceMessageType; //required

   @OseeArtifactAttribute(attributeId = 1152921504606847090L)
   private String Description;
   private List<InterfaceSubMessageToken> subMessages = new LinkedList<InterfaceSubMessageToken>();

   public InterfaceMessageToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InterfaceMessageToken(ArtifactReadable art) {
      this();
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
   }

   public InterfaceMessageToken(Long id, String name) {
      super(id, name);
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
}
