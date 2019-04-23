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

package org.eclipse.osee.framework.ui.skynet.widgets.xHistory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialogWithBranchSelect;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
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
public class XHistoryWidget extends GenericXWidget {

   private HistoryXViewer xHistoryViewer;
   public final static String normalColor = "#EEEEEE";
   private static final String NO_HISTORY = "No History changes were found";
   protected Label extraInfoLabel;
   private Artifact artifact;
   private ToolBar toolBar;
   private Composite rightComp;
   private final Set<Long> shadedTransactions = new HashSet<>();
   private int numberTransactionsToShow = 25;
   private ToolItem show25Item, showXItem, showAllItem, show50Item;

   public XHistoryWidget() {
      super("History");
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
      mainComp.setLayoutData(new GridData(GridData.FILL_BOTH));
      mainComp.setLayout(ALayout.getZeroMarginLayout());
      if (toolkit != null) {
         toolkit.paintBordersFor(mainComp);
      }

      createTaskActionBar(mainComp);

      xHistoryViewer = new HistoryXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this);
      xHistoryViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      xHistoryViewer.setContentProvider(new XHistoryContentProvider(xHistoryViewer));
      xHistoryViewer.setLabelProvider(new XHistoryLabelProvider(xHistoryViewer));

      createToolBar();

      if (toolkit != null) {
         toolkit.adapt(xHistoryViewer.getStatusLabel(), false, false);
      }

