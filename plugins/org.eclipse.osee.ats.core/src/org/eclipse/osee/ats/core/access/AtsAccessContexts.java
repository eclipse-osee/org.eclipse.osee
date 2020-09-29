/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.access;

import org.eclipse.osee.ats.api.access.AtsAccessContextTokens;
import org.eclipse.osee.framework.core.access.FrameworkAccessContexts;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Donald G. Dunne
 */
public class AtsAccessContexts extends FrameworkAccessContexts {

   private AtsAccessContexts() {
      // do nothing
   }

   public static void register() {
      new AtsDefaultAccessContext();
   }

   /**
    * Deny editing all for baseline branches
    *
    * @author Donald G. Dunne
    */
   public static class AtsDefaultAccessContext extends DefaultAccessContext {
      public AtsDefaultAccessContext() {
         this(AtsAccessContextTokens.ATS_BRANCH_READ);
      }

      public AtsDefaultAccessContext(AccessContextToken accessToken) {
         super(accessToken);

         denyEditArtifactType(CoreArtifactTypes.Artifact);
      }
   }

}
