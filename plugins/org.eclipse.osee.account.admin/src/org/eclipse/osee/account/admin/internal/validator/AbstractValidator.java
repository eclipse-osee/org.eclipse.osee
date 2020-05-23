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

import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractValidator implements FieldValidator {

   @Override
   public String getName() {
      return getFieldType().name();
   }

   @Override
   public int getPriority() {
      return getFieldType().ordinal();
   }

   @Override
   public void validate(String value) {
      Conditions.checkNotNullOrEmpty(value, getName());
      Conditions.checkExpressionFailOnTrue(!isValid(value), "Invalid [%s]", getName());
   }

}
