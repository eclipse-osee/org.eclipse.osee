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

package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import java.sql.SQLException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.skynet.core.transactionChange.TransactionArtifactChange;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * @see ViewPart
 * @author Donald G. Dunne
 */
public class MergeView extends ViewPart implements IActionable {

   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView";
   private static String HELP_CONTEXT_ID = "MergeManagerView";
   private XMergeViewer xMergeViewer;
   private TransactionArtifactChange[] transactionArtifactChanges;
   private static final String INPUT = "MergeViewInput";

   /**
    * @author Donald G. Dunne
    */
   public MergeView() {
   }

   public static void openViewUpon(final TransactionArtifactChange[] transactionArtifactChanges) {
      Job job = new Job("Open Merge View") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            Displays.ensureInDisplayThread(new Runnable() {
               public void run() {
                  try {
                	  if(transactionArtifactChanges.length == 0){
                		 
                	  } else{
                		  IWorkbenchPage page = AWorkbench.getActivePage();
                		  MergeView mergeView = (MergeView)page.showView(MergeView.VIEW_ID, String.valueOf(transactionArtifactChanges[0].getArtifact().getBranch().getBranchId()),IWorkbenchPage.VIEW_ACTIVATE);
                		  mergeView.explore(transactionArtifactChanges);
                	  }
                  } catch (Exception ex) {
                     OSEELog.logException(SkynetGuiPlugin.class, ex, true);
                  }
               }
            });

            monitor.done();
            return Status.OK_STATUS;
         }
      };

      Jobs.startJob(job);
   }

   @Override
   public void dispose() {
      super.dispose();
   }

   public void setFocus() {
   }

   /*
    * @see IWorkbenchPart#createPartControl(Composite)
    */
   public void createPartControl(Composite parent) {
      /*
       * Create a grid layout object so the text and treeviewer are layed out the way I want.
       */
      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));

      xMergeViewer = new XMergeViewer();
      xMergeViewer.setDisplayLabel(false);
      xMergeViewer.createWidgets(parent, 1);
      
      
      
      try {
         if (transactionArtifactChanges != null) xMergeViewer.setBranch(transactionArtifactChanges);
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }

      SkynetGuiPlugin.getInstance().setHelp(parent, HELP_CONTEXT_ID);

   }

   public void explore(TransactionArtifactChange[] transactionArtifactChanges) {
      this.transactionArtifactChanges = transactionArtifactChanges;
      try {
         if (transactionArtifactChanges != null) xMergeViewer.setBranch(transactionArtifactChanges);
         setPartName("Commit Manager: " +"");
         
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   public String getActionDescription() {
      return "";
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
//
//      try {
//         if (memento != null) {
//            memento = memento.getChild(INPUT);
//            if (memento != null) {
////               int artId = memento.getInteger("artId");
////               if (artId > 0) {
//                  int branchId = memento.getInteger("branchId");
//                  if (branchId > 0) {
//                     Branch branch = BranchPersistenceManager.getInstance().getBranch(branchId);
//                     if (branch != null) {
////                        Artifact artifact = ArtifactPersistenceManager.getInstance().getArtifactFromId(artId, branch);
////                        if (artifact != null) 
//                        	explore(branch);
//                     }
//                  }
////               }
//            }
//         }
//      } catch (Exception ex) {
//         OSEELog.logWarning(getClass(), "Merge View error on init: " + ex.getLocalizedMessage(), false);
//      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
    */
   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
//      try {
//         memento = memento.createChild(INPUT);
//         memento.putInteger("artId", branchArtifact.getArtifact().getArtId());
//         memento.putInteger("branchId", branch.getBranchId());
//      } catch (Exception ex) {
//         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
//      }
   }

}