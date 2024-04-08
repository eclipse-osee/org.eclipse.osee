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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileItem;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsArtifactImages;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.AtsTopicEvent;
import org.eclipse.osee.ats.ide.AtsArtifactImageProvider;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
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

   public SortAgileBacklog() {
      super("Sort Agile Backlog", AtsArtifactImageProvider.getKeyedImage(AtsArtifactImages.AGILE_BACKLOG),
         AgileNavigateItemProvider.AGILE);
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
      if (dialog.open() == Window.OK) {
         Artifact agileTeamArt = dialog.getSelectedFirst();
         Artifact backlog = agileTeamArt.getRelatedArtifactOrNull(AtsRelationTypes.AgileTeamToBacklog_Backlog);
         if (MessageDialog.openConfirm(Displays.getActiveShell(), "Sort Agile Team",
            String.format("Sort Agile Team Backlog\n\n%s\n\nAre you sure?", backlog.toStringWithId()))) {
            sort(backlog);
         }
      }
   }

   private void sort(Artifact backlog) {
      Job sortJob = new Job(getName()) {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            IAgileBacklog bLog = atsApi.getAgileService().getAgileBacklog(backlog);
            Collection<IAgileItem> items;
            List<IAgileItem> sItems;

            if (debug) {
               items = atsApi.getAgileService().getItems(bLog);
               sItems = new ArrayList<>();
               sItems.addAll(items);
            }

            String unOrdered = "";
            String ordered = "";

            if (debug) {
               unOrdered = print("Un-Ordered Backlog", sItems);
            }

            XResultData rd = atsApi.getServerEndpoints().getAgileEndpoint().sortBacklog(bLog.getArtifactToken());
            if (rd.isErrors()) {
               XResultDataUI.report(rd, getName());
               return Status.OK_STATUS;
            }

            backlog.reloadAttributesAndRelations();
            TransactionRecord transaction =
               org.eclipse.osee.framework.skynet.core.transaction.TransactionManager.getTransaction(
                  TransactionId.valueOf(rd.getTxId()));
            atsApi.getEventService().postAtsWorkItemTopicEvent(AtsTopicEvent.WORK_ITEM_MODIFIED,
               Collections.singleton(bLog), transaction);

            if (debug) {
               ordered = print("Ordered Backlog", sItems);
            }

            if (debug) {
               CompareHandler compareHandler =
                  new CompareHandler(null, new CompareItem("Un-Ordered", unOrdered, System.currentTimeMillis(), null),
                     new CompareItem("Ordered", ordered, System.currentTimeMillis(), null), null);
               compareHandler.compare();
            }
            return Status.OK_STATUS;
         }
      };
      sortJob.schedule();
   }

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
