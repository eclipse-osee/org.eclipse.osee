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
import java.util.concurrent.ThreadLocalRandom;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Luciano T. Vaglienti
 */
public class NodeViewData extends PLGenericDBObject {

   private String Description;
   private ApplicabilityToken applicability;
   private String interfaceNodeNumber;
   private String interfaceNodeGroupId;
   private String interfaceNodeBgColor = generateColor() ? "#81d4fa" : "#c5e1a5"; //has to be called bgColor due to @swimlane/ngx-graph having weird handling behavior of node.data.color
   private String interfaceNodeAddress = "";
   private String interfaceNodeNameAbbrev = "";
   private String interfaceNodeCodeGenName = "";
   private String InterfaceNodeType = "";
   private String notes = "";
   private boolean interfaceNodeCodeGen = false;
   private boolean interfaceNodeBuildCodeGen = false;
   private boolean interfaceNodeToolUse = false;

   public NodeViewData(ArtifactToken art) {
      this((ArtifactReadable) art);
   }

   public NodeViewData(ArtifactReadable art) {
      super(art);
      this.setApplicability(
         !art.getApplicabilityToken().getId().equals(-1L) ? art.getApplicabilityToken() : ApplicabilityToken.SENTINEL);
   }

   public NodeViewData(Long id, String name, String nodeNumber, String nodeGroupId) {
      super(id, name);
      this.setInterfaceNodeNumber(nodeNumber);
      this.setInterfaceNodeGroupId(nodeGroupId);
   }

   public NodeViewData(InterfaceNode node) {
      super(node.getId(), node.getName());
      this.setInterfaceNodeNumber(node.getNodeNumber());
      this.setInterfaceNodeGroupId(node.getNodeGroupId());
      this.setInterfaceNodeNameAbbrev(node.getInterfaceNodeNameAbbrev());
      this.setInterfaceNodeCodeGenName(node.getInterfaceNodeCodeGenName());
      this.setInterfaceNodeType(node.getInterfaceNodeType());
      this.setNotes(node.getNotes());
      this.setInterfaceNodeCodeGen(node.isInterfaceNodeCodeGen());
      this.setInterfaceNodeBuildCodeGen(node.isInterfaceNodeBuildCodeGen());
      this.setInterfaceNodeToolUse(node.isInterfaceNodeToolUse());
   }

   public NodeViewData() {
   }

   /**
    * @return the color
    */
   public String getinterfaceNodeBgColor() {
      return interfaceNodeBgColor;
   }

   /**
    * @param color the color to set
    */
   public void setinterfaceNodeBgColor(String color) {
      this.interfaceNodeBgColor = color;
   }

   @JsonIgnore
   private boolean generateColor() {
      return ThreadLocalRandom.current().nextInt(1, 3) > 1 ? true : false;
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
    * @return the interfaceNodeAddress
    */
   public String getInterfaceNodeAddress() {
      return interfaceNodeAddress;
   }

   /**
    * @param interfaceNodeAddress the interfaceNodeAddress to set
    */
   public void setInterfaceNodeAddress(String interfaceNodeAddress) {
      this.interfaceNodeAddress = interfaceNodeAddress;
   }

   public String getInterfaceNodeNumber() {
      return interfaceNodeNumber;
   }

   public void setInterfaceNodeNumber(String nodeNumber) {
      this.interfaceNodeNumber = nodeNumber;
   }

   public String getInterfaceNodeGroupId() {
      return interfaceNodeGroupId;
   }

   public void setInterfaceNodeGroupId(String interfaceNodeGroupId) {
      this.interfaceNodeGroupId = interfaceNodeGroupId;
   }

   public String getInterfaceNodeNameAbbrev() {
      return interfaceNodeNameAbbrev;
   }

   public void setInterfaceNodeNameAbbrev(String interfaceNodeNameAbbrev) {
      this.interfaceNodeNameAbbrev = interfaceNodeNameAbbrev;
   }

   public String getInterfaceNodeCodeGenName() {
      return interfaceNodeCodeGenName;
   }

   public void setInterfaceNodeCodeGenName(String interfaceNodeCodeGenName) {
      this.interfaceNodeCodeGenName = interfaceNodeCodeGenName;
   }

   public String getInterfaceNodeType() {
      return InterfaceNodeType;
   }

   public void setInterfaceNodeType(String interfaceNodeType) {
      InterfaceNodeType = interfaceNodeType;
   }

   public String getNotes() {
      return notes;
   }

   public void setNotes(String notes) {
      this.notes = notes;
   }

   public boolean isInterfaceNodeCodeGen() {
      return interfaceNodeCodeGen;
   }

   public void setInterfaceNodeCodeGen(boolean interfaceNodeCodeGen) {
      this.interfaceNodeCodeGen = interfaceNodeCodeGen;
   }

   public boolean isInterfaceNodeBuildCodeGen() {
      return interfaceNodeBuildCodeGen;
   }

   public void setInterfaceNodeBuildCodeGen(boolean interfaceNodeBuildCodeGen) {
      this.interfaceNodeBuildCodeGen = interfaceNodeBuildCodeGen;
   }

   public boolean isInterfaceNodeToolUse() {
      return interfaceNodeToolUse;
   }

   public void setInterfaceNodeToolUse(boolean interfaceNodeToolUse) {
      this.interfaceNodeToolUse = interfaceNodeToolUse;
   }

}
