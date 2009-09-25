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

import java.util.List;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrder;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderId;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderStore;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrdering;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationSorter {

   private final RelationType type;
   private final RelationSide side;
   private final Artifact artifact;
   RelationOrderStore store = null;

   public RelationSorter(RelationType type, RelationSide side, Artifact artifact) throws OseeWrappedException, OseeCoreException {
      this.type = type;
      this.side = side;
      this.artifact = artifact;
      if (artifact != null) {
         store = new RelationOrderStore(artifact);
      }
   }

   public RelationSorter(RelationType type, RelationSide side) throws OseeWrappedException, OseeCoreException {
      this(type, side, null);
   }

   public RelationSorter(String typeName, String sideName) throws OseeCoreException {
      this.type = RelationTypeManager.getType(typeName);
      this.side = type.isSideAName(sideName) ? RelationSide.SIDE_A : RelationSide.SIDE_B;
      this.artifact = null;
   }

   public RelationType getRelationType() {
      return type;
   }

   public RelationSide getSide() {
      return side;
   }

   public String getSideName() {
      return type.getSideName(side);
   }

   public String getName() {
      return type.getName();
   }

   public boolean isSideA() {
      return side == RelationSide.SIDE_A;
   }

   public boolean isThisType(RelationLink link) {
      return link.getRelationType() == type;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public boolean equals(Object arg0) {
      if (arg0 instanceof RelationSorter) {
         RelationSorter arg = (RelationSorter) arg0;
         if (artifact == null && arg.artifact == null) {
            return type.equals(arg.type) && side.equals(arg.side);
         } else if (artifact == null) {
            return false;
         } else if (arg.artifact == null) {
            return false;
         } else {
            return type.equals(arg.type) && side.equals(arg.side) && artifact.equals(arg.artifact);
         }
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hashCode = 11;
      hashCode = hashCode * 31 + type.hashCode();
      hashCode = hashCode * 31 + side.hashCode();
      if (artifact != null) {
         hashCode = hashCode * 31 + artifact.hashCode();
      }
      return hashCode;
   }

   public RelationOrderId getOrderId() {
      String orderGuid = getOrderGuid();
      return RelationOrdering.getInstance().getOrderId(orderGuid);
   }

   public String getOrderGuid() {
      String orderGuid = store.getOrderGuid(type.getName(), side);
      if (orderGuid == null) {
         orderGuid = type.getDefaultOrderTypeGuid();
      }
      return orderGuid;
   }

   public String getOrderName() {
      RelationOrderId id = getOrderId();
      return id.prettyName();
   }

   public void setOrder(RelationOrderId orderId, List<Artifact> relatives) throws OseeCoreException {
      RelationOrder order = RelationOrdering.getInstance().getRelationOrder(orderId.getGuid());
      order.applyOrder(artifact, type, side, relatives);
   }
}
