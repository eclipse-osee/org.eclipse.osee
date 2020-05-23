/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.core.ds;

/**
 * @author Roberto E. Escobar
 */
public class SelectSet implements Cloneable {

   private long limit = -1;
   private DynamicData data;

   public long getLimit() {
      return limit;
   }

   public void setLimit(long limit) {
      this.limit = limit;
   }

   public DynamicData getData() {
      return data;
   }

   public void reset() {
      limit = -1;
      data = null;
   }

   public void setData(DynamicData data) {
      this.data = data;
   }
}