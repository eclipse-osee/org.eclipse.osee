/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.column.ActionableItemsColumn;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemsColumnUI extends XViewerAtsAttributeValueColumn {

   public static ActionableItemsColumnUI instance = new ActionableItemsColumnUI();

   public static ActionableItemsColumnUI getInstance() {
      return instance;
   }

   private ActionableItemsColumnUI() {
      super(AtsAttributeTypes.ActionableItem, WorldXViewerFactory.COLUMN_NAMESPACE + ".actionableItems",
         AtsAttributeTypes.ActionableItem.getUnqualifiedName(), 80, SWT.LEFT, true, SortDataType.String, false, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ActionableItemsColumnUI copy() {
      ActionableItemsColumnUI newXCol = new ActionableItemsColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         return ActionableItemsColumn.getColumnText(element);
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
   }

   public static String getActionableItemsStr(Object element) throws OseeCoreException {
      return ActionableItemsColumn.getActionableItemsStr(element);
   }

   public static Collection<IAtsActionableItem> getActionableItems(Object element) throws OseeCoreException {
      return ActionableItemsColumn.getActionableItems(element);
   }
}
