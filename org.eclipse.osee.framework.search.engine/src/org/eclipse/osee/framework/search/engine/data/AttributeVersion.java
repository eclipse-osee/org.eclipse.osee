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
package org.eclipse.osee.framework.search.engine.data;

/**
 * @author Roberto E. Escobar
 */
public class AttributeVersion implements IAttributeLocator {
   private int attrId;
   private long gamma_id;

   public AttributeVersion(int attrId, long gamma_id) {
      super();
      this.attrId = attrId;
      this.gamma_id = gamma_id;
   }

   public int getAttrId() {
      return attrId;
   }

   public long getGamma_id() {
      return gamma_id;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object object) {
      if (this == object) return true;
      if (!(object instanceof AttributeVersion)) return false;
      AttributeVersion other = (AttributeVersion) object;
      return other.attrId == this.attrId && other.gamma_id == this.gamma_id;
   }

   public String toString() {
      return String.format("attrId: [%s] gammaId: [%d]", getAttrId(), getGamma_id());
   }
}
