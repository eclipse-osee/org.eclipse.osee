/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.reportdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roberto E. Escobar
 */
public class ReportData implements Serializable {

   private static final long serialVersionUID = 6645261625619889708L;

   private List<String> headers;
   private final List<ReportDataItem> items;

   /**
    * Creates the ReportData class with the given headers. The number of headers should match the values passed into
    * <code>addItem</code>.
    */
   public ReportData(List<String> headers) {
      this.headers = headers;
      items = new ArrayList<>();
   }

   /**
    * Adds an item to the ReportData
    * 
    * @param guid The GUID corresponding to the item added
    * @param values The values (such as username, script name) associated with the item. These should match the headers.
    */
   public void addItem(String guid, ArrayList<String> values) {
      ReportDataItem item = new ReportDataItem(guid, values);
      items.add(item);
   }

   public void clearItems() {
      items.clear();
   }

   public void setHeaders(List<String> headers) {
      this.headers = headers;
   }

   public List<String> getHeaders() {
      return headers;
   }

   public List<ReportDataItem> getItems() {
      return items;
   }
}
