/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public final class ArtifactValidityReportAction extends Action {
   private final Artifact artifact;

   public ArtifactValidityReportAction(Artifact artifact) {
      super();
      this.artifact = artifact;
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.CHECKBOX_CHECK_TRUE));
      setText("Artifact &Validity Report");
      setToolTipText("Run validity checks on this artifact in the database.");
   }

   @Override
   public void run() {
      try {
         String urlString = String.format("%s/orcs/branch/%s/artifact/%s/validity",
            OseeClient.getOseeWebApplicationServer(), artifact.getBranch().getIdString(), artifact.getIdString());
         Program.launch(urlString);
      } catch (Exception ex) {
         OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error obtaining url for - guid: [%s] branch:[%s]",
            artifact.getGuid(), artifact.getBranch().getIdString());
      }
   }
}
