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

package org.eclipse.osee.ats.ide.agile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.column.BackgroundLoadingPreComputedColumnUI;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.world.IAtsWorldArtifactEventColumn;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.EventTopicArtifactTransfer;
import org.eclipse.osee.framework.skynet.core.utility.Branches;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class ApplicabilityColumnUI extends BackgroundLoadingPreComputedColumnUI implements IAtsWorldArtifactEventColumn {

   public static ApplicabilityColumnUI instance = new ApplicabilityColumnUI();
   private final Map<Long, String> idToApplic = new HashMap<>();

   public static ApplicabilityColumnUI getInstance() {
      return instance;
   }

   private ApplicabilityColumnUI() {
      super(AtsColumnTokensDefault.ApplicabilityColumn);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ApplicabilityColumnUI copy() {
      ApplicabilityColumnUI newXCol = new ApplicabilityColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getValue(IAtsWorkItem workItem, Map<Long, String> idToValueMap) {
      String value = "";
      if (Strings.isInValid(value)) {
         if (workItem.isTeamWorkflow()) {
            String cachedVal = idToApplic.get(workItem.getId());
            if (Strings.isValid(cachedVal)) {
               value = cachedVal;
            } else {
               IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
               ApplicabilityToken applicTok = AtsApiService.get().getStoreService().getApplicabilityToken(teamWf);
               if (applicTok.isValid()) {
                  value = applicTok.getName();
               } else {
                  value = " ";
               }
            }
            idToApplic.put(workItem.getId(), value);
         }
      }
      return value;
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      try {
         if (treeItem.getData() instanceof AbstractWorkflowArtifact) {
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) treeItem.getData();
            boolean modified = promptChangeApplicability(Arrays.asList(awa));
            if (modified) {
               awa.persist("Persist via alt-left-click");
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public static boolean promptChangeApplicability(Collection<AbstractWorkflowArtifact> awas) {
      try {
         AbstractWorkflowArtifact firstAwa = awas.iterator().next();
         if (firstAwa.isTeamWorkflow()) {
            // We get applicabilities from related configured branch, eg: PLE branch config
            List<ApplicabilityToken> applicabilityTokens =
               getSelectableApplicabilityTokens((IAtsTeamWorkflow) firstAwa);

            FilteredTreeDialog dialog =
               new FilteredTreeDialog("Select Applicability Impacted", "Select Applicability Impacted",
                  new ArrayTreeContentProvider(), new StringLabelProvider(), new StringNameComparator());
            dialog.setInput(applicabilityTokens);

            if (dialog.open() == Window.OK) {
               ApplicabilityToken selected = dialog.getSelectedFirst();
               BranchToken branch = CoreBranches.COMMON;
               // Set applicability value on this workflow on Common
               ApplicabilityEndpoint applicEp = AtsApiService.get().getOseeClient().getApplicabilityEndpoint(branch);
               List<ArtifactId> wfIds = new ArrayList<>();
               for (AbstractWorkflowArtifact awa : awas) {
                  wfIds.add(awa.getArtifactId());
               }
               TransactionToken tx = applicEp.setApplicability(selected, wfIds);
               if (tx.isValid()) {
                  for (AbstractWorkflowArtifact awa : awas) {
                     awa.reloadAttributesAndRelations();
                  }
               }

               return true;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   private static List<ApplicabilityToken> getSelectableApplicabilityTokens(IAtsTeamWorkflow teamWf) {
      List<ApplicabilityToken> tokens = new ArrayList<>();
      IAtsProgram program = AtsApiService.get().getProgramService().getProgram(teamWf);
      if (program != null) {
         BranchId branch = AtsApiService.get().getProgramService().getProductLineBranch(program);
         if (Branches.isValid(branch)) {
            ApplicabilityEndpoint applicEndpoint = AtsApiService.get().getOseeClient().getApplicabilityEndpoint(branch);
            tokens.addAll(applicEndpoint.getApplicabilityTokens());
            return tokens;
         }
      }
      return java.util.Collections.emptyList();
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      try {
         Set<AbstractWorkflowArtifact> awas = new HashSet<>();
         List<Artifact> arts = new ArrayList<>();
         for (TreeItem item : treeItems) {
            Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(item);
            if (art != null && art.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
               awas.add((AbstractWorkflowArtifact) art);
               arts.add(art);
            }
         }

         promptChangeApplicability(awas);

         return;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /**
    * Don't want columns to listen to their own events, so have WorldXViewerEventManager call here to tell columns to
    * handle
    */
   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, WorldXViewer xViewer) {
      if (!Widgets.isAccessible(xViewer.getTree())) {
         return;
      }
      for (EventBasicGuidArtifact guidArt : artifactEvent.get(EventModType.Reloaded)) {
         Artifact workflow = ArtifactCache.getActive(guidArt);
         if (workflow != null && workflow.isOfType(AtsArtifactTypes.TeamWorkflow)) {
            IAtsWorkItem workItem = AtsApiService.get().getWorkItemService().getWorkItem(workflow);
            String newValue = getValue(workItem, preComputedValueMap);
            preComputedValueMap.put(workflow.getId(), newValue);
            idToApplic.put(workItem.getId(), newValue);
            xViewer.update(workflow, null);
         }
      }
   }

   /**
    * Don't want columns to listen to their own events, so have WorldXViewerEventManager call here to tell columns to
    * handle
    */
   @Override
   public void handleArtifactTopicEvent(ArtifactTopicEvent artifactTopicEvent, WorldXViewer xViewer) {
      if (!Widgets.isAccessible(xViewer.getTree())) {
         return;
      }
      for (EventTopicArtifactTransfer topicArt : artifactTopicEvent.getTransfer(EventModType.Reloaded)) {
         Artifact workflow = ArtifactCache.getActive(topicArt.getArtifactToken());
         if (workflow != null && workflow.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
            IAtsWorkItem workItem = AtsApiService.get().getWorkItemService().getWorkItem(workflow);
            String newValue = getValue(workItem, preComputedValueMap);
            preComputedValueMap.put(workflow.getId(), newValue);
            xViewer.update(workflow, null);
         }
      }
   }

}
