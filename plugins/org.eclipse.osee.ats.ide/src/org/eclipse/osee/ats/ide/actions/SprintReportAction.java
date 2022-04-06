/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.ide.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class SprintReportAction extends AbstractAtsAction {

   private final IAgileBacklog bLog;
   private final AtsApi atsApi;
   private final HashCollection<Long, IAgileItem> sprintToItem = new HashCollection<Long, IAgileItem>();
   private final HashMap<Long, IAgileSprint> idToSprint = new HashMap<Long, IAgileSprint>();
   private XResultData rd;
   private final Map<String, Long> sprintNameToId = new HashMap<String, Long>();

   public SprintReportAction() {
      this(null);
   }

   public SprintReportAction(IAgileBacklog bLog) {
      super();
      this.bLog = bLog;
      setText("Generate Sprint Loading Report");
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.REPORT));
      setToolTipText("Generate Sprint Loading Report for Agile Team's Backlog");
      atsApi = AtsApiService.get();
   }

   @Override
   public void runWithException() {
      sprintToItem.clear();
      idToSprint.clear();
      sprintNameToId.clear();

      IAgileBacklog useBLog = bLog;
      if (bLog == null) {
         if (MessageDialog.openConfirm(Displays.getActiveShell(), getText(), "No Agile Backlog Specified\n\n" //
            + "NOTE: This report avaiable in Backlog Items Tab without having to choose Agile Team\n\n" //
            + "Select Ok to Choose Backlog to Report or Cancel")) {
            List<Artifact> activeTeams = new LinkedList<>();
            for (Artifact agTeam : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.AgileTeam,
               AtsApiService.get().getAtsBranch())) {
               if (agTeam.getSoleAttributeValue(AtsAttributeTypes.Active, true)) {
                  activeTeams.add(agTeam);
               }
            }
            FilteredTreeArtifactDialog dialog = new FilteredTreeArtifactDialog(getText(), "Select Agile Team",
               activeTeams, new ArtifactTreeContentProvider(), new ArtifactLabelProvider());
            if (dialog.open() == Window.OK) {
               IAgileTeam aTeam = atsApi.getAgileService().getAgileTeam(dialog.getSelectedFirst());
               useBLog = atsApi.getAgileService().getAgileBacklog(aTeam);
            } else {
               return;
            }
         }
      }

      rd = new XResultData();
      rd.logf("Sprint Loading Report for Backlog %s\n", useBLog.getName());

      for (IAgileItem item : atsApi.getAgileService().getItems(useBLog)) {
         IAgileSprint sprint = atsApi.getAgileService().getSprint(item);
         if (sprint != null) {
            sprintToItem.put(sprint.getId(), item);
            idToSprint.put(sprint.getId(), sprint);
            sprintNameToId.put(sprint.getName(), sprint.getId());
         }
      }

      processSprints();

      XResultDataUI.report(rd, "Sprint Loading Report");
   }

   private void processSprints() {

      List<String> sprintNames = new ArrayList<String>();
      sprintNames.addAll(sprintNameToId.keySet());
      Collections.sort(sprintNames);

      for (String sprintName : sprintNames) {

         Long sprintId = sprintNameToId.get(sprintName);
         IAgileSprint sprint = idToSprint.get(sprintId);
         List<IAgileItem> sprintItems = sprintToItem.getValues(sprint.getId());

         rd.logf("\n======================================================\nSprint: " //
            + "%s\n======================================================\n", sprint.toString());
         rd.logf("Num Items: %s\n\n", sprintItems.size());

         Integer sprintPoints = 0;
         CountingMap<String> userNameToPoints = new CountingMap<String>();
         Set<String> names = new HashSet<String>();

         for (IAgileItem sprintItem : sprintItems) {
            String itemPtsStr = atsApi.getAgileService().getPointsStr(sprintItem);
            Integer itemPts = 0;
            if (Strings.isNumeric(itemPtsStr)) {
               itemPts = Integer.valueOf(itemPtsStr);
               sprintPoints += itemPts;
            }

            if (itemPts > 0) {

               List<AtsUser> assignees = sprintItem.getStateMgr().getAssignees();
               if (assignees.size() > 0) {
                  Integer pointsByAssignee = itemPts / assignees.size();

                  for (AtsUser user : assignees) {
                     userNameToPoints.put(user.getName(), pointsByAssignee);
                     names.add(user.getName());
                  }
               }
            }
         }

         rd.logf("Num Points: %s\n\n", sprintPoints);

         List<String> namesSorted = new ArrayList<String>();
         namesSorted.addAll(names);
         java.util.Collections.sort(namesSorted);
         for (String name : namesSorted) {
            Integer pts = userNameToPoints.get(name);
            if (pts > 0) {
               rd.logf("Pts: %s - %s\n", name, pts);
            }
         }

      }

   }

}
