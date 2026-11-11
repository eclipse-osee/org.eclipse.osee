/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.testscript;

import java.util.Collection;
import java.util.LinkedList;

public class ColumnFilterRequest {

   private Collection<ColumnFilter> filters = new LinkedList<>();

   public ColumnFilterRequest() {
   }

   public Collection<ColumnFilter> getFilters() {
      return filters;
   }

   public void setFilters(Collection<ColumnFilter> filters) {
      this.filters = filters;
   }
}
