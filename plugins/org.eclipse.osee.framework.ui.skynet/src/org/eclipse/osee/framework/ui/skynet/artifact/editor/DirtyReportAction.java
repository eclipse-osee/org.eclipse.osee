/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

public final class DirtyReportAction extends Action {
   private final Artifact artifact;

   public DirtyReportAction(Artifact artifact) {
      super();
      this.artifact = artifact;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.DIRTY));
      setToolTipText("&Dirty Report");
      setText("&Dirty Report");
      setToolTipText("Show what attribute or relation making artifact dirty.");
   }

   @Override
   public void run() {
      String rString = Artifacts.getDirtyReport(artifact);
      AWorkbench.popup("Dirty Report",
         "NOTE: This only shows if the artifact is dirty and may not report if editor is dirty.\n\n" + (!Strings.isValid(
            rString) ? "Not Dirty" : "Dirty -> " + rString));
   }
}
