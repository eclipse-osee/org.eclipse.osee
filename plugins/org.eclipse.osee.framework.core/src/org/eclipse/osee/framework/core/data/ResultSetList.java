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
package org.eclipse.osee.framework.core.data;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.exception.ItemDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleItemsExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class ResultSetList<T> implements ResultSet<T> {

   private final List<T> data;

   public ResultSetList() {
      super();
      this.data = Collections.emptyList();
   }

   public ResultSetList(List<T> data) {
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
         throw new ItemDoesNotExist("No item found");
      } else if (result.size() > 1) {
         throw new MultipleItemsExist("Multiple items found - total [%s]", result.size());
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
