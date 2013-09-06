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

import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractProxied<T> {

   private final T proxiedObject;
   private final OrcsSession session;
   private final ExternalArtifactManager proxyManager;

   public AbstractProxied(ExternalArtifactManager proxyManager, OrcsSession session, T proxiedObject) {
      super();
      this.proxiedObject = proxiedObject;
      this.session = session;
      this.proxyManager = proxyManager;
   }

   protected OrcsSession getSession() {
      return session;
   }

   protected ExternalArtifactManager getProxyManager() {
      return proxyManager;
   }

   protected T getProxiedObject() {
      return proxiedObject;
   }

   @Override
   public String toString() {
      return getProxiedObject().toString();
   }

   @Override
   public boolean equals(Object arg0) {
      return getProxiedObject().equals(arg0);
   }

   @Override
   public int hashCode() {
      return getProxiedObject().hashCode();
   }

}