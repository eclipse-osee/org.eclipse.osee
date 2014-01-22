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

import org.eclipse.osee.account.rest.client.internal.AccountClientImpl;
import org.eclipse.osee.account.rest.client.internal.AccountClientModule;
import org.eclipse.osee.rest.client.OseeClientConfig;
import org.eclipse.osee.rest.client.OseeRestClientStandaloneSetup;
import com.google.inject.Module;

/**
 * Class to use when using the API in a non-OSGI environment
 * 
 * @author Roberto E. Escobar
 */
public final class AccountClientStandaloneSetup {

   private AccountClientStandaloneSetup() {
      // Utility class
   }

   public static AccountClient createClient(OseeClientConfig config) {
      Module module = new AccountClientModule();
      return OseeRestClientStandaloneSetup.createClient(AccountClientImpl.class, config, module);
   }
}
