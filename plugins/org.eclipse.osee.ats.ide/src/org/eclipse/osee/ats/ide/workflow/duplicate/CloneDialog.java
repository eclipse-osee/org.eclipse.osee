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

package org.eclipse.osee.ats.ide.workflow.duplicate;

import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.column.OriginatorColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.XWidgetsDialog;

/**
 * @author Donald G. Dunne
 */
public class CloneDialog extends XWidgetsDialog {

   private final IAtsTeamWorkflow teamWf;

   public CloneDialog(String dialogTitle, String dialogMessage, IAtsTeamWorkflow teamWf) {
      super(dialogTitle, dialogMessage);
      this.teamWf = teamWf;
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidgets>");
      builder.append("<XWidget xwidgetType=\"XText\" displayName=\"Title\" id=\"title\"/>");
      builder.append(
         "<XWidget xwidgetType=\"XText\" displayName=\"Description\" height=\"60\" id=\"desc\" fill=\"Vertically\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Create New Action with Workflow\" "//
         + "toolTip=\"Un-Check to add Workflow to this Action, otherwise new Workflow will belong to this Action.\" " //
         + " id=\"newAction\" defaultValue=\"true\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      builder.append(
         String.format("<XWidget xwidgetType=\"XLabel\" displayName=\"Leave blank to get current Originator [%s]\" />",
            OriginatorColumn.getInstance().getColumnText(teamWf, null, 0)));
      builder.append("<XWidget xwidgetType=\"XAssigneesHyperlinkWidget\" displayName=\"Originator\" id=\"orig\" />");
      builder.append("</XWidgets>");
      return builder.toString();
   }

}
