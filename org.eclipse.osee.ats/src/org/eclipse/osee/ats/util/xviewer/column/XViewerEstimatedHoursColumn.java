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
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerEstimatedHoursColumn extends XViewerAtsAttributeColumn {

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    * 
    * @param col
    */
   @Override
   public XViewerEstimatedHoursColumn copy() {
      return new XViewerEstimatedHoursColumn(getId(), getWidth(), getAlign(), isShow(), getSortDataType(),
            isMultiColumnEditable(), getDescription());
   }

   public XViewerEstimatedHoursColumn(String id, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE, width, align, show, sortDataType, multiColumnEditable,
            description);
   }

   public XViewerEstimatedHoursColumn() {
      super(
            WorldXViewerFactory.COLUMN_NAMESPACE + ".estimatedHours",
            ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE,
            40,
            SWT.CENTER,
            false,
            SortDataType.Float,
            true,
            "Hours estimated to implement the changes associated with this Action.\nIncludes estimated hours for workflows, tasks and reviews.");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn#getToolTip()
    */
   @Override
   public String getToolTip() {
      return getDescription();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn#getColumnText(java.lang.Object, org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn)
    */
   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws XViewerException {
      try {
         if (element instanceof IWorldViewArtifact) {
            return AtsLib.doubleToStrString(((IWorldViewArtifact) element).getWorldViewEstimatedHours());
         }
         return "";
      } catch (OseeCoreException ex) {
         throw new XViewerException(ex);
      }
   }

}
