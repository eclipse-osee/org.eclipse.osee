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
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.core.config.WorkPackageUtility;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link InsertionColumn}
 *
 * @author Donald G. Dunne
 */
public class InsertionActivityColumnTest {

// @formatter:off
   @Mock private WorkPackageUtility util;
   @Mock private IAtsServices services;
   @Mock private IAtsWorkItem workItem;
   @Mock private IAtsObject object;
   @Mock private IAtsInsertionActivity program;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void test() {
      String str = InsertionActivityColumn.getInsertionActivityStr(object, services, util);
      assertEquals(str, "");

      when(util.getInsertionActivity(services, workItem)).thenReturn(
         new Pair<IAtsInsertionActivity, Boolean>(null, false));

      str = InsertionActivityColumn.getInsertionActivityStr(workItem, services, util);
      assertEquals(str, "");

      when(util.getInsertionActivity(services, workItem)).thenReturn(
         new Pair<IAtsInsertionActivity, Boolean>(program, false));
      when(program.getName()).thenReturn("Country");
      str = InsertionActivityColumn.getInsertionActivityStr(workItem, services, util);
      assertEquals(str, "Country");

      when(util.getInsertionActivity(services, workItem)).thenReturn(
         new Pair<IAtsInsertionActivity, Boolean>(program, true));
      str = InsertionActivityColumn.getInsertionActivityStr(workItem, services, util);
      assertEquals(str, "Country (I)");

   }
}
