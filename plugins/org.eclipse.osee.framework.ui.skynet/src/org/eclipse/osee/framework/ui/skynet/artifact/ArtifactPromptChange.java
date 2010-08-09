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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.internal.ArtifactPromptService;

/**
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public final class ArtifactPromptChange {

   private ArtifactPromptChange() {
      // Utility Class
   }

   private static ArtifactPromptService getService() throws OseeCoreException {
      return SkynetGuiPlugin.getInstance().getArtifactPromptService();
   }

   public static boolean promptChangeAttribute(IAttributeType attributeType, final Collection<? extends Artifact> artifacts, boolean persist) {
      return promptChangeAttribute(attributeType, artifacts, persist, true);
   }

   public static boolean promptChangeAttribute(IAttributeType attributeType, final Collection<? extends Artifact> artifacts, boolean persist, boolean multiLine) {
      boolean result = false;
      try {
         result = getService().promptChangeAttribute(attributeType, artifacts, persist, multiLine);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return result;
   }
}
