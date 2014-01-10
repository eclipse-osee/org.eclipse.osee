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
package org.eclipse.osee.account.admin.internal.validator;

import org.eclipse.osee.account.admin.AccountAdminConfiguration;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.ds.AccountStorage;

/**
 * @author Roberto E. Escobar
 */
public class UserNameValidator extends AbstractConfigurableValidator {

   private final AccountStorage storage;

   public UserNameValidator(AccountStorage storage) {
      super();
      this.storage = storage;
   }

   @Override
   public AccountField getFieldType() {
      return AccountField.USERNAME;
   }

   @Override
   public boolean exists(String value) {
      return storage.userNameExists(value);
   }

   @Override
   public String getPatternFromConfig(AccountAdminConfiguration config) {
      return config.getUserNamePattern();
   }

}
