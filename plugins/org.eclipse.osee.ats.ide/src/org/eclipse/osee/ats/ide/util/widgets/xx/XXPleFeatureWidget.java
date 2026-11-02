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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Branches;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.osgi.service.component.annotations.Component;

/**
 * Currently only a selection widget based on a give team definition. Does not persist.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXPleFeatureWidget extends XXAbstractBaseXWidget<FeatureDefinition> {

   public static final WidgetId ID = WidgetIdAts.XXPleFeatureWidget;
   public static final String LABEL = "Feature(s) Impacted";

   private XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXPleFeatureWidget() {
      super(ID, LABEL);
      setAttributeType(AtsAttributeTypes.FeatureImpactReference);
      setAsProviderFor(AtsAttributeTypes.FeatureImpactReference);
      set(XOption.VAL_MULTI_SEL, XOption.CLEAR);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public FeatureDefinition getSentinel() {
      return FeatureDefinition.SENTINEL;
   }

   @Override
   public Collection<FeatureDefinition> getSelectable() {
      return getFeatureDefinitions();
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof FeatureDefinition;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   private List<FeatureDefinition> getFeatureDefinitions() {
      BranchToken branch = getBranch();
      if (Branches.isValid(branch)) {
         ApplicabilityEndpoint applicEndpoint = AtsApiService.get().getOseeClient().getApplicabilityEndpoint(branch);
         return applicEndpoint.getFeatureDefinitionData();
      }
      return Collections.emptyList();
   }

   private BranchToken getBranch() {
      AtsApi atsApi = AtsApiService.get();
      if (!xxWid.isTeamWf()) {
         return BranchToken.SENTINEL;
      }
      IAtsTeamWorkflow teamWf = xxWid.getTeamWf();
      if (teamWf != null) {
         BranchToken branch = atsApi.getBranchService().getWorkingBranch(teamWf);
         if (Branches.isValid(branch)) {
            return branch;
         }
         Collection<BranchToken> branches = atsApi.getBranchService().getBranchesCommittedTo(teamWf);
         if (Branches.isNotEmpty(branches)) {
            return branches.iterator().next();
         }
         IAtsTeamDefinition teamDef = teamWf.getTeamDefinition();
         BranchId branch2 = atsApi.getBranchService().getBranch(teamDef);
         if (Branches.isValid(branch2)) {
            return atsApi.getBranchService().getBranch(branch2);
         }
         IAtsTeamDefinition mainTeamDef = atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(teamDef);
         if (mainTeamDef != null) {
            BranchId branch3 = atsApi.getBranchService().getBranch(mainTeamDef);
            if (Branches.isValid(branch3)) {
               return atsApi.getBranchService().getBranch(branch3);
            }
            for (IAtsVersion ver : atsApi.getVersionService().getVersions(mainTeamDef)) {
               BranchId branch4 = atsApi.getBranchService().getBranch(ver);
               if (Branches.isValid(branch4)) {
                  return atsApi.getBranchService().getBranch(branch4);
               }
            }
         }
      }
      return BranchToken.SENTINEL;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   protected void initializeSelected() {
      List<FeatureDefinition> features = new ArrayList<>();
      if (xxWid.isTeamWf()) {
         IAtsTeamWorkflow teamWf = xxWid.getTeamWf();
         Collection<Object> values =
            AtsApiService.get().getAttributeResolver().getAttributeValues(teamWf, getAttributeType());
         if (!values.isEmpty()) {
            List<FeatureDefinition> featureDefs = getFeatureDefinitions();
            for (Object obj : values) {
               ArtifactId featureId = (ArtifactId) obj;
               for (FeatureDefinition fDef : featureDefs) {
                  if (fDef.getId().equals(featureId.getId())) {
                     features.add(fDef);
                     continue;
                  }
               }
            }
         }
      }
      setSelected(features, false);
   }

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
