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

import java.util.regex.Pattern;
import org.eclipse.osee.account.admin.AccountAdminConfiguration;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.ds.AccountStorage;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class EmailValidator extends AbstractConfigurableValidator {

   private static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + //
   "\\@" + //
   "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + // 
   "(" + //
   "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + //
   ")+");

   private final AccountStorage storage;

   public EmailValidator(AccountStorage storage) {
      super();
      this.storage = storage;
   }

   @Override
   public AccountField getFieldType() {
      return AccountField.EMAIL;
   }

   @Override
   public boolean exists(String value) {
      return storage.emailExists(value);
   }

   @Override
   public boolean isValid(String value) {
      boolean result = Strings.isValid(value);
      if (result) {
         result = isValid(EMAIL_ADDRESS_PATTERN, value);
         if (result && hasCustomPattern()) {
            result = isValid(getCustomPattern(), value);
         }
      }
      return result;
   }

   @Override
   public String getPatternFromConfig(AccountAdminConfiguration config) {
      return config.getEmailPattern();
   }

}
