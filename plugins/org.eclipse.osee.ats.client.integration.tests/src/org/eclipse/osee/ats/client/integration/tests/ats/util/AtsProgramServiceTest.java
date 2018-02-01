/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.util;

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
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.program.ProjectType;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
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
      programService = AtsClientService.get().getProgramService();
      sawProgram = getSawProgram();
   }

   @Test
   public void testGetProgram() {
      Collection<IAtsProgram> programs = programService.getPrograms();
      assertEquals(5, programs.size());

      IAtsProgram program = programService.getProgram(programs.iterator().next().getId());
      assertEquals(program.getId(), program.getId());

      Collection<IAtsProgram> programs2 = programService.getPrograms(AtsArtifactTypes.Program);
      assertEquals(5, programs2.size());

      IAtsCountry usgCountry = programService.getCountry(sawProgram);
      List<IAtsProgram> programs3 = programService.getPrograms(usgCountry);
      assertEquals(2, programs3.size());

      Collection<IAtsInsertion> insertions = programService.getInsertions(program);
      IAtsProgram program4 = programService.getProgram(insertions.iterator().next());
      assertEquals(program.getId(), program4.getId());

      program4 = programService.getProgram(program.getStoreObject().getId());
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
      assertEquals(sawProgram.getId(), program.getId());

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

      assertEquals(commInsertion.getId(), insertion.getId());

      Long id = activities.iterator().next().getId();
      IAtsInsertionActivity activity2 = programService.getInsertionActivity(id);
      assertEquals(activity2.getId(), id);
   }

   @Test
   public void testGetWorkPackage() {
      IAtsWorkPackage workPackage =
         (IAtsWorkPackage) AtsClientService.get().getQueryService().createQuery(AtsArtifactTypes.WorkPackage).andAttr(
            CoreAttributeTypes.Name, "Work Pkg 0B").getConfigObjectResultSet().getAtMostOneOrNull();

      assertEquals("COMM Page", programService.getInsertionActivity(workPackage).getName());
   }

   @Test
   public void testGetTeamDefHoldingVersions() {
      Assert.assertEquals("SAW SW", programService.getTeamDefHoldingVersions(sawProgram).getName());
   }

   @Test
   public void testGetTeamDefinition() {
      assertEquals("SAW SW", programService.getTeamDefinition(sawProgram).getName());

      assertEquals(5, programService.getTeamDefs(sawProgram).size());

      assertEquals(1, programService.getTeamDefs(sawProgram, WorkType.Code).size());

      assertEquals(2, programService.getTeamDefs(sawProgram, Arrays.asList(WorkType.Code, WorkType.Test)).size());
   }

   @Test
   public void testGetAIs() {
      assertEquals(2, programService.getAis(sawProgram).size());

      assertEquals(1, programService.getAis(sawProgram, WorkType.Code).size());

      assertEquals(2, programService.getAis(sawProgram, Arrays.asList(WorkType.Code, WorkType.Test)).size());
   }

   @Test
   public void testGetWorkType() {
      Collection<IAtsTeamWorkflow> workflows = programService.getWorkflows(sawProgram, WorkType.Code);
      assertEquals(3, workflows.size());
      IAtsTeamWorkflow codeTeamWf = workflows.iterator().next();

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

      assertEquals(DemoBranches.SAW_Bld_1.getName(),
         programService.getVersion(sawProgram, DemoBranches.SAW_Bld_1.getName()).getName());

      assertTrue(programService.isActive(sawProgram));

      assertEquals(3, programService.getCscis(sawProgram).size());

      assertTrue(programService.getDescription(sawProgram).contains("SAW"));

      Assert.assertEquals("org.demo.saw", programService.getNamespace(sawProgram));

   }

   private IAtsProgram getSawProgram() {
      if (sawProgram == null) {
         for (IAtsProgram prog : programService.getPrograms()) {
            if (prog.getName().contains("SAW")) {
               sawProgram = prog;
               break;
            }
         }
      }
      return sawProgram;
   }

}
