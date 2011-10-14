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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.display.api.components.ArtifactHeaderComponent;
import org.eclipse.osee.display.api.components.AttributeComponent;
import org.eclipse.osee.display.api.components.DisplaysErrorComponent;
import org.eclipse.osee.display.api.components.RelationComponent;
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.api.data.SearchResultMatch;
import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.api.search.ArtifactProvider;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author John Misinco
 */
public class WebSearchPresenter<T extends SearchHeaderComponent> implements SearchPresenter<T> {

   protected final ArtifactProvider artifactProvider;
   private final static Pattern branchPattern = Pattern.compile("branch=([0-9A-Za-z\\+_=]{20,22})");
   private final static Pattern artifactPattern = Pattern.compile("artifact=([0-9A-Za-z\\+_=]{20,22})");
   private final static Pattern nameOnlyPattern = Pattern.compile("nameOnly=(true|false)");
   private final static Pattern searchPhrasePattern = Pattern.compile("search=([\\d\\w%]*)");

   protected final Matcher branchMatcher;
   protected final Matcher artifactMatcher;
   protected final Matcher nameOnlyMatcher;
   protected final Matcher searchPhraseMatcher;

   public WebSearchPresenter(ArtifactProvider artifactProvider) {
      this.artifactProvider = artifactProvider;
      branchMatcher = branchPattern.matcher("");
      artifactMatcher = artifactPattern.matcher("");
      nameOnlyMatcher = nameOnlyPattern.matcher("");
      searchPhraseMatcher = searchPhrasePattern.matcher("");
   }

   @Override
   public void initSearchHome(T searchHeaderComp) {
      searchHeaderComp.clearAll();
   }

   @Override
   public void initSearchResults(String url, T searchHeaderComp, SearchResultsListComponent searchResultsComp) {
      searchResultsComp.clearAll();
      SearchParameters params = decodeSearchUrl(url);
      if (!params.isValid()) {
         setErrorMessage(searchResultsComp, String.format("Invalid url received: %s", url));
         return;
      }
      List<Match<ReadableArtifact, ReadableAttribute<?>>> searchResults = null;
      try {
         searchResults =
            artifactProvider.getSearchResults(TokenFactory.createBranch(params.getBranchId(), ""), params.isNameOnly(),
               params.getSearchPhrase());
      } catch (OseeCoreException ex) {
         setErrorMessage(searchResultsComp, "Error while searching");
         return;
      }
      if (searchResults != null && searchResults.size() > 0) {
         try {
            processSearchResults(searchResults, searchResultsComp);
         } catch (OseeCoreException ex) {
            setErrorMessage(searchResultsComp, "Error while processing results");
         }
      }
   }

   private void processSearchResults(List<Match<ReadableArtifact, ReadableAttribute<?>>> searchResults, SearchResultsListComponent searchResultsComp) throws OseeCoreException {
      for (Match<ReadableArtifact, ReadableAttribute<?>> match : searchResults) {
         ReadableArtifact matchedArtifact = match.getItem();
         List<WebArtifact> ancestry = getAncestry(matchedArtifact);
         WebId webBranch = new WebId(matchedArtifact.getBranch().getGuid(), matchedArtifact.getBranch().getName());
         WebArtifact webArt =
            new WebArtifact(matchedArtifact.getGuid(), matchedArtifact.getName(),
               matchedArtifact.getArtifactType().getName(), ancestry, webBranch);
         SearchResultComponent searchResult = searchResultsComp.createSearchResult();
         searchResult.setArtifact(webArt);
         for (ReadableAttribute<?> element : match.getElements()) {
            List<MatchLocation> locations = match.getLocation(element);
            SearchResultMatch srm =
               new SearchResultMatch(element.getAttributeType().getName(), locations.iterator().next().toString(),
                  locations.size());
            searchResult.addSearchResultMatch(srm);
         }
      }
   }

   @Override
   public void selectArtifact(WebArtifact artifact, SearchNavigator oseeNavigator) {
      oseeNavigator.navigateArtifactPage(encode(artifact));
   }

