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

package org.eclipse.osee.ote.ui.define.viewers.actions;

import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.ote.define.artifacts.ArtifactTestRunOperator;
import org.eclipse.osee.ote.ui.define.internal.Activator;
import org.eclipse.osee.ote.ui.define.utilities.SelectionHelper;

/**
 * @author Roberto E. Escobar
 */
public class OpenInArtifactEditor extends AbstractActionHandler {

   public OpenInArtifactEditor(StructuredViewer viewer, String text) throws Exception {
      super(viewer, text);
   }

   public OpenInArtifactEditor(StructuredViewer viewer, String text, ImageDescriptor image) throws Exception {
      super(viewer, text, image);
   }

   @Override
   public void run() {
      try {
         ArtifactTestRunOperator operator = SelectionHelper.getInstance().getSelection(getViewer());
         if (isValidSelection(operator)) {
            Artifact artifact = operator.getTestRunArtifact();
            checkPermissions(artifact);
            RendererManager.open(artifact, PresentationType.GENERALIZED_EDIT);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Unable to open artifact.", ex);
      }
   }

   @Override
   public void updateState() {
      ArtifactTestRunOperator operator = SelectionHelper.getInstance().getSelection(getViewer());
      setEnabled(isValidSelection(operator));
   }

   private boolean isValidSelection(ArtifactTestRunOperator operator) {
      return operator != null && operator.hasValidArtifact() && operator.isFromLocalWorkspace() != true;
   }

   private void checkPermissions(Artifact artifact) {
      if (ServiceUtil.getOseeClient().getAccessControlService().hasArtifactPermission(artifact, PermissionEnum.READ,
         null).isErrors()) {
         throw new OseeArgumentException("The user %s does not have read access to %s", UserManager.getUser(),
            artifact);
      }
   }
}