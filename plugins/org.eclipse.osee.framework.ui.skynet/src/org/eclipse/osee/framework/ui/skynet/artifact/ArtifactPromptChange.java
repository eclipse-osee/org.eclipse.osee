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
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.PromptFactory;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public final class ArtifactPromptChange {

   private static ArtifactPrompt prompt;

   private AccessPolicy accessPolicy;

   public void setAccessPolicy(AccessPolicy accessPolicy) {
      this.accessPolicy = accessPolicy;
   }

   public void start() {
      prompt = new ArtifactPrompt(new PromptFactory(), accessPolicy);
   }

   public void stop() {
      prompt = null;
   }

   private static ArtifactPrompt getArtifactPrompt() {
      return ArtifactPromptChange.prompt;
   }

   public static boolean promptChangeAttribute(AttributeTypeToken attributeType, final Collection<? extends Artifact> artifacts, boolean persist) {
      return promptChangeAttribute(attributeType, artifacts, persist, true);
   }

   public static boolean promptChangeAttribute(AttributeTypeToken attributeType, final Collection<? extends Artifact> artifacts, boolean persist, boolean multiLine) {
      boolean result = false;
      ArtifactPrompt prompt = getArtifactPrompt();
      if (prompt != null) {
         try {
            result = prompt.promptChangeAttribute(attributeType, artifacts, persist, multiLine);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      } else {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Artifact prompt was null");
      }
      return result;
   }
}
