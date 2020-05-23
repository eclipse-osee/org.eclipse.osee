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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class LocalIdValidator extends AbstractValidator {

   private static final Pattern LOCAL_ID_PATTERN = Pattern.compile("\\d+");

   @Override
   public AccountField getFieldType() {
      return AccountField.LOCAL_ID;
   }

   @Override
   public boolean isValid(String value) {
      boolean result = Strings.isValid(value);
      if (result) {
         Matcher matcher = LOCAL_ID_PATTERN.matcher(value);
         result = matcher.matches();
      }
      return result;
   }

}
