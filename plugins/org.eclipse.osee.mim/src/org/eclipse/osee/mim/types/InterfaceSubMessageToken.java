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

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.mim.annotations.OseeArtifactAttribute;
import org.eclipse.osee.mim.annotations.OseeArtifactRequiredAttribute;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceSubMessageToken extends PLGenericDBObject {
   public static final InterfaceSubMessageToken SENTINEL = new InterfaceSubMessageToken();

   @OseeArtifactRequiredAttribute()
   @OseeArtifactAttribute(attributeId = 1152921504606847088L)
   private String Name;

   @OseeArtifactAttribute(attributeId = 2455059983007225763L)
   private String InterfaceMessageRate;

   @OseeArtifactRequiredAttribute()
   @OseeArtifactAttribute(attributeId = 2455059983007225769L)
   private String InterfaceSubMessageNumber; //required

   @OseeArtifactAttribute(attributeId = 1152921504606847090L)
   private String Description;

   public InterfaceSubMessageToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InterfaceSubMessageToken(ArtifactReadable art) {
      this();
      this.setId(art.getId());
      this.setName(art.getName());
      this.setDescription(art.getSoleAttributeValue(CoreAttributeTypes.Description, ""));
      this.setInterfaceSubMessageNumber(art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceSubMessageNumber, ""));
      this.setInterfaceMessageRate(art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceMessageRate, ""));
   }

   public InterfaceSubMessageToken(Long id, String name) {
      super(id, name);
   }

   public InterfaceSubMessageToken() {
      super();
   }

   /**
    * @return the interfaceSubMessageNumber
    */
   public String getInterfaceSubMessageNumber() {
      return InterfaceSubMessageNumber;
   }

   /**
    * @param interfaceSubMessageNumber the interfaceSubMessageNumber to set
    */
   public void setInterfaceSubMessageNumber(String interfaceSubMessageNumber) {
      InterfaceSubMessageNumber = interfaceSubMessageNumber;
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
