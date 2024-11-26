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

package org.eclipse.osee.ats.ide.agile.jira;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.editor.IWfeEditorContributor;
import org.eclipse.osee.ats.ide.editor.WfeEditorContributors;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class JiraStoryLinkColumnUI extends AbstractJiraSyncColumnUI {

   public static JiraStoryLinkColumnUI instance = new JiraStoryLinkColumnUI();

   public static JiraStoryLinkColumnUI getInstance() {
      return instance;
   }

   private JiraStoryLinkColumnUI() {
      super(AtsColumnTokensDefault.JiraStoryLinkColumn);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public JiraStoryLinkColumnUI copy() {
      JiraStoryLinkColumnUI newXCol = new JiraStoryLinkColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getValue(IAtsWorkItem workItem, Map<Long, String> idToValueMap) {
      if (workItem.isTeamWorkflow()) {
         if (AtsApiService.get().getAttributeResolver().getSoleAttributeValue(workItem, AtsAttributeTypes.Points,
            "").equals(AtsAttributeTypes.Points.Epic.getName())) {
            return "Epic";
         }
         return AtsApiService.get().getJiraService().getJiraStoryLink(workItem);
      }
      return "";
   }

   @Override
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      if (treeItem.getData() instanceof IAtsTeamWorkflow) {
         createStoryLink(Arrays.asList((IAtsTeamWorkflow) treeItem.getData()));
      }
      return super.handleAltLeftClick(treeColumn, treeItem);
   }

   @Override
   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      Set<IAtsTeamWorkflow> teamWfs = new HashSet<>();
      for (TreeItem item : treeItems) {
         if (item.getData() instanceof IAtsTeamWorkflow) {
            IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) item.getData();
            teamWfs.add(teamWf);
         }
      }
      createStoryLink(teamWfs);
      super.handleColumnMultiEdit(treeColumn, treeItems);
   }

   private void createStoryLink(Collection<IAtsTeamWorkflow> teamWfs) {
      Set<IAtsTeamWorkflow> unlinkedTeamWfs = new HashSet<>();
      for (IAtsTeamWorkflow teamWf : teamWfs) {
         String storyId = AtsApiService.get().getJiraService().getJiraStoryLink(teamWf);
         if (Strings.isInvalid(storyId)) {
            unlinkedTeamWfs.add(teamWf);
         }
      }
      if (unlinkedTeamWfs.isEmpty()) {
         AWorkbench.popup("No Team Workflows to Link");
         return;
      }
      XResultData rd = new XResultData();
      for (IWfeEditorContributor contrib : WfeEditorContributors.getContributors()) {
         contrib.createStoryLink(teamWfs, rd);
      }
      XResultDataUI.report(rd, getName());
   }

}
