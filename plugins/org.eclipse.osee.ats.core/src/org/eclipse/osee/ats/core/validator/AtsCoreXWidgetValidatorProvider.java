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
package org.eclipse.osee.ats.core.validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.core.validator.AtsXComboBooleanValidator.IAllowedBooleanValueProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.OseeInfo;

/**
 * @author Donald G. Dunne
 */
public class AtsCoreXWidgetValidatorProvider implements AtsXWidgetValidatorProvider {
   private static List<IAtsXWidgetValidator> atsValidators;
   public static AtsCoreXWidgetValidatorProvider instance = new AtsCoreXWidgetValidatorProvider();

   @Override
   public Collection<IAtsXWidgetValidator> getValidators() {
      if (atsValidators == null) {
         atsValidators = new ArrayList<IAtsXWidgetValidator>();
         atsValidators.add(new AtsXIntegerValidator());
         atsValidators.add(new AtsXFloatValidator());
         atsValidators.add(new AtsXTextValidator());
         atsValidators.add(new AtsXDateValidator());
         atsValidators.add(new AtsXComboValidator());
         atsValidators.add(new AtsXComboBooleanValidator(new AllowedBooleanValueProviderImpl()));
         atsValidators.add(new AtsXListValidator());
      }
      return atsValidators;
   }

   private static final class AllowedBooleanValueProviderImpl implements IAllowedBooleanValueProvider {

      private static final List<String> YES_NO_VALUES = Arrays.asList("yes", "no");
      private static final List<String> TRUE_FALSE_VALUES = Arrays.asList("true", "false");

      // if true, then database is using yes/no values
      private static final String YES_NO_KEY = "yes.no.values";

      @Override
      public Collection<String> getValues() throws OseeCoreException {
         Collection<String> toReturn = TRUE_FALSE_VALUES;
         if (OseeInfo.isCacheEnabled(YES_NO_KEY)) {
            toReturn = YES_NO_VALUES;
         }
         return toReturn;
      }

   }

}
