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
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.IHandlePromptChange;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.IPromptFactory;

/**
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public final class ArtifactPrompt {

   private final IPromptFactory promptFactory;
   private final AccessPolicy policyHandler;

   public ArtifactPrompt(IPromptFactory promptFactory, AccessPolicy policyHandler) {
      this.promptFactory = promptFactory;
      this.policyHandler = policyHandler;
   }

   public boolean promptChangeAttribute(AttributeTypeToken attributeType, final Collection<? extends Artifact> artifacts, boolean persist, boolean multiLine)  {
      boolean toReturn = false;
      boolean hasPermission = policyHandler.hasAttributeTypePermission(artifacts, attributeType, PermissionEnum.WRITE,
         OseeLevel.SEVERE_POPUP).matched();

      if (hasPermission) {
         IHandlePromptChange promptChange = promptFactory.createPrompt(attributeType,
            attributeType.getUnqualifiedName(), artifacts, persist, multiLine);
         if (promptChange.promptOk()) {
            toReturn = promptChange.store();
         }
      }
      return toReturn;
   }
}
