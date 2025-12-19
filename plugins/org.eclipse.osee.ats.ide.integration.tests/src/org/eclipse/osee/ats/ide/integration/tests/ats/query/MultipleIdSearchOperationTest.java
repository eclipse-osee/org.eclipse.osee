/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.query;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.ide.integration.tests.ats.util.AbstractAtsTest;
import org.eclipse.osee.ats.ide.world.search.MultipleIdSearchData;
import org.eclipse.osee.ats.ide.world.search.MultipleIdSearchOperation;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.junit.Test;

/**
 * Test case for {@link MultipleIdSearchOperation}
 *
 * @author Donald G. Dunne
 */
public class MultipleIdSearchOperationTest extends AbstractAtsTest {

   @Test
   public void testMyWorldSearchItem() {

      // By ats id
      MultipleIdSearchData data = new MultipleIdSearchData(getClass().getSimpleName(), null);
      data.setEnteredIds("TW5, TW6, TW7");
      data.setBranch(atsApi.getAtsBranch());
      MultipleIdSearchOperation op = new MultipleIdSearchOperation(data);
      op.setPerformUi(false);
      op.doSubWork(op, null, 0);
      Set<Artifact> resultAtsArts = op.getResultAtsArts();
      org.junit.Assert.assertEquals(3, resultAtsArts.size());

      // By artifact id
      List<Long> ids =
         Arrays.asList(AtsArtifactToken.TopTeamDefinition.getId(), AtsArtifactToken.TopActionableItem.getId());
      data = new MultipleIdSearchData(getClass().getSimpleName(), null);
      data.setEnteredIds(Collections.toString(",", ids));
      data.setBranch(atsApi.getAtsBranch());
      data.setIncludeArtIds(true);
      op = new MultipleIdSearchOperation(data);
      op.setPerformUi(false);
      op.doSubWork(op, null, 0);
      Set<Artifact> resultArts = op.getResultNonAtsArts();
      org.junit.Assert.assertEquals(2, resultArts.size());

      // Mixed ats ids and art id
      List<String> bothIds = Arrays.asList("TW5, TW6", ids.iterator().next().toString());
      data = new MultipleIdSearchData(getClass().getSimpleName(), null);
      data.setEnteredIds(Collections.toString(",", bothIds));
      data.setBranch(atsApi.getAtsBranch());
      data.setIncludeArtIds(true);
      op = new MultipleIdSearchOperation(data);
      op.setPerformUi(false);
      op.doSubWork(op, null, 0);
      resultArts = op.getResultNonAtsArts();
      resultAtsArts = op.getResultAtsArts();

      org.junit.Assert.assertEquals(1, resultArts.size());
      org.junit.Assert.assertEquals(2, resultAtsArts.size());

   }

}
