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

import static org.eclipse.osee.account.admin.AccountConstants.ACCOUNT_DISPLAY_NAME_VALIDATION_PATTERN;
import static org.eclipse.osee.account.admin.AccountConstants.DEFAULT_DISPLAY_NAME_VALIDATION_PATTERN;
import java.util.Map;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.ds.AccountStorage;

/**
 * @author Roberto E. Escobar
 */
public class DisplayNameValidator extends AbstractConfigurableValidator {

   private final AccountStorage storage;

   public DisplayNameValidator(AccountStorage storage) {
      super();
      this.storage = storage;
   }

   @Override
   public AccountField getFieldType() {
      return AccountField.DISPLAY_NAME;
   }

   @Override
   public boolean exists(String value) {
      return storage.displayNameExists(value);
   }

   @Override
   public String getPatternFromConfig(Map<String, Object> props) {
      return get(props, ACCOUNT_DISPLAY_NAME_VALIDATION_PATTERN, DEFAULT_DISPLAY_NAME_VALIDATION_PATTERN);
   }

}
