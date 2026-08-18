/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.navigate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * Finds 1 example workflow for each Work Definition by searching on the Workflow Definition Reference attribute, then
 * opens each in both the Workflow Editor and Artifact Editor and reports results.
 *
 * @author Donald G. Dunne
 */
public class OpenWorkDefExamplesNavigateItem extends XNavigateItemAction {

   private static final String TITLE = "Testing - Open Work Def Examples";

   public OpenWorkDefExamplesNavigateItem(XNavItemCat... xNavItemCat) {
      super(TITLE, PluginUiImage.ADMIN, xNavItemCat);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (!MessageDialog.openConfirm(Displays.getActiveShell(), TITLE,
               "This will find 1 example workflow for each Work Definition and open it in both the " + "Workflow Editor and Artifact Editor.\n\nThis may open many editors.\n\nContinue?")) {
               return;
            }
            XResultData rd = getResults();
            XResultDataUI.reportAndOpen(rd, TITLE, "OpenWorkDefExamples.html");
         }
      });
   }

   public static XResultData getResults() {
      XResultData rd = new XResultData();
      rd.log("Opening 1 example workflow per Work Definition\n");

      Collection<WorkDefinition> workDefs = AtsApiService.get().getWorkDefinitionService().getAllWorkDefinitions();

      List<String> allIds = new ArrayList<>();

      for (WorkDefinition workDef : workDefs) {
         rd.logf("\n=== Work Def: %s (id=%s) ===\n", workDef.getName(), workDef.getId());

         List<Artifact> artifacts;
         try {
            artifacts = ArtifactQuery.getArtifactListFromAttribute(AtsAttributeTypes.WorkflowDefinitionReference,
               String.valueOf(workDef.getId()), AtsApiService.get().getAtsBranch());
         } catch (Exception ex) {
            rd.errorf("   Error querying for work def [%s]: %s\n", workDef.getName(), ex.getMessage());
            continue;
         }

         if (artifacts.isEmpty()) {
            rd.logf("   No workflows found\n");
            continue;
         }

         Artifact artifact = artifacts.iterator().next();
         String artInfo = String.format("[%s] type=[%s] id=[%s]", artifact.getName(), artifact.getArtifactTypeName(),
            artifact.getIdString());

         try {
            WorkflowEditor.editArtifact((AbstractWorkflowArtifact) artifact);
            rd.logf("   WFE opened: %s\n", artInfo);
         } catch (Exception ex) {
            rd.errorf("   WFE error: %s - %s\n", artInfo, ex.getMessage());
         }

         try {
            ArtifactEditor.editArtifact(artifact);
            rd.logf("   ArtEd opened: %s\n", artInfo);
         } catch (Exception ex) {
            rd.errorf("   ArtEd error: %s - %s\n", artInfo, ex.getMessage());
         }

         allIds.add(artifact.getIdString());
      }

      rd.logf("\n\n=== All Workflow IDs (comma delimited) ===\n%s\n", String.join(",", allIds));
      return rd;
   }

   @Override
   public String getDescription() {
      return "Find 1 example workflow for each Work Definition and open in both the Workflow Editor and Artifact Editor with a results report.";
   }
}
