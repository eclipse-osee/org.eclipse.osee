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
package org.eclipse.osee.orcs.rest.client;

import org.eclipse.osee.jaxrs.client.OseeClientConfig;
import org.eclipse.osee.jaxrs.client.JaxRsClientFactory;
import org.eclipse.osee.orcs.rest.client.internal.OrcsClientModule;
import org.eclipse.osee.orcs.rest.client.internal.OseeClientImpl;
import com.google.inject.Module;

/**
 * Class to use when using the API in a non-OSGI environment
 * 
 * @author Roberto E. Escobar
 */
public final class OseeClientStandaloneSetup {

   private OseeClientStandaloneSetup() {
      // Utility class
   }

   public static OseeClient createClient(OseeClientConfig config) {
      Module module = new OrcsClientModule();
      return JaxRsClientFactory.createClient(OseeClientImpl.class, config, module);
   }
}
