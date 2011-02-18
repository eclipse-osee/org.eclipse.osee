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
package org.eclipse.osee.support.test.util;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Ryan D. Brooks
 */
public final class DemoArtifactTypes {

   // @formatter:off
   public static final IArtifactType DemoCodeTeamWorkflow = TokenFactory.createArtifactType("ABRNqDKnpGEKAyUm49gA", "Demo Code Team Workflow");
   public static final IArtifactType DemoReqTeamWorkflow = TokenFactory.createArtifactType("ABRO5pC6kCmP35t06RwA", "Demo Req Team Workflow");
   public static final IArtifactType DemoTestTeamWorkflow = TokenFactory.createArtifactType("ABRPeQO1qlCd4J7Bv5AA", "Demo Test Team Workflow");
   // @formatter:on

   private DemoArtifactTypes() {
      // Constants
   }
}
