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

import java.util.logging.Level;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.InRelationSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.NotSearch;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.swt.widgets.Control;

/**
 * @author Ryan D. Brooks
 */
public class InRelationFilter extends SearchFilter {
   private ComboViewer relationTypeList;
   private ComboViewer relationSideList;

   public InRelationFilter(Control optionsControl, ComboViewer relationTypeList, ComboViewer relationSideList) {
      super("Artifact in Relation", optionsControl);
      this.relationTypeList = relationTypeList;
      this.relationSideList = relationSideList;
   }

   @Override
   public void addFilterTo(FilterTableViewer filterViewer) {
      String type = relationTypeList.getCombo().getText();
      String sideName = relationSideList.getCombo().getText();

      RelationType linkDescriptor = (RelationType) relationTypeList.getData(relationTypeList.getCombo().getText());
      try {
         ISearchPrimitive primitive = new InRelationSearch(type, linkDescriptor.isSideAName(sideName));
         if (not) primitive = new NotSearch(primitive);

         filterViewer.addItem(primitive, getFilterName(), type, sideName);
      } catch (OseeArgumentException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public boolean isValid() {
      return true;
   }

   @Override
   public void loadFromStorageString(FilterTableViewer filterViewer, String type, String value, String storageString, boolean isNotEnabled) {
      ISearchPrimitive primitive = InRelationSearch.getPrimitive(storageString);
      if (isNotEnabled) primitive = new NotSearch(primitive);
      filterViewer.addItem(primitive, getFilterName(), type, value);
   }
}
