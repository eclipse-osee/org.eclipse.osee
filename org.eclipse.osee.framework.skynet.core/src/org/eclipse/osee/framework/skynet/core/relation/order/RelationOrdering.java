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
package org.eclipse.osee.framework.skynet.core.relation.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Andrew M. Finkbeiner
 */
public class RelationOrdering {

   private final Map<String, RelationOrder> orderMap;
   private static final RelationOrdering instance = new RelationOrdering();

   public static RelationOrdering getInstance() {
      return instance;
   }

   private RelationOrdering() {
      orderMap = new ConcurrentHashMap<String, RelationOrder>();
      registerOrderType(new Lexicographical(false, RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC));
      registerOrderType(new Lexicographical(true, RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC));
      registerOrderType(new Unordered());
      registerOrderType(new UserDefinedOrder());
   }

   public void registerOrderType(RelationOrder order) {
      orderMap.put(order.getOrderId().getGuid(), order);
   }

   public void sort(Artifact artifact, RelationType type, RelationSide side, List<Artifact> relatives) throws OseeCoreException {
      if (type == null) {
         return;
      }
      String orderGuid = getOrderGuid(artifact, type, side);
      RelationOrder order = getRelationOrder(orderGuid);
      if (order == null) {
         return;
      }
      order.sort(artifact, type, side, relatives);
   }

   public RelationOrder getRelationOrder(String orderGuid) throws OseeCoreException {
      RelationOrder order = orderMap.get(orderGuid);
      if (order == null && !orderGuid.contains("debug")) {
         throw new OseeCoreException(String.format("Unable to locate RelationOrder for guid[%s].", orderGuid));
      }
      return order;
   }

   private String getOrderGuid(Artifact artifact, RelationType type, RelationSide side) throws OseeCoreException {
      RelationOrderStore relationOrderXmlProcessor = new RelationOrderStore(artifact);
      String relationOrderGuid = relationOrderXmlProcessor.findRelationOrderGuid(type.getName(), side);
      if (relationOrderGuid != null) {
         return relationOrderGuid;
      }

      return type.getDefaultOrderTypeGuid();
   }

   public void setOrder(Artifact artifact, RelationType type, RelationSide side, RelationOrderId orderId, List<Artifact> relatives) throws OseeCoreException {
      RelationOrder order = getRelationOrder(orderId.getGuid());
      order.applyOrder(artifact, type, side, relatives);
   }

   public void updateOrderOnRelationDelete(Artifact artifact, RelationType type, RelationSide side, List<Artifact> relatives) throws OseeCoreException {
      Attribute<String> attribute = artifact.getSoleAttribute(CoreAttributes.RELATION_ORDER.getName());
      if (attribute != null) {
         RelationOrderStore relationOrderXmlProcessor = new RelationOrderStore(attribute.getValue());
         String relationOrderGuid = relationOrderXmlProcessor.findRelationOrderGuid(type.getName(), side);
         if (relationOrderGuid != null) {
            RelationOrder order = getRelationOrder(relationOrderGuid);
            if (order != null) {
               order.applyOrder(artifact, type, side, relatives);
            }
         }
      }
   }

   public List<RelationOrderId> getRegisteredRelationOrderIds() {
      Collection<RelationOrder> relationOrder = orderMap.values();
      List<RelationOrderId> ids = new ArrayList<RelationOrderId>();
      for (RelationOrder order : relationOrder) {
         ids.add(order.getOrderId());
      }
      Collections.sort(ids, new RelationOrderIdComparator());
      return ids;
   }

   public RelationOrderId getOrderId(String wantedOrderGuid) {
      List<RelationOrderId> allOrderIds = getAllRelationOrderIds();
      for (RelationOrderId currentOrderId : allOrderIds) {
         if (currentOrderId.getGuid().equals(wantedOrderGuid)) {
            return currentOrderId;
         }
      }

      return null;
   }

   public List<RelationOrderId> getAllRelationOrderIds() {
      Collection<RelationOrder> relationOrder = orderMap.values();
      List<RelationOrderId> ids = new ArrayList<RelationOrderId>();
      for (RelationOrder order : relationOrder) {
         ids.add(order.getOrderId());
      }
      Collections.sort(ids, new RelationOrderIdComparator());
      return ids;
   }
}
