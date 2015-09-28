/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
