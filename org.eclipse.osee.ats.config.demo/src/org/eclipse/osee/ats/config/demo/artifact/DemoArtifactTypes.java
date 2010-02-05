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
package org.eclipse.osee.ats.config.demo.artifact;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.NamedIdentity;

/**
 * @author Ryan D. Brooks
 */
public class DemoArtifactTypes extends NamedIdentity implements IArtifactType {
   public static final DemoArtifactTypes DemoCodeTeamWorkflow =
         new DemoArtifactTypes("ABRNqDKnpGEKAyUm49gA", "Demo Code Team Workflow");
   public static final DemoArtifactTypes DemoReqTeamWorkflow =
         new DemoArtifactTypes("ABRO5pC6kCmP35t06RwA", "Demo Req Team Workflow");
   public static final DemoArtifactTypes DemoTestTeamWorkflow =
         new DemoArtifactTypes("ABRPeQO1qlCd4J7Bv5AA", "Demo Test Team Workflow");

   private DemoArtifactTypes(String guid, String name) {
      super(guid, name);
   }
}
