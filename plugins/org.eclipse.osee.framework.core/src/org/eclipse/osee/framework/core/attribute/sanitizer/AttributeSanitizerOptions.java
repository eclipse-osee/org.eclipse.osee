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
package org.eclipse.osee.framework.core.attribute.sanitizer;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;

/**
 * Options that scope which attributes are scanned and sanitized.
 *
 * @author Jaden W. Puckett
 */
public class AttributeSanitizerOptions {

   /**
    * Optional list of attribute types to scan/sanitize.
    * <p>
    * When provided (non-null and non-empty), only attributes whose {@link IAttribute#getAttributeType()} is contained
    * in this list are evaluated for non-ASCII characters and potentially cleaned. When omitted or empty, all attribute
    * types on the selected artifacts are scanned.
    */
   List<AttributeTypeToken> attributeTypes = new ArrayList<>();

   /**
    * Optional list of artifact types to include in the scan.
    * <p>
    * When provided (non-null and non-empty), only artifacts whose type matches one of these {@link ArtifactTypeToken}s
    * are retrieved by the query. When omitted or empty, artifacts are not filtered by type.
    */
   List<ArtifactTypeToken> artifactTypes = new ArrayList<>();

   /**
    * Optional list of specific artifact ids to include in the scan.
    * <p>
    * When provided (non-null and non-empty), only artifacts with ids in this list are retrieved by the query. When
    * omitted or empty, artifacts are not filtered by id.
    */
   List<ArtifactId> artifactIds = new ArrayList<>();

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
}
