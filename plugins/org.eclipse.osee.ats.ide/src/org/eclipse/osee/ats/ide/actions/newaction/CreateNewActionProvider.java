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

package org.eclipse.osee.ats.ide.actions.newaction;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Allows the New Action BLAM to be extended with custom widgets when the appropriate Actionable Items are selected
 *
 * @author Donald G. Dunne
 */
public interface CreateNewActionProvider {

   /**
    * @return true if widgets will be added based on selected aias
    */
   default boolean hasProviderXWidgetExtensions(Collection<IAtsActionableItem> aias) {
      return false;
   }

   /**
    * Determine if Action is valid to create based on blam data entered. hasProviderXWidgetExtenstions will be called to
    * determine if this method should be called.
    *
    * @return result of validation. if true, action will be created; if not, error will popup and action will not be
    * created
    */
   default Result isActionValidToCreate(Collection<IAtsActionableItem> aias) {
      return Result.TrueResult;
   }

   /**
    * Validation that the data entered is valid and the create can be finished. This will be called after every
    * character is entered, so extensive processing should not be performed during this check. Extensive processing can
    * be performed during isActionValidToCreate(). hasProviderXWidgetExtenstions will be called to determine if this
    * method should be called.
    *
    * @return true if widget data entered is valid
    */
   default Result isCreateActionXWidgetsComplete() {
      return Result.TrueResult;
   }

   /**
    * @return Name of the product or team that uses these fields. This will display after default fields to separate out
    * different fields for different teams.
    */
   String getName();

   public default void getAdditionalXWidgetItems(XWidgetBuilder wb, IAtsTeamDefinition teamDef) {
      // do nothing
   }

   public default void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art,
      SwtXWidgetRenderer swtXWidgetRenderer , XModifiedListener xModListener, boolean isEditable) {
      // do nothing
   }

   public default void handlePopulateWithDebugInfo(String title) {
      // do nothing
   }

   public default Collection<IAtsActionableItem> getDisabledAis() {
      return Collections.emptyList();
   }

   public default void teamCreating(NewActionData data, Collection<XWidget> teamXWidgets) {
      // do nothing
   }

}
