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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.IReviewArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class XUserRoleViewer extends XWidget implements IArtifactWidget, IFrameworkTransactionEventListener {

   private UserRoleXViewer xViewer;
   private IDirtiableEditor editor;
   private IReviewArtifact reviewArt;
   public final static String normalColor = "#EEEEEE";
   private static ToolItem newUserRoleItem, deleteUserRoleItem;
   private Label extraInfoLabel;

   /**
    * @param label
    */
   public XUserRoleViewer() {
      super("Roles");

      OseeEventManager.addListener(this);
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#createWidgets(org.eclipse.swt.widgets.Composite, int)
    */
   @Override
   public void createWidgets(Composite parent, int horizontalSpan) {

      // Create Text Widgets
      if (displayLabel && !label.equals("")) {
         labelWidget = new Label(parent, SWT.NONE);
         labelWidget.setText(label + ":");
         if (toolTip != null) {
            labelWidget.setToolTipText(toolTip);
         }
      }

      Composite mainComp = new Composite(parent, SWT.BORDER);
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));
      mainComp.setLayout(ALayout.getZeroMarginLayout());
      if (toolkit != null) toolkit.paintBordersFor(mainComp);

      createTaskActionBar(mainComp);

      xViewer = new UserRoleXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this);
      xViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      xViewer.setContentProvider(new UserRoleContentProvider(xViewer));
      xViewer.setLabelProvider(new UserRoleLabelProvider(xViewer));
      xViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
          */
         public void selectionChanged(SelectionChangedEvent event) {
            refreshActionEnablement();
         }
      });

      if (toolkit != null) toolkit.adapt(xViewer.getStatusLabel(), false, false);

      Tree tree = xViewer.getTree();
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = 100;
      tree.setLayout(ALayout.getZeroMarginLayout());
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);
      // NOTE: Don't adapt the tree using xToolkit cause will loose xViewer's context menu

      (new Label(mainComp, SWT.None)).setText("Select \"New Role\" to add.  Select icon in cell to update value.");
      loadTable();
   }

   public void createTaskActionBar(Composite parent) {

      // Button composite for state transitions, etc
      Composite bComp = new Composite(parent, SWT.NONE);
      // bComp.setBackground(mainSComp.getDisplay().getSystemColor(SWT.COLOR_CYAN));
      bComp.setLayout(new GridLayout(2, false));
      bComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Composite leftComp = new Composite(bComp, SWT.NONE);
      leftComp.setLayout(new GridLayout());
      leftComp.setLayoutData(new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL));

      extraInfoLabel = new Label(leftComp, SWT.NONE);
      extraInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      extraInfoLabel.setText("");
      extraInfoLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));

      Composite rightComp = new Composite(bComp, SWT.NONE);
      rightComp.setLayout(new GridLayout());
      rightComp.setLayoutData(new GridData(GridData.END));

      ToolBar toolBar = new ToolBar(rightComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);
      ToolItem item = null;

      newUserRoleItem = new ToolItem(toolBar, SWT.PUSH);
      newUserRoleItem.setImage(AtsPlugin.getInstance().getImage("userAdd.gif"));
      newUserRoleItem.setToolTipText("New Role");
      newUserRoleItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleNewUserRole();
         }
      });

      deleteUserRoleItem = new ToolItem(toolBar, SWT.PUSH);
      deleteUserRoleItem.setImage(SkynetGuiPlugin.getInstance().getImage("redRemove.gif"));
      deleteUserRoleItem.setToolTipText("Delete Role");
      deleteUserRoleItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleDeleteUserRole(false);
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("refresh.gif"));
      item.setToolTipText("Refresh Roles");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            loadTable();
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("customize.gif"));
      item.setToolTipText("Customize Table");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            xViewer.getCustomizeMgr().handleTableCustomization();
         }
      });

      refreshActionEnablement();
   }

   public void refreshActionEnablement() {
      deleteUserRoleItem.setEnabled(editable && getSelectedUserRoleItems().size() > 0);
      newUserRoleItem.setEnabled(editable);
   }

   public void loadTable() {
      try {
         if (reviewArt != null && xViewer != null) {
            xViewer.set(reviewArt.getUserRoleManager().getUserRoles());
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
      refresh();
   }

   public void handleDeleteUserRole(boolean persist) {
      final List<UserRole> items = getSelectedUserRoleItems();
      if (items.size() == 0) {
         AWorkbench.popup("ERROR", "No Roles Selected");
         return;
      }
      StringBuilder builder = new StringBuilder();
      for (UserRole userRole : items)
         builder.append("\"" + userRole.toString() + "\"\n");

      boolean delete =
            MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Delete Roles",
                  "Are You Sure You Wish to Delete the Roles(s):\n\n" + builder.toString());
      if (delete) {
         try {
            SkynetTransaction transaction = new SkynetTransaction(reviewArt.getArtifact().getBranch());
            removeUserRoleHelper(items, persist, transaction);
            transaction.execute();
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
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
         SkynetTransaction transaction = new SkynetTransaction(reviewArt.getArtifact().getBranch());
         reviewArt.getUserRoleManager().addOrUpdateUserRole(new UserRole(), false, transaction);
         transaction.execute();
         notifyXModifiedListeners();
         loadTable();
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   @SuppressWarnings("unchecked")
   public ArrayList<UserRole> getSelectedUserRoleItems() {
      ArrayList<UserRole> items = new ArrayList<UserRole>();
      if (xViewer == null) return items;
      if (xViewer.getSelection().isEmpty()) return items;
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
      if (xViewer == null || xViewer.getTree() == null || xViewer.getTree().isDisposed()) return;
      xViewer.refresh();
      setLabelError();
      refreshActionEnablement();
   }

   @Override
   public Result isValid() {
      try {
         if (isRequiredEntry() && xViewer.getTree().getItemCount() == 0) {
            extraInfoLabel.setText("At least one role entry is required");
            return new Result("At least one role entry is required");
         }
         Result result = reviewArt.isUserRoleValid();
         if (result.isFalse()) {
            extraInfoLabel.setText(result.getText());
            return result;
         }
         extraInfoLabel.setText("");
         return Result.TrueResult;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return new Result("Exception validating roles. See log for details. " + ex.getLocalizedMessage());
      }
   }

   @Override
   public void setXmlData(String str) {
   }

   @Override
   public String getXmlData() {
      return null;
   }

   @Override
   public String toHTML(String labelFont) {
      if (getXViewer().getTree().getItemCount() == 0) return "";
      StringBuffer html = new StringBuffer();
      try {
         html.append(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT, "Tasks"));
         html.append(AHTML.startBorderTable(100, normalColor, ""));
         html.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Role", "User", "Hours", "Major", "Minor",
               "Issues"}));
         for (UserRole item : reviewArt.getUserRoleManager().getUserRoles()) {
            html.append(AHTML.addRowMultiColumnTable(new String[] {item.getRole().name(), item.getUser().getName(),
                  item.getHoursSpentStr(), reviewArt.getUserRoleManager().getNumMajor(item.getUser()) + "",
                  reviewArt.getUserRoleManager().getNumMinor(item.getUser()) + "",
                  reviewArt.getUserRoleManager().getNumIssues(item.getUser()) + ""}));
         }
         html.append(AHTML.endBorderTable());
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
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

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#getData()
    */
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

   @Override
   public boolean isEditable() {
      return editable;
   }

   @Override
   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   public IReviewArtifact getReviewArt() {
      return reviewArt;
   }

   public void setReviewArt(IReviewArtifact reviewArt) {
      this.reviewArt = reviewArt;
      if (xViewer != null) loadTable();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IDamWidget#setArtifact(org.eclipse.osee.framework.skynet.core.artifact.Artifact,
    *      java.lang.String)
    */
   public void setArtifact(Artifact artifact, String attrName) {
      setReviewArt((IReviewArtifact) artifact);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#saveToArtifact()
    */
   @Override
   public void saveToArtifact() throws OseeCoreException {
      // RoleViewer uses artifact as storage mechanism, nothing to save
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#isDirty()
    */
   @Override
   public Result isDirty() throws OseeCoreException {
      // RoleViewer uses artifact as storage mechanism which already determines dirty
      return Result.FalseResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#revert()
    */
   @Override
   public void revert() throws OseeCoreException {
      // Nothing to revert cause artifact will be reverted
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, org.eclipse.osee.framework.skynet.core.eventx.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, final FrameworkTransactionData transData) throws OseeCoreException {
      if (transData.getBranchId() != AtsPlugin.getAtsBranch().getBranchId()) return;
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (xViewer == null || xViewer.getTree() == null || xViewer.getTree().isDisposed()) return;
            if (transData.isRelAddedChangedDeleted(reviewArt.getArtifact())) {
               loadTable();
            } else
               refresh();
         }
      });
   }

}
