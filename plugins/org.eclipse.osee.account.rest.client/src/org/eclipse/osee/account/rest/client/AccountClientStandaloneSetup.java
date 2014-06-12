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
