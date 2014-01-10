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
