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
import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
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

/**
 * GUI that is used to maintain an <Code>Artifact</Code> access control list.
 * 
 * @author Jeff C. Phillips
 */
public class PolicyDialog extends Dialog {
   private PolicyTableViewer policyTableViewer;
   private Button btnAdd;
   private Button chkChildrenPermission;
   private Combo cmbUsers;
   private Combo cmbPermissionLevel;
   private final Object accessControlledObject;
   private Label accessLabel;

   public PolicyDialog(Shell parentShell, Object accessControlledObject) {
      super(parentShell);

      this.accessControlledObject = accessControlledObject;
      setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | getDefaultOrientation() | SWT.RESIZE);
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      getShell().setText("Access Control List: " + getHeaderName(accessControlledObject));
      Composite mainComposite = new Composite(parent, SWT.NONE);
      mainComposite.setFont(parent.getFont());
      mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      mainComposite.setLayout(new GridLayout(1, false));

      addDialogContols(mainComposite);
      try {
         setInputs();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      addListeners();
      checkEnabled();
      setMaxModificationLevel();

      return mainComposite;
   }

   private void setInputs() throws OseeCoreException {
      cmbUsers.setText("-Select Person-");
      cmbPermissionLevel.setText("-Select Permission-");

      ArrayList<Artifact> subjectList = new ArrayList<Artifact>();
      subjectList.addAll(UserManager.getUsersSortedByName());
      subjectList.addAll(ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.UserGroup,
         BranchManager.getCommonBranch()));
      Collections.sort(subjectList, new UserComparator<Artifact>());
      for (Artifact subject : subjectList) {
         String name = subject.getName();
         cmbUsers.add(name);
         cmbUsers.setData(name, subject);
      }

      PermissionEnum[] permissions = PermissionEnum.values();
      Arrays.sort(permissions, new Comparator<PermissionEnum>() {

         @Override
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
      btnAdd.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Artifact subject = (Artifact) cmbUsers.getData(cmbUsers.getText().replaceAll(" - Rank.*", ""));
            PermissionEnum permission =
               (PermissionEnum) cmbPermissionLevel.getData(cmbPermissionLevel.getText().replaceAll(" - Rank.*", ""));

            if (subject != null && permission != null) {
               try {
                  policyTableViewer.addOrModifyItem(subject, accessControlledObject, permission);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
      });
   }

   private void addDialogContols(Composite mainComposite) {

      accessLabel = new Label(mainComposite, SWT.NONE);

      Group group = new Group(mainComposite, SWT.NULL);
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      group.setLayout(new GridLayout(1, false));

      policyTableViewer = new PolicyTableViewer(group, accessControlledObject);

      Composite composite = new Composite(group, SWT.NONE);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
      composite.setLayout(new GridLayout(4, false));

      cmbUsers = new Combo(composite, SWT.NONE);
      cmbPermissionLevel = new Combo(composite, SWT.NONE);
      btnAdd = new Button(composite, SWT.PUSH);
      btnAdd.setText("Add");
      new Label(composite, SWT.NONE).setText("  NOTE: Higher permission rank overrides lower rank.");

      chkChildrenPermission = new Button(mainComposite, SWT.CHECK);
      chkChildrenPermission.setText("Set permission for artifact's default hierarchy descendents.");
   }

   private void setMaxModificationLevel() {
      PermissionEnum permission = AccessControlManager.getPermission(accessControlledObject);
      policyTableViewer.setMaxModificationLevel(permission);
   }

   private void checkEnabled() {
      boolean isAccessEnabled = isAddAccessEnabled();
      boolean isModifyEnabled = isModifyAccessEnabled();

      String displayText = "";
      if (!isAccessEnabled) {
         displayText = "You do not have permissions to add/delete users";
         if (!isModifyEnabled) {
            displayText += " or modify access";
         }
      } else if (!isModifyEnabled) {
         displayText = "You do not have permissions to modify access";
      }

      accessLabel.setText(displayText);
      boolean isArtifact = accessControlledObject instanceof Artifact;

      cmbUsers.setEnabled(isAccessEnabled);
      cmbPermissionLevel.setEnabled(isAccessEnabled);
      btnAdd.setEnabled(isAccessEnabled);
      policyTableViewer.allowTableModification(isAccessEnabled);
      chkChildrenPermission.setEnabled(isArtifact);
   }

   private boolean isModifyAccessEnabled() {
      boolean returnValue;

      try {
         if (policyTableViewer.getAccessControlList().size() > 0) {
            returnValue = AccessControlManager.hasPermission(accessControlledObject, PermissionEnum.OWNER);
         } else {
            returnValue = true;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         returnValue = false;
      }

      return returnValue;
   }

   private boolean isAddAccessEnabled() {
      boolean returnValue;

      try {
         if (policyTableViewer.getAccessControlList().size() > 0) {
            returnValue = AccessControlManager.hasPermission(accessControlledObject, PermissionEnum.FULLACCESS);
         } else {
            returnValue = true;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         returnValue = false;
      }

      return returnValue;
   }

   @Override
   protected void okPressed() {
      for (AccessControlData data : policyTableViewer.getAccessControlList().values()) {
         if (data.isDirty()) {
            boolean isRecursionAllowed = chkChildrenPermission.getSelection();
            AccessControlManager.persistPermission(data, isRecursionAllowed);
         }
      }
      policyTableViewer.removeDataFromDB();
      super.okPressed();
   }

   private String getHeaderName(Object object) {
      String name = "";
      if (object instanceof Artifact) {
         name = ((Artifact) object).getName();
      } else if (object instanceof IOseeBranch) {
         name = ((IOseeBranch) object).getName();
      }
      return name;
   }

   private static final class UserComparator<T> implements Comparator<T> {
      @Override
      public int compare(T o1, T o2) {
         if (o1 instanceof Artifact && o2 instanceof Artifact) {
            return ((Artifact) o1).getName().compareToIgnoreCase(((Artifact) o2).getName());
         }
         return 0;
      }
   }
}
