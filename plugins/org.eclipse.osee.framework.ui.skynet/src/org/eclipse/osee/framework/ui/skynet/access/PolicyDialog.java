/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.access;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.access.AccessControlData;
import org.eclipse.osee.framework.core.access.event.AccessArtifactLockTopicEvent;
import org.eclipse.osee.framework.core.access.event.AccessTopicEvent;
import org.eclipse.osee.framework.core.access.object.AccessObject;
import org.eclipse.osee.framework.core.access.object.ArtifactAccessObject;
import org.eclipse.osee.framework.core.access.object.BranchAccessObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlArtifactUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.ui.skynet.access.internal.OseeApiService;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.HyperLinkLabel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * GUI that is used to maintain an <Code>Artifact</Code> access control list.
 *
 * @author Jeff C. Phillips
 */
public class PolicyDialog extends Dialog {
   private PolicyTableViewer policyTableViewer;
   private Button addButton;
   private Button chkChildrenPermission;
   private Combo userCombo;
   private Combo permissionLevelCombo;
   private final AccessObject accessControlledObject;
   private Label accessErrorLabel, accessTitleLabel;
   private final Shell parentShell;
   Boolean isArtifactLockedBeforeDialog;
   private XResultData accessModifyEnabled;

   public PolicyDialog(Shell parentShell, AccessObject accessControlledObject) {
      super(parentShell);
      this.parentShell = parentShell;
      this.accessControlledObject = accessControlledObject;
      setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | getDefaultOrientation() | SWT.RESIZE);
   }

   public static PolicyDialog createPolicyDialog(Shell activeShell, Object object) {
      if (object instanceof ArtifactToken) {
         return new PolicyDialog(activeShell, ArtifactAccessObject.valueOf((ArtifactToken) object));
      } else if (object instanceof BranchToken) {
         return new PolicyDialog(activeShell, BranchAccessObject.valueOf((BranchToken) object));
      }
      throw new OseeArgumentException("Unhandled object %", object);
   }

   @Override
   protected Control createDialogArea(Composite parent) {
      final String title = "Access Control for " + (accessControlledObject.isArtifact() ? "Artifact" : "Branch");
      getShell().setText(title);

      Composite mainComposite = new Composite(parent, SWT.NONE);
      mainComposite.setFont(parent.getFont());
      mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      mainComposite.setLayout(new GridLayout(1, false));

      createTitleComp(mainComposite, title);

      accessErrorLabel = new Label(mainComposite, SWT.NONE);
      accessErrorLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));

      Group group = new Group(mainComposite, SWT.NULL);
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      group.setLayout(new GridLayout(1, false));

      policyTableViewer = new PolicyTableViewer(group, accessControlledObject);

      // Create Input Widgets
      Composite composite = new Composite(group, SWT.NONE);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
      composite.setLayout(new GridLayout(3, false));

      permissionLevelCombo = new Combo(composite, SWT.NONE);
      userCombo = new Combo(composite, SWT.NONE);
      addButton = new Button(composite, SWT.PUSH);
      addButton.setText("Add");

      boolean accessEnabled = isAccessEnabled();
      addButton.setEnabled(accessEnabled);
      policyTableViewer.setReadonly(!accessEnabled);

      chkChildrenPermission = new Button(composite, SWT.CHECK);
      chkChildrenPermission.setText("Set permission for artifact's default hierarchy descendents.");

      new Label(mainComposite, SWT.NONE).setText("  NOTE: Higher permission rank overrides lower rank.");
      Label baseNote = new Label(mainComposite, SWT.NONE);
      baseNote.setText("  NOTE: Baseline Branches are Read-Only by default.");
      if (accessControlledObject instanceof BranchToken) {
         BranchToken branch = (BranchToken) accessControlledObject;
         if (BranchManager.getType(branch).isBaselineBranch()) {
            baseNote.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         }
      }

      try {
         populateInputWidgets();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      addListeners();
      checkEnabled();
      setMaxModificationLevel();

      if (accessControlledObject instanceof ArtifactAccessObject) {
         isArtifactLockedBeforeDialog = OseeApiService.get().getAccessControlService().hasLock(
            ((ArtifactAccessObject) accessControlledObject).getArtifact());
      }

      return mainComposite;
   }

   private void createTitleComp(Composite parent, final String title) {
      Composite titleComposite = new Composite(parent, SWT.NONE);
      titleComposite.setFont(parent.getFont());
      titleComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      titleComposite.setLayout(new GridLayout(2, false));

      accessTitleLabel = new Label(titleComposite, SWT.NONE);
      accessTitleLabel.setText(String.format("Access Control for %s\nUser %s",
         Strings.truncate(accessControlledObject.toString(), 70), UserManager.getUser().toStringWithId()));
      accessTitleLabel.setFont(FontManager.getCourierNew12Bold());

      HyperLinkLabel edit = new HyperLinkLabel(titleComposite, SWT.None);
      edit.setText("           Show Access Details");
      edit.addListener(SWT.MouseUp, new Listener() {

         @Override
         public void handleEvent(Event event) {
            XResultData rd = null;
            String useTitle = "Modify " + title;
            if (accessControlledObject.isArtifact()) {
               rd = AccessControlArtifactUtil.getXResultAccessHeader(useTitle,
                  (Artifact) ((ArtifactAccessObject) accessControlledObject).getArtifact());
            } else {
               rd = AccessControlArtifactUtil.getXResultAccessHeader(useTitle, accessControlledObject.getBranch());
            }
            rd.addRaw(accessModifyEnabled.toString());
            XResultDataUI.report(rd, useTitle);
            close();
         }
      });
   }

   private void populateInputWidgets() {

      // Setup permissions combo
      permissionLevelCombo.setText("-Select Permission-");
      List<PermissionEnum> permissions = new ArrayList<>();
      for (PermissionEnum permission : PermissionEnum.values()) {
         if (permission == PermissionEnum.USER_LOCK) {
            if (accessControlledObject instanceof ArtifactId) {
               permissions.add(permission);
            }
         } else {
            permissions.add(permission);
         }
      }
      Collections.sort(permissions, new Comparator<PermissionEnum>() {

         @Override
         public int compare(PermissionEnum o1, PermissionEnum o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
         }
      });
      for (PermissionEnum permission : permissions) {
         permissionLevelCombo.add(
            permission.getName() + " - Rank = " + permission.getRank() + " - " + permission.getDescription());
         permissionLevelCombo.setData(permission.getName(), permission);
      }

      // Setup user combo
      userCombo.setText("-Select Person-");
      ArrayList<Artifact> subjectList = new ArrayList<>();
      subjectList.addAll(UserManager.getUsersSortedByName());
      subjectList.addAll(ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.UserGroup, CoreBranches.COMMON));
      Collections.sort(subjectList, new UserComparator<Artifact>());
      for (Artifact subject : subjectList) {
         String name = subject.getName();
         userCombo.add(name);
         userCombo.setData(name, subject);
      }

   }

   private void addListeners() {
      addButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            Artifact subject = (Artifact) userCombo.getData(userCombo.getText().replaceAll(" - Rank.*", ""));
            PermissionEnum permission = (PermissionEnum) permissionLevelCombo.getData(
               permissionLevelCombo.getText().replaceAll(" - Rank.*", ""));

            if (subject != null && permission != null) {
               try {
                  policyTableViewer.addOrModifyItem(subject, accessControlledObject, permission);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            } else {
               MessageDialog.openError(parentShell, "Add Error",
                  "Please select a Person and Permission Level to add to the Access Control List");
            }
         }
      });
   }

   private void setMaxModificationLevel() {
      PermissionEnum permission = null;
      if (accessControlledObject.isArtifact()) {
         permission = OseeApiService.get().getAccessControlService().getPermission(
            ((ArtifactAccessObject) accessControlledObject).getArtifact());
      } else if (accessControlledObject.isBranch()) {
         permission = OseeApiService.get().getAccessControlService().getPermission(
            ((BranchAccessObject) accessControlledObject).getBranch());
      } else {
         throw new OseeArgumentException("Unhandled object %s", accessControlledObject);
      }
      policyTableViewer.setMaxModificationLevel(permission);
   }

   private void checkEnabled() {
      boolean isAccessEnabled = isAccessEnabled();

      String displayText = "";
      if (!isAccessEnabled) {
         displayText = "You do not have permissions to modify access";
      }

      accessErrorLabel.setText(displayText);
      boolean isArtifact = accessControlledObject.isArtifact();

      userCombo.setEnabled(isAccessEnabled);
      permissionLevelCombo.setEnabled(isAccessEnabled);
      addButton.setEnabled(isAccessEnabled);
      policyTableViewer.allowTableModification(isAccessEnabled);
      chkChildrenPermission.setEnabled(isArtifact);
   }

   private boolean isAccessEnabled() {
      accessModifyEnabled = new XResultData();
      if (accessControlledObject.isArtifact()) {
         OseeApiService.get().getAccessControlService().isModifyAccessEnabled(UserManager.getUser(),
            ((ArtifactAccessObject) accessControlledObject).getArtifact(), accessModifyEnabled);
      } else if (accessControlledObject.isBranch()) {
         OseeApiService.get().getAccessControlService().isModifyAccessEnabled(UserManager.getUser(),
            accessControlledObject.getBranch(), accessModifyEnabled);
      } else {
         accessModifyEnabled.errorf("User %s DOES NOT have Access Modify rights for %s: Reason [Unhandled Object]",
            UserManager.getUser().getName(), accessControlledObject);
      }
      return accessModifyEnabled.isSuccess();
   }

   @Override
   protected void okPressed() {
      for (AccessControlData data : policyTableViewer.getAccessControlList().values()) {
         if (data.isDirty()) {
            boolean isRecursionAllowed = chkChildrenPermission.getSelection();
            OseeApiService.get().getAccessControlService().persistPermission(data, isRecursionAllowed);
         }
      }
      policyTableViewer.removeDataFromDB();
      OseeApiService.get().getAccessControlService().clearCaches();

      // Send artifact locked event if changed in dialog
      if (isArtifactLockedBeforeDialog != null) {
         try {
            Artifact artifact = (Artifact) ((ArtifactAccessObject) accessControlledObject).getArtifact();
            boolean isArtifactLockedAfterDialog = OseeApiService.get().getAccessControlService().hasLock(artifact);
            if (isArtifactLockedAfterDialog != isArtifactLockedBeforeDialog) {
               AccessArtifactLockTopicEvent event = new AccessArtifactLockTopicEvent();
               event.setBranch(artifact.getBranch());
               event.addArtifact(artifact.getUuid());
               OseeEventManager.kickAccessTopicEvent(this, event, AccessTopicEvent.ACCESS_ARTIFACT_LOCK_MODIFIED);
            }
         } catch (Exception ex) {
            OseeLog.log(PolicyDialog.class, Level.SEVERE, ex);
         }

      }
      super.okPressed();
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
