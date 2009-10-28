/*
 * Created on Oct 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.test.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.coverage.model.CoverageTestUnit;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;

/**
 * @author Donald G. Dunne
 */
public class CoverageTestUtil {
   public static String COVERAGE_STATIC_ID = "coverage.artifact";

   public static void cleanupCoverageTests() throws OseeCoreException {
      try {
         for (Artifact artifact : getAllCoverageArtifacts()) {
            artifact.purgeFromBranch();
         }
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
   }

   public static Collection<Artifact> getAllCoverageArtifacts() throws OseeCoreException {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      artifacts.addAll(getTestUnitArtifacts());
      artifacts.addAll(getCoveragePackageArtifacts());
      artifacts.addAll(getCoverageUnitArtifacts());
      return artifacts;
   }

   public static Collection<Artifact> getTestUnitArtifacts() throws OseeCoreException {
      return StaticIdManager.getArtifactsFromArtifactQuery(CoverageTestUnit.ARTIFACT_NAME, COVERAGE_STATIC_ID,
            CoverageUtil.getBranch());
   }

   public static Collection<Artifact> getCoverageUnitArtifacts() throws OseeCoreException {
      return StaticIdManager.getArtifactsFromArtifactQuery(CoverageUnit.ARTIFACT_NAME, COVERAGE_STATIC_ID,
            CoverageUtil.getBranch());
   }

   public static Collection<Artifact> getCoveragePackageArtifacts() throws OseeCoreException {
      return StaticIdManager.getArtifactsFromArtifactQuery(CoveragePackage.ARTIFACT_NAME, COVERAGE_STATIC_ID,
            CoverageUtil.getBranch());
   }
}
