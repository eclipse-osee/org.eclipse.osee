/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.ide.column;

import org.eclipse.osee.ats.api.config.AtsAttrValCol;
import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsAttributeValueColumn;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeValueColumnFactory {

   public static AtsAttrValCol get(String namespace, XViewerAtsAttributeValueColumn inCol) {
      AtsAttrValCol col = new AtsAttrValCol();
      col.setName(inCol.getName());
      col.setNamespace(namespace);
      col.setAttrTypeId(inCol.getAttributeType().getId());
      col.setAttrTypeName(inCol.getAttributeType().getName());
      ColumnAlign colAlign = AtsEditors.getColumnAlign(inCol.getAlign());
      col.setAlign(colAlign);
      col.setColumnMultiEdit(inCol.isMultiColumnEditable());
      col.setDescription(inCol.getDescription());
      col.setSortDataType(inCol.getSortDataType().name());
      col.setBooleanOnTrueShow(inCol.getBooleanOnTrueShow());
      col.setBooleanOnFalseShow(inCol.getBooleanOnFalseShow());
      col.setBooleanNotSetShow(inCol.getBooleanNotSetShow());
      col.setVisible(inCol.isShow());
      col.setWidth(inCol.getWidth());
      return col;
   }

}
