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

import org.eclipse.osee.account.admin.AccountField;

/**
 * @author Roberto E. Escobar
 */
public class NoopValidator implements FieldValidator {

   @Override
   public AccountField getFieldType() {
      return AccountField.UNKNOWN;
   }

   @Override
   public boolean isValid(String value) {
      return true;
   }

   @Override
   public void validate(String value) {
      // Do Nothing;
   }

   @Override
   public String getName() {
      return getFieldType().name();
   }

   @Override
   public int getPriority() {
      return Integer.MAX_VALUE;
   }

}
