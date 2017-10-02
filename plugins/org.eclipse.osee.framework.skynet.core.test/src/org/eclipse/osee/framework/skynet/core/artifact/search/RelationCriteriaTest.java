/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact.search;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IRelationType;
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
   public void testAddRelationTypeSideToQueryBuilder()  {
      RelationCriteria criteria = new RelationCriteria(CoreRelationTypes.Allocation__Component);
      QueryBuilder builder = mock(QueryBuilder.class);
      criteria.addToQueryBuilder(builder);
      verify(builder).andExists(CoreRelationTypes.Allocation__Component);

      reset(builder);
      ArtifactId artifact = ArtifactId.valueOf(4);
      criteria = new RelationCriteria(artifact, CoreRelationTypes.Allocation__Component, RelationSide.SIDE_A);
      criteria.addToQueryBuilder(builder);
      ArgumentCaptor<RelationTypeSide> rtsCaptor = ArgumentCaptor.forClass(RelationTypeSide.class);
      verify(builder).andRelatedTo(rtsCaptor.capture(), eq(artifact));
      Assert.assertEquals(CoreRelationTypes.Allocation__Component.getGuid(), rtsCaptor.getValue().getGuid());
      Assert.assertEquals(RelationSide.SIDE_A, rtsCaptor.getValue().getSide());
   }

   @Test
   public void testAddRelationTypeToQueryBuilder()  {
      RelationCriteria criteria = new RelationCriteria((IRelationType) CoreRelationTypes.Allocation__Component);
      QueryBuilder builder = mock(QueryBuilder.class);
      criteria.addToQueryBuilder(builder);
      verify(builder).andExists((IRelationType) CoreRelationTypes.Allocation__Component);

      reset(builder);
      ArtifactId artifact = ArtifactId.valueOf(4);
      criteria = new RelationCriteria(artifact, CoreRelationTypes.Allocation__Component, RelationSide.SIDE_A);
      criteria.addToQueryBuilder(builder);
      ArgumentCaptor<RelationTypeSide> rtsCaptor = ArgumentCaptor.forClass(RelationTypeSide.class);
      verify(builder).andRelatedTo(rtsCaptor.capture(), eq(artifact));
      Assert.assertEquals(CoreRelationTypes.Allocation__Component.getGuid(), rtsCaptor.getValue().getGuid());
      Assert.assertEquals(RelationSide.SIDE_A, rtsCaptor.getValue().getSide());
   }
}
