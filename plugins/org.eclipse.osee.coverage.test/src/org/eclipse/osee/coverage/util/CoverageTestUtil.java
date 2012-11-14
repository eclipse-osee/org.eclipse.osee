/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOption;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.store.CoverageArtifactTypes;
import org.eclipse.osee.coverage.store.OseeCoveragePackageStore;
import org.eclipse.osee.coverage.util.dialog.CoveragePackageArtifactListDialog;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class CoverageTestUtil {
   private static String COVERAGE_STATIC_ID = "coverage.artifact";
   public static final IOseeBranch SAW_Bld_1 = TokenFactory.createBranch("AyH_f2sSKy3l07fIvAAA", "SAW_Bld_1");

   public static void cleanupCoverageTests() throws OseeCoreException {
      try {
         Operations.executeWorkAndCheckStatus(new PurgeArtifacts(getAllCoverageArtifacts()));
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
      artifact.setSingletonAttributeValue(CoreAttributeTypes.StaticId, CoverageTestUtil.COVERAGE_STATIC_ID);
      if (recurse) {
         for (Artifact childArt : artifact.getChildren()) {
            if (childArt.isOfType(CoverageArtifactTypes.CoveragePackage, CoverageArtifactTypes.CoveragePackage)) {
               registerAsTestArtifact(childArt, recurse);
            }
         }
      }
   }

   public static ICoverage getFirstCoverageByNameEquals(CoveragePackageBase coveragePackageBase, String name) {
      for (ICoverage coverage : coveragePackageBase.getChildren(true)) {
         if (coverage.getName().equals(name)) {
            return coverage;
         }
      }
      return null;
   }

   public static CoverageUnit getFirstCoverageUnitByNameContains(CoverageUnit coverageUnit, String name) {
      for (CoverageUnit coverage : coverageUnit.getCoverageUnits()) {
         if (coverage.getName().contains(name)) {
            return coverage;
         }
      }
      return null;
   }

   public static CoverageItem getFirstCoverageItemByNameContains(CoverageUnit coverageUnit, String name) {
      for (CoverageItem coverage : coverageUnit.getCoverageItems()) {
         if (coverage.getName().contains(name)) {
            return coverage;
         }
      }
      return null;
   }

   public static Collection<Artifact> getAllCoverageArtifacts() throws OseeCoreException {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      artifacts.addAll(getCoveragePackageArtifacts());
      artifacts.addAll(getCoverageUnitArtifacts());
      artifacts.addAll(getCoverageRecordArtifacts());
      return artifacts;
   }

   public static Collection<Artifact> getCoverageUnitArtifacts() throws OseeCoreException {
      return ArtifactQuery.getArtifactListFromTypeAndAttribute(CoverageArtifactTypes.CoverageUnit,
         CoreAttributeTypes.StaticId, COVERAGE_STATIC_ID, CoverageTestUtil.getTestBranch());
   }

   public static Collection<Artifact> getCoveragePackageArtifacts() throws OseeCoreException {
      return ArtifactQuery.getArtifactListFromTypeAndAttribute(CoverageArtifactTypes.CoveragePackage,
         CoreAttributeTypes.StaticId, COVERAGE_STATIC_ID, CoverageTestUtil.getTestBranch());
   }

   public static Collection<Artifact> getCoverageRecordArtifacts() throws OseeCoreException {
      return ArtifactQuery.getArtifactListFromTypeAndAttribute(CoreArtifactTypes.GeneralDocument,
         CoreAttributeTypes.StaticId, COVERAGE_STATIC_ID, CoverageTestUtil.getTestBranch());
   }

   public static void setAllCoverageMethod(CoverageUnit coverageUnit, CoverageOption CoverageOption, boolean recurse) {
      for (CoverageItem item : coverageUnit.getCoverageItems(recurse)) {
         item.setCoverageMethod(CoverageOption);
      }
   }

   public static IOseeBranch getTestBranch() {
      return SAW_Bld_1;
   }

   public static Artifact getSelectedCoveragePackageFromDialog() throws OseeCoreException {
      CoveragePackageArtifactListDialog dialog =
         new CoveragePackageArtifactListDialog("Open Coverage Package", "Select Coverage Package");
      if (!CoverageUtil.getBranchFromUser(false)) {
         return null;
      }
      dialog.setInput(OseeCoveragePackageStore.getCoveragePackageArtifacts(CoverageUtil.getBranch()));
      if (dialog.open() != 0) {
         return null;
      }
      Artifact coveragePackageArtifact = (Artifact) dialog.getResult()[0];
      return coveragePackageArtifact;
   }

}
