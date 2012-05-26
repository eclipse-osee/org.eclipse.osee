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

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.RelationTypeCache;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.orcs.core.internal.transaction.ReadableArtifactProxy;
import org.eclipse.osee.orcs.core.internal.transaction.WritableArtifactProxy;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactFactory {

   private final RelationTypeCache relationTypeCache;

   public ArtifactFactory(RelationTypeCache relationTypeCache) {
      this.relationTypeCache = relationTypeCache;
   }

   public ReadableArtifact loadExisitingArtifact(int artId, String guid, String humandReadableId, ArtifactType artifactType, int gammaId, Branch branch, int transactionId, ModificationType modType, boolean historical) {
      //TODO implement an artifact class resolver for specific artifact types
      Artifact artifact =
         new Artifact(artId, guid, humandReadableId, artifactType, gammaId, branch, transactionId, modType, historical,
            relationTypeCache);
      ReadableArtifactProxy proxy = new ReadableArtifactProxy(artifact);
      return proxy;
   }

   public WritableArtifactProxy createWriteableArtifact(ReadableArtifact readable) {
      ReadableArtifactProxy proxy = (ReadableArtifactProxy) readable;
      Artifact artifact = proxy.getProxiedOject();
      WritableArtifactProxy writeable = new WritableArtifactProxy(artifact);
      return writeable;
   }
}
