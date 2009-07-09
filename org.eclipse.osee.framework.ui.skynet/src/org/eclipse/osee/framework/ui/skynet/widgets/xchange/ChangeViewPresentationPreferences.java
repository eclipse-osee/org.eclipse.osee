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
import org.eclipse.osee.framework.ui.skynet.commandHandlers.change.ToggleChangeViewDocOrderHandler;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author Jeff C. Phillips
 */
public class ChangeViewPresentationPreferences {
   public static final String SHOW_DOC_ORDER = "show doc order";

   private final IPreferencesService preferencesService;
   private IPreferenceChangeListener preferenceChangeListener;
   private ChangeView changeView;
   private boolean disposed;

   public ChangeViewPresentationPreferences(ChangeView changeView) {
      this.preferencesService = Platform.getPreferencesService();
      this.preferenceChangeListener = null;
      this.changeView = changeView;
      this.disposed = false;

      IEclipsePreferences instanceNode =
            (IEclipsePreferences) preferencesService.getRootNode().node(InstanceScope.SCOPE);

      try {
         if (instanceNode.nodeExists(ChangeView.VIEW_ID)) {
            ((IEclipsePreferences) instanceNode.node(ChangeView.VIEW_ID)).addPreferenceChangeListener(singletonPreferenceChangeListener());
         }
      } catch (BackingStoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

      instanceNode.addNodeChangeListener(new IEclipsePreferences.INodeChangeListener() {

         public void added(NodeChangeEvent event) {
            if (event.getChild().name().equals(ChangeView.VIEW_ID)) {
               ((IEclipsePreferences) event.getChild()).addPreferenceChangeListener(singletonPreferenceChangeListener());
            }
         }

         public void removed(NodeChangeEvent event) {
            if (event.getChild().name().equals(ChangeView.VIEW_ID)) {
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
                  
                  if (propertyName.equals(SHOW_DOC_ORDER)) {
                     setShowDocumentOrder(getViewPreference().getBoolean(SHOW_DOC_ORDER, false));
                  }
               }
            }
         };
      }

      return preferenceChangeListener;
   }
   
   private void refreshCommands(){
      ((ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class)).refreshElements(ToggleChangeViewDocOrderHandler.COMMAND_ID, null);
   }

   private void loadPreferences() {
      setShowDocumentOrder(getViewPreference().getBoolean(SHOW_DOC_ORDER, false));
   }

   private void setShowDocumentOrder(boolean showDocOrder) {
      changeView.setShowDocumentOrder(showDocOrder);      
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
      return preferencesService.getRootNode().node(InstanceScope.SCOPE).node(ChangeView.VIEW_ID);
   }
}
