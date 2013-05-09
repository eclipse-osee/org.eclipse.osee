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
package org.eclipse.osee.ats.ev;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.client.ev.SearchWorkPackageOperation;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Donald G. Dunne
 */
public class SearchWorkPackageBlam extends AbstractBlam {

   private static final String RETURN_ACTIVE_WORK_PACKAGES = "Return Active Work Packages";
   private static final String ACTIONABLE_ITEMS = "Actionable Items(s)";
   private static final String INCLUDE_CHILD_TEAM_DEFS = "Include Child Team Definitions";
   private static final String INCLUDE_CHILD_AIS = "Include Child Actionable Items";
   private static final String TEAM_DEFINITIONS = "Team Definitions(s)";
   private static final String RUN_EV_REPORT_ON_RESULTS = "Run EV Report on Results";

   @Override
   public String getName() {
      return "Search Work Packages by Team Def or AI";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      String returnActiveSel = variableMap.getString(RETURN_ACTIVE_WORK_PACKAGES);
      if (!Strings.isValid(returnActiveSel) || returnActiveSel.equals("--select--")) {
         AWorkbench.popup(String.format("Must select [%s] option.", RETURN_ACTIVE_WORK_PACKAGES));
      } else {
         Active returnActiveWorkPkgs = Active.valueOf(returnActiveSel);
         boolean includeChildrenTeamDefs = variableMap.getBoolean(INCLUDE_CHILD_TEAM_DEFS);
         boolean includeChildrenAis = variableMap.getBoolean(INCLUDE_CHILD_AIS);
         final boolean runEvReportOnResults = variableMap.getBoolean(RUN_EV_REPORT_ON_RESULTS);
         Collection<IAtsTeamDefinition> teamDefs =
            variableMap.getCollection(IAtsTeamDefinition.class, TEAM_DEFINITIONS);
         Collection<IAtsActionableItem> ais = variableMap.getCollection(IAtsActionableItem.class, ACTIONABLE_ITEMS);

         final SearchWorkPackageOperation operation =
            new SearchWorkPackageOperation(getName(), teamDefs, includeChildrenTeamDefs, ais, includeChildrenAis,
               returnActiveWorkPkgs);
         Operations.executeAsJob(operation, false, Job.LONG, new JobChangeAdapter() {

            @Override
            public void done(IJobChangeEvent event) {
               super.done(event);
               Set<Artifact> results = operation.getResultArtifacts();
               if (results.isEmpty()) {
                  AWorkbench.popup(getName() + " - No Results Returned");
               } else {
                  MassArtifactEditor.editArtifacts(getName(), results);
                  if (runEvReportOnResults) {
                     EarnedValueWorkPacakgeReportBlam.runReport("Earned Value Work Package Report", results);
                  }
               }
            }

         });
      }
   }

   @Override
   public String getXWidgetsXml() {
      // @formatter:off
      return "<xWidgets>" +
      "<XWidget displayName=\"" + TEAM_DEFINITIONS + "\" beginComposite=\"2\" xwidgetType=\"XHyperlabelTeamDefinitionSelection\" horizontalLabel=\"true\"/>" +
      "<XWidget displayName=\"" + INCLUDE_CHILD_TEAM_DEFS + "\" xwidgetType=\"XCheckBox\" />" +
      "<XWidget displayName=\"" + ACTIONABLE_ITEMS + "\" xwidgetType=\"XHyperlabelActionableItemSelection\" horizontalLabel=\"true\"/>" +
      "<XWidget displayName=\"" + INCLUDE_CHILD_AIS + "\" xwidgetType=\"XCheckBox\" />" +
      "<XWidget displayName=\"" + RETURN_ACTIVE_WORK_PACKAGES + "\" xwidgetType=\"XCombo(Active,InActive,Both)\" defaultValue=\"Active\"/>" +
      "<XWidget displayName=\"" + RUN_EV_REPORT_ON_RESULTS + "\" xwidgetType=\"XCheckBox\" />" +
      "</xWidgets>";
      // @formatter:on
   }

   @Override
   public String getDescriptionUsage() {
      return "Given Team Definitions, AIs or both, load all configured Work Packages and open in Mass Editor.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS/Util");
   }

}