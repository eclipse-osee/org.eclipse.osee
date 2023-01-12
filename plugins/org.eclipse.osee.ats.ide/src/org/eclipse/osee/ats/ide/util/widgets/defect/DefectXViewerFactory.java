/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.defect;

import static org.eclipse.osee.ats.core.review.PeerReviewDefectXViewerColumns.Closed_Col;
import static org.eclipse.osee.ats.core.review.PeerReviewDefectXViewerColumns.Created_Date_Col;
import static org.eclipse.osee.ats.core.review.PeerReviewDefectXViewerColumns.DefectId_Col;
import static org.eclipse.osee.ats.core.review.PeerReviewDefectXViewerColumns.Description_Col;
import static org.eclipse.osee.ats.core.review.PeerReviewDefectXViewerColumns.Disposition_Col;
import static org.eclipse.osee.ats.core.review.PeerReviewDefectXViewerColumns.Injection_Activity_Col;
import static org.eclipse.osee.ats.core.review.PeerReviewDefectXViewerColumns.Location_Col;
import static org.eclipse.osee.ats.core.review.PeerReviewDefectXViewerColumns.Notes_Col;
import static org.eclipse.osee.ats.core.review.PeerReviewDefectXViewerColumns.Resolution_Col;
import static org.eclipse.osee.ats.core.review.PeerReviewDefectXViewerColumns.Severity_Col;
import static org.eclipse.osee.ats.core.review.PeerReviewDefectXViewerColumns.User_Col;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class DefectXViewerFactory extends SkynetXViewerFactory {

   private final static String NAMESPACE = "DefectXViewer";

   public DefectXViewerFactory(IOseeTreeReportProvider reportProvider, List<XViewerColumn> defectCols) {
      super(NAMESPACE, reportProvider);
      if (defectCols.isEmpty()) {
         registerColumns(DefectId_Col, Severity_Col, Disposition_Col, Closed_Col, User_Col, Created_Date_Col,
            Injection_Activity_Col, Description_Col, Location_Col, Resolution_Col, Notes_Col);
      } else {
         registerColumns(defectCols);
      }
   }

}
