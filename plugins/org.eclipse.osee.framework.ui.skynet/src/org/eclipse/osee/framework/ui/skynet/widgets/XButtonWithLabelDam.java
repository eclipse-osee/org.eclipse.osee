/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Donald G. Dunne
 */
public abstract class XButtonWithLabelDam extends XButton implements IArtifactWidget {

   private Artifact artifact;
   protected Label resultsLabelWidget;

   public XButtonWithLabelDam(String displayLabel, String toolTip, Image image) {
      super(displayLabel);
      setImage(image);
      setToolTip(toolTip);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      numColumns = 3;
      super.createControls(parent, horizontalSpan);
      resultsLabelWidget = new Label(bComp, SWT.NONE);
      refreshLabel();
   }

   protected void refreshLabel() {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            resultsLabelWidget.setText(getResultsText());
            resultsLabelWidget.getParent().layout(true);
         }
      });
   }

   protected abstract String getResultsText();

   @Override
   public void setArtifact(Artifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

}
