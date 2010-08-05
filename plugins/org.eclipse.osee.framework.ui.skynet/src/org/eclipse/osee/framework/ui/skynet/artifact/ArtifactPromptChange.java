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
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.IHandlePromptChange;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.IPromptFactory;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.PromptFactory;

/**
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public final class ArtifactPromptChange {

   public static boolean promptChangeAttribute(IAttributeType attributeType, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws OseeCoreException {
      return promptChangeAttribute(attributeType, displayName, artifacts, persist, true);
   }

   public static boolean promptChangeAttribute(IAttributeType attributeType, String displayName, final Collection<? extends Artifact> artifacts, boolean persist, boolean multiLine) throws OseeCoreException {
      PromptFactory promptFactory = new PromptFactory(attributeType, displayName, artifacts, persist, multiLine);
      return promptChangeAttribute(UserManager.getUser(), AccessControlManager.getService(), promptFactory,
         attributeType, artifacts);
   }

   public static boolean promptChangeAttribute(IBasicArtifact<?> userArtifact, IAccessControlService accessControlService, IPromptFactory promptFactory, IAttributeType attributeType, final Collection<? extends Artifact> artifacts) {
      boolean toReturn = false;
      try {
         AccessPolicyHandler accessPolicyHandler =
            new AccessPolicyHandler(userArtifact, accessControlService, artifacts);
         boolean hasPermission =
            accessPolicyHandler.hasAttributeTypePermission(AttributeTypeManager.getType(attributeType),
               PermissionEnum.WRITE, true).matched();

         if (hasPermission) {
            IHandlePromptChange promptChange = promptFactory.createPrompt();
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
