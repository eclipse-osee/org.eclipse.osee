/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsAttributeValueColumn;
import org.eclipse.osee.ats.workflow.EstimatedHoursUtil;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class EstimatedHoursColumn extends XViewerAtsAttributeValueColumn {

   public static EstimatedHoursColumn instance = new EstimatedHoursColumn();

   public static EstimatedHoursColumn getInstance() {
      return instance;
   }

   private EstimatedHoursColumn() {
      super(AtsAttributeTypes.EstimatedHours, WorldXViewerFactory.COLUMN_NAMESPACE + ".estimatedHours",
         AtsAttributeTypes.EstimatedHours.getUnqualifiedName(), 40, XViewerAlign.Center, false, SortDataType.Float,
         true, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public EstimatedHoursColumn copy() {
      EstimatedHoursColumn newXCol = new EstimatedHoursColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         return AtsUtil.doubleToI18nString(EstimatedHoursUtil.getEstimatedHours(element));
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return super.getColumnText(element, column, columnIndex);
   }

}
