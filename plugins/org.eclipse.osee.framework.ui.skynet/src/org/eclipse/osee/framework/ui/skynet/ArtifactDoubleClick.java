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

import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
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
      PermissionEnum permissionEnum = getPermissionEnum(artifact);
      if (AccessControlManager.hasPermission(artifact, permissionEnum)) {
         RendererManager.openInJob(artifact, type);
      } else {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP,
            "The user " + UserManager.getUser() + " does not have " + permissionEnum + " access to " + artifact);
      }
   }

   public static PermissionEnum getPermissionEnum(Artifact artifact) {
      return artifact.isReadOnly() ? PermissionEnum.READ : PermissionEnum.WRITE;
   }

   public static PresentationType getPresentationType(Artifact artifact) {
      return artifact.isReadOnly() ? PresentationType.DEFAULT_OPEN : edit(artifact);
   }

   private static PresentationType edit(Artifact artifact) {
      return (UserManager.getBooleanSetting(UserManager.DOUBLE_CLICK_SETTING_KEY)) ? PresentationType.GENERALIZED_EDIT : PresentationType.SPECIALIZED_EDIT;
   }

}