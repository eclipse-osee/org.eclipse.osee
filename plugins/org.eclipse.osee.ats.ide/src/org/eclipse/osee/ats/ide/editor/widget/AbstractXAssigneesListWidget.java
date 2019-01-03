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
package org.eclipse.osee.ats.ide.editor.widget;

import java.util.List;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.ide.util.AtsUserLabelProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.XListViewer;
import org.eclipse.osee.framework.ui.swt.ALayout;
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
public abstract class AbstractXAssigneesListWidget extends XListViewer {

   public AbstractXAssigneesListWidget(String displayLabel) {
      super(displayLabel);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      setLabelProvider(new AtsUserLabelProvider());
      setContentProvider(new ArrayContentProvider());

      super.createControls(parent, horizontalSpan);

      setVerticalLabel(true);

      List<IAtsUser> assignees = getCurrentAssignees();
      assignees.remove(AtsCoreUsers.UNASSIGNED_USER);
      setInput(assignees);

      GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
      gd.horizontalSpan = 2;
      gd.heightHint = 60;
      gd.widthHint = 60;
      getTable().setLayoutData(gd);

   }

   public abstract List<IAtsUser> getCurrentAssignees();

   public abstract void handleModifySelection();

   public abstract void handleEmailSelection();

   public abstract boolean includeEmailButton();

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
            handleModifySelection();
         }

      });

      if (includeEmailButton()) {
         Button emailSelected = new Button(mComp, SWT.PUSH);
         emailSelected.setImage(ImageManager.getImage(FrameworkImage.EMAIL));
         emailSelected.setToolTipText("Email");
         emailSelected.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false));
         emailSelected.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               handleEmailSelection();
            }

         });
      }

   }

}
