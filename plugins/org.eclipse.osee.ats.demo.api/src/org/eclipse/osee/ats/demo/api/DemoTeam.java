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
package org.eclipse.osee.ats.demo.api;

import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * Convenience method for demo plugin to retrieve configured teams for use mostly in DemoNavigateViewItem.
 * 
 * @author Donald G. Dunne
 */
public enum DemoTeam {
   Process_Team(DemoArtifactToken.Process_Team),
   Tools_Team(DemoArtifactToken.Tools_Team),
   SAW_HW(DemoArtifactToken.SAW_HW),
   SAW_Code(DemoArtifactToken.SAW_Code),
   SAW_Test(DemoArtifactToken.SAW_Test),
   SAW_SW_Design(DemoArtifactToken.SAW_SW_Design),
   SAW_Requirements(DemoArtifactToken.SAW_Requirements),
   SAW_SW(DemoArtifactToken.SAW_SW),
   CIS_SW(DemoArtifactToken.CIS_SW),
   CIS_Code(DemoArtifactToken.CIS_Code),
   CIS_Test(DemoArtifactToken.CIS_Test),
   Facilities_Team(DemoArtifactToken.Facilities_Team);

   private final ArtifactToken teamDefToken;

   private DemoTeam(ArtifactToken teamDefToken) {
      this.teamDefToken = teamDefToken;
   }

   public ArtifactToken getTeamDefToken() {
      return teamDefToken;
   }
}
