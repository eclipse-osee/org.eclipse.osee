/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.jaxrs.server.internal.applications;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import org.eclipse.osee.framework.jdk.core.type.LazyObject;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsApplicationRegistry.JaxRsContainer;
import org.eclipse.osee.jaxrs.server.internal.applications.JaxRsApplicationRegistry.JaxRsContainerProvider;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsContainerProviderImpl extends LazyObject<JaxRsContainer> implements JaxRsContainerProvider {

   private final JaxRsFactory factory;
   private final String contextName;
   private volatile JaxRsContainer container;

   public JaxRsContainerProviderImpl(JaxRsFactory factory, String contextName) {
      super();
      this.factory = factory;
      this.contextName = contextName;
   }

   @Override
   protected FutureTask<JaxRsContainer> createLoaderTask() {
      return new FutureTask<>(new Callable<JaxRsContainer>() {
         @Override
         public JaxRsContainer call() throws Exception {
            container = factory.newJaxRsContainer(contextName);
            return container;
         }
      });
   }

   @Override
   public boolean hasContainer() {
      return container != null;
   }

   @Override
   public JaxRsContainer unSet() {
      JaxRsContainer oldContainer = container;
      container = null;
      invalidate();
      return oldContainer;
   }

}