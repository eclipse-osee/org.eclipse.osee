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
package org.eclipse.osee.ats.util.xviewer.column;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerAttributeColumn;

/**
 * @author Donald G. Dunne
 */
public class XViewerAtsAttributeColumn extends XViewerAttributeColumn {

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    * 
    * @param col
    */
   public XViewerAtsAttributeColumn copy() {
      return new XViewerAtsAttributeColumn(getId(), getName(), getAttributeTypeName(), getWidth(), getAlign(),
            isShow(), getSortDataType(), isMultiColumnEditable(), getDescription());
   }

   public XViewerAtsAttributeColumn(String id, ATSAttributes atsAttribute, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      this(id, atsAttribute.getDisplayName(), atsAttribute.getStoreName(), width, align, show, sortDataType,
            multiColumnEditable, description);
   }

   public XViewerAtsAttributeColumn(ATSAttributes atsAttribute, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      this(
            WorldXViewerFactory.COLUMN_NAMESPACE + "." + (atsAttribute.getDisplayName().replaceAll(" ", "").toLowerCase()),
            atsAttribute.getDisplayName(), atsAttribute.getStoreName(), width, align, show, sortDataType,
            multiColumnEditable, description);
   }

   public XViewerAtsAttributeColumn(String id, String name, String attributeTypeName, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, attributeTypeName, width, align, show, sortDataType, multiColumnEditable, description);
   }

}
