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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.HttpClientMessage;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericXWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
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
 * @author Karol M. Wilk
 * @author Jeff C. Phillips
 */
public class XBranchWidget extends GenericXWidget {
   private BranchXViewer branchXViewer;
   public final static String normalColor = "#EEEEEE";
   private static final String LOADING = "Loading ...";
   protected Label extraInfoLabel;
   private XBranchContentProvider branchContentProvider;
   private XBranchLabelProvider xBranchLabelProvider;
   private FavoriteSorter sorter;
   private static final String VIEW_ID = "BrachView";
   private final boolean filterRealTime;
   private final boolean searchRealTime;
   private ToolBar toolBar;
   private final IOseeBranch selectedBranch;
   private final List<BranchSelectedListener> branchSelectedListeners;

   public XBranchWidget() {
      this(false, false, null);
   }

   public XBranchWidget(boolean filterRealTime, boolean searchRealTime, IOseeBranch selectedBranch) {
      super(VIEW_ID);
      this.filterRealTime = filterRealTime;
      this.searchRealTime = searchRealTime;
      this.selectedBranch = selectedBranch;
      branchSelectedListeners = new CopyOnWriteArrayList<BranchSelectedListener>();
   }

   public void setBranchOptions(boolean state, BranchOptionsEnum... options) {
      for (BranchOptionsEnum option : options) {
         if (branchContentProvider != null) {
            switch (option) {
               case FAVORITE_KEY:
                  sorter.setFavoritesFirst(state);
                  break;
               case FLAT_KEY:
                  branchContentProvider.setPresentation(state);
                  break;
               case SHOW_MERGE_BRANCHES:
                  branchContentProvider.setShowMergeBranches(state);
                  break;
               case SHOW_TRANSACTIONS:
                  branchContentProvider.setShowTransactions(state);
                  break;
               case SHOW_ARCHIVED_BRANCHES:
                  branchContentProvider.setShowArchivedBranches(state);
                  break;
               case SHOW_WORKING_BRANCHES_ONLY:
                  branchContentProvider.setShowOnlyWorkingBranches(state);
                  break;
            }
         }
      }
      refresh();
   }

   public void reveal(Branch branch) {
      branchXViewer.reveal(branch);
      branchXViewer.setSelection(new StructuredSelection(branch), true);
      refresh();
   }

   public void setSelectedBranch(Branch branch) {
      branchXViewer.reveal(branch);
      branchXViewer.setSelection(new StructuredSelection(branch), true);
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

      Composite mainComp = new Composite(parent, SWT.BORDER);
      mainComp.setLayout(ALayout.getZeroMarginLayout());
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));
      if (toolkit != null) {
         toolkit.paintBordersFor(mainComp);
      }

      createTaskActionBar(mainComp);

      branchXViewer =
         new BranchXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this, filterRealTime, searchRealTime);
      branchXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      branchContentProvider = new XBranchContentProvider(branchXViewer);
      branchXViewer.setContentProvider(branchContentProvider);
      xBranchLabelProvider = new XBranchLabelProvider(branchXViewer);
      branchXViewer.setLabelProvider(xBranchLabelProvider);
      sorter = new FavoriteSorter(branchXViewer);
      branchXViewer.setSorter(sorter);

      if (toolkit != null) {
         toolkit.adapt(branchXViewer.getStatusLabel(), false, false);
      }
      new ActionContributionItem(branchXViewer.getCustomizeAction()).fill(toolBar, -1);

      Tree tree = branchXViewer.getTree();
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = 100;
      tree.setLayout(ALayout.getZeroMarginLayout());
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);
   }

   public void createTaskActionBar(Composite parent) {
      // Button composite for state transitions, etc
      Composite composite = new Composite(parent, SWT.NONE);
      //      composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_CYAN));
      GridLayout layout = ALayout.getZeroMarginLayout(2, false);
      layout.marginLeft = 5;
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      extraInfoLabel = new Label(composite, SWT.NONE);
      extraInfoLabel.setAlignment(SWT.LEFT);
      extraInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      extraInfoLabel.setText("\n");

      toolBar = new ToolBar(composite, SWT.FLAT);
      ToolItem item = null;

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(PluginUiImage.REFRESH));
      item.setToolTipText("Refresh");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               refreshServerBranchCache();
               BranchManager.refreshBranches();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            loadData();
         }
      });

   }

   private void refreshServerBranchCache() {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", Function.RELOAD_BRANCH_CACHE.name());
      HttpClientMessage.send(OseeServerContext.BRANCH_CONTEXT, parameters, null, null, null);
   }

   public ArrayList<Branch> getSelectedBranches() {
      ArrayList<Branch> items = new ArrayList<Branch>();
      if (branchXViewer == null || branchXViewer.getSelection().isEmpty()) {
         return items;
      }
      Iterator<?> i = ((IStructuredSelection) branchXViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();

         if (obj instanceof Branch) {
            items.add((Branch) obj);
         }
      }
      return items;
   }

   @SuppressWarnings("rawtypes")
   public ArrayList<TransactionRecord> getSelectedTransactionRecords() {
      ArrayList<TransactionRecord> items = new ArrayList<TransactionRecord>();
      if (branchXViewer == null || branchXViewer.getSelection().isEmpty()) {
         return items;
      }
      Iterator i = ((IStructuredSelection) branchXViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();

         if (obj instanceof TransactionRecord) {
            items.add((TransactionRecord) obj);
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
      super.dispose();
      if (branchXViewer != null) {
         branchXViewer.dispose();
      }
   }

   @Override
   public void refresh() {
      loadData();
      branchXViewer.refresh();
      validate();
   }

   @Override
   public IStatus isValid() {
      return Status.OK_STATUS;
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.simplePage("Unhandled");
   }

   public BranchXViewer getXViewer() {
      return branchXViewer;
   }

   @Override
   public Object getData() {
      return branchXViewer.getInput();
   }

   public void loadData() {
      loadData(((XBranchContentProvider) branchXViewer.getContentProvider()).getBranchManagerChildren());
   }

   public void loadData(final Object input) {
      final Object[] expandedBranches = getXViewer().getExpandedElements();

      if (extraInfoLabel != null && !extraInfoLabel.isDisposed()) {
         extraInfoLabel.setText(LOADING);
      }

      Job job = new Job("Banch Manager") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {

            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  if (extraInfoLabel != null && !extraInfoLabel.isDisposed()) {
                     extraInfoLabel.setText("");
                  }
                  if (branchXViewer != null && branchXViewer.getTree() != null && !branchXViewer.getTree().isDisposed()) {
                     branchXViewer.setInput(input);
                     getXViewer().setExpandedElements(expandedBranches);
                     if (selectedBranch != null) {
                        getXViewer().reveal(selectedBranch);
                        getXViewer().setSelection(new StructuredSelection(selectedBranch), true);
                        for (BranchSelectedListener listener : branchSelectedListeners) {
                           listener.onBranchSelected(selectedBranch);
                        }
                     }
                  }
               }
            });
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job);
   }

   @Override
   public boolean isEmpty() {
      return branchXViewer.getTree().getItemCount() == 0;
   }

   public void addBranchSelectedListener(BranchSelectedListener listener) {
      branchSelectedListeners.add(listener);
   }

   public void removeBranchSelectedListener(BranchSelectedListener listener) {
      branchSelectedListeners.remove(listener);
   }

   public interface BranchSelectedListener {

      public void onBranchSelected(IOseeBranch branch);
   }

}
