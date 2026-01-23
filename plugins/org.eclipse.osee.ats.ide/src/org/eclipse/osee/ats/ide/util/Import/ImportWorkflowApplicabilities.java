/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.ide.util.Import;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.BranchEntryEntryDialog;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;

/**
 * @author Donald G. Dunne
 */
public class ImportWorkflowApplicabilities extends XNavigateItemAction {

   private static final String FINAL_IMPORT_CHANGES = "Final Import Changes:";
   private BranchToken branch;
   private AtsApi atsApi;
   private XResultData rd;
   private StringBuffer applics;
   private boolean persist = false;
   private Map<String, ApplicabilityToken> applicNameToTok;

   public ImportWorkflowApplicabilities() {
      super("Import Workflow Applicabilities from Branch", FrameworkImage.IMPORT, AtsNavigateViewItems.ATS_IMPORT);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      atsApi = AtsApiService.get();
      BranchEntryEntryDialog dialog = new BranchEntryEntryDialog(getName(), getDescription(),
         "Multi-lined: <art id>,<applic name>", "Commit Comment");
      dialog.setFillVertically(true);
      dialog.setFillVertically2(false);
      dialog.setEntry2(getName());
      dialog.addCheckbox("Persist");
      if (dialog.open() == Window.OK) {
         persist = dialog.isChecked();
         rd = new XResultData();
         rd.log(getName());
         rd.log("\n<b>Search for \"" + FINAL_IMPORT_CHANGES + "\" to see final changes/results.</b>\n");
         importApplic(dialog, rd);
         rd.addRaw("\n\n" + applics.toString());
         XResultDataUI.report(rd, getName());
      }
   }

   private void importApplic(BranchEntryEntryDialog dialog, XResultData rd) {
      branch = dialog.getBranch();
      if (branch == null) {
         rd.error("Branch must be selected");
         return;
      }
      if (rd.isErrors()) {
         return;
      }
      rd.logf("Branch: %s\n", (branch == null ? "Not Selected" : branch.toStringWithId()));
      getApplicabilitesFromBranch();
      rd.logf("Commit Comment: [%s]\n", dialog.getEntry2());
      HashCollection<ApplicabilityToken, ArtifactId> applicToTeamWfArt = new HashCollection<>();
      ApplicabilityEndpoint commonBranchApplicEp =
         ServiceUtil.getOseeClient().getApplicabilityEndpoint(atsApi.getAtsBranch());

      if (Strings.isValid(dialog.getEntry())) {
         for (String line : dialog.getEntry().split("[\n\r]+")) {
            String[] split = line.split(",");
            if (split.length != 2) {
               rd.errorf("Invalid line: [%s]\n", line);
            } else {
               String artId = split[0];
               String importApplicName = split[1];
               rd.logf("\nimport line: [%s]\n", line);
               rd.logf("--- artId: [%s]\n", artId);
               rd.logf("--- importApplicName: [%s]\n", importApplicName);
               if (Strings.isNotNumeric(artId)) {
                  rd.errorf("Applic Id [%s] isn't numeric", artId);
               } else {
                  IAtsTeamWorkflow teamWf = atsApi.getQueryService().getTeamWf(Long.valueOf(artId));
                  if (teamWf == null) {
                     rd.errorf("Art Id [%s] isn't team wf", artId);
                  } else {
                     rd.logf("--- teamWf %s\n", teamWf.toStringWithId());
                     ApplicabilityToken currApplic = commonBranchApplicEp.getApplicabilityToken(teamWf.getArtifactId());
                     rd.logf("--- curr applic [%s]\n", currApplic);
                     if (importApplicName.equals(currApplic.getName())) {
                        rd.logf("--- NO CHANGE\n");
                     } else {
                        ApplicabilityToken importApplicTok = applicNameToTok.get(importApplicName);
                        if (importApplicTok == null) {
                           rd.errorf("Invalid Import Applic Name [%s], No Applic Found on selected Branch\n",
                              importApplicName);
                        } else {
                           rd.warningf("--- Curr [%s]; Set New Applic [%s] (on persist)\n", currApplic,
                              importApplicName);
                           applicToTeamWfArt.put(importApplicTok, teamWf.getArtifactId());
                        }
                     }
                  }
               }
            }
         }
         Set<Entry<ApplicabilityToken, List<ArtifactId>>> entrySet = applicToTeamWfArt.entrySet();
         rd.logf("\n\n");
         rd.logf(FINAL_IMPORT_CHANGES);
         rd.logf("\n");
         if (applicToTeamWfArt.isEmpty()) {
            rd.log("No Changes To Make");
         } else {
            for (Entry<ApplicabilityToken, List<ArtifactId>> entry : entrySet) {
               rd.logf("Set Applic [%s] to teamWfs %s (on persit)\n", entry.getKey(), entry.getValue());
               if (persist) {
                  if (rd.isErrors()) {
                     rd.errorf("Import Aborted Due To Errors\n");
                  } else {
                     TransactionToken tx = commonBranchApplicEp.setApplicability(entry.getKey(), entry.getValue());
                     if (tx.isValid()) {
                        rd.logf("---Transaction %s\n", tx.toString());
                     } else {
                        rd.errorf("--- Set did not succeed\n");
                     }
                  }
               } else {
                  rd.log("Report Only - No Persist");
               }
            }
         }
      } else {
         rd.errorf("Must enter valid lines\n");
      }
   }

   private void getApplicabilitesFromBranch() {
      ApplicabilityEndpoint applicEp = ServiceUtil.getOseeClient().getApplicabilityEndpoint(branch);
      Collection<ApplicabilityToken> applicToks = applicEp.getApplicabilityTokens();
      applicNameToTok = new HashMap<>(100);
      applics = new StringBuffer("Applicabilities found on selected branch: \n");
      for (ApplicabilityToken appTok : applicToks) {
         applics.append(String.format("--- Applic: %s\n", appTok.toStringWithId()));
         applicNameToTok.put(appTok.getName(), appTok);
      }
   }

   @Override
   public String getDescription() {
      return "This will retrieve the valid Applicability Tokens from the selected branch and then set the" //
         + " appropriate applicability id from that branch as the applicability id for the TeamWf." //
         + " \n\nThis is uncommon! \n\nExample Case: A Team Workflow, like a Problem Report, needs to be marked as" //
         + " one of the applicabilies from the Product Line branch to enable a Build Memo by Config / Applicability" //
         + " to be generated or to see what Problems exist for a current Config or Applicability.";
   }

}
