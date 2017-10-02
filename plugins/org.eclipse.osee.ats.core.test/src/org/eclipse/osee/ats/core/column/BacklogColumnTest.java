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
package org.eclipse.osee.ats.core.column;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link BacklogColumn}
 *
 * @author Donald G. Dunne
 */
public class BacklogColumnTest extends ProgramColumn {

   // @formatter:off
   @Mock private IAtsWorkItem workItem;
   @Mock private IAtsWorkItem goal, backlog;
   @Mock private IAtsObject object;
   @Mock private IRelationResolver relResolver;
   @Mock private IAtsServices services;
   @Mock private IAtsStoreService storeService;
   @Mock private ArtifactToken artId;
   // @formatter:on

   @Before
   public void setup()  {
      MockitoAnnotations.initMocks(this);

      when(services.getRelationResolver()).thenReturn(relResolver);
   }

   @Test
   public void test() {
      String str = BacklogColumn.getColumnText(object, services, true);
      assertEquals(str, "");

      when(relResolver.getRelated(workItem, AtsRelationTypes.Goal_Goal, IAtsWorkItem.class)).thenReturn(
         Arrays.asList());
      str = BacklogColumn.getColumnText(workItem, services, true);
      assertEquals(str, "");

      when(relResolver.getRelated(workItem, AtsRelationTypes.Goal_Goal, IAtsWorkItem.class)).thenReturn(
         Arrays.asList(goal));
      when(relResolver.getRelatedCount(goal, AtsRelationTypes.AgileTeamToBacklog_AgileTeam)).thenReturn(0);
      when(goal.getName()).thenReturn("My Goal");
      when(services.getStoreService()).thenReturn(storeService);
      when(goal.getStoreObject()).thenReturn(artId);
      when(storeService.isOfType(artId, AtsArtifactTypes.AgileBacklog)).thenReturn(false);
      str = BacklogColumn.getColumnText(workItem, services, false);
      assertEquals("My Goal", str);

      when(relResolver.getRelated(workItem, AtsRelationTypes.Goal_Goal, IAtsWorkItem.class)).thenReturn(
         Arrays.asList(backlog));
      when(relResolver.getRelatedCount(backlog, AtsRelationTypes.AgileTeamToBacklog_AgileTeam)).thenReturn(1);
      when(backlog.getName()).thenReturn("My Backlog");
      str = BacklogColumn.getColumnText(workItem, services, true);
      assertEquals("My Backlog", str);

      when(relResolver.getRelated(workItem, AtsRelationTypes.Goal_Goal, IAtsWorkItem.class)).thenReturn(
         Arrays.asList(backlog, goal));
      when(relResolver.getRelatedCount(backlog, AtsRelationTypes.AgileTeamToBacklog_AgileTeam)).thenReturn(1);
      str = BacklogColumn.getColumnText(workItem, services, true);
      assertEquals("My Backlog", str);
      str = BacklogColumn.getColumnText(workItem, services, false);
      assertEquals("My Goal", str);
   }
}
