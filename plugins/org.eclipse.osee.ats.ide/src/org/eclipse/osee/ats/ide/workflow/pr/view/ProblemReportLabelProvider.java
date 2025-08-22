/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.workflow.pr.view;

import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.framework.core.data.ArtifactResultRow;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewer;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewerLabelProvider;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

public class ProblemReportLabelProvider extends ResultsXViewerLabelProvider {

   public ProblemReportLabelProvider(ResultsXViewer resultsXViewer) {
      super(resultsXViewer);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) throws Exception {
      if (col.equals(ProblemrReportViewColumns.ArtifactTypeCol)) {
         IResultsXViewerRow row = (IResultsXViewerRow) element;
         if (row.getData() instanceof Artifact) {
            return ArtifactImageManager.getImage((Artifact) row.getData());
         } else if (row.getData() instanceof ArtifactResultRow) {
            ArtifactTypeToken artType = ((ArtifactResultRow) row.getData()).getArtType();
            if (artType != null) {
               return ArtifactImageManager.getImage(artType);
            }
         }
      }
      if (col.equals(ProblemrReportViewColumns.StateCol)) {
         IResultsXViewerRow row = (IResultsXViewerRow) element;
         if (row.getData() instanceof Artifact && ((Artifact) row.getData()).isOfType(
            AtsArtifactTypes.BuildImpactData)) {
            return ImageManager.getImage(AtsImage.STATE);
         }
      }
      if (col.equals(ProblemrReportViewColumns.ProgramCol)) {
         return ImageManager.getImage(AtsImage.PROGRAM);
      }

      return super.getColumnImage(element, col, columnIndex);
   }

}
