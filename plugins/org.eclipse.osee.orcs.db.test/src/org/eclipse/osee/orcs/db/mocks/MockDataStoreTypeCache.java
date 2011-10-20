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
package org.eclipse.osee.orcs.db.mocks;

import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.orcs.DataStoreTypeCache;

/**
 * @author Roberto E. Escobar
 */
public class MockDataStoreTypeCache implements DataStoreTypeCache {

   @Override
   public AttributeTypeCache getAttributeTypeCache() {
      return null;
   }

   @Override
   public ArtifactTypeCache getArtifactTypeCache() {
      return null;
   }

   @Override
   public RelationTypeCache getRelationTypeCache() {
      return null;
   }

   @Override
   public List<RelationType> getValidRelationTypes(IArtifactType artifactType, IOseeBranch branch) throws OseeCoreException {
      return null;
   }

   @Override
   public int getRelationSideMax(RelationType relationType, IArtifactType artifactType, RelationSide relationSide) throws OseeCoreException {
      return 0;
   }

}
