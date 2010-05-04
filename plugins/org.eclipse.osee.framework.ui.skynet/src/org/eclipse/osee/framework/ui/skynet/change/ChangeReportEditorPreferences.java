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
package org.eclipse.osee.framework.ui.skynet.change;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportEditorPreferences implements IChangeReportPreferences {
   private static final String SHOW_IN_DOCUMENT_ORDER = "change.report.show.in.document.order";

   private final Collection<Listener> listeners = new CopyOnWriteArraySet<Listener>();
   private final String nodeID;

   public ChangeReportEditorPreferences(String nodeID) {
      this.nodeID = nodeID;
   }

   public Preferences getPreferences() {
      IPreferencesService preferencesService = Platform.getPreferencesService();
      return preferencesService.getRootNode().node(InstanceScope.SCOPE).node(nodeID);
   }

   private void notifyOnDocumentOrderChange(boolean newValue) {
      for (Listener listener : listeners) {
         listener.onDocumentOrderChange(newValue);
      }
   }

   @Override
   public void addListener(Listener listener) {
      if (listener != null) {
         listeners.add(listener);
         listener.onDocumentOrderChange(isInDocumentOrder());
      }
   }

   @Override
   public void removeListener(Listener listener) {
      if (listener != null) {
         listeners.remove(listener);
      }
   }

   @Override
   public boolean isInDocumentOrder() {
      return getPreferences().getBoolean(SHOW_IN_DOCUMENT_ORDER, false);
   }

   @Override
   public void setInDocumentOrder(boolean isEnabled) {
      boolean original = isInDocumentOrder();
      if (original != isEnabled) {
         getPreferences().putBoolean(SHOW_IN_DOCUMENT_ORDER, isEnabled);
         notifyOnDocumentOrderChange(isEnabled);
      }
   }

   @Override
   public void saveState() {
      try {
         getPreferences().flush();
      } catch (BackingStoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }
}
