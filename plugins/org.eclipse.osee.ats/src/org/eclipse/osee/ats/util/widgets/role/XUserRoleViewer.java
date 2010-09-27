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
import org.eclipse.osee.ats.artifact.IReviewArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
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
public class XUserRoleViewer extends XWidget implements IArtifactWidget, IArtifactEventListener {

   private UserRoleXViewer xViewer;
   private IDirtiableEditor editor;
   private IReviewArtifact reviewArt;
   public final static String normalColor = "#EEEEEE";
   private ToolItem newUserRoleItem, deleteUserRoleItem;
   private Label extraInfoLabel;
   private ToolBar toolBar;
   private static Map<IReviewArtifact, Integer> tableHeight = new HashMap<IReviewArtifact, Integer>();

   public XUserRoleViewer() {
      super("Roles");

      OseeEventManager.addListener(this);
   }

   @Override
   public Artifact getArtifact() {
      return reviewArt.getArtifact();
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
            xViewer.set(reviewArt.getUserRoleManager().getUserRoles());
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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

      boolean delete =
         MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Delete Roles",
            "Are You Sure You Wish to Delete the Roles(s):\n\n" + builder.toString());
      if (delete) {
         try {
            SkynetTransaction transaction =
               new SkynetTransaction(reviewArt.getArtifact().getBranch(), "Delete Review Roles");
            removeUserRoleHelper(items, persist, transaction);
            transaction.execute();
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private void removeUserRoleHelper(List<UserRole> items, boolean persist, SkynetTransaction transaction) throws OseeCoreException {
      for (UserRole userRole : items) {
         reviewArt.getUserRoleManager().removeUserRole(userRole, persist, transaction);
         xViewer.remove(userRole);
      }
      loadTable();
      notifyXModifiedListeners();
   }

   public void handleNewUserRole() {
      try {
         SkynetTransaction transaction = new SkynetTransaction(reviewArt.getArtifact().getBranch(), "Add Review Roles");
         reviewArt.getUserRoleManager().addOrUpdateUserRole(new UserRole(), false, transaction);
         transaction.execute();
         notifyXModifiedListeners();
         loadTable();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @SuppressWarnings("rawtypes")
   public ArrayList<UserRole> getSelectedUserRoleItems() {
      ArrayList<UserRole> items = new ArrayList<UserRole>();
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
   public void setFocus() {
      xViewer.getTree().setFocus();
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
         if (isRequiredEntry() && xViewer.getTree().getItemCount() == 0) {
            extraInfoLabel.setText("At least one role entry is required. Select \"New Role\" to add.");
            extraInfoLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
            return new Status(IStatus.ERROR, getClass().getSimpleName(), "At least one role entry is required");
         }
         IStatus result = reviewArt.isUserRoleValid(getClass().getSimpleName());
         if (!result.isOK()) {
            extraInfoLabel.setText(result.getMessage() + " - Select \"New Role\" to add.  Select icon in cell to update value.");
            extraInfoLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
            return result;
         }
         extraInfoLabel.setText("Select \"New Role\" to add.  Select icon in cell to update value.");
         extraInfoLabel.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return new Status(IStatus.ERROR, getClass().getSimpleName(),
            "Exception validating roles. See log for details. " + ex.getLocalizedMessage(), ex);
      }
      // Need this cause it removes all error items of this namespace
      return new Status(IStatus.OK, getClass().getSimpleName(), "");
   }

   @Override
   public void setXmlData(String str) {
      // do nothing
   }

   @Override
   public String getXmlData() {
      return null;
   }

   @Override
   public String toHTML(String labelFont) {
      StringBuffer html = new StringBuffer();
      try {
         html.append(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT, "Tasks"));
         html.append(AHTML.startBorderTable(100, normalColor, ""));
         html.append(AHTML.addHeaderRowMultiColumnTable(new String[] {
            "Role",
            "User",
            "Hours",
            "Major",
            "Minor",
            "Issues"}));
         for (UserRole item : reviewArt.getUserRoleManager().getUserRoles()) {
            html.append(AHTML.addRowMultiColumnTable(new String[] {
               item.getRole().name(),
               item.getUser().getName(),
               item.getHoursSpentStr(),
               reviewArt.getUserRoleManager().getNumMajor(item.getUser()) + "",
               reviewArt.getUserRoleManager().getNumMinor(item.getUser()) + "",
               reviewArt.getUserRoleManager().getNumIssues(item.getUser()) + ""}));
         }
         html.append(AHTML.endBorderTable());
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "User Role Item Exception - " + ex.getLocalizedMessage();
      }
      return html.toString();
   }

   @Override
   public String getReportData() {
      return null;
   }

   /**
    * @return Returns the xViewer.
    */
   public UserRoleXViewer getXViewer() {
      return xViewer;
   }

   @Override
   public Object getData() {
      return xViewer.getInput();
   }

   public IDirtiableEditor getEditor() {
      return editor;
   }

   public void setEditor(IDirtiableEditor editor) {
      this.editor = editor;
   }

   public IReviewArtifact getReviewArt() {
      return reviewArt;
   }

   public void setReviewArt(IReviewArtifact reviewArt) {
      this.reviewArt = reviewArt;
      if (xViewer != null) {
         loadTable();
      }
   }

   @Override
   public void setArtifact(Artifact artifact) {
      setReviewArt((IReviewArtifact) artifact);
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
      return Arrays.asList(OseeEventManager.getCommonBranchFilter(), AtsUtil.getReviewArtifactTypeEventFilter());
   }

   @Override
   public void handleArtifactEvent(final ArtifactEvent artifactEvent, Sender sender) {
      if (!artifactEvent.isHasEvent(reviewArt.getArtifact())) {
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

}
