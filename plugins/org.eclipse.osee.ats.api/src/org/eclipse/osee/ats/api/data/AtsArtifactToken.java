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

import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Donald G. Dunne
 */
public final class AtsArtifactToken {

   public static IArtifactToken HeadingFolder = TokenFactory.createArtifactToken("AAABER+3yR4A8O7WYQ+Xbw",
      "Action Tracking System", CoreArtifactTypes.Folder);
   public static IArtifactToken ConfigFolder = TokenFactory.createArtifactToken("AAABF4n18eYAc1ruQSSWdg", "Config",
      CoreArtifactTypes.Folder);
   public static IArtifactToken WorkDefinitionsFolder = TokenFactory.createArtifactToken("ADTfjCLEj2DH2WYyeOgA",
      "Work Definitions", CoreArtifactTypes.Folder);
   public static IArtifactToken WorkPagesFolder = TokenFactory.createArtifactToken("AAABGnncY_gAAo+3N69ASA",
      "Work Pages", CoreArtifactTypes.Folder);
   public static IArtifactToken WorkRulesFolder = TokenFactory.createArtifactToken("AAABGnmhCyYAoJoIciyaag",
      "Work Rules", CoreArtifactTypes.Folder);
   public static IArtifactToken WorkWidgetsFolder = TokenFactory.createArtifactToken("AAABGnmjk4IAoJoIa945Kg",
      "Work Widgets", CoreArtifactTypes.Folder);
   public static IArtifactToken WorkFlowsFolder = TokenFactory.createArtifactToken("AAABGnncZ_4AAo+3D0sGfw",
      "Work Flows", CoreArtifactTypes.Folder);
   public static IArtifactToken WebPrograms = TokenFactory.createArtifactToken("Awsk_RtncCochcuSxagA", "Web Programs",
      CoreArtifactTypes.UniversalGroup);
   public static IArtifactToken WorkDef_State_Names = TokenFactory.createArtifactToken("BFqfTrN8W3QmQSFAi6wA",
      "WorkDef_State_Names", CoreArtifactTypes.GeneralData);
   public static IArtifactToken AtsAdmin = TokenFactory.createArtifactToken("AAABHaItoVsAG6ZAAMyhQw", "AtsAdmin",
      CoreArtifactTypes.UserGroup);
   public static IArtifactToken AtsTempAdmin = TokenFactory.createArtifactToken("AAABHaItoVsAG7ZAAMyhQw",
      "AtsTempAdmin", CoreArtifactTypes.UserGroup);

   private AtsArtifactToken() {
      // Constants
   }

}
