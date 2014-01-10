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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.account.admin.AccountAdminConfiguration;
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

   public abstract String getPatternFromConfig(AccountAdminConfiguration config);

   public abstract boolean exists(String value);

}
