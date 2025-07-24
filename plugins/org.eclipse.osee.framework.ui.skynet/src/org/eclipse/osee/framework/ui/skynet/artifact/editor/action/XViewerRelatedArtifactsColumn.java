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

   public final static String ID = "osee.framework.related.artifacts";
   public final static String AS_TOKEN = "AsToken";
   public final static String AS_NAME = "AsName";
   private final RelationTypeSide rts;
   private boolean asToken;

   /**
    * Create relation column where id = <relation id prefix>--<relTypeName>--<relTypeSide>--<AsToken or AsName>
    *
    * @param asToken - false, show just names; true show token in form of [<name>]-[<artId>]
    */
   public XViewerRelatedArtifactsColumn(RelationTypeSide rts, boolean asToken) {
      super(
         ID + "--" + rts.getRelationType().getId() + "--" + rts.getSide().name() + "--" + (asToken ? AS_TOKEN : AS_NAME),
         AbstractAddRelationColumnAction.getTypeSideName(rts), 90, XViewerAlign.Left, false, SortDataType.String, false,
         "Show delimited list of artifacts on other side of relation.");
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
   public XViewerRelatedArtifactsColumn copy() {
      XViewerRelatedArtifactsColumn col = new XViewerRelatedArtifactsColumn(rts, asToken);
      col.setXViewer(getXViewer());
      col.setAsToken(asToken);
      return col;
   }

   public boolean isAsToken() {
      return asToken;
   }

   public void setAsToken(boolean asToken) {
      this.asToken = asToken;
   }

}
