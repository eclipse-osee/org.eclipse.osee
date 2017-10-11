/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.workdef.IAtsRuleDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionDslService;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionStringProvider;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.NullRuleDefinition;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.config.TeamDefinition;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.logger.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link AtsWorkDefinitionServiceImpl} for Rules
 *
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionServiceImplRulesTest {

   // @formatter:off
   @Mock AtsWorkDefinitionStoreService workDefinitionStore;
   @Mock IAtsWorkDefinitionDslService workDefinitionDslService;
   @Mock AtsApi atsApi;
   @Mock IAtsWorkDefinitionStringProvider workDefinitionStringProvider;
   @Mock IAtsRuleDefinition ruleDef1;
   @Mock IAtsRuleDefinition ruleDef2;
   @Mock IAtsTeamWorkflow teamWf;
   @Mock ArtifactToken teamWfArt;
   @Mock ArtifactToken teamDefArt;
   @Mock IAtsAbstractReview review;
   @Mock IAttributeResolver attrResolver;
   @Mock Log logger;

   AtsWorkDefinitionServiceImpl workDefService;

   // @formatter:on

   @Before
   public void setup() throws Exception {
      MockitoAnnotations.initMocks(this);

      when(workDefinitionStore.loadRuleDefinitionString()).thenReturn("");
      when(workDefinitionDslService.getRuleDefinitions("")).thenReturn(new ArrayList<IAtsRuleDefinition>());

      workDefService = new AtsWorkDefinitionServiceImpl(atsApi, workDefinitionStore, workDefinitionStringProvider,
         workDefinitionDslService, null);

      when(workDefinitionDslService.getRuleDefinitions("")).thenReturn(Arrays.asList(ruleDef1, ruleDef2));
      when(ruleDef1.getName()).thenReturn("ruleDef1");
      when(ruleDef2.getName()).thenReturn("ruleDef2");
   }

   @Test
   public void getRuleDefinitionkByName() {

      IAtsRuleDefinition ruleDefinition = workDefService.getRuleDefinition("asdf");
      Assert.assertTrue(ruleDefinition instanceof NullRuleDefinition);

      IAtsRuleDefinition ruleDefinition2 = workDefService.getRuleDefinition("ruleDef1");
      Assert.assertNotNull(ruleDefinition2);

      ruleDefinition = workDefService.getRuleDefinition("asdf");
      Assert.assertTrue(ruleDefinition instanceof NullRuleDefinition);
   }

   @Test
   public void getAllRuleDefinitions() {
      Collection<IAtsRuleDefinition> ruleDefs = workDefService.getAllRuleDefinitions();
      Assert.assertEquals(2, ruleDefs.size());
   }

   @Test
   public void teamDefHasRule() {
      when(review.getParentTeamWorkflow()).thenReturn(teamWf);
      TeamDefinition teamDef = new TeamDefinition(logger, atsApi, teamDefArt);
      when(teamWf.getTeamDefinition()).thenReturn(teamDef);
      when(atsApi.getAttributeResolver()).thenReturn(attrResolver);
      when(attrResolver.getAttributesToStringList(teamDef, AtsAttributeTypes.RuleDefinition)).thenReturn(
         Arrays.asList(RuleDefinitionOption.AllowPrivilegedEditToTeamMember.name(),
            RuleDefinitionOption.AllowPrivilegedEditToTeamMemberAndOriginator.name()));

      boolean teamDefHasRule = workDefService.teamDefHasRule(teamWf, RuleDefinitionOption.AllowEditToAll);
      Assert.assertFalse(teamDefHasRule);

      teamDefHasRule = workDefService.teamDefHasRule(teamWf, RuleDefinitionOption.AllowPrivilegedEditToTeamMember);
      Assert.assertTrue(teamDefHasRule);

      teamDefHasRule = workDefService.teamDefHasRule(review, RuleDefinitionOption.AllowPrivilegedEditToTeamMember);
      Assert.assertTrue(teamDefHasRule);
   }

}
