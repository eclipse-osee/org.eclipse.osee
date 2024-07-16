/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.actions.wizard;

import java.util.Collection;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.swt.widgets.Composite;

/**
 * Allows the New Action wizard to be extended with custom widgets when the appropriate Actionable Items are selected
 *
 * @author Donald G. Dunne
 */
public interface IAtsWizardItem {

   /**
    * Add the desired XWidget declarations to the stringbuffer. Selected AIAs are provided so validation can be done to
    * determine what (if any) widgets should be added. eg. <XWidget displayName=\"Description\" height=\"80\" required=\
    * "true\" xwidgetType=\"XText\" fill=\"Vertically\" \"/>");
    */
   default void getWizardXWidgetExtensions(Collection<IAtsActionableItem> aias, StringBuffer stringBuffer) {
      // do nothing
   }

   /**
    * @return true if widgets will be added based on selected aias
    */
   default boolean hasWizardXWidgetExtensions(Collection<IAtsActionableItem> aias) {
      return false;
   }

   /**
    * Determine if Action is valid to create based on wizard data entered. hasWizardXWidgetExtenstions will be called to
    * determine if this method should be called.
    *
    * @return result of validation. if true, action will be created; if not, error will popup and action will not be
    * created
    */
   default Result isActionValidToCreate(Collection<IAtsActionableItem> aias) {
      return Result.TrueResult;
   }

   /**
    * Callback with created action upon completion and creation of the action and it's workflows.
    * hasWizardXWidgetExtenstions will be called to determine if this method should be called.
    */
   void wizardCompleted(ActionResult actionResult, IAtsChangeSet changes);

   /**
    * Validation that the data entered is valid and the wizard can be finished. This will be called after every
    * character is entered, so extensive processing should not be performed during this check. Extensive processing can
    * be performed during isActionValidToCreate(). hasWizardXWidgetExtenstions will be called to determine if this
    * method should be called.
    *
    * @return true if widget data entered is valid
    */
   default Result isWizardXWidgetsComplete() {
      return Result.TrueResult;
   }

   /**
    * @return Name of the product or team that uses these fields. This will display in Page3 of the wizard to separate
    * out different fields for different teams.
    */
   String getName();

   /**
    * Add the desired XWidget declarations directly to the composite. These will be displayed after the other xml
    * xwidget extensions.
    */
   public default void getWizardXWidgetExtensions(Collection<IAtsActionableItem> selectedIAtsActionableItems, Composite comp) {
      // do nothing
   }

}
