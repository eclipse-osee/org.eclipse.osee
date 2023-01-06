/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.country.IAtsCountry;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.program.ProgramVersions;
import org.eclipse.osee.ats.api.program.ProjectType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for AtsProgramService
 *
 * @author Donald G. Dunne
 */
public class AtsProgramServiceTest {

   private IAtsProgramService programService;
   private static IAtsProgram sawProgram;

   @Before
   public void setup() {
      programService = AtsApiService.get().getProgramService();
      sawProgram = getSawProgram();
   }

   @Test
   public void testGetProgram() {
      Collection<IAtsProgram> programs = programService.getPrograms();
      assertEquals(6, programs.size());

      Collection<IAtsProgram> programs2 = programService.getPrograms(AtsArtifactTypes.Program);
      assertEquals(6, programs2.size());

      IAtsCountry country = programService.getCountry(sawProgram);

      if (country != null) {
         List<IAtsProgram> programs3 = programService.getPrograms(country);
         assertEquals(2, programs3.size());
      }

      IAtsProgram program = programService.getProgramById(DemoArtifactToken.SAW_Program);
      Collection<IAtsInsertion> insertions = programService.getInsertions(program);
      IAtsProgram program4 = programService.getProgram(insertions.iterator().next());
      assertEquals(program.getId(), program4.getId());

      program4 = programService.getProgramById(program.getStoreObject());
      assertEquals(program.getId(), program4.getId());
   }

   @Test
   public void testGetCountry() {
      IAtsCountry country = programService.getCountry(sawProgram);
      assertNotNull(country);

      assertEquals(2, programService.getPrograms(country).size());
   }

   @Test
   public void testGetInsertions() {
      Collection<IAtsInsertion> insertions = programService.getInsertions(sawProgram);
      assertEquals(4, insertions.size());

      IAtsProgram program = programService.getProgram(insertions.iterator().next());
      if (program != null) {
         assertEquals(sawProgram.getId(), program.getId());
      }

      Long id = insertions.iterator().next().getId();
      IAtsInsertion insertion2 = programService.getInsertion(id);
      assertEquals(insertion2.getId(), id);
   }

   @Test
   public void testGetInsertionActivity() {
      IAtsInsertion commInsertion = null;
      for (IAtsInsertion insertion : programService.getInsertions(sawProgram)) {
         if (insertion.getName().equals("COMM")) {
            commInsertion = insertion;
         }
      }

      Assert.assertNotNull(commInsertion);
      Collection<IAtsInsertionActivity> activities = programService.getInsertionActivities(commInsertion);
      assertEquals(2, activities.size());

      IAtsInsertion insertion = programService.getInsertion(activities.iterator().next());

      if (insertion != null) {
         assertEquals(commInsertion.getId(), insertion.getId());
      }

      Long id = activities.iterator().next().getId();
      IAtsInsertionActivity activity2 = programService.getInsertionActivity(id);
      assertEquals(activity2.getId(), id);
   }

   @Test
   public void testGetWorkPackage() {
      IAtsWorkPackage workPackage =
         (IAtsWorkPackage) AtsApiService.get().getQueryService().createQuery(AtsArtifactTypes.WorkPackage).andAttr(
            CoreAttributeTypes.Name, "Work Pkg 0B").getConfigObjectResultSet().getExactlyOne();

      if (programService.getInsertionActivity(workPackage) != null) {
         assertEquals("COMM Page", programService.getInsertionActivity(workPackage).getName());
      }
   }

   @Test
   public void testGetTeamDefHoldingVersions() {
      if (programService.getTeamDefHoldingVersions(sawProgram) != null) {
         Assert.assertEquals("SAW SW", programService.getTeamDefHoldingVersions(sawProgram).getName());
      }
   }

   @Test
   public void testGetTeamDefinition() {
      if (programService.getTeamDefinition(sawProgram) != null) {
         assertEquals("SAW SW", programService.getTeamDefinition(sawProgram).getName());
      }

      assertEquals(8, programService.getTeamDefs(sawProgram).size());

      assertEquals(1, programService.getTeamDefs(sawProgram, WorkType.Code).size());

      assertEquals(2, programService.getTeamDefs(sawProgram, Arrays.asList(WorkType.Code, WorkType.Test)).size());
   }

   @Test
   public void testGetAIs() {
      assertEquals(8, programService.getAis(sawProgram).size());

      assertEquals(1, programService.getAis(sawProgram, WorkType.Code).size());

      assertEquals(2, programService.getAis(sawProgram, Arrays.asList(WorkType.Code, WorkType.Test)).size());
   }

   @Test
   public void testGetWorkType() {
      Collection<IAtsTeamWorkflow> workflows = programService.getWorkflows(sawProgram, WorkType.Code);
      assertEquals(6, workflows.size());
      IAtsTeamWorkflow codeTeamWf = (IAtsTeamWorkflow) workflows.toArray()[1];

      assertEquals(WorkType.Code, programService.getWorkType(codeTeamWf));

      Collection<IAtsTeamWorkflow> workflows2 =
         programService.getWorkflows(sawProgram, Collections.singleton(WorkType.Test), codeTeamWf);
      assertEquals(1, workflows2.size());
      IAtsTeamWorkflow testTeamWf = workflows2.iterator().next();

      assertEquals(WorkType.Test, programService.getWorkType(testTeamWf));
   }

   @Test
   public void testGetProgramInfo() {
      assertEquals(ProjectType.MultiProcessor, programService.getProjectType(sawProgram));

      assertEquals(3, programService.getVersions(sawProgram).size());

      if (programService.getVersion(sawProgram, DemoBranches.SAW_Bld_1.getName()) != null) {
         assertEquals(DemoBranches.SAW_Bld_1.getName(),
            programService.getVersion(sawProgram, DemoBranches.SAW_Bld_1.getName()).getName());
      }

      assertTrue(programService.isActive(sawProgram));

      assertEquals(3, programService.getCscis(sawProgram).size());

      assertTrue(programService.getDescription(sawProgram).contains("SAW"));

      Assert.assertEquals("org.demo.saw", programService.getNamespace(sawProgram));

   }

   private IAtsProgram getSawProgram() {
      if (sawProgram == null) {
         sawProgram = programService.getProgramById(DemoArtifactToken.SAW_Program);
         ArtifactToken art = sawProgram.getArtifactToken();
         AtsApiService.get().getStoreService().reloadArts(Arrays.asList(art));
      }
      return sawProgram;
   }

   @Test
   public void testGetProgramVersions() {
      List<ProgramVersions> progVers = programService.getProgramVersions(AtsArtifactTypes.Program, false);
      Assert.assertNotNull(progVers);

      Assert.assertTrue("Should be at least 5 programs", progVers.size() >= 5);

      ProgramVersions sawProgVer = null;
      for (ProgramVersions program : progVers) {
         if (program.getProgram().equals(DemoArtifactToken.SAW_Program)) {
            sawProgVer = program;
            break;
         }
      }
      Assert.assertNotNull(sawProgVer);
      Assert.assertEquals(sawProgVer.getTeam(), DemoArtifactToken.SAW_SW);
      Assert.assertTrue("Should at least 3 versions", sawProgVer.getVersions().size() >= 3);
   }

}
