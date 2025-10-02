/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.framework.skynet.core.access;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchService;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Donald G. Dunne
 */
public class BranchServiceImpl implements BranchService {

   private static BranchServiceImpl instance;
   private TransactionId currUserModTx = TransactionId.SENTINEL;
   private Set<BranchId> currUserFavoriteBranchIds = null;

   public BranchServiceImpl() {
   }

   public static BranchService getInstance() {
      if (instance == null) {
         instance = new BranchServiceImpl();
      }
      return instance;
   }

   @Override
   public void toggleFavoriteBranch(BranchId favoriteBranch) {
      Conditions.checkNotNull(favoriteBranch, "Branch");
      HashSet<BranchId> branches = new HashSet<>(
         BranchManager.getBranches(BranchArchivedState.UNARCHIVED, BranchType.WORKING, BranchType.BASELINE));

      Artifact userArt = OseeApiService.userArt();
      boolean found = false;
      Collection<Attribute<String>> attributes = userArt.getAttributes(CoreAttributeTypes.FavoriteBranch);
      for (Attribute<String> attribute : attributes) {
         // Remove attributes that are no longer valid
         BranchId branch;
         try {
            branch = BranchId.valueOf(attribute.getValue());
         } catch (Exception ex) {
            continue;
         }
         if (!branches.contains(branch)) {
            attribute.delete();
         } else if (favoriteBranch.equals(branch)) {
            attribute.delete();
            found = true;
            // Do not break here in case there are multiples of same branch
         }
      }

      if (!found) {
         userArt.addAttribute(CoreAttributeTypes.FavoriteBranch, favoriteBranch.getIdString());
      }
      userArt.persist("Toggle Favorite Branch");
   }

   /**
    * Cached for efficiency as this is called thousands of times to sort favorites. Cache is updated when User artifact
    * is changed (eg: transaction in User art is different)
    */
   @Override
   public boolean isFavoriteBranch(BranchId branch) {
      Artifact art = OseeApiService.userArt();
      if (!art.getTransaction().equals(currUserModTx)) {
         if (currUserFavoriteBranchIds == null) {
            currUserFavoriteBranchIds = new HashSet<>();
         } else {
            currUserFavoriteBranchIds.clear();
         }
         for (Attribute<Object> attri : art.getAttributes(CoreAttributeTypes.FavoriteBranch)) {
            currUserFavoriteBranchIds.add(BranchId.valueOf((String) attri.getValue()));
         }
         currUserModTx = art.getTransaction();
      }
      return currUserFavoriteBranchIds.contains(branch);
   }

}
