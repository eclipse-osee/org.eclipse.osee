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
package org.eclipse.osee.orcs.rest.internal.search;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.internal.search.artifact.ArtifactSearch_V1;
import org.eclipse.osee.orcs.rest.internal.search.artifact.PredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.artifact.predicate.PredicateHandlerUtil;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchRequest;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResponse;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Megumi Telles
 */
public class ArtifactSearch_V1Test {

   // @formatter:off
   @Mock private OrcsApi orcsApi;
   @Mock private PredicateHandler handler;
   @Mock private QueryFactory queryFactory;
   @Mock private QueryBuilder builder;
   @Mock private UriInfo uriInfo;
   @Mock private Request request;
   // @formatter:on

   private final List<String> types = Arrays.asList("1000000000000070");
   private ArtifactSearch_V1 search;

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      search = new ArtifactSearch_V1(uriInfo, request, orcsApi);
   }

   @Test
   public void testSearchRequestNull()  {
      when(orcsApi.getQueryFactory()).thenReturn(queryFactory);
      when(queryFactory.fromBranch(COMMON)).thenReturn(builder);

      Collection<AttributeTypeId> attributeTypes = PredicateHandlerUtil.getAttributeTypes(types);
      Predicate predicate = new Predicate(SearchMethod.ATTRIBUTE_TYPE, types, Arrays.asList("AtsAdmin"));
      when(builder.and(attributeTypes, predicate.getValues().iterator().next(), predicate.getOptions())).thenReturn(
         builder);

      SearchRequest params = new SearchRequest(COMMON, Arrays.asList(predicate), null, 0, false);
      SearchResponse response = search.getSearchWithMatrixParams(params);

      Assert.assertEquals(response.getSearchRequest(), params);
   }
}