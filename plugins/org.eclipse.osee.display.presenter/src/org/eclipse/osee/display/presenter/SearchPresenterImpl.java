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
package org.eclipse.osee.display.presenter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.display.api.components.ArtifactHeaderComponent;
import org.eclipse.osee.display.api.components.AttributeComponent;
import org.eclipse.osee.display.api.components.DisplayOptionsComponent;
import org.eclipse.osee.display.api.components.DisplaysErrorComponent;
import org.eclipse.osee.display.api.components.DisplaysErrorComponent.MsgType;
import org.eclipse.osee.display.api.components.RelationComponent;
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.api.data.DisplayOptions;
import org.eclipse.osee.display.api.data.SearchResultMatch;
import org.eclipse.osee.display.api.data.StyledText;
import org.eclipse.osee.display.api.data.ViewArtifact;
import org.eclipse.osee.display.api.data.ViewId;
import org.eclipse.osee.display.api.data.ViewSearchParameters;
import org.eclipse.osee.display.api.search.ArtifactProvider;
import org.eclipse.osee.display.api.search.AsyncSearchListener;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.display.api.search.SearchProgressListener;
import org.eclipse.osee.display.api.search.SearchProgressProvider;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.UrlQuery;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;
import com.google.common.collect.Iterables;

/**
 * @author John R. Misinco
 */
public class SearchPresenterImpl<T extends SearchHeaderComponent, K extends ViewSearchParameters> implements SearchPresenter<T, K>, SearchProgressProvider {

   protected final ArtifactProvider artifactProvider;

   private final static String SIDE_A_KEY = "sideAName";
   private final static String SIDE_B_KEY = "sideBName";
   protected final Log logger;
   private final AsyncSearchHandler searchHandler = new AsyncSearchHandler();
   protected final Set<SearchProgressListener> searchListeners = new HashSet<SearchProgressListener>();

   public SearchPresenterImpl(ArtifactProvider artifactProvider, Log logger) {
      this.artifactProvider = artifactProvider;
      this.logger = logger;
   }

   @Override
   public void initSearchResults(String url, T searchHeaderComp, SearchResultsListComponent searchResultsComp, DisplayOptionsComponent options) {
      artifactProvider.cancelSearch();
      SearchParameters params = null;
      try {
         params = decodeSearchUrl(url);
      } catch (UnsupportedEncodingException ex) {
         setErrorMessage(searchResultsComp, "Error parsing url", ex);
      }

      if (!Strings.isValid(url) || params == null || !params.isValid()) {
         sendSearchCompleted();
         return;
      }

      options.setDisplayOptions(new DisplayOptions(params.isVerbose()));

      try {
         searchHandler.setSearchValues(searchResultsComp, params.isVerbose());
         artifactProvider.getSearchResults(TokenFactory.createBranch(params.getBranchUuid(), ""), params.isNameOnly(),
            params.getSearchPhrase(), searchHandler);
      } catch (Exception ex) {
         setErrorMessage(searchResultsComp, "Error loading search results", ex);
      }
      searchResultsComp.clearAll();
      sendSearchInProgress();
   }

   private void processSearchResults(Iterable<Match<ArtifactReadable, AttributeReadable<?>>> searchResults, SearchResultsListComponent searchResultsComp, boolean isVerbose) throws OseeCoreException {
      searchResultsComp.clearAll();
      if (searchResults != null && Iterables.isEmpty(searchResults)) {
         searchResultsComp.noSearchResultsFound();
      } else {
         for (Match<ArtifactReadable, AttributeReadable<?>> match : searchResults) {
            ArtifactReadable matchedArtifact = match.getItem();
            ViewArtifact viewArtifact = convertToViewArtifact(matchedArtifact, isVerbose);

            SearchResultComponent searchResult = searchResultsComp.createSearchResult();
            searchResult.setArtifact(viewArtifact);
            if (isVerbose) {
               for (AttributeReadable<?> element : match.getElements()) {
                  List<MatchLocation> matches = match.getLocation(element);
                  String data = String.valueOf(element.getDisplayableString());
                  List<StyledText> text = Utility.getMatchedText(data, matches);
                  SearchResultMatch srm =
                     new SearchResultMatch(element.getAttributeType().getName(), matches.size(), text);
                  searchResult.addSearchResultMatch(srm);
               }
            }
         }
      }
      sendSearchCompleted();
   }

   @Override
   public void selectArtifact(String url, ViewArtifact artifact, SearchNavigator oseeNavigator) {
      try {
         UrlQuery query = new UrlQuery();
         query.parse(url);
         query.putInPlace("branch", artifact.getBranch().getGuid());
         query.putInPlace("artifact", artifact.getGuid());

         String value = query.toUrl();
         oseeNavigator.navigateArtifactPage("/" + value);
      } catch (UnsupportedEncodingException ex) {
         logger.error(ex, "Error in Encoding url in selectArtifact");
      }
   }

   @Override
   public void initArtifactPage(String url, T searchHeaderComp, ArtifactHeaderComponent artHeaderComp, RelationComponent relComp, AttributeComponent attrComp, DisplayOptionsComponent options) {
      if (!Strings.isValid(url)) {
         return;
      }

      ArtifactParameters params = null;
      try {
         params = decodeArtifactUrl(url);
      } catch (UnsupportedEncodingException ex1) {
         setErrorMessage(artHeaderComp, String.format("Invalid url received: %s", url), ex1);
      }
      if (params == null || !params.isValid()) {
         setErrorMessage(artHeaderComp, String.format("Invalid url received: %s", url), null);
         return;
      }

      long branch = params.getBranchUuid();
      String art = params.getArtifactId();
      ArtifactReadable displayArt = null;
      try {
         displayArt = artifactProvider.getArtifactByGuid(TokenFactory.createBranch(branch, ""), art);
      } catch (Exception ex) {
         setErrorMessage(artHeaderComp, "Error finding artifact", ex);
         return;
      }
      if (displayArt == null) {
         setErrorMessage(artHeaderComp, String.format("No artifact[%s] found on branch:[%s]", art, branch), null);
         return;
      }

      ViewArtifact artifact = null;
      try {
         artifact = convertToViewArtifact(displayArt, true);
      } catch (Exception ex) {
         setErrorMessage(artHeaderComp, "Error in initArtifactPage", ex);
         return;
      }

      artHeaderComp.setArtifact(artifact);

      relComp.clearAll();
      relComp.setArtifact(artifact);

      try {
         Collection<? extends IRelationType> relationTypes = artifactProvider.getValidRelationTypes(displayArt);
         for (IRelationType relType : relationTypes) {
            ViewId toAdd = new ViewId(relType.getGuid().toString(), relType.getName());
            toAdd.setAttribute(SIDE_A_KEY, artifactProvider.getSideAName(relType));
            toAdd.setAttribute(SIDE_B_KEY, artifactProvider.getSideBName(relType));
            relComp.addRelationType(toAdd);
         }
      } catch (Exception ex) {
         setErrorMessage(relComp, "Error in initArtifactPage:\n Cannot load valid relation types", ex);
         return;
      }

      attrComp.clearAll();
      Collection<IAttributeType> attributeTypes = null;
      try {
         attributeTypes = AttributeTypeUtil.getTypesWithData(displayArt);
      } catch (Exception ex) {
         setErrorMessage(attrComp, "Error in initArtifactPage:\n Cannot load attribute types", ex);
         return;
      }
      for (IAttributeType attrType : attributeTypes) {
         try {
            for (AttributeReadable<Object> value : displayArt.getAttributes(attrType)) {
               attrComp.addAttribute(attrType.getName(), value.getDisplayableString());
            }
         } catch (Exception ex) {
            setErrorMessage(attrComp, "Error in initArtifactPage:\n Cannot load attribute values", ex);
            return;
         }
      }
   }

   @Override
   public void selectRelationType(ViewArtifact artifact, ViewId relation, RelationComponent relationComponent) {
      relationComponent.clearRelations();
      if (artifact == null || relation == null) {
         setErrorMessage(relationComponent, "Error: Null detected in selectRelationType parameters", null);
         return;
      }
      String relGuid = relation.getGuid();

      IRelationType type = TokenFactory.createRelationType(Long.parseLong(relGuid), relation.getName());
      IOseeBranch branch = TokenFactory.createBranch(Long.valueOf(artifact.getBranch().getGuid()), "");
      ArtifactReadable sourceArt;
      ResultSet<ArtifactReadable> relatedSideA;
      ResultSet<ArtifactReadable> relatedSideB;
      try {
         sourceArt = artifactProvider.getArtifactByGuid(branch, artifact.getGuid());
         relatedSideA =
            artifactProvider.getRelatedArtifacts(sourceArt,
               TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, type.getGuid(), type.getName()));
         relatedSideB =
            artifactProvider.getRelatedArtifacts(sourceArt,
               TokenFactory.createRelationTypeSide(RelationSide.SIDE_B, type.getGuid(), type.getName()));
      } catch (Exception ex) {
         setErrorMessage(relationComponent, "Error in selectRelationType", ex);
         return;
      }

      String leftSideName = Strings.capitalize(relation.getAttribute(SIDE_A_KEY));
      String rightSideName = Strings.capitalize(relation.getAttribute(SIDE_B_KEY));
      relationComponent.setLeftName(leftSideName);
      relationComponent.setRightName(rightSideName);

      if (relatedSideA.isEmpty()) {
         relationComponent.addLeftRelated(null);
      }
      if (relatedSideB.isEmpty()) {
         relationComponent.addRightRelated(null);
      }

