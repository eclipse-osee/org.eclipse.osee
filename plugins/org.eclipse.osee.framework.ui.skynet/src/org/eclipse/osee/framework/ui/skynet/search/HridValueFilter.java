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

import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactHridSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.NotSearch;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Ryan D. Brooks
 */
public class HridValueFilter extends SearchFilter {
   private Text hridValue;

   public HridValueFilter(Control optionsControl, Text hridValue) {
      super("Human Readable ID", optionsControl);
      this.hridValue = hridValue;
   }

   @Override
   public void addFilterTo(FilterTableViewer filterViewer) {
      OperatorAndValue result = handleWildCard(hridValue.getText());
      ISearchPrimitive primitive = new ArtifactHridSearch(result.value, result.operator);
      if (not) primitive = new NotSearch(primitive);
      filterViewer.addItem(primitive, getFilterName(), "huid", result.value);
   }

   @Override
   public boolean isValid() {
      return !hridValue.getText().equals("");
   }

   @Override
   public void loadFromStorageString(FilterTableViewer filterViewer, String type, String value, String storageString, boolean isNotEnabled) {
      ISearchPrimitive primitive = ArtifactHridSearch.getPrimitive(storageString);
      if (isNotEnabled) primitive = new NotSearch(primitive);
      filterViewer.addItem(primitive, getFilterName(), type, value);
   }
}
