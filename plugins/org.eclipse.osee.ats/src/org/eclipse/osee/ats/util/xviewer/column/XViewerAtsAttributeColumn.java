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
import org.eclipse.osee.framework.core.data.IAttributeType;

/**
 * @author Donald G. Dunne
 */
public class XViewerAtsAttributeColumn extends XViewerColumn {

   private final IAttributeType attributeType;

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    */
   @Override
   public XViewerAtsAttributeColumn copy() {
      return new XViewerAtsAttributeColumn(getId(), getName(), getWidth(), getAlign(), isShow(), getSortDataType(),
         isMultiColumnEditable(), getDescription(), attributeType);
   }

   private XViewerAtsAttributeColumn(String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description, IAttributeType attributeType) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
      this.attributeType = attributeType;
   }

   public XViewerAtsAttributeColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      this(generateId(attributeType.getUnqualifiedName()), attributeType, width, align, show, sortDataType,
         multiColumnEditable);
   }

   private static final String generateId(String unqualifiedName) {
      return WorldXViewerFactory.COLUMN_NAMESPACE + "." + unqualifiedName.replaceAll(" ", "").toLowerCase();
   }

   public XViewerAtsAttributeColumn(ATSAttributes atsAttribute, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      this(generateId(atsAttribute.getDisplayName()), atsAttribute.getDisplayName(), width, align, show, sortDataType,
         multiColumnEditable, atsAttribute.getDescription(), null);
   }

   public XViewerAtsAttributeColumn(String id, IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      this(id, attributeType.getUnqualifiedName(), width, align, show, sortDataType, multiColumnEditable,
         attributeType.getDescription(), attributeType);
   }

   public IAttributeType getAttributeType() {
      return attributeType;
   }
}