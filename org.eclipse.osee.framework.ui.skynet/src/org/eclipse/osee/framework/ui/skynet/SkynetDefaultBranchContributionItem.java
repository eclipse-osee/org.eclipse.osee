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
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.DefaultBranchChangedEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.skynet.branch.BranchLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.BranchSelectionDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Robert A. Fisher
 */
public class SkynetDefaultBranchContributionItem extends SkynetContributionItem {
   private static final String ID = "skynet.defaultBranch";
   private static final BranchPersistenceManager branchPersistenceManager = BranchPersistenceManager.getInstance();
   private static final Image ENABLED = SkynetGuiPlugin.getInstance().getImage("branch.gif");
   private static final Image DISABLED = ENABLED;
   private static final String ENABLED_TOOLTIP = "The default branch that Skynet is working from.";
   private static final String DISABLED_TOOLTIP = ENABLED_TOOLTIP;
   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();

   public SkynetDefaultBranchContributionItem() {
      super(ID, ENABLED, DISABLED, ENABLED_TOOLTIP, DISABLED_TOOLTIP, eventManager, 25);
      init();
      setActionHandler(new Action() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.jface.action.Action#run()
          */
         @Override
         public void run() {
            BranchSelectionDialog branchSelection = new BranchSelectionDialog("Set Default Branch");
            int result = branchSelection.open();
            if (result == Window.OK) {
               BranchPersistenceManager.getInstance().setDefaultBranch(branchSelection.getSelection());
            }
         }
      });
   }

   private void init() {
      updateStatus(true);
      updateInfo();
      eventManager.register(DefaultBranchChangedEvent.class, this);
   }

   private void updateInfo() {
      setText(branchPersistenceManager.getDefaultBranch().getDisplayName());
      setToolTipText(ENABLED_TOOLTIP + "\nDouble-click to change.");
      setImage(BranchLabelProvider.getBranchImage(branchPersistenceManager.getDefaultBranch()));
   }

   public static void addTo(IStatusLineManager manager) {
      for (IContributionItem item : manager.getItems())
         if (item instanceof SkynetDefaultBranchContributionItem) return;
      manager.add(new SkynetDefaultBranchContributionItem());
   }

   public static void addTo(ViewPart view, boolean update) {
      addTo(view.getViewSite().getActionBars().getStatusLineManager());

      if (update) view.getViewSite().getActionBars().updateActionBars();
   }

   public static void addTo(MultiPageEditorPart editorPart, boolean update) {
      addTo(editorPart.getEditorSite().getActionBars().getStatusLineManager());
      if (update) editorPart.getEditorSite().getActionBars().updateActionBars();
   }

   public void onEvent(Event event) {
      if (event instanceof DefaultBranchChangedEvent) updateInfo();
   }

   public boolean runOnEventInDisplayThread() {
      return true;
   }
}
