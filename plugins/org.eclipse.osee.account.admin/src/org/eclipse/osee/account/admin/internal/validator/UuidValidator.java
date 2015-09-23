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
 * @author Roberto E. Escobar
 */
public class UuidValidator extends AbstractValidator {

   @Override
   public AccountField getFieldType() {
      return AccountField.GUID;
   }

   @Override
   public boolean isValid(String value) {
      return GUID.isValid(value);
   }

}
