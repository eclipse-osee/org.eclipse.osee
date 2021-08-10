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

import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceStructureElementToken extends PLGenericDBObject {
   public static final InterfaceStructureElementToken SENTINEL = new InterfaceStructureElementToken();

   private String Name;

   private Boolean InterfaceElementAlterable;

   private String Notes;

   private String Description;

   private Integer InterfaceElementIndexStart;

   private Integer InterfaceElementIndexEnd;

   private Long PlatformTypeId;
   private String PlatformTypeName;

   private Double beginByte = 0.0;
   private Double endByte = 0.0;
   private Double beginWord = 0.0;
   private Double endWord = 0.0;

   private ApplicabilityToken applicability;

   /**
    * @param art
    */
   public InterfaceStructureElementToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   /**
    * @param art
    */
   public InterfaceStructureElementToken(ArtifactReadable art) {
      this();
      this.setId(art.getId());
      this.setName(art.getSoleAttributeValue(CoreAttributeTypes.Name));
      this.setInterfaceElementAlterable(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementAlterable, false));
      this.setInterfaceElementIndexStart(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexStart, 0));
      this.setInterfaceElementIndexEnd(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexEnd, 0));
      this.setNotes(art.getSoleAttributeValue(CoreAttributeTypes.Notes, ""));
      this.setDescription(art.getSoleAttributeValue(CoreAttributeTypes.Description, ""));

   }

   /**
    * @param id
    * @param name
    */
   public InterfaceStructureElementToken(Long id, String name) {
      super(id, name);
   }

   /**
    *
    */
   public InterfaceStructureElementToken() {
      super();
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
    * @return the notes
    */
   public String getNotes() {
      return Notes;
   }

   /**
    * @param notes the notes to set
    */
   public void setNotes(String notes) {
      Notes = notes;
   }

   /**
    * @return the interfaceElementAlterable
    */
   public Boolean getInterfaceElementAlterable() {
      return InterfaceElementAlterable;
   }

   /**
    * @param interfaceElementAlterable the interfaceElementAlterable to set
    */
   public void setInterfaceElementAlterable(Boolean interfaceElementAlterable) {
      InterfaceElementAlterable = interfaceElementAlterable;
   }

   /**
    * @return the interfaceElementIndexStart
    */
   public Integer getInterfaceElementIndexStart() {
      return InterfaceElementIndexStart;
   }

   /**
    * @param interfaceElementIndexStart the interfaceElementIndexStart to set
    */
   public void setInterfaceElementIndexStart(Integer interfaceElementIndexStart) {
      InterfaceElementIndexStart = interfaceElementIndexStart;
   }

   /**
    * @return the interfaceElementIndexEnd
    */
   public Integer getInterfaceElementIndexEnd() {
      return InterfaceElementIndexEnd;
   }

   /**
    * @param interfaceElementIndexEnd the interfaceElementIndexEnd to set
    */
   public void setInterfaceElementIndexEnd(Integer interfaceElementIndexEnd) {
      InterfaceElementIndexEnd = interfaceElementIndexEnd;
   }

   /**
    * @return the platformTypeId
    */
   public Long getPlatformTypeId() {
      return PlatformTypeId;
   }

   /**
    * @param platformTypeId the platformTypeId to set
    */
   public void setPlatformTypeId(Long platformTypeId) {
      PlatformTypeId = platformTypeId;
   }

   /**
    * @return the platformTypeName
    */
   public String getPlatformTypeName2() {
      return PlatformTypeName;
   }

   /**
    * @param platformTypeName the platformTypeName to set
    */
   public void setPlatformTypeName(String platformTypeName) {
      PlatformTypeName = platformTypeName;
   }

   /**
    * @return the beginByte
    */
   public Double getBeginByte() {
      return beginByte;
   }

   /**
    * @param beginByte the beginByte to set
    */
   public void setBeginByte(Double beginByte) {
      this.beginByte = beginByte;
   }

   /**
    * @return the endByte
    */
   public Double getEndByte() {
      return endByte;
   }

   /**
    * @param endByte the endByte to set
    */
   public void setEndByte(Double endByte) {
      this.endByte = endByte;
   }

   /**
    * @return the beginWord
    */
   public Double getBeginWord() {
      return beginWord;
   }

   /**
    * @param beginWord the beginWord to set
    */
   public void setBeginWord(Double beginWord) {
      this.beginWord = beginWord;
   }

   /**
    * @return the endWord
    */
   public Double getEndWord() {
      return endWord;
   }

   /**
    * @param endWord the endWord to set
    */
   public void setEndWord(Double endWord) {
      this.endWord = endWord;
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
