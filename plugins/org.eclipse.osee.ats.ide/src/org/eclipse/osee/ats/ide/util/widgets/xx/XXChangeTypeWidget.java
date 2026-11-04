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

package org.eclipse.osee.ats.ide.util.widgets.xx;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.ChangeType;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.result.XResultType;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXChangeTypeWidget extends XXStringsWidget {

   public static final WidgetId ID = WidgetIdAts.XXChangeTypeWidget;

   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;
   private IAtsTeamDefinition teamDef;
   private boolean teamDefMustBeSelected = false;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXChangeTypeWidget() {
      super(ID, "Change Type");
      setAttributeType(ChangeType);
      setAsProviderFor(AtsAttributeTypes.ChangeType);
      setOseeImage(CoreImage.PROBLEM);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public Collection<String> getSelectable() {
      if (xxWid.isWorkItem()) {
         IAtsWorkItem workItem = xxWid.getWorkItem();
         List<ChangeTypes> changeTypes = AtsApiService.get().getWorkItemService().getChangeTypeOptions(workItem);
         return Collections.toStrings(changeTypes);
      }
      if (teamDef != null) {
         List<ChangeTypes> changeTypes = AtsApiService.get().getWorkItemService().getChangeTypeOptions(teamDef);
         return Collections.toStrings(changeTypes);
      }
      return ChangeTypes.getDefaultValuesStrs();
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected XResultData validateHandleSelection() {
      // Ok if widget has TeamWf cause versions will come from TeamWf's Team Definition
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
