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
