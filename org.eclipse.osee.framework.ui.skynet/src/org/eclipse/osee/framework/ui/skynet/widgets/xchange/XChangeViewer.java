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

package org.eclipse.osee.framework.ui.skynet.widgets.xchange;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
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
 * @author Donald G. Dunne
 */
public class XChangeViewer extends XWidget implements IEventReceiver, IActionable {

   private ChangeXViewer xChangeViewer;
   public final static String normalColor = "#EEEEEE";
   private static final String LOADING = "Loading ...";
   private static final String NOT_CHANGES = "No changes were found";
   private Label extraInfoLabel;
   private Branch branch;
   private int transactionNumber;

   /**
    * @param label
    */
   public XChangeViewer() {
      super("Change Report");
      SkynetEventManager.getInstance().register(RemoteTransactionEvent.class, this);
      SkynetEventManager.getInstance().register(LocalTransactionEvent.class, this);
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

      xChangeViewer = new ChangeXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION, this);
      xChangeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      xChangeViewer.setContentProvider(new XChangeContentProvider(xChangeViewer));
      xChangeViewer.setLabelProvider(new XChangeLabelProvider(xChangeViewer));

      if (toolkit != null) toolkit.adapt(xChangeViewer.getStatusLabel(), false, false);

      Tree tree = xChangeViewer.getTree();
      GridData gridData = new GridData(GridData.FILL_BOTH);
      gridData.heightHint = 100;
      tree.setLayout(ALayout.getZeroMarginLayout());
      tree.setLayoutData(gridData);
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);

      new ChangeDragAndDrop(tree, ChangeXViewer.NAMESPACE);
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
         public void widgetSelected(SelectionEvent e) {
            setInputData(branch, transactionNumber);
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("customize.gif"));
      item.setToolTipText("Customize Table");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            xChangeViewer.getCustomize().handleTableCustomization();
         }
      });

      OseeAts.addButtonToEditorToolBar(this, SkynetGuiPlugin.getInstance(), toolBar, ChangeView.VIEW_ID,
            "Change Report");
   }

   public void loadTable() {
      refresh();
   }

   @SuppressWarnings("unchecked")
   public ArrayList<Branch> getSelectedBranches() {
      ArrayList<Branch> items = new ArrayList<Branch>();
      if (xChangeViewer == null) return items;
      if (xChangeViewer.getSelection().isEmpty()) return items;
      Iterator i = ((IStructuredSelection) xChangeViewer.getSelection()).iterator();
      while (i.hasNext()) {
         Object obj = i.next();
         items.add((Branch) obj);
      }
      return items;
   }

   @Override
   public Control getControl() {
      return xChangeViewer.getTree();
   }

   @Override
   public void dispose() {
      xChangeViewer.dispose();
   }

   @Override
   public void setFocus() {
      xChangeViewer.getTree().setFocus();
   }

   public void refresh() {
      xChangeViewer.refresh();
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
   public ChangeXViewer getXViewer() {
      return xChangeViewer;
   }

   public void onEvent(final Event event) {
      if (xChangeViewer == null || xChangeViewer.getTree() == null || xChangeViewer.getTree().isDisposed()) return;
      if (event instanceof TransactionEvent) {
         //TODO Add event 
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.jdk.core.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.widgets.XWidget#getData()
    */
   @Override
   public Object getData() {
      return xChangeViewer.getInput();
   }

   public void setInputData(final Branch branch, final int transactionNumber) {
      this.branch = branch;
      this.transactionNumber = transactionNumber;

      extraInfoLabel.setText(LOADING);

      Job job = new Job("Open Change View") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            final Change[] changes;
            final boolean hasBranch = branch != null;

            try {
               if (hasBranch) {
                  changes = RevisionManager.getInstance().getChangesPerBranch(branch).toArray(new Change[0]);
               } else {
                  changes =
                        RevisionManager.getInstance().getChangesPerTransaction(transactionNumber).toArray(new Change[0]);
               }

               Displays.ensureInDisplayThread(new Runnable() {
                  public void run() {
                     try {
                        if (changes.length == 0) {
                           extraInfoLabel.setText(NOT_CHANGES);
                           xChangeViewer.setChanges(changes);
                           xChangeViewer.refresh();
                        } else {
                           TransactionId transId =
                                 hasBranch ? null : TransactionIdManager.getInstance().getPossiblyEditableTransactionId(
                                       transactionNumber);
                           String infoLabel =
                                 String.format(
                                       "Changes %s to branch: %s\n%s",
                                       hasBranch ? "made" : "committed",
                                       hasBranch ? branch : "(" + transId.getTransactionNumber() + ") " + transId.getBranch(),
                                       hasBranch ? "" : "Comment: " + transId.getComment());
                           extraInfoLabel.setText(infoLabel);
                           xChangeViewer.setChanges(changes);
                           loadTable();
                        }
                     } catch (SQLException ex) {
                        OSEELog.logException(SkynetGuiPlugin.class, ex.getLocalizedMessage(), ex, false);
                     } catch (BranchDoesNotExist ex) {
                        OSEELog.logException(SkynetGuiPlugin.class, ex.getLocalizedMessage(), ex, false);
                     }
                  }
               });
            } catch (SQLException ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            } catch (OseeCoreException ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
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
   public class ChangeDragAndDrop extends SkynetDragAndDrop {

      public ChangeDragAndDrop(Tree tree, String viewId) {
         super(tree, viewId);
      }

      @Override
      public void performDragOver(DropTargetEvent event) {
         event.detail = DND.DROP_NONE;
      }

      @Override
      public Artifact[] getArtifacts() throws SQLException {
         IStructuredSelection selection = (IStructuredSelection) xChangeViewer.getSelection();
         ArrayList<Artifact> artifacts = new ArrayList<Artifact>();

         if (selection != null && !selection.isEmpty()) {
            for (Object object : selection.toArray()) {

               if (object instanceof IAdaptable) {
                  Artifact artifact = (Artifact) ((IAdaptable) object).getAdapter(Artifact.class);

                  if (artifact != null) {
                     artifacts.add(artifact);
                  }
               }
            }
         }
         return artifacts.toArray(new Artifact[artifacts.size()]);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ats.IActionable#getActionDescription()
    */
   @Override
   public String getActionDescription() {
      StringBuffer sb = new StringBuffer();
      if (branch != null) sb.append("\nBranch: " + branch);
      sb.append("\nTransaction Id: " + transactionNumber);
      return sb.toString();
   }

   /**
    * @return the transactionNumber
    */
   public int getTransactionNumber() {
      return transactionNumber;
   }

   public TransactionId getTransactionId() throws OseeCoreException, SQLException {
      return TransactionIdManager.getInstance().getPossiblyEditableTransactionId(getTransactionNumber());
   }

   /**
    * @return the branch
    */
   public Branch getBranch() {
      return branch;
   }
}
