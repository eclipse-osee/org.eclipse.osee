/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.proxy.impl;

import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationNode;
import org.eclipse.osee.orcs.core.internal.util.ResultSetIterable;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Megumi Telles
 */
public class ExternalArtifactManagerImpl implements ExternalArtifactManager {

   public ExternalArtifactManagerImpl() {
      super();
   }

   @Override
   public ResultSet<ArtifactReadable> asExternalArtifacts(final OrcsSession session, Iterable<? extends Artifact> artifacts) {
      Iterable<ArtifactReadable> transformed =
         Iterables.transform(artifacts, new Function<Artifact, ArtifactReadable>() {

            @Override
            public ArtifactReadable apply(Artifact internal) {
               return asExternalArtifact(session, internal);
            }
         });
      return new ResultSetIterable<ArtifactReadable>(transformed);
   }

   @Override
   public ResultSet<? extends RelationNode> asInternalArtifacts(Iterable<? extends ArtifactReadable> externals) {
      Iterable<Artifact> transformed = Iterables.transform(externals, new Function<ArtifactReadable, Artifact>() {

         @Override
         public Artifact apply(ArtifactReadable external) {
            return asInternalArtifact(external);
         }
      });
      return new ResultSetIterable<Artifact>(transformed);
   }

   @Override
   public Artifact asInternalArtifact(ArtifactReadable external) {
      return ((ArtifactReadOnlyImpl) external).getProxiedObject();
   }

   @Override
   public ArtifactReadable asExternalArtifact(OrcsSession session, Artifact artifact) {
      return new ArtifactReadOnlyImpl(ExternalArtifactManagerImpl.this, session, artifact);
   }

}
