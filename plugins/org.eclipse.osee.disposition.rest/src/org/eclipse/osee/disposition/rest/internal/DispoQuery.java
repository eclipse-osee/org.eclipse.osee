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

package org.eclipse.osee.disposition.rest.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.disposition.model.CiSetData;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.UserId;

/**
 * @author Angel Avila
 */
public interface DispoQuery {

   Map<String, ArtifactReadable> getCoverageUnits(BranchId branch, Long artifactUuid);

   UserId findUser();

   UserId findUser(String userId);

   UserId findUserByName(String name);

   boolean isUniqueProgramName(String name);

   boolean isUniqueSetName(BranchId branch, String name);

   boolean isUniqueItemName(BranchId branch, String setId, String name);

   BranchToken findDispoProgramIdByName(String branchName);

   String findDispoSetIdByName(BranchId branch, String setName);

   List<DispoSet> findDispoSets(BranchId branch, String type);

   List<String> getDispoSets(BranchId branch, String type);

   DispoSet findDispoSetsById(BranchId branch, String id);

   List<DispoItem> findDispoItems(BranchId branch, String setId, boolean isDetailed);

   DispoItem findDispoItemById(BranchId branch, String itemId);

   String findDispoItemIdByName(BranchId branchId, String setId, String itemName);

   List<String> getCheckedReruns(HashMap<String, DispoItem> items, String setId);

   List<BranchToken> getDispoBranches();

   List<String> getDispoBranchNames();

   Collection<DispoItem> findDispoItemByAnnoationText(BranchId branch, String setId, String keyword,
      boolean isDetailed);

   DispoConfig findDispoConfig(BranchId branch);

   ArtifactId getDispoItemParentSet(BranchId branch, String itemId);

   HashMap<ArtifactReadable, BranchId> getCiSet(CiSetData setData);

   List<CiSetData> getAllCiSets();

   String getDispoItemId(BranchId branch, String setId, String item);

}
