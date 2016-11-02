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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;

/**
 * @author Donald G. Dunne
 */
public class AttributeColumn extends XViewerValueColumn implements IAttributeColumn {

   private IAttributeType attributeType;

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    */
   @Override
   public AttributeColumn copy() {
      AttributeColumn newXCol = new AttributeColumn((XViewer) this.getXViewer(), this.toXml());
      this.copy(this, newXCol);
      return newXCol;
   }

   @Override
   protected void copy(XViewerColumn fromXCol, XViewerColumn toXCol) {
      super.copy(fromXCol, toXCol);
      if (fromXCol instanceof IAttributeColumn && toXCol instanceof IAttributeColumn) {
         ((IAttributeColumn) toXCol).setAttributeType(((IAttributeColumn) fromXCol).getAttributeType());
      }
   }

   protected AttributeColumn(XViewer xViewer, String xml) {
      super(xViewer, xml);
   }

   public AttributeColumn(String id, String name, IAttributeType attributeType, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
      this.attributeType = attributeType;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws XViewerException {
      try {
         if (element instanceof Artifact) {
            return ((Artifact) element).getAttributesToString(getAttributeType());
         } else if (element instanceof Change) {
            return ((Change) element).getChangeArtifact().getAttributesToString(getAttributeType());
         } else if (element instanceof Conflict) {
            return ((Conflict) element).getArtifact().getAttributesToString(getAttributeType());
         } else {
            return "";
         }
      } catch (OseeCoreException ex) {
         throw new XViewerException(ex);
      }
   }

   @Override
   public IAttributeType getAttributeType() {
      return attributeType;
   }

   @Override
   public void setAttributeType(IAttributeType attributeType) {
      this.attributeType = attributeType;
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      if (element instanceof Artifact) {
         Artifact art = (Artifact) element;
         return art.getSoleAttributeValue(attributeType, null);
      }
      return null;
   }
}
