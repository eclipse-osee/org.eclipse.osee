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
package org.eclipse.osee.framework.search.engine.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;

/**
 * @author Roberto E. Escobar
 */
public class SearchTag {

   private AttributeVersion attributeVersion;
   private Set<Long> codedTags;

   public SearchTag(AttributeVersion attributeVersion) {
      this.attributeVersion = attributeVersion;
      this.codedTags = new HashSet<Long>();
   }

   public void addTag(Long codedTag) {
      this.codedTags.add(codedTag);
   }

   public int size() {
      return this.codedTags.size();
   }

   public void clear() {
      this.codedTags.clear();
   }

   public String toString() {
      return String.format("%s with %d tags", attributeVersion.toString(), size());
   }

   public List<Object[]> toList() {
      List<Object[]> datas = new ArrayList<Object[]>();
      if (this.codedTags.isEmpty() != true) {
         for (Long codedTag : this.codedTags) {
            datas.add(new Object[] {SQL3DataType.INTEGER, attributeVersion.getAttrId(), SQL3DataType.BIGINT,
                  attributeVersion.getGamma_id(), SQL3DataType.BIGINT, codedTag});
         }
      }
      return datas;
   }
}
