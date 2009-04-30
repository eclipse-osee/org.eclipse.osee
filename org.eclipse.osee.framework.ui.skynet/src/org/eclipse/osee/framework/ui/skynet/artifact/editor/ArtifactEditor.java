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

package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import java.util.Collection;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.implementations.NewArtifactEditor;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactEditor extends NewArtifactEditor {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.artifact.editor.NewArtifactEditor";

   public static void editArtifacts(final Collection<Artifact> artifacts) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               for (Artifact artifact : artifacts) {
                  if (!AccessControlManager.checkObjectPermission(artifact, PermissionEnum.READ)) {
                     OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP,
                           "The user " + UserManager.getUser() + " does not have read access to " + artifact);
                  } else
                     AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact), EDITOR_ID);
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   public static void editArtifact(final Artifact artifact) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               if (!AccessControlManager.checkObjectPermission(artifact, PermissionEnum.READ)) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP,
                        "The user " + UserManager.getUser() + " does not have read access to " + artifact);
               } else if (artifact != null) {
                  AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact), EDITOR_ID);
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }
}
