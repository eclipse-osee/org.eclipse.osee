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

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.NodeChangeEvent;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.FlatPresentationHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.HierarchicalPresentationHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.ShowArchivedBranchHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.ShowFavoriteBranchesFirstHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.ShowMergeBranchPresentationHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.ShowTransactionPresentationHandler;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author Jeff C. Phillips
 */
public class BranchViewPresentationPreferences {
   public static final String FAVORITE_KEY = "favorites_first";
   public static final String SHOW_TRANSACTIONS = "show_transactions";
   public static final String SHOW_MERGE_BRANCHES = "show_merge_branches";
   public static final String SHOW_ARCHIVED_BRANCHES = "show_archived_branches";
   public static final String FLAT_KEY = "flat";
   public static final String BRANCH_ID = "branchId";

   private final IPreferencesService preferencesService;
   private IPreferenceChangeListener preferenceChangeListener;
   private BranchView branchView;
   private boolean disposed;

   public BranchViewPresentationPreferences(BranchView branchView) {
      this.preferencesService = Platform.getPreferencesService();
      this.preferenceChangeListener = null;
      this.branchView = branchView;
      this.disposed = false;

      IEclipsePreferences instanceNode =
            (IEclipsePreferences) preferencesService.getRootNode().node(InstanceScope.SCOPE);

      try {
         if (instanceNode.nodeExists(BranchView.VIEW_ID)) {
            ((IEclipsePreferences) instanceNode.node(BranchView.VIEW_ID)).addPreferenceChangeListener(singletonPreferenceChangeListener());
         }
      } catch (BackingStoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      instanceNode.addNodeChangeListener(new IEclipsePreferences.INodeChangeListener() {

         public void added(NodeChangeEvent event) {
            if (event.getChild().name().equals(BranchView.VIEW_ID)) {
               ((IEclipsePreferences) event.getChild()).addPreferenceChangeListener(singletonPreferenceChangeListener());
            }
         }

         public void removed(NodeChangeEvent event) {
            if (event.getChild().name().equals(BranchView.VIEW_ID)) {
               ((IEclipsePreferences) event.getChild()).removePreferenceChangeListener(singletonPreferenceChangeListener());
            }
         }
      });

      loadPreferences();
   }

   private synchronized IPreferenceChangeListener singletonPreferenceChangeListener() {
      if (preferenceChangeListener == null) {
         preferenceChangeListener = new IPreferenceChangeListener() {

            public void preferenceChange(PreferenceChangeEvent event) {
               if (disposed) {
                  ((IEclipsePreferences) event.getNode()).removePreferenceChangeListener(this);
               } else {
                  String propertyName = event.getKey();
                  
                  refreshCommands();
                  
                  if (propertyName.equals(FLAT_KEY)) {
                     setPresentation(getViewPreference().getBoolean(FLAT_KEY, true));
                  }
                  if (propertyName.equals(SHOW_TRANSACTIONS)) {
                     setShowTransactions(getViewPreference().getBoolean(SHOW_TRANSACTIONS, true));
                  }
                  if (propertyName.equals(SHOW_MERGE_BRANCHES)) {
                     setShowMergeBranches(getViewPreference().getBoolean(SHOW_MERGE_BRANCHES, true));
                  }
                  if (propertyName.equals(SHOW_ARCHIVED_BRANCHES)) {
                     setShowArchivedBranches(getViewPreference().getBoolean(SHOW_ARCHIVED_BRANCHES, true));
                  }
                  if (propertyName.equals(FAVORITE_KEY)) {
                     setFavoritesFirst(getViewPreference().getBoolean(FAVORITE_KEY, false));
                  }
               }
            }
         };
      }

      return preferenceChangeListener;
   }
   
   private void refreshCommands(){
      ((ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class)).refreshElements(HierarchicalPresentationHandler.COMMAND_ID, null);
      ((ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class)).refreshElements(FlatPresentationHandler.COMMAND_ID, null);
      ((ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class)).refreshElements(ShowTransactionPresentationHandler.COMMAND_ID, null);
      ((ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class)).refreshElements(ShowMergeBranchPresentationHandler.COMMAND_ID, null);
      ((ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class)).refreshElements(ShowArchivedBranchHandler.COMMAND_ID, null);
      ((ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class)).refreshElements(ShowFavoriteBranchesFirstHandler.COMMAND_ID, null);
   }

   private void loadPreferences() {
      setPresentation(getViewPreference().getBoolean(FLAT_KEY, true));
      setShowTransactions(getViewPreference().getBoolean(SHOW_TRANSACTIONS, true));
      setShowMergeBranches(getViewPreference().getBoolean(SHOW_MERGE_BRANCHES, false));
      setShowArchivedBranches(getViewPreference().getBoolean(SHOW_ARCHIVED_BRANCHES, false));
      setFavoritesFirst(getViewPreference().getBoolean(FAVORITE_KEY, false));
   }

   private void setFavoritesFirst(boolean favoritesFirst) {
      branchView.setFavoritesFirst(favoritesFirst);
   }

   private void setPresentation(boolean flat) {
      branchView.setPresentation(flat);
   }

   private void setShowMergeBranches(boolean showMergeBranches) {
      branchView.setShowMergeBranches(showMergeBranches);
   }

   private void setShowTransactions(boolean showTransactions) {
      branchView.setShowTransactions(showTransactions);
   }
   
   private void setShowArchivedBranches(boolean showArchivedBranches) {
      branchView.setShowArchivedBranches(showArchivedBranches);      
   }

   /**
    * @param disposed the disposed to set
    */
   public void setDisposed(boolean disposed) {
      this.disposed = disposed;

      try {
         getViewPreference().flush();
      } catch (BackingStoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public Preferences getViewPreference() {
      return preferencesService.getRootNode().node(InstanceScope.SCOPE).node(BranchView.VIEW_ID);
   }
}
