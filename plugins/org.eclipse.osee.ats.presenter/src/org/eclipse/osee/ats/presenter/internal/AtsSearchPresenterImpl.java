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
import java.util.Map;
import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponent;
import org.eclipse.osee.ats.api.data.AtsSearchParameters;
import org.eclipse.osee.ats.api.search.AtsArtifactProvider;
import org.eclipse.osee.ats.api.search.AtsSearchPresenter;
import org.eclipse.osee.display.api.components.ArtifactHeaderComponent;
import org.eclipse.osee.display.api.components.AttributeComponent;
import org.eclipse.osee.display.api.components.DisplayOptionsComponent;
import org.eclipse.osee.display.api.components.RelationComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.api.data.ViewId;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.presenter.SearchPresenterImpl;
import org.eclipse.osee.display.presenter.Utility;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ReadableArtifact;

/**
 * @author John Misinco
 */
public class AtsSearchPresenterImpl<T extends AtsSearchHeaderComponent, K extends AtsSearchParameters> extends SearchPresenterImpl<T, K> implements AtsSearchPresenter<T, K> {

   private final AtsArtifactProvider atsArtifactProvider;

   public AtsSearchPresenterImpl(AtsArtifactProvider artifactProvider) {
      super(artifactProvider);
      atsArtifactProvider = artifactProvider;
   }

   @Override
   public void selectSearch(String url, K params, SearchNavigator atsNavigator) {
      String newUrl = encode(url, params, null);
      atsNavigator.navigateSearchResults(newUrl);
   }

   private void addProgramsToSearchHeader(T headerComponent) {
      headerComponent.clearAll();
      Collection<ViewId> programs = null;
      try {
         programs = getPrograms();
      } catch (Exception ex) {
         setErrorMessage(headerComponent, ex.getMessage());
         return;
      }
      for (ViewId program : programs) {
         headerComponent.addProgram(program);
      }
   }

   @Override
   public void initSearchResults(String url, T searchHeaderComponent, SearchResultsListComponent resultsComponent, DisplayOptionsComponent optionsComponent) {
      setSearchHeaderFields(url, searchHeaderComponent);

      if (!Strings.isValid(url)) {
         return;
      }

      AtsSearchParameters params = decodeIt(url);

      if (!params.isValid()) {
         setErrorMessage(searchHeaderComponent, String.format("Invalid url received: %s", url));
         return;
      }

      String branchGuid;
      try {
         branchGuid = atsArtifactProvider.getBaselineBranchGuid(params.getBuild().getGuid());
      } catch (Exception ex) {
         setErrorMessage(searchHeaderComponent,
            String.format("Cannot resolve branch id from build id: [%s]", params.getBuild().getGuid()));
         return;
      }

      String newUrl = encode(url, params, branchGuid);
      super.initSearchResults(newUrl, searchHeaderComponent, resultsComponent, optionsComponent);

   }

   @Override
   public void selectProgram(ViewId program, T headerComponent) {
      headerComponent.clearBuilds();
      Collection<ViewId> builds = null;
      try {
         builds = getBuilds(program);
      } catch (Exception ex) {
         setErrorMessage(headerComponent, ex.getMessage());
         return;
      }
      for (ViewId build : builds) {
         headerComponent.addBuild(build);
      }
   }

   protected Collection<ViewId> getPrograms() throws OseeCoreException {
      Collection<ViewId> toReturn = new LinkedList<ViewId>();
      Collection<ReadableArtifact> programs = atsArtifactProvider.getPrograms();
      if (programs != null) {
         for (ReadableArtifact program : programs) {
            toReturn.add(new ViewId(program.getGuid(), program.getName()));
         }
      }
      return toReturn;
   }

   protected Collection<ViewId> getBuilds(ViewId program) throws OseeCoreException {
      Collection<ReadableArtifact> relatedBuilds = atsArtifactProvider.getBuilds(program.getGuid());
      Collection<ViewId> builds = new ArrayList<ViewId>();
      if (relatedBuilds != null) {
         for (ReadableArtifact build : relatedBuilds) {
            builds.add(new ViewId(build.getGuid(), build.getName()));
         }
      }
      return builds;
   }

   protected String encode(String url, AtsSearchParameters searchParams, String branchId) {
      Map<String, String> params = Utility.decode(url);
      if (Strings.isValid(branchId)) {
         params.put("branch", branchId);
      }
      params.put("program", searchParams.getProgram().getGuid());
      params.put("build", searchParams.getBuild().getGuid());
      params.put("nameOnly", String.valueOf(searchParams.isNameOnly()));
      params.put("search", searchParams.getSearchString());
      try {
         return "/" + getParametersAsEncodedUrl(params);
      } catch (UnsupportedEncodingException ex) {
         return "";
      }
   }

   protected AtsSearchParameters decodeIt(String url) {
      Map<String, String> data = Utility.decode(url);

      ViewId program = new ViewId(data.get("program"), "");
      ViewId build = new ViewId(data.get("build"), "");

      String nValue = data.get("nameOnly");
      boolean nameOnly = nValue == null ? false : nValue.equalsIgnoreCase("true");
      String searchPhrase = data.get("search");

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
         setErrorMessage(searchHeaderComp, String.format("Invalid url received: %s", url));
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
