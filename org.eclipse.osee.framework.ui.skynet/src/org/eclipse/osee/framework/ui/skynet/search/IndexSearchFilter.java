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

import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.NotSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.TagSearch;
import org.eclipse.osee.framework.skynet.core.tagging.Tagger;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * @author Jeff C. Phillips
 */
public class IndexSearchFilter extends SearchFilter {

   private Text indexText;
   private Button caseSensitive;
   private Button partialMatch;
   private static final String FILTER_NAME = "Index Based";

   /**
    * @param optionsControl
    */
   public IndexSearchFilter(Control optionsControl, Text indexText, Button caseSensitive, Button partialMatch) {
      super(FILTER_NAME, optionsControl);

      this.indexText = indexText;
      this.partialMatch = partialMatch;
      this.caseSensitive = caseSensitive;
   }

   @Override
   public void addFilterTo(FilterTableViewer filterViewer) {

      for (String tag : Tagger.tokenizeAndSplit(indexText.getText())) {
         ISearchPrimitive primitive = new TagSearch(tag, caseSensitive.getSelection(), partialMatch.getSelection());

         if (not) primitive = new NotSearch(primitive);

         filterViewer.addItem(primitive, FILTER_NAME, tag, "");
      }
   }

   @Override
   public boolean isValid() {
      return indexText.getText().length() > 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.SearchFilter#loadFromStorageString(org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer, java.lang.String, java.lang.String, java.lang.String, boolean)
    */
   @Override
   public void loadFromStorageString(FilterTableViewer filterViewer, String type, String value, String storageString, boolean isNotEnabled) {
      ISearchPrimitive primitive = TagSearch.getPrimitive(storageString);
      if (isNotEnabled) primitive = new NotSearch(primitive);
      filterViewer.addItem(primitive, FILTER_NAME, type, value);
   }
}
