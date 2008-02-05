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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.access.AccessControlData;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
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

   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(PolicyDialog.class);
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static final SkynetAuthentication skynetAuthentication = SkynetAuthentication.getInstance();
   private PolicyTableViewer policyTableViewer;
   private Button radEnabled;
   private Button radDisabled;
   private Button btnAdd;
   private Button chkChildrenPermission;
   private Combo cmbUsers;
   private Combo cmbPermissionLevel;
   private Object object;
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
      setInputs();
      addListeners();
      checkEnabled();

      return mainComposite;
   }

   private void setInputs() {
      cmbUsers.setText("-Select Person-");
      cmbPermissionLevel.setText("-Select Permission-");
      ArrayList<Artifact> subjectList = new ArrayList<Artifact>();
      subjectList.addAll(skynetAuthentication.getUsers());
      try {
         subjectList.addAll(artifactManager.getArtifacts(new ArtifactTypeSearch("User Group", Operator.EQUAL),
               branchManager.getCommonBranch()));
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }

      Artifact[] subjectsArray = (Artifact[]) subjectList.toArray(Artifact.EMPTY_ARRAY);
      Arrays.sort(subjectsArray, new Comparator<Artifact>() {

         public int compare(Artifact o1, Artifact o2) {
            return o1.getDescriptiveName().compareToIgnoreCase(o2.getDescriptiveName());
         }
      });

      for (Artifact subject : subjectsArray) {
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

   private void addListeners() {
      radDisabled.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            checkEnabled();
         }
      });

      radEnabled.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            checkEnabled();
         }
      });

      btnAdd.addSelectionListener(new SelectionAdapter() {
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
      boolean accessEnabled = AccessControlManager.getInstance().checkObjectPermission(object, PermissionEnum.WRITE);

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
