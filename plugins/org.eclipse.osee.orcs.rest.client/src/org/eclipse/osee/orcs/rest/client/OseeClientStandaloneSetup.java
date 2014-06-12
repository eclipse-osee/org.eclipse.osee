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

import java.util.Map;
import org.eclipse.osee.orcs.rest.client.internal.OseeClientImpl;

/**
 * Class to use when using the API in a non-OSGI environment
 * 
 * @author Roberto E. Escobar
 */
public final class OseeClientStandaloneSetup {

   private OseeClientStandaloneSetup() {
      // Utility class
   }

   public static OseeClient createClient(Map<String, Object> config) {
      OseeClientImpl client = new OseeClientImpl();
      client.start(config);
      return client;
   }
}
