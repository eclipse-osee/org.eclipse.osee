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
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerDeadlineColumn extends XViewerAtsAttributeColumn {

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn need to extend this constructor to copy extra stored fields
    * 
    * @param col
    */
   @Override
   public XViewerDeadlineColumn copy() {
      return new XViewerDeadlineColumn(getId(), getWidth(), getAlign(), isShow(), getSortDataType(),
            isMultiColumnEditable(), getDescription());
   }

   public XViewerDeadlineColumn(String id, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, ATSAttributes.DEADLINE_ATTRIBUTE, width, align, show, sortDataType, multiColumnEditable, description);
   }

   public XViewerDeadlineColumn() {
      super("ats.column.deadline", ATSAttributes.DEADLINE_ATTRIBUTE, 75, SWT.LEFT, true, SortDataType.String, true,
            null);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerValueColumn#getColumnText(java.lang.Object, org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn)
    */
   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws XViewerException {
      try {
         if (element instanceof IWorldViewArtifact) {
            return ((IWorldViewArtifact) element).getWorldViewDeadlineDateStr();
         }
         return "";
      } catch (OseeCoreException ex) {
         throw new XViewerException(ex);
      }
   }

}
