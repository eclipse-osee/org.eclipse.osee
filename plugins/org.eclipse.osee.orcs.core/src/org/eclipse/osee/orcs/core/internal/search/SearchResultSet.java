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
package org.eclipse.osee.orcs.core.internal.search;

import java.util.List;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.search.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public class SearchResultSet<T> implements ResultSet<T> {

   private final List<T> data;

   public SearchResultSet(List<T> data) {
      super();
      this.data = data;
   }

   @Override
   public T getOneOrNull() {
      List<T> result = getList();
      return result.isEmpty() ? null : result.iterator().next();
   }

   @Override
   public T getExactlyOne() throws OseeCoreException {
      List<T> result = getList();
      if (result.isEmpty()) {
         throw new ArtifactDoesNotExist("No artifacts found");
      } else if (result.size() > 1) {
         throw new MultipleArtifactsExist("Multiple artifact found - total [%s]", result.size());
      }
      return result.iterator().next();
   }

   @Override
   public List<T> getList() {
      return data;
   }

   @Override
   public Iterable<T> getIterable(int fetchSize) {
      return getList();
   }
}
