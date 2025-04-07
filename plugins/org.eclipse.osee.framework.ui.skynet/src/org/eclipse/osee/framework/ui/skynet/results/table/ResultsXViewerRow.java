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

package org.eclipse.osee.framework.ui.skynet.results.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class ResultsXViewerRow implements IResultsXViewerRow {

   protected final List<String> values;
   private Object data;
   protected final List<IResultsXViewerRow> children = new ArrayList<>();

   public ResultsXViewerRow() {
      values = new ArrayList<String>();
   }

   public ResultsXViewerRow(List<String> values, Object data) {
      this.data = data;
      this.values = values;
   }

   public ResultsXViewerRow(List<String> values) {
      this(values, null);
   }

   public ResultsXViewerRow(String[] values, Object data) {
      this(Arrays.asList(values), data);
   }

   public ResultsXViewerRow(String[] values) {
      this(Arrays.asList(values), null);
   }

   @Override
   public String getValue(int col) {
      return values.get(col);
   }

   @Override
   public String[] values() {
      return values.toArray(new String[values.size()]);
   }

   public void addValue(String value) {
      values.add(value);
   }

   @Override
   public Object getData() {
      return data;
   }

   public void setData(Object data) {
      this.data = data;
   }

   @Override
   public Collection<IResultsXViewerRow> getChildren() {
      return children;
   }

   @Override
   public boolean hasChildren() {
      return !children.isEmpty();
   }

}
