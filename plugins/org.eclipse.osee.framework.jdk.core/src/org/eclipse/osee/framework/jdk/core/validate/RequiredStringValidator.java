/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.validate;

public class RequiredStringValidator extends FieldValidator {

   public RequiredStringValidator(String fieldName) {
      super(fieldName);
   }

   public boolean isValid() {
      Object object = paramMap.get(fieldName);
      if (object != null && object instanceof String) {
         String value = (String) object;
         return (value != null && !value.equals(""));
      }
      return false;
   }
}
