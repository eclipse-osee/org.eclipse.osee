/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.workflow.task.trace;

import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewer;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewerLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class TraceabilityLabelProvider extends ResultsXViewerLabelProvider {

   public TraceabilityLabelProvider(ResultsXViewer resultsXViewer) {
      super(resultsXViewer);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) throws Exception {
      if (col.getName().equals("Requirement")) {
         if (element instanceof ResultsXViewerRow) {
            Object data = ((ResultsXViewerRow) element).getData();
            if (data instanceof Artifact) {
               return ArtifactImageManager.getImage(((Artifact) data).getArtifactType());
            }
         }
      }
      return null;
   }
}
