/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.orcs.core.ds.criteria;

import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Luciano Vaglienti
 */
public class CriteriaPagination extends Criteria {

   private final long pageNum;
   private final long pageSize;

   public CriteriaPagination(long pageNum, long pageSize) {
      this.pageSize = pageSize;
      this.pageNum = pageNum;
   }

   /**
    * @return the pageSize
    */
   public long getPageSize() {
      return pageSize;
   }

   /**
    * @return the pageNum
    */
   public long getPageNum() {
      return pageNum;
   }

   @Override
   public void checkValid(Options options) {
      Conditions.checkNotNull(pageNum, "pageNum");
      Conditions.checkNotNull(pageSize, "pageSize");
      Conditions.assertNotEquals((int) pageNum, 0, "PageNum is equal to 0");
   }

   @Override
   public String toString() {
      return getClass().getSimpleName() + " " + pageNum + " " + pageSize;
   }

   public boolean isValid() {
      if (Conditions.notNull(pageSize) && pageSize > 0) {
         return true;
      }
      return false;
   }

}
