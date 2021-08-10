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
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceStructureToken extends PLGenericDBObject {
   public static final InterfaceStructureToken SENTINEL = new InterfaceStructureToken();

   private String Name;

   private String InterfaceStructureCategory;

   private String InterfaceMinSimultaneity;

   private String InterfaceMaxSimultaneity;

   private Integer InterfaceTaskFileType;

   private String Description;

   private Integer numElements = 0;
   private Double sizeInBytes = 0.0;
   private Double BytesPerSecondMinimum = 0.0;
   private Double BytesPerSecondMaximum = 0.0;

   private Collection<InterfaceStructureElementToken> elements = new LinkedList<>();

   private ApplicabilityToken applicability;

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
      this.setNumElements(art.getRelatedCount(CoreRelationTypes.InterfaceStructureContent_DataElement));
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

   /**
    * @return the numElements
    */
   public Integer getNumElements() {
      return numElements;
   }

   /**
    * @param numElements the numElements to set
    */
   public void setNumElements(Integer numElements) {
      this.numElements = numElements;
   }

   /**
    * @return the sizeInBytes
    */
   public Double getSizeInBytes() {
      return sizeInBytes;
   }

   /**
    * @return the sizeInBytes
    */
   public void setSizeInBytes(Double sizeInBytes) {
      this.sizeInBytes = sizeInBytes;
      this.setBytesPerSecondMaximum(this.sizeInBytes * Double.parseDouble(this.getInterfaceMaxSimultaneity()));
      this.setBytesPerSecondMinimum(this.sizeInBytes * Double.parseDouble(this.getInterfaceMinSimultaneity()));
   }

   /**
    * @return the bytesPerSecondMaximum
    */
   public Double getBytesPerSecondMaximum() {
      return BytesPerSecondMaximum;
   }

   /**
    * @param bytesPerSecondMaximum the bytesPerSecondMaximum to set
    */
   public void setBytesPerSecondMaximum(Double bytesPerSecondMaximum) {
      BytesPerSecondMaximum = bytesPerSecondMaximum;
   }

   /**
    * @return the bytesPerSecondMinimum
    */
   public Double getBytesPerSecondMinimum() {
      return BytesPerSecondMinimum;
   }

   /**
    * @param bytesPerSecondMinimum the bytesPerSecondMinimum to set
    */
   public void setBytesPerSecondMinimum(Double bytesPerSecondMinimum) {
      BytesPerSecondMinimum = bytesPerSecondMinimum;
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

}
