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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
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
public class XXAgileFeatureSelWidget extends XXAbstractBaseXWidget<IAgileFeatureGroup> {

   public static final WidgetId ID = WidgetIdAts.XXAgileFeatureSelWidget;

   private XXWorkItemData xxWid = XXWorkItemData.SENTINEL;
   protected IAtsTeamWorkflow teamWf;
   protected IAtsTeamDefinition teamDef;
   AtsApi atsApi;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXAgileFeatureSelWidget() {
      super(ID, "Feature");
      atsApi = AtsApiService.get();
      setRelationTypeSide(AtsRelationTypes.AgileFeatureToItem_AgileFeatureGroup);
      set(XOption.WRITE, XOption.VAL_MULTI_SEL, XOption.CLEAR);
      setOseeImage(AtsImage.AGILE_FEATURE);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public IAgileFeatureGroup getSentinel() {
      return null;
   }

   @Override
   public Collection<IAgileFeatureGroup> getSelectable() {
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
      List<IAgileFeatureGroup> activeFeatures = new LinkedList<>();
      for (IAgileFeatureGroup agileFeature : atsApi.getAgileService().getAgileFeatureGroups(agileTeam)) {
         if (agileFeature.isActive()) {
            activeFeatures.add(agileFeature);
         }
      }
      return activeFeatures;
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof IAgileFeatureGroup;
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
         Collection<ArtifactToken> relFeatures =
            atsApi.getRelationResolver().getRelated(teamWf, AtsRelationTypes.AgileFeatureToItem_AgileFeatureGroup);
         if (!relFeatures.isEmpty()) {
            List<IAgileFeatureGroup> features = new ArrayList<>();
            for (ArtifactToken featureTok : relFeatures) {
               IAgileFeatureGroup feature = atsApi.getAgileService().getAgileFeatureGroup(featureTok);
               features.add(feature);
            }
            setSelected(features, false);
         }
      }
   }

}
