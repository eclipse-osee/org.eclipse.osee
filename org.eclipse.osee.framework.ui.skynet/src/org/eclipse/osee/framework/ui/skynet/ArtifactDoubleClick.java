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

import java.util.ArrayList;
import java.util.logging.Level;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.ui.skynet.preferences.EditorsPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.search.ui.text.Match;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactDoubleClick implements IDoubleClickListener {
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
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, "The artifact associated with the double-click was null");
      } else {
         try {
            if (AccessControlManager.checkObjectPermission(artifact, PermissionEnum.READ)) {
               ArrayList<Artifact> artifacts = new ArrayList<Artifact>(1);
               artifacts.add(artifact);

               if (StaticIdManager.hasValue(UserManager.getUser(),
                     EditorsPreferencePage.PreviewOnDoubleClickForWordArtifacts)) {
                  RendererManager.previewInJob(artifact);
               } else {
                  RendererManager.openInJob(artifact, PresentationType.GENERALIZED_EDIT);
               }

            } else {
               OSEELog.logSevere(SkynetGuiPlugin.class,
                     "The user " + UserManager.getUser() + " does not have read access to " + artifact, true);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
   }
}