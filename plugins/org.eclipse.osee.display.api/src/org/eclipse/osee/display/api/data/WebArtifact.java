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

import java.util.ArrayList;
import java.util.Collection;

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
   private final WebId branch;

   //ancestry should include all parental Artifacts starting with parent(index 0), grandparent(index 1), great-grandparent(index 2), etc. 
   private final Collection<WebArtifact> ancestry = new ArrayList<WebArtifact>();

   public WebArtifact(String guid, String artifactName, String artifactType, Collection<WebArtifact> ancestry, WebId branch) {
      this.guid = guid;
      this.artifactName = artifactName;
      this.artifactType = artifactType;
      if (ancestry != null) {
         this.ancestry.addAll(ancestry);
      }
      this.branch = branch;
   }

   public WebArtifact(String guid, String artifactName, String artifactType) {
      this(guid, artifactName, artifactType, (Collection<WebArtifact>) null, null);
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

   public WebId getBranch() {
      return branch;
   }

   public Collection<WebArtifact> getAncestry() {
      return ancestry;
   }

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
