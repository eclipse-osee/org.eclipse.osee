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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.change.BranchTransactionUiData;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
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
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Karol M. Wilk
 * @author Jeff C. Phillips
 */
public class XBranchWidget extends GenericXWidget implements IOseeTreeReportProvider {
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
   private final BranchId selectedBranch;
   private final List<BranchSelectedListener> branchSelectedListeners;
   private BranchXViewerFactory branchXViewerFactory;
   private final IBranchWidgetMenuListener menuListener;

   public XBranchWidget(IBranchWidgetMenuListener menuListener) {
      this(false, false, null, menuListener);
   }

   public XBranchWidget(boolean filterRealTime, boolean searchRealTime, BranchId selectedBranch, IBranchWidgetMenuListener menuListener) {
      super(VIEW_ID);
      this.filterRealTime = filterRealTime;
      this.searchRealTime = searchRealTime;
      this.selectedBranch = selectedBranch;
      this.menuListener = menuListener;
      branchSelectedListeners = new CopyOnWriteArrayList<>();
      branchXViewerFactory = new BranchXViewerFactory(this);
   }

   public XBranchWidget(BranchXViewerFactory branchXViewerFactory, IBranchWidgetMenuListener menuListener) {
      this(false, false, null, menuListener);
      this.branchXViewerFactory = branchXViewerFactory;
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

   public void reveal(BranchId branch) {
      branchXViewer.reveal(branch);
      branchXViewer.setSelection(new StructuredSelection(branch), true);
      refresh();
   }

   public void setSelectedBranch(BranchId branch) {
      branchXViewer.reveal(branch);
      branchXViewer.setSelection(new StructuredSelection(branch), true);
   }

   @Override
   public void adaptControls(FormToolkit toolkit) {
      super.adaptControls(toolkit);
      toolkit.adapt(extraInfoLabel, true, true);
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

      branchXViewer = new BranchXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, branchXViewerFactory,
         this, filterRealTime, searchRealTime);
      branchXViewer.setMenuListener(menuListener);
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
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout layout = ALayout.getZeroMarginLayout(2, false);
      layout.marginLeft = 5;
      composite.setLayout(layout);
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      extraInfoLabel = new Label(composite, SWT.NONE);
      extraInfoLabel.setAlignment(SWT.LEFT);
      extraInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      extraInfoLabel.setText("\n");
      extraInfoLabel.setFont(FontManager.getCourierNew12Bold());

      if (branchXViewerFactory.isBranchManager()) {
         toolBar = new ToolBar(composite, SWT.FLAT);
         ToolItem item = null;

         item = new ToolItem(toolBar, SWT.PUSH);
         item.setImage(ImageManager.getImage(PluginUiImage.REFRESH));
         item.setToolTipText("Refresh");
         item.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               try {
                  BranchManager.refreshBranches();
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
               loadData();
            }
         });
      }
   }

   public ArrayList<BranchId> getSelectedBranches() {
      ArrayList<BranchId> items = new ArrayList<>();
      if (branchXViewer == null || branchXViewer.getSelection().isEmpty()) {
         return items;
      }
      Iterator<?> i = ((IStructuredSelection) branchXViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();

         if (obj instanceof BranchId) {
            items.add((BranchId) obj);
         }
      }
      return items;
   }

   @SuppressWarnings("rawtypes")
   public ArrayList<TransactionId> getSelectedTransactionRecords() {
      ArrayList<TransactionId> items = new ArrayList<>();
      if (branchXViewer == null || branchXViewer.getSelection().isEmpty()) {
         return items;
      }
      Iterator i = ((IStructuredSelection) branchXViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();

         if (obj instanceof TransactionId) {
            items.add((TransactionId) obj);
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

   public void setExtraInfoLabel(String message) {
      if (extraInfoLabel != null && !extraInfoLabel.isDisposed()) {
         extraInfoLabel.setText(message);
      }
   }

   public void loadData(final Object input) {
      final Object[] expandedBranches = getXViewer().getExpandedElements();

      setExtraInfoLabel(LOADING);

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
                     if (input instanceof BranchTransactionUiData) {
                        Object[] transactions = ((BranchTransactionUiData) input).getTransactions();
                        branchXViewer.setInput(transactions);
                     } else {
                        branchXViewer.setInput(input);
                     }
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

      public void onBranchSelected(BranchId branch);
   }
   public interface IBranchWidgetMenuListener {
      public void updateMenuActionsForTable(MenuManager mm);

   }

   @Override
   public String getEditorTitle() {
      if (selectedBranch != null) {
         IOseeBranch branch = BranchManager.getBranch(selectedBranch);
         return String.format("Table Report - Branch View %s", branch.getName());
      }
      return "Table Report - Branch View";
   }

   @Override
   public String getReportTitle() {
      return getEditorTitle();
   }

}
