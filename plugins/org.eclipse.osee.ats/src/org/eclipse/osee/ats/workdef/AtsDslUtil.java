/*
 * Created on Jun 25, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

import java.io.IOException;
import java.util.logging.Level;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.ats.dsl.ModelUtil;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

public class AtsDslUtil {

   public static String getString(WorkDefinitionSheet sheet) throws OseeCoreException {
      if (!sheet.getFile().exists()) {
         OseeLog.logf(Activator.class, Level.SEVERE, "WorkDefinition [%s]", sheet);
         return null;
      }
      try {
         return Lib.fileToString(sheet.getFile());
      } catch (IOException ex) {
         throw new OseeWrappedException(String.format("Error loading workdefinition sheet[%s]", sheet), ex);
      }
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
