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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.column.AssigneeColumnUI;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.notify.ArtifactEmailWizard;
import org.eclipse.osee.ats.util.AtsUserLabelProvider;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XListViewer;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class XAssigneesListWidget extends XListViewer implements IArtifactWidget {

   private AbstractWorkflowArtifact awa;

   public XAssigneesListWidget(String displayLabel) {
      super(displayLabel);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      setLabelProvider(new AtsUserLabelProvider());
      setContentProvider(new ArrayContentProvider());

      super.createControls(parent, horizontalSpan);

      setVerticalLabel(true);

      List<IAtsUser> assignees = awa.getAssignees();
      assignees.remove(AtsCoreUsers.UNASSIGNED_USER);
      setInput(assignees);

      GridData gd = new GridData(SWT.FILL, SWT.NONE, false, false);
      gd.horizontalSpan = 2;
      gd.heightHint = 60;
      getTable().setLayoutData(gd);

   }

   @Override
   protected void createControlsAfterLabel(Composite parent, int horizontalSpan) {

      Composite mComp = new Composite(parent, SWT.FLAT);
      GridData gd = new GridData(SWT.FILL, SWT.NONE, false, false);
      gd.horizontalSpan = 2;
      mComp.setLayoutData(gd);
      mComp.setLayout(ALayout.getZeroMarginLayout(2, false));
      mComp.setBackgroundMode(SWT.INHERIT_FORCE);

      Button modifyList = new Button(mComp, SWT.PUSH);
      modifyList.setImage(ImageManager.getImage(FrameworkImage.EDIT));
      modifyList.setToolTipText("Select to modify");
      modifyList.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false));
      modifyList.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               AssigneeColumnUI.promptChangeAssignees(awa, true);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

      });

      Button emailSelected = new Button(mComp, SWT.PUSH);
      emailSelected.setImage(ImageManager.getImage(FrameworkImage.EMAIL));
      emailSelected.setToolTipText("Email");
      emailSelected.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false));
      emailSelected.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               ArtifactEmailWizard wizard = new ArtifactEmailWizard(awa);
               WizardDialog dialog = new WizardDialog(Displays.getActiveShell(), wizard);
               dialog.create();
               dialog.open();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

      });

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
   public Artifact getArtifact() throws OseeCoreException {
      return awa;
   }

   @Override
   public void setArtifact(Artifact artifact) throws OseeCoreException {
      this.awa = (AbstractWorkflowArtifact) artifact;
   }

}
