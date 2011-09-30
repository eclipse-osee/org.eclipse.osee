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
package org.eclipse.osee.ats.presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponentInterface;
import org.eclipse.osee.ats.api.search.AtsWebSearchPresenter;
import org.eclipse.osee.ats.api.tokens.AtsArtifactToken;
import org.eclipse.osee.ats.api.tokens.AtsAttributeTypes;
import org.eclipse.osee.ats.api.tokens.AtsRelationTypes;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.presenter.ArtifactProvider;
import org.eclipse.osee.display.presenter.WebSearchPresenter;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/*
 * @author John Misinco
 */
public class WebProgramsPresenter extends WebSearchPresenter implements AtsWebSearchPresenter {

   public WebProgramsPresenter(ArtifactProvider artifactProvider) {
      super(artifactProvider);
   }

   @Override
   public void selectSearch(WebId program, WebId build, boolean nameOnly, String searchPhrase, SearchNavigator atsNavigator) {
      String url = encode(program, build, nameOnly, searchPhrase);
      atsNavigator.navigateSearchResults(url);
   }

   @Override
   public void initSearchHome(AtsSearchHeaderComponentInterface headerComponent) {
      headerComponent.clearAll();
      Collection<WebId> programs = null;
      try {
         programs = getPrograms();
      } catch (Exception ex) {
         headerComponent.setErrorMessage(ex.getMessage());
         return;
      }
      for (WebId program : programs) {
         headerComponent.addProgram(program);
      }
   }

   @Override
   public void initSearchResults(String url, AtsSearchHeaderComponentInterface searchHeaderComponent, SearchResultsListComponent resultsComponent) {
      SearchParameters params = decode(url);
      WebId program = null, build = null;
      searchHeaderComponent.clearAll();
      Collection<WebId> programs = null;

      try {
         programs = getPrograms();
      } catch (Exception ex) {
         searchHeaderComponent.setErrorMessage(ex.getMessage());
         return;
      }
      for (WebId p : programs) {
         searchHeaderComponent.addProgram(p);
         if (p.getGuid().equals(params.getProgram().getGuid())) {
            program = p;
         }
      }

      if (program == null) {
         searchHeaderComponent.setErrorMessage(String.format("Invalid program id: [%s]", params.getProgram().getGuid()));
         return;
      }

      Collection<WebId> builds = null;
      try {
         builds = getbuilds(program);
      } catch (Exception ex) {
         searchHeaderComponent.setErrorMessage(ex.getMessage());
         return;
      }
      for (WebId b : builds) {
         searchHeaderComponent.addBuild(b);
         if (b.getGuid().equals(params.getBuild().getGuid())) {
            build = b;
         }
      }

      if (build == null) {
         searchHeaderComponent.setErrorMessage(String.format("Invalid build id: [%s]", params.getBuild().getGuid()));
         return;
      }

      String branchGuid;
      try {
         branchGuid = getBranchGuid(build);
      } catch (OseeCoreException ex) {
         searchHeaderComponent.setErrorMessage(String.format("Cannot resolve branch id from build id: [%s]",
            params.getBuild().getGuid()));
         return;
      }

      searchHeaderComponent.setSearchCriteria(program, build, params.getNameOnly(), params.getSearchPhrase());
      String newUrl = encode(new WebId(branchGuid, ""), params.getNameOnly(), params.getSearchPhrase());
      initSearchResults(newUrl, searchHeaderComponent, resultsComponent);

   }

   @Override
   public void selectProgram(WebId program, AtsSearchHeaderComponentInterface headerComponent) {
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
      ReadableArtifact webProgramsArtifact =
         artifactProvider.getArtifactByArtifactToken(CoreBranches.COMMON, AtsArtifactToken.WebPrograms);
      List<ReadableArtifact> programs =
         webProgramsArtifact.getRelatedArtifacts(CoreRelationTypes.Universal_Grouping__Members);
      for (ReadableArtifact program : programs) {
         toReturn.add(new WebId(program.getGuid(), program.getName()));
      }
      return toReturn;
   }

   private Collection<WebId> getbuilds(WebId program) throws OseeCoreException {
      ReadableArtifact programArtifact = artifactProvider.getArtifactByGuid(CoreBranches.COMMON, program.getGuid());
      ReadableArtifact teamDef = programArtifact.getRelatedArtifact(CoreRelationTypes.SupportingInfo_SupportingInfo);
      Collection<ReadableArtifact> relatedArtifacts =
         teamDef.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_Version);
      Collection<WebId> builds = new ArrayList<WebId>();
      for (ReadableArtifact build : relatedArtifacts) {
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
      String[] tokens = url.split("\\?");
      WebId program = new WebId(tokens[0].split("=")[1], "unknown");
      WebId build = new WebId(tokens[1].split("=")[1], "unknown");
      boolean nameOnly = tokens[2].split("=")[1].equalsIgnoreCase("true") ? true : false;
      String searchPhrase = tokens[3].split("=")[1];
      searchPhrase = searchPhrase.replaceAll("%20", " ");
      return new SearchParameters(program, build, nameOnly, searchPhrase);
   }

   private String getBranchGuid(WebId build) throws OseeCoreException {
      String guid = null;
      ReadableArtifact buildArtifact = artifactProvider.getArtifactByGuid(CoreBranches.COMMON, build.getGuid());
      guid = buildArtifact.getSoleAttributeAsString(AtsAttributeTypes.BaselineBranchGuid);
      return guid;
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
