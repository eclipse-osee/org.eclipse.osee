/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.framework.core.attribute.cleaner;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;

public class AttributeCleanerOptions {

   List<AttributeTypeToken> attributeTypes = new ArrayList<>();
   List<ArtifactTypeToken> artifactTypes = new ArrayList<>();
   List<ArtifactId> artifactIds = new ArrayList<>();
   BranchId branchId = BranchId.SENTINEL;

   public AttributeCleanerOptions() {

   }

   public List<AttributeTypeToken> getAttributeTypes() {
      return attributeTypes;
   }

   public void setAttributeTypes(List<AttributeTypeToken> attributeTypes) {
      this.attributeTypes = attributeTypes;
   }

   public List<ArtifactTypeToken> getArtifactTypes() {
      return artifactTypes;
   }

   public void setArtifactTypes(List<ArtifactTypeToken> artifactTypes) {
      this.artifactTypes = artifactTypes;
   }

   public List<ArtifactId> getArtifactIds() {
      return artifactIds;
   }

   public void setArtifactIds(List<ArtifactId> artifactIds) {
      this.artifactIds = artifactIds;
   }

   public BranchId getBranchId() {
      return branchId;
   }

   public void setBranchId(BranchId branchId) {
      this.branchId = branchId;
   }
}
