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

import java.util.HashMap;
import java.util.Map;

public abstract class FieldValidator implements IValidator {

   protected String fieldName;
   private String message;
   protected Map<String, Object> paramMap;

   FieldValidator(String fieldName) {
      this.fieldName = fieldName;
      this.message = "";
      this.paramMap = new HashMap<String, Object>();
   }

   public String getFieldName() {
      return fieldName;
   }

   public String getMessage() {
      for (String key : paramMap.keySet()) {
         message = message.replaceAll("\\$\\{" + key + "\\}", paramMap.get(key).toString());
      }
      return message;
   }

   void setMessage(String message) {
      this.message = message;
   }

   public void setItemToValidate(Object object) {
      this.paramMap.put(fieldName, object);
   }

   void addParam(String paramName, Object value) {
      this.paramMap.put(paramName, value);
   }
}
