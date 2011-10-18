/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter.internal;

import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.api.search.ArtifactProvider;
import org.eclipse.osee.display.presenter.WebSearchPresenter;
import org.eclipse.osee.display.presenter.mocks.MockArtifactHeaderComponent;
import org.eclipse.osee.display.presenter.mocks.MockArtifactProvider;
import org.eclipse.osee.display.presenter.mocks.MockAttributeComponent;
import org.eclipse.osee.display.presenter.mocks.MockRelationComponent;
import org.eclipse.osee.display.presenter.mocks.MockSearchHeaderComponent;
import org.eclipse.osee.display.presenter.mocks.MockSearchNavigator;
import org.eclipse.osee.display.presenter.mocks.MockSearchResultComponent;
import org.eclipse.osee.display.presenter.mocks.MockSearchResultsListComponent;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.mock.MockArtifact;
import org.eclipse.osee.orcs.mock.MockAttribute;
import org.eclipse.osee.orcs.mock.MockMatch;
import org.eclipse.osee.orcs.search.Match;
import org.junit.Test;

/**
 * @author John R. Misinco
 */
public class WebSearchPresenterTest {

   private List<Match<ReadableArtifact, ReadableAttribute<?>>> getSearchReslts() {
      List<Match<ReadableArtifact, ReadableAttribute<?>>> toReturn =
         new ArrayList<Match<ReadableArtifact, ReadableAttribute<?>>>();
      MockArtifact art = new MockArtifact("guid1", "matchArt");
      MockAttribute attr = new MockAttribute(CoreAttributeTypes.Name, "matchArt");
      Match match = new MockMatch(art, attr);
      toReturn.add(match);
      return toReturn;
   }

   @Test
   public void testInitSearchResults() {
      MockArtifactProvider provider = new MockArtifactProvider();
      provider.setResultList(getSearchReslts());
      WebSearchPresenter<SearchHeaderComponent> presenter = new WebSearchPresenter<SearchHeaderComponent>(provider);
      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      MockSearchResultsListComponent searchResultsComp = new MockSearchResultsListComponent();
      String url = "/branch=" + GUID.create() + "?nameOnly=true?search=this%20is%20a%20test";
      presenter.initSearchResults(url, searchHeaderComp, searchResultsComp);
      List<MockSearchResultComponent> searchResults = searchResultsComp.getSearchResults();
      Assert.assertEquals(1, searchResults.size());
   }

   @Test
   public void testInitSearchResultsErrors() {
      WebSearchPresenter<SearchHeaderComponent> presenter = new WebSearchPresenter<SearchHeaderComponent>(null);
      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      MockSearchResultsListComponent searchResultsComp = new MockSearchResultsListComponent();
      String url = "badUrl";
      presenter.initSearchResults(url, searchHeaderComp, searchResultsComp);
      Assert.assertNotNull(searchResultsComp.getErrorMessage());

      ExceptionArtifactProvider provider = new ExceptionArtifactProvider();
      presenter = new WebSearchPresenter<SearchHeaderComponent>(provider);
      searchHeaderComp = new MockSearchHeaderComponent();
      searchResultsComp = new MockSearchResultsListComponent();
      url = "/branch=" + GUID.create() + "?nameOnly=true?search=this%20is%20a%20test";
      presenter.initSearchResults(url, searchHeaderComp, searchResultsComp);
      Assert.assertNotNull(searchResultsComp.getErrorMessage());
   }

   @Test
   public void testSelectArtifact() {
      MockSearchNavigator navigator = new MockSearchNavigator();
      WebSearchPresenter<SearchHeaderComponent> presenter = new WebSearchPresenter<SearchHeaderComponent>(null);
      String branchGuid = GUID.create();
      String artGuid = GUID.create();
      WebArtifact artifact = new WebArtifact(artGuid, "name", "type", null, new WebId(branchGuid, "branchName"));
      presenter.selectArtifact(artifact, navigator);
      String expectedUrl = "/branch=" + branchGuid + "?artifact=" + artGuid;
      Assert.assertEquals(expectedUrl, navigator.getArtifactUrl());
   }

   @Test
   public void testInitSearchHome() {
      WebSearchPresenter<SearchHeaderComponent> presenter = new WebSearchPresenter<SearchHeaderComponent>(null);
      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      presenter.initSearchHome(searchHeaderComp);
      Assert.assertTrue(searchHeaderComp.isClearAllCalled());
   }

