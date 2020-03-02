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
public class ResultRows {

   List<String> headers = new ArrayList<>();
   List<ResultRow> results = new ArrayList<>();
   XResultData rd = new XResultData();

   public List<ResultRow> getResults() {
      return results;
   }

   public void setResults(List<ResultRow> results) {
      this.results = results;
   }

   public void add(ResultRow row) {
      results.add(row);
   }

   public List<String> getHeaders() {
      return headers;
   }

   public void setHeaders(List<String> headers) {
      this.headers = headers;
   }

   public XResultData getRd() {
      return rd;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }

}
