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

package org.eclipse.osee.account.admin.internal;

import org.eclipse.osee.account.admin.Account;
import org.eclipse.osee.account.admin.AccountAdmin;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.AccountPreferences;
import org.eclipse.osee.account.admin.internal.validator.Validator;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class AccountResolver {

   private final Validator validator;
   private final AccountAdmin accountAdmin;

   public AccountResolver(Validator validator, AccountAdmin accountAdmin) {
      super();
      this.validator = validator;
      this.accountAdmin = accountAdmin;
   }

   public ResultSet<Account> resolveAccount(String uniqueFieldValue) {
      Conditions.checkNotNullOrEmpty(uniqueFieldValue, "account unique field value");
      ResultSet<Account> toReturn;
      AccountField type = validator.guessFormatType(uniqueFieldValue);
      switch (type) {
         case EMAIL:
            toReturn = accountAdmin.getAccountByEmail(uniqueFieldValue);
            break;
         default:
            toReturn = ResultSets.emptyResultSet();
            break;
      }
      return toReturn;
   }

   public ResultSet<AccountPreferences> resolveAccountPreferences(String uniqueField) {
      ResultSet<Account> results = resolveAccount(uniqueField);
      return ResultSets.transform(results, source -> source.getPreferences());
   }
}