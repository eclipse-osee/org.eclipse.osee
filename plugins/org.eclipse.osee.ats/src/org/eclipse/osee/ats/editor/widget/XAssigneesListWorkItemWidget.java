/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor.widget;

import java.util.List;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.column.AssigneeColumnUI;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.notify.ArtifactEmailWizard;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class XAssigneesListWorkItemWidget extends AbstractXAssigneesListWidget implements IArtifactWidget {

   private AbstractWorkflowArtifact awa;
   public static final String WIDGET_ID = XAssigneesListWorkItemWidget.class.getSimpleName();

   public XAssigneesListWorkItemWidget(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public List<IAtsUser> getCurrentAssignees() {
      return awa.getAssignees();
   }

   @Override
   public void handleModifySelection() {
      try {
         AssigneeColumnUI.promptChangeAssignees(awa, true);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void handleEmailSelection() {
      try {
         ArtifactEmailWizard wizard = new ArtifactEmailWizard(awa);
         WizardDialog dialog = new WizardDialog(Displays.getActiveShell(), wizard);
         dialog.create();
         dialog.open();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public boolean includeEmailButton() {
      return true;
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public Artifact getArtifact()  {
      return awa;
   }

   @Override
   public void setArtifact(Artifact artifact)  {
      this.awa = (AbstractWorkflowArtifact) artifact;
   }

}
