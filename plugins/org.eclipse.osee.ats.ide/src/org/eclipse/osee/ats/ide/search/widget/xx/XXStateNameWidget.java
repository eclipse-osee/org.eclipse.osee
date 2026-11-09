/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.search.widget.xx;

import java.util.Collection;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.result.XResultType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsWidget;
import org.osgi.service.component.annotations.Component;

/**
 * Selection widget used by Action Search. Does not persist.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXStateNameWidget extends XXStringsWidget {

   public static final WidgetId ID = WidgetIdAts.XXStateNameWidget;

   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;
   private IAtsTeamDefinition teamDef;
   private boolean teamDefMustBeSelected = false;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXStateNameWidget() {
      super(ID, "State Name(s)");
      setOseeImage(AtsImage.STATE);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public Collection<String> getSelectable() {
      if (teamDef != null) {
         WorkDefinition workDef =
            AtsApiService.get().getWorkDefinitionService().computeWorkDefinitionForTeamWfNotYetCreated(teamDef);
         if (workDef != null) {
            return AtsApiService.get().getWorkDefinitionService().getStateNames(workDef);
         }
      }
      return AtsApiService.get().getWorkDefinitionService().getStateNames();
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected XResultData validateHandleSelection() {
      // Ok if widget has TeamWf cause state names will come from TeamWf's Team Definition
      if (xxWid.isWorkItem()) {
         return XResultData.OK_STATUS;
      }
      // Else, error cause teamDef needs to be selected/set before selecting this widget
      else if (teamDefMustBeSelected && (teamDef == null && getTeamId().isInvalid())) {
         return new XResultData(XResultType.Error, "Team Definition(s) must be selected first for [%s]", getLabel());
      }
      // If selectable was set in another way, then don't need teamDef
      else if (!getSelectable().isEmpty()) {
         return XResultData.OK_STATUS;
      }
      return XResultData.OK_STATUS;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public void setTeamDef(IAtsTeamDefinition teamDef) {
      this.teamDef = teamDef;
   }

   public boolean isTeamDefMustBeSelected() {
      return teamDefMustBeSelected;
   }

   public void setTeamDefMustBeSelected(boolean teamDefMustBeSelected) {
      this.teamDefMustBeSelected = teamDefMustBeSelected;
   }

}
