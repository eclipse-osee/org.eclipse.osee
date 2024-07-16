/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.internal.proxy.impl;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.core.internal.relation.RelationManager;

/**
 * @author Megumi Telles
 */
public class ExternalArtifactManagerImpl implements ExternalArtifactManager {
   private final RelationManager relationManager;
   private final OrcsTokenService tokenService;

   public static interface ProxyProvider {
      Artifact getInternalArtifact(ArtifactReadable external);
   }

   public ExternalArtifactManagerImpl(RelationManager relationManager, OrcsTokenService tokenService) {
      this.relationManager = relationManager;
      this.tokenService = tokenService;
   }

   @Override
   public Artifact asInternalArtifact(ArtifactReadable external) {
      return external instanceof ArtifactReadOnlyImpl && external.isValid() ? ((ArtifactReadOnlyImpl) external).getProxiedObject() : null;
   }

   @Override
   public ArtifactReadable asExternalArtifact(OrcsSession session, Artifact artifact) {
      return artifact == null ? null : new ArtifactReadOnlyImpl(this, relationManager, session, artifact,
         artifact.getArtifactType());
   }

   @Override
   public <T> AttributeReadable<T> asExternalAttribute(OrcsSession session, Attribute<T> attribute) {
      return attribute == null ? null : new AttributeReadOnlyImpl<>(this, session, attribute);
   }

   @Override
   public ResultSet<? extends Artifact> asInternalArtifacts(Iterable<? extends ArtifactReadable> externals) {
      Iterable<Artifact> transformed = Iterables.transform(externals, this::asInternalArtifact);
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
}