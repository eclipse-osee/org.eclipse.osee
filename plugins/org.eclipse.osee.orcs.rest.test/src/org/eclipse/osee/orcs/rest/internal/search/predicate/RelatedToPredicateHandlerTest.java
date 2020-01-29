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
package org.eclipse.osee.orcs.rest.internal.search.predicate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.RelatedToPredicateHandler;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author John Misinco
 */
public class RelatedToPredicateHandlerTest {

   @Mock
   private QueryBuilder builder;
   @Captor
   private ArgumentCaptor<Collection<ArtifactId>> idsCaptor;
   @Captor
   private ArgumentCaptor<RelationTypeSide> rtsCaptor;

   @Before
   public void initialize() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testRelatedToLocalIds() {
      RelatedToPredicateHandler handler = new RelatedToPredicateHandler();
      Predicate testPredicate = new Predicate(SearchMethod.RELATED_TO, Arrays.asList("A1", "B2"),
         Arrays.asList("4", "5"), QueryOption.TOKEN_DELIMITER__ANY);
      handler.handle(null, builder, testPredicate);
      verify(builder, times(2)).andRelatedTo(rtsCaptor.capture(), idsCaptor.capture());
      List<RelationTypeSide> rts = rtsCaptor.getAllValues();
      Assert.assertEquals(2, rts.size());
      verifyRelationTypeSide(rts.get(0), "A1");
      verifyRelationTypeSide(rts.get(1), "B2");

      List<Collection<ArtifactId>> ids = idsCaptor.getAllValues();
      Assert.assertEquals(2, ids.size());
      ids.containsAll(Arrays.asList(ArtifactId.valueOf(4), ArtifactId.valueOf(5)));
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testUnsupportedOperation() {
      RelatedToPredicateHandler handler = new RelatedToPredicateHandler();
      Predicate testPredicate = new Predicate(SearchMethod.RELATED_TO, Arrays.asList("A1", "B2"),
         Arrays.asList(GUID.create()), QueryOption.TOKEN_DELIMITER__ANY);
      handler.handle(null, builder, testPredicate);
   }

   private void verifyRelationTypeSide(RelationTypeSide rts, String input) {
      if (input.startsWith("A")) {
         Assert.assertEquals(RelationSide.SIDE_A, rts.getSide());
      } else {
         Assert.assertEquals(RelationSide.SIDE_B, rts.getSide());
      }
      Assert.assertTrue(rts.getGuid().equals(Long.parseLong(input.substring(1))));
   }
}
