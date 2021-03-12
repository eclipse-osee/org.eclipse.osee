/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.agile.JaxAgileFeatureGroup;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.agile.AgileFeatureGroupColumn;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;

/**
 * @author Donald G. Dunne
 */
public class XAgileFeatureHyperlinkWidget extends XHyperlinkLabelCmdValueSelection {

   Collection<IAgileFeatureGroup> features = new HashSet<>();
   IAtsTeamWorkflow teamWf;
   public static final String WIDGET_ID = XAgileFeatureHyperlinkWidget.class.getSimpleName();
   AtsApi atsApi;

   public XAgileFeatureHyperlinkWidget() {
      super("Agile Feature", true, 50);
      atsApi = AtsApiService.get();
   }

   @Override
   public String getCurrentValue() {
      return Collections.toString(", ", features);
   }

   @Override
   public boolean handleSelection() {
      IAgileTeam agileTeam = atsApi.getAgileService().getAgileTeam(teamWf);
      AgileEndpointApi agileEp = atsApi.getServerEndpoints().getAgileEndpoint();

      FilteredCheckboxTreeDialog<JaxAgileFeatureGroup> dialog = AgileFeatureGroupColumn.openSelectionDialog(agileEp,
         agileTeam.getId(), java.util.Collections.singleton((AbstractWorkflowArtifact) teamWf));

      if (dialog != null) {
         for (JaxAgileFeatureGroup grp : dialog.getChecked()) {
            for (IAgileFeatureGroup feature : atsApi.getAgileService().getAgileFeatureGroups(agileTeam)) {
               if (grp.getId().equals(feature.getId())) {
                  features.add(feature);
               }
            }
         }
      }
      return dialog != null;
   }

   @Override
   public boolean handleClear() {
      features.clear();
      return true;
   }

   public void setTeamWf(IAtsTeamWorkflow teamWf) {
      this.teamWf = teamWf;
   }

   public Collection<IAgileFeatureGroup> getFeatures() {
      return features;
   }

   public void setSelected(Collection<IAgileFeatureGroup> currFeatures) {
      features.clear();
      features.addAll(currFeatures);
   }

}
