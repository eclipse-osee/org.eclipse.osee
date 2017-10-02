/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.util.widgets.defect;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelValue;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;

/**
 * Label to display the number of defects and direct user to Defects tab. This use to be the embedded Defects XViewer.
 *
 * @author Donald G. Dunne
 */
public class XDefectViewer extends XLabelValue implements IArtifactWidget, IOseeTreeReportProvider {

   private PeerToPeerReviewArtifact reviewArt;
   public final static String normalColor = "#EEEEEE";

   public XDefectViewer() {
      super("Defects", "");
   }

   @Override
   public Artifact getArtifact() {
      return reviewArt.getArtifact();
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void setArtifact(Artifact artifact) {
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
