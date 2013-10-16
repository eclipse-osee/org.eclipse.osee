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
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.data.ViewArtifact;
import org.eclipse.osee.display.api.data.ViewId;
import org.eclipse.osee.display.api.data.ViewSearchParameters;
import org.eclipse.osee.display.api.search.ArtifactProvider;
import org.eclipse.osee.display.api.search.AsyncSearchListener;
import org.eclipse.osee.display.presenter.SearchPresenterImpl;
import org.eclipse.osee.display.presenter.mocks.MockArtifact;
import org.eclipse.osee.display.presenter.mocks.MockArtifactHeaderComponent;
import org.eclipse.osee.display.presenter.mocks.MockArtifactProvider;
import org.eclipse.osee.display.presenter.mocks.MockAttribute;
import org.eclipse.osee.display.presenter.mocks.MockAttributeComponent;
import org.eclipse.osee.display.presenter.mocks.MockDisplayOptionsComponent;
import org.eclipse.osee.display.presenter.mocks.MockLogger;
import org.eclipse.osee.display.presenter.mocks.MockMatch;
import org.eclipse.osee.display.presenter.mocks.MockRelationComponent;
import org.eclipse.osee.display.presenter.mocks.MockSearchHeaderComponent;
import org.eclipse.osee.display.presenter.mocks.MockSearchNavigator;
import org.eclipse.osee.display.presenter.mocks.MockSearchResultComponent;
import org.eclipse.osee.display.presenter.mocks.MockSearchResultsListComponent;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.UrlQuery;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author John R. Misinco
 */
public class SearchPresenterTest {

   @SuppressWarnings({"rawtypes", "unchecked"})
   private List<Match<ArtifactReadable, AttributeReadable<?>>> getSearchResults() {
      List<Match<ArtifactReadable, AttributeReadable<?>>> toReturn =
         new ArrayList<Match<ArtifactReadable, AttributeReadable<?>>>();
      MockArtifact art = new MockArtifact("guid1", "matchArt");
      MockAttribute attr = new MockAttribute(CoreAttributeTypes.Name, "matchArt");
      Match match = new MockMatch(art, attr);
      toReturn.add(match);
      return toReturn;
   }

   @Test
   public void testInitSearchResults() {
      MockArtifactProvider provider = new MockArtifactProvider();
      MockDisplayOptionsComponent optionsComp = new MockDisplayOptionsComponent();
      provider.setResultList(getSearchResults());
      SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters> presenter =
         new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(provider, new MockLogger());
      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      MockSearchResultsListComponent searchResultsComp = new MockSearchResultsListComponent();
      String url =
         "/" + new UrlQuery().put("branch", CoreBranches.COMMON.getGuid()).put("nameOnly", "true").put("search",
            "this is a test").toString();

      presenter.initSearchResults(url, searchHeaderComp, searchResultsComp, optionsComp);
      List<MockSearchResultComponent> searchResults = searchResultsComp.getSearchResults();
      Assert.assertEquals(1, searchResults.size());
   }

   @Test
   public void testInitSearchResultsErrors() {
      SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters> presenter =
         new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(null, new MockLogger());
      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      MockDisplayOptionsComponent optionsComp = new MockDisplayOptionsComponent();
      MockSearchResultsListComponent searchResultsComp = new MockSearchResultsListComponent();

      ExceptionArtifactProvider provider = new ExceptionArtifactProvider();
      presenter = new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(provider, new MockLogger());
      searchHeaderComp = new MockSearchHeaderComponent();
      searchResultsComp = new MockSearchResultsListComponent();

      String url =
         "/" + new UrlQuery().put("branch", CoreBranches.COMMON.getGuid()).put("nameOnly", "true").put("search",
            "this is a test").toString();

      presenter.initSearchResults(url, searchHeaderComp, searchResultsComp, optionsComp);
      Assert.assertNotNull(searchResultsComp.getErrorMessage());
   }

