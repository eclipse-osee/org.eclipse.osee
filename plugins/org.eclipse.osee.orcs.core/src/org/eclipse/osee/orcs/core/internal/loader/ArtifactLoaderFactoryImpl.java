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
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.internal.ArtifactBuilderFactory;
import org.eclipse.osee.orcs.core.internal.ArtifactLoader;
import org.eclipse.osee.orcs.core.internal.ArtifactLoaderFactory;
import org.eclipse.osee.orcs.core.internal.SessionContext;

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
   public ArtifactLoader fromQueryContext(SessionContext sessionContext, QueryContext queryContext) throws OseeCoreException {
      DataLoader loader = dataLoaderFactory.fromQueryContext(queryContext);
      return create(loader);
   }

   @Override
   public ArtifactLoader fromBranchAndArtifactIds(SessionContext sessionContext, IOseeBranch branch, Collection<Integer> artifactIds) throws OseeCoreException {
      String sessionId = sessionContext.getSessionId();
      DataLoader loader = dataLoaderFactory.fromBranchAndArtifactIds(sessionId, branch, artifactIds);
      return create(loader);
   }

   @Override
   public ArtifactLoader fromBranchAndArtifactIds(SessionContext sessionContext, IOseeBranch branch, int... artifactIds) throws OseeCoreException {
      String sessionId = sessionContext.getSessionId();
      DataLoader loader = dataLoaderFactory.fromBranchAndArtifactIds(sessionId, branch, artifactIds);
      return create(loader);
   }

   @SuppressWarnings("unchecked")
   private <T> T create(DataLoader loader) {
      InvocationHandler handler = new ArtifactLoaderInvocationHandler(loader, builderFactory);
      Class<?>[] types = new Class<?>[] {ArtifactLoader.class};
      return (T) Proxy.newProxyInstance(ArtifactLoader.class.getClassLoader(), types, handler);
   }

}
