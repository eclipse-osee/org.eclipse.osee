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
package org.eclipse.osee.framework.core.dsl.integration.test.mocks;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider.ArtifactData;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.RelationType;

/**
 * @author Roberto E. Escobar
 */
public class MockArtifactData implements ArtifactData {

   private final String guid;
   private final ArtifactType artifactType;
   private final Collection<String> hierarchy;
   private final Collection<RelationType> validRelationTypes;
   private final IBasicArtifact<?> artifactObject;

   public MockArtifactData(IBasicArtifact<?> artifactObject) {
      this(artifactObject.getGuid(), artifactObject.getArtifactType(), artifactObject,
         Collections.<String> emptyList(), Collections.<RelationType> emptyList());
   }

   public MockArtifactData(String guid, ArtifactType artifactType) {
      this(guid, artifactType, null, Collections.<String> emptyList(), Collections.<RelationType> emptyList());
   }

   public MockArtifactData(String guid, ArtifactType artifactType, IBasicArtifact<?> artifactObject, Collection<String> hierarchy, Collection<RelationType> validRelationTypes) {
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
   public Collection<String> getHierarchy() {
      return hierarchy;
   }

   @Override
   public IBasicArtifact<?> getObject() {
      return artifactObject;
   }
}
