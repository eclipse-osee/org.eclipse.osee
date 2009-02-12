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

package org.eclipse.osee.ats.util.widgets.defect;

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
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Disposition;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Severity;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
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
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class XDefectViewer extends XWidget implements IArtifactWidget, IFrameworkTransactionEventListener {

   private DefectXViewer xViewer;
   private IDirtiableEditor editor;
   private IReviewArtifact reviewArt;
   public final static String normalColor = "#EEEEEE";
   private static ToolItem newDefectItem, deleteDefectItem;
   private Label extraInfoLabel;

   /**
    * @param label
    */
   public XDefectViewer() {
      super("Defects");
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

      xViewer = new DefectXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this);
      xViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      xViewer.setContentProvider(new DefectContentProvider(xViewer));
      xViewer.setLabelProvider(new DefectLabelProvider(xViewer));
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

      (new Label(mainComp, SWT.None)).setText("Select \"New Defect\" to add.  Select icon in cell to update value or Alt-Left-Click to update field.");
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

      newDefectItem = new ToolItem(toolBar, SWT.PUSH);
      newDefectItem.setImage(SkynetGuiPlugin.getInstance().getImage("greenPlus.gif"));
      newDefectItem.setToolTipText("New Defect");
      newDefectItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleNewDefect();
         }
      });

      deleteDefectItem = new ToolItem(toolBar, SWT.PUSH);
      deleteDefectItem.setImage(SkynetGuiPlugin.getInstance().getImage("redRemove.gif"));
      deleteDefectItem.setToolTipText("Delete Defect");
      deleteDefectItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleDeleteDefect(false);
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("refresh.gif"));
      item.setToolTipText("Refresh Defects");
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

      createTaskActionBarPulldown(toolBar, rightComp);
      refreshActionEnablement();
   }

   public void refreshActionEnablement() {
      deleteDefectItem.setEnabled(editable && getSelectedDefectItems().size() > 0);
      newDefectItem.setEnabled(editable);
   }

   public void createTaskActionBarPulldown(final ToolBar toolBar, Composite composite) {
      final ToolItem dropDown = new ToolItem(toolBar, SWT.DROP_DOWN);
      final Menu menu = new Menu(composite);

      dropDown.addListener(SWT.Selection, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            if (event.detail == SWT.ARROW) {
               Rectangle rect = dropDown.getBounds();
               Point pt = new Point(rect.x, rect.y + rect.height);
               pt = toolBar.toDisplay(pt);
               menu.setLocation(pt.x, pt.y);
               menu.setVisible(true);
            }
         }
      });

      MenuItem item = new MenuItem(menu, SWT.PUSH);
      item.setText("Create Defects via simple list");
      item.setEnabled(editable);
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleImportDefectsViaList();
         }
      });

   }

   public void loadTable() {
      try {
         if (reviewArt != null && xViewer != null) {
            xViewer.set(reviewArt.getDefectManager().getDefectItems());
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      refresh();
   }

   public void handleImportDefectsViaList() {
      try {
         EntryDialog ed =
               new EntryDialog(Display.getCurrent().getActiveShell(), "Create Defects", null,
                     "Enter task titles, one per line.", MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
         ed.setFillVertically(true);
         if (ed.open() == 0) {
            SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
            for (String str : ed.getEntry().split("\n")) {
               str = str.replaceAll("\r", "");
               if (!str.equals("")) {
                  reviewArt.getDefectManager().addDefectItem(str, false, transaction);
               }
            }
            transaction.execute();
            loadTable();
            notifyXModifiedListeners();
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void handleDeleteDefect(boolean persist) {
      final List<DefectItem> items = getSelectedDefectItems();
      if (items.size() == 0) {
         AWorkbench.popup("ERROR", "No Defects Selected");
         return;
      }
      StringBuilder builder = new StringBuilder();
      for (DefectItem defectItem : items)
         builder.append("\"" + defectItem.getDescription() + "\"\n");

      boolean delete =
            MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                  "Delete Defects", "Are You Sure You Wish to Delete the Defects(s):\n\n" + builder.toString());
      if (delete) {
         try {
            SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
            deleteDefectHelper(items, persist, transaction);
            transaction.execute();
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private void deleteDefectHelper(List<DefectItem> items, boolean persist, SkynetTransaction transaction) {
      try {
         for (DefectItem defectItem : items) {
            reviewArt.getDefectManager().removeDefectItem(defectItem, persist, transaction);
            xViewer.remove(defectItem);
         }
         loadTable();
         notifyXModifiedListeners();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void handleNewDefect() {
      EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Create New Defect", null,
                  "Enter Defect Description", MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
      if (ed.open() == 0) {
         try {
            SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
            reviewArt.getDefectManager().addDefectItem(ed.getEntry(), false, transaction);
            transaction.execute();
            notifyXModifiedListeners();
            loadTable();
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   @SuppressWarnings("unchecked")
   public ArrayList<DefectItem> getSelectedDefectItems() {
      ArrayList<DefectItem> items = new ArrayList<DefectItem>();
      if (xViewer == null) return items;
      if (xViewer.getSelection().isEmpty()) return items;
      Iterator i = ((IStructuredSelection) xViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         items.add((DefectItem) obj);
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
            extraInfoLabel.setText("At least one defect entry is required");
            return new Result("At least one defect entry is required");
         }
         if (reviewArt != null) {
            for (DefectItem item : reviewArt.getDefectManager().getDefectItems()) {
               if (item.isClosed() == false || item.getDisposition() == Disposition.None || (item.getSeverity() == Severity.None && (item.getDisposition() != Disposition.Duplicate && item.getDisposition() != Disposition.Reject))) {
                  extraInfoLabel.setText("Review not complete until all items are marked for severity, disposition and closed");
                  return new Result(
                        "Review not complete until all items are marked for severity, disposition and closed");
               }

            }
         }
         extraInfoLabel.setText("");
         return Result.TrueResult;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return new Result("Exception validating defects. See log for details. " + ex);
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
         html.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Date", "User", "Location", "Description",
               "Severity", "Disposition", "Injection Activity", "Resolution", "Location", "Closted"}));
         for (DefectItem item : reviewArt.getDefectManager().getDefectItems()) {
            html.append(AHTML.addRowMultiColumnTable(new String[] {item.getCreatedDate(XDate.MMDDYY),
                  item.getUser().getName(), item.getLocation(), item.getDescription(), item.getSeverity().name(),
                  item.getDisposition().name(), item.getInjectionActivity().name(), item.getResolution(),
                  item.isClosed() + ""}));
         }
         html.append(AHTML.endBorderTable());
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         return "Defect Item Exception - " + ex.getLocalizedMessage();
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
   public DefectXViewer getXViewer() {
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
      // DefectViewer uses artifact as storage mechanism; nothing to do here
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#isDirty()
    */
   @Override
   public Result isDirty() throws OseeCoreException {
      // DefectViewer uses artifact as storage mechanism which already determines dirty
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
