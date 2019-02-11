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
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidator;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidatorProvider;

/**
 * @author Donald G. Dunne
 */
public class AtsCoreXWidgetValidatorProvider implements IAtsXWidgetValidatorProvider {

   private static List<IAtsXWidgetValidator> atsValidators;

   static {
      atsValidators = new ArrayList<>();
      atsValidators.add(new AtsXNumberValidator());
      atsValidators.add(new AtsXTextValidator());
      atsValidators.add(new AtsXDateValidator());
      atsValidators.add(new AtsXComboValidator());
      atsValidators.add(new AtsXComboBooleanValidator());
      atsValidators.add(new AtsXListValidator());
   }

   @Override
   public synchronized Collection<IAtsXWidgetValidator> getValidators() {
      return atsValidators;
   }

}
