/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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

}
