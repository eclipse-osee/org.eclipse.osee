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
import java.util.List;
import org.eclipse.osee.framework.core.model.tabledataframework.KeyColumn;

/**
 * @author Shawn F. Cook
 */
public class KeyColumn_1to10 implements KeyColumn {
   private Integer curValue;
   private Integer nextValue;

   public KeyColumn_1to10() {
      reset();
   }

   @Override
   public boolean hasNext() {
      return nextValue <= 10;
   }

   @Override
   public Object next() {
      curValue = nextValue;
      nextValue++;//Note: This will keep counting.  It is up to the calling function to use hasNext() and either not call next() or call reset() first.
      return curValue;
   }

   @Override
   public void remove() {
      //Do nothing
   }

   @Override
   public Object getCurrent() {
      return curValue;
   }

   @Override
   public List<Object> getAll() {
      List<Object> allValues = new ArrayList<>();
      allValues.add(1);
      allValues.add(2);
      allValues.add(3);
      allValues.add(4);
      allValues.add(5);
      allValues.add(6);
      allValues.add(7);
      allValues.add(8);
      allValues.add(9);
      allValues.add(10);
      return allValues;
   }

   @Override
   public void reset() {
      curValue = null;
      nextValue = 1;
   }

}
