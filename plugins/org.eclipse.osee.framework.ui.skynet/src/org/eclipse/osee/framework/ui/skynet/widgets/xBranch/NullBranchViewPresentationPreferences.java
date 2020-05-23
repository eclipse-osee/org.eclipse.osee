/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.service.prefs.Preferences;

/**
 * @author Karol M. Wilk
 */
public class NullBranchViewPresentationPreferences extends BranchViewPresentationPreferences {
   public NullBranchViewPresentationPreferences() {
      super(null);
   }

   /**
    * @param disposed the disposed to set
    */
   @Override
   public void setDisposed(boolean disposed) {
      //
   }

   @Override
   public Preferences getViewPreference() {
      return new Preferences() {
         @Override
         public void put(String key, String value) {
            //
         }

         @Override
         public String get(String key, String def) {
            return "";
         }

         @Override
         public void remove(String key) {
            //
         }

         @Override
         public void clear() {
            //
         }

         @Override
         public void putInt(String key, int value) {
            //
         }

         @Override
         public int getInt(String key, int def) {
            return 0;
         }

         @Override
         public void putLong(String key, long value) {
            //
         }

         @Override
         public long getLong(String key, long def) {
            return 0;
         }

         @Override
         public void putBoolean(String key, boolean value) {
            //
         }

         @Override
         public boolean getBoolean(String key, boolean def) {
            return false;
         }

         @Override
         public void putFloat(String key, float value) {
            //
         }

         @Override
         public float getFloat(String key, float def) {
            return 0;
         }

         @Override
         public void putDouble(String key, double value) {
            //
         }

         @Override
         public double getDouble(String key, double def) {
            return 0;
         }

         @Override
         public void putByteArray(String key, byte[] value) {
            //
         }

         @Override
         public byte[] getByteArray(String key, byte[] def) {
            return new byte[] {};
         }

         @Override
         public String[] keys() {
            return new String[] {};
         }

         @Override
         public String[] childrenNames() {
            return new String[] {};
         }

         @Override
         public Preferences parent() {
            showImproperStateMessage();
            return null;
         }

         @Override
         public Preferences node(String pathName) {
            showImproperStateMessage();
            return null;
         }

         @Override
         public boolean nodeExists(String pathName) {
            return false;
         }

         @Override
         public void removeNode() {
            //
         }

         @Override
         public String name() {
            return "";
         }

         @Override
         public String absolutePath() {
            return "";
         }

         @Override
         public void flush() {
            //
         }

         @Override
         public void sync() {
            //
         }
      };
   }

   private void showImproperStateMessage() {
      OseeLog.logf(NullBranchViewPresentationPreferences.class, Level.SEVERE, "%s has not been properlly initialized.",
         BranchViewPresentationPreferences.class.getName());
   }
}
