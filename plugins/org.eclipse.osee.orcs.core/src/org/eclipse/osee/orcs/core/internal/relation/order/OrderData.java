/*********************************************************************
 * Copyright (c) 2012 Boeing
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
