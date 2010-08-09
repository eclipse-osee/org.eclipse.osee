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

package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Collection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.IHandlePromptChange;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.IPromptFactory;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.PromptFactory;

/**
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public final class ArtifactPromptChange {

   private IPromptFactory promptFactory;
   private AccessPolicyHandler policyHandler;

   public ArtifactPromptChange() {
      this.promptFactory = null;
      this.policyHandler = null;
   }

   public ArtifactPromptChange(IPromptFactory promptFactory, AccessPolicyHandler policyHandler) {
      this.promptFactory = promptFactory;
      this.policyHandler = policyHandler;
   }

   public void dependeices() throws OseeCoreException {
      if (promptFactory == null && policyHandler == null) {
         promptFactory = new PromptFactory();
         policyHandler = new AccessPolicyHandler(UserManager.getUser(), AccessControlManager.getService());
      }
   }

   public static boolean promptChangeAttribute(IAttributeType attributeType, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws OseeCoreException {
      return promptChangeAttribute(attributeType, displayName, artifacts, persist, true);
   }

   public static boolean promptChangeAttribute(IAttributeType attributeType, String displayName, final Collection<? extends Artifact> artifacts, boolean persist, boolean multiLine) throws OseeCoreException {
      ArtifactPromptChange artifactPromptChange = new ArtifactPromptChange();
      artifactPromptChange.dependeices();
      return artifactPromptChange.promptChangeAttribute(attributeType, artifacts, persist, multiLine, displayName);
   }

   public boolean promptChangeAttribute(IAttributeType attributeType, final Collection<? extends Artifact> artifacts, boolean persist, boolean multiLine, String displayName) {
      boolean toReturn = false;
      try {

         boolean hasPermission =
            policyHandler.hasAttributeTypePermission(artifacts, attributeType, PermissionEnum.WRITE,
               OseeLevel.SEVERE_POPUP).matched();

         if (hasPermission) {
            IHandlePromptChange promptChange =
               promptFactory.createPrompt(attributeType, displayName, artifacts, persist, multiLine);
            toReturn = handlePromptChange(promptChange);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return toReturn;
   }

   private static boolean handlePromptChange(IHandlePromptChange promptChange) throws OseeCoreException {
      boolean toReturn = false;

      if (promptChange.promptOk()) {
         toReturn = promptChange.store();
      }
      return toReturn;
   }
}
