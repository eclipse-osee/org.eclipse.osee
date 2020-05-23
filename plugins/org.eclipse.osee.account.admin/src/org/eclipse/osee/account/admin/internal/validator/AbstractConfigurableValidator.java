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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractConfigurableValidator extends AbstractValidator {

   private Pattern customPattern;

   public void setCustomPattern(Pattern customPattern) {
      this.customPattern = customPattern;
   }

   public boolean hasCustomPattern() {
      return customPattern != null;
   }

   public Pattern getCustomPattern() {
      return customPattern;
   }

   @Override
   public boolean isValid(String value) {
      boolean result = Strings.isValid(value);
      if (result && hasCustomPattern()) {
         result = isValid(getCustomPattern(), value);
      }
      return result;
   }

   protected boolean isValid(Pattern pattern, String value) {
      Matcher matcher = pattern.matcher(value);
      return matcher.matches();
   }

   @Override
   public void validate(String value) {
      super.validate(value);
      Conditions.checkExpressionFailOnTrue(exists(value), "Invalid [%s] - [%s] is already in use", getName(), value);
   }

   public abstract String getPatternFromConfig(Map<String, Object> props);

   public abstract boolean exists(String value);

   protected String get(Map<String, Object> props, String key, String defaultValue) {
      String toReturn = defaultValue;
      Object object = props != null ? props.get(key) : null;
      if (object != null) {
         toReturn = String.valueOf(object);
      }
      return toReturn;
   }

}
