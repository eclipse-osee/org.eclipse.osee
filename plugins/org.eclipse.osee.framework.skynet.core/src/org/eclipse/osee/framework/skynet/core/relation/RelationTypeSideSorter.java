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

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.RelationTypeSide;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationSorterProvider;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan Schmitt
 */
public class RelationTypeSideSorter extends RelationTypeSide {
   private final RelationType relationType;
   private final RelationOrderData orderData;
   private final RelationSorterProvider sorterProvider;

   public RelationTypeSideSorter(RelationType relationType, RelationSide side, RelationSorterProvider sorterProvider, RelationOrderData orderData) {
      super(relationType, side);
      this.relationType = relationType;
      this.sorterProvider = sorterProvider;
      this.orderData = orderData;
   }

   public Artifact getArtifact() throws OseeCoreException {
      return getIArtifact().getFullArtifact();
   }

   public IArtifact getIArtifact() {
      return orderData.getIArtifact();
   }

   @Override
   public boolean equals(Object arg0) {
      if (arg0 instanceof RelationTypeSideSorter) {
         RelationTypeSideSorter arg = (RelationTypeSideSorter) arg0;
         if (getIArtifact() == null && arg.getIArtifact() == null) {
            return super.equals(arg);
         } else if (getIArtifact() == null) {
            return false;
         } else if (arg.getIArtifact() == null) {
            return false;
         } else {
            return getIArtifact().equals(arg.getIArtifact()) && super.equals(arg);
         }
      } else {
         return super.equals(arg0);
      }
   }

   @Override
   public int hashCode() {
      int hashCode = 11;
      hashCode = hashCode * 31 + super.hashCode();
      if (getIArtifact() != null) {
         hashCode = hashCode * 31 + getIArtifact().hashCode();
      }
      return hashCode;
   }

   public RelationType getRelationTypeHard() {
      return relationType;
   }

   private IRelationSorter getSorter() throws OseeCoreException {
      String guid = orderData.getCurrentSorterGuid(getRelationTypeHard(), getSide());
      return sorterProvider.getRelationOrder(guid);
   }

   public RelationSorter getSorterId() throws OseeCoreException {
      return getSorter().getSorterId();
   }

   public String getSorterName() throws OseeCoreException {
      RelationSorter id = getSorterId();
      return id.getName();
   }

   public void sort(List<? extends IArtifact> listToOrder) throws OseeCoreException {
      IRelationSorter order = getSorter();
      List<String> relativeOrder = orderData.getOrderList(getRelationType(), getSide());
      order.sort(listToOrder, relativeOrder);
   }

   @SuppressWarnings("unchecked")
   public void addItem(RelationSorter sorterId, IArtifact itemToAdd) throws OseeCoreException {
      RelationSorter sorterIdToUse = sorterId;
      if (sorterIdToUse == RelationOrderBaseTypes.PREEXISTING) {
         sorterIdToUse = getSorterId();
      }
      List<IArtifact> relatives = Collections.emptyList();
      if (RelationOrderBaseTypes.USER_DEFINED == sorterIdToUse) {
         IArtifact target = getIArtifact();
         relatives = (List<IArtifact>) target.getRelatedArtifacts(this);
         if (relatives.contains(itemToAdd)) {
            relatives.remove(itemToAdd);
         }
         relatives.add(itemToAdd);
      }
      setOrder(relatives, sorterIdToUse);
   }

   @SuppressWarnings("unchecked")
   public void removeItem(RelationSorter sorterId, IArtifact itemToRemove) throws OseeCoreException {
      RelationSorter sorterIdToUse = sorterId;
      if (sorterIdToUse == null) {
         sorterIdToUse = getSorterId();
      }
      List<IArtifact> relatives = Collections.emptyList();
      if (RelationOrderBaseTypes.USER_DEFINED == sorterIdToUse) {
         IArtifact target = getIArtifact();
         relatives = (List<IArtifact>) target.getRelatedArtifacts(this);
         if (relatives.contains(itemToRemove)) {
            relatives.remove(itemToRemove);
         }
      }
      setOrder(relatives, sorterIdToUse);
   }

   public void setOrder(List<? extends IArtifact> relatives, RelationSorter sorterId) throws OseeCoreException {
      orderData.store(getRelationTypeHard(), getSide(), sorterId, relatives);
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
         getRelationType(), getSide(), getIArtifact(), sorterId);
   }
}
