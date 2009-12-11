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

/**
 * @author Ryan D. Brooks
 */
public enum DemoArtifactTypes implements IArtifactType {
   DemoCodeTeamWorkflow("Demo Code Team Workflow", "ABRNqDKnpGEKAyUm49gA"),
   DemoReqTeamWorkflow("Demo Req Team Workflow", "ABRO5pC6kCmP35t06RwA"),
   DemoTestTeamWorkflow("Demo Test Team Workflow", "ABRPeQO1qlCd4J7Bv5AA");

   private final String name;
   private final String guid;

   private DemoArtifactTypes(String name, String guid) {
      this.name = name;
      this.guid = guid;
   }

   public String getName() {
      return this.name;
   }

   public String getGuid() {
      return guid;
   }
}
