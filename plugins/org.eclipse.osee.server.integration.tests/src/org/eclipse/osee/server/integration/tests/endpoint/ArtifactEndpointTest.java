/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.server.integration.tests.endpoint;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.RequestType;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchRequest;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResponse;
import org.eclipse.osee.server.integration.tests.util.IntegrationUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactEndpointTest {
   private static ArtifactEndpoint artifactEndpoint;

   @BeforeClass
   public static void testSetup() {
      artifactEndpoint = IntegrationUtil.createClient().getArtifactEndpoint(CoreBranches.COMMON);
   }

   @Test
   public void testSearchWithMatrixParams() {
      ArtifactToken root = CoreArtifactTokens.DefaultHierarchyRoot;

      List<String> emptyStringList = Collections.emptyList();
      List<String> artifactTypeList = Arrays.asList(root.getArtifactTypeId().getIdString());
      Predicate predicate = new Predicate(SearchMethod.TYPE_EQUALS, emptyStringList, artifactTypeList);
      SearchRequest params = new SearchRequest(COMMON, Arrays.asList(predicate), RequestType.IDS, 0, false);
      SearchResponse response = artifactEndpoint.getSearchWithMatrixParams(params);
      List<ArtifactId> ids = response.getIds();

      Assert.assertEquals(1, ids.size());
      Assert.assertEquals(root, ids.get(0));
   }
}