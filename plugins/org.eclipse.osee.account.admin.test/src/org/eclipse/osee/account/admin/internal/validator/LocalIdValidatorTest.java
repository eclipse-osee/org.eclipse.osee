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

import org.eclipse.osee.account.admin.AccountField;

/**
 * Test Case for {@link LocalIdValidator}
 * 
 * @author Roberto E. Escobar
 */
public class LocalIdValidatorTest extends AbstractValidatorTest<LocalIdValidator> {

   private static final String VALID_LOCALID = "12341";
   private static final String INVALID_LOCALID = "12 31 asd";

   public LocalIdValidatorTest() {
      super(AccountField.LOCAL_ID, VALID_LOCALID, INVALID_LOCALID);
   }

   @Override
   protected LocalIdValidator createValidator() {
      return new LocalIdValidator();
   }

}
