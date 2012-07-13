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
package org.eclipse.osee.orcs.core.internal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.ArtifactBuilder;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.data.ArtifactReadable;

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
   public ArtifactLoader fromQueryContext(SessionContext sessionContext, QueryContext queryContext) throws OseeCoreException {
      DataLoader loader = dataLoaderFactory.fromQueryContext(queryContext);
      return create(sessionContext, loader);
   }

   @Override
   public ArtifactLoader fromBranchAndArtifactIds(SessionContext sessionContext, IOseeBranch branch, Collection<Integer> artifactIds) throws OseeCoreException {
      DataLoader loader =
         dataLoaderFactory.fromBranchAndArtifactIds(sessionContext.getSessionId(), branch, artifactIds);
      return create(sessionContext, loader);
   }

   @Override
   public ArtifactLoader fromBranchAndArtifactIds(SessionContext sessionContext, IOseeBranch branch, int... artifactIds) throws OseeCoreException {
      DataLoader loader =
         dataLoaderFactory.fromBranchAndArtifactIds(sessionContext.getSessionId(), branch, artifactIds);
      return create(sessionContext, loader);
   }

   @SuppressWarnings("unchecked")
   public <T> T create(SessionContext sessionContext, DataLoader loader) {
      InvocationHandler handler = new LoaderInvocationHandler(sessionContext, loader, builderFactory);
      Class<?>[] types = new Class<?>[] {ArtifactLoader.class};
      return (T) Proxy.newProxyInstance(ArtifactLoader.class.getClassLoader(), types, handler);
   }

   private static class LoaderInvocationHandler implements InvocationHandler {
      private final SessionContext sessionContext;
      private final DataLoader loader;
      private final ArtifactBuilderFactory builderFactory;

      public LoaderInvocationHandler(SessionContext sessionContext, DataLoader loader, ArtifactBuilderFactory builderFactory) {
         super();
         this.sessionContext = sessionContext;
         this.loader = loader;
         this.builderFactory = builderFactory;
      }

      @Override
      public Object invoke(Object object, Method method, Object[] args) throws Throwable {
         Object toReturn = null;
         if (isLoad(method)) {
            toReturn = load((HasCancellation) args[0]);
         } else {
            toReturn = method.invoke(loader, args);
            if (toReturn instanceof DataLoader) {
               toReturn = object;
            }
         }
         return toReturn;
      }

      private boolean isLoad(Method method) {
         return "load".equals(method.getName());
      }

      @SuppressWarnings("unused")
      private List<ArtifactReadable> load(HasCancellation cancellation) throws OseeCoreException {
         ArtifactBuilder builder = builderFactory.createArtifactBuilder(sessionContext);
         return builder.getArtifacts();
      }

   }

}
