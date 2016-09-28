/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.relation.order;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.enums.RelationSorter;

/**
 * @author Roberto E. Escobar
 */
public class OrderData {

   private RelationSorter sorterId;
   private List<String> guids;

   public OrderData(RelationSorter sorterId, List<String> guids) {
      super();
      this.sorterId = sorterId;
      this.guids = guids;
   }

   public void setSorterId(RelationSorter sorterId) {
      this.sorterId = sorterId;
   }

   public RelationSorter getSorterId() {
      return sorterId;
   }

   public void setOrderIds(List<String> guids) {
      this.guids = guids;
   }

   public List<String> getOrderIds() {
      return guids != null ? guids : Collections.<String> emptyList();
   }

   @Override
   public String toString() {
      return "OrderData [sorterId=" + sorterId + ", guids=" + guids + "]";
   }
}
