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
public class InterfaceStructureToken extends PLGenericDBObject {
   public static final InterfaceStructureToken SENTINEL = new InterfaceStructureToken();

   @OseeArtifactRequiredAttribute()
   @OseeArtifactAttribute(attributeId = 1152921504606847088L)
   private String Name;

   @OseeArtifactAttribute(attributeId = 2455059983007225764L)
   private String InterfaceStructureCategory;

   @OseeArtifactAttribute(attributeId = 2455059983007225755L)
   private String InterfaceMinSimultaneity;

   @OseeArtifactAttribute(attributeId = 2455059983007225756L)
   private String InterfaceMaxSimultaneity;

   @OseeArtifactAttribute(attributeId = 2455059983007225760L)
   private Integer InterfaceTaskFileType;

   @OseeArtifactAttribute(attributeId = 1152921504606847090L)
   private String Description;

   private Collection<InterfaceStructureElementToken> elements = new LinkedList<>();

   public InterfaceStructureToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InterfaceStructureToken(ArtifactReadable art) {
      this();
      this.setId(art.getId());
      this.setName(art.getName());
      this.setDescription(art.getSoleAttributeValue(CoreAttributeTypes.Description, ""));
      this.setInterfaceMaxSimultaneity(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceMaxSimultaneity, ""));
      this.setInterfaceMinSimultaneity(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceMinSimultaneity, ""));
      this.setInterfaceStructureCategory(
         art.getSoleAttributeAsString(CoreAttributeTypes.InterfaceStructureCategory, ""));
      this.setInterfaceTaskFileType(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceTaskFileType, 0));
   }

   /**
    * @param id
    * @param name
    */
   public InterfaceStructureToken(Long id, String name) {
      super(id, name);
   }

   /**
    *
    */
   public InterfaceStructureToken() {
      super();

   }

   /**
    * @return the interfaceStructureCategory
    */
   public String getInterfaceStructureCategory() {
      return InterfaceStructureCategory;
   }

   /**
    * @param interfaceStructureCategory the interfaceStructureCategory to set
    */
   public void setInterfaceStructureCategory(String interfaceStructureCategory) {
      InterfaceStructureCategory = interfaceStructureCategory;
   }

   /**
    * @return the interfaceMinSimultaneity
    */
   public String getInterfaceMinSimultaneity() {
      return InterfaceMinSimultaneity;
   }

   /**
    * @param interfaceMinSimultaneity the interfaceMinSimultaneity to set
    */
   public void setInterfaceMinSimultaneity(String interfaceMinSimultaneity) {
      InterfaceMinSimultaneity = interfaceMinSimultaneity;
   }

   /**
    * @return the interfaceMaxSimultaneity
    */
   public String getInterfaceMaxSimultaneity() {
      return InterfaceMaxSimultaneity;
   }

   /**
    * @param interfaceMaxSimultaneity the interfaceMaxSimultaneity to set
    */
   public void setInterfaceMaxSimultaneity(String interfaceMaxSimultaneity) {
      InterfaceMaxSimultaneity = interfaceMaxSimultaneity;
   }

   /**
    * @return the interfaceTaskFileType
    */
   public Integer getInterfaceTaskFileType() {
      return InterfaceTaskFileType;
   }

   /**
    * @param interfaceTaskFileType the interfaceTaskFileType to set
    */
   public void setInterfaceTaskFileType(Integer interfaceTaskFileType) {
      InterfaceTaskFileType = interfaceTaskFileType;
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
      Description = description;
   }

   /**
    * @return the elements
    */
   public List<InterfaceStructureElementToken> getElements() {
      return (List<InterfaceStructureElementToken>) elements;
   }

   /**
    * @param elements the elements to set
    */
   public void setElements(Collection<InterfaceStructureElementToken> elements) {
      this.elements = elements;
   }

}
