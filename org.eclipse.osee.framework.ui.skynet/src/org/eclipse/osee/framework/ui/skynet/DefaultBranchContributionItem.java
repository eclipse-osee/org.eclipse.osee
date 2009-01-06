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
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.branch.BranchLabelProvider;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Robert A. Fisher
 */
public class DefaultBranchContributionItem extends OseeContributionItem implements IBranchEventListener {
   private static final String ID = "skynet.defaultBranch";
   private static final Image ENABLED = SkynetGuiPlugin.getInstance().getImage("branch.gif");
   private static final String ENABLED_TOOLTIP =
         "The default branch that Skynet is working from.\nDouble-click to change.";
   private static final String DISABLED_TOOLTIP = ENABLED_TOOLTIP;

   private DefaultBranchContributionItem() {
      super(ID, 25);
      init();
   }

   private void init() {
      setActionHandler(new Action() {
         @Override
         public void run() {
            BranchSelectionDialog branchSelection = new BranchSelectionDialog("Set Default Branch", false);
            int result = branchSelection.open();
            if (result == Window.OK) {
               try {
                  Branch branch = branchSelection.getSelection();
                  if (branch == null) {
                     AWorkbench.popup("ERROR", "No Branch Selected");
                     return;
                  }
                  BranchManager.setDefaultBranch(branch);
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
      updateStatus(true);
      updateInfo();
      OseeEventManager.addListener(this);
   }

   private void updateInfo() {
      setText(BranchManager.getDefaultBranch().getDisplayName());
   }

   public static void addTo(IStatusLineManager manager) {
      boolean wasFound = false;
      for (IContributionItem item : manager.getItems()) {
         if (item instanceof DefaultBranchContributionItem) {
            wasFound = true;
            break;
         }
      }
      if (!wasFound) {
         manager.add(new DefaultBranchContributionItem());
      }
   }

   public static void addTo(ViewPart view, boolean update) {
      addTo(view.getViewSite().getActionBars().getStatusLineManager());
      if (update) view.getViewSite().getActionBars().updateActionBars();
   }

   public static void addTo(MultiPageEditorPart editorPart, boolean update) {
      addTo(editorPart.getEditorSite().getActionBars().getStatusLineManager());
      if (update) editorPart.getEditorSite().getActionBars().updateActionBars();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      if (branchModType == BranchEventType.DefaultBranchChanged) {
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               updateInfo();
            }
         });
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.ui.plugin.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeContributionItem#getDisabledImage()
    */
   @Override
   protected Image getDisabledImage() {
      return ENABLED;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeContributionItem#getDisabledToolTip()
    */
   @Override
   protected String getDisabledToolTip() {
      return DISABLED_TOOLTIP;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeContributionItem#getEnabledImage()
    */
   @Override
   protected Image getEnabledImage() {
      return BranchLabelProvider.getBranchImage(BranchManager.getDefaultBranch());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeContributionItem#getEnabledToolTip()
    */
   @Override
   protected String getEnabledToolTip() {
      return ENABLED_TOOLTIP;
   }

}
