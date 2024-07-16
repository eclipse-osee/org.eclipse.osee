/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ToStringViewerSorter;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassXViewer;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author Donald G. Dunne
 */
public class AddRelationColumnAction extends Action {

   private final MassXViewer xViewer;
   private final boolean asToken;

   public AddRelationColumnAction(MassXViewer xViewer, boolean asToken) {
      super(String.format("Add Related Artifact(s) %s Column", (asToken ? "Token(s)" : "Name(s)")));
      this.xViewer = xViewer;
      this.asToken = asToken;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.ADD_GREEN);
   }

   @Override
   public void run() {
      TreeColumn insertTreeCol = xViewer.getRightClickSelectedColumn();
      XViewerColumn insertXCol = (XViewerColumn) insertTreeCol.getData();
      FilteredCheckboxTreeDialog<RelationTypeSide> dialog =
         new FilteredCheckboxTreeDialog<RelationTypeSide>("Add Relation Column", "Select Relation Type and Side",
            getInput(), new ArrayTreeContentProvider(), new RelationSideLabelProvider(), new ToStringViewerSorter());
      if (dialog.open() == Window.OK) {
         CustomizeData custData = xViewer.getCustomizeMgr().generateCustDataFromTable();
         List<XViewerColumn> xCols = custData.getColumnData().getColumns();
         List<XViewerColumn> newXCols = new ArrayList<>();
         for (XViewerColumn currXCol : xCols) {
            if (currXCol.equals(insertXCol)) {
               for (RelationTypeSide rts : dialog.getChecked()) {
                  XViewerRelatedArtifactsColumn newXCol = new XViewerRelatedArtifactsColumn(rts, asToken);
                  newXCol.setShow(true);
                  newXCol.setXViewer(xViewer);
                  newXCols.add(newXCol);
               }
            }
            newXCols.add(currXCol);
         }
         custData.getColumnData().setColumns(newXCols);
         xViewer.getCustomizeMgr().loadCustomization(custData);
         xViewer.refresh();
      }
   }

   private Collection<RelationTypeSide> getInput() {
      List<RelationTypeSide> sides = new ArrayList<>();
      for (RelationTypeToken type : ServiceUtil.getTokenService().getRelationTypes()) {
         sides.add(new RelationTypeSide(type, RelationSide.SIDE_A));
         sides.add(new RelationTypeSide(type, RelationSide.SIDE_B));
      }
      return sides;
   }

   private class RelationSideLabelProvider extends LabelProvider {

      @Override
      public String getText(Object element) {
         if (element instanceof RelationTypeSide) {
            RelationTypeSide typeSide = ((RelationTypeSide) element);
            return getTypeSideName(typeSide);
         }
         return element.toString();
      }

   }

   public static String getTypeSideName(RelationTypeSide typeSide) {
      return String.format("%s - %s", typeSide.getRelationType().getName(), typeSide.getSideName(typeSide.getSide()));
   }
}