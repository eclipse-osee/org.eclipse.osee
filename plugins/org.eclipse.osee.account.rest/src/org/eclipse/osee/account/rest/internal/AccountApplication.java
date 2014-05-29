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
package org.eclipse.osee.account.rest.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.account.admin.AccountAdmin;

/**
 * @author Roberto E. Escobar
 */
@ApplicationPath("oseex/accounts")
public class AccountApplication extends Application {

   private final Set<Object> singletons = new HashSet<Object>();
   private AccountAdmin accountAdmin;

   public void setAccountAdmin(AccountAdmin accountAdmin) {
      this.accountAdmin = accountAdmin;
   }

   public void start() {
      AccountOps ops = new AccountOps(accountAdmin);
      singletons.add(new AccountsResource(ops));
   }

   public void stop() {
      singletons.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

}
