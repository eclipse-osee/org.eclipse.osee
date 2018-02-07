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
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class AtsDslUtil {

   public static String getString(WorkDefinitionSheet sheet) {
      try {
         File file = OseeInf.getResourceAsFile("atsConfig/" + sheet.getName() + ".ats", sheet.getClazz());
         if (!file.exists()) {
            OseeLog.logf(Activator.class, Level.SEVERE, "WorkDefinition [%s]", sheet);
            return null;
         }
         return Lib.fileToString(file);
      } catch (IOException ex) {
         throw new OseeWrappedException(String.format("Error loading workdefinition sheet[%s]", sheet), ex);
      }
   }

   public static AtsDsl getFromSheet(WorkDefinitionSheet sheet) {
      try {
         return getFromString(sheet.getName() + ".ats", getString(sheet));
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
