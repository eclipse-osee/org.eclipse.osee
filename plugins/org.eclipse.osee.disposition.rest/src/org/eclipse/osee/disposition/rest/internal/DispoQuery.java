/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.disposition.model.CiSetData;
import org.eclipse.osee.disposition.model.DispoConfig;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.orcs.data.ArtifactReadable;

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

   IOseeBranch findDispoProgramIdByName(String branchName);

   String findDispoSetIdByName(BranchId branch, String setName);

   List<DispoSet> findDispoSets(BranchId branch, String type);

   DispoSet findDispoSetsById(BranchId branch, String id);

   List<DispoItem> findDipoItems(BranchId branch, String setId, boolean isDetailed);

   DispoItem findDispoItemById(BranchId branch, String itemId);

   List<String> getCheckedReruns(HashMap<String, DispoItem> items, String setId);

   List<IOseeBranch> getDispoBranches();

   Collection<DispoItem> findDispoItemByAnnoationText(BranchId branch, String setId, String keyword, boolean isDetailed);

   DispoConfig findDispoConfig(BranchId branch);

   Long getDispoItemParentSet(BranchId branch, String itemId);

   HashMap<ArtifactReadable, BranchId> getCiSet(CiSetData setData);

   List<CiSetData> getAllCiSets();

   String getDispoItemId(BranchId branch, String setId, String item);

}
