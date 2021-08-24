/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.task.widgets;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.util.Import.ImportTasksFromSimpleList;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class CreateTasksFromSimpleList extends ImportTasksFromSimpleList {

   public CreateTasksFromSimpleList(IAtsTeamWorkflow teamWf, AtsWorkDefinitionToken taskWorkDef) {
      this.teamWf = teamWf;
      this.taskWorkDef = taskWorkDef;
   }

   @Override
   public String getTitle() {
      return "Create Estimates Tasks";
   }

   @Override
   public boolean showUsageSection() {
      return false;
   }

   @Override
   protected void createTeamWfWidget(StringBuffer buffer) {
      buffer.append(
         "<XWidget xwidgetType=\"XLabelValue\" displayName=\"Team Workflow\" defaultValue=\"" + teamWf.toStringWithId() + " \"/>");
      buffer.append(
         "<XWidget xwidgetType=\"XLabelValue\" displayName=\"Task Workflow Definition\" defaultValue=\"" + taskWorkDef.getName() + " \"/>");
   }

   @Override
   protected String getTitlesLabel() {
      return "Task Title(s)";
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
   }

   @Override
   public String getRunText() {
      return getTitle();
   }

   @Override
   public String getName() {
      return getTitle();
   }

   @Override
   public String getOutputMessage() {
      return "Not yet run.";
   }

   @Override
   public String getTabTitle() {
      return getName();
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(AtsImage.CONTEXT_CHANGE_REPORT);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.CONTEXT_CHANGE_REPORT);
   }

}
