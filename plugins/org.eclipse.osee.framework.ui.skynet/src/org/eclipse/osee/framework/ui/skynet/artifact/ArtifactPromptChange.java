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

import java.text.NumberFormat;
import java.util.Collection;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessDataQuery;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.BooleanHandlePromptChange;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.DateHandlePromptChange;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.EnumeratedHandlePromptChange;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.IHandlePromptChange;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.StringHandlePromptChange;

/**
 * @author Donald G. Dunne
 */
public final class ArtifactPromptChange {

   public static boolean promptChangeAttribute(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist) throws OseeCoreException {
      return promptChangeAttribute(attributeName, displayName, artifacts, persist, true);
   }

   public static boolean promptChangeAttribute(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist, boolean multiLine) throws OseeCoreException {
      return promptChangeAttribute(UserManager.getUser(), AccessControlManager.getService(), null, attributeName,
         displayName, artifacts, persist, multiLine);
   }

   public static boolean promptChangeAttribute(IBasicArtifact<?> userArtifact, IAccessControlService accessControlService, IHandlePromptChange promptChange, String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist, boolean multiLine) {
      boolean toReturn = false;
      try {
         PermissionStatus permissionStatus =
            hasWritePermission(userArtifact, accessControlService, attributeName, artifacts);
         toReturn = permissionStatus.matches();

         if (toReturn) {
            //For testing a testPromptChange can be passed
            if (promptChange == null) {
               promptChange = createHandlePrompt(attributeName, displayName, artifacts, persist, multiLine);
            }
            if (promptChange != null) {
               toReturn = handlePromptChange(promptChange);
            } else {
               AWorkbench.popup("ERROR", "Unhandled attribute type.  Can't edit through this view");
            }
         } else {
            AWorkbench.popup("No Permission", permissionStatus.getReason());
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return toReturn;
   }

   private static IHandlePromptChange createHandlePrompt(String attributeName, String displayName, final Collection<? extends Artifact> artifacts, boolean persist, boolean multiLine) throws OseeCoreException, Exception {
      IHandlePromptChange promptChange = null;

      if (AttributeTypeManager.isBaseTypeCompatible(DateAttribute.class, attributeName)) {
         promptChange = new DateHandlePromptChange(artifacts, attributeName, displayName, persist);
      } else if (AttributeTypeManager.isBaseTypeCompatible(FloatingPointAttribute.class, attributeName)) {
         promptChange =
            new StringHandlePromptChange(attributeName, persist, displayName, artifacts, NumberFormat.getInstance(),
               false);
      } else if (AttributeTypeManager.isBaseTypeCompatible(IntegerAttribute.class, attributeName)) {
         promptChange =
            new StringHandlePromptChange(attributeName, persist, displayName, artifacts,
               NumberFormat.getIntegerInstance(), false);
      } else if (AttributeTypeManager.isBaseTypeCompatible(BooleanAttribute.class, attributeName)) {
         promptChange = new BooleanHandlePromptChange(artifacts, attributeName, displayName, persist, null);
      } else if (AttributeTypeManager.isBaseTypeCompatible(EnumeratedAttribute.class, attributeName)) {
         promptChange = new EnumeratedHandlePromptChange(artifacts, attributeName, displayName, persist);
      } else if (AttributeTypeManager.isBaseTypeCompatible(StringAttribute.class, attributeName)) {
         promptChange = new StringHandlePromptChange(attributeName, persist, displayName, artifacts, null, multiLine);
      }
      return promptChange;
   }

   private static PermissionStatus hasWritePermission(IBasicArtifact<?> userArtifact, IAccessControlService accessControlService, String attributeName, final Collection<? extends Artifact> artifacts) throws OseeCoreException {
      AccessDataQuery query = accessControlService.getAccessData(userArtifact, artifacts);
      PermissionStatus permissionStatus = new PermissionStatus();

      if (artifacts != null) {
         for (Artifact artifact : artifacts) {
            query.attributeTypeMatches(PermissionEnum.WRITE, artifact, AttributeTypeManager.getType(attributeName),
               permissionStatus);

            if (!permissionStatus.matches()) {
               break;
            }
         }
      }
      return permissionStatus;
   }

   private static boolean handlePromptChange(IHandlePromptChange promptChange) throws OseeCoreException {
      boolean toReturn = false;

      if (promptChange.promptOk()) {
         toReturn = promptChange.store();
      }
      return toReturn;
   }
}
