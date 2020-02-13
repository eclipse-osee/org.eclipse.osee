/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Donald G. Dunne
 */
public class RecentlyVisistedItem {

   ArtifactToken idToken;
   ArtifactTypeId typeId;

   public ArtifactToken getIdToken() {
      return idToken;
   }

   public void setIdToken(ArtifactToken idToken) {
      this.idToken = idToken;
   }

   public static RecentlyVisistedItem valueOf(ArtifactToken idToken, ArtifactTypeToken typeToken) {
      RecentlyVisistedItem item = new RecentlyVisistedItem();
      item.setIdToken(idToken);
      item.setTypeId(typeToken);
      return item;
   }

   @Override
   public String toString() {
      return idToken.getName();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((idToken == null) ? 0 : idToken.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      RecentlyVisistedItem other = (RecentlyVisistedItem) obj;
      if (idToken == null) {
         if (other.idToken != null) {
            return false;
         }
      } else if (!idToken.equals(other.idToken)) {
         return false;
      }
      return true;
   }

   public ArtifactTypeId getTypeId() {
      return typeId;
   }

   public void setTypeId(ArtifactTypeId typeId) {
      this.typeId = typeId;
   }
}
