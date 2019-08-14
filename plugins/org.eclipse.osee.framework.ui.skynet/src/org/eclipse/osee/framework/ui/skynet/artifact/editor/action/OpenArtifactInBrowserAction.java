/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.artifact.editor.action;

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactUrlClient;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/**
 * Open Artifact in Native Browser.
 *
 * @author Donald G. Dunne
 */
public final class OpenArtifactInBrowserAction extends Action {

   private final Artifact artifact;

   public OpenArtifactInBrowserAction(Artifact artifact) {
      super();
      this.artifact = artifact;
      setImageDescriptor(ImageManager.getProgramImageDescriptor("html"));
      setToolTipText("Open Artifact in Native Browser");
   }

   @Override
   public void run() {
      try {
         String urlString =
            String.format("%sorcs/branch/%s/artifact/%s", new ArtifactUrlClient().getSelectedPermanentLinkUrl(),
               artifact.getBranch().getIdString(), artifact.getIdString());
         Program.launch(urlString);
      } catch (Exception ex) {
         OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error obtaining url for - guid: [%s] branch:[%s]",
            artifact.getGuid(), artifact.getBranch().getIdString());
      }
   }

}
