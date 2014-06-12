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
package org.eclipse.osee.jaxrs.client.internal;

import java.util.Collections;
import org.eclipse.osee.jaxrs.client.JaxRsClient.JaxRsClientFactory;
import org.eclipse.osee.jaxrs.client.internal.ext.CxfJaxRsClientConfigurator;
import org.eclipse.osee.jaxrs.client.internal.ext.CxfJaxRsClientFactory;

/**
 * @author Roberto E. Escobar
 */
public final class JaxRsClientRuntime {

   private JaxRsClientRuntime() {
      //
   }

   private static volatile JaxRsClientFactory instance;

   public static JaxRsClientFactory getClientFactoryInstance() {
      if (instance == null) {
         instance = newClientFactory();
      }
      return instance;
   }

   private static JaxRsClientFactory newClientFactory() {
      CxfJaxRsClientConfigurator configurator = new CxfJaxRsClientConfigurator();
      configurator.configureJaxRsRuntime();
      configurator.configureDefaults(Collections.<String, Object> emptyMap());
      return new CxfJaxRsClientFactory(configurator);
   }

}