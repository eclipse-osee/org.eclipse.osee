/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.access;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlData;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

/**
 * GUI that is used to maintain an <Code>Artifact</Code> access contol list.
 * 
 * @author Jeff C. Phillips
 */
public class PolicyDialog extends Dialog {
   private PolicyTableViewer policyTableViewer;
   private Button radEnabled;
   private Button radDisabled;
   private Button btnAdd;
   private Button chkChildrenPermission;
   private Combo cmbUsers;
   private Combo cmbPermissionLevel;
   private final Object object;
   private Label accessLabel;

   public PolicyDialog(Shell parentShell, Object object) {
      super(parentShell);

      this.object = object;
      setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | getDefaultOrientation() | SWT.RESIZE);
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      getShell().setText("Access Control List: " + getHeadertName(object));
      Composite mainComposite = new Composite(parent, SWT.NONE);
      mainComposite.setFont(parent.getFont());
      mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      mainComposite.setLayout(new GridLayout(1, false));

      addDialogContols(mainComposite);
      try {
         setInputs();
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      addListeners();
      checkEnabled();

      return mainComposite;
   }

   private void setInputs() throws OseeCoreException {
      cmbUsers.setText("-Select Person-");
      cmbPermissionLevel.setText("-Select Permission-");
      ArrayList<Artifact> subjectList = new ArrayList<Artifact>();
      subjectList.addAll(UserManager.getUsersSortedByName());
      subjectList.addAll(ArtifactQuery.getArtifactsFromType("User Group", BranchManager.getCommonBranch()));
      Collections.sort(subjectList, new userComparator<Artifact>());
      for (Artifact subject : subjectList) {
         String name = subject.getDescriptiveName();
         cmbUsers.add(name);
         cmbUsers.setData(name, subject);
      }

      PermissionEnum[] permissions = PermissionEnum.values();
      Arrays.sort(permissions, new Comparator<PermissionEnum>() {

         public int compare(PermissionEnum o1, PermissionEnum o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
         }
      });

      for (PermissionEnum permission : permissions) {
         if (!permission.equals(PermissionEnum.LOCK)) {
            cmbPermissionLevel.add(permission.getName() + " - Rank = " + permission.getRank() + "");
            cmbPermissionLevel.setData(permission.getName(), permission);
         }
      }
   }
   private class userComparator<T> implements Comparator<T> {

      /* (non-Javadoc)
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      @Override
      public int compare(T o1, T o2) {
         if (o1 instanceof Artifact && o2 instanceof Artifact) {
            return ((Artifact) o1).getDescriptiveName().compareToIgnoreCase(((Artifact) o2).getDescriptiveName());
         }
         return 0;
      }

   }

   private void addListeners() {
      radDisabled.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            checkEnabled();
         }
      });

      radEnabled.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            checkEnabled();
         }
      });

      btnAdd.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Artifact subject = (Artifact) cmbUsers.getData(cmbUsers.getText().replaceAll(" - Rank.*", ""));
            PermissionEnum permission =
                  (PermissionEnum) cmbPermissionLevel.getData(cmbPermissionLevel.getText().replaceAll(" - Rank.*", ""));

            if (subject != null && permission != null) {
               policyTableViewer.addItem(subject, object, permission);
            }
         }
      });
   }

   private void addDialogContols(Composite mainComposite) {

      accessLabel = new Label(mainComposite, SWT.NONE);

      radDisabled = new Button(mainComposite, SWT.RADIO);
      radDisabled.setText("Disabled");
      radDisabled.setEnabled(false);

      radEnabled = new Button(mainComposite, SWT.RADIO);
      radEnabled.setText("Enabled");

      Group group = new Group(mainComposite, SWT.NULL);
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      group.setLayout(new GridLayout(1, false));

      Table table = new Table(group, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
      policyTableViewer = new PolicyTableViewer(table, object);
      GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
      gridData.heightHint = 100;
      gridData.widthHint = 500;
      table.setLayoutData(gridData);

      Composite composite = new Composite(group, SWT.NONE);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
      composite.setLayout(new GridLayout(3, false));

      cmbUsers = new Combo(composite, SWT.NONE);
      cmbPermissionLevel = new Combo(composite, SWT.NONE);
      btnAdd = new Button(composite, SWT.PUSH);
      btnAdd.setText("Add");

      (new Label(group, SWT.NONE)).setText("  NOTE: Higher permission rank overrides lower rank.");

      chkChildrenPermission = new Button(mainComposite, SWT.CHECK);
      chkChildrenPermission.setText("Set permission for artifact's default hierarchy descendents.");
   }

   private void checkEnabled() {
      // get information from db
      boolean accessEnabled;
      try {
         accessEnabled = AccessControlManager.checkObjectPermission(object, PermissionEnum.WRITE);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         accessEnabled = false;
      }

      accessLabel.setText(accessEnabled ? "" : "You do not have permissions to modify access.");
      radEnabled.setSelection(true);

      boolean enable = radEnabled.getSelection() && accessEnabled;
      boolean isArtifact = object instanceof Artifact;

      cmbUsers.setEnabled(enable);
      cmbPermissionLevel.setEnabled(enable);
      btnAdd.setEnabled(enable);
      policyTableViewer.setEnabled(enable);

      chkChildrenPermission.setEnabled(isArtifact);
   }

   @Override
   protected void okPressed() {
      for (AccessControlData data : policyTableViewer.getAccessControlList().values()) {
         if (data.isDirty()) data.persist(chkChildrenPermission.getSelection());
      }
      super.okPressed();
   }

   private String getHeadertName(Object object) {
      String name = "";
      if (object instanceof Artifact) {
         name = ((Artifact) object).getDescriptiveName();
      } else if (object instanceof Branch) {
         name = ((Branch) object).getBranchName();
      }
      return name;
   }
}
