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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
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

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceNode extends ArtifactAccessorResultWithGammas {

   public static final InterfaceNode SENTINEL = new InterfaceNode();

   private AttributePojo<String> description =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Description, GammaId.SENTINEL, "", "");
   private AttributePojo<String> interfaceNodeNumber =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceNodeNumber, GammaId.SENTINEL, "", "");;
   private AttributePojo<String> interfaceNodeGroupId =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceNodeGroupId, GammaId.SENTINEL, "", "");
   private ApplicabilityToken applicability;
   private AttributePojo<String> interfaceNodeBackgroundColor = AttributePojo.valueOf(Id.SENTINEL,
      CoreAttributeTypes.InterfaceNodeBackgroundColor, GammaId.SENTINEL, generateColor() ? "#81d4fa" : "#c5e1a5", "");
   private AttributePojo<String> interfaceNodeAddress =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceNodeAddress, GammaId.SENTINEL, "", "");
   private AttributePojo<String> nameAbbrev =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.NameAbbrev, GammaId.SENTINEL, "", "");
   private AttributePojo<String> interfaceNodeCodeGenName =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceNodeCodeGenName, GammaId.SENTINEL, "", "");
   private AttributePojo<String> InterfaceNodeType =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceNodeType, GammaId.SENTINEL, "", "");
   private AttributePojo<String> notes =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Notes, GammaId.SENTINEL, "", "");
   private AttributePojo<Boolean> interfaceNodeCodeGen =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceNodeCodeGen, GammaId.SENTINEL, false, "");
   private AttributePojo<Boolean> interfaceNodeBuildCodeGen =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceNodeBuildCodeGen, GammaId.SENTINEL, false, "");
   private AttributePojo<Boolean> interfaceNodeToolUse =
      AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.InterfaceNodeToolUse, GammaId.SENTINEL, false, "");

   public InterfaceNode(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public InterfaceNode(ArtifactReadable art) {
      super(art);
      this.setDescription(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.Description, "")));
      this.setInterfaceNodeBackgroundColor(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceNodeBackgroundColor, "")));
      this.setInterfaceNodeAddress(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceNodeAddress, "")));
      this.setInterfaceNodeNumber(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceNodeNumber, "")));
      this.setInterfaceNodeGroupId(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceNodeGroupId, "")));
      this.setApplicability(
         !art.getApplicabilityToken().getId().equals(-1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
      this.setNameAbbrev(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.NameAbbrev, "")));
      this.setInterfaceNodeCodeGenName(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceNodeCodeGenName, "")));
      this.setInterfaceNodeType(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceNodeType, "")));
      this.setNotes(AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.Notes, "")));
      this.setInterfaceNodeCodeGen(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceNodeCodeGen, false)));
      this.setInterfaceNodeBuildCodeGen(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceNodeBuildCodeGen, false)));
      this.setInterfaceNodeToolUse(
         AttributePojo.valueOf(art.getSoleAttribute(CoreAttributeTypes.InterfaceNodeToolUse, false)));
   }

   public InterfaceNode(Long id, String name) {
      super(id, AttributePojo.valueOf(Id.SENTINEL, CoreAttributeTypes.Name, GammaId.SENTINEL, name, ""));
   }

   public InterfaceNode() {
   }

   /**
    * @return the description
    */
   public AttributePojo<String> getDescription() {
      return description;
   }

   /**
    * @param description the description to set
    */
   @JsonProperty
   public void setDescription(AttributePojo<String> description) {
      this.description = description;
   }

   public void setDescription(String description) {
      this.description = AttributePojo.valueOf(this.description.getId(), this.description.getTypeId(),
         this.description.getGammaId(), description, this.description.getDisplayableString());
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
    * @return the color
    */
   public AttributePojo<String> getInterfaceNodeBackgroundColor() {
      return interfaceNodeBackgroundColor;
   }

   /**
    * @param interfaceNodeBackgroundColor the color to set
    */
   @JsonProperty
   public void setInterfaceNodeBackgroundColor(AttributePojo<String> interfaceNodeBackgroundColor) {
      this.interfaceNodeBackgroundColor = interfaceNodeBackgroundColor;
   }

   public void setInterfaceNodeBackgroundColor(String interfaceNodeBackgroundColor) {
      this.interfaceNodeBackgroundColor = AttributePojo.valueOf(this.interfaceNodeBackgroundColor.getId(),
         this.interfaceNodeBackgroundColor.getTypeId(), this.interfaceNodeBackgroundColor.getGammaId(),
         interfaceNodeBackgroundColor, this.interfaceNodeBackgroundColor.getDisplayableString());
   }

   /**
    * @return the address
    */
   public AttributePojo<String> getInterfaceNodeAddress() {
      return interfaceNodeAddress;
   }

   /**
    * @param interfaceNodeAddress the address to set
    */
   @JsonProperty
   public void setInterfaceNodeAddress(AttributePojo<String> interfaceNodeAddress) {
      this.interfaceNodeAddress = interfaceNodeAddress;
   }

   public void setInterfaceNodeAddress(String interfaceNodeAddress) {
      this.interfaceNodeAddress = AttributePojo.valueOf(this.interfaceNodeAddress.getId(),
         this.interfaceNodeAddress.getTypeId(), this.interfaceNodeAddress.getGammaId(), interfaceNodeAddress,
         this.interfaceNodeAddress.getDisplayableString());
   }

   public AttributePojo<String> getInterfaceNodeNumber() {
      return interfaceNodeNumber;
   }

   public void setInterfaceNodeNumber(String interfaceNodeNumber) {
      this.interfaceNodeNumber = AttributePojo.valueOf(this.interfaceNodeNumber.getId(),
         this.interfaceNodeNumber.getTypeId(), this.interfaceNodeNumber.getGammaId(), interfaceNodeNumber,
         this.interfaceNodeNumber.getDisplayableString());
   }

   @JsonProperty
   public void setInterfaceNodeNumber(AttributePojo<String> interfaceNodeNumber) {
      this.interfaceNodeNumber = interfaceNodeNumber;
   }

   public AttributePojo<String> getInterfaceNodeGroupId() {
      return interfaceNodeGroupId;
   }

   public void setInterfaceNodeGroupId(String interfaceNodeGroupId) {
      this.interfaceNodeGroupId = AttributePojo.valueOf(this.interfaceNodeGroupId.getId(),
         this.interfaceNodeGroupId.getTypeId(), this.interfaceNodeGroupId.getGammaId(), interfaceNodeGroupId,
         this.interfaceNodeGroupId.getDisplayableString());
   }

   @JsonProperty
   public void setInterfaceNodeGroupId(AttributePojo<String> interfaceNodeGroupId) {
      this.interfaceNodeGroupId = interfaceNodeGroupId;
   }

   public AttributePojo<String> getNameAbbrev() {
      return nameAbbrev;
   }

   public void setNameAbbrev(String nameAbbrev) {
      this.nameAbbrev = AttributePojo.valueOf(this.nameAbbrev.getId(), this.nameAbbrev.getTypeId(),
         this.nameAbbrev.getGammaId(), nameAbbrev, this.nameAbbrev.getDisplayableString());
   }

   @JsonProperty
   public void setNameAbbrev(AttributePojo<String> nameAbbrev) {
      this.nameAbbrev = nameAbbrev;
   }

   public AttributePojo<String> getInterfaceNodeCodeGenName() {
      return interfaceNodeCodeGenName;
   }

   public void setInterfaceNodeCodeGenName(String interfaceNodeCodeGenName) {
      this.interfaceNodeCodeGenName = AttributePojo.valueOf(this.interfaceNodeCodeGenName.getId(),
         this.interfaceNodeCodeGenName.getTypeId(), this.interfaceNodeCodeGenName.getGammaId(),
         interfaceNodeCodeGenName, this.interfaceNodeCodeGenName.getDisplayableString());
   }

   @JsonProperty
   public void setInterfaceNodeCodeGenName(AttributePojo<String> interfaceNodeCodeGenName) {
      this.interfaceNodeCodeGenName = interfaceNodeCodeGenName;
   }

   public AttributePojo<String> getInterfaceNodeType() {
      return InterfaceNodeType;
   }

   public void setInterfaceNodeType(String interfaceNodeType) {
      this.InterfaceNodeType = AttributePojo.valueOf(this.InterfaceNodeType.getId(), this.InterfaceNodeType.getTypeId(),
         this.InterfaceNodeType.getGammaId(), interfaceNodeType, this.InterfaceNodeType.getDisplayableString());
   }

   @JsonProperty
   public void setInterfaceNodeType(AttributePojo<String> interfaceNodeType) {
      InterfaceNodeType = interfaceNodeType;
   }

   public AttributePojo<String> getNotes() {
      return notes;
   }

   public void setNotes(String notes) {
      this.notes = AttributePojo.valueOf(this.notes.getId(), this.notes.getTypeId(), this.notes.getGammaId(), notes,
         this.notes.getDisplayableString());
   }

   @JsonProperty
   public void setNotes(AttributePojo<String> notes) {
      this.notes = notes;
   }

   public AttributePojo<Boolean> getInterfaceNodeCodeGen() {
      return interfaceNodeCodeGen;
   }

   public void setInterfaceNodeCodeGen(Boolean interfaceNodeCodeGen) {
      this.interfaceNodeCodeGen = AttributePojo.valueOf(this.interfaceNodeCodeGen.getId(),
         this.interfaceNodeCodeGen.getTypeId(), this.interfaceNodeCodeGen.getGammaId(), interfaceNodeCodeGen,
         this.interfaceNodeCodeGen.getDisplayableString());
   }

   @JsonProperty
   public void setInterfaceNodeCodeGen(AttributePojo<Boolean> interfaceNodeCodeGen) {
      this.interfaceNodeCodeGen = interfaceNodeCodeGen;
   }

   public AttributePojo<Boolean> getInterfaceNodeBuildCodeGen() {
      return interfaceNodeBuildCodeGen;
   }

   public void setInterfaceNodeBuildCodeGen(Boolean interfaceNodeBuildCodeGen) {
      this.interfaceNodeBuildCodeGen = AttributePojo.valueOf(this.interfaceNodeBuildCodeGen.getId(),
         this.interfaceNodeBuildCodeGen.getTypeId(), this.interfaceNodeBuildCodeGen.getGammaId(),
         interfaceNodeBuildCodeGen, this.interfaceNodeBuildCodeGen.getDisplayableString());
   }

   @JsonProperty
   public void setInterfaceNodeBuildCodeGen(AttributePojo<Boolean> interfaceNodeBuildCodeGen) {
      this.interfaceNodeBuildCodeGen = interfaceNodeBuildCodeGen;
   }

   public AttributePojo<Boolean> getInterfaceNodeToolUse() {
      return interfaceNodeToolUse;
   }

   public void setInterfaceNodeToolUse(Boolean interfaceNodeToolUse) {
      this.interfaceNodeToolUse = AttributePojo.valueOf(this.interfaceNodeToolUse.getId(),
         this.interfaceNodeToolUse.getTypeId(), this.interfaceNodeToolUse.getGammaId(), interfaceNodeToolUse,
         this.interfaceNodeToolUse.getDisplayableString());
   }

   @JsonProperty
   public void setInterfaceNodeToolUse(AttributePojo<Boolean> interfaceNodeToolUse) {
      this.interfaceNodeToolUse = interfaceNodeToolUse;
   }

   public String getColor() {
      return interfaceNodeBackgroundColor.getValue() != "" ? interfaceNodeBackgroundColor.getValue() : generateColor() ? "#81d4fa" : "#c5e1a5";
   }

   @JsonIgnore
   private boolean generateColor() {
      return ThreadLocalRandom.current().nextInt(1, 3) > 1 ? true : false;
   }

   public CreateArtifact createArtifact(String key, ApplicabilityId applicId) {
      // @formatter:off
      Map<AttributeTypeToken, String> values = new HashMap<>();
      values.put(CoreAttributeTypes.InterfaceNodeAddress, this.getInterfaceNodeAddress().getValue());
      values.put(CoreAttributeTypes.InterfaceNodeNumber, this.getInterfaceNodeNumber().getValue());
      values.put(CoreAttributeTypes.InterfaceNodeGroupId, this.getInterfaceNodeGroupId().getValue());
      values.put(CoreAttributeTypes.NameAbbrev, this.getNameAbbrev().getValue());
      values.put(CoreAttributeTypes.InterfaceNodeCodeGenName, this.getInterfaceNodeCodeGenName().getValue());
      values.put(CoreAttributeTypes.InterfaceNodeType, this.getInterfaceNodeType().getValue());
      values.put(CoreAttributeTypes.InterfaceNodeBackgroundColor, this.getInterfaceNodeBackgroundColor().getValue());
      values.put(CoreAttributeTypes.InterfaceNodeCodeGen, Boolean.toString(this.getInterfaceNodeCodeGen().getValue()));
      values.put(CoreAttributeTypes.InterfaceNodeBuildCodeGen, Boolean.toString(this.getInterfaceNodeBuildCodeGen().getValue()));
      values.put(CoreAttributeTypes.InterfaceNodeToolUse, Boolean.toString(this.getInterfaceNodeToolUse().getValue()));
      values.put(CoreAttributeTypes.Notes, this.getNotes().getValue());
      // @formatter:on

      CreateArtifact art = new CreateArtifact();
      art.setName(this.getName().getValue());
      art.setTypeId(CoreArtifactTypes.InterfaceNode.getIdString());

      List<Attribute> attrs = new LinkedList<>();

      for (AttributeTypeToken type : CoreArtifactTypes.InterfaceNode.getValidAttributeTypes()) {
         String value = values.get(type);
         if (Strings.isInValid(value)) {
            continue;
         }
         Attribute attr = new Attribute(type.getIdString());
         attr.setValue(Arrays.asList(value));
         attrs.add(attr);
      }

      art.setAttributes(attrs);
      art.setApplicabilityId(applicId.getIdString());
      art.setkey(key);
      return art;
   }

}
