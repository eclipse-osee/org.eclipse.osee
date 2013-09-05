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
package org.eclipse.osee.orcs.core.internal.loader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Collection;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.internal.ArtifactBuilderFactory;
import org.eclipse.osee.orcs.core.internal.ArtifactLoader;
import org.eclipse.osee.orcs.core.internal.ArtifactLoaderFactory;

/**
 * @author Andrew M. Finkbeiner
 */
public class ArtifactLoaderFactoryImpl implements ArtifactLoaderFactory {

   private final DataLoaderFactory dataLoaderFactory;
   private final ArtifactBuilderFactory builderFactory;

   public ArtifactLoaderFactoryImpl(DataLoaderFactory dataLoaderFactory, ArtifactBuilderFactory builderFactory) {
      super();
      this.dataLoaderFactory = dataLoaderFactory;
      this.builderFactory = builderFactory;
   }

   @Override
   public int getCount(HasCancellation cancellation, QueryContext queryContext) throws OseeCoreException {
      return dataLoaderFactory.getCount(cancellation, queryContext);
   }

   @Override
   public ArtifactLoader fromQueryContext(OrcsSession session, QueryContext queryContext) throws OseeCoreException {
      DataLoader loader = dataLoaderFactory.fromQueryContext(queryContext);
      return create(session, loader);
   }

   @Override
   public ArtifactLoader fromBranchAndArtifactIds(OrcsSession session, IOseeBranch branch, Collection<Integer> artifactIds) throws OseeCoreException {
      DataLoader loader = dataLoaderFactory.fromBranchAndArtifactIds(session, branch, artifactIds);
      return create(session, loader);
   }

   @Override
   public ArtifactLoader fromBranchAndArtifactIds(OrcsSession session, IOseeBranch branch, int... artifactIds) throws OseeCoreException {
      DataLoader loader = dataLoaderFactory.fromBranchAndArtifactIds(session, branch, artifactIds);
      return create(session, loader);
   }

   @SuppressWarnings("unchecked")
   private <T> T create(OrcsSession session, DataLoader loader) {
      InvocationHandler handler = new ArtifactLoaderInvocationHandler(builderFactory, session, loader);
      Class<?>[] types = new Class<?>[] {ArtifactLoader.class};
      return (T) Proxy.newProxyInstance(ArtifactLoader.class.getClassLoader(), types, handler);
   }

}
