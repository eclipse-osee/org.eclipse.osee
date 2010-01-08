/*
 * Created on Oct 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOption;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.store.CoverageArtifactTypes;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;

/**
 * @author Donald G. Dunne
 */
public class CoverageTestUtil {
   private static String COVERAGE_STATIC_ID = "coverage.artifact";

   public static void cleanupCoverageTests() throws OseeCoreException {
      try {
         for (Artifact artifact : getAllCoverageArtifacts()) {
            artifact.purgeFromBranch();
         }
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
   }

   /**
    * Adds the static id to the artifact to ensure that test cleans (purges) this artifact after completion.
    */
   public static void registerAsTestArtifact(Artifact artifact) throws OseeCoreException {
      registerAsTestArtifact(artifact, false);
   }

   /**
    * Adds the static id to the artifact to ensure that test cleans (purges) this artifact after completion.
    */
   public static void registerAsTestArtifact(Artifact artifact, boolean recurse) throws OseeCoreException {
      StaticIdManager.setSingletonAttributeValue(artifact, CoverageTestUtil.COVERAGE_STATIC_ID);
      if (recurse) {
         for (Artifact childArt : artifact.getChildren()) {
            if (childArt.isOfType(CoverageArtifactTypes.CoveragePackage) || childArt.isOfType(CoverageArtifactTypes.CoveragePackage)) {
               registerAsTestArtifact(childArt, recurse);
            }
         }
      }
   }

   public static ICoverage getFirstCoverageByName(CoveragePackageBase coveragePackageBase, String name) {
      for (ICoverage coverage : coveragePackageBase.getChildren(true)) {
         if (coverage.getName().equals(name)) {
            return coverage;
         }
      }
      return null;
   }

   public static Collection<Artifact> getAllCoverageArtifacts() throws OseeCoreException {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      artifacts.addAll(getCoveragePackageArtifacts());
      artifacts.addAll(getCoverageUnitArtifacts());
      return artifacts;
   }

   public static Collection<Artifact> getCoverageUnitArtifacts() throws OseeCoreException {
      return StaticIdManager.getArtifactsFromArtifactQuery(CoverageArtifactTypes.CoveragePackage, COVERAGE_STATIC_ID,
            CoverageUtil.getBranch());
   }

   public static Collection<Artifact> getCoveragePackageArtifacts() throws OseeCoreException {
      return StaticIdManager.getArtifactsFromArtifactQuery(CoverageArtifactTypes.CoveragePackage, COVERAGE_STATIC_ID,
            CoverageUtil.getBranch());
   }

   public static void setAllCoverageMethod(CoverageUnit coverageUnit, CoverageOption CoverageOption, boolean recurse) {
      for (CoverageItem item : coverageUnit.getCoverageItems(recurse)) {
         item.setCoverageMethod(CoverageOption);
      }
   }
}
