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

import java.util.HashSet;
import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public class SearchTag implements IAttributeLocator {

   private AttributeVersion attributeVersion;
   private Set<Long> codedTags;

   public SearchTag(long gammaId) {
      this.attributeVersion = new AttributeVersion(gammaId);
      this.codedTags = new HashSet<Long>();
   }

   public void addTag(long codedTag) {
      this.codedTags.add(codedTag);
   }

   public int cacheSize() {
      return this.codedTags.size();
   }

   public void clearCache() {
      this.codedTags.clear();
   }

   public long getGammaId() {
      return attributeVersion.getGammaId();
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object object) {
      if (this == object) return true;
      if (!(object instanceof SearchTag)) return false;
      SearchTag other = (SearchTag) object;
      return other.getGammaId() == this.getGammaId();
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return attributeVersion.hashCode();
   }

   public Set<Long> getTags() {
      return this.codedTags;
   }

   public String toString() {
      return String.format("%s with %d tags cached", attributeVersion.toString(), cacheSize());
   }
}
