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

package org.eclipse.osee.jaxrs.server.internal;

import javax.ws.rs.core.Application;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class JaxRsVisitor {

   public void onStartRegistry() {
      //
   }

   public void onServletContext(String servletContext, int numberOfAppContexts) {
      //
   }

   public void onProvider(String componentName, Bundle bundle, Object provider) {
      //
   }

   public void onStartApplicationContainer(String applicationContext, int size) {
      //
   }

   public void onApplication(String applicationContext, String componentName, Bundle bundle, Application application) {
      //
   }

   public void onEndApplicationContainer() {
      //
   }

   public void onEndRegistry() {
      //
   }

}