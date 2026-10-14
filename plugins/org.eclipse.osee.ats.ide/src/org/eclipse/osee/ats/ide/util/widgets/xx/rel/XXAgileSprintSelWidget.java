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

package org.eclipse.osee.ats.ide.util.widgets.xx.rel;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXAgileSprintSelWidget extends XXAbstractBaseXWidget<IAgileSprint> {

   public static final WidgetId ID = WidgetIdAts.XXAgileSprintSelWidget;

   private XXWorkItemData xxWid = XXWorkItemData.SENTINEL;
   AtsApi atsApi;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXAgileSprintSelWidget() {
      super(ID, "Sprint");
      atsApi = AtsApiService.get();
      setRelationTypeSide(AtsRelationTypes.AgileSprintToItem_AgileSprint);
      set(XOption.WRITE, XOption.VAL_SINGLE_SEL, XOption.CLEAR);
      setOseeImage(AtsImage.AGILE_SPRINT);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public IAgileSprint getSentinel() {
      return null;
   }

   @Override
   public Collection<IAgileSprint> getSelectable() {
      IAtsTeamWorkflow teamWf = null;
      IAtsTeamDefinition teamDef = null;
      if (xxWid.isTeamWf()) {
         teamWf = xxWid.getTeamWf();
      } else if (getTeamId().isValid()) {
         teamDef = atsApi.getTeamDefinitionService().getTeamDefinitionById(getTeamId());
      }

      IAgileTeam agileTeam = null;
      if (teamWf != null) {
         agileTeam = atsApi.getAgileService().getAgileTeam(teamWf);
      } else if (teamDef != null) {
         agileTeam = atsApi.getAgileService().getAgileTeam(teamDef);
      } else if (getTeamId().isValid()) {
         IAtsTeamDefinition useTeamDef = atsApi.getTeamDefinitionService().getTeamDefinitionById(getTeamId());
         if (useTeamDef != null) {
            agileTeam = atsApi.getAgileService().getAgileTeam(useTeamDef);
         }
      }
      if (agileTeam == null) {
         AWorkbench.popup("No Agile Team configured for this ATS Team");
         return Collections.emptyList();
      }
      List<IAgileSprint> activeSprints = new LinkedList<>();
      for (IAgileSprint agileSprint : atsApi.getAgileService().getAgileSprints(agileTeam)) {
         if (agileSprint.isActive()) {
            activeSprints.add(agileSprint);
         }
      }
      return activeSprints;
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof IAgileSprint;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

   @Override
   protected void initializeSelected() {
      if (isRelationStorage() && xxWid.isTeamWf()) {
         IAtsTeamWorkflow teamWf = atsApi.getWorkItemService().getTeamWf(getArtifact());
         ArtifactToken relSprint =
            atsApi.getRelationResolver().getRelatedOrSentinel(teamWf, AtsRelationTypes.AgileSprintToItem_AgileSprint);
         if (relSprint.isValid()) {
            IAgileSprint sprint = atsApi.getAgileService().getAgileSprint(relSprint);
            setSelected(sprint, false);
         }
      }
   }

}
