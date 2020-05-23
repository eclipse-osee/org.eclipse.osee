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