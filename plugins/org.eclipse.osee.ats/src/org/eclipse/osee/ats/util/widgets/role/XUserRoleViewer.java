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

package org.eclipse.osee.ats.util.widgets.role;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.review.UserRoleManager;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsUtilClient;
import org.eclipse.osee.ats.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.workflow.review.defect.ReviewDefectManager;
import org.eclipse.osee.ats.workflow.review.role.UserRoleError;
import org.eclipse.osee.ats.workflow.review.role.UserRoleValidator;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class XUserRoleViewer extends GenericXWidget implements IArtifactWidget, IArtifactEventListener {

   private UserRoleXViewer xViewer;
   private PeerToPeerReviewArtifact reviewArt;
   public final static String normalColor = "#EEEEEE";
   private ToolItem newUserRoleItem, deleteUserRoleItem;
   private Label extraInfoLabel;
   private ToolBar toolBar;
   private IAtsPeerReviewRoleManager roleMgr;

   private static Map<PeerToPeerReviewArtifact, Integer> tableHeight = new HashMap<>();

   public XUserRoleViewer() {
      super("Roles");
      OseeEventManager.addListener(this);
   }

   @Override
   public Artifact getArtifact() {
      return reviewArt;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      // Create Text Widgets
      if (isDisplayLabel() && !getLabel().equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
         if (getToolTip() != null) {
            labelWidget.setToolTipText(getToolTip());
         }
      }

      final Composite mainComp = new Composite(parent, SWT.BORDER);
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));
      mainComp.setLayout(ALayout.getZeroMarginLayout());
      if (toolkit != null) {
         toolkit.paintBordersFor(mainComp);
      }

      createTaskActionBar(mainComp);

      xViewer = new UserRoleXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this);
      xViewer.setContentProvider(new UserRoleContentProvider(xViewer));
      xViewer.setLabelProvider(new UserRoleLabelProvider(xViewer));
      xViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            refreshActionEnablement();
         }
      });
      new ActionContributionItem(xViewer.getCustomizeAction()).fill(toolBar, -1);

      if (toolkit != null) {
         toolkit.adapt(xViewer.getStatusLabel(), false, false);
      }

      refreshTableSize();
      // NOTE: Don't adapt the tree using xToolkit cause will loose xViewer's context menu

      final Sash sash = new Sash(parent, SWT.HORIZONTAL);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.heightHint = 3;
      sash.setLayoutData(gd);
      sash.setBackground(Displays.getSystemColor(SWT.COLOR_GRAY));
      sash.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event e) {
            Rectangle treeRect = xViewer.getTree().getClientArea();
            int newHeight = treeRect.height + e.y;
            setTableHeight(newHeight);
            refreshTableSize();
            mainComp.layout();
            xViewer.refresh();
            if (getForm(mainComp) != null) {
               getForm(mainComp).reflow(true);
            }
         }
      });

      loadTable();
   }

   private void refreshTableSize() {
      Tree tree = xViewer.getTree();
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = getTableHeight();
      tree.setLayout(ALayout.getZeroMarginLayout());
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);
   }

   public ScrolledForm getForm(Composite composite) {
      ScrolledForm form = null;
      if (composite == null) {
         return null;
      }
      if (composite instanceof ScrolledForm) {
         return (ScrolledForm) composite;
      }
      if (!(composite instanceof ScrolledForm)) {
         form = getForm(composite.getParent());
      }
      return form;
   }

   private int getTableHeight() {
      if (reviewArt != null && tableHeight.containsKey(reviewArt)) {
         return tableHeight.get(reviewArt);
      }
      return 100;
   }

   private void setTableHeight(int newHeight) {
      if (reviewArt != null) {
         if (newHeight < 100) {
            newHeight = 100;
         }
         tableHeight.put(reviewArt, newHeight);
      }
   }

   public void createTaskActionBar(Composite parent) {

      // Button composite for state transitions, etc
      Composite bComp = new Composite(parent, SWT.NONE);
      // bComp.setBackground(mainSComp.getDisplay().getSystemColor(SWT.COLOR_CYAN));
      bComp.setLayout(new GridLayout(2, false));
      bComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Composite actionComp = new Composite(bComp, SWT.NONE);
      actionComp.setLayout(new GridLayout());
      actionComp.setLayoutData(new GridData(GridData.END));

      toolBar = new ToolBar(actionComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);
      ToolItem item = null;

      newUserRoleItem = new ToolItem(toolBar, SWT.PUSH);
      newUserRoleItem.setImage(ImageManager.getImage(FrameworkImage.USER_ADD));
      newUserRoleItem.setToolTipText("New Role");
      newUserRoleItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleNewUserRole();
         }
      });

      deleteUserRoleItem = new ToolItem(toolBar, SWT.PUSH);
      deleteUserRoleItem.setImage(ImageManager.getImage(FrameworkImage.X_RED));
      deleteUserRoleItem.setToolTipText("Delete Role");
      deleteUserRoleItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleDeleteUserRole(false);
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(PluginUiImage.REFRESH));
      item.setToolTipText("Refresh Roles");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            loadTable();
         }
      });

      Composite labelComp = new Composite(bComp, SWT.NONE);
      labelComp.setLayout(new GridLayout());
      labelComp.setLayoutData(new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL));

      extraInfoLabel = new Label(labelComp, SWT.NONE);
      extraInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      extraInfoLabel.setText("");
      extraInfoLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));

      refreshActionEnablement();
   }

   public void refreshActionEnablement() {
      if (deleteUserRoleItem != null && !deleteUserRoleItem.isDisposed()) {
         deleteUserRoleItem.setEnabled(isEditable() && getSelectedUserRoleItems().size() > 0);
      }
      if (newUserRoleItem != null && !newUserRoleItem.isDisposed()) {
         newUserRoleItem.setEnabled(isEditable());
      }
   }

   public void loadTable() {
      try {
         if (reviewArt != null && xViewer != null) {
            xViewer.set(roleMgr.getUserRoles());
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      refresh();
   }

   public void handleDeleteUserRole(boolean persist) {
      final List<UserRole> items = getSelectedUserRoleItems();
      if (items.isEmpty()) {
         AWorkbench.popup("ERROR", "No Roles Selected");
         return;
      }
      StringBuilder builder = new StringBuilder();
      for (UserRole userRole : items) {
         builder.append("\"" + userRole.toString() + "\"\n");
      }

      boolean delete = MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
         "Delete Roles", "Are You Sure You Wish to Delete the Roles(s):\n\n" + builder.toString());
      if (delete) {
         try {
            IAtsChangeSet changes = AtsClientService.get().createChangeSet("Delete Review Roles");
            removeUserRoleHelper(items, changes);
            changes.execute();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private void removeUserRoleHelper(List<UserRole> items, IAtsChangeSet changes) {
      for (UserRole userRole : items) {
         roleMgr.removeUserRole(userRole);
         roleMgr.saveToArtifact(changes);
         xViewer.remove(userRole);
      }
      loadTable();
      notifyXModifiedListeners();
   }

   public void handleNewUserRole() {

      NewRoleDialog dialog = new NewRoleDialog();
      dialog.setReview(reviewArt);
      if (dialog.open() == Window.OK) {
         if (dialog.getRole() == null) {
            AWorkbench.popup("Role not selected");
            return;
         }
         try {
            if (dialog.getUsers().isEmpty()) {
               AWorkbench.popup("Users not selected");
               return;
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            return;
         }
         try {
            IAtsChangeSet changes = AtsClientService.get().createChangeSet("Add Review Roles");
            for (IAtsUser user : dialog.getUsers()) {
               UserRole userRole = new UserRole(dialog.getRole(), user);
               roleMgr.addOrUpdateUserRole(userRole);
               changes.add(reviewArt);
            }
            roleMgr.saveToArtifact(changes);
            changes.execute();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
         notifyXModifiedListeners();
         loadTable();
      }
   }

   @SuppressWarnings("rawtypes")
   public List<UserRole> getSelectedUserRoleItems() {
      List<UserRole> items = new ArrayList<>();
      if (xViewer == null) {
         return items;
      }
      if (xViewer.getSelection().isEmpty()) {
         return items;
      }
      Iterator i = ((IStructuredSelection) xViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         items.add((UserRole) obj);
      }
      return items;
   }

   @Override
   public Control getControl() {
      return xViewer.getTree();
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      xViewer.dispose();
   }

   @Override
   public void refresh() {
      if (xViewer == null || xViewer.getTree() == null || xViewer.getTree().isDisposed()) {
         return;
      }
      xViewer.refresh();
      validate();
      refreshActionEnablement();
   }

   @Override
   public IStatus isValid() {
      try {
         UserRoleError error = UserRoleValidator.isValid(reviewArt.getArtifact());
         if (error == UserRoleError.OneRoleEntryRequired) {
            extraInfoLabel.setText("At least one role entry is required. Select \"New Role\" to add.");
            extraInfoLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
            return new Status(IStatus.ERROR, getClass().getSimpleName(), "At least one role entry is required");
         }
         if (!error.isOK()) {
            extraInfoLabel.setText(
               error.getError() + " - Select \"New Role\" to add.  Select icon in cell to update value.");
            extraInfoLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
            return new Status(IStatus.ERROR, getClass().getSimpleName(), error.getError());
         }
         extraInfoLabel.setText("Select \"New Role\" to add.  Select icon in cell to update value.");
         extraInfoLabel.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Status(IStatus.ERROR, getClass().getSimpleName(),
            "Exception validating roles. See log for details. " + ex.getLocalizedMessage(), ex);
      }
      // Need this cause it removes all error items of this namespace
      return new Status(IStatus.OK, getClass().getSimpleName(), "");
   }

   @Override
   public boolean isEmpty() {
      return xViewer.getTree().getItemCount() == 0;
   }

   @Override
   public String toHTML(String labelFont) {
      StringBuffer html = new StringBuffer();
      try {
         html.append(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT, "Tasks"));
         html.append(AHTML.startBorderTable(100, normalColor, ""));
         html.append(
            AHTML.addHeaderRowMultiColumnTable(new String[] {"Role", "User", "Hours", "Major", "Minor", "Issues"}));
         ReviewDefectManager defectMgr = new ReviewDefectManager(reviewArt);
         for (UserRole item : roleMgr.getUserRoles()) {
            IAtsUser atsUser = UserRoleManager.getUser(item, AtsClientService.get());
            html.append(AHTML.addRowMultiColumnTable(new String[] {
               item.getRole().name(),
               atsUser.getName(),
               AtsUtil.doubleToI18nString(item.getHoursSpent()),
               defectMgr.getNumMajor(atsUser) + "",
               defectMgr.getNumMinor(atsUser) + "",
               defectMgr.getNumIssues(atsUser) + ""}));
         }
         html.append(AHTML.endBorderTable());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return "User Role Item Exception - " + ex.getLocalizedMessage();
      }
      return html.toString();
   }

   @Override
   public Object getData() {
      return xViewer.getInput();
   }

   public PeerToPeerReviewArtifact getReviewArt() {
      return reviewArt;
   }

   public void setReviewArt(PeerToPeerReviewArtifact reviewArt) {
      this.reviewArt = reviewArt;
      roleMgr = reviewArt.getRoleManager();
      if (xViewer != null) {
         loadTable();
      }
   }

   @Override
   public void setArtifact(Artifact artifact) {
      setReviewArt((PeerToPeerReviewArtifact) artifact);
   }

   @Override
   public void saveToArtifact() {
      // RoleViewer uses artifact as storage mechanism, nothing to save
   }

   @Override
   public Result isDirty() {
      // RoleViewer uses artifact as storage mechanism which already determines dirty
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      // Nothing to revert cause artifact will be reverted
   }

   @Override
   public Control getErrorMessageControl() {
      return labelWidget;
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return Arrays.asList(AtsUtilClient.getAtsBranchFilter(), AtsUtilClient.getReviewArtifactTypeEventFilter());
   }

   @Override
   public void handleArtifactEvent(final ArtifactEvent artifactEvent, Sender sender) {
      if (reviewArt == null || !artifactEvent.isHasEvent(reviewArt.getArtifact())) {
         return;
      }
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (xViewer == null || xViewer.getTree() == null || xViewer.getTree().isDisposed()) {
               return;
            }
            if (artifactEvent.isRelAddedChangedDeleted(reviewArt.getArtifact())) {
               loadTable();
            } else {
               refresh();
            }
         }
      });
   }

   public IAtsPeerReviewRoleManager getUserRoleMgr() {
      return roleMgr;
   }

}
