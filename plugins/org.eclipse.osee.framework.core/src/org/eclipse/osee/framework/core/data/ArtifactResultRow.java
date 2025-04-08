/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class ArtifactResultRow {

   private ArtifactTypeToken artType;
   private ArtifactToken artifact;
   private BranchId branch;
   private List<ArtifactResultRow> children = new ArrayList<>();
   private List<String> values = new ArrayList<>();

   public ArtifactResultRow() {
      // for jax-rs
   }

   public ArtifactResultRow(ArtifactToken artifact, BranchId branch, ArtifactTypeToken artType, String... values) {
      this.artifact = artifact;
      this.branch = branch;
      this.artType = artType;
      for (String value : values) {
         this.values.add(value);
      }
   }

   public ArtifactTypeToken getArtType() {
      return artType;
   }

   public void setArtType(ArtifactTypeToken artType) {
      this.artType = artType;
   }

   public ArtifactToken getArtifact() {
      return artifact;
   }

   public void setArtifact(ArtifactToken artifact) {
      this.artifact = artifact;
   }

   public List<String> getValues() {
      return values;
   }

   public List<ArtifactResultRow> getChildren() {
      return children;
   }

   public void setChildren(List<ArtifactResultRow> children) {
      this.children = children;
   }

   public void addChild(ArtifactResultRow resultRow) {
      this.children.add(resultRow);
   }

   public void setValues(List<String> values) {
      this.values = values;
   }

   public void addValues(String... values) {
      for (String val : values) {
         this.values.add(val);
      }
   }

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }
}
