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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.data.ViewArtifact;
import org.eclipse.osee.display.api.data.ViewId;
import org.eclipse.osee.display.api.data.ViewSearchParameters;
import org.eclipse.osee.display.api.search.ArtifactProvider;
import org.eclipse.osee.display.presenter.SearchPresenterImpl;
import org.eclipse.osee.display.presenter.Utility;
import org.eclipse.osee.display.presenter.mocks.MockArtifactHeaderComponent;
import org.eclipse.osee.display.presenter.mocks.MockArtifactProvider;
import org.eclipse.osee.display.presenter.mocks.MockAttributeComponent;
import org.eclipse.osee.display.presenter.mocks.MockDisplayOptionsComponent;
import org.eclipse.osee.display.presenter.mocks.MockLogger;
import org.eclipse.osee.display.presenter.mocks.MockRelationComponent;
import org.eclipse.osee.display.presenter.mocks.MockSearchHeaderComponent;
import org.eclipse.osee.display.presenter.mocks.MockSearchNavigator;
import org.eclipse.osee.display.presenter.mocks.MockSearchResultComponent;
import org.eclipse.osee.display.presenter.mocks.MockSearchResultsListComponent;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
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
public class SearchPresenterTest {

   @SuppressWarnings({"rawtypes", "unchecked"})
   private List<Match<ReadableArtifact, ReadableAttribute<?>>> getSearchResults() {
      List<Match<ReadableArtifact, ReadableAttribute<?>>> toReturn =
         new ArrayList<Match<ReadableArtifact, ReadableAttribute<?>>>();
      MockArtifact art = new MockArtifact("guid1", "matchArt");
      MockAttribute attr = new MockAttribute(CoreAttributeTypes.Name, "matchArt");
      Match match = new MockMatch(art, attr);
      toReturn.add(match);
      return toReturn;
   }

   @Test
   public void testInitSearchResults() throws UnsupportedEncodingException {
      MockArtifactProvider provider = new MockArtifactProvider();
      MockDisplayOptionsComponent optionsComp = new MockDisplayOptionsComponent();
      provider.setResultList(getSearchResults());
      SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters> presenter =
         new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(provider, new MockLogger());
      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      MockSearchResultsListComponent searchResultsComp = new MockSearchResultsListComponent();
      String url =
         "/branch=" + Utility.encode(GUID.create()) + "&nameOnly=true&search=" + Utility.encode("this is a test");
      presenter.initSearchResults(url, searchHeaderComp, searchResultsComp, optionsComp);
      List<MockSearchResultComponent> searchResults = searchResultsComp.getSearchResults();
      Assert.assertEquals(1, searchResults.size());
   }

   @Test
   public void testInitSearchResultsErrors() throws UnsupportedEncodingException {
      SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters> presenter =
         new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(null, new MockLogger());
      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      MockDisplayOptionsComponent optionsComp = new MockDisplayOptionsComponent();
      MockSearchResultsListComponent searchResultsComp = new MockSearchResultsListComponent();
      String url = "badUrl";
      presenter.initSearchResults(url, searchHeaderComp, searchResultsComp, optionsComp);
      Assert.assertNotNull(searchResultsComp.getErrorMessage());

      ExceptionArtifactProvider provider = new ExceptionArtifactProvider();
      presenter = new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(provider, new MockLogger());
      searchHeaderComp = new MockSearchHeaderComponent();
      searchResultsComp = new MockSearchResultsListComponent();
      url = "/branch=" + Utility.encode(GUID.create()) + "&nameOnly=true&search=" + Utility.encode("this is a test");
      presenter.initSearchResults(url, searchHeaderComp, searchResultsComp, optionsComp);
      Assert.assertNotNull(searchResultsComp.getErrorMessage());
   }

   @Test
   public void testSelectArtifact() throws UnsupportedEncodingException {
      MockSearchNavigator navigator = new MockSearchNavigator();
      SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters> presenter =
         new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(null, new MockLogger());
      String branchGuid = GUID.create();
      String artGuid = GUID.create();
      ViewArtifact artifact = new ViewArtifact(artGuid, "name", "type", null, new ViewId(branchGuid, "branchName"));
      presenter.selectArtifact("", artifact, navigator);
      String expectedUrl = "/artifact=" + Utility.encode(artGuid) + "&branch=" + Utility.encode(branchGuid);
      Assert.assertEquals(expectedUrl, navigator.getArtifactUrl());
   }

   @Test
   public void testInitArtifactPage() throws UnsupportedEncodingException {
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
      String url = "/branch=" + Utility.encode(CoreBranches.COMMON.getGuid()) + "&artifact=" + artGuid;
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
   public void testInitArtifactPageErrors() throws UnsupportedEncodingException {
      String url = "badUrl";
      SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters> presenter =
         new SearchPresenterImpl<SearchHeaderComponent, ViewSearchParameters>(null, new MockLogger());
      MockSearchHeaderComponent searchHeaderComp = new MockSearchHeaderComponent();
      MockArtifactHeaderComponent artHeaderComp = new MockArtifactHeaderComponent();
      MockDisplayOptionsComponent optionsComp = new MockDisplayOptionsComponent();
      MockRelationComponent relComp = new MockRelationComponent();
      MockAttributeComponent attrComp = new MockAttributeComponent();
      presenter.initArtifactPage(url, searchHeaderComp, artHeaderComp, relComp, attrComp, optionsComp);
      Assert.assertNotNull(artHeaderComp.getErrorMessage());

      url = "/branch=" + Utility.encode(GUID.create()) + "&artifact=" + Utility.encode(GUID.create());
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

      @Override
      public List<ReadableArtifact> getRelatedArtifacts(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

      @Override
      public ReadableArtifact getRelatedArtifact(ReadableArtifact art, IRelationTypeSide relationTypeSide) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

      @Override
      public ReadableArtifact getParent(ReadableArtifact art) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

      @Override
      public Collection<RelationType> getValidRelationTypes(ReadableArtifact art) throws OseeCoreException {
         throw new OseeCoreException("test");
      }

   }
}
