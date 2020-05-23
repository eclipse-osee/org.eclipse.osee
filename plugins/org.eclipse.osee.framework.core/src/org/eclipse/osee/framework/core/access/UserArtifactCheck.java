/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.core.access;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

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
   public XResultData isDeleteable(Collection<ArtifactToken> artifacts, XResultData results) {
      if (enabled) {
         for (ArtifactToken art : artifacts) {
            if (art.getArtifactType().equals(CoreArtifactTypes.User)) {
               results.error("Deletion of User artifact is prohibited.  Use Purge User Blam instead.");
            }
         }
      }
      return new XResultData();
   }

}
