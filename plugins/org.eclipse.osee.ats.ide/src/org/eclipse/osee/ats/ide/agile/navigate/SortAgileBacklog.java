/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.ide.agile.navigate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.ide.AtsArtifactImageProvider;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.compare.CompareHandler;
import org.eclipse.osee.framework.ui.skynet.compare.CompareItem;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class SortAgileBacklog extends XNavigateItemAction {

   private AtsApiIde atsApi;
   private final boolean debug = false;

   public SortAgileBacklog(XNavigateItem parent) {
      super(parent, "Sort Agile Backlog", AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_BACKLOG));
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      atsApi = AtsApiService.get();

      List<Artifact> activeTeams = new LinkedList<>();
      for (Artifact agTeam : ArtifactQuery.getArtifactListFromType(AtsArtifactTypes.AgileTeam,
         AtsApiService.get().getAtsBranch())) {
         if (agTeam.getSoleAttributeValue(AtsAttributeTypes.Active, true)) {
            activeTeams.add(agTeam);
         }
      }
      FilteredTreeArtifactDialog dialog = new FilteredTreeArtifactDialog(getName(), "Select Agile Team", activeTeams,
         new ArtifactTreeContentProvider(), new ArtifactLabelProvider());
      if (dialog.open() == 0) {
         Artifact agileTeamArt = dialog.getSelectedFirst();
         Artifact backlog = agileTeamArt.getRelatedArtifactOrNull(AtsRelationTypes.AgileTeamToBacklog_Backlog);
         if (MessageDialog.openConfirm(Displays.getActiveShell(), "Sort Agile Team",
            String.format("Sort Agile Team Backlog\n\n%s\n\nAre you sure?", backlog.toStringWithId()))) {

            List<IAgileItem> aItems = sort(backlog);

            List<Artifact> arts = new ArrayList<>();
            for (IAgileItem aItem : aItems) {
               arts.add((Artifact) atsApi.getQueryService().getArtifact(aItem.getArtifactId()));
            }
            backlog.setRelationOrder(AtsRelationTypes.Goal_Member, arts);
            backlog.persist("Set Backlog Order");
         }
      }
   }

   private List<IAgileItem> sort(Artifact backlog) {
      IAgileBacklog bLog = atsApi.getAgileService().getAgileBacklog(backlog);
      Collection<IAgileItem> items = atsApi.getAgileService().getItems(bLog);
      List<IAgileItem> sItems = new ArrayList<>();
      sItems.addAll(items);
      String unOrdered = print("Un-Ordered Backlog", sItems);
      Collections.sort(sItems, new NoSprintCompCancelledComparator() //
         .thenComparing(new NoSprintCompCancelledComparator() //
            .thenComparing(new SprintComparator()) //
            .thenComparing(new CompCancelledComparator()) //
         ));
      String ordered = print("Ordered Backlog", sItems);

      if (debug) {
         CompareHandler compareHandler =
            new CompareHandler(null, new CompareItem("Un-Ordered", unOrdered, System.currentTimeMillis(), null),
               new CompareItem("Ordered", ordered, System.currentTimeMillis(), null), null);
         compareHandler.compare();
      }

      return sItems;
   }

   // Bubble completed/cancelled with no sprint to top
   private class NoSprintCompCancelledComparator implements Comparator<IAgileItem> {

      @Override
      public int compare(IAgileItem a1, IAgileItem a2) {
         IAgileSprint sprint1 = atsApi.getAgileService().getSprint(a1);
         IAgileSprint sprint2 = atsApi.getAgileService().getSprint(a2);

         // Must declare equal or next comparator will not be applied
         if (sprint1 == null && a1.getStateMgr().getCurrentStateType().isCompletedOrCancelled() && //
            sprint2 == null && a2.getStateMgr().getStateType().isCompletedOrCancelled()) {
            return 0;
         }
         if (sprint1 == null && a1.getStateMgr().getCurrentStateType().isCompletedOrCancelled()) {
            return -1;
         } else if (sprint2 == null && a2.getStateMgr().getStateType().isCompletedOrCancelled()) {
            return 1;
         }

         return 0;
      }
   };

   private class SprintComparator implements Comparator<IAgileItem> {

      @Override
      public int compare(IAgileItem a1, IAgileItem a2) {
         IAgileSprint sprint1 = atsApi.getAgileService().getSprint(a1);
         IAgileSprint sprint2 = atsApi.getAgileService().getSprint(a2);

         if (sprint1 == null && sprint2 == null) {
            return 0;
         }
         // Sort lower if no sprint
         else if (sprint1 == null) {
            return 1;
         } else if (sprint2 == null) {
            return -1;
         }

         return sprint1.getName().compareTo(sprint2.getName());
      }
   };

   private class CompCancelledComparator implements Comparator<IAgileItem> {

      @Override
      public int compare(IAgileItem a1, IAgileItem a2) {
         IAgileSprint sprint1 = atsApi.getAgileService().getSprint(a1);
         IAgileSprint sprint2 = atsApi.getAgileService().getSprint(a2);

         if (sprint1 == null && sprint2 == null) {
            return 0;
         }

         if (a1.getStateMgr().getCurrentStateType().isCancelled()) {
            return -1;
         }
         if (a2.getStateMgr().getCurrentStateType().isCancelled()) {
            return 1;
         }
         if (a1.getStateMgr().getCurrentStateType().isCompleted()) {
            return -1;
         }
         if (a2.getStateMgr().getCurrentStateType().isCompleted()) {
            return 1;
         }
         return -1 * a1.getStateMgr().getCurrentStateName().compareTo(a2.getStateMgr().getCurrentStateName());
      }
   };

   private String print(String title, List<IAgileItem> sItems) {
      if (!debug) {
         return "";
      }
      XResultData rows = new XResultData();

      XResultData results = new XResultData();
      results.addRaw(AHTML.beginMultiColumnTable(95, 1));
      results.addRaw(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("Id", "Title", "State", "Sprint")));
      for (IAgileItem item : sItems) {
         IAgileSprint sprint = atsApi.getAgileService().getSprint(item);
         String row = AHTML.addHeaderRowMultiColumnTable(Arrays.asList(item.getAtsId(), item.getName(),
            item.getStateDefinition().getName(), sprint == null ? "" : sprint.getName()));
         results.addRaw(row);
         rows.addRaw(AXml.removeXmlTags(row) + "\n");
      }
      results.addRaw(AHTML.endMultiColumnTable());
      XResultDataUI.report(results, title);

      return rows.toString();
   }

}
