/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.disposition.rest;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.disposition.model.CiItemData;
import org.eclipse.osee.disposition.model.CiSetData;
import org.eclipse.osee.disposition.model.CopySetParams;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.DispoSetData;
import org.eclipse.osee.disposition.model.DispoSetDescriptorData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;

/**
 * @author Angel Avila
 */
public interface DispoApi {

   // Queries
   List<BranchToken> getDispoPrograms();

   List<String> getDispoProgramNames();

   BranchToken getDispoProgramIdByName(String branchName);

   List<DispoSet> getDispoSets(BranchId branch, String type);

   List<String> getDispoSetNames(BranchId branch, String type);

   DispoSet getDispoSetById(BranchId branch, String dispoSetId);

   String getDispoSetIdByName(BranchId branchId, String setName);

   List<DispoItem> getDispoItems(BranchId branch, String dispoSetId, boolean isDetailed);

   DispoItem getDispoItemById(BranchId branch, String itemId);

   String getDispoItemIdByName(BranchId branchId, String setId, String itemName);

   List<DispoAnnotationData> getDispoAnnotations(BranchId branch, String itemId);

   List<DispoAnnotationData> getDispoAnnotationsByType(Iterable<DispoAnnotationData> annotationData,
      String resolutionType);

   DispoAnnotationData getDispoAnnotationById(BranchId branch, String itemId, String annotationId);

   DispoConfig getDispoConfig(BranchId branch);

   DispoSet getDispoItemParentSet(BranchId branch, String itemId);

   List<String> getCheckedReruns(BranchId branch, String setId);

   // Writes
   Long createDispoProgram(String name);

   ArtifactId createSet(BranchId branch, String importPath, String setName);

   ArtifactId createDispoSet(BranchId branch, DispoSetDescriptorData descriptor);

   String createDispoAnnotation(BranchId branch, String itemId, DispoAnnotationData annotation, String userName,
      boolean isCi);

   String createDispoDiscrepancy(BranchId branch, String itemId, Discrepancy discrepancy, String userName);

   void createDispoDiscrepancies(BranchId branch, String itemId, List<Discrepancy> discrepancies, String userName);

   boolean editDispoDiscrepancy(BranchId branch, String itemId, String discrepancyId, Discrepancy newDiscrepancy,
      String userName);

   void editDispoDiscrepancies(BranchId branch, String itemId, List<Discrepancy> discrepancies, String userName);

   void editDispoSet(BranchId branch, String dispoSetId, DispoSetData newDispoSet);

   void importDispoSet(BranchId branch, String dispoSetId, String importPath);

   void importAllDispoSets(BranchId branch, String filterState);

   void importAllDispoPrograms(String filterState);

   Set<String> getTestScripts(BranchId branch, String dispoSetId);

   Set<String> getTestScripts(BranchId branch);

   boolean editDispoItem(BranchId branch, String itemId, DispoItemData newDispoItem, String userName,
      boolean assignUser);

   boolean updateAllDispoItems(BranchId branch, String dispoSetId);

   boolean editMassDispositions(BranchId branch, String itemId, List<String> ids, String resolutionType,
      String resolution);

   boolean massEditTeam(BranchId branch, String setId, List<String> itemNames, String team, String commitMessage);

   boolean editDispoAnnotation(BranchId branch, String itemId, String annotationId, DispoAnnotationData newAnnotation,
      String userName, boolean isCi);

   void copyDispoSet(BranchId branch, String destSetId, BranchId sourceBranch, String sourceSetId,
      CopySetParams params);

   void copyDispoSetCoverage(BranchId sourceBranch, Long sourceCoverageUuid, BranchId destBranch, String destSetId,
      CopySetParams params);

   // Deletes

   boolean deleteDispoSet(BranchId branch, String dispoSetId);

   boolean deleteDispoItem(BranchId branch, String itemId);

   boolean deleteDispoAnnotation(BranchId branch, String itemId, String annotationId, String userName, boolean isCi);

   boolean deleteAllDispoAnnotation(BranchId branch, String itemId, String userName, boolean isCi);

   boolean deleteDispoDiscrepancy(BranchId branch, String itemId, String discrepancyId, String userName);

   // Utilities
   boolean isUniqueProgramName(String name);

   boolean isUniqueSetName(BranchId branch, String setName);

   boolean isUniqueItemName(BranchId branch, String setId, String itemName);

   Collection<DispoItem> getDispoItemByAnnotationText(BranchId branch, String setId, String keyword,
      boolean isDetailed);

   DispoApiConfiguration getConfig();

   // CI
   HashMap<ArtifactReadable, BranchId> getCiSet(CiSetData setData);

   String getDispoItemId(BranchId branch, String setId, String item);

   List<CiSetData> getAllCiSets();

   String createDispoItem(BranchId branch, CiItemData data, String userName);

}
