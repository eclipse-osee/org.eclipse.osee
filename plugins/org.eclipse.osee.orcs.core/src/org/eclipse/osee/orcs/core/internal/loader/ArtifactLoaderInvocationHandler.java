/*******************************************************************************
 * Copyright (c) 2012 Boeing.
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
import java.lang.reflect.Method;
import java.util.List;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.data.ResultSetList;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.internal.ArtifactBuilder;
import org.eclipse.osee.orcs.core.internal.ArtifactBuilderFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Andrew M. Finkbeiner
 */
public class ArtifactLoaderInvocationHandler implements InvocationHandler {
   private final DataLoader proxied;
   private final ArtifactBuilderFactory builderFactory;

   public ArtifactLoaderInvocationHandler(DataLoader proxied, ArtifactBuilderFactory builderFactory) {
      super();
      this.proxied = proxied;
      this.builderFactory = builderFactory;
   }

   @Override
   public Object invoke(Object object, Method method, Object[] args) throws Throwable {
      Object toReturn = null;
      if (isLoad(method)) {
         HasCancellation cancellation = null;
         if (args != null && args.length > 0) {
            cancellation = (HasCancellation) args[0];
         }
         toReturn = load(cancellation);
      } else if (isGetResults(method)) {
         HasCancellation cancellation = null;
         if (args != null && args.length > 0) {
            cancellation = (HasCancellation) args[0];
         }
         toReturn = getResults(cancellation);
      } else {
         Method delegateMethod = getMethodFor(proxied.getClass(), method);
         toReturn = delegateMethod.invoke(proxied, args);
         if (toReturn instanceof DataLoader) {
            toReturn = object;
         }
      }
      return toReturn;
   }

   private ResultSet<ArtifactReadable> getResults(HasCancellation cancellation) throws OseeCoreException {
      List<ArtifactReadable> data = load(cancellation);
      return new ResultSetList<ArtifactReadable>(data);
   }

   private boolean isLoad(Method method) {
      return "load".equals(method.getName());
   }

   private boolean isGetResults(Method method) {
      return "getResults".equals(method.getName());
   }

   private List<ArtifactReadable> load(HasCancellation cancellation) throws OseeCoreException {
      ArtifactBuilder builder = builderFactory.createArtifactBuilder();
      proxied.load(cancellation, builder);
      return builder.getArtifacts();
   }

   private Method getMethodFor(Class<?> clazz, Method method) {
      Method toReturn = null;
      try {
         toReturn = clazz.getMethod(method.getName(), method.getParameterTypes());
      } catch (Exception ex) {
         // Do Nothing;
      }
      return toReturn;
   }

}