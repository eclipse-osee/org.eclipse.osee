/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.column;

import java.util.Date;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class EstimatedReleaseDateColumn extends AbstractWorkflowVersionDateColumnUI {

   public static EstimatedReleaseDateColumn instance = new EstimatedReleaseDateColumn();

   public static EstimatedReleaseDateColumn getInstance() {
      return instance;
   }

   private EstimatedReleaseDateColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".estimatedReleaseDate", AtsAttributeTypes.EstimatedReleaseDate);
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public EstimatedReleaseDateColumn copy() {
      EstimatedReleaseDateColumn newXCol = new EstimatedReleaseDateColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   public static Date getDateFromWorkflow(Object object) {
      return AbstractWorkflowVersionDateColumnUI.getDateFromWorkflow(AtsAttributeTypes.EstimatedReleaseDate, object);
   }

   public static Date getDateFromTargetedVersion(Object object) {
      return AbstractWorkflowVersionDateColumnUI.getDateFromTargetedVersion(AtsAttributeTypes.EstimatedReleaseDate,
         object);
   }

   @Override
   public Object getBackingData(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      Date date = getDateFromWorkflow(element);
      if (date == null) {
         date = getDateFromTargetedVersion(element);
      }

      return date;
   }
}
