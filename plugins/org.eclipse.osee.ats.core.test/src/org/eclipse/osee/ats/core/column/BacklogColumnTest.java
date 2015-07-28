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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
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
   // @formatter:on

   @Before
   public void setup() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void test() {
      String str = BacklogColumn.getColumnText(object, relResolver);
      assertEquals(str, "");

      when(relResolver.getRelated(workItem, AtsRelationTypes.Goal_Goal, IAtsWorkItem.class)).thenReturn(
         Arrays.asList());
      str = BacklogColumn.getColumnText(workItem, relResolver);
      assertEquals(str, "");

      when(relResolver.getRelated(workItem, AtsRelationTypes.Goal_Goal, IAtsWorkItem.class)).thenReturn(
         Arrays.asList(goal));
      when(relResolver.getRelatedCount(goal, AtsRelationTypes.AgileTeamToBacklog_AgileTeam)).thenReturn(0);
      when(goal.getName()).thenReturn("My Goal");
      str = BacklogColumn.getColumnText(workItem, relResolver);
      assertEquals("My Goal", str);

      when(relResolver.getRelated(workItem, AtsRelationTypes.Goal_Goal, IAtsWorkItem.class)).thenReturn(
         Arrays.asList(backlog));
      when(relResolver.getRelatedCount(backlog, AtsRelationTypes.AgileTeamToBacklog_AgileTeam)).thenReturn(1);
      when(backlog.getName()).thenReturn("My Backlog");
      str = BacklogColumn.getColumnText(workItem, relResolver);
      assertEquals("My Backlog (BL)", str);

      when(relResolver.getRelated(workItem, AtsRelationTypes.Goal_Goal, IAtsWorkItem.class)).thenReturn(
         Arrays.asList(backlog, goal));
      when(relResolver.getRelatedCount(backlog, AtsRelationTypes.AgileTeamToBacklog_AgileTeam)).thenReturn(1);
      str = BacklogColumn.getColumnText(workItem, relResolver);
      assertEquals("My Backlog (BL); My Goal", str);

   }
}
