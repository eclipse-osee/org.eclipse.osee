/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.api.data;

/**
 * @author Shawn F. Cook
 */
public class WebArtifact {
   private final String guid;
   private final String artifactName;
   private final String artifactType;
   private String attr_Category;
   private String attr_DevAssurLevel;
   private String attr_ImpoParaNum;
   private String attr_Partition;
   private String attr_QualMethod;
   private String attr_Subsystm;
   private String attr_TechPerfParam;

   public WebArtifact(String guid, String artifactName, String artifactType) {
      this.guid = guid;
      this.artifactName = artifactName;
      this.artifactType = artifactType;
   }

   public String getArtifactName() {
      return artifactName;
   }

   public String getArtifactType() {
      return artifactType;
   }

   public String getGuid() {
      return guid;
   }

   //   public Artifact getParent() {
   //      Collection<Artifact> listOfParents = relations.get(RelationType.PARENT);
   //      if (listOfParents == null || listOfParents.size() <= 0) {
   //         return null;
   //      }
   //      return listOfParents.iterator().next();
   //   }
   //
   //   /*
   //    * Returns list of ancestor Artifacts or empty list if there are no ancestors (i.e.: parent is null).
   //    */
   //   public Collection<Artifact> getAncestry() {
   //      Collection<Artifact> ancestry = new ArrayList<Artifact>();
   //      Artifact parent = this.getParent();
   //      if (parent != null) {
   //         ancestry.addAll(parent.getAncestry());
   //         ancestry.add(parent);
   //      }
   //      return ancestry;
   //   }

   @Override
   public String toString() {
      return artifactName;
   }

   public String getAttr_Category() {
      return attr_Category;
   }

   public void setAttr_Category(String attr_Category) {
      this.attr_Category = attr_Category;
   }

   public String getAttr_DevAssurLevel() {
      return attr_DevAssurLevel;
   }

   public void setAttr_DevAssurLevel(String attr_DevAssurLevel) {
      this.attr_DevAssurLevel = attr_DevAssurLevel;
   }

   public String getAttr_ImpoParaNum() {
      return attr_ImpoParaNum;
   }

   public void setAttr_ImpoParaNum(String attr_ImpoParaNum) {
      this.attr_ImpoParaNum = attr_ImpoParaNum;
   }

   public String getAttr_Partition() {
      return attr_Partition;
   }

   public void setAttr_Partition(String attr_Partition) {
      this.attr_Partition = attr_Partition;
   }

   public String getAttr_QualMethod() {
      return attr_QualMethod;
   }

   public void setAttr_QualMethod(String attr_QualMethod) {
      this.attr_QualMethod = attr_QualMethod;
   }

   public String getAttr_Subsystm() {
      return attr_Subsystm;
   }

   public void setAttr_Subsystm(String attr_Subsystm) {
      this.attr_Subsystm = attr_Subsystm;
   }

   public String getAttr_TechPerfParam() {
      return attr_TechPerfParam;
   }

   public void setAttr_TechPerfParam(String attr_TechPerfParam) {
      this.attr_TechPerfParam = attr_TechPerfParam;
   }

}