   @Test
   public void testInitArtifactPage() {
      MockArtifactProvider provider = new MockArtifactProvider();
      provider.setResultList(getSearchReslts());
      WebSearchPresenter<SearchHeaderComponent> presenter = new WebSearchPresenter<SearchHeaderComponent>(provider);
      String artGuid = GUID.create();
      MockArtifact testArt = new MockArtifact(artGuid, "name");
      MockArtifact parentArt = new MockArtifact(GUID.create(), "parent");
      MockArtifact grandParentArt = new MockArtifact(GUID.create(), "grandParent");
      parentArt.setParent(grandParentArt);
      testArt.setParent(parentArt);
      RelationType relType =
         new RelationType(0L, "typeName", "sideA", "sideB", CoreArtifactTypes.AbstractSoftwareRequirement,
            CoreArtifactTypes.AbstractTestResult, RelationTypeMultiplicity.ONE_TO_ONE, "");
      testArt.addRelationType(relType);
      provider.addArtifact(testArt);
      String url = "/branch=" + GUID.create() + "?artifact=" + artGuid;
      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      MockArtifactHeaderComponent artHeaderComp = new MockArtifactHeaderComponent();
      MockRelationComponent relComp = new MockRelationComponent();
      MockAttributeComponent attrComp = new MockAttributeComponent();
      presenter.initArtifactPage(url, searchHeaderComp, artHeaderComp, relComp, attrComp);
      Assert.assertEquals(artGuid, artHeaderComp.getArtifact().getGuid());
      Assert.assertEquals(2, relComp.getRelationTypes().size());
      Assert.assertEquals(1, attrComp.getAttributes().keySet().size());

      provider = new MockArtifactProvider();
      presenter = new WebSearchPresenter<SearchHeaderComponent>(provider);
      presenter.initArtifactPage(url, searchHeaderComp, artHeaderComp, relComp, attrComp);
      Assert.assertNotNull(artHeaderComp.getErrorMessage());
   }

   @Test
   public void testInitArtifactPageErrors() {
      String url = "badUrl";
      WebSearchPresenter<SearchHeaderComponent> presenter = new WebSearchPresenter<SearchHeaderComponent>(null);
      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      MockArtifactHeaderComponent artHeaderComp = new MockArtifactHeaderComponent();
      MockRelationComponent relComp = new MockRelationComponent();
      MockAttributeComponent attrComp = new MockAttributeComponent();
      presenter.initArtifactPage(url, searchHeaderComp, artHeaderComp, relComp, attrComp);
      Assert.assertNotNull(artHeaderComp.getErrorMessage());

      url = "/branch=" + GUID.create() + "?artifact=" + GUID.create();
      ExceptionArtifactProvider provider = new ExceptionArtifactProvider();
      presenter = new WebSearchPresenter<SearchHeaderComponent>(provider);
      searchHeaderComp = new MockSearchHeaderComponent();
      artHeaderComp = new MockArtifactHeaderComponent();
      relComp = new MockRelationComponent();
      attrComp = new MockAttributeComponent();
      presenter.initArtifactPage(url, searchHeaderComp, artHeaderComp, relComp, attrComp);
      Assert.assertNotNull(artHeaderComp.getErrorMessage());
   }

   @Test
   public void testSelectRelationType() {
      MockArtifactProvider provider = new MockArtifactProvider();
      provider.setResultList(getSearchReslts());
      WebSearchPresenter<SearchHeaderComponent> presenter = new WebSearchPresenter<SearchHeaderComponent>(provider);
      String artGuid = GUID.create();
      String artGuid2 = GUID.create();
      MockArtifact testArt = new MockArtifact(artGuid, "name");
      MockArtifact relatedArt = new MockArtifact(artGuid2, "related");
      testArt.addRelation(CoreRelationTypes.Allocation__Component, relatedArt);
      long relGuid = CoreRelationTypes.Allocation__Component.getGuid();
      String relName = CoreRelationTypes.Allocation__Component.getName();
      RelationType relType =
         new RelationType(relGuid, relName, "sideA", "sideB", CoreArtifactTypes.AbstractSoftwareRequirement,
            CoreArtifactTypes.AbstractTestResult, RelationTypeMultiplicity.ONE_TO_ONE, "");
      testArt.addRelationType(relType);
      provider.addArtifact(testArt);
      MockRelationComponent relComp = new MockRelationComponent();
      WebId branch = new WebId(GUID.create(), "branchName");
      WebArtifact artifact = new WebArtifact(artGuid, "artName", "artType", null, branch);
      String strRelGuid = Long.toString(relGuid) + ":B";
      WebId relation = new WebId(strRelGuid, relName);
      presenter.selectRelationType(artifact, relation, relComp);
      Assert.assertEquals(relatedArt.getGuid(), relComp.getRelations().iterator().next().getGuid());
   }

   @Test
   public void testSelectRelationTypeErrors() {
      WebSearchPresenter<SearchHeaderComponent> presenter = new WebSearchPresenter<SearchHeaderComponent>(null);
      MockRelationComponent relComp = new MockRelationComponent();
      WebId relation = new WebId("0:A", "Name");
      presenter.selectRelationType(null, relation, relComp);
      Assert.assertNotNull(relComp.getErrorMessage());

      relComp = new MockRelationComponent();
      WebArtifact artifact =
         new WebArtifact(GUID.create(), "name", "type", null, new WebId(GUID.create(), "branchName"));
      presenter.selectRelationType(artifact, null, relComp);
      Assert.assertNotNull(relComp.getErrorMessage());

      ExceptionArtifactProvider provider = new ExceptionArtifactProvider();
      relComp = new MockRelationComponent();
      presenter = new WebSearchPresenter<SearchHeaderComponent>(provider);
      presenter.selectRelationType(artifact, relation, relComp);
      Assert.assertNotNull(relComp.getErrorMessage());
   }

   private class ExceptionArtifactProvider implements ArtifactProvider {

      @Override
      public ReadableArtifact getArtifactByArtifactToken(IOseeBranch branch, IArtifactToken token) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

      @Override
      public ReadableArtifact getArtifactByGuid(IOseeBranch branch, String guid) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

      @Override
      public List<Match<ReadableArtifact, ReadableAttribute<?>>> getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

   }
}
