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

import java.util.Date;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;

/**
 * @author Donald G. Dunne
 */
public class LastModifiedDateColumn extends XViewerValueColumn {

   public static final String FRAMEWORK_LAST_MOD_DATE = "framework.lastModDate";

   public LastModifiedDateColumn(boolean show) {
      super(FRAMEWORK_LAST_MOD_DATE, "Last Modified Date", 50, XViewerAlign.Left, show, SortDataType.Date, false,
         "Retrieves date of last attribute update of this artifact.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public LastModifiedDateColumn copy() {
      LastModifiedDateColumn newXCol = new LastModifiedDateColumn(isShow());
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return DateUtil.getMMDDYYHHMM(((Artifact) element).getLastModified());
         } else if (element instanceof Change) {
            Date date = ((Change) element).getChangeArtifact().getLastModified();
            return DateUtil.getMMDDYYHHMM(date);
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

   public static Date getDate(Object object)  {
      Date result = null;
      if (object instanceof Artifact) {
         result = ((Artifact) object).getLastModified();
      } else if (object instanceof Change) {
         result = ((Change) object).getChangeArtifact().getLastModified();
      }
      return result;
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      return getDate(element);
   }
}
