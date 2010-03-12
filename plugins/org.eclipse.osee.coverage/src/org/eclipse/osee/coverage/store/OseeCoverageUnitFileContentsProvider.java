/*
 * Created on Jan 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.store;

import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverageUnitFileContentsProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class OseeCoverageUnitFileContentsProvider implements ICoverageUnitFileContentsProvider {

   private static OseeCoverageUnitFileContentsProvider instance = new OseeCoverageUnitFileContentsProvider();

   private OseeCoverageUnitFileContentsProvider() {
   }

   public static OseeCoverageUnitFileContentsProvider getInstance() {
      return instance;
   }

   @Override
   public String getFileContents(CoverageUnit coverageUnit) {
      try {
         OseeCoverageUnitStore store = new OseeCoverageUnitStore(coverageUnit);
         Artifact artifact = store.getArtifact(false);
         if (artifact != null) {
            return artifact.getSoleAttributeValue(CoverageAttributes.FILE_CONTENTS.getStoreName(), "");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return "";
   }

   @Override
   public void setFileContents(CoverageUnit coverageUnit, String fileContents) {
      try {
         OseeCoverageUnitStore store = new OseeCoverageUnitStore(coverageUnit);
         Artifact artifact = store.getArtifact(false);
         if (artifact != null) {
            artifact.setSoleAttributeValue(CoverageAttributes.FILE_CONTENTS.getStoreName(), fileContents);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

}
