/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.util.matrix;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Donald G. Dunne
 */
public class MatrixItem {

   private String name;
   private Set<String> values = new HashSet<>();

   public MatrixItem() {
      this("Unset", null);
   }

   public MatrixItem(String name) {
      this(name, null);
   }

   public void addValues(Collection<String> values) {
      this.values.addAll(values);
   }

   public void addValue(String value) {
      this.values.add(value);
   }

   public MatrixItem(String name, String[] values) {
      this.name = name;
      if (values != null) {
         for (String value : values) {
            this.values.add(value);
         }
      }
   }

   public Set<String> getValues() {
      return values;
   }

   public void setValues(Set<String> values) {
      this.values = values;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

}
