/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.artifact.editor.action;

import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class XViewerRelatedArtifactsColumn extends XViewerValueColumn {

   private final static String ID = "osee.framework.related.artifacts";
   private final RelationTypeSide rts;
   private final boolean asToken;

   public XViewerRelatedArtifactsColumn(RelationTypeSide rts, boolean asToken) {
      super(ID + rts.getRelationType().getName() + rts.getSide().name(), "", 90, XViewerAlign.Left, false,
         SortDataType.String, false, "Show delimited list of artifacts on other side of relation.");
      this.rts = rts;
      this.asToken = asToken;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      if (element instanceof Artifact) {
         List<Artifact> relatedArtifacts = ((Artifact) element).getRelatedArtifacts(rts);
         StringBuilder sb = new StringBuilder();
         for (Artifact art : relatedArtifacts) {
            if (!asToken) {
               sb.append("[");
            }
            sb.append(asToken ? art.toStringWithId() : art.getName());
            if (!asToken) {
               sb.append("], [");
            } else {
               sb.append(", ");
            }
         }
         if (asToken) {
            return sb.toString().replaceFirst(", $", "");
         } else {
            return sb.toString().replaceFirst(", \\[$", "");
         }
      }
      return "";
   }

   @Override
   public String getName() {
      return AddRelationColumnAction.getTypeSideName(rts);
   }

   @Override
   public XViewerRelatedArtifactsColumn copy() {
      XViewerRelatedArtifactsColumn col = new XViewerRelatedArtifactsColumn(rts, asToken);
      col.setXViewer(getXViewer());
      return col;
   }

}
