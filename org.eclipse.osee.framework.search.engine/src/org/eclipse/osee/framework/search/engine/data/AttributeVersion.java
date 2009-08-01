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
   private long gammaId;

   public AttributeVersion(long gammaId) {
      super();
      this.gammaId = gammaId;
   }

   public long getGammaId() {
      return gammaId;
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) return true;
      if (!(object instanceof IAttributeLocator)) return false;
      IAttributeLocator other = (IAttributeLocator) object;
      return other.getGammaId() == this.getGammaId();
   }

   @Override
   public int hashCode() {
      return (int) (37 * getGammaId());
   }

   public String toString() {
      return String.format("gammaId: [%d]", getGammaId());
   }
}
