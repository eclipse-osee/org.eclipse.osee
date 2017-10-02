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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;

/**
 * @author Roberto E. Escobar
 */
public final class HierarchyHandler {
   private static final Matcher subsystemMatcher = Pattern.compile("(\\w*)\\.ss").matcher("");
   private final Map<String, Artifact> folderNameToArtifact = new HashMap<>(50);
   private final SkynetTransaction transaction;
   private final BranchId branch;
   private Artifact root;

   public HierarchyHandler(SkynetTransaction transaction) {
      this.transaction = transaction;
      this.branch = transaction.getBranch();
   }

   public void addArtifact(Artifact testUnit)  {
      Conditions.checkExpressionFailOnTrue(!testUnit.isOnBranch(branch), "Artifact [%s] must be on branch [%s]",
         testUnit.toString(), branch.getId());
      Artifact folder = null;

      if (testUnit.isOfType(CoreArtifactTypes.TestCase)) {
         folder = getOrCreateTestCaseFolder();
      } else if (testUnit.isOfType(CoreArtifactTypes.TestSupport)) {
         folder = getOrCreateTestSupportFolder();
      } else if (testUnit.isOfType(CoreArtifactTypes.CodeUnit)) {
         folder = getOrCreateCodeUnitFolder(testUnit.getName());
      } else {
         folder = getOrCreateUnknownTestUnitFolder();
      }

      addChildIfNotRelated(folder, testUnit);
   }

   private Artifact getOrCreateUnknownTestUnitFolder()  {
      return getOrCreateTestUnitsFolder("Unknown Test Unit Type", true);
   }

   private Artifact getOrCreateTestSupportFolder()  {
      return getOrCreateTestUnitsFolder(Requirements.TEST_SUPPORT_UNITS, true);
   }

   private Artifact getOrCreateTestCaseFolder()  {
      return getOrCreateTestUnitsFolder("Test Cases", true);
   }

   private Artifact getRoot() {
      if (root == null) {
         root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
      }
      return root;
   }

   private Artifact getOrCreateCodeUnitFolder(String codeUnitName)  {
      Artifact root = getRoot();
      Artifact toReturn = getOrCreateFolder("Code Units", root);

      String subSystem;
      subsystemMatcher.reset(codeUnitName);
      if (subsystemMatcher.find()) {
         subSystem = subsystemMatcher.group(1);
         subSystem = subSystem.toUpperCase();
         toReturn = getOrCreateFolder(subSystem, toReturn);
      }

      return toReturn;
   }

   private Artifact getOrCreateTestUnitsFolder(String subfolderName, boolean includesSubfolder)  {
      Artifact root = getRoot();
      Artifact testFolder = getOrCreateFolder("Test", root);
      Artifact testUnitFolder = getOrCreateFolder("Test Units", testFolder);

      if (subfolderName != null && includesSubfolder) {
         Artifact subFolder = getOrCreateFolder(subfolderName, testUnitFolder);
         return subFolder;
      }
      return testUnitFolder;
   }

   private void persistHelper(Artifact toPersist)  {
      if (transaction != null) {
         toPersist.persist(transaction);
      }
   }

   private void addChildIfNotRelated(Artifact parentFolder, Artifact childFolder) {
      boolean related = parentFolder.isRelated(CoreRelationTypes.Default_Hierarchical__Child, childFolder);
      if (!related) {
         parentFolder.addChild(childFolder);
         persistHelper(parentFolder);
      }
   }

   private Artifact getOrCreateFolder(String folderName, Artifact parentFolder)  {
      Artifact toReturn = folderNameToArtifact.get(folderName);
      if (toReturn == null) {
         List<Artifact> relatedFolders =
            ArtifactQuery.getArtifactListFromTypeAndName(CoreArtifactTypes.Folder, folderName, branch);
         if (relatedFolders.size() == 1) {
            toReturn = relatedFolders.iterator().next();
         } else if (relatedFolders.size() > 1) {
            for (Artifact folder : relatedFolders) {
               if (parentFolder.isRelated(CoreRelationTypes.Default_Hierarchical__Child, folder)) {
                  toReturn = folder;
                  break;
               }
            }
         }
         if (toReturn == null) {
            toReturn = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, branch, folderName);
            parentFolder.addChild(toReturn);
            toReturn.persist(transaction);
         }
         folderNameToArtifact.put(folderName, toReturn);
      }
      return toReturn;
   }
}