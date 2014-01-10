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
