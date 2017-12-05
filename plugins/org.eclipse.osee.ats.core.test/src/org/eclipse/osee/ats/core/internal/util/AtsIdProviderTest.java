/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.util;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link AtsIdProvider}
 *
 * @author Donald G. Dunne
 */
public class AtsIdProviderTest {

   // @formatter:off
   @Mock private ISequenceProvider sequenceProvider;
   @Mock private IAttributeResolver attrResolver;
   @Mock private IAtsObject newObject;
   @Mock private IAtsTeamDefinition teamDef;
   @Mock private IAtsTeamDefinition parentTeamDef;
   @Mock private IAtsChangeSet changes;
   // @formatter:on

   AtsIdProvider atsIdProvider = null;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      atsIdProvider = new AtsIdProvider(sequenceProvider, attrResolver, newObject, teamDef);

      when(sequenceProvider.getNext("ATS_SEQ")).thenReturn(345L);
   }

   @Test
   public void testGetNextId() {
      Assert.assertEquals("ATS345", atsIdProvider.getNextId("ATS", "ATS_SEQ"));
   }

   @Test
   public void testGetAttrValue() {
      when(
         attrResolver.getSoleAttributeValueAsString(teamDef, AtsAttributeTypes.AtsIdPrefix, (String) null)).thenReturn(
            null);
      when(teamDef.getTeamDefinitionHoldingVersions()).thenReturn(null);

      Assert.assertNull(atsIdProvider.getAttrValueFromTeamDef(AtsAttributeTypes.AtsIdPrefix));

      when(teamDef.getTeamDefinitionHoldingVersions()).thenReturn(parentTeamDef);
      when(attrResolver.getSoleAttributeValueAsString(parentTeamDef, AtsAttributeTypes.AtsIdPrefix,
         (String) null)).thenReturn("ATS");

      Assert.assertEquals("ATS", atsIdProvider.getAttrValueFromTeamDef(AtsAttributeTypes.AtsIdPrefix));

      when(
         attrResolver.getSoleAttributeValueAsString(teamDef, AtsAttributeTypes.AtsIdPrefix, (String) null)).thenReturn(
            "TEST");

      Assert.assertEquals("TEST", atsIdProvider.getAttrValueFromTeamDef(AtsAttributeTypes.AtsIdPrefix));
   }

   @Test
   public void testGetNextAtsId() {
      when(
         attrResolver.getSoleAttributeValueAsString(teamDef, AtsAttributeTypes.AtsIdPrefix, (String) null)).thenReturn(
            "ASDF");
      when(attrResolver.getSoleAttributeValueAsString(teamDef, AtsAttributeTypes.AtsIdSequenceName,
         (String) null)).thenReturn("ASDF_SEQ");
      when(sequenceProvider.getNext("ASDF_SEQ")).thenReturn(333L);

      Assert.assertEquals("ASDF333", atsIdProvider.getNextAtsId());
   }

   @Test
   public void testGetNextAtsIdWithPrefixAndSequenceName() {
      when(
         attrResolver.getSoleAttributeValueAsString(teamDef, AtsAttributeTypes.AtsIdPrefix, (String) null)).thenReturn(
            "");
      when(attrResolver.getSoleAttributeValueAsString(teamDef, AtsAttributeTypes.AtsIdSequenceName,
         (String) null)).thenReturn("");

      when(sequenceProvider.getNext(AtsIdProvider.DEFAULT_WORKFLOW_SEQ_NAME)).thenReturn(333L);
      when(newObject.isTypeEqual(AtsArtifactTypes.TeamWorkflow)).thenReturn(true);
      Assert.assertEquals(AtsIdProvider.DEFAULT_WORKFLOW_ID_PREFIX + "333", atsIdProvider.getNextAtsId());

      when(sequenceProvider.getNext(AtsIdProvider.DEFAULT_ACTION_SEQ_NAME)).thenReturn(333L);
      when(newObject.isTypeEqual(AtsArtifactTypes.TeamWorkflow)).thenReturn(false);
      when(newObject.isTypeEqual(AtsArtifactTypes.Action)).thenReturn(true);
      Assert.assertEquals(AtsIdProvider.DEFAULT_ACTION_ID_PREFIX + "333", atsIdProvider.getNextAtsId());

      when(sequenceProvider.getNext(AtsIdProvider.DEFAULT_REVIEW_SEQ_NAME)).thenReturn(333L);
      when(newObject.isTypeEqual(AtsArtifactTypes.TeamWorkflow)).thenReturn(false);
      when(newObject.isTypeEqual(AtsArtifactTypes.Action)).thenReturn(false);
      when(newObject.isTypeEqual(AtsArtifactTypes.ReviewArtifact)).thenReturn(true);
      Assert.assertEquals(AtsIdProvider.DEFAULT_REVIEW_ID_PREFIX + "333", atsIdProvider.getNextAtsId());

      when(sequenceProvider.getNext(AtsIdProvider.DEFAULT_TASK_SEQ_NAME)).thenReturn(333L);
      when(newObject.isTypeEqual(AtsArtifactTypes.TeamWorkflow)).thenReturn(false);
      when(newObject.isTypeEqual(AtsArtifactTypes.Action)).thenReturn(false);
      when(newObject.isTypeEqual(AtsArtifactTypes.ReviewArtifact)).thenReturn(false);
      when(newObject.isTypeEqual(AtsArtifactTypes.Task)).thenReturn(true);
      Assert.assertEquals(AtsIdProvider.DEFAULT_TASK_ID_PREFIX + "333", atsIdProvider.getNextAtsId());
   }

   @Test
   public void testSetAtsId() {
      when(attrResolver.getSoleAttributeValueAsString(newObject, AtsAttributeTypes.AtsId, (String) null)).thenReturn(
         null);
      when(
         attrResolver.getSoleAttributeValueAsString(teamDef, AtsAttributeTypes.AtsIdPrefix, (String) null)).thenReturn(
            "ASDF");
      when(attrResolver.getSoleAttributeValueAsString(teamDef, AtsAttributeTypes.AtsIdSequenceName,
         (String) null)).thenReturn("ASDF_SEQ");
      when(sequenceProvider.getNext("ASDF_SEQ")).thenReturn(333L);
      when(teamDef.getTeamDefinitionHoldingVersions()).thenReturn(parentTeamDef);

      atsIdProvider.setAtsId(changes);

      verify(attrResolver).setSoleAttributeValue(newObject, AtsAttributeTypes.AtsId, "ASDF333", changes);

   }

   @Test
   public void testNotSetAtsId() {
      when(attrResolver.getSoleAttributeValueAsString(newObject, AtsAttributeTypes.AtsId, (String) null)).thenReturn(
         "QQQQ444");
      when(
         attrResolver.getSoleAttributeValueAsString(teamDef, AtsAttributeTypes.AtsIdPrefix, (String) null)).thenReturn(
            "ASDF");
      when(attrResolver.getSoleAttributeValueAsString(teamDef, AtsAttributeTypes.AtsIdSequenceName,
         (String) null)).thenReturn("ASDF_SEQ");
      when(sequenceProvider.getNext("ASDF_SEQ")).thenReturn(333L);
      when(teamDef.getTeamDefinitionHoldingVersions()).thenReturn(parentTeamDef);

      atsIdProvider.setAtsId(changes);

      verify(attrResolver, never()).setSoleAttributeValue(any(IAtsObject.class), any(AttributeTypeId.class),
         any(String.class), any(IAtsChangeSet.class));
   }
}
