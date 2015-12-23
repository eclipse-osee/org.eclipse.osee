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