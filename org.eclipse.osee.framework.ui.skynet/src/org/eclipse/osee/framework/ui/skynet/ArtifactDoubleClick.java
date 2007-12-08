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

package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.search.ui.text.Match;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactDoubleClick implements IDoubleClickListener {
   private static final SkynetAuthentication skynetAuth = SkynetAuthentication.getInstance();
   private static final AccessControlManager accessManager = AccessControlManager.getInstance();

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IDoubleClickListener#doubleClick(org.eclipse.jface.viewers.DoubleClickEvent)
    */
   public void doubleClick(DoubleClickEvent event) {
      openArtifact(event.getSelection());
   }

   public static void openArtifact(ISelection selection) {
      IStructuredSelection structSel = (IStructuredSelection) selection;
      Object object = (Object) structSel.getFirstElement();
      Artifact artifact = null;
      if (object instanceof Artifact) {
         artifact = (Artifact) structSel.getFirstElement();
      } else if (object instanceof Match) {
         Match match = (Match) object;

         if (match.getElement() instanceof Artifact) {
            artifact = (Artifact) match.getElement();
         }
      }

      if (artifact == null) {
         OSEELog.logSevere(SkynetGuiPlugin.class, "The artifact associated with the double-click was null", false);
      } else {
         if (accessManager.checkObjectPermission(skynetAuth.getAuthenticatedUser(), artifact, PermissionEnum.READ)) {
            if (artifact instanceof IATSArtifact)
               OseeAts.openATSArtifact(artifact);
            else
               ArtifactEditor.editArtifact(artifact);
         } else {
            OSEELog.logInfo(SkynetGuiPlugin.class,
                  "The user " + skynetAuth.getAuthenticatedUser() + " does not have read access to " + artifact, true);
         }
      }

   }
}
