/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * User artifacts should not be deleted. User Purge User Blam instead.
 * 
 * @author Donald G. Dunne
 */
public class UserArtifactCheck extends ArtifactCheck {

   static boolean enabled = true;

   public static void setEnabled(boolean enabled) {
      UserArtifactCheck.enabled = enabled;
   }

   @Override
   public IStatus isDeleteable(Collection<Artifact> artifacts)  {
      if (enabled) {
         for (Artifact art : artifacts) {
            if (art.isOfType(CoreArtifactTypes.User)) {
               return new Status(IStatus.ERROR, org.eclipse.osee.framework.skynet.core.internal.Activator.PLUGIN_ID,
                  "Deletion of User artifact is prohibited.  Use Purge User Blam instead.");
            }
         }
      }
      return Status.OK_STATUS;
   }

}
