/*********************************************************************
 * Copyright (c) 2022 Boeing
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

/**
 * @author Audrey Denk
 */
package org.eclipse.osee.framework.core.applicability;

import java.util.Set;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactToken;

public class ApplicabilityUseResultToken {
   private ApplicabilityToken app;
   private Set<ArtifactToken> arts;

   public ApplicabilityUseResultToken() {
      //Empty constructor is required for json compatibility
   }

   public ApplicabilityUseResultToken(ApplicabilityToken app, Set<ArtifactToken> arts) {
      this.app = app;
      this.arts = arts;
   }

   public ApplicabilityToken getApp() {
      return app;
   }

   public Set<ArtifactToken> getArts() {
      return arts;
   }
}