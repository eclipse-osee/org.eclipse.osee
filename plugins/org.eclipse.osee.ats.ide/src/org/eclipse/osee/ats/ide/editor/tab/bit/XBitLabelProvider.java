/*******************************************************************************
 * Copyright (c) 2021 Boeing.
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
package org.eclipse.osee.ats.ide.editor.tab.bit;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.JaxTeamWorkflow;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.cr.TaskEstDefinition;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XBitLabelProvider extends XViewerLabelProvider {

   private final AtsApi atsApi;

   public XBitLabelProvider(XBitViewer xBitViewer) {
      super(xBitViewer);
      this.atsApi = AtsApiService.get();
   }

   @Override
   public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
      if (element instanceof TaskEstDefinition) {
         return null;
      }
      return super.getForeground(element, xCol, columnIndex);
   }

   @Override
   public Color getBackground(Object element, XViewerColumn xCol, int columnIndex) {
      if (element instanceof TaskEstDefinition) {
         return null;
      }
      return super.getBackground(element, xCol, columnIndex);
   }

   @Override
   public String getColumnText(Object element, int columnIndex) {
      XViewerColumn xViewerColumn = getTreeColumnOffIndex(columnIndex);
      if (element instanceof BuildImpactData) {
         BuildImpactData bid = (BuildImpactData) element;
         if (xViewerColumn.getName().equals(XBitXViewerFactory.Program_Col.getName())) {
            return bid.getProgram().getName();
         } else if (xViewerColumn.getName().equals(XBitXViewerFactory.Build_Col.getName())) {
            return bid.getBuild().getName();
         } else if (xViewerColumn.getName().equals(XBitXViewerFactory.State_Col.getName())) {
            return bid.getState();
         }
         if (xViewerColumn.getName().equals(XBitXViewerFactory.Id_Col.getName())) {
            return bid.getBidArt().getIdString();
         } else if (xViewerColumn.getName().equals(XBitXViewerFactory.Cr_State_Col.getName())) {
            if (bid.getTeamWfs().isEmpty()) {
               return "None Created";
            }
            int completed = 0;
            for (JaxTeamWorkflow teamWf : bid.getTeamWfs()) {
               if (teamWf.getStateType().isCompletedOrCancelled()) {
                  completed++;
               }
            }
            return String.format("%s of %s Completed", completed, bid.getTeamWfs().size());
         }
      }
      if (element instanceof JaxTeamWorkflow) {
         JaxTeamWorkflow teamWf = (JaxTeamWorkflow) element;
         if (xViewerColumn.getName().equals(XBitXViewerFactory.Id_Col.getName())) {
            return teamWf.getAtsId();
         } else if (xViewerColumn.getName().equals(XBitXViewerFactory.Cr_State_Col.getName())) {
            return teamWf.getCurrentState();
         } else if (xViewerColumn.getName().equals(XBitXViewerFactory.Cr_Title_Col.getName())) {
            return teamWf.getName();
         } else if (xViewerColumn.getName().equals(XBitXViewerFactory.Cr_Type_Col.getName())) {
            return teamWf.getTeamName();
         }

      }
      return "";
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) {
      XViewerColumn xViewerColumn = getTreeColumnOffIndex(columnIndex);
      if (element instanceof JaxTeamWorkflow) {
         if (xViewerColumn.getName().equals(XBitXViewerFactory.Cr_Type_Col.getName())) {
            return ImageManager.getImage(AtsImage.WORKFLOW);
         }
      }
      if (element instanceof BuildImpactData) {
         if (xViewerColumn.getName().equals(XBitXViewerFactory.Program_Col.getName())) {
            return ImageManager.getImage(AtsImage.PROGRAM);
         } else if (xViewerColumn.getName().equals(XBitXViewerFactory.Build_Col.getName())) {
            return ImageManager.getImage(AtsImage.VERSION);
         }
      }
      return null;
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
      return "";
   }

}
