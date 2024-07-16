/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.workflow.goal;

import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokens;
import org.eclipse.osee.ats.core.workflow.util.ChangeTypeUtil;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenAgileTasksAction extends Action {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;
   private String agileCard;

   public static interface RemovedFromCollectorHandler {
      void removedFromCollector(Collection<? extends Artifact> removed);
   }

   public OpenAgileTasksAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super("Open Agile Task Cards for Selected");
      this.selectedAtsArtifacts = selectedAtsArtifacts;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.PRINT);
   }

   @Override
   public void run() {
      try {
         Collection<? extends Artifact> selected = selectedAtsArtifacts.getSelectedAtsArtifacts();
         if (selected.isEmpty()) {
            AWorkbench.popup("No items selected");
            return;
         }
         StringBuilder sb = new StringBuilder();
         sb.append("<table class=\"fixed\" border=\"2\" cellpadding=\"3\" cellspacing=\"0\" width=\"100\">");
         Artifact[] arts = selected.toArray(new Artifact[selected.size()]);
         for (int x = 0; x < arts.length; x += 2) {
            String firstCard = getCardHtml(arts[x]);
            String secondCard = "";
            if (arts.length != x + 1) {
               secondCard = getCardHtml(arts[x + 1]);
            }
            sb.append(AHTML.addRowMultiColumnTable(firstCard, secondCard));
         }
         sb.append(AHTML.endMultiColumnTable());
         ResultsEditor.open("Agile Tasks", "Agile Tasks", sb.toString());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private String getCardHtml(Artifact artifact) {
      String card = "";
      if (artifact instanceof IAtsWorkItem) {
         card = getCardTemplate();
         IAtsWorkItem workItem = (IAtsWorkItem) artifact;
         card = card.replaceFirst("PUT_TITLE_HERE", workItem.getName());

         card =
            card.replaceFirst("PUT_POINTS_HERE", AtsApiService.get().getAgileService().getAgileTeamPointsStr(workItem));
         card = card.replaceFirst("PUT_FEATURE_HERE",
            AtsApiService.get().getAgileService().getAgileFeatureGroupStr(workItem));

         card =
            card.replaceFirst("PUT_CHANGE_TYPE_HERE", ChangeTypeUtil.getChangeTypeStr(workItem, AtsApiService.get()));
         card = card.replaceFirst("PUT_PRIORITY_HERE",
            AtsApiService.get().getColumnService().getColumnText(AtsColumnTokens.PriorityColumn, workItem));

         card = card.replaceFirst("PUT_ASSIGNEES_HERE",
            AtsApiService.get().getColumnService().getColumnText(AtsColumnTokens.AssigneeColumn, workItem));

         card = card.replaceFirst("PUT_ATSID_HERE",
            AtsApiService.get().getColumnService().getColumnText(AtsColumnTokens.AtsIdColumn, workItem));
         card = card.replaceFirst("PUT_AI_HERE",
            AtsApiService.get().getColumnService().getColumnText(AtsColumnTokens.ActionableItemsColumn, workItem));
         card = card.replaceFirst("PUT_VERSION_HERE",
            AtsApiService.get().getColumnService().getColumnText(AtsColumnTokens.TargetedVersionColumn, workItem));
      }
      return card;
   }

   private String getCardTemplate() {
      if (agileCard == null) {
         agileCard = OseeInf.getResourceContents("templates/agileCard.html", OpenAgileTasksAction.class);
         return OseeInf.getResourceContents("templates/agileCard.html", OpenAgileTasksAction.class);
      }
      return agileCard;
   }
}
