/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.task;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class JaxRelation {

   private String relationTypeName;
   private boolean sideA;
   private List<Long> relatedIds;

   public String getRelationTypeName() {
      return relationTypeName;
   }

   public void setRelationTypeName(String relationTypeName) {
      this.relationTypeName = relationTypeName;
   }

   public boolean isSideA() {
      return sideA;
   }

   public void setSideA(boolean sideA) {
      this.sideA = sideA;
   }

   public List<Long> getRelatedIds() {
      if (relatedIds == null) {
         relatedIds = new LinkedList<>();
      }
      return relatedIds;
   }

   public void setRelatedIds(List<Long> relatedIds) {
      this.relatedIds = relatedIds;
   }

   @Override
   public String toString() {
      return "JaxRelation [type=" + relationTypeName + ", sideA=" + sideA + ", relatedIds=" + relatedIds + "]";
   }

}
