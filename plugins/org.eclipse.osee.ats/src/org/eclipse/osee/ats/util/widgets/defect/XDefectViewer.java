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
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Disposition;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Severity;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class XDefectViewer extends XWidget implements IArtifactWidget, IArtifactEventListener {

   private DefectXViewer xViewer;
   private IDirtiableEditor editor;
   private IReviewArtifact reviewArt;
   public final static String normalColor = "#EEEEEE";
   private static ToolItem newDefectItem, deleteDefectItem;
   private Label extraInfoLabel;
   private Composite parentComposite;
   private ToolBar toolBar;
   private static Map<IReviewArtifact, Integer> tableHeight = new HashMap<IReviewArtifact, Integer>();

   public XDefectViewer() {
      super("Defects");
      OseeEventManager.addListener(this);
   }

   @Override
   public Artifact getArtifact() {
      return reviewArt.getArtifact();
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      parentComposite = parent;
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

      xViewer = new DefectXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this);
      xViewer.setContentProvider(new DefectContentProvider(xViewer));
      xViewer.setLabelProvider(new DefectLabelProvider(xViewer));
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

      newDefectItem = new ToolItem(toolBar, SWT.PUSH);
      newDefectItem.setImage(ImageManager.getImage(FrameworkImage.GREEN_PLUS));
      newDefectItem.setToolTipText("New Defect");
      newDefectItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleNewDefect();
         }
      });

      deleteDefectItem = new ToolItem(toolBar, SWT.PUSH);
      deleteDefectItem.setImage(ImageManager.getImage(FrameworkImage.X_RED));
      deleteDefectItem.setToolTipText("Delete Defect");
      deleteDefectItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleDeleteDefect(false);
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(PluginUiImage.REFRESH));
      item.setToolTipText("Refresh Defects");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            loadTable();
         }
      });

      createTaskActionBarPulldown(toolBar, actionComp);

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
      deleteDefectItem.setEnabled(isEditable() && getSelectedDefectItems().size() > 0);
      newDefectItem.setEnabled(isEditable());
   }

   public void createTaskActionBarPulldown(final ToolBar toolBar, Composite composite) {
      final ToolItem dropDown = new ToolItem(toolBar, SWT.DROP_DOWN);
      dropDown.setImage(ImageManager.getImage(FrameworkImage.GEAR));
      final Menu menu = new Menu(composite);

      dropDown.addListener(SWT.Selection, new Listener() {
         @Override
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
      item.setEnabled(isEditable());
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleImportDefectsViaList();
         }
      });

   }

   private void handleExpandCollapseDefectTableList() {
      xViewer.refresh();
      if (getForm(parentComposite) != null) {
         getForm(parentComposite).reflow(true);
      }
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

   public void loadTable() {
      try {
         if (reviewArt != null && xViewer != null) {
            xViewer.set(reviewArt.getDefectManager().getDefectItems());
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      handleExpandCollapseDefectTableList();
      refresh();
   }

   public void handleImportDefectsViaList() {
      try {
         EntryDialog ed =
            new EntryDialog(Displays.getActiveShell(), "Create Defects", null, "Enter task titles, one per line.",
               MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
         ed.setFillVertically(true);
         if (ed.open() == 0) {
            SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Import Review Defects");
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
      if (items.isEmpty()) {
         AWorkbench.popup("ERROR", "No Defects Selected");
         return;
      }
      StringBuilder builder = new StringBuilder();
      for (DefectItem defectItem : items) {
         builder.append("\"" + defectItem.getDescription() + "\"\n");
      }

      boolean delete =
         MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Delete Defects",
            "Are You Sure You Wish to Delete the Defects(s):\n\n" + builder.toString());
      if (delete) {
         try {
            SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Delete Review Defects");
            deleteDefectHelper(items, persist, transaction);
            transaction.execute();
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
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
      NewDefectDialog ed = new NewDefectDialog();
      ed.setFillVertically(true);
      if (ed.open() == 0) {
         try {
            SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Add Review Defect");
            DefectItem item = new DefectItem();
            item.setDescription(ed.getEntry());
            if (ed.getSeverity() != null) {
               item.setSeverity(ed.getSeverity());
            }
            if (Strings.isValid(ed.getEntry2())) {
               item.setLocation(ed.getEntry2());
            }
            reviewArt.getDefectManager().addOrUpdateDefectItem(item, false, transaction);
            transaction.execute();
            notifyXModifiedListeners();
            loadTable();
         } catch (Exception ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   @SuppressWarnings("rawtypes")
   public ArrayList<DefectItem> getSelectedDefectItems() {
      ArrayList<DefectItem> items = new ArrayList<DefectItem>();
      if (xViewer == null) {
         return items;
      }
      if (xViewer.getSelection().isEmpty()) {
         return items;
      }
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
            extraInfoLabel.setText("At least one defect entry is required.  Select \"New Defect\" to add.");
            extraInfoLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
            return new Status(IStatus.ERROR, getClass().getSimpleName(), "At least one defect entry is required");
         }
         if (reviewArt != null) {
            for (DefectItem item : reviewArt.getDefectManager().getDefectItems()) {
               if (item.isClosed() == false || item.getDisposition() == Disposition.None || item.getSeverity() == Severity.None && item.getDisposition() != Disposition.Duplicate && item.getDisposition() != Disposition.Reject) {
                  extraInfoLabel.setText("All items must be marked for severity, disposition and closed.  Select icon in cell or right-click to update field.");
                  extraInfoLabel.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
                  return new Status(IStatus.ERROR, getClass().getSimpleName(),
                     "Review not complete until all items are marked for severity, disposition and closed");
               }
            }
         }
         extraInfoLabel.setText("Select \"New Defect\" to add.  Select icon in cell or right-click to update field.");
         extraInfoLabel.setForeground(Displays.getSystemColor(SWT.COLOR_BLACK));
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         return new Status(IStatus.ERROR, getClass().getSimpleName(),
            "Exception validating defects. See log for details. " + ex);
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
      if (getXViewer().getTree().getItemCount() == 0) {
         return "";
      }
      StringBuffer html = new StringBuffer();
      try {
         html.append(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT, "Tasks"));
         html.append(AHTML.startBorderTable(100, normalColor, ""));
         html.append(AHTML.addHeaderRowMultiColumnTable(new String[] {
            "Date",
            "User",
            "Location",
            "Description",
            "Severity",
            "Disposition",
            "Injection Activity",
            "Resolution",
            "Location",
            "Closted"}));
         for (DefectItem item : reviewArt.getDefectManager().getDefectItems()) {
            html.append(AHTML.addRowMultiColumnTable(new String[] {
               DateUtil.getMMDDYY(item.getDate()),
               item.getUser().getName(),
               item.getLocation(),
               item.getDescription(),
               item.getSeverity().name(),
               item.getDisposition().name(),
               item.getInjectionActivity().name(),
               item.getResolution(),
               item.isClosed() + ""}));
         }
         html.append(AHTML.endBorderTable());
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
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
      // DefectViewer uses artifact as storage mechanism; nothing to do here
   }

   @Override
   public Result isDirty() {
      // DefectViewer uses artifact as storage mechanism which already determines dirty
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
