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
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactDataHandler;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactCollector.LoadSourceType;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactRowMapper implements ArtifactDataHandler {

   private final SessionContext context;
   private final ArtifactFactory artifactFactory;
   private final ArtifactCollector artifactReceiver;

   public ArtifactRowMapper(SessionContext context, ArtifactFactory artifactFactory, ArtifactCollector artifactReciever) {
      this.context = context;
      this.artifactFactory = artifactFactory;
      this.artifactReceiver = artifactReciever;
   }

   @Override
   public void onData(ArtifactData data) throws OseeCoreException {
      LoadSourceType loadSourceType = LoadSourceType.FOUND_IN_CACHE;
      ArtifactReadable artifact = getLoadedArtifact(data);
      if (artifact == null) {
         loadSourceType = LoadSourceType.WAS_CREATED;

         artifact = artifactFactory.createReadableArtifact(data);
      }
      artifactReceiver.onArtifact(artifact, loadSourceType);
   }

   private ArtifactReadable getLoadedArtifact(ArtifactData current) {
      ArtifactReadable container = null;
      if (current.isHistorical()) {
         container = context.getHistorical(current.getArtifactId(), current.getStripeId());
      } else {
         container = context.getActive(current.getArtifactId(), current.getBranchId());
      }
      return container;
   }
}
