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

import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class XViewerAtsAttributeColumn extends XViewerAtsColumn {

   private IAttributeType attributeType;

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    */
   @Override
   public XViewerAtsAttributeColumn copy() {
      XViewerAtsAttributeColumn newXCol = new XViewerAtsAttributeColumn();
      copy(this, newXCol);
      return newXCol;
   }

   private void copy(XViewerAtsAttributeColumn fromXCol, XViewerAtsAttributeColumn toXCol) {
      super.copy(fromXCol, toXCol);
      toXCol.setAttributeType(fromXCol.attributeType);
   }

   protected XViewerAtsAttributeColumn() {
      super();
   }

   public XViewerAtsAttributeColumn(IAttributeType attributeType, String id, String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable,
         Strings.isValid(description) ? description : attributeType.getDescription());
      this.attributeType = attributeType;
   }

   public XViewerAtsAttributeColumn(IAttributeType attributeType, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      this(attributeType, generateId(attributeType.getUnqualifiedName()), attributeType.getUnqualifiedName(), width,
         align, show, sortDataType, multiColumnEditable, description);
   }

   private static final String generateId(String unqualifiedName) {
      return WorldXViewerFactory.COLUMN_NAMESPACE + "." + unqualifiedName.replaceAll(" ", "").toLowerCase();
   }

   //   public XViewerAtsAttributeColumn(ATSAttributes atsAttribute, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
   //      this(null, generateId(atsAttribute.getDisplayName()), atsAttribute.getDisplayName(), width, align, show,
   //         sortDataType, multiColumnEditable, atsAttribute.getDescription());
   //   }

   public IAttributeType getAttributeType() {
      return attributeType;
   }

   public void setAttributeType(IAttributeType attributeType) {
      this.attributeType = attributeType;
   }

}