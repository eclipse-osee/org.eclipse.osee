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

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.world.WorldXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class XViewerAtsAttributeColumn extends XViewerColumn {

   private final String attributeTypeName;

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    * 
    * @param col
    */
   @Override
   public XViewerAtsAttributeColumn copy() {
      return new XViewerAtsAttributeColumn(getId(), getName(), getAttributeTypeName(), getWidth(), getAlign(),
            isShow(), getSortDataType(), isMultiColumnEditable(), getDescription());
   }

   public XViewerAtsAttributeColumn(String id, ATSAttributes atsAttribute, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      this(id, atsAttribute, width, align, show, sortDataType, multiColumnEditable, atsAttribute.getDescription());
   }

   public XViewerAtsAttributeColumn(String id, ATSAttributes atsAttribute, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      this(id, atsAttribute.getDisplayName(), atsAttribute.getStoreName(), width, align, show, sortDataType,
            multiColumnEditable, description);
   }

   public XViewerAtsAttributeColumn(ATSAttributes atsAttribute, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      this(atsAttribute, width, align, show, sortDataType, multiColumnEditable, atsAttribute.getDescription());
   }

   public XViewerAtsAttributeColumn(ATSAttributes atsAttribute, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      this(
            WorldXViewerFactory.COLUMN_NAMESPACE + "." + (atsAttribute.getDisplayName().replaceAll(" ", "").toLowerCase()),
            atsAttribute.getDisplayName(), atsAttribute.getStoreName(), width, align, show, sortDataType,
            multiColumnEditable,
            description == null || description.equals("") ? atsAttribute.getDescription() : description);
   }

   public XViewerAtsAttributeColumn(String id, String name, String attributeTypeName, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
      this.attributeTypeName = attributeTypeName;
   }

   public String getAttributeTypeName() {
      return attributeTypeName;
   }

}
