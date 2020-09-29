/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.access;

import org.eclipse.osee.framework.core.access.context.AccessContext;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Donald G. Dunne
 */
public class FrameworkAccessContexts {

   //   accessContext "default.context" {
   //      guid "Bx9Pez3QYw92FEvfKOgA";
   //      ALLOW edit artifactType "Artifact";
   //   }
   public static class DefaultAccessContext extends AccessContext {
      public DefaultAccessContext() {
         this(FrameworkAccessContextTokens.DEFAULT_CONTEXT);
      }

      public DefaultAccessContext(AccessContextToken accessToken) {
         super(accessToken);
         allowEditArtifactType(CoreArtifactTypes.Artifact);
      }
   }

}
