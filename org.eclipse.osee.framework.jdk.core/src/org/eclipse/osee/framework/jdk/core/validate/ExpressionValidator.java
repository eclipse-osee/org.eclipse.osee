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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionValidator extends FieldValidator {

   public ExpressionValidator(String fieldName) {
      super(fieldName);
   }

   public boolean isValid() {
      Object object = this.paramMap.get(fieldName);
      if (object instanceof String) {
         String toValidate = (String) object;

         Object expression = this.paramMap.get("expression");
         if (expression instanceof String) {
            String expressionString = (String) expression;
            if (expressionString.contains("matches")) {
               expressionString = expressionString.replaceAll("\\s*matches\\('", "");
               expressionString = expressionString.replaceAll("\\'\\)\\s*", "");
               return matcher(toValidate, expressionString);
               //return true;
            }
         }
      }
      return false;
   }

   private boolean matcher(String toValidate, String regularExpression) {
      Pattern pattern = Pattern.compile(regularExpression);
      Matcher matcher = pattern.matcher(toValidate);
      return matcher.matches();
   }

}
