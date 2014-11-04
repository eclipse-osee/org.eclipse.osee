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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
   private final Map<String, Artifact> folderNameToArtifact = new HashMap<String, Artifact>(50);
   private final SkynetTransaction transaction;

   public HierarchyHandler(SkynetTransaction transaction) {
      this.transaction = transaction;
   }

   public void addArtifact(Artifact testUnit) throws OseeCoreException {
      Artifact folder = null;

      Branch branch = testUnit.getFullBranch();
      if (testUnit.isOfType(CoreArtifactTypes.TestCase)) {
         folder = getOrCreateTestCaseFolder(branch);
      } else if (testUnit.isOfType(CoreArtifactTypes.TestSupport)) {
         folder = getOrCreateTestSupportFolder(branch);
      } else if (testUnit.isOfType(CoreArtifactTypes.CodeUnit)) {
         folder = getOrCreateCodeUnitFolder(branch, testUnit.getName());
      } else {
         folder = getOrCreateUnknownTestUnitFolder(branch);
      }

      addChildIfNotRelated(folder, testUnit);
   }

   private Artifact getOrCreateUnknownTestUnitFolder(Branch branch) throws OseeCoreException {
      return getOrCreateTestUnitsFolder(branch, "Unknown Test Unit Type", true);
   }

   private Artifact getOrCreateTestSupportFolder(Branch branch) throws OseeCoreException {
      return getOrCreateTestUnitsFolder(branch, Requirements.TEST_SUPPORT_UNITS, true);
   }

   private Artifact getOrCreateTestCaseFolder(Branch branch) throws OseeCoreException {
      return getOrCreateTestUnitsFolder(branch, "Test Cases", true);
   }

   private Artifact getOrCreateCodeUnitFolder(Branch branch, String codeUnitName) throws OseeCoreException {
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
      Artifact toReturn = getOrCreateFolder(branch, "Code Units", root);

      String subSystem;
      subsystemMatcher.reset(codeUnitName);
      if (subsystemMatcher.find()) {
         subSystem = subsystemMatcher.group(1);
         subSystem = subSystem.toUpperCase();
         toReturn = getOrCreateFolder(branch, subSystem, toReturn);
      }

      return toReturn;
   }

   private Artifact getOrCreateTestUnitsFolder(Branch branch, String subfolderName, boolean includesSubfolder) throws OseeCoreException {
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
      Artifact testFolder = getOrCreateFolder(branch, "Test", root);
      addChildIfNotRelated(root, testFolder);
      Artifact testUnitFolder = getOrCreateFolder(branch, "Test Units", testFolder);
      addChildIfNotRelated(testFolder, testUnitFolder);

      if (subfolderName != null && includesSubfolder) {
         Artifact subFolder = getOrCreateFolder(branch, subfolderName, testFolder);
         addChildIfNotRelated(testFolder, subFolder);
         return subFolder;
      }
      return testUnitFolder;
   }

   private void persistHelper(Artifact toPersist) throws OseeCoreException {
      if (transaction != null) {
         toPersist.persist(transaction);
      }
   }

   private void addChildIfNotRelated(Artifact parentFolder, Artifact childFolder) {
      Collection<Artifact> toCheck = new LinkedList<Artifact>();
      if (parentFolder.isOfType(CoreArtifactTypes.RootArtifact)) {
         toCheck.addAll(parentFolder.getChildren());
      } else {
         toCheck.addAll(parentFolder.getDescendants());
      }
      if (!toCheck.contains(childFolder)) {
         parentFolder.addChild(childFolder);
         persistHelper(parentFolder);
      }
   }

   private Artifact getOrCreateFolder(Branch branch, String folderName, Artifact parentFolder) throws OseeCoreException {
      Artifact toReturn = folderNameToArtifact.get(folderName);
      if (toReturn == null) {
         List<Artifact> relatedFolders =
            ArtifactQuery.getArtifactListFromTypeAndName(CoreArtifactTypes.Folder, folderName, branch);
         if (relatedFolders.size() == 1) {
            toReturn = relatedFolders.iterator().next();
         } else if (relatedFolders.size() > 1) {
            List<Artifact> descendants = parentFolder.getDescendants();
            for (Artifact folder : relatedFolders) {
               if (descendants.contains(folder)) {
                  toReturn = folder;
                  break;
               }
            }
         }
         if (toReturn == null) {
            toReturn = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, branch, folderName);
            addChildIfNotRelated(parentFolder, toReturn);
            toReturn.persist(transaction);
         }
         folderNameToArtifact.put(folderName, toReturn);
      }
      return toReturn;
   }
}