/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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