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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.osee.ats.ui.api.data.AtsSearchParameters;
import org.eclipse.osee.ats.ui.api.search.AtsArtifactProvider;
import org.eclipse.osee.ats.ui.api.search.AtsSearchPresenter;
import org.eclipse.osee.ats.ui.api.view.AtsSearchHeaderComponent;
import org.eclipse.osee.display.api.components.ArtifactHeaderComponent;
import org.eclipse.osee.display.api.components.AttributeComponent;
import org.eclipse.osee.display.api.components.DisplayOptionsComponent;
import org.eclipse.osee.display.api.components.RelationComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.api.data.ViewId;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.presenter.SearchPresenterImpl;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.UrlQuery;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author John R. Misinco
 */
public class AtsSearchPresenterImpl<T extends AtsSearchHeaderComponent, K extends AtsSearchParameters> extends SearchPresenterImpl<T, K> implements AtsSearchPresenter<T, K> {

   private final AtsArtifactProvider atsArtifactProvider;

   public AtsSearchPresenterImpl(AtsArtifactProvider artifactProvider, Log logger) {
      super(artifactProvider, logger);
      atsArtifactProvider = artifactProvider;
   }

   @Override
   public void selectSearch(String url, K params, SearchNavigator atsNavigator) {
      String newUrl = encode("", params, null);
      atsNavigator.navigateSearchResults(newUrl);
   }

   private void addProgramsToSearchHeader(T headerComponent) {
      headerComponent.clearAll();
      Collection<ViewId> programs = null;
      try {
         programs = getPrograms();
      } catch (Exception ex) {
         setErrorMessage(headerComponent, "Error in addProgramsToSearchHeader", ex);
         return;
      }
      for (ViewId program : programs) {
         headerComponent.addProgram(program);
      }
   }

   @Override
   public void initSearchResults(String url, T searchHeaderComponent, SearchResultsListComponent resultsComponent, DisplayOptionsComponent optionsComponent) {
      setSearchHeaderFields(url, searchHeaderComponent);
      resultsComponent.clearAll();

      if (!Strings.isValid(url)) {
         sendSearchCompleted();
         return;
      }

      AtsSearchParameters params = decodeIt(url);

      if (params == null || !params.isValid()) {
         return;
      }

      Long branchUuid;
      try {
         branchUuid = atsArtifactProvider.getBaselineBranchUuid(params.getBuild().getGuid());
      } catch (Exception ex) {
         setErrorMessage(searchHeaderComponent, "Error in initSearchResults", ex);
         return;
      }

      if (branchUuid == null) {
         setErrorMessage(resultsComponent, "Could not find baseline branch guid for selected build/program", null);
      } else {
         String newUrl = encode(url, params, String.valueOf(branchUuid));
         super.initSearchResults(newUrl, searchHeaderComponent, resultsComponent, optionsComponent);
      }
   }

   @Override
   public void selectProgram(ViewId program, T headerComponent) {
      headerComponent.clearBuilds();
      Collection<ViewId> builds = null;
      if (program != null) {
         try {
            builds = getBuilds(program);
         } catch (Exception ex) {
            setErrorMessage(headerComponent, "Error in selectProgram", ex);
            return;
         }
         for (ViewId build : builds) {
            headerComponent.addBuild(build);
         }
      }
   }

   protected Collection<ViewId> getPrograms() throws OseeCoreException {
      Collection<ViewId> toReturn = new LinkedList<ViewId>();
      Iterable<ArtifactReadable> programs = atsArtifactProvider.getPrograms();
      if (programs != null) {
         for (ArtifactReadable program : programs) {
            toReturn.add(new ViewId(program.getGuid(), program.getName()));
         }
      }
      return toReturn;
   }

   protected Collection<ViewId> getBuilds(ViewId program) throws OseeCoreException {
      Iterable<ArtifactReadable> relatedBuilds = atsArtifactProvider.getBuilds(program.getGuid());
      Collection<ViewId> builds = new ArrayList<ViewId>();
      if (relatedBuilds != null) {
         for (ArtifactReadable build : relatedBuilds) {
            builds.add(new ViewId(build.getGuid(), build.getName()));
         }
      }
      return builds;
   }

   protected String encode(String url, AtsSearchParameters searchParams, String branchId) {
      UrlQuery query = new UrlQuery();
      try {
         query.parse(url);

         if (Strings.isValid(branchId)) {
            query.putInPlace("branch", branchId);
         }

         query.putInPlace("program", searchParams.getProgram().getGuid());
         query.putInPlace("build", searchParams.getBuild().getGuid());
         query.putInPlace("nameOnly", String.valueOf(searchParams.isNameOnly()));
         query.putInPlace("search", searchParams.getSearchString());

         return "/" + query.toString();
      } catch (UnsupportedEncodingException ex) {
         logger.error(ex, "Error in encode");
         return "";
      }
   }

   protected AtsSearchParameters decodeIt(String url) {
      UrlQuery query = new UrlQuery();
      try {
         query.parse(url);
      } catch (UnsupportedEncodingException ex) {
         logger.error(ex, "Error in encode");
         return null;
      }

      ViewId program = null, build = null;

      if (query.containsKey("program")) {
         program = new ViewId(query.getParameter("program"), "");
      }

      if (query.containsKey("build")) {
         build = new ViewId(query.getParameter("build"), "");
      }

      String nValue = query.getParameter("nameOnly");
      boolean nameOnly = nValue == null ? false : nValue.equalsIgnoreCase("true");
      String searchPhrase = "";
      if (query.containsKey("search")) {
         searchPhrase = query.getParameter("search");
      }

      //      String vValue = data.get("verbose");
      //      boolean verbose = vValue == null ? false : vValue.equalsIgnoreCase("true");
      return new AtsSearchParameters(searchPhrase, nameOnly, build, program);
   }

   protected void setSearchHeaderFields(String url, T searchHeaderComp) {
      searchHeaderComp.clearAll();
      addProgramsToSearchHeader(searchHeaderComp);

      if (!Strings.isValid(url)) {
         return;
      }

      AtsSearchParameters params = decodeIt(url);
      if (!params.isValid()) {
         return;
      }

      selectProgram(params.getProgram(), searchHeaderComp);
      searchHeaderComp.setSearchCriteria(params);

   }

   @Override
   public void initArtifactPage(String url, T searchHeaderComp, ArtifactHeaderComponent artHeaderComp, RelationComponent relComp, AttributeComponent attrComp, DisplayOptionsComponent options) {
      setSearchHeaderFields(url, searchHeaderComp);
      super.initArtifactPage(url, searchHeaderComp, artHeaderComp, relComp, attrComp, options);
   }

}
