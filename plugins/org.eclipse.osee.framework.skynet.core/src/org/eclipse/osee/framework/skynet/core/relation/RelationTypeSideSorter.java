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

package org.eclipse.osee.framework.skynet.core.relation;

import static org.eclipse.osee.framework.core.enums.RelationSorter.PREEXISTING;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationSorterProvider;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan Schmitt
 */
public class RelationTypeSideSorter extends RelationTypeSide {
   private final RelationType relationType;
   private final RelationOrderData orderData;
   private final RelationSorterProvider sorterProvider;
   private final Map<Artifact, RelationLink> artToRelation = new HashMap<>();

   public RelationTypeSideSorter(RelationType relationType, RelationSide side, RelationSorterProvider sorterProvider, RelationOrderData orderData) {
      super(relationType, side);
      this.relationType = relationType;
      this.sorterProvider = sorterProvider;
      this.orderData = orderData;
   }

   public Artifact getArtifact() {
      return orderData.getArtifact();
   }

   @Override
   public boolean equals(Object arg0) {
      if (arg0 instanceof RelationTypeSideSorter) {
         RelationTypeSideSorter arg = (RelationTypeSideSorter) arg0;
         if (getArtifact() == null && arg.getArtifact() == null) {
            return super.equals(arg);
         } else if (getArtifact() == null) {
            return false;
         } else if (arg.getArtifact() == null) {
            return false;
         } else {
            return getArtifact().equals(arg.getArtifact()) && super.equals(arg);
         }
      } else {
         return super.equals(arg0);
      }
   }

   public RelationSorter getSorterId() {
      return orderData.getCurrentSorterGuid(relationType, getSide());
   }

   public void sort(List<? extends ArtifactToken> listToOrder) {
      IRelationSorter order = sorterProvider.getRelationOrder(getSorterId());
      List<String> relativeOrder = orderData.getOrderList(getRelationType(), getSide());
      order.sort(listToOrder, relativeOrder);
   }

   public void sortRelations(List<? extends RelationLink> listToOrder) {
      IRelationSorter order = sorterProvider.getRelationOrder(getSorterId());
      List<String> relativeOrder = orderData.getOrderList(getRelationType(), getSide());
      order.sortRelations(listToOrder, relativeOrder);
   }

   public RelationLink getRelation(Artifact artifact) {
      return artToRelation.get(artifact);
   }

   public void addItem(RelationSorter sorterId, Artifact itemToAdd) {
      RelationSorter sorterIdToUse = sorterId;
      if (sorterIdToUse == PREEXISTING) {
         sorterIdToUse = getSorterId();
      }
      List<Artifact> relatives = Collections.emptyList();
      if (USER_DEFINED == sorterIdToUse) {
         Artifact target = getArtifact();
         relatives = target.getRelatedArtifacts(this);
         if (relatives.contains(itemToAdd)) {
            relatives.remove(itemToAdd);
         }
         relatives.add(itemToAdd);
      }
      setOrder(relatives, sorterIdToUse);
   }

   public void removeItem(ArtifactId itemToRemove) {
      List<Artifact> relatives = Collections.emptyList();
      if (USER_DEFINED == getSorterId()) {
         Artifact target = getArtifact();
         relatives = target.getRelatedArtifacts(this);
         if (relatives.contains(itemToRemove)) {
            relatives.remove(itemToRemove);
         }
      }
      setOrder(relatives, getSorterId());
   }

   public void setOrder(List<Artifact> relatives, RelationSorter sorterId) {
      orderData.store(relationType, getSide(), sorterId, relatives);
   }

   @Override
   public String toString() {
      RelationSorter sorterId = null;
      try {
         sorterId = getSorterId();
      } catch (Exception ex) {
         // Do Nothing;
      }
      return String.format("Relation Sorter {relationType=%s, relationSide=[%s], artifact=%s, sorterId=%s}",
         getRelationType(), getSide(), getArtifact(), sorterId);
   }
}
