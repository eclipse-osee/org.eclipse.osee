/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.task;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class JaxRelation {

   private String relationTypeName;
   private boolean sideA;
   private List<Long> relatedUuids;

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

   public List<Long> getRelatedUuids() {
      if (relatedUuids == null) {
         relatedUuids = new LinkedList<>();
      }
      return relatedUuids;
   }

   public void setRelatedUuids(List<Long> relatedUuids) {
      this.relatedUuids = relatedUuids;
   }

   @Override
   public String toString() {
      return "JaxRelation [type=" + relationTypeName + ", sideA=" + sideA + ", relatedUuids=" + relatedUuids + "]";
   }

}
