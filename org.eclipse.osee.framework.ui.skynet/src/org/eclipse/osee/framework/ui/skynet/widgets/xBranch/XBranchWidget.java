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

package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Jeff C. Phillips
 */
public class XBranchWidget extends XWidget implements IActionable {
   private BranchXViewer branchXViewer;
   public final static String normalColor = "#EEEEEE";
   private static final String LOADING = "Loading ...";
   protected Label extraInfoLabel;
   private XBranchContentProvider branchContentProvider;
   XBranchLabelProvider xBranchLabelProvider;
   private FavoriteSorter sorter;
   private static final String VIEW_ID = "BrachView";
   private boolean filterRealTime;
   private boolean searchRealTime;

   
   public XBranchWidget(boolean filterRealTime, boolean searchRealTime) {
      this();
      
      this.filterRealTime = filterRealTime;
      this.searchRealTime = searchRealTime;
   }
   /**
    * @param label
    */
   public XBranchWidget() {
      super(VIEW_ID);
      
      this.filterRealTime = false;
      this.searchRealTime = false;
   }
   
   public void setBranchOptions(BranchOptions... options) {
      for (BranchOptions option : options) {

         switch (option) {
            case FAVORITES_FIRST:
               setFavoritesFirst(true);
               break;
            case FLAT:
               setPresentation(true);
               break;
            case SHOW_MERGE_BRANCHES:
               setShowMergeBranches(true);
               break;
            case SHOW_TRANSACTIONS:
               setShowMergeBranches(true);
               break;
            case SHOW_ARCHIVED:
               setShowMergeBranches(true);
               break;
         }
      }
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

      try {
         createTaskActionBar(mainComp);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      branchXViewer = new BranchXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this, filterRealTime, searchRealTime);
      branchXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      branchContentProvider = new XBranchContentProvider(branchXViewer);
      branchXViewer.setContentProvider(branchContentProvider);
      xBranchLabelProvider = new XBranchLabelProvider(branchXViewer);
      branchXViewer.setLabelProvider(xBranchLabelProvider);
      sorter = new FavoriteSorter(branchXViewer);
      branchXViewer.setSorter(sorter);

      if (toolkit != null) toolkit.adapt(branchXViewer.getStatusLabel(), false, false);

      Tree tree = branchXViewer.getTree();
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = 100;
      tree.setLayout(ALayout.getZeroMarginLayout());
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);
   }

   public void createTaskActionBar(Composite parent) throws OseeCoreException {

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
      extraInfoLabel.setText("\n");

      Composite rightComp = new Composite(bComp, SWT.NONE);
      rightComp.setLayout(new GridLayout());
      rightComp.setLayoutData(new GridData(GridData.END));

      ToolBar toolBar = new ToolBar(rightComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);
      ToolItem item = null;

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("refresh.gif"));
      item.setToolTipText("Refresh");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            loadData();
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("customize.gif"));
      item.setToolTipText("Customize Table");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            branchXViewer.getCustomizeMgr().handleTableCustomization();
         }
      });

      OseeAts.addButtonToEditorToolBar(this, SkynetGuiPlugin.getInstance(), toolBar, BranchView.VIEW_ID, "Branch");
   }

   public void loadTable() {
      refresh();
   }

   @SuppressWarnings("unchecked")
   public ArrayList<Branch> getSelectedBranches() {
      ArrayList<Branch> items = new ArrayList<Branch>();
      if (branchXViewer == null) return items;
      if (branchXViewer.getSelection().isEmpty()) return items;
      Iterator i = ((IStructuredSelection) branchXViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();

         if (obj instanceof Branch) {
            items.add((Branch) obj);
         }
      }
      return items;
   }

   @Override
   public Control getControl() {
      return branchXViewer.getTree();
   }

   @Override
   public void dispose() {
      branchXViewer.dispose();
   }

   @Override
   public void setFocus() {
      branchXViewer.getTree().setFocus();
   }

   @Override
   public void refresh() {
      branchXViewer.refresh();
      setLabelError();
   }

   @Override
   public Result isValid() {
      return Result.TrueResult;
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.simplePage("Unhandled");
   }

   /**
    * @return Returns the xViewer.
    */
   public BranchXViewer getXViewer() {
      return branchXViewer;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return branchXViewer.getInput();
   }

   public void loadData() {
      extraInfoLabel.setText(LOADING);

      Job job = new Job("Banch Manager") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {

            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {
                  extraInfoLabel.setText("");
                  branchXViewer.setInput(BranchManager.getInstance());
               }
            });
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getReportData()
    */
   @Override
   public String getReportData() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#getXmlData()
    */
   @Override
   public String getXmlData() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XWidget#setXmlData(java.lang.String)
    */
   @Override
   public void setXmlData(String str) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ats.IActionable#getActionDescription()
    */
   @Override
   public String getActionDescription() {
      return null;
   }

   /**
    * @param favoritesFirst
    */
   public void setFavoritesFirst(boolean favoritesFirst) {
      if (branchContentProvider != null) {
         sorter.setFavoritesFirst(favoritesFirst);
         refresh();
      }
   }

   /**
    * @param flat
    */
   public void setPresentation(boolean flat) {
      if (branchContentProvider != null) {
         branchContentProvider.setPresentation(flat);
         refresh();
      }
   }

   /**
    * @param showMergeBranches
    */
   public void setShowMergeBranches(boolean showMergeBranches) {
      if (branchContentProvider != null) {
         branchContentProvider.setShowMergeBranches(showMergeBranches);
         refresh();
      }
   }

   /**
    * @param showArchivedBranches
    */
   public void setShowArchivedBranches(boolean showArchivedBranches) {
      if (branchContentProvider != null) {
         branchContentProvider.setShowArchivedBranches(showArchivedBranches);
         refresh();
      }
   }

   /**
    * @param showTransactions
    */
   public void setShowTransactions(boolean showTransactions) {
      if (branchContentProvider != null) {
         branchContentProvider.setShowTransactions(showTransactions);
         refresh();
      }
   }

   /**
    * @param showTransactions
    */
   public void setShowWorkingBranchesOnly(boolean allowOnlyWorkingBranches) {
      if (branchContentProvider != null) {
         branchContentProvider.setShowOnlyWorkingBranches(allowOnlyWorkingBranches);
         refresh();
      }
   }

   /**
    * @param branch
    */
   public void reveal(Branch branch) {
      for (Object obj : ((XBranchContentProvider) branchXViewer.getContentProvider()).getAllElements(BranchManager.getInstance())) {
         if (obj instanceof Branch && (Branch) obj == branch) {
            branchXViewer.reveal(obj);
            branchXViewer.setSelection(new StructuredSelection(obj), true);
            refresh();
            return;
         }
      }
   }
}
