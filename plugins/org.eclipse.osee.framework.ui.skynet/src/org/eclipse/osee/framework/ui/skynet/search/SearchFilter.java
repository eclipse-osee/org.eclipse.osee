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

package org.eclipse.osee.framework.ui.skynet.search;

import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.swt.widgets.Control;

/**
 * @author Ryan D. Brooks
 */
public abstract class SearchFilter {
   private final String filterName;
   protected Control optionsControl;

   public SearchFilter(String filterName, Control optionsControl) {
      this.filterName = filterName;
      this.optionsControl = optionsControl;
   }

   public abstract void addFilterTo(FilterTableViewer filterViewer);

   public boolean isValid() {
      return false;
   }

   protected String getFilterName() {
      return filterName;
   }

   public abstract String getSearchDescription();

   public abstract void loadFromStorageString(FilterTableViewer filterViewer, String type, String value, String storageString, boolean isNotEnabled);

}