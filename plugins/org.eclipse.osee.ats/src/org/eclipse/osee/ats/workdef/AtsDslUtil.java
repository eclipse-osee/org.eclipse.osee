/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workdef;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.ats.dsl.ModelUtil;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.PluginUtil;

/**
 * @author Donald G. Dunne
 */
public class AtsDslUtil {

   public static String getString(WorkDefinitionSheet sheet) {
      Conditions.assertNotNullOrEmpty(sheet.getPluginId(), "pluginId");
      try {
         File file = getFile(sheet.getPluginId(), "OSEE-INF/atsConfig/" + sheet.getName() + ".ats");
         if (!file.exists()) {
            OseeLog.logf(Activator.class, Level.SEVERE, "WorkDefinition [%s]", sheet);
            return null;
         }
         return Lib.fileToString(file);
      } catch (IOException ex) {
         throw new OseeWrappedException(String.format("Error loading workdefinition sheet[%s]", sheet), ex);
      }
   }

   private static File getFile(String pluginId, String filename) {
      try {
         PluginUtil util = new PluginUtil(pluginId);
         return util.getPluginFile(filename);
      } catch (IOException ex) {
         OseeLog.logf(Activator.class, Level.SEVERE, ex, "Unable to access work definition sheet [%s]", filename);
      }
      return null;
   }

   public static AtsDsl getFromSheet(String modelName, WorkDefinitionSheet sheet) {
      try {
         return getFromString(modelName, getString(sheet));
      } catch (Exception ex) {
         throw new WrappedException(ex);
      }
   }

   public static AtsDsl getFromString(String modelName, String dslString) {
      try {
         AtsDsl atsDsl = ModelUtil.loadModel(modelName, dslString);
         return atsDsl;
      } catch (Exception ex) {
         throw new WrappedException(ex);
      }
   }

}
