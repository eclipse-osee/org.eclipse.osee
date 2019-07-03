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

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Megumi Telles
 */
public class ExternalArtifactManagerImpl implements ExternalArtifactManager {

   private final Function<ArtifactReadable, Artifact> readableToArtifact;
   private final RelationManager relationManager;
   private final ArtifactTypes artifactTypeCache;

   public static interface ProxyProvider {
      Artifact getInternalArtifact(ArtifactReadable external);
   }

   public ExternalArtifactManagerImpl(RelationManager relationManager, ArtifactTypes artifactTypeCache) {
      this.relationManager = relationManager;
      this.readableToArtifact = new ReadableToArtifactFunction();
      this.artifactTypeCache = artifactTypeCache;
   }

   @Override
   public Artifact asInternalArtifact(ArtifactReadable external) {
      return external == null || external.isInvalid() ? null : ((ArtifactReadOnlyImpl) external).getProxiedObject();
   }

   @Override
   public ArtifactReadable asExternalArtifact(OrcsSession session, Artifact artifact) {
      return artifact == null ? null : new ArtifactReadOnlyImpl(this, relationManager, session, artifact,
         artifactTypeCache.get(artifact.getArtifactType()));
   }

   @Override
   public <T> AttributeReadable<T> asExternalAttribute(OrcsSession session, Attribute<T> attribute) {
      return attribute == null ? null : new AttributeReadOnlyImpl<>(this, session, attribute);
   }

   @Override
   public ResultSet<? extends Artifact> asInternalArtifacts(Iterable<? extends ArtifactReadable> externals) {
      Iterable<Artifact> transformed = Iterables.transform(externals, readableToArtifact);
      return ResultSets.newResultSet(transformed);
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
      return ResultSets.newResultSet(transformed);
   }

   @Override
   public <T> ResultSet<AttributeReadable<T>> asExternalAttributes(final OrcsSession session, Iterable<? extends Attribute<T>> attributes) {
      Iterable<AttributeReadable<T>> transformed =
         Iterables.transform(attributes, new Function<Attribute<T>, AttributeReadable<T>>() {

            @Override
            public AttributeReadable<T> apply(Attribute<T> internal) {
               return asExternalAttribute(session, internal);
            }
         });
      return ResultSets.newResultSet(transformed);
   }

   private final class ReadableToArtifactFunction implements Function<ArtifactReadable, Artifact> {

      @Override
      public Artifact apply(ArtifactReadable external) {
         return asInternalArtifact(external);
      }
   };
}
