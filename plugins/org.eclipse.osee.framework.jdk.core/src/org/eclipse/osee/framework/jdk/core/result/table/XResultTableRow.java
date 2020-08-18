/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.result.table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class XResultTableRow {

   public List<String> values = new ArrayList<>();

   public XResultTableRow() {
      // for jax-rs
   }

   public XResultTableRow(String... strings) {
      for (String str : strings) {
         values.add(str);
      }
   }

   @JsonIgnore
   public String getValue(int col) {
      return values.get(col);
   }

   public List<String> getValues() {
      return values;
   }

   public void setValues(List<String> values) {
      this.values = values;
   }

}
