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
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceStructureElementArrayToken extends PLGenericDBObject {

   public static final InterfaceStructureElementArrayToken SENTINEL = new InterfaceStructureElementArrayToken();

   private String name;

   private String Notes;

   private String Description;

   private Integer InterfaceElementIndexStart;

   private Integer InterfaceElementIndexEnd;

   private String Units;

   private Double beginByte = 0.0;
   private Double endByte = 0.0;
   private Double beginWord = 0.0;
   private Double endWord = 0.0;

   private ApplicabilityToken applicability;

   private String logicalType;
   private String InterfacePlatformTypeMinval;
   private String InterfacePlatformTypeMaxval;
   private String InterfaceDefaultValue;

   public InterfaceStructureElementArrayToken(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InterfaceStructureElementArrayToken(ArtifactReadable art) {
      super(art);
      this.setInterfaceElementIndexStart(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexStart));
      this.setInterfaceElementIndexEnd(art.getSoleAttributeValue(CoreAttributeTypes.InterfaceElementIndexEnd));
      this.setApplicability(
         !art.getApplicabilityToken().getId().equals(-1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
   }

   /**
    * Special constructor for handling new arrays being created using element endpoint(s)
    *
    * @param token
    */
   public InterfaceStructureElementArrayToken(InterfaceStructureElementToken token) {
      super(token.getId(), token.getName());
      this.setId(token.getId());
      this.setName(token.getName());
      this.setDescription(token.getDescription());
      this.setInterfaceElementIndexEnd(token.getInterfaceElementIndexEnd());
      this.setInterfaceElementIndexStart(token.getInterfaceElementIndexStart());
      this.setNotes(token.getNotes());

   }

   public InterfaceStructureElementArrayToken(String name, Long id, String description, Integer indexEnd, Integer indexStart, String notes, Double beginByte, Double beginWord, Double endByte, Double endWord) {
      super(id, name);
      this.setId(id);
      this.setName(name);
      this.setDescription(description);
      this.setInterfaceElementIndexEnd(indexEnd);
      this.setInterfaceElementIndexStart(indexStart);
      this.setNotes(notes);
      this.setBeginByte(beginByte);
      this.setBeginWord(beginWord);
      this.setEndByte(endByte);
      this.setEndWord(endWord);

   }

   public InterfaceStructureElementArrayToken(Long id, String name) {
      super(id, name);
   }

   public InterfaceStructureElementArrayToken() {
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

   /**
    * @return the logicalType
    */
   public String getLogicalType() {
      return logicalType;
   }

   /**
    * @param logicalType the logicalType to set
    */
   public void setLogicalType(String logicalType) {
      this.logicalType = logicalType;
   }

   /**
    * @return the interfacePlatformTypeMinval
    */
   public String getInterfacePlatformTypeMinval() {
      return InterfacePlatformTypeMinval;
   }

   /**
    * @param interfacePlatformTypeMinval the interfacePlatformTypeMinval to set
    */
   public void setInterfacePlatformTypeMinval(String interfacePlatformTypeMinval) {
      InterfacePlatformTypeMinval = interfacePlatformTypeMinval;
   }

   /**
    * @return the interfacePlatformTypeMaxval
    */
   public String getInterfacePlatformTypeMaxval() {
      return InterfacePlatformTypeMaxval;
   }

   /**
    * @param interfacePlatformTypeMaxval the interfacePlatformTypeMaxval to set
    */
   public void setInterfacePlatformTypeMaxval(String interfacePlatformTypeMaxval) {
      InterfacePlatformTypeMaxval = interfacePlatformTypeMaxval;
   }

   /**
    * @return the InterfaceDefaultValue
    */
   public String getInterfaceDefaultValue() {
      return InterfaceDefaultValue;
   }

   /**
    * @param InterfaceDefaultValue the InterfaceDefaultValue to set
    */
   public void setInterfaceDefaultValue(String InterfaceDefaultValue) {
      InterfaceDefaultValue = InterfaceDefaultValue;
   }

   /**
    * @return the units
    */
   public String getUnits() {
      return Units;
   }

   /**
    * @param units the units to set
    */
   public void setUnits(String units) {
      Units = units;
   }

}
