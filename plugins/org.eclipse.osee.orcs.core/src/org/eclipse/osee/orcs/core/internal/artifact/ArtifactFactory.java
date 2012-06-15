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
package org.eclipse.osee.orcs.core.internal.artifact;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.internal.AbstractProxy;
import org.eclipse.osee.orcs.core.internal.relation.RelationFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactWriteable;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactFactory {

   private final RelationFactory relationFactory;
   private final ArtifactTypeCache artifactTypeCache;
   private final BranchCache branchCache;

   public ArtifactFactory(RelationFactory relationFactory, ArtifactTypeCache artifactTypeCache, BranchCache branchCache) {
      super();
      this.relationFactory = relationFactory;
      this.artifactTypeCache = artifactTypeCache;
      this.branchCache = branchCache;
   }

   public ArtifactWriteable createWriteableArtifact(ArtifactData artifactData) throws OseeCoreException {
      //TODO implement an artifact class resolver for specific artifact types

      ArtifactImpl artifact = createArtifactImpl(artifactData);

      WritableArtifactProxy proxy = new WritableArtifactProxy(artifact);
      return proxy;
   }

   public ArtifactReadable createReadableArtifact(ArtifactData artifactData) throws OseeCoreException {
      //TODO implement an artifact class resolver for specific artifact types

      ArtifactImpl artifact = createArtifactImpl(artifactData);

      ReadableArtifactProxy proxy = new ReadableArtifactProxy(artifact);
      return proxy;
   }

   public WritableArtifactProxy createWriteableArtifact(ArtifactReadable readable) throws OseeCoreException {
      Conditions.checkExpressionFailOnTrue(!(readable instanceof ReadableArtifactProxy),
         "Expected [ArtifactReadable] of type [ReadableArtifactProxy] but was [%s]",
         readable != null ? readable.getClass().getName() : "null");

      ArtifactImpl artifact = asArtifactImpl(readable);
      WritableArtifactProxy writeable = new WritableArtifactProxy(artifact);
      return writeable;
   }

   @SuppressWarnings("unchecked")
   public ArtifactImpl asArtifactImpl(ArtifactReadable readable) {
      ArtifactImpl toReturn = null;
      if (readable instanceof AbstractProxy) {
         AbstractProxy<? extends ArtifactImpl> proxy = (AbstractProxy<? extends ArtifactImpl>) readable;
         toReturn = proxy.getProxiedObject();
      }
      return toReturn;
   }

   private ArtifactImpl createArtifactImpl(ArtifactData artifactData) throws OseeCoreException {
      RelationContainer relationContainer = relationFactory.createRelationContainer(artifactData.getLocalId());

      ArtifactType type = artifactTypeCache.getByGuid(artifactData.getTypeUuid());
      Branch branch = branchCache.getById(artifactData.getBranchId());
      ArtifactImpl artifact = new ArtifactImpl(type, branch, relationContainer, artifactData);
      return artifact;
   }
}
