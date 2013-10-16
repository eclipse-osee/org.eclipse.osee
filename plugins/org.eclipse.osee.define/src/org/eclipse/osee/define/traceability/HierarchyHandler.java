/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.traceability;

import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;

/**
 * @author Roberto E. Escobar
 */
public final class HierarchyHandler {

   public static void addArtifact(SkynetTransaction transaction, Artifact testUnit) throws OseeCoreException {
      Artifact folder = null;
      Branch branch = testUnit.getFullBranch();
      if (testUnit.isOfType(CoreArtifactTypes.TestCase)) {
         folder = getOrCreateTestCaseFolder(transaction, branch);
      } else if (testUnit.isOfType(CoreArtifactTypes.TestSupport)) {
         folder = getOrCreateTestSupportFolder(transaction, branch);
      } else if (testUnit.isOfType(CoreArtifactTypes.CodeUnit)) {
         folder = getOrCreateCodeUnitFolder(transaction, branch);
      } else {
         folder = getOrCreateUnknownTestUnitFolder(transaction, branch);
      }

      if (folder != null && !folder.isRelated(CoreRelationTypes.Default_Hierarchical__Child, testUnit)) {
         folder.addChild(testUnit);
         persistHelper(transaction, folder);
      }
   }

   private static Artifact getOrCreateUnknownTestUnitFolder(SkynetTransaction transaction, Branch branch) throws OseeCoreException {
      return getOrCreateTestUnitSubFolder(transaction, branch, "Unknown Test Unit Type");
   }

   private static Artifact getOrCreateTestSupportFolder(SkynetTransaction transaction, Branch branch) throws OseeCoreException {
      return getOrCreateTestUnitSubFolder(transaction, branch, Requirements.TEST_SUPPORT_UNITS);
   }

   private static Artifact getOrCreateTestCaseFolder(SkynetTransaction transaction, Branch branch) throws OseeCoreException {
      return getOrCreateTestUnitSubFolder(transaction, branch, "Test Cases");
   }

   private static Artifact getOrCreateCodeUnitFolder(SkynetTransaction transaction, Branch branch) throws OseeCoreException {
      Artifact codeUnitFolder = getOrCreateFolder(branch, "Code Units");
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
      if (!root.isRelated(CoreRelationTypes.Default_Hierarchical__Child, codeUnitFolder)) {
         root.addChild(codeUnitFolder);
         persistHelper(transaction, root);
      }
      return codeUnitFolder;
   }

   private static Artifact getOrCreateTestUnitSubFolder(SkynetTransaction transaction, Branch branch, String folderName) throws OseeCoreException {
      Artifact subFolder = getOrCreateFolder(branch, folderName);
      Artifact testUnits = getOrCreateTestUnitsFolder(transaction, branch);
      if (!testUnits.isRelated(CoreRelationTypes.Default_Hierarchical__Child, subFolder)) {
         testUnits.addChild(subFolder);
         persistHelper(transaction, testUnits);
      }
      return subFolder;
   }

   private static Artifact getOrCreateTestUnitsFolder(SkynetTransaction transaction, Branch branch) throws OseeCoreException {
      Artifact testFolder = getOrCreateFolder(branch, "Test");
      Artifact testUnitFolder = getOrCreateFolder(branch, "Test Units");
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
      if (!root.isRelated(CoreRelationTypes.Default_Hierarchical__Child, testFolder)) {
         root.addChild(testFolder);
         persistHelper(transaction, root);
      }
      if (!testFolder.isRelated(CoreRelationTypes.Default_Hierarchical__Child, testUnitFolder)) {
         testFolder.addChild(testUnitFolder);
         persistHelper(transaction, testFolder);
      }
      return testUnitFolder;
   }

   private static void persistHelper(SkynetTransaction transaction, Artifact toPersist) throws OseeCoreException {
      if (transaction != null) {
         toPersist.persist(transaction);
      }
   }

   private static Artifact getOrCreateFolder(Branch branch, String folderName) throws OseeCoreException {
      return OseeSystemArtifacts.getOrCreateArtifact(CoreArtifactTypes.Folder, folderName, branch);
   }
}