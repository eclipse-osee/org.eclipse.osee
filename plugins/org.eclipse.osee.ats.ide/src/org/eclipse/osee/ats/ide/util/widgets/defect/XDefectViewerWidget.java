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

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelValueWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.osgi.service.component.annotations.Component;

/**
 * Label to display the number of defects and direct user to Defects tab. This use to be the embedded Defects XViewer.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XDefectViewerWidget extends XLabelValueWidget implements IOseeTreeReportProvider {

   public static WidgetId ID = WidgetIdAts.XDefectViewerWidget;

   private PeerToPeerReviewArtifact reviewArt;
   public final static String normalColor = "#EEEEEE";

   public XDefectViewerWidget() {
      super(ID, "Defects", "");
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      reviewArt = (PeerToPeerReviewArtifact) artifact;
      refreshLabel();
   }

   @Override
   public void refresh() {
      super.refresh();
      refreshLabel();
   }

   private void refreshLabel() {
      setValueText(String.format("%d Found. See Defects tab for details",
         reviewArt.getAttributeCount(AtsAttributeTypes.ReviewDefect)));
   }

   public PeerToPeerReviewArtifact getReviewArt() {
      return reviewArt;
   }

   @Override
   public String getEditorTitle() {
      try {
         return String.format("Table Report - Defects for [%s]", getReviewArt());
      } catch (Exception ex) {
         // do nothing
      }
      return "Table Report - Defects";
   }

   @Override
   public String getReportTitle() {
      return getEditorTitle();
   }

}
