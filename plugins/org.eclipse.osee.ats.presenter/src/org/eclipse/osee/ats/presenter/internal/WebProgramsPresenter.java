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
package org.eclipse.osee.ats.presenter.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponent;
import org.eclipse.osee.ats.api.search.AtsWebSearchPresenter;
import org.eclipse.osee.ats.api.tokens.AtsArtifactToken;
import org.eclipse.osee.ats.api.tokens.AtsRelationTypes;
import org.eclipse.osee.display.api.components.ArtifactHeaderComponent;
import org.eclipse.osee.display.api.components.AttributeComponent;
import org.eclipse.osee.display.api.components.RelationComponent;
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.ArtifactQuery;
import org.eclipse.osee.orcs.IArtifactQueryService;

/*
 * @author John Misinco
 */
public class WebProgramsPresenter implements AtsWebSearchPresenter {

   public interface IFakeArtifact {

      List<IFakeArtifact> getRelatedArtifacts(IRelationTypeSide relationSide);

      IFakeArtifact getRelatedArtifact(IRelationTypeSide relationSide);

      String getName();

      String getGuid();
   }

   private IArtifactQueryService queryService;
   private SearchNavigator navigator;

   public void setArtifactQueryService(IArtifactQueryService queryService) {
      this.queryService = queryService;
   }

   public void setSearchNavigator(SearchNavigator navigator) {
      this.navigator = navigator;
   }

   @Override
   public void initSearchHome(SearchHeaderComponent searchHeaderComp) {
   }

   @Override
   public void initSearchResults(String url, SearchHeaderComponent searchHeaderComp, SearchResultsListComponent searchResultsComp) {
   }

   @Override
   public void selectArtifact(WebId id) {
   }

   @Override
   public void initArtifactPage(String url, SearchHeaderComponent searchHeaderComp, ArtifactHeaderComponent artHeaderComp, RelationComponent relComp, AttributeComponent attrComp) {
   }

   @Override
   public void selectRelationType(WebId id) {
   }

   @Override
   public void selectSearch(WebId program, WebId build, boolean nameOnly, String searchPhrase) {
      String url = encode(program, build, nameOnly, searchPhrase);
      navigator.navigateSearchResults(url);
   }

   @Override
   public void initSearchHome(AtsSearchHeaderComponent headerComponent) {
      headerComponent.clearAll();
      Collection<WebId> programs = null;
      try {
         programs = getPrograms();
      } catch (OseeCoreException ex) {
         headerComponent.setErrorMessage(ex.getMessage());
         return;
      }
      for (WebId program : programs) {
         headerComponent.addProgram(program);
      }
   }

   @Override
   public void initSearchResults(String url, AtsSearchHeaderComponent searchHeaderComponent, SearchResultsListComponent resultsComponent) {
      SearchParameters params = decode(url);
      WebId program = null, build = null;
      searchHeaderComponent.clearAll();
      Collection<WebId> programs = null;

      try {
         programs = getPrograms();
      } catch (OseeCoreException ex) {
         searchHeaderComponent.setErrorMessage(ex.getMessage());
         return;
      }
      for (WebId p : programs) {
         searchHeaderComponent.addProgram(p);
         if (p.getGuid().equals(params.getProgram().getGuid())) {
            program = p;
         }
      }

      Collection<WebId> builds = null;
      try {
         builds = getbuilds(program);
      } catch (OseeCoreException ex) {
         searchHeaderComponent.setErrorMessage(ex.getMessage());
         return;
      }
      for (WebId b : builds) {
         searchHeaderComponent.addBuild(b);
         if (b.getGuid().equals(params.getBuild().getGuid())) {
            build = b;
         }
      }

      searchHeaderComponent.setSearchCriteria(program, build, params.getNameOnly(), params.getSearchPhrase());

      resultsComponent.clearAll();
      SearchResultComponent searchResult = resultsComponent.createSearchResult();

   }

   @Override
   public void programSelected(AtsSearchHeaderComponent headerComponent, WebId program) {
      Collection<WebId> builds = null;
      try {
         builds = getbuilds(program);
      } catch (OseeCoreException ex) {
         headerComponent.setErrorMessage(ex.getMessage());
         return;
      }
      for (WebId build : builds) {
         headerComponent.addBuild(build);
      }
   }

   private Collection<WebId> getPrograms() throws OseeCoreException {
      Collection<WebId> toReturn = new LinkedList<WebId>();
      ArtifactQuery webProgramsQuery = queryService.getFromToken(AtsArtifactToken.WebPrograms, CoreBranches.COMMON);
      IFakeArtifact webProgramsArtifact = (IFakeArtifact) webProgramsQuery.getArtifactExactlyOne(LoadLevel.RELATION);
      List<IFakeArtifact> programs =
         webProgramsArtifact.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Members);
      for (IFakeArtifact program : programs) {
         toReturn.add(new WebId(program.getGuid(), program.getName()));
      }
      return toReturn;
   }

   private Collection<WebId> getbuilds(WebId program) throws OseeCoreException {
      ArtifactQuery programQuery = queryService.getFromGuidOrHrid(program.getGuid(), CoreBranches.COMMON);
      IFakeArtifact programArtifact = (IFakeArtifact) programQuery.getArtifactExactlyOne(LoadLevel.RELATION);
      IFakeArtifact teamDef = programArtifact.getRelatedArtifact(CoreRelationTypes.SupportingInfo_SupportingInfo);
      Collection<IFakeArtifact> relatedArtifacts =
         teamDef.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_Version);
      Collection<WebId> builds = new ArrayList<WebId>();
      for (IFakeArtifact build : relatedArtifacts) {
         builds.add(new WebId(build.getGuid(), build.getName()));
      }
      return builds;
   }

   private String encode(WebId program, WebId build, boolean nameOnly, String searchPhrase) {
      StringBuilder sb = new StringBuilder();
      sb.append("program=");
      sb.append(program.getGuid());
      sb.append("?build=");
      sb.append(build.getGuid());
      sb.append("?nameOnly=");
      sb.append(nameOnly);
      sb.append("?search=");
      sb.append(searchPhrase);
      return sb.toString().replaceAll("\\s", "%20");
   }

   private SearchParameters decode(String url) {
      String[] tokens = url.split("?");
      WebId program = new WebId(tokens[0].split("=")[1], "unknown");
      WebId build = new WebId(tokens[1].split("=")[1], "unknown");
      boolean nameOnly = Boolean.getBoolean(tokens[2].split("=")[1]);
      String searchPhrase = tokens[3].split("=")[1];
      searchPhrase = searchPhrase.replaceAll("%20", " ");
      return new SearchParameters(program, build, nameOnly, searchPhrase);
   }

   private class SearchParameters {

      private final WebId program, build;
      private final boolean nameOnly;
      private final String searchPhrase;

      public SearchParameters(WebId program, WebId build, boolean nameOnly, String searchPhrase) {
         this.program = program;
         this.build = build;
         this.nameOnly = nameOnly;
         this.searchPhrase = searchPhrase;
      }

      public WebId getProgram() {
         return program;
      }

      public WebId getBuild() {
         return build;
      }

      public boolean getNameOnly() {
         return nameOnly;
      }

      public String getSearchPhrase() {
         return searchPhrase;
      }

   }

}
