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
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.NodeChangeEvent;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.FlatPresentationHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.HierarchicalPresentationHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.ShowArchivedBranchHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.ShowFavoriteBranchesFirstHandler;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.branch.ShowMergeBranchPresentationHandler;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author Jeff C. Phillips
 */
public class BranchViewPresentationPreferences {
   private final IPreferencesService preferencesService;
   private IPreferenceChangeListener preferenceChangeListener;
   private final BranchView branchView;
   private boolean disposed;

   private static final String[] listOfCommandIds = {
      HierarchicalPresentationHandler.COMMAND_ID,
      FlatPresentationHandler.COMMAND_ID,
      ShowMergeBranchPresentationHandler.COMMAND_ID,
      ShowArchivedBranchHandler.COMMAND_ID,
      ShowFavoriteBranchesFirstHandler.COMMAND_ID};

   public BranchViewPresentationPreferences(BranchView branchView) {
      preferencesService = Platform.getPreferencesService();
      preferenceChangeListener = null;
      this.branchView = branchView;
      disposed = false;

      IEclipsePreferences instanceNode =
         (IEclipsePreferences) preferencesService.getRootNode().node(InstanceScope.SCOPE);

      try {
         if (instanceNode.nodeExists(BranchView.VIEW_ID)) {
            ((IEclipsePreferences) instanceNode.node(BranchView.VIEW_ID)).addPreferenceChangeListener(
               singletonPreferenceChangeListener());
         }
      } catch (BackingStoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      instanceNode.addNodeChangeListener(new IEclipsePreferences.INodeChangeListener() {

         @Override
         public void added(NodeChangeEvent event) {
            Preferences child = event.getChild();
            if (child.name().equals(BranchView.VIEW_ID)) {
               ((IEclipsePreferences) child).addPreferenceChangeListener(singletonPreferenceChangeListener());
            }
         }

         @Override
         public void removed(NodeChangeEvent event) {
            Preferences child = event.getChild();
            if (child.name().equals(BranchView.VIEW_ID)) {
               ((IEclipsePreferences) child).removePreferenceChangeListener(singletonPreferenceChangeListener());
            }
         }
      });

      loadPreferences();
   }

   private synchronized IPreferenceChangeListener singletonPreferenceChangeListener() {
      if (preferenceChangeListener == null) {
         preferenceChangeListener = new IPreferenceChangeListener() {
            @Override
            public void preferenceChange(PreferenceChangeEvent event) {
               if (disposed) {
                  ((IEclipsePreferences) event.getNode()).removePreferenceChangeListener(this);
               } else {
                  BranchOptionsEnum presEnum = BranchOptionsEnum.fromInitValue(event.getKey());

                  refreshCommands();

                  branchView.getXBranchWidget().setBranchOptions(getViewPreference().getBoolean(presEnum.origKeyName,
                     presEnum == BranchOptionsEnum.FAVORITE_KEY ? false : true), presEnum);
               }
            }
         };
      }

      return preferenceChangeListener;
   }

   private void refreshCommands() {
      ICommandService service = PlatformUI.getWorkbench().getService(ICommandService.class);
      for (String command : listOfCommandIds) {
         service.refreshElements(command, null);
         service = PlatformUI.getWorkbench().getService(ICommandService.class);
      }
   }

   public void loadPreferences() {
      Preferences pref = getViewPreference();
      for (BranchOptionsEnum keyEnum : BranchOptionsEnum.values()) {
         XBranchWidget branchWidget = branchView.getXBranchWidget();
         switch (keyEnum) {
            case FLAT_KEY:
            case SHOW_MERGE_BRANCHES:
            case SHOW_ARCHIVED_BRANCHES:
            case FAVORITE_KEY:
               branchWidget.setBranchOptions(pref.getBoolean(keyEnum.origKeyName, false), keyEnum);
               break;
         }
      }
   }

   /**
    * @param disposed the disposed to set
    */
   public void setDisposed(boolean disposed) {
      this.disposed = disposed;

      try {
         getViewPreference().flush();
      } catch (BackingStoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public Preferences getViewPreference() {
      return preferencesService.getRootNode().node(InstanceScope.SCOPE).node(BranchView.VIEW_ID);
   }
}