   @Test
   public void testSelectArtifact() {
      MockSearchNavigator navigator = new MockSearchNavigator();
      SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters> presenter =
         new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(null, new MockLogger());
      String branchGuid = GUID.create();
      String artGuid = GUID.create();
      ViewArtifact artifact = new ViewArtifact(artGuid, "name", "type", null, new ViewId(branchGuid, "branchName"));
      presenter.selectArtifact("", artifact, navigator);

      String expectedUrl = "/" + new UrlQuery().put("artifact", artGuid).put("branch", branchGuid).toString();

      Assert.assertEquals(expectedUrl, navigator.getArtifactUrl());
   }

   @Test
   public void testInitArtifactPage() {
      MockArtifactProvider provider = new MockArtifactProvider();
      MockDisplayOptionsComponent optionsComp = new MockDisplayOptionsComponent();
      provider.setResultList(getSearchResults());
      SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters> presenter =
         new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(provider, new MockLogger());
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

      String url =
         "/" + new UrlQuery().put("branch", CoreBranches.COMMON.getGuid()).put("artifact", artGuid).toString();

      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      MockArtifactHeaderComponent artHeaderComp = new MockArtifactHeaderComponent();
      MockRelationComponent relComp = new MockRelationComponent();
      MockAttributeComponent attrComp = new MockAttributeComponent();
      presenter.initArtifactPage(url, searchHeaderComp, artHeaderComp, relComp, attrComp, optionsComp);

      ViewArtifact artifact = artHeaderComp.getArtifact();
      Assert.assertNotNull(artifact);
      Assert.assertEquals(artGuid, artifact.getGuid());
      Assert.assertEquals(1, relComp.getRelationTypes().size());
      Assert.assertEquals(1, attrComp.getAttributes().keySet().size());

      provider = new MockArtifactProvider();
      presenter = new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(provider, new MockLogger());
      presenter.initArtifactPage(url, searchHeaderComp, artHeaderComp, relComp, attrComp, optionsComp);
      Assert.assertNotNull(artHeaderComp.getErrorMessage());
   }

   @Test
   public void testInitArtifactPageErrors() {
      String url = "badUrl";
      SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters> presenter =
         new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(new MockArtifactProvider(),
            new MockLogger());

      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      MockArtifactHeaderComponent artHeaderComp = new MockArtifactHeaderComponent();
      MockDisplayOptionsComponent optionsComp = new MockDisplayOptionsComponent();
      MockRelationComponent relComp = new MockRelationComponent();
      MockAttributeComponent attrComp = new MockAttributeComponent();
      presenter.initArtifactPage(url, searchHeaderComp, artHeaderComp, relComp, attrComp, optionsComp);
      Assert.assertNotNull(artHeaderComp.getErrorMessage());

      url = "/" + new UrlQuery().put("branch", CoreBranches.COMMON.getGuid()).put("artifact", GUID.create()).toString();

      ExceptionArtifactProvider provider = new ExceptionArtifactProvider();
      presenter = new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(provider, new MockLogger());
      searchHeaderComp = new MockSearchHeaderComponent();
      artHeaderComp = new MockArtifactHeaderComponent();
      relComp = new MockRelationComponent();
      attrComp = new MockAttributeComponent();
      presenter.initArtifactPage(url, searchHeaderComp, artHeaderComp, relComp, attrComp, optionsComp);
      Assert.assertNotNull(artHeaderComp.getErrorMessage());
   }

   @Test
   public void testSelectRelationType() {
      MockArtifactProvider provider = new MockArtifactProvider();
      MockRelationComponent relComp = new MockRelationComponent();

      provider.setResultList(getSearchResults());
      SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters> presenter =
         new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(provider, new MockLogger());
      String artGuid = GUID.create();
      String artGuidA = GUID.create();
      String artGuidB = GUID.create();

      MockArtifact testArt = new MockArtifact(artGuid, "name");
      long relGuid = CoreRelationTypes.Allocation__Component.getGuid();
      String relName = CoreRelationTypes.Allocation__Component.getName();
      RelationType relType =
         new RelationType(relGuid, relName, "sideA", "sideB", CoreArtifactTypes.AbstractSoftwareRequirement,
            CoreArtifactTypes.AbstractTestResult, RelationTypeMultiplicity.ONE_TO_ONE, "");
      testArt.addRelationType(relType);

      provider.addArtifact(testArt);

      ViewId branch = new ViewId(GUID.create(), "branchName");
      ViewArtifact artifact = new ViewArtifact(artGuid, "artName", "artType", null, branch);
      ViewId relation = new ViewId(Long.toString(relGuid), relName);
      presenter.selectRelationType(artifact, relation, relComp);
      Assert.assertEquals(0, relComp.getLeftRelations().size());
      Assert.assertEquals(0, relComp.getRightRelations().size());

      MockArtifact relatedArtA = new MockArtifact(artGuidA, "related");
      testArt.addRelation(CoreRelationTypes.Allocation__Requirement, relatedArtA);
      presenter.selectRelationType(artifact, relation, relComp);
      Assert.assertEquals(1, relComp.getLeftRelations().size());
      Assert.assertEquals(0, relComp.getRightRelations().size());

      MockArtifact relatedArtB = new MockArtifact(artGuidB, "related");
      testArt.addRelation(CoreRelationTypes.Allocation__Component, relatedArtB);
      presenter.selectRelationType(artifact, relation, relComp);
      Assert.assertEquals(1, relComp.getLeftRelations().size());
      Assert.assertEquals(1, relComp.getRightRelations().size());

      testArt.clearRelations();
      testArt.addRelationType(relType);
      testArt.addRelation(CoreRelationTypes.Allocation__Component, relatedArtB);
      presenter.selectRelationType(artifact, relation, relComp);
      Assert.assertEquals(0, relComp.getLeftRelations().size());
      Assert.assertEquals(1, relComp.getRightRelations().size());

   }

   @Test
   public void testSelectRelationTypeErrors() {
      SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters> presenter =
         new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(null, new MockLogger());
      MockRelationComponent relComp = new MockRelationComponent();
      ViewId relation = new ViewId("0", "Name");
      presenter.selectRelationType(null, relation, relComp);
      Assert.assertNotNull(relComp.getErrorMessage());

      relComp = new MockRelationComponent();
      ViewArtifact artifact =
         new ViewArtifact(GUID.create(), "name", "type", null, new ViewId(GUID.create(), "branchName"));
      presenter.selectRelationType(artifact, null, relComp);
      Assert.assertNotNull(relComp.getErrorMessage());

      ExceptionArtifactProvider provider = new ExceptionArtifactProvider();
      relComp = new MockRelationComponent();
      presenter = new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(provider, new MockLogger());
      presenter.selectRelationType(artifact, relation, relComp);
      Assert.assertNotNull(relComp.getErrorMessage());
   }

   private class ExceptionArtifactProvider implements ArtifactProvider {

      @Override
      public ArtifactReadable getArtifactByArtifactToken(IOseeBranch branch, IArtifactToken token) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

      @Override
      public ArtifactReadable getArtifactByGuid(IOseeBranch branch, String guid) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

      @Override
      public ResultSet<ArtifactReadable> getRelatedArtifacts(ArtifactReadable art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

      @Override
      public ArtifactReadable getRelatedArtifact(ArtifactReadable art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

      @Override
      public ArtifactReadable getParent(ArtifactReadable art) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

      @Override
      public Collection<? extends IRelationType> getValidRelationTypes(ArtifactReadable art) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

      @Override
      public void getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase, AsyncSearchListener callback) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

      @Override
      public void cancelSearch() {
         // do nothing
      }

      @Override
      public String getSideAName(IRelationType type) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

      @Override
      public String getSideBName(IRelationType type) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

   }
}
