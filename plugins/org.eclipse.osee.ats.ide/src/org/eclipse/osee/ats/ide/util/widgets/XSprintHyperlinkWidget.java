/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.agile.SprintFilteredListDialog;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;

/**
 * @author Donald G. Dunne
 */
public class XSprintHyperlinkWidget extends XHyperlinkLabelCmdValueSelection {

   IAgileSprint sprint;
   IAtsTeamWorkflow teamWf;
   public static final String WIDGET_ID = XSprintHyperlinkWidget.class.getSimpleName();
   AtsApi atsApi;

   public XSprintHyperlinkWidget() {
      super("Sprint", true, 50);
      atsApi = AtsApiService.get();
   }

   @Override
   public String getCurrentValue() {
      return sprint == null ? "" : sprint.getName();
   }

   @Override
   public boolean handleSelection() {
      IAgileTeam agileTeam = atsApi.getAgileService().getAgileTeam(teamWf);
      Collection<IAgileSprint> agileSprints = atsApi.getAgileService().getAgileSprints(agileTeam);

      final SprintFilteredListDialog dialog =
         new SprintFilteredListDialog("Select Sprint", "Select Sprint", agileSprints);
      int result = dialog.open();
      if (result != 0) {
         return false;
      }
      Object obj = dialog.getSelectedFirst();
      sprint = (IAgileSprint) obj;
      return true;
   }

   @Override
   public boolean handleClear() {
      sprint = null;
      return true;
   }

   public IAgileSprint getSelected() {
      return sprint;
   }

   public void setSprint(IAgileSprint sprint) {
      this.sprint = sprint;
   }

   public void setTeamWf(IAtsTeamWorkflow teamWf) {
      this.teamWf = teamWf;
   }

}
