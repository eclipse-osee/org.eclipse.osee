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

package org.eclipse.osee.account.rest.client;

import java.util.Map;
import org.eclipse.osee.account.rest.client.internal.AccountClientImpl;

/**
 * Class to use when using the API in a non-OSGI environment
 * 
 * @author Roberto E. Escobar
 */
public final class AccountClientStandaloneSetup {

   private AccountClientStandaloneSetup() {
      // Utility class
   }

   public static AccountClient createClient(Map<String, Object> config) {
      AccountClientImpl client = new AccountClientImpl();
      client.start(config);
      return client;
   }
}
