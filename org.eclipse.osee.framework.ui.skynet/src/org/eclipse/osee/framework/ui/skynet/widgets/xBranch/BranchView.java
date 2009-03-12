/*******************************************************************************
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

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.util.SkynetViews;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

/**
 * Displays persisted changes made to an artifact.
 * 
 * @author Jeff C. Phillips
 */
public class BranchView extends ViewPart implements IActionable, IBranchEventListener{

   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView";
   private BranchViewPresentationPreferences branchViewPresentationPreferences;
   private static String HELP_CONTEXT_ID = "BranchView";
   private XBranchWidget xBranchWidget;
   private BranchOptions[] branchOptions;

   private void setBranchOptions(BranchOptions[] options){
      if(options == null){
         return;
      }
      
      for(BranchOptions option : options){
         if(option == BranchOptions.FAVORITES_FIRST){
            setFavoritesFirst(true);
         } else if (option == BranchOptions.FLAT){
            setPresentation(true);
         } else if (option == BranchOptions.SHOW_MERGE_BRANCHES){
            setShowMergeBranches(true);
         }else if(option == BranchOptions.SHOW_TRANSACTIONS){
            setShowTransactions(true);
         }
      }
   }
   
   @Override
   public void dispose() {
      super.dispose();
      
      branchViewPresentationPreferences.setDisposed(true);
   }

   @Override
   public void setFocus() {
   }

   /*
    * @see IWorkbenchPart#createPartControl(Composite)
    */
   @Override
   public void createPartControl(Composite parent) {
      setPartName("Branch Manager");
      
      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));

      xBranchWidget = new XBranchWidget();
      xBranchWidget.setDisplayLabel(false);
      xBranchWidget.createWidgets(parent, 1);
      
      setBranchOptions(branchOptions);
      branchViewPresentationPreferences = new BranchViewPresentationPreferences(this);
      
      xBranchWidget.loadData();

      MenuManager menuManager = new MenuManager();
      menuManager.setRemoveAllWhenShown(true);
      menuManager.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            MenuManager menuManager = (MenuManager) manager;
            menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
         }
      });

      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      xBranchWidget.getXViewer().getTree().setMenu(
            menuManager.createContextMenu(xBranchWidget.getXViewer().getTree()));
      getSite().registerContextMenu(VIEW_ID, menuManager, xBranchWidget.getXViewer());

      getSite().setSelectionProvider(xBranchWidget.getXViewer());
      SkynetGuiPlugin.getInstance().setHelp(parent, HELP_CONTEXT_ID);
   }

   public String getActionDescription() {
      return "";
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, final int branchId) {
      if (branchModType == BranchEventType.Deleted) {
         Displays.ensureInDisplayThread(new Runnable() {
            public void run() {
               closeView();
            }
         });
         return;
      } else if (branchModType == BranchEventType.Committed) {
         Displays.ensureInDisplayThread(new Runnable() {
            public void run() {
               try {
                 xBranchWidget.refresh();
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
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

   private void closeView() {
      SkynetViews.closeView(VIEW_ID, getViewSite().getSecondaryId());
   }

   public void changeBranchPresentation(boolean flat){
      if(branchViewPresentationPreferences != null){
         branchViewPresentationPreferences.getViewPreference().putBoolean(BranchViewPresentationPreferences.FLAT_KEY, flat);
      }
   }
   
   protected void setPresentation(boolean flat) {
      xBranchWidget.setPresentation(flat);
   }
   
   protected void setFavoritesFirst(boolean favoritesFirst) {
      xBranchWidget.setFavoritesFirst(favoritesFirst);
   }

   protected void setShowMergeBranches(boolean showMergeBranches) {
      xBranchWidget.setShowMergeBranches(showMergeBranches);
   }

   protected void setShowTransactions(boolean showTransactions) {
      xBranchWidget.setShowTransactions(showTransactions);
   }
}