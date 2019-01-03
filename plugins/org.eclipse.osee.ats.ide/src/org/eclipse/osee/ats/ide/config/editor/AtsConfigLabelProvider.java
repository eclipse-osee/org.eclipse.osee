/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.config.editor;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewer;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewerLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigLabelProvider extends ResultsXViewerLabelProvider implements ILabelProvider {

   public AtsConfigLabelProvider(ResultsXViewer resultsXViewer) {
      super(resultsXViewer);
   }

   @Override
   public org.eclipse.swt.graphics.Image getColumnImage(Object element, XViewerColumn col, int columnIndex) throws Exception {
      if (col.getName().equals("Type")) {
         if (element instanceof ResultsXViewerRow) {
            Object data = ((ResultsXViewerRow) element).getData();
            if (data instanceof IAtsActionableItem) {
               return ArtifactImageManager.getImage(AtsArtifactTypes.ActionableItem);
            } else if (data instanceof IAtsTeamDefinition) {
               return ArtifactImageManager.getImage(AtsArtifactTypes.TeamDefinition);
            } else if (data instanceof IAtsVersion) {
               return ArtifactImageManager.getImage(AtsArtifactTypes.Version);
            }
         } else if (element instanceof IAtsActionableItem) {
            return ArtifactImageManager.getImage(AtsArtifactTypes.ActionableItem);
         } else if (element instanceof IAtsTeamDefinition) {
            return ArtifactImageManager.getImage(AtsArtifactTypes.TeamDefinition);
         } else if (element instanceof IAtsVersion) {
            return ArtifactImageManager.getImage(AtsArtifactTypes.Version);
         }
      }
      return null;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) {
      if (element instanceof String) {
         if (columnIndex == 1) {
            return (String) element;
         } else {
            return "";
         }
      }
      if (element instanceof IResultsXViewerRow) {
         IResultsXViewerRow task = (IResultsXViewerRow) element;
         element = task.getData();
      }
      if (element instanceof IAtsConfigObject) {
         IAtsConfigObject configObj = (IAtsConfigObject) element;
         if (xCol.getName().equals("Type")) {
            return configObj.getClass().getSimpleName();
         } else if (xCol.getName().equals("Name")) {
            return configObj.toString();
         } else if (xCol.getName().equals("Id")) {
            return String.valueOf(configObj.getId());
         }
         return "";
      }
      return super.getColumnText(element, xCol, columnIndex);
   }

   @Override
   public Image getImage(Object element) {
      Image result = null;
      if (element instanceof IAtsActionableItem) {
         result = ArtifactImageManager.getImage(AtsArtifactTypes.ActionableItem);
      } else if (element instanceof IAtsTeamDefinition) {
         result = ArtifactImageManager.getImage(AtsArtifactTypes.TeamDefinition);
      } else if (element instanceof IAtsVersion) {
         result = ArtifactImageManager.getImage(AtsArtifactTypes.Version);
      } else if (element instanceof IAtsProgram) {
         result = ArtifactImageManager.getImage(AtsArtifactTypes.Program);
      }
      return result;
   }

   @Override
   public String getText(Object element) {
      return element.toString();
   }
}
