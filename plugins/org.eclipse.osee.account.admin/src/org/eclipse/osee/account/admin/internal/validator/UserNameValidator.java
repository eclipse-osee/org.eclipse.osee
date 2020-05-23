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

package org.eclipse.osee.account.admin.internal.validator;

import static org.eclipse.osee.account.admin.AccountConstants.ACCOUNT_USERNAME_VALIDATION_PATTERN;
import static org.eclipse.osee.account.admin.AccountConstants.DEFAULT_USERNAME_VALIDATION_PATTERN;
import java.util.Map;
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
   public String getPatternFromConfig(Map<String, Object> props) {
      return get(props, ACCOUNT_USERNAME_VALIDATION_PATTERN, DEFAULT_USERNAME_VALIDATION_PATTERN);
   }

}
