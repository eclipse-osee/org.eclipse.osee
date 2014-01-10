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
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * Test Case for {@link UuidValidator}
 * 
 * @author Roberto E. Escobar
 */
public class UuidValidatorTest extends AbstractValidatorTest<UuidValidator> {

   private static final String VALID_UUID = GUID.create();
   private static final String INVALID_UUID = "12 31 asd";

   public UuidValidatorTest() {
      super(AccountField.UUID, VALID_UUID, INVALID_UUID);
   }

   @Override
   protected UuidValidator createValidator() {
      return new UuidValidator();
   }

}
