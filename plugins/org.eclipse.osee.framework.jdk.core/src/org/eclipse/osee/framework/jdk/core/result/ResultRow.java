/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.jdk.core.result;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class ResultRow {

   List<String> values = new ArrayList<String>();
   String id = "-1";
   String id2 = "-1";

   public ResultRow() {
      // for jax-rs
   }

   public ResultRow(Long id, Long id2, String... datas) {
      this.id = id.toString();
      this.id2 = id2.toString();
      for (String data : datas) {
         values.add(data);
      }
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public List<String> getValues() {
      return values;
   }

   public void setValues(List<String> values) {
      this.values = values;
   }

   public String getId2() {
      return id2;
   }

   public void setId2(String id2) {
      this.id2 = id2;
   }

   public void addValue(String value) {
      this.values.add(value);
   }

}
