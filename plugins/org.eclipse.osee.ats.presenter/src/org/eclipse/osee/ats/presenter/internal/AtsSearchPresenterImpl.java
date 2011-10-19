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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponent;
import org.eclipse.osee.ats.api.data.AtsSearchParameters;
import org.eclipse.osee.ats.api.search.AtsArtifactProvider;
import org.eclipse.osee.ats.api.search.AtsSearchPresenter;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.presenter.WebSearchPresenter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author John Misinco
 */
public class AtsSearchPresenterImpl<T extends AtsSearchHeaderComponent> extends WebSearchPresenter<T> implements AtsSearchPresenter<T> {

   private final static Pattern buildPattern = Pattern.compile("build=([0-9A-Za-z\\+_=]{20,22})");
   private final static Pattern programPattern = Pattern.compile("program=([0-9A-Za-z\\+_=]{20,22})");

   private final Matcher buildMatcher;
   private final Matcher programMatcher;
   private final AtsArtifactProvider atsArtifactProvider;

   public AtsSearchPresenterImpl(AtsArtifactProvider artifactProvider) {
      super(artifactProvider);
      atsArtifactProvider = artifactProvider;
      buildMatcher = buildPattern.matcher("");
      programMatcher = programPattern.matcher("");
   }

   @Override
   public void selectSearch(AtsSearchParameters params, SearchNavigator atsNavigator) {
      String url = encode(params, null);
      atsNavigator.navigateSearchResults(url);
   }

   private void addProgramsToSearchHeader(T headerComponent) {
      headerComponent.clearAll();
      Collection<WebId> programs = null;
      try {
         programs = getPrograms();
      } catch (Exception ex) {
         setErrorMessage(headerComponent, ex.getMessage());
         return;
      }
      for (WebId program : programs) {
         headerComponent.addProgram(program);
      }
   }

   @Override
   public void initSearchResults(String url, T searchHeaderComponent, SearchResultsListComponent resultsComponent) {
      if (!Strings.isValid(url)) {
         addProgramsToSearchHeader(searchHeaderComponent);
         return;
      }

      AtsSearchParameters params = decode(url);
      //      WebId program = null, build = null;
      //      Collection<WebId> programs = null;

      if (!params.isValid()) {
         setErrorMessage(searchHeaderComponent, String.format("Invalid url received: %s", url));
         return;
      }

      //      try {
      //         programs = getPrograms();
      //      } catch (Exception ex) {
      //         setErrorMessage(searchHeaderComponent, ex.getMessage());
      //         return;
      //      }
      //      for (WebId p : programs) {
      //         if (p.getGuid().equals(params.getProgram().getGuid())) {
      //            program = p;
      //            break;
      //         }
      //      }
      //
      //      if (program == null) {
      //         setErrorMessage(searchHeaderComponent,
      //            String.format("Invalid program id: [%s]", params.getProgram().getGuid()));
      //         return;
      //      }
      //
      //      Collection<WebId> builds = null;
      //      try {
      //         builds = getBuilds(program);
      //      } catch (Exception ex) {
      //         setErrorMessage(searchHeaderComponent, ex.getMessage());
      //         return;
      //      }
      //      for (WebId b : builds) {
      //         if (b.getGuid().equals(params.getBuild().getGuid())) {
      //            build = b;
      //            break;
      //         }
      //      }
      //
      //      if (build == null) {
      //         setErrorMessage(searchHeaderComponent, String.format("Invalid build id: [%s]", params.getBuild().getGuid()));
      //         return;
      //      }

      String branchGuid;
      try {
         branchGuid = atsArtifactProvider.getBaselineBranchGuid(params.getBuild().getGuid());
      } catch (Exception ex) {
         setErrorMessage(searchHeaderComponent,
            String.format("Cannot resolve branch id from build id: [%s]", params.getBuild().getGuid()));
         return;
      }

      String newUrl = encode(params, branchGuid);
      super.initSearchResults(newUrl, searchHeaderComponent, resultsComponent);

   }

   @Override
   public void selectProgram(WebId program, T headerComponent) {
      headerComponent.clearBuilds();
      Collection<WebId> builds = null;
      try {
         builds = getBuilds(program);
      } catch (Exception ex) {
         setErrorMessage(headerComponent, ex.getMessage());
         return;
      }
      for (WebId build : builds) {
         headerComponent.addBuild(build);
      }
   }

   protected Collection<WebId> getPrograms() throws OseeCoreException {
      Collection<WebId> toReturn = new LinkedList<WebId>();
      Collection<ReadableArtifact> programs = atsArtifactProvider.getPrograms();
      if (programs != null) {
         for (ReadableArtifact program : programs) {
            toReturn.add(new WebId(program.getGuid(), program.getName()));
         }
      }
      return toReturn;
   }

   protected Collection<WebId> getBuilds(WebId program) throws OseeCoreException {
      Collection<ReadableArtifact> relatedBuilds = atsArtifactProvider.getBuilds(program.getGuid());
      Collection<WebId> builds = new ArrayList<WebId>();
      if (relatedBuilds != null) {
         for (ReadableArtifact build : relatedBuilds) {
            builds.add(new WebId(build.getGuid(), build.getName()));
         }
      }
      return builds;
   }

   //   protected String encode(String branchId, boolean nameOnly, String searchPhrase) {
   //      StringBuilder sb = new StringBuilder();
   //      sb.append("/");
   //      sb.append("branch=");
   //      sb.append(branchId);
   //      sb.append("&nameOnly=");
   //      sb.append(nameOnly);
   //      sb.append("&search=");
   //      sb.append(searchPhrase);
   //      return sb.toString().replaceAll("\\s", "%20");
   //   }

   protected String encode(AtsSearchParameters params, String branchId) {
      StringBuilder sb = new StringBuilder();
      sb.append("/");
      if (Strings.isValid(branchId)) {
         sb.append("branch=");
         sb.append(branchId);
         sb.append("&");
      }
      sb.append("program=");
      sb.append(params.getProgram().getGuid());
      sb.append("&build=");
      sb.append(params.getBuild().getGuid());
      sb.append("&nameOnly=");
      sb.append(params.isNameOnly());
      sb.append("&search=");
      sb.append(params.getSearchString());
      sb.append("&verbose=");
      sb.append(params.isVerboseResults());
      return sb.toString().replaceAll("\\s", "%20");
   }

   protected AtsSearchParameters decode(String url) {
      WebId program = null, build = null;
      String searchPhrase = "";
      boolean nameOnly = true;
      boolean verboseResults = true;

      programMatcher.reset(url);
      buildMatcher.reset(url);
      nameOnlyMatcher.reset(url);
      searchPhraseMatcher.reset(url);
      verboseMatcher.reset(url);

      if (programMatcher.find()) {
         program = new WebId(programMatcher.group(1), "");
      }
      if (buildMatcher.find()) {
         build = new WebId(buildMatcher.group(1), "");
      }
      if (nameOnlyMatcher.find()) {
         nameOnly = nameOnlyMatcher.group(1).equalsIgnoreCase("true") ? true : false;
      }
      if (searchPhraseMatcher.find()) {
         searchPhrase = searchPhraseMatcher.group(1).replaceAll("%20", " ");
      }
      if (verboseMatcher.find()) {
         verboseResults = verboseMatcher.group(1).equalsIgnoreCase("true") ? true : false;
      }

      return new AtsSearchParameters(searchPhrase, nameOnly, verboseResults, build, program);
   }

   protected class SearchParameters {

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

      public boolean isValid() {
         return (program != null) && (build != null);
      }

   }

}
