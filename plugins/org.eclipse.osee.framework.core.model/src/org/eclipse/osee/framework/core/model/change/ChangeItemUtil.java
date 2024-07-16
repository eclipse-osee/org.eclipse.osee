/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.model.change;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public final class ChangeItemUtil {

   private ChangeItemUtil() {
      // Utility Class
   }

   public static ChangeItem newArtifactChange(ArtifactId artId, ArtifactTypeId artTypeId, GammaId currentSourceGammaId, ModificationType currentSourceModType, ApplicabilityToken appToken, TransactionToken txToken) {
      ChangeItem item = new ChangeItem();
      item.setChangeType(ChangeType.Artifact);

      item.setItemId(artId);
      item.setItemTypeId(artTypeId);
      item.setSynthetic(false);

      ChangeVersion current = item.getCurrentVersion();
      current.setGammaId(currentSourceGammaId);
      current.setModType(currentSourceModType);
      current.setApplicabilityToken(appToken);
      current.setTransactionToken(txToken);

      item.setArtId(artId);
      return item;
   }

   public static ChangeItem newAttributeChange(AttributeId attrId, AttributeTypeId attrTypeId, ArtifactId artId, GammaId currentSourceGammaId, ModificationType currentSourceModType, String value, ApplicabilityToken appToken, TransactionToken txToken) {
      ChangeItem item = new ChangeItem();
      item.setChangeType(ChangeType.Attribute);

      item.setItemId(attrId);
      item.setItemTypeId(attrTypeId);
      item.setSynthetic(false);

      ChangeVersion current = item.getCurrentVersion();
      current.setGammaId(currentSourceGammaId);
      current.setModType(currentSourceModType);
      current.setApplicabilityToken(appToken);
      current.setTransactionToken(txToken);

      item.setArtId(artId);
      item.getCurrentVersion().setValue(value);
      return item;
   }

   public static ChangeItem newRelationChange(RelationId relLinkId, RelationTypeToken relTypeId, GammaId currentSourceGammaId, ModificationType currentSourceModType, ArtifactId aArtId, ArtifactId bArtId, String rationale, ApplicabilityToken appToken, TransactionToken txToken) {
      ChangeItem item = new ChangeItem();
      item.setChangeType(ChangeType.Relation);

      item.setItemId(relLinkId);
      item.setItemTypeId(relTypeId);
      item.setSynthetic(false);

      ChangeVersion current = item.getCurrentVersion();
      current.setGammaId(currentSourceGammaId);
      current.setModType(currentSourceModType);
      current.setApplicabilityToken(appToken);
      current.setTransactionToken(txToken);

      item.setArtId(aArtId);
      item.setArtIdB(bArtId);
      item.getCurrentVersion().setValue(rationale);
      return item;
   }

   public static ChangeItem newTupleChange(TupleTypeId tupleTypeId, GammaId gammaId, ApplicabilityToken appToken, ModificationType currentSourceModType, TransactionToken txToken, Long... e) {
      ChangeItem item = new ChangeItem();
      item.setChangeType(ChangeType.Tuple);

      item.setItemId(gammaId);
      item.setItemTypeId(tupleTypeId);
      item.setSynthetic(false);

      ChangeVersion current = item.getCurrentVersion();
      current.setGammaId(gammaId);
      current.setModType(currentSourceModType);
      current.setApplicabilityToken(appToken);
      current.setTransactionToken(txToken);

      if (e.length == 2) {
         item.getCurrentVersion().setValue(String.format("Tuple2|%s, %s", e[0], e[1]));
      } else if (e.length == 3) {
         item.getCurrentVersion().setValue(String.format("Tuple3|%s, %s, %s", e[0], e[1], e[2]));
      } else if (e.length == 4) {
         item.getCurrentVersion().setValue(String.format("Tuple4|%s, %s, %s, %s", e[0], e[1], e[2], e[3]));
      }
      return item;
   }

   public static ChangeVersion getStartingVersion(ChangeItem item) {
      if (item == null) {
         throw new OseeArgumentException("ChangeItem cannot be null");
      }
      ChangeVersion toReturn = item.getBaselineVersion();
      if (!toReturn.isValid()) {
         toReturn = item.getFirstNonCurrentChange();
         if (!toReturn.isValid()) {
            toReturn = item.getCurrentVersion();
            if (!toReturn.isValid()) {
               throw new OseeStateException("Cannot find a valid starting point for change item: %s", item);
            }
         }
      }
      return toReturn;
   }

   public static void copy(ChangeVersion source, ChangeVersion dest) {
      Conditions.checkNotNull(source, "Source ChangeVersion");
      Conditions.checkNotNull(dest, "Destination ChangeVersion");
      dest.copy(source);
   }

   public static boolean isModType(ChangeVersion changeVersion, ModificationType matchModType) {
      return changeVersion != null && changeVersion.getModType().equals(matchModType);
   }

   public static boolean isNew(ChangeVersion changeVersion) {
      return isModType(changeVersion, ModificationType.NEW);
   }

   public static boolean isIntroduced(ChangeVersion changeVersion) {
      return isModType(changeVersion, ModificationType.INTRODUCED);
   }

   public static boolean isDeleted(ChangeVersion changeVersion) {
      return changeVersion != null && changeVersion.getModType().isDeleted();
   }

   public static boolean isArtifactDeleted(ChangeVersion changeVersion) {
      return changeVersion != null && changeVersion.getModType().isArtifactDeleted();
   }

   public static boolean wasNewOnSource(ChangeItem changeItem) {
      return isNew(changeItem.getFirstNonCurrentChange()) || isNew(changeItem.getCurrentVersion());
   }

   public static boolean wasIntroducedOnSource(ChangeItem changeItem) {
      return isIntroduced(changeItem.getFirstNonCurrentChange()) || isIntroduced(changeItem.getCurrentVersion());
   }

   public static boolean hasBeenReplacedWithVersion(ChangeItem changeItem) {
      boolean results = areGammasEqual(changeItem.getCurrentVersion(), changeItem.getBaselineVersion()) && //
         isModType(changeItem.getCurrentVersion(), ModificationType.MODIFIED);
      return results;
   }

   public static boolean isAlreadyOnDestination(ChangeItem changeItem) {
      return areGammasEqual(changeItem.getCurrentVersion(), changeItem.getDestinationVersion()) && //
         areModTypesEqual(changeItem.getCurrentVersion(), changeItem.getDestinationVersion()) && //
         areApplicabilitiesEqual(changeItem.getCurrentVersion(), changeItem.getDestinationVersion());
   }

   public static boolean areModTypesEqual(ChangeVersion object1, ChangeVersion object2) {
      boolean result = false;
      if (object1 == null && object2 == null) {
         result = true;
      } else if (object1 != null && object2 != null) {
         result = object1.getModType().equals(object2.getModType());
      }
      return result;
   }

   public static boolean areGammasEqual(ChangeVersion object1, ChangeVersion object2) {
      boolean result = false;
      if (object1 == null && object2 == null) {
         result = true;
      } else if (object1 != null && object2 != null) {
         if (object1.getGammaId() == object2.getGammaId()) {
            result = true;
         } else if (object1.getGammaId() != null) {
            result = object1.getGammaId().equals(object2.getGammaId());
         }
      }
      return result;
   }

   public static boolean areApplicabilitiesEqual(ChangeVersion object1, ChangeVersion object2) {
      boolean result = false;
      if (object1 == null && object2 == null) {
         result = true;
      } else if (object1 != null && object2 != null) {
         if (object1.getApplicabilityToken() == object2.getApplicabilityToken()) {
            result = true;
         } else if (object1.getApplicabilityToken() != null) {
            result = object1.getApplicabilityToken().equals(object2.getApplicabilityToken());
         }
      }
      return result;
   }

   public static ChangeItem splitForApplicability(ChangeItem source) {
      ChangeItem dest = new ChangeItem();
      dest.copy(source);
      dest.setApplicabilityCopy(true);
      return dest;
   }

   public static void checkAndSetIgnoreCase(ChangeItem changeItem) {
      boolean changed = false;
      changed |= createdAndDeleted(changeItem);
      changed |= wasAlreadyOnDestination(changeItem);
      changed |= deletedAndDoesNotExistInDestination(changeItem);
      changed |= deletedOnDestAndNotResurrected(changeItem);
      changed |= replacedWithVerAndNotRessurected(changeItem);
      if (!changed) {
         changeItem.setIgnoreType(ChangeIgnoreType.NONE);
      }
   }

   public static boolean wasAlreadyOnDestination(ChangeItem changeItem) {
      if (isAlreadyOnDestination(changeItem)) {
         changeItem.setIgnoreType(ChangeIgnoreType.ALREADY_ON_DESTINATION);
         return true;
      }
      return false;
   }

   public static boolean createdAndDeleted(ChangeItem changeItem) {
      if (wasCreatedAndDeleted(changeItem)) {
         changeItem.setIgnoreType(ChangeIgnoreType.CREATED_AND_DELETED);
         return true;
      }
      return false;
   }

   public static boolean deletedAndDoesNotExistInDestination(ChangeItem changeItem) {
      if (!changeItem.getDestinationVersion().isValid() && isDeleted(changeItem.getCurrentVersion())) {
         changeItem.setIgnoreType(ChangeIgnoreType.DELETED_AND_DNE_ON_DESTINATION);
         return true;
      }
      return false;
   }

   public static boolean beenDeletedInDestination(ChangeItem changeItem) {
      if (hasBeenDeletedInDestination(changeItem)) {
         changeItem.setIgnoreType(ChangeIgnoreType.DELETED_ON_DESTINATION);
         return true;
      }
      return false;
   }

   public static boolean deletedOnDestAndNotResurrected(ChangeItem changeItem) {
      if (hasBeenDeletedInDestination(changeItem) && !isResurrected(changeItem)) {
         changeItem.setIgnoreType(ChangeIgnoreType.DELETED_ON_DEST_AND_NOT_RESURRECTED);
         return true;
      }
      return false;
   }

   public static boolean replacedWithVerAndNotRessurected(ChangeItem changeItem) {
      if (hasBeenReplacedWithVersion(changeItem) && !isResurrected(changeItem)) {
         changeItem.setIgnoreType(ChangeIgnoreType.REPLACED_WITH_VERSION_AND_NOT_RESURRECTED);
         return true;
      }
      return false;
   }

   public static boolean resurrected(ChangeItem changeItem) {
      if (isResurrected(changeItem)) {
         changeItem.setIgnoreType(ChangeIgnoreType.RESURRECTED);
         return true;
      }
      return false;
   }

   public static boolean wasCreatedAndDeleted(ChangeItem changeItem) {
      return !changeItem.getBaselineVersion().isValid() && isDeleted(changeItem.getCurrentVersion());
   }

   public static boolean isDeletedAndDoesNotExistInDestination(ChangeItem changeItem) {
      return !changeItem.getDestinationVersion().isValid() && isDeleted(changeItem.getCurrentVersion());
   }

   public static boolean isResurrected(ChangeItem changeItem) {
      // There's a change corresponding to a Deleted Item, item MUST have been resurrected
      return changeItem.getBaselineVersion().isValid() && isDeleted(changeItem.getBaselineVersion());

   }

   public static boolean hasBeenDeletedInDestination(ChangeItem changeItem) {
      return changeItem.getDestinationVersion().isValid() && isDeleted(changeItem.getDestinationVersion());
   }

   public static boolean hasValueChange(ChangeItem changeItem) {
      if (changeItem.getCurrentVersion().isValid() && changeItem.getDestinationVersion().isValid() && changeItem.getCurrentVersion().getValue() != null && changeItem.getDestinationVersion().getValue() != null) {
         if (!changeItem.getCurrentVersion().getValue().equals(changeItem.getDestinationVersion().getValue())) {
            return true;
         }
      }
      return false;
   }

   public static boolean hasApplicabilityChange(ChangeItem changeItem) {
      if (changeItem.isApplicabilityCopy()) {
         return true;
      }
      if (changeItem.getCurrentVersion().isValid() && changeItem.getDestinationVersion().isValid()) {
         if (!areApplicabilitiesEqual(changeItem.getCurrentVersion(), changeItem.getDestinationVersion())) {
            return true;
         }
      }
      if (changeItem.getCurrentVersion().isValid() && !changeItem.getDestinationVersion().isValid()) {
         return true;
      }
      return false;
   }

   public static boolean hasApplicabilityOnlyChange(ChangeItem changeItem) {
      if (changeItem.isApplicabilityCopy()) {
         return false;
      }
      if (isDeleted(changeItem.getCurrentVersion())) {
         return false;
      }
      if (changeItem.getCurrentVersion().isValid() && changeItem.getDestinationVersion().isValid()) {
         if (!areApplicabilitiesEqual(changeItem.getCurrentVersion(), changeItem.getDestinationVersion())) {
            return true;
         }
      }
      return false;
   }

   public static void computeNetChanges(List<ChangeItem> changes) {
      for (ChangeItem change : changes) {
         checkAndSetIgnoreCase(change);
         if (change.getNetChange().getModType().equals(ModificationType.MERGED)) {
            if (isDeleted(change.getCurrentVersion())) {
               change.getNetChange().copy(change.getCurrentVersion());
            }
         } else {
            ModificationType netModType = calculateNetWithDestinationBranch(change);
            if (netModType == null) {
               throw new OseeStateException("Net Mod Type was null");
            }
            change.getNetChange().copy(change.getCurrentVersion());
            change.getNetChange().setModType(netModType);
         }
      }
   }

   public static List<ChangeItem> computeNetChangesAndFilter(List<ChangeItem> changes) {
      ChangeItemUtil.computeNetChanges(changes);
      List<ChangeItem> allowedChanges = new ArrayList<>();
      for (ChangeItem item : changes) {
         if (isAllowableChange(item.getIgnoreType())) {
            allowedChanges.add(item);
         }
      }
      return allowedChanges;
   }

   private static boolean isAllowableChange(ChangeIgnoreType type) {
      return type.isNone() || type.isResurrected();
   }

   private static ModificationType calculateNetWithDestinationBranch(ChangeItem change) {
      ModificationType netModType = change.getCurrentVersion().getModType();
      if (change.getDestinationVersion().isValid() && (change.getBaselineVersion().isValid() || change.getFirstNonCurrentChange().isValid())) {
         netModType = change.getCurrentVersion().getModType();
      } else if (ChangeItemUtil.wasNewOnSource(change)) {
         netModType = ModificationType.NEW;
      } else if (ChangeItemUtil.wasIntroducedOnSource(change)) {
         netModType = ModificationType.INTRODUCED;
      } else if (!change.getDestinationVersion().isValid()) {
         if (change.getBaselineVersion().isValid()) {
            // Case when committing into non-parent
            netModType = ModificationType.INTRODUCED;
         } else {
            netModType = ModificationType.NEW;
         }
      }
      return netModType;
   }

   public static Collection<ArtifactId> getArtifacts(Collection<ChangeItem> changes, ChangeType changeItemType, ModificationType... modificationType) {
      List<ArtifactId> artifacts = new ArrayList<>();
      for (ChangeItem change : changes) {
         if (change.getChangeType().equals(changeItemType)) {
            ModificationType changeModType = change.getCurrentVersion().getModType();
            for (ModificationType modType : modificationType) {
               if (changeModType.equals(modType)) {
                  if (!artifacts.contains(change.getArtId())) {
                     artifacts.add(change.getArtId());
                     break;
                  }
               }
            }
         }
      }
      return artifacts;
   }

}