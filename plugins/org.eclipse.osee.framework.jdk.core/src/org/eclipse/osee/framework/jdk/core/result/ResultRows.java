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