      Tree tree = xHistoryViewer.getTree();
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = 100;
      tree.setLayout(ALayout.getZeroMarginLayout());
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);

      new HistoryDragAndDrop(tree, HistoryXViewerFactory.NAMESPACE);
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
      extraInfoLabel.setText("\n");

      rightComp = new Composite(bComp, SWT.NONE);
      rightComp.setLayout(new GridLayout());
      rightComp.setLayoutData(new GridData(GridData.END));
   }

   public void createToolBar() {
      toolBar = new ToolBar(rightComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);

      ToolItem item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(PluginUiImage.REFRESH));
      item.setToolTipText("Refresh");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            setInputData(artifact, true);
            onRefresh();
         }
      });

      show25Item = new ToolItem(toolBar, SWT.RADIO | SWT.BORDER);
      show25Item.setText(" 25");
      show25Item.setToolTipText("Show last 25 transactions.");
      show25Item.setSelection(true);
      show25Item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            numberTransactionsToShow = 25;
         }
      });

      show50Item = new ToolItem(toolBar, SWT.RADIO | SWT.BORDER);
      show50Item.setText(" 50");
      show50Item.setToolTipText("Show last 50 transactions.");
      show50Item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            numberTransactionsToShow = 50;
         }
      });

      showXItem = new ToolItem(toolBar, SWT.RADIO | SWT.BORDER);
      showXItem.setText(" #");
      showXItem.setToolTipText("Set number of transactions to show.");
      showXItem.addSelectionListener(showXSelectionAdapter);

      showAllItem = new ToolItem(toolBar, SWT.RADIO | SWT.BORDER);
      showAllItem.setText(" All");
      showAllItem.setToolTipText("Show All transactions.");
      showAllItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            numberTransactionsToShow = Integer.MAX_VALUE;
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(ImageManager.getImage(FrameworkImage.OPEN));
      item.setToolTipText("Load by Artifact ID");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            EntryDialogWithBranchSelect dialog =
               new EntryDialogWithBranchSelect("Open by Artifact ID", "Enter Artifact ID");
            if (dialog.open() == 0) {
               String artId = dialog.getEntry();
               Conditions.checkNotNullOrEmpty(artId, "Artifact ID");
               BranchId branch = dialog.getBranch();
               Conditions.checkNotNull(branch, "Branch");
               Artifact art =
                  ArtifactQuery.getArtifactFromId(ArtifactId.valueOf(artId), branch, DeletionFlag.EXCLUDE_DELETED);
               if (art != null) {
                  setInputData(art, true);
                  onRefresh();
               }
            }
         }
      });

      new ActionContributionItem(xHistoryViewer.getCustomizeAction()).fill(toolBar, -1);

      rightComp.layout();
      rightComp.getParent().layout();
   }

   private final SelectionAdapter showXSelectionAdapter = new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
         if (showXItem.getSelection()) {
            EntryDialog dialog = new EntryDialog("Transactions", "Enter number of transactions to show");
            if (dialog.open() == Window.OK) {
               if (Strings.isNumeric(dialog.getEntry())) {
                  numberTransactionsToShow = Integer.valueOf(dialog.getEntry());
                  showXItem.setText(" # (" + dialog.getEntry() + ")");
               }
            }
         }
      }
   };

   protected void onRefresh() {
      // Can be overridden by clients
   }

   public void loadTable() {
      refresh();
   }

   public ArrayList<BranchId> getSelectedBranches() {
      ArrayList<BranchId> items = new ArrayList<>();
      if (xHistoryViewer == null) {
         return items;
      }
      if (xHistoryViewer.getSelection().isEmpty()) {
         return items;
      }
      Iterator<?> i = ((IStructuredSelection) xHistoryViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         items.add((BranchId) obj);
      }
      return items;
   }

   @Override
   public Control getControl() {
      return xHistoryViewer.getTree();
   }

   @Override
   public void dispose() {
      super.dispose();
      if (xHistoryViewer != null) {
         xHistoryViewer.dispose();
      }
   }

   @Override
   public void refresh() {
      xHistoryViewer.refresh();
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

   /**
    * @return Returns the xViewer.
    */
   public HistoryXViewer getXViewer() {
      return xHistoryViewer;
   }

   @Override
   public Object getData() {
      return xHistoryViewer.getInput();
   }

   public void setInputData(final Artifact artifact, final boolean loadHistory) {
      this.artifact = artifact;
      String numberTransactionsToShowStr =
         numberTransactionsToShow == Integer.MAX_VALUE ? "All" : String.valueOf(numberTransactionsToShow);
      extraInfoLabel.setText(String.format("Loading %s Transactions...", numberTransactionsToShowStr));
      extraInfoLabel.setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
      extraInfoLabel.setFont(FontManager.getCourierNew12Bold());

      final int fNumnberTransactionsToShow = numberTransactionsToShow;
      final String fNumnberTransactionsToShowStr = numberTransactionsToShowStr;

      Job job = new Job("History: " + artifact.getName()) {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            final Collection<Change> changes = new ArrayList<>();

            try {
               if (loadHistory) {
                  changes.addAll(ChangeManager.getChangesPerArtifact(artifact, fNumnberTransactionsToShow, monitor));
               }

               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     if (loadHistory) {
                        String shortName = Strings.emptyString();
                        try {
                           shortName = artifact.getBranchToken().getShortName();
                        } catch (OseeCoreException ex) {
                           OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                        }
                        String infoLabel = NO_HISTORY;
                        if (!changes.isEmpty()) {
                           infoLabel = String.format("History (Id: %s Showing: %s): %s on branch: %s", artifact.getId(),
                              fNumnberTransactionsToShowStr, artifact.getName(), shortName);
                        }

                        if (Widgets.isAccessible(extraInfoLabel)) {
                           extraInfoLabel.setText(infoLabel);
                        }
                        if (Widgets.isAccessible(xHistoryViewer.getControl())) {
                           calculateShading(changes);
                           calculateTransactions(changes);
                           ((XHistoryLabelProvider) getXViewer().getLabelProvider()).calculateImages(changes);
                           xHistoryViewer.setInputXViewer(changes);
                        }
                     } else {
                        if (Widgets.isAccessible(extraInfoLabel)) {
                           extraInfoLabel.setText("Cleared on shut down - press refresh to reload");
                        }
                     }
                  }

               });
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job);
   }

   public void calculateTransactions(Collection<Change> changes) {
      Set<Long> ids = new HashSet<>();
      for (Change change : changes) {
         ids.add(change.getTxDelta().getEndTx().getId());
      }
      for (TransactionRecord transaction : TransactionManager.getTransactions(ids)) {
         xHistoryViewer.put(transaction.getId(), transaction);
      }
   }

   private void calculateShading(Collection<Change> changes) {
      shadedTransactions.clear();
      try {
         Set<Long> transactionIds = new TreeSet<>();
         for (Change change : changes) {
            long transId = change.getTxDelta().getEndTx().getId();
            transactionIds.add(transId);
         }

         /**
          * Loop through sorted transactionIds and store every other one in shadedTransactions collection. This allows
          * isShaded method to quickly return true or false for transactionId.
          */
         boolean shade = false;
         for (Long transId : transactionIds) {
            if (shade) {
               shadedTransactions.add(transId);
            }
            shade = !shade;
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

   /**
    * @return true if transactionId line should be shaded, false otherwise
    */
   public boolean isShaded(Long transactionId) {
      return shadedTransactions.contains(transactionId);
   }

   public class HistoryDragAndDrop extends SkynetDragAndDrop {

      public HistoryDragAndDrop(Tree tree, String viewId) {
         super(tree, viewId);
      }

      @Override
      public void performDragOver(DropTargetEvent event) {
         event.detail = DND.DROP_NONE;
      }

      @Override
      public Artifact[] getArtifacts() {
         IStructuredSelection selection = (IStructuredSelection) xHistoryViewer.getSelection();
         ArrayList<Artifact> artifacts = new ArrayList<>();

         if (selection != null && !selection.isEmpty()) {
            for (Object object : selection.toArray()) {

               if (object instanceof IAdaptable) {
                  Artifact artifact = ((IAdaptable) object).getAdapter(Artifact.class);

                  if (artifact != null) {
                     artifacts.add(artifact);
                  }
               }
            }
         }
         return artifacts.toArray(new Artifact[artifacts.size()]);
      }
   }

   @SuppressWarnings("rawtypes")
   public ArrayList<TransactionId> getSelectedTransactionRecords() {
      ArrayList<TransactionId> items = new ArrayList<>();
      if (xHistoryViewer == null) {
         return items;
      }
      if (xHistoryViewer.getSelection().isEmpty()) {
         return items;
      }
      Iterator i = ((IStructuredSelection) xHistoryViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();

         if (obj instanceof Change) {
            items.add(((Change) obj).getTxDelta().getEndTx());
         }
      }
      return items;
   }

}
