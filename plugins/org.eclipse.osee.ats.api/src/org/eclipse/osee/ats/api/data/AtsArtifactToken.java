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
package org.eclipse.osee.ats.api.data;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Donald G. Dunne
 */
public final class AtsArtifactToken {

   /**
    * This token points to the ATS Access Control artifact that matches the current code base. Production and other code
    * bases may be looking at different artifacts for their access control based on the current OSEE Types and Access
    * grammar.
    */
   public static final ArtifactToken AtsCmAccessControl =
      TokenFactory.createArtifactToken(8635612, "ATS CM Access Control", CoreArtifactTypes.GeneralData);

   public static final ArtifactToken AtsConfig =
      TokenFactory.createArtifactToken(5367053, "ATS Config", CoreArtifactTypes.GeneralData);
   public static final ArtifactToken RuleDefinitions =
      TokenFactory.createArtifactToken(7880473, "Rule Definitions", AtsArtifactTypes.RuleDefinition);
   public static final ArtifactToken Users = TokenFactory.createArtifactToken(95459, "Users", CoreArtifactTypes.Folder);
   public static ArtifactToken HeadingFolder =
      TokenFactory.createArtifactToken(114713, "Action Tracking System", CoreArtifactTypes.Folder);
   public static ArtifactToken TopTeamDefinition =
      TokenFactory.createArtifactToken(56004, "Teams", AtsArtifactTypes.TeamDefinition);
   public static ArtifactToken TopActionableItem =
      TokenFactory.createArtifactToken(122894, "Actionable Items", AtsArtifactTypes.ActionableItem);
   public static ArtifactToken CountryFolder =
      TokenFactory.createArtifactToken(7968155, "Countries", CoreArtifactTypes.Folder);
   public static ArtifactToken ProgramFolder =
      TokenFactory.createArtifactToken(90442279, "Programs", CoreArtifactTypes.Folder);
   public static ArtifactToken ConfigFolder =
      TokenFactory.createArtifactToken(113036, "Config", CoreArtifactTypes.Folder);
   public static ArtifactToken ConfigsFolder =
      TokenFactory.createArtifactToken(5086714, "Configs", CoreArtifactTypes.Folder);
   public static ArtifactToken WorkDefinitionsFolder =
      TokenFactory.createArtifactToken(284655, "Work Definitions", CoreArtifactTypes.Folder);
   public static ArtifactToken WebPrograms =
      TokenFactory.createArtifactToken(277592, "Web Programs", CoreArtifactTypes.UniversalGroup);
   public static ArtifactToken EVReportPrograms =
      TokenFactory.createArtifactToken(8174118, "EV Report Programs", CoreArtifactTypes.UniversalGroup);
   public static ArtifactToken AtsAdmin =
      TokenFactory.createArtifactToken(136750, "AtsAdmin", CoreArtifactTypes.UserGroup);
   public static ArtifactToken AtsTempAdmin =
      TokenFactory.createArtifactToken(5367074, "AtsTempAdmin", CoreArtifactTypes.UserGroup);

   // Default Work Definitions
   public static ArtifactToken WorkDef_Goal =
      TokenFactory.createArtifactToken(142177, "WorkDef_Goal", AtsArtifactTypes.WorkDefinition);
   public static ArtifactToken WorkDef_Review_Decision =
      TokenFactory.createArtifactToken(25335, "WorkDef_Review_Decision", AtsArtifactTypes.WorkDefinition);
   public static ArtifactToken WorkDef_Review_PeerToPeer =
      TokenFactory.createArtifactToken(25334, "WorkDef_Review_PeerToPeer", AtsArtifactTypes.WorkDefinition);
   public static ArtifactToken WorkDef_Task_Default =
      TokenFactory.createArtifactToken(105373, "WorkDef_Task_Default", AtsArtifactTypes.WorkDefinition);
   public static ArtifactToken WorkDef_Team_Default =
      TokenFactory.createArtifactToken(72301, "WorkDef_Team_Default", AtsArtifactTypes.WorkDefinition);
   public static ArtifactToken WorkDef_Team_Simple =
      TokenFactory.createArtifactToken(72302, "WorkDef_Team_Simple", AtsArtifactTypes.WorkDefinition);

   // Safety Workflow configuration
   public static final ArtifactToken SafetyActionableItem =
      TokenFactory.createArtifactToken(7244546, "System Safety", AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken SafetyTeamDefinition =
      TokenFactory.createArtifactToken(7241589, "System Safety Team", AtsArtifactTypes.TeamDefinition);

   // Agile
   public static ArtifactToken TopAgileFolder =
      TokenFactory.createArtifactToken(6915493, "Agile", CoreArtifactTypes.Folder);

   private AtsArtifactToken() {
      // Constants
   }

}
