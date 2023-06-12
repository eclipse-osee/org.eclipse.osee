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

package org.eclipse.osee.ats.core.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidator;
import org.eclipse.osee.ats.api.workflow.transition.IAtsXWidgetValidatorProvider;

/**
 * @author Donald G. Dunne
 */
public class AtsXWidgetValidatorProviderCore implements IAtsXWidgetValidatorProvider {

   private static List<IAtsXWidgetValidator> atsValidators;

   @Override
   public Collection<IAtsXWidgetValidator> getValidators() {
      if (atsValidators == null) {
         atsValidators = new ArrayList<>();
         atsValidators.add(new AtsXNumberValidator());
         atsValidators.add(new AtsXTextValidator());
         atsValidators.add(new AtsXDateValidator());
         atsValidators.add(new AtsXComboValidator());
         atsValidators.add(new AtsXComboBooleanValidator());
         atsValidators.add(new AtsXListValidator());
         atsValidators.add(new AtsXWidgetAttrValidator());
         atsValidators.add(new AtsXPointsAttrValidator());
         atsValidators.add(new AtsXHyperlinkWfdForEnumAttrValidator());
         atsValidators.add(new AtsXHyperlinkLabelDateValidator());
         atsValidators.add(new AtsXHyperlinkLabelValueSelectionValidator());
         atsValidators.add(new AtsXCheckBoxThreeStateValidator());
         atsValidators.add(new AtsXHyperlinkTriStateBooleanValidator());
         atsValidators.add(new AtsWalktrhoughAttrValidator());
         atsValidators.add(new AtsXHyperlinkWithFilteredDialogValidator());
         atsValidators.add(new AtsXHyperlinkMemberSelValidator());
         atsValidators.add(new AtsXUserRoleValidator());
         atsValidators.add(new AtsXCommitManagerValidator());
         atsValidators.add(new AtsOperationalImpactValidator());
         atsValidators.add(new AtsOperationalImpactWithWorkaroundValidator());

      }
      return atsValidators;
   }

}
