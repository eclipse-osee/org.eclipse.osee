/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.duplicate;

import org.eclipse.osee.framework.ui.skynet.widgets.dialog.XWidgetsDialog;

/**
 * @author Donald G. Dunne
 */
public class CloneDialog extends XWidgetsDialog {

   public CloneDialog(String dialogTitle, String dialogMessage) {
      super(dialogTitle, dialogMessage);
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidgets>");
      builder.append("<XWidget xwidgetType=\"XText\" displayName=\"Title\" id=\"title\"/>");
      builder.append("<XWidget xwidgetType=\"XText\" displayName=\"Description\" id=\"desc\" fill=\"Vertically\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Create New Action with Workflow\" "//
         + "toolTip=\"Un-Check to add Workflow to this Action, otherwise new Workflow will belong to this Action.\" " //
         + " id=\"newAction\" defaultValue=\"true\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Leave blank to get current Originator\" />");
      builder.append("<XWidget xwidgetType=\"XAssigneesHyperlinkWidget\" displayName=\"Originator\" id=\"orig\" />");
      builder.append("</XWidgets>");
      return builder.toString();
   }

}
