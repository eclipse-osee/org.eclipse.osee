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

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.core.review.ReviewDefectManager;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public class ReviewNumMinorDefectsColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static ReviewNumMinorDefectsColumn instance = new ReviewNumMinorDefectsColumn();

   public static ReviewNumMinorDefectsColumn getInstance() {
      return instance;
   }

   private ReviewNumMinorDefectsColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".reviewMinorDefects", "Review Minor Defects", 40,
         XViewerAlign.Center, false, SortDataType.Integer, false, "Number of Minor Defects found in Review");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ReviewNumMinorDefectsColumn copy() {
      ReviewNumMinorDefectsColumn newXCol = new ReviewNumMinorDefectsColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof PeerToPeerReviewArtifact) {
            return String.valueOf(
               new ReviewDefectManager((PeerToPeerReviewArtifact) element, AtsApiService.get()).getNumMinor());
         }
      } catch (OseeCoreException ex) {
         LogUtil.getCellExceptionString(ex);
      }
      return "";
   }
}
