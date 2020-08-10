/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactDoubleClick implements IDoubleClickListener {
   @Override
   public void doubleClick(DoubleClickEvent event) {
      openArtifact(event.getSelection());
   }

   public static void openArtifact(ISelection selection) {
      IStructuredSelection structuredSelection = (IStructuredSelection) selection;
      List<Artifact> artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);
      if (artifacts.isEmpty()) {
         OseeLog.log(Activator.class, Level.SEVERE, "The artifact associated with the double-click was null");
      } else {
         Artifact artifact = artifacts.iterator().next();
         try {
            open(artifact);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   public static void open(Artifact artifact) {
      PresentationType type = getPresentationType(artifact);
      RendererManager.openInJob(artifact, type);
   }

   public static PermissionEnum getPermissionEnum(Artifact artifact) {
      return artifact.isReadOnly() ? PermissionEnum.READ : PermissionEnum.WRITE;
   }

   public static PresentationType getPresentationType(Artifact artifact) {
      return artifact.isReadOnly() ? PresentationType.DEFAULT_OPEN : edit(artifact);
   }

   private static PresentationType edit(Artifact artifact) {
      PresentationType type = PresentationType.DEFAULT_OPEN;
      if (RendererManager.isDefaultArtifactEditor()) {
         type = PresentationType.GENERAL_REQUESTED;
      } else if (UserManager.getBooleanSetting(UserManager.DOUBLE_CLICK_SETTING_KEY_EDIT)) {
         type = PresentationType.SPECIALIZED_EDIT;
      }
      return type;
   }
}