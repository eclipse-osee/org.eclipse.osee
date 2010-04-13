/*
 * Created on Apr 9, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.store;

import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePreferences;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class CoverageOptionManagerStore {

   private final OseeCoveragePackageStore oseeCoveragePackageStore;
   public static enum StoreLocation {
      Local, Global, None
   };

   public CoverageOptionManagerStore(OseeCoveragePackageStore oseeCoveragePackageStore) {
      this.oseeCoveragePackageStore = oseeCoveragePackageStore;
   }

   public String getCoverageOptions() throws MultipleAttributesExist, OseeCoreException {
      String coverageOptions =
            oseeCoveragePackageStore.getArtifact(true).getSoleAttributeValueAsString(
                  CoverageAttributes.COVERAGE_OPTIONS.getStoreName(), null);
      if (!Strings.isValid(coverageOptions)) {
         coverageOptions =
               (new CoveragePreferences(oseeCoveragePackageStore.getArtifact(true).getBranch())).getCoverageOptions();
      }
      return coverageOptions;
   }

   private String getLocalCoverageOptions() throws OseeCoreException {
      return oseeCoveragePackageStore.getArtifact(true).getSoleAttributeValueAsString(
            CoverageAttributes.COVERAGE_OPTIONS.getStoreName(), null);
   }

   private String getGlobalCoverageOptions() throws OseeCoreException {
      return (new CoveragePreferences(oseeCoveragePackageStore.getArtifact(true).getBranch())).getCoverageOptions();
   }

   public StoreLocation getStoreLocation() throws OseeCoreException {
      if (Strings.isValid(getLocalCoverageOptions())) return StoreLocation.Local;
      if (Strings.isValid(getGlobalCoverageOptions())) return StoreLocation.Global;
      return StoreLocation.None;
   }

   public CoverageOptionManager getCoverageOptionManager() throws OseeCoreException {
      String coverageOptions = getCoverageOptions();
      if (!Strings.isValid(coverageOptions)) {
         coverageOptions = CoverageOptionManagerDefault.instance().toXml();
      }
      return new CoverageOptionManager(coverageOptions);
   }

   public void store(CoverageOptionManager coverageOptionManager, StoreLocation storeLocation) throws OseeCoreException {
      if (storeLocation == StoreLocation.None) throw new OseeArgumentException(
            "No Valid Store Location selected for Coverage Options");
      store(coverageOptionManager.toXml(), storeLocation);
   }

   public void store(String coverageOptions, StoreLocation storeLocation) throws OseeCoreException {
      if (storeLocation == StoreLocation.Local) {
         oseeCoveragePackageStore.getArtifact(true).setSoleAttributeFromString(
               CoverageAttributes.COVERAGE_OPTIONS.getStoreName(), coverageOptions);
         oseeCoveragePackageStore.getArtifact(true).persist();
      } else if (storeLocation == StoreLocation.Global) {
         (new CoveragePreferences(oseeCoveragePackageStore.getArtifact(true).getBranch())).setCoverageOptions(coverageOptions);
      } else
         throw new OseeArgumentException("No Valid Store Location selected for Coverage Options");

   }
}
