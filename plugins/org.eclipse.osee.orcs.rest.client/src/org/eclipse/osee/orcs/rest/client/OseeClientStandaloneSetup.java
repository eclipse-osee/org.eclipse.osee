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
