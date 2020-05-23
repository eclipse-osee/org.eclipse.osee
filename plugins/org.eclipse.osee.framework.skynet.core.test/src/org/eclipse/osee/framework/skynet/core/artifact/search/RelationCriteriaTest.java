/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.skynet.core.artifact.search;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.orcs.rest.client.QueryBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * @author John Misinco
 */
public class RelationCriteriaTest {

   @Test
   public void testAddRelationTypeSideToQueryBuilder() {
      RelationCriteria criteria = new RelationCriteria(CoreRelationTypes.Allocation_Component);
      QueryBuilder builder = mock(QueryBuilder.class);
      criteria.addToQueryBuilder(builder);
      verify(builder).andExists(CoreRelationTypes.Allocation_Component);

      reset(builder);
      ArtifactId artifact = ArtifactId.valueOf(4);
      criteria = new RelationCriteria(artifact, CoreRelationTypes.Allocation_Component, RelationSide.SIDE_A);
      criteria.addToQueryBuilder(builder);
      ArgumentCaptor<RelationTypeSide> rtsCaptor = ArgumentCaptor.forClass(RelationTypeSide.class);
      verify(builder).andRelatedTo(rtsCaptor.capture(), eq(artifact));
      Assert.assertEquals(CoreRelationTypes.Allocation_Component.getGuid(), rtsCaptor.getValue().getGuid());
      Assert.assertEquals(RelationSide.SIDE_A, rtsCaptor.getValue().getSide());
   }

   @Test
   public void testAddRelationTypeToQueryBuilder() {
      RelationCriteria criteria = new RelationCriteria((RelationTypeToken) CoreRelationTypes.Allocation_Component);
      QueryBuilder builder = mock(QueryBuilder.class);
      criteria.addToQueryBuilder(builder);
      verify(builder).andExists((RelationTypeToken) CoreRelationTypes.Allocation_Component);

      reset(builder);
      ArtifactId artifact = ArtifactId.valueOf(4);
      criteria = new RelationCriteria(artifact, CoreRelationTypes.Allocation_Component, RelationSide.SIDE_A);
      criteria.addToQueryBuilder(builder);
      ArgumentCaptor<RelationTypeSide> rtsCaptor = ArgumentCaptor.forClass(RelationTypeSide.class);
      verify(builder).andRelatedTo(rtsCaptor.capture(), eq(artifact));
      Assert.assertEquals(CoreRelationTypes.Allocation_Component.getGuid(), rtsCaptor.getValue().getGuid());
      Assert.assertEquals(RelationSide.SIDE_A, rtsCaptor.getValue().getSide());
   }
}
