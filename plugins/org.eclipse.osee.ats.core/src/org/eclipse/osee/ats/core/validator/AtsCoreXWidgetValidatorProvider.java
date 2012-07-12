/*
 * Created on Mar 20, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
         atsValidators.add(new AtsXComboBooleanValidator());
         atsValidators.add(new AtsXListValidator());
      }
      return atsValidators;
   }

}
