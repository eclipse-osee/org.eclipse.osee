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
package org.eclipse.osee.account.admin.internal;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.eclipse.osee.account.admin.AccountAdminConfiguration;
import org.eclipse.osee.account.admin.AccountField;
import org.eclipse.osee.account.admin.internal.validator.AbstractConfigurableValidator;
import org.eclipse.osee.account.admin.internal.validator.FieldValidator;
import org.eclipse.osee.account.admin.internal.validator.NoopValidator;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class AccountValidator {

   protected static final NoopValidator DEFAULT_VALIDATOR = new NoopValidator();

   private final Log logger;
   private final Map<AccountField, FieldValidator> validators;

   private Iterable<FieldValidator> ordered;

   public AccountValidator(Log logger, Map<AccountField, FieldValidator> validators) {
      this.logger = logger;
      this.validators = validators;
   }

   public void configure(AccountAdminConfiguration config) {
      logger.info("Start Account Validator Config Update...");
      for (FieldValidator validator : validators.values()) {
         if (validator instanceof AbstractConfigurableValidator) {
            AbstractConfigurableValidator configurable = (AbstractConfigurableValidator) validator;
            configure(config, configurable);
         }
      }
      logger.info("Completed Account Validator Config Update");
   }

   private void configure(AccountAdminConfiguration config, AbstractConfigurableValidator configurable) {
      String patternString = configurable.getPatternFromConfig(config);
      if (Strings.isValid(patternString)) {
         try {
            Pattern customPattern = Pattern.compile(patternString);
            configurable.setCustomPattern(customPattern);
            logger.info("Configured validator [%s] with [%s]", configurable.getName(), customPattern.pattern());
         } catch (Throwable th) {
            logger.error(th, "Error configuring validator [%s] - custom pattern[%s] was invalid.",
               configurable.getName(), patternString);
         }
      }
   }

   public FieldValidator getValidator(AccountField type) {
      FieldValidator validator = validators.get(type);
      if (validator == null) {
         validator = DEFAULT_VALIDATOR;
      }
      return validator;
   }

   public boolean isValid(AccountField type, String value) {
      FieldValidator validator = getValidator(type);
      return validator.isValid(value);
   }

   public void validate(AccountField type, String value) {
      FieldValidator validator = getValidator(type);
      validator.validate(value);
   }

   public Iterable<FieldValidator> getOrdered() {
      if (ordered == null) {
         List<FieldValidator> list = new LinkedList<FieldValidator>(validators.values());
         list.add(DEFAULT_VALIDATOR);
         Collections.sort(list, Validators.VALIDATOR_PRIORITY_ORDER_COMPARATOR);
         ordered = list;
      }
      return ordered;
   }

   public AccountField guessFormatType(String value) {
      AccountField toReturn = AccountField.UNKNOWN;
      if (Strings.isValid(value)) {
         for (FieldValidator validator : getOrdered()) {
            if (validator.isValid(value)) {
               toReturn = validator.getFieldType();
               break;
            }
         }
      }
      return toReturn;
   }

}