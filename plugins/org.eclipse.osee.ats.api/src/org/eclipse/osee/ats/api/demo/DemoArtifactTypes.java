/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.demo;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Ryan D. Brooks
 */
public final class DemoArtifactTypes {

   // @formatter:off
   public static final ArtifactTypeToken DemoCodeTeamWorkflow = ArtifactTypeToken.valueOf(79, "Demo Code Team Workflow");
   public static final ArtifactTypeToken DemoReqTeamWorkflow = ArtifactTypeToken.valueOf(80, "Demo Req Team Workflow");
   public static final ArtifactTypeToken DemoTestTeamWorkflow = ArtifactTypeToken.valueOf(81, "Demo Test Team Workflow");
   // @formatter:on

   private DemoArtifactTypes() {
      // Constants
   }
}
