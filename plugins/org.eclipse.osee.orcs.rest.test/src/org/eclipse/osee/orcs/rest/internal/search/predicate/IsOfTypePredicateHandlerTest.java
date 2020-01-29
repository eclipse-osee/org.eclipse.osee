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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.IsOfTypePredicateHandler;
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
 * @author John R. Misinco
 */
public class IsOfTypePredicateHandlerTest {

   @Mock
   private QueryBuilder builder;
   @Mock
   private OrcsApi orcsApi;
   @Mock
   private OrcsTypes orcsTypes;
   @Mock
   private ArtifactTypes artifactTypes;
   @Captor
   private ArgumentCaptor<Collection<ArtifactTypeToken>> artifactTypesCaptor;

   @Before
   public void initialize() {
      MockitoAnnotations.initMocks(this);
      when(orcsApi.getOrcsTypes()).thenReturn(orcsTypes);
      when(orcsTypes.getArtifactTypes()).thenReturn(artifactTypes);
      when(artifactTypes.get(Artifact.getId())).thenReturn(Artifact);
      when(artifactTypes.get(Folder.getId())).thenReturn(Folder);
   }

   @Test
   public void testHandleSingle() {
      IsOfTypePredicateHandler handler = new IsOfTypePredicateHandler();
      //no type params, op, or flags for ids - any passed are ignored

      List<String> values = Collections.singletonList(Artifact.getIdString());
      Predicate testPredicate = new Predicate(SearchMethod.IS_OF_TYPE, null, values);
      handler.handle(orcsApi, builder, testPredicate);
      verify(builder).andIsOfType(artifactTypesCaptor.capture());
      Assert.assertEquals(1, artifactTypesCaptor.getValue().size());
      // :-)
      Assert.assertTrue(artifactTypesCaptor.getValue().iterator().next().equals(Artifact));
   }

   @Test
   public void testHandleMultiple() {
      IsOfTypePredicateHandler handler = new IsOfTypePredicateHandler();
      String id1 = Artifact.getIdString();
      String id2 = Folder.getIdString();

      Predicate testPredicate = new Predicate(SearchMethod.IS_OF_TYPE, null, Arrays.asList(id1, id2));
      handler.handle(orcsApi, builder, testPredicate);
      verify(builder).andIsOfType(artifactTypesCaptor.capture());
      Assert.assertEquals(2, artifactTypesCaptor.getValue().size());

      Iterator<ArtifactTypeToken> iterator = artifactTypesCaptor.getValue().iterator();
      Assert.assertEquals(CoreArtifactTypes.Artifact, iterator.next());
      Assert.assertEquals(CoreArtifactTypes.Folder, iterator.next());
   }

   @Test(expected = OseeArgumentException.class)
   public void testHandleBadValues() {
      IsOfTypePredicateHandler handler = new IsOfTypePredicateHandler();
      Predicate testPredicate = new Predicate(SearchMethod.IS_OF_TYPE, null, null);
      handler.handle(orcsApi, builder, testPredicate);
   }

   @Test(expected = OseeArgumentException.class)
   public void testBadSearchMethod() {
      IsOfTypePredicateHandler handler = new IsOfTypePredicateHandler();
      String id1 = "12345";
      List<String> values = Collections.singletonList(id1);
      Predicate testPredicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, null, values);
      handler.handle(orcsApi, builder, testPredicate);
   }
}