/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.widget;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.notify.WorkflowEmailWizard;
import org.eclipse.osee.ats.ide.util.AtsUserLabelProvider;
import org.eclipse.osee.ats.ide.util.UserCheckTreeDialog;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.EmailGroup;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeWidget;
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
public class XAtsUserListDam extends XListViewer implements AttributeWidget {

   private AbstractWorkflowArtifact awa;
   private AttributeTypeToken attributeType;
   private final Collection<AtsUser> selectedUsers = new LinkedList<>();

   public XAtsUserListDam(String displayLabel) {
      super(displayLabel);
   }

   public Artifact getStored() {
      Object obj = awa.getSoleAttributeValue(attributeType, null);
      if (obj instanceof Integer) {
         return ArtifactQuery.getArtifactFromId((Integer) obj, awa.getBranch());
      } else if (obj instanceof Artifact) {
         return (Artifact) obj;
      }
      return null;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      setLabelProvider(new AtsUserLabelProvider());
      setContentProvider(new ArrayContentProvider());

      super.createControls(parent, horizontalSpan);

      setVerticalLabel(true);

      selectedUsers.addAll(getStoredAtsUsers());
      setInput(selectedUsers);

      GridData gd = new GridData(SWT.FILL, SWT.NONE, false, false);
      gd.horizontalSpan = 2;
      gd.heightHint = 60;
      getTable().setLayoutData(gd);

   }

   private Collection<AtsUser> getStoredAtsUsers() {
      List<AtsUser> users = new LinkedList<>();
      for (String userId : awa.getAttributesToStringList(attributeType)) {
         AtsUser user = AtsApiService.get().getUserService().getUserByUserId(userId);
         if (user != null && !AtsCoreUsers.isSystemUser(user)) {
            users.add(user);
         }
      }
      return users;
   }

   private Collection<AtsUser> getSelectedUsers() {
      return selectedUsers;
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
            handleModifyList();
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
               WorkflowEmailWizard wizard = new WorkflowEmailWizard(awa);
               List<String> emails = new LinkedList<>();
               for (AtsUser user : isDirty().isTrue() ? getSelectedUsers() : getStoredAtsUsers()) {
                  emails.add(user.getEmail());
               }
               if (!emails.isEmpty()) {
                  wizard.getEmailableGroups().add(new EmailGroup(getLabel(), emails));
               }
               WizardDialog dialog = new WizardDialog(Displays.getActiveShell(), wizard);
               dialog.create();
               dialog.open();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

      });

   }

   private void handleModifyList() {
      try {
         UserCheckTreeDialog uld = new UserCheckTreeDialog("Select User(s)", "Select Users.",
            AtsApiService.get().getUserService().getUsers(Active.Active));
         Collection<AtsUser> atsUsers = getStoredAtsUsers();
         if (!atsUsers.isEmpty()) {
            uld.setInitialSelections(atsUsers);
         }
         if (uld.open() == Window.OK) {
            selectedUsers.clear();
            for (Object obj : uld.getResult()) {
               User user = (User) obj;
               AtsUser atsUser = AtsApiService.get().getUserService().getUserById(user);
               if (!AtsCoreUsers.isUnAssignedUser(atsUser)) {
                  selectedUsers.add(atsUser);
               }
            }
            listViewer.setInput(selectedUsers);
            notifyXModifiedListeners();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void saveToArtifact() {
      List<String> userIds = new LinkedList<>();
      for (AtsUser user : getSelectedUsers()) {
         if (!AtsCoreUsers.isUnAssignedUser(user)) {
            userIds.add(user.getUserId());
         }
      }
      if (userIds.isEmpty()) {
         awa.deleteAttributes(attributeType);
      } else {
         awa.setAttributeValues(attributeType, userIds);
      }
   }

   @Override
   public void revert() {
      selectedUsers.clear();
      selectedUsers.addAll(getStoredAtsUsers());
      listViewer.setInput(selectedUsers);
   }

   @Override
   public Result isDirty() {
      Result result = Result.FalseResult;
      if (isEditable()) {
         Collection<AtsUser> selectedUsers2 = getSelectedUsers();
         Collection<AtsUser> storedAtsUsers = getStoredAtsUsers();
         if (!Collections.isEqual(selectedUsers2, storedAtsUsers)) {
            result = new Result(true, attributeType + " is dirty");
         }
      }
      return result;
   }

   @Override
   public Artifact getArtifact() {
      return awa;
   }

   @Override
   public void refresh() {
      // TODO: Should this be simplified to only return an Object?  See setSelected method below.
      Artifact storedArt = getStored();
      if (storedArt != null) {
         setSelected(storedArt);
      }
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      this.awa = (AbstractWorkflowArtifact) artifact;
      this.attributeType = attributeType;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return this.attributeType;
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         try {
            if (getArtifact() != null && getAttributeType() != null) {
               XResultData rd = OseeValidator.getInstance().validate(IOseeValidator.SHORT, getArtifact(),
                  getAttributeType(), getSelectedUsers());
               if (rd.isErrors()) {
                  status = new Status(IStatus.ERROR, getClass().getSimpleName(), rd.toString());
               }
            }
         } catch (OseeCoreException ex) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error getting Artifact", ex);
         }
      }
      return status;
   }

}
