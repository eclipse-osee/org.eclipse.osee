/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.dsl.integration.mocks;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactProxy;

/**
 * @author Roberto E. Escobar
 */
public class MockArtifactProxy implements ArtifactProxy {

   private final ArtifactTypeToken artifactType;
   private final Collection<ArtifactProxy> hierarchy;
   private final Collection<RelationTypeToken> validRelationTypes;
   private final ArtifactToken artifactObject;

   public MockArtifactProxy(ArtifactToken artifactObject) {
      this(artifactObject.getArtifactType(), artifactObject, Collections.emptyList(), Collections.emptyList());
   }

   public MockArtifactProxy(ArtifactToken artifactObject, RelationTypeToken validRelationType) {
      this(artifactObject.getArtifactType(), artifactObject, Collections.emptyList(),
         Collections.singleton(validRelationType));
   }

   public MockArtifactProxy(ArtifactTypeToken artifactType, RelationTypeToken validRelationType) {
      this(artifactType, null, Collections.emptyList(), Collections.singleton(validRelationType));
   }

   public MockArtifactProxy() {
      this((ArtifactTypeToken) null);
   }

   public MockArtifactProxy(ArtifactTypeToken artifactType) {
      this(artifactType, null, Collections.emptyList(), Collections.emptyList());
   }

   private MockArtifactProxy(ArtifactTypeToken artifactType, ArtifactToken artifactObject, Collection<ArtifactProxy> hierarchy, Collection<RelationTypeToken> validRelationTypes) {
      this.artifactType = artifactType;
      this.hierarchy = hierarchy;
      this.validRelationTypes = validRelationTypes;
      this.artifactObject = artifactObject;
   }

   @Override
   public String getGuid() {
      return artifactObject.getGuid();
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   @Override
   public Collection<RelationTypeToken> getValidRelationTypes() {
      return validRelationTypes;
   }

   @Override
   public Collection<ArtifactProxy> getHierarchy() {
      return hierarchy;
   }

   @Override
   public BranchToken getBranch() {
      return artifactObject.getBranch();
   }

   @Override
   public BranchToken getBranchToken() {
      return BranchToken.create(getBranch(), getClass().getName());
   }

   @Override
   public String getName() {
      return artifactObject.getName();
   }

   @Override
   public Long getId() {
      return Long.valueOf(artifactObject.getId());
   }
}