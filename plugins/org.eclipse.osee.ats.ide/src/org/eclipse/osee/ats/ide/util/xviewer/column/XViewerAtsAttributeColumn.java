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
package org.eclipse.osee.ats.ide.util.xviewer.column;

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IAttributeColumn;

/**
 * @author Donald G. Dunne
 */
public class XViewerAtsAttributeColumn extends XViewerAtsColumn implements IAttributeColumn {

   private AttributeTypeToken attributeType;

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

   @Override
   public void copy(XViewerColumn fromXCol, XViewerColumn toXCol) {
      super.copy(fromXCol, toXCol);
      if (fromXCol instanceof XViewerAtsAttributeColumn && toXCol instanceof XViewerAtsAttributeColumn) {
         ((XViewerAtsAttributeColumn) toXCol).setAttributeType(
            ((XViewerAtsAttributeColumn) fromXCol).getAttributeType());
      }
   }

   protected XViewerAtsAttributeColumn() {
      super();
   }

   public XViewerAtsAttributeColumn(AttributeTypeToken attributeType, String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable,
         Strings.isValid(description) ? description : attributeType.getDescription());
      this.attributeType = attributeType;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public void setAttributeType(AttributeTypeToken attributeType) {
      this.attributeType = attributeType;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + (attributeType == null ? 0 : attributeType.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      XViewerAtsAttributeColumn other = (XViewerAtsAttributeColumn) obj;
      if (attributeType == null) {
         if (other.attributeType != null) {
            return false;
         }
      } else if (!attributeType.equals(other.attributeType)) {
         return false;
      }
      return true;
   }

}