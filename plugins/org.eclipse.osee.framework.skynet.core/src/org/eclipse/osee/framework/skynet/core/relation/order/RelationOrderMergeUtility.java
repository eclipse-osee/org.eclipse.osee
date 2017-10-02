/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *public static final CoreAttributeTypes   Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.relation.order;

import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

public class RelationOrderMergeUtility {

   public static RelationOrderData mergeRelationOrder(Artifact left, Artifact right)  {
      RelationOrderFactory factory = new RelationOrderFactory();
      RelationOrderData leftData = factory.createRelationOrderData(left);
      RelationOrderData rightData = factory.createRelationOrderData(right);
      RelationOrderData mergedData =
         new RelationOrderData(new ArtifactRelationOrderAccessor(new RelationOrderParser()), right);

      for (Pair<RelationTypeToken, RelationSide> typeSide : getAllTypeSides(leftData, rightData)) {
         RelationType type = RelationTypeManager.getType(typeSide.getFirst());
         RelationSide side = typeSide.getSecond();
         RelationTypeSide rts = new RelationTypeSide(type, side);
         RelationSorter leftSorter = leftData.getCurrentSorterGuid(type, side);
         RelationSorter rightSorter = rightData.getCurrentSorterGuid(type, side);

         List<String> order = null;
         if (rightSorter.equals(USER_DEFINED) && leftSorter.equals(USER_DEFINED)) {
            order = mergeTypeSideOrder(left, right, rts);
         }
         if (order == null) {
            return null;
         }
         mergedData.addOrderList(type, side, leftSorter, order);
      }

      return mergedData;
   }

   private static Collection<Pair<RelationTypeToken, RelationSide>> getAllTypeSides(RelationOrderData leftData, RelationOrderData rightData) {
      Collection<Pair<RelationTypeToken, RelationSide>> rts = new HashSet<>();
      rts.addAll(leftData.getAvailableTypeSides());
      rts.addAll(rightData.getAvailableTypeSides());
      return rts;
   }

   private static List<String> mergeTypeSideOrder(Artifact left, Artifact right, RelationTypeSide rts)  {
      RelationOrderMerger<String> merger = new RelationOrderMerger<>();
      List<String> leftRelatives = getGuidList(left.getRelatedArtifacts(rts, DeletionFlag.EXCLUDE_DELETED));
      List<String> rightRelatives = getGuidList(right.getRelatedArtifacts(rts, DeletionFlag.EXCLUDE_DELETED));
      Collection<String> mergedSet = getMergedSet(left, right, rts);

      return merger.computeMergedOrder(leftRelatives, rightRelatives, mergedSet);
   }

   private static Collection<String> getMergedSet(Artifact left, Artifact right, RelationTypeSide relationTypeSide)  {
      Collection<String> mergedSet = new HashSet<>();
      Collection<String> deleted = new HashSet<>();
      List<String> leftRelatives =
         getGuidList(left.getRelatedArtifacts(relationTypeSide, DeletionFlag.EXCLUDE_DELETED));
      List<String> rightRelatives =
         getGuidList(right.getRelatedArtifacts(relationTypeSide, DeletionFlag.EXCLUDE_DELETED));

      deleted.addAll(getDeleted(left, relationTypeSide));
      deleted.addAll(getDeleted(right, relationTypeSide));

      mergedSet.addAll(leftRelatives);
      mergedSet.addAll(rightRelatives);
      mergedSet.removeAll(deleted);
      return mergedSet;
   }

   private static List<String> getGuidList(List<Artifact> artifacts) {
      List<String> toReturn = new ArrayList<>();
      for (Artifact art : artifacts) {
         toReturn.add(art.getGuid());
      }
      return toReturn;
   }

   private static Collection<String> getDeleted(Artifact art, RelationTypeSide relationType)  {
      Collection<String> toReturn = new HashSet<>();

      for (RelationLink link : art.getRelationsAll(DeletionFlag.INCLUDE_DELETED)) {
         if (link.isOfType(relationType) && link.isDeleted()) {
            if (link.getOppositeSide(art).equals(relationType.getSide())) {
               toReturn.add(link.getArtifactOnOtherSide(art).getGuid());
            }
         }
      }

      for (Artifact relative : art.getRelatedArtifacts(relationType, DeletionFlag.INCLUDE_DELETED)) {
         if (relative.isDeleted()) {
            toReturn.add(relative.getGuid());
         }
      }

      return toReturn;
   }
}