      try {
         for (ArtifactReadable rel : relatedSideA) {
            ViewArtifact id = convertToViewArtifact(rel, false);
            relationComponent.addLeftRelated(id);
         }
         for (ArtifactReadable rel : relatedSideB) {
            ViewArtifact id = convertToViewArtifact(rel, false);
            relationComponent.addRightRelated(id);
         }
      } catch (Exception ex) {
         setErrorMessage(relationComponent, "Error in selectRelationType", ex);
         return;
      }
   }

   protected ViewArtifact convertToViewArtifact(ArtifactReadable artifact, boolean addAncestry) throws OseeCoreException {
      ViewId branch = new ViewId(String.valueOf(artifact.getBranch().getGuid()), artifact.getBranch().getName());
      List<ViewArtifact> ancestry = addAncestry ? getAncestry(artifact) : null;
      ViewArtifact toReturn =
         new ViewArtifact(artifact.getGuid(), artifact.getName(), artifact.getArtifactType().getName(), ancestry,
            branch);
      return toReturn;
   }

   protected List<ViewArtifact> getAncestry(ArtifactReadable art) throws OseeCoreException {
      ArtifactReadable cur = artifactProvider.getParent(art);
      List<ViewArtifact> ancestry = new ArrayList<ViewArtifact>();
      while (cur != null) {
         ancestry.add(convertToViewArtifact(cur, false));
         cur = artifactProvider.getParent(cur);
      }
      return ancestry;
   }

   protected void setErrorMessage(DisplaysErrorComponent component, String message, Throwable ex) {
      if (component != null) {
         String longMsg = "No Details";
         if (ex != null) {
            longMsg = Lib.exceptionToString(ex);
            logger.error(ex, message);
         }
         component.setErrorMessage(message, longMsg, MsgType.MSGTYPE_ERROR);
      }
   }

   private ArtifactParameters decodeArtifactUrl(String url) throws UnsupportedEncodingException {
      UrlQuery query = new UrlQuery();
      query.parse(url);
      String branchId = query.getParameter("branch");
      long branch = 0;
      if (Strings.isValid(branchId)) {
         branch = Long.valueOf(branchId);
      }
      String artifact = query.getParameter("artifact");
      return new ArtifactParameters(branch, artifact);
   }

   private SearchParameters decodeSearchUrl(String url) throws UnsupportedEncodingException {
      UrlQuery query = new UrlQuery();
      query.parse(url);
      long branch = Long.valueOf(query.getParameter("branch"));
      String vValue = query.getParameter("verbose");
      boolean verbose = vValue == null ? false : vValue.equalsIgnoreCase("true");
      String nValue = query.getParameter("nameOnly");
      boolean nameOnly = nValue == null ? false : nValue.equalsIgnoreCase("true");
      String searchPhrase = query.getParameter("search");
      return new SearchParameters(branch, nameOnly, searchPhrase, verbose);
   }

   @Override
   public void selectDisplayOptions(String url, DisplayOptions options, SearchNavigator navigator) {
      UrlQuery query = new UrlQuery();
      try {
         query.parse(url);
         query.putInPlace("verbose", options.getVerboseResults());
         String newUrl = query.toUrl();
         navigator.navigateSearchResults("/" + newUrl);
      } catch (UnsupportedEncodingException ex) {
         logger.error(ex, "Error in Encoding url in selectArtifact");
      }
   }

   @Override
   public void selectSearch(String url, K params, SearchNavigator navigator) {
      //do nothing for now
   }

   @Override
   public void selectCancel() {
      artifactProvider.cancelSearch();
   }

   protected void sendSearchCancelled() {
      for (SearchProgressListener listener : searchListeners) {
         listener.searchCancelled();
      }
   }

   protected void sendSearchInProgress() {
      for (SearchProgressListener listener : searchListeners) {
         listener.searchInProgress();
      }
   }

   protected void sendSearchCompleted() {
      for (SearchProgressListener listener : searchListeners) {
         listener.searchCompleted();
      }
   }

   protected void sendSearchFailed(Throwable throwable, SearchResultsListComponent component) {
      setErrorMessage(component, "Search failed", throwable);
      for (SearchProgressListener listener : searchListeners) {
         listener.searchCancelled();
      }
   }

   @Override
   public void addListener(SearchProgressListener listener) {
      searchListeners.add(listener);
   }

   @Override
   public void removeListener(SearchProgressListener listener) {
      searchListeners.remove(listener);
   }

   private class AsyncSearchHandler implements AsyncSearchListener {
      private SearchResultsListComponent resultsComp;
      private boolean isVerbose;

      public void setSearchValues(SearchResultsListComponent resultsComp, boolean isVerbose) {
         this.resultsComp = resultsComp;
         this.isVerbose = isVerbose;
      }

      @Override
      public void onSearchComplete(Iterable<Match<ArtifactReadable, AttributeReadable<?>>> results) {
         try {
            processSearchResults(results, resultsComp, isVerbose);
         } catch (OseeCoreException ex) {
            setErrorMessage(resultsComp, "Error processing results", ex);
         }
      }

      @Override
      public void onSearchCancelled() {
         sendSearchCancelled();
      }

      @Override
      public void onSearchFailed(Throwable throwable) {
         sendSearchFailed(throwable, resultsComp);
      }

   }

   private class ArtifactParameters {
      private final long branchUuid;
      private final String artifactId;

      public ArtifactParameters(long branchUuid, String artifactId) {
         this.branchUuid = branchUuid;
         this.artifactId = artifactId;
      }

      public long getBranchUuid() {
         return branchUuid;
      }

      public String getArtifactId() {
         return artifactId;
      }

      public boolean isValid() {
         return branchUuid > 0 && Strings.isValid(artifactId);
      }
   }

   private class SearchParameters {

      private final long branchId;
      private final boolean nameOnly, verbose;
      private final String searchPhrase;

      public SearchParameters(long branchUuid, boolean nameOnly, String searchPhrase, boolean verbose) {
         this.branchId = branchUuid;
         this.nameOnly = nameOnly;
         this.searchPhrase = searchPhrase;
         this.verbose = verbose;
      }

      public boolean isVerbose() {
         return verbose;
      }

      public long getBranchUuid() {
         return branchId;
      }

      public boolean isNameOnly() {
         return nameOnly;
      }

      public String getSearchPhrase() {
         return searchPhrase;
      }

      public boolean isValid() {
         return branchId > 0;
      }
   }

}
