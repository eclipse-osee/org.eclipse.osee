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
import org.eclipse.osee.framework.ui.swt.Widgets;

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
   private final IAgileSprint sprint;
   private final boolean forBacklog;

   public SprintReportAction(IAgileSprint sprint) {
      super();
      this.sprint = sprint;
      this.bLog = null;
      this.forBacklog = false;
      setText("Generate Sprint Loading Report");
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.REPORT));
      setToolTipText("Generate Sprint Loading Report for Agile Sprint");
      atsApi = AtsApiService.get();
   }

   public SprintReportAction(IAgileBacklog bLog, boolean forBacklog) {
      super();
      this.bLog = bLog;
      this.forBacklog = forBacklog;
      this.sprint = null;
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

      if (forBacklog) {
         loadItemsFromBacklog();
      } else {
         loadItemsFromSprint();
      }

      processSprints();

      XResultDataUI.report(rd, "Sprint Loading Report");
   }

   private void loadItemsFromSprint() {
      sprintNameToId.put(sprint.getName(), sprint.getId());
      for (IAgileItem item : atsApi.getAgileService().getItems(sprint)) {
         if (!item.isCancelled()) {
            sprintToItem.put(sprint.getId(), item);
            idToSprint.put(sprint.getId(), sprint);
         }
      }

      rd = new XResultData();
      rd.logf("Sprint Loading Report for %s\n", sprint.getName());

   }

   private void loadItemsFromBacklog() {
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

      for (IAgileItem item : atsApi.getAgileService().getItems(useBLog)) {
         if (!item.isCancelled()) {
            IAgileSprint sprint = atsApi.getAgileService().getSprint(item);
            if (sprint != null) {
               sprintToItem.put(sprint.getId(), item);
               idToSprint.put(sprint.getId(), sprint);
               sprintNameToId.put(sprint.getName(), sprint.getId());
            }
         }
      }

      rd = new XResultData();
      rd.logf("Sprint Loading Report for Backlog %s\n", useBLog.getName());
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

         double sprintPoints = 0;
         Map<String, Double> userNameToPoints = new HashMap<>();
         Set<String> names = new HashSet<String>();

         Map<String, Double> cat3ToPoints = new HashMap<>();
         Set<String> cat3s = new HashSet<String>();

         for (IAgileItem sprintItem : sprintItems) {
            String itemPtsStr = atsApi.getAgileService().getPointsStr(sprintItem);
            double itemPts = 0;
            if (Strings.isNumeric(itemPtsStr)) {
               itemPts = Double.valueOf(itemPtsStr);
               sprintPoints += itemPts;
            }

            if (itemPts > 0) {

               // Add points per assignee
               List<AtsUser> assignees = null;
               if (forBacklog) {
                  assignees = sprintItem.getAssignees();
               } else {
                  assignees = atsApi.getImplementerService().getImplementers(sprintItem);
               }
               if (assignees.size() > 0) {
                  Double pointsByAssignee = itemPts / assignees.size();

                  for (AtsUser user : assignees) {
                     Double pts = userNameToPoints.get(user.getName());
                     if (pts == null) {
                        pts = 0.0;
                     }
                     pts = pts + pointsByAssignee;
                     userNameToPoints.put(user.getName(), pts);
                     names.add(user.getName());
                  }
               }

               // Add points per Category3
               String cat3 = atsApi.getAttributeResolver().getSoleAttributeValue(sprintItem,
                  AtsAttributeTypes.Category3, Widgets.NOT_SET);
               String[] split = cat3.split("/");
               for (String splitCat : split) {
                  Double pts = cat3ToPoints.get(splitCat);
                  if (pts == null) {
                     pts = 0.0;
                  }
                  Double ptsPerCat = itemPts / split.length;
                  pts = pts + ptsPerCat;
                  cat3ToPoints.put(splitCat, pts);
                  cat3s.add(splitCat);
               }

            }
         }

         Double totalPts = 0.0;

         rd.logf("Num Points: %s\n\n", sprintPoints);

         // Show points per assignee
         List<String> namesSorted = new ArrayList<String>();
         namesSorted.addAll(names);
         java.util.Collections.sort(namesSorted);
         for (String name : namesSorted) {
            Double pts = userNameToPoints.get(name);
            totalPts += pts;
            if (pts > 0) {
               rd.logf("Pts: %s - %s\n", name, pts);
            }
         }

         rd.logf("\n\nPoints by Category\n\n");

         // Show points per cat3
         List<String> cat3Sorted = new ArrayList<String>();
         cat3Sorted.addAll(cat3s);
         java.util.Collections.sort(cat3Sorted);
         for (String cat3 : cat3Sorted) {
            Double pts = cat3ToPoints.get(cat3);
            if (pts > 0) {
               Double percent = 0.0;
               if (pts > 0.0) {
                  percent = pts / totalPts;
               }
               rd.logf("Cat3: %s - %s  -   Percent: %.2f\n", cat3, pts, percent);
            }
         }
      }
   }

}
