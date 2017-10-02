/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model.change;

import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeId;
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

   public static ChangeItem newArtifactChange(ArtifactId artId, ArtifactTypeId artTypeId, GammaId currentSourceGammaId, ModificationType currentSourceModType, ApplicabilityToken appToken) {
      ChangeItem item = new ChangeItem();
      item.setChangeType(ChangeType.ARTIFACT_CHANGE);

      item.setItemId(artId);
      item.setItemTypeId(artTypeId);
      item.setSynthetic(false);

      ChangeVersion current = item.getCurrentVersion();
      current.setGammaId(currentSourceGammaId);
      current.setModType(currentSourceModType);
      current.setApplicabilityToken(appToken);

      item.setArtId(artId);
      return item;
   }

   public static ChangeItem newAttributeChange(AttributeId attrId, AttributeTypeId attrTypeId, ArtifactId artId, GammaId currentSourceGammaId, ModificationType currentSourceModType, String value, ApplicabilityToken appToken) {
      ChangeItem item = new ChangeItem();
      item.setChangeType(ChangeType.ATTRIBUTE_CHANGE);

      item.setItemId(attrId);
      item.setItemTypeId(attrTypeId);
      item.setSynthetic(false);

      ChangeVersion current = item.getCurrentVersion();
      current.setGammaId(currentSourceGammaId);
      current.setModType(currentSourceModType);
      current.setApplicabilityToken(appToken);

      item.setArtId(artId);
      item.getCurrentVersion().setValue(value);
      return item;
   }

   public static ChangeItem newRelationChange(RelationId relLinkId, RelationTypeId relTypeId, GammaId currentSourceGammaId, ModificationType currentSourceModType, ArtifactId aArtId, ArtifactId bArtId, String rationale, ApplicabilityToken appToken) {
      ChangeItem item = new ChangeItem();
      item.setChangeType(ChangeType.RELATION_CHANGE);

      item.setItemId(relLinkId);
      item.setItemTypeId(relTypeId);
      item.setSynthetic(false);

      ChangeVersion current = item.getCurrentVersion();
      current.setGammaId(currentSourceGammaId);
      current.setModType(currentSourceModType);
      current.setApplicabilityToken(appToken);

      item.setArtId(aArtId);
      item.setArtIdB(bArtId);
      item.getCurrentVersion().setValue(rationale);
      return item;
   }

   public static ChangeItem newTupleChange(TupleTypeId tupleTypeId, GammaId gammaId, ApplicabilityToken appToken, Long... e) {
      ChangeItem item = new ChangeItem();
      item.setChangeType(ChangeType.TUPLE_CHANGE);

      item.setItemId(gammaId);
      item.setItemTypeId(tupleTypeId);
      item.setSynthetic(false);

      ChangeVersion current = item.getCurrentVersion();
      current.setGammaId(gammaId);
      current.setModType(ModificationType.MODIFIED);
      current.setApplicabilityToken(appToken);

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
      return changeVersion != null && changeVersion.getModType() == matchModType;
   }

   public static boolean isNew(ChangeVersion changeVersion) {
      return isModType(changeVersion, ModificationType.NEW);
   }

   public static boolean isIntroduced(ChangeVersion changeVersion) {
      return isModType(changeVersion, ModificationType.INTRODUCED);
   }

   public static boolean isDeleted(ChangeVersion changeVersion) {
      return changeVersion != null && changeVersion.getModType() != null && changeVersion.getModType().isDeleted();
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
         if (object1.getModType() == object2.getModType()) {
            result = true;
         } else if (object1.getModType() != null) {
            result = object1.getModType().equals(object2.getModType());
         }
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
}
