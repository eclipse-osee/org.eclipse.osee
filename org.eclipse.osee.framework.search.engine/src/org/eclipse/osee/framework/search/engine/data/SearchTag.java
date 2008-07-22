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

   public int size() {
      return this.codedTags.size();
   }

   public void clear() {
      this.codedTags.clear();
   }

   public long getGammaId() {
      return attributeVersion.getGammaId();
   }

   public Set<Long> getTags() {
      return this.codedTags;
   }

   public String toString() {
      return String.format("%s with %d tags", attributeVersion.toString(), size());
   }
}
