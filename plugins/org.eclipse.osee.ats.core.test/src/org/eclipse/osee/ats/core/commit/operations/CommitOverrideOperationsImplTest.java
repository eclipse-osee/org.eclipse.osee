/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.commit.operations;

import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.commit.CommitOverride;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Unit for {@link CommitOverrideOperationsImpl}
 *
 * @author Donald G. Dunne
 */
public class CommitOverrideOperationsImplTest {

   // @formatter:off
   @Mock private IAtsTeamWorkflow teamWf;

   @Mock private AtsApi atsApi;
   @Mock private IAttributeResolver attributeResolver;
   @Mock private IRelationResolver relationResolver;
   @Mock private BranchId branch;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      when(teamWf.isTeamWorkflow()).thenReturn(true);
      when(attributeResolver.getAttributesToStringList(teamWf, AtsAttributeTypes.CommitOverride)).thenReturn(
         Arrays.asList(Commit_Override_1, Commit_Override_2));

      when(atsApi.getAttributeResolver()).thenReturn(attributeResolver);
      when(atsApi.getRelationResolver()).thenReturn(relationResolver);

      when(branch.getIdString()).thenReturn("345");
   }

   @Test
   public void testGetCommitOverrides() {
      CommitOverrideOperationsImpl ops = new CommitOverrideOperationsImpl(atsApi);
      Collection<CommitOverride> commitOverrides = ops.getCommitOverrides(teamWf);
      Assert.assertEquals(2, commitOverrides.size());
   }

   @Test
   public void testGetCommitOverrideAndUser() {
      CommitOverrideOperationsImpl ops = new CommitOverrideOperationsImpl(atsApi);
      CommitOverride commitOverride = ops.getCommitOverride(teamWf, branch);
      Assert.assertEquals(commitOverride.getBranchId(), branch.getIdString());
      Assert.assertEquals(DemoUsers.Joe_Smith.getIdString(), commitOverride.getUser().getIdString());

      commitOverride = ops.getCommitOverride(teamWf, BranchId.valueOf(222L));
      Assert.assertNull(commitOverride);
   }

   private static String Commit_Override_1 = "{\"user\":\"61106791\",\"branchId\":\"345\",\"reason\":\"asdfads\"}";
   private static String Commit_Override_2 = "{\"user\":\"61106791\",\"branchId\":\"645\",\"reason\":\"testing\"}";
}
