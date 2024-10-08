/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.core.ds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Roberto E. Escobar
 */
public class SelectData implements Cloneable {

   private final List<SelectSet> selects;

   public SelectData() {
      this.selects = new ArrayList<>();
      this.selects.add(new SelectSet());
   }

   public SelectData(List<SelectSet> selects) {
      this.selects = selects;
   }

   public int size() {
      return selects.size();
   }

   public List<SelectSet> getAll() {
      return Collections.unmodifiableList(selects);
   }

   public SelectSet newSelectSet() {
      SelectSet data = new SelectSet();
      selects.add(data);
      return data;
   }

   public SelectSet getLast() {
      return !selects.isEmpty() ? selects.get(selects.size() - 1) : null;
   }

   public SelectSet getFirst() {
      return !selects.isEmpty() ? selects.get(0) : null;
   }

   public void reset() {
      SelectSet data = null;
      if (!selects.isEmpty()) {
         data = selects.get(0);
         data.reset();
      }
      selects.clear();
      if (data != null) {
         selects.add(data);
      }
   }
}