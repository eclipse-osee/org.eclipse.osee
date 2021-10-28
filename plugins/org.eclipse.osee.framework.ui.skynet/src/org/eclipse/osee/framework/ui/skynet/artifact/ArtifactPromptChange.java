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

package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.PromptFactory;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public final class ArtifactPromptChange {

   private static ArtifactPrompt prompt;

   private static ArtifactPrompt getArtifactPrompt() {
      if (prompt == null) {
         prompt = new ArtifactPrompt(new PromptFactory());
      }
      return ArtifactPromptChange.prompt;
   }

   @SuppressWarnings("unchecked")
   public static boolean promptChangeAttribute(AttributeTypeToken attributeType, final Collection<? extends Artifact> artifacts, boolean persist) {
      boolean result = false;
      ArtifactPrompt prompt = getArtifactPrompt();
      if (prompt != null) {
         try {
            result = prompt.promptChangeAttribute(attributeType, (Collection<Artifact>) artifacts, persist);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      } else {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Artifact prompt was null");
      }
      return result;
   }
}
