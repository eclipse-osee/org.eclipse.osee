/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.ui.skynet.explorer;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * @author Donald G. Dunne
 */
public class ArtifactExplorerAccessUserAuthEventHandler implements EventHandler {

   static Set<ArtifactExplorer> artifactExplorers = new HashSet<>();

   public static void registerArtifactExplorer(ArtifactExplorer artifactExplorer) {
      artifactExplorers.add(artifactExplorer);
   }

   @Override
   public void handleEvent(Event event) {
      try {
         for (ArtifactExplorer artifactExplorer : artifactExplorers) {
            ArtifactExplorer fArtifactExplorer = artifactExplorer;
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  fArtifactExplorer.getTreeViewer().refresh();
                  fArtifactExplorer.refreshBranchWarning();
               }
            });
         }
      } catch (Exception ex) {
         OseeLog.log(ArtifactExplorerAccessUserAuthEventHandler.class, Level.SEVERE, ex);
      }
   }

}
