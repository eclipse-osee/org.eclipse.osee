/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.demo.api;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Donald G. Dunne
 */
public final class DemoArtifactToken {

   public static final ArtifactToken SAW_Test_AI =
      TokenFactory.createArtifactToken(75881049, "SAW Test", AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken SAW_Code_AI =
      TokenFactory.createArtifactToken(733306468, "SAW Code", AtsArtifactTypes.ActionableItem);

   public static ArtifactToken Process_Team =
      TokenFactory.createArtifactToken(55170736, "Process_Team", AtsArtifactTypes.TeamDefinition);
   public static ArtifactToken Tools_Team =
      TokenFactory.createArtifactToken(4830548, "Tools_Team", AtsArtifactTypes.TeamDefinition);

   public static final ArtifactToken SAW_Program =
      TokenFactory.createArtifactToken(19196003, "SAW Program", AtsArtifactTypes.Program);
   public static ArtifactToken SAW_HW =
      TokenFactory.createArtifactToken(2876840, "SAW HW", AtsArtifactTypes.TeamDefinition);
   public static ArtifactToken SAW_Code =
      TokenFactory.createArtifactToken(30013695, "SAW Code", AtsArtifactTypes.TeamDefinition);
   public static ArtifactToken SAW_Test =
      TokenFactory.createArtifactToken(31608252, "SAW Test", AtsArtifactTypes.TeamDefinition);
   public static ArtifactToken SAW_SW_Design =
      TokenFactory.createArtifactToken(138220, "SAW SW Design", AtsArtifactTypes.TeamDefinition);
   public static ArtifactToken SAW_Requirements =
      TokenFactory.createArtifactToken(20592, "SAW Requirements", AtsArtifactTypes.TeamDefinition);
   public static ArtifactToken SAW_SW =
      TokenFactory.createArtifactToken(3902389, "SAW SW", AtsArtifactTypes.TeamDefinition);
   public static ArtifactToken SAW_CSCI_AI =
      TokenFactory.createArtifactToken(1866, "SAW CSCI", AtsArtifactTypes.ActionableItem);

   // SAW_SW Versions
   public static ArtifactToken SAW_Bld_1 =
      TokenFactory.createArtifactToken(2749182, "SAW_Bld_1", AtsArtifactTypes.Version);
   public static ArtifactToken SAW_Bld_2 =
      TokenFactory.createArtifactToken(7632957, "SAW_Bld_2", AtsArtifactTypes.Version);
   public static ArtifactToken SAW_Bld_3 =
      TokenFactory.createArtifactToken(577781, "SAW_Bld_3", AtsArtifactTypes.Version);

   public static final ArtifactToken CIS_Program =
      TokenFactory.createArtifactToken(8242414, "CIS Program", AtsArtifactTypes.Program);

   public static ArtifactToken CIS_SW =
      TokenFactory.createArtifactToken(695910, "CIS_SW", AtsArtifactTypes.TeamDefinition);
   public static ArtifactToken CIS_Code =
      TokenFactory.createArtifactToken(1629262, "CIS_Code", AtsArtifactTypes.TeamDefinition);
   public static ArtifactToken CIS_Test =
      TokenFactory.createArtifactToken(541255, "CIS_Test", AtsArtifactTypes.TeamDefinition);

   public static ArtifactToken Facilities_Team =
      TokenFactory.createArtifactToken(4811031, "Facilities_Team", CoreArtifactTypes.Folder);

   public static ArtifactToken DemoPrograms =
      TokenFactory.createArtifactToken(90120, "Demo Programs", CoreArtifactTypes.Artifact);

   public static ArtifactToken SAW_Code_Team_WorkPackage_01 =
      TokenFactory.createArtifactToken(38512616, "AZp8M1dPuESWYBPPbDgA", "Work Pkg 01", AtsArtifactTypes.WorkPackage);
   public static ArtifactToken SAW_Code_Team_WorkPackage_02 =
      TokenFactory.createArtifactToken(513994, "AZp8M1d7TCJiBw6A5bgA", "Work Pkg 02", AtsArtifactTypes.WorkPackage);
   public static ArtifactToken SAW_Code_Team_WorkPackage_03 =
      TokenFactory.createArtifactToken(304908, "AZp8M1em4EC1xE6bPEwA", "Work Pkg 03", AtsArtifactTypes.WorkPackage);

   public static ArtifactToken SAW_Test_AI_WorkPackage_0A =
      TokenFactory.createArtifactToken(75666, "AZp8M1fSc1JwMDQBtLwA", "Work Pkg 0A", AtsArtifactTypes.WorkPackage);
   public static ArtifactToken SAW_Test_AI_WorkPackage_0B =
      TokenFactory.createArtifactToken(281326, "AZp8M1hP81QOm6W9yNgA", "Work Pkg 0B", AtsArtifactTypes.WorkPackage);
   public static ArtifactToken SAW_Test_AI_WorkPackage_0C =
      TokenFactory.createArtifactToken(8141323, "AZp8M1kvEGrRt9tybTwA", "Work Pkg 0C", AtsArtifactTypes.WorkPackage);

   private DemoArtifactToken() {
      // Constants
   }
}
