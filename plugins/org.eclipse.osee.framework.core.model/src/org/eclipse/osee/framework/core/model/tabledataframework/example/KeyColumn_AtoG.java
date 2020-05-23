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

package org.eclipse.osee.framework.core.model.tabledataframework.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.model.tabledataframework.KeyColumn;

/**
 * @author Shawn F. Cook
 */
public class KeyColumn_AtoG implements KeyColumn {
   List<String> listAtoG = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G"));
   private Integer currentIndex;
   private Integer nextIndex;

   public KeyColumn_AtoG() {
      reset();
   }

   @Override
   public boolean hasNext() {
      return nextIndex < listAtoG.size();
   }

   @Override
   public Object next() {
      currentIndex = nextIndex;
      nextIndex++;
      return getCurrent();
   }

   @Override
   public void remove() {
      //Do nothing
   }

   @Override
   public Object getCurrent() {
      String currentValue = listAtoG.get(currentIndex);
      return currentValue;
   }

   @Override
   public List<Object> getAll() {
      List<Object> retList = new ArrayList<>();
      retList.addAll(listAtoG);
      return retList;
   }

   @Override
   public void reset() {
      currentIndex = null;
      nextIndex = 0;
   }

}
