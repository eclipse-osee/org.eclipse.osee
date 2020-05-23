/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.core.model.tabledataframework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Shawn F. Cook
 */
public class KeyColumnController implements Iterator<Collection<Object>> {
   private final Collection<KeyColumn> keyColumns = new ArrayList<>();

   public KeyColumnController(KeyColumn... keyColumns) {
      this.keyColumns.addAll(Arrays.asList(keyColumns));
      System.out.println();
   }

   @Override
   public boolean hasNext() {
      for (KeyColumn keyColumn : keyColumns) {
         if (keyColumn.hasNext()) {
            return true;
         }
      }
      return false;
   }

   @Override
   public Collection<Object> next() {
      Collection<Object> keyList = new ArrayList<>();
      if (hasNext()) {
         for (KeyColumn keyColumn : keyColumns) {
            if (!keyColumn.hasNext()) {
               keyColumn.reset();
            }
            keyList.add(keyColumn.next());
         }
      }
      return keyList;
   }

   @Override
   public void remove() {
      //
   }
}