   @Override
   public void initArtifactPage(String url, SearchHeaderComponent searchHeaderComp, ArtifactHeaderComponent artHeaderComp, RelationComponent relComp, AttributeComponent attrComp) {
      ArtifactParameters params = decodeArtifactUrl(url);

      if (!params.isValid()) {
         setErrorMessage(artHeaderComp, String.format("Invalid url received: %s", url));
         return;
      }

      String branch = params.getBranchId();
      String art = params.getArtifactId();

      ReadableArtifact displayArt = null;
      try {
         displayArt = artifactProvider.getArtifactByGuid(TokenFactory.createBranch(branch, ""), art);
      } catch (OseeCoreException e) {
         setErrorMessage(artHeaderComp, String.format("Error while loading artifact[%s] from branch:[%s]", art, branch));
         return;
      }
      if (displayArt == null) {
         setErrorMessage(artHeaderComp, String.format("No artifact[%s] found on branch:[%s]", art, branch));
         return;
      }
      WebId artBranch = new WebId(displayArt.getBranch().getGuid(), displayArt.getBranch().getName());
      List<WebArtifact> ancestry = getAncestry(displayArt);
      WebArtifact artifact =
         new WebArtifact(displayArt.getGuid(), displayArt.getName(), displayArt.getArtifactType().getName(), ancestry,
            artBranch);
      artHeaderComp.setArtifact(artifact);

      relComp.clearAll();
      Collection<RelationType> relationTypes = null;
      try {
         relationTypes = displayArt.getValidRelationTypes();
      } catch (OseeCoreException ex1) {
         setErrorMessage(relComp, String.format("Error loading relation types for: [%s]", displayArt.getName()));
      }
      for (RelationType relType : relationTypes) {
         relComp.addRelationType(new WebId(relType.getGuid().toString() + ":A", relType.getSideAName()));
         relComp.addRelationType(new WebId(relType.getGuid().toString() + ":B", relType.getSideBName()));
      }

      attrComp.clearAll();
      Collection<IAttributeType> attributeTypes = null;
      try {
         attributeTypes = displayArt.getAttributeTypes();
      } catch (OseeCoreException ex) {
         setErrorMessage(attrComp, String.format("Error loading attributes for: [%s]", displayArt.getName()));
      }
      for (IAttributeType attrType : attributeTypes) {
         List<ReadableAttribute<Object>> attributesValues = null;
         try {
            attributesValues = displayArt.getAttributes(attrType);
            for (ReadableAttribute<Object> value : attributesValues) {
               attrComp.addAttribute(attrType.getName(), value.getDisplayableString());
            }
         } catch (OseeCoreException ex) {
            setErrorMessage(attrComp, String.format("Error loading attributes for: [%s]", displayArt.getName()));
         }
      }
   }

   @Override
   public void selectRelationType(WebArtifact artifact, WebId relation, RelationComponent relationComponent) {
      relationComponent.clearRelations();
      //artifactProvider.getRelatedArtifacts(artifact.getGuid(), side)
   }

   protected String encode(WebArtifact artifact) {
      StringBuilder sb = new StringBuilder();
      sb.append("/branch=");
      sb.append(artifact.getBranch().getGuid());
      sb.append("?artifact=");
      sb.append(artifact.getGuid());
      return sb.toString();
   }

   protected String encode(WebId branch, boolean nameOnly, String searchPhrase) {
      StringBuilder sb = new StringBuilder();
      sb.append("/branch=");
      sb.append(branch.getGuid());
      sb.append("?nameOnly=");
      sb.append(nameOnly);
      sb.append("?search=");
      sb.append(searchPhrase);
      return sb.toString().replaceAll("\\s", "%20");
   }

   protected List<WebArtifact> getAncestry(ReadableArtifact art) {
      ReadableArtifact cur = art.getParent();
      List<WebArtifact> ancestry = new ArrayList<WebArtifact>();
      while (cur != null) {
         WebId branch = new WebId(cur.getBranch().getGuid(), cur.getBranch().getName());
         ancestry.add(new WebArtifact(cur.getGuid(), cur.getName(), cur.getArtifactType().getName(), getAncestry(cur),
            branch));
         if (cur.hasParent()) {
            cur = cur.getParent();
         } else {
            break;
         }
      }
      return ancestry;
   }

   protected void setErrorMessage(DisplaysErrorComponent component, String message) {
      component.setErrorMessage(message);
   }

   private ArtifactParameters decodeArtifactUrl(String url) {
      String branch = "";
      String artifact = "";

      branchMatcher.reset(url);
      artifactMatcher.reset(url);

      if (branchMatcher.find()) {
         branch = branchMatcher.group(1);
      }
      if (artifactMatcher.find()) {
         artifact = artifactMatcher.group(1);
      }
      return new ArtifactParameters(branch, artifact);
   }

   private SearchParameters decodeSearchUrl(String url) {
      String branch = "";
      boolean nameOnly = true;
      String searchPhrase = "";

      branchMatcher.reset(url);
      nameOnlyMatcher.reset(url);
      searchPhraseMatcher.reset(url);

      if (branchMatcher.find()) {
         branch = branchMatcher.group(1);
      }
      if (nameOnlyMatcher.find()) {
         nameOnly = nameOnlyMatcher.group(1).equalsIgnoreCase("true") ? true : false;
      }
      if (searchPhraseMatcher.find()) {
         searchPhrase = searchPhraseMatcher.group(1).replaceAll("%20", " ");
      }
      return new SearchParameters(branch, nameOnly, searchPhrase);
   }

   private class ArtifactParameters {
      private final String branchId;
      private final String artifactId;

      public ArtifactParameters(String branchId, String artifactId) {
         this.branchId = branchId;
         this.artifactId = artifactId;
      }

      public String getBranchId() {
         return branchId;
      }

      public String getArtifactId() {
         return artifactId;
      }

      public boolean isValid() {
         return Strings.isValid(branchId) && Strings.isValid(artifactId);
      }
   }

   private class SearchParameters {

      private final String branchId;
      private final boolean nameOnly;
      private final String searchPhrase;

      public SearchParameters(String branchId, boolean nameOnly, String searchPhrase) {
         this.branchId = branchId;
         this.nameOnly = nameOnly;
         this.searchPhrase = searchPhrase;
      }

      public String getBranchId() {
         return branchId;
      }

      public boolean isNameOnly() {
         return nameOnly;
      }

      public String getSearchPhrase() {
         return searchPhrase;
      }

      public boolean isValid() {
         return Strings.isValid(branchId);
      }
   }

}
