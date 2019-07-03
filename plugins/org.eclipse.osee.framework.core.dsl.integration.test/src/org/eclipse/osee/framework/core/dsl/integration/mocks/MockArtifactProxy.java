/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.integration.mocks;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactProxy;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.RelationType;

/**
 * @author Roberto E. Escobar
 */
public class MockArtifactProxy implements ArtifactProxy {

   private final ArtifactType artifactType;
   private final Collection<ArtifactProxy> hierarchy;
   private final Collection<RelationType> validRelationTypes;
   private final ArtifactToken artifactObject;

   public MockArtifactProxy(ArtifactToken artifactObject) {
      this(null, artifactObject, Collections.emptyList(), Collections.emptyList());
   }

   public MockArtifactProxy() {
      this((ArtifactType) null);
   }

   public MockArtifactProxy(ArtifactType artifactType) {
      this(artifactType, null, Collections.emptyList(), Collections.emptyList());
   }

   public MockArtifactProxy(ArtifactType artifactType, ArtifactToken artifactObject, Collection<ArtifactProxy> hierarchy, Collection<RelationType> validRelationTypes) {
      this.artifactType = artifactType;
      this.hierarchy = hierarchy;
      this.validRelationTypes = validRelationTypes;
      this.artifactObject = artifactObject;
   }

   @Override
   public boolean isOfType(ArtifactTypeId... artifactTypes) {
      return artifactType.inheritsFrom(artifactTypes);
   }

   @Override
   public String getGuid() {
      return artifactObject.getGuid();
   }

   @Override
   public ArtifactType getArtifactType() {
      return artifactType;
   }

   @Override
   public boolean isAttributeTypeValid(AttributeTypeId attributeType) {
      return false;
   }

   @Override
   public Collection<RelationType> getValidRelationTypes() {
      return validRelationTypes;
   }

   @Override
   public Collection<ArtifactProxy> getHierarchy() {
      return hierarchy;
   }

   @Override
   public ArtifactToken getObject() {
      return artifactObject;
   }

   @Override
   public BranchId getBranch() {
      return artifactObject.getBranch();
   }

   @Override
   public IOseeBranch getBranchToken() {
      return IOseeBranch.create(getBranch(), getClass().getName());
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
