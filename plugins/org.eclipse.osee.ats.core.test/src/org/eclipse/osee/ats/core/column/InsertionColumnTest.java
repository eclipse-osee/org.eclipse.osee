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

package org.eclipse.osee.ats.core.column;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
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
public class InsertionColumnTest {

   // @formatter:off
   @Mock private WorkPackageUtility util;
   @Mock private AtsApi atsApi;
   @Mock private IAtsWorkItem workItem;
   @Mock private IAtsObject object;
   @Mock private IAtsInsertion program;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void test() {
      String str = InsertionColumn.getInsertionStr(object, atsApi, util);
      assertEquals(str, "");

      when(util.getInsertion(atsApi, workItem)).thenReturn(new Pair<IAtsInsertion, Boolean>(null, false));

      str = InsertionColumn.getInsertionStr(workItem, atsApi, util);
      assertEquals(str, "");

      when(util.getInsertion(atsApi, workItem)).thenReturn(new Pair<>(program, false));
      when(program.getName()).thenReturn("Country");
      str = InsertionColumn.getInsertionStr(workItem, atsApi, util);
      assertEquals(str, "Country");

      when(util.getInsertion(atsApi, workItem)).thenReturn(new Pair<>(program, true));
      str = InsertionColumn.getInsertionStr(workItem, atsApi, util);
      assertEquals(str, "Country (I)");

   }
}
