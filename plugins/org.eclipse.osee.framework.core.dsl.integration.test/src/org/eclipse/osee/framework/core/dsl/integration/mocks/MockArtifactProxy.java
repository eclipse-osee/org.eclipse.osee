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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactProxy;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.RelationType;

/**
 * @author Roberto E. Escobar
 */
public class MockArtifactProxy implements ArtifactProxy {

   private final String guid;
   private final ArtifactType artifactType;
   private final Collection<ArtifactProxy> hierarchy;
   private final Collection<RelationType> validRelationTypes;
   private final IBasicArtifact<?> artifactObject;

   public MockArtifactProxy(IBasicArtifact<?> artifactObject) {
      this(artifactObject.getGuid(), artifactObject.getArtifactType(), artifactObject,
         Collections.<ArtifactProxy> emptyList(), Collections.<RelationType> emptyList());
   }

   public MockArtifactProxy(String guid, ArtifactType artifactType) {
      this(guid, artifactType, null, Collections.<ArtifactProxy> emptyList(), Collections.<RelationType> emptyList());
   }

   public MockArtifactProxy(String guid, ArtifactType artifactType, IBasicArtifact<?> artifactObject, Collection<ArtifactProxy> hierarchy, Collection<RelationType> validRelationTypes) {
      this.guid = guid;
      this.artifactType = artifactType;
      this.hierarchy = hierarchy;
      this.validRelationTypes = validRelationTypes;
      this.artifactObject = artifactObject;
   }

   @Override
   public String getGuid() {
      return guid;
   }

   @Override
   public ArtifactType getArtifactType() {
      return artifactType;
   }

   @Override
   public boolean isAttributeTypeValid(IAttributeType attributeType) {
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
   public IBasicArtifact<?> getObject() {
      return artifactObject;
   }

   @Override
   public BranchId getBranch() {
      return getBranchToken();
   }

   @Override
   public IOseeBranch getBranchToken() {
      return TokenFactory.createBranch(getBranchId(), getClass().getName());
   }

   @Override
   public String getName() {
      return artifactObject.getName();
   }

   @Override
   public Long getId() {
      return Long.valueOf(artifactObject.getArtId());
   }
}
