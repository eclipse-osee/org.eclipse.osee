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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.attr;

import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelValue;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;

/**
 * @author Donald G. Dunne
 */
public class XArtEdAttrViewer extends XLabelValue implements IArtifactWidget, IOseeTreeReportProvider {

   private Artifact artifact;
   public final static String normalColor = "#EEEEEE";

   public XArtEdAttrViewer() {
      super("Attributes", "");
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
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
      this.artifact = artifact;
   }

   @Override
   public String getEditorTitle() {
      try {
         return String.format("Attributes for %s", artifact.toStringWithId());
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
