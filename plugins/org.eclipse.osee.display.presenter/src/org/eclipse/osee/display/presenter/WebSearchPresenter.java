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
import java.util.Collections;
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
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.RelationSide;
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
   private final static Pattern verbosePattern = Pattern.compile("verbose=(true|false)");

   private final static String SIDE_A_KEY = "sideAName";
   private final static String SIDE_B_KEY = "sideBName";

   protected final Matcher branchMatcher;
   protected final Matcher artifactMatcher;
   protected final Matcher nameOnlyMatcher;
   protected final Matcher searchPhraseMatcher;
   protected final Matcher verboseMatcher;

   public WebSearchPresenter(ArtifactProvider artifactProvider) {
      this.artifactProvider = artifactProvider;
      branchMatcher = branchPattern.matcher("");
      artifactMatcher = artifactPattern.matcher("");
      nameOnlyMatcher = nameOnlyPattern.matcher("");
      searchPhraseMatcher = searchPhrasePattern.matcher("");
      verboseMatcher = verbosePattern.matcher("");
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
      } catch (Exception ex) {
         setErrorMessage(searchResultsComp, "An error occured while searching");
         return;
      }
      if (searchResults != null && searchResults.size() > 0) {
         try {
            processSearchResults(searchResults, searchResultsComp);
         } catch (Exception ex) {
            setErrorMessage(searchResultsComp, "Error while processing results");
            return;
         }
      }
   }

   private void processSearchResults(List<Match<ReadableArtifact, ReadableAttribute<?>>> searchResults, SearchResultsListComponent searchResultsComp) throws OseeCoreException {
      for (Match<ReadableArtifact, ReadableAttribute<?>> match : searchResults) {
         ReadableArtifact matchedArtifact = match.getItem();
         WebArtifact webArt = convertToWebArtifact(matchedArtifact);
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
      } catch (Exception e) {
         setErrorMessage(artHeaderComp, String.format("Error while loading artifact[%s] from branch:[%s]", art, branch));
         return;
      }
      if (displayArt == null) {
         setErrorMessage(artHeaderComp, String.format("No artifact[%s] found on branch:[%s]", art, branch));
         return;
      }
      WebArtifact artifact = convertToWebArtifact(displayArt);
      artHeaderComp.setArtifact(artifact);

      relComp.clearAll();
      relComp.setArtifact(artifact);
      Collection<RelationType> relationTypes = null;
      try {
         relationTypes = displayArt.getValidRelationTypes();
      } catch (Exception e) {
         setErrorMessage(relComp, String.format("Error loading relation types for: [%s]", displayArt.getName()));
         return;
      }
      for (RelationType relType : relationTypes) {
         WebId toAdd = new WebId(relType.getGuid().toString(), relType.getName());
         toAdd.setAttribute(SIDE_A_KEY, relType.getSideAName());
         toAdd.setAttribute(SIDE_B_KEY, relType.getSideBName());
         relComp.addRelationType(toAdd);
      }

      attrComp.clearAll();
      Collection<IAttributeType> attributeTypes = null;
      try {
         attributeTypes = displayArt.getAttributeTypes();
      } catch (Exception ex) {
         setErrorMessage(attrComp, String.format("Error loading attributes for: [%s]", displayArt.getName()));
         return;
      }
      for (IAttributeType attrType : attributeTypes) {
         List<ReadableAttribute<Object>> attributesValues = null;
         try {
            attributesValues = displayArt.getAttributes(attrType);
            for (ReadableAttribute<Object> value : attributesValues) {
               attrComp.addAttribute(attrType.getName(), value.getDisplayableString());
            }
         } catch (Exception ex) {
            setErrorMessage(attrComp, String.format("Error loading attributes for: [%s]", displayArt.getName()));
            return;
         }
      }
   }

   @Override
   public void selectRelationType(WebArtifact artifact, WebId relation, RelationComponent relationComponent) {
      relationComponent.clearRelations();
      if (artifact == null || relation == null) {
         setErrorMessage(relationComponent, "Error: Null detected in selectRelationType parameters");
         return;
      }
      String relGuid = relation.getGuid();

      IRelationType type = TokenFactory.createRelationType(Long.parseLong(relGuid), relation.getName());
      IOseeBranch branch = TokenFactory.createBranch(artifact.getBranch().getGuid(), "");
      ReadableArtifact sourceArt;
      Collection<ReadableArtifact> relatedSideA = null;
      Collection<ReadableArtifact> relatedSideB = null;
      Collection<ReadableArtifact> related = null;
      try {
         sourceArt = artifactProvider.getArtifactByGuid(branch, artifact.getGuid());
         for (RelationSide side : RelationSide.values()) {
            related =
               sourceArt.getRelatedArtifacts(TokenFactory.createRelationTypeSide(side, type.getGuid(), type.getName()),
                  null);
            if (side.isSideA()) {
               relatedSideA = related;
            } else {
               relatedSideB = related;
            }
         }
      } catch (Exception ex) {
         setErrorMessage(relationComponent,
            String.format("Error loading relations for artifact[%s]", artifact.getGuid()));
         return;
      }

      Collection<ReadableArtifact> relatedLeftSide = null;
      Collection<ReadableArtifact> relatedRightSide = null;
      String leftSideName = null, rightSideName = null;
      if (relatedSideA.size() == 0 && relatedSideB.size() != 0) {
         relatedRightSide = relatedSideB;
         relatedLeftSide = Collections.emptyList();
         rightSideName = relation.getAttribute(SIDE_B_KEY);
      } else if (relatedSideA.size() != 0 && relatedSideB.size() == 0) {
         relatedRightSide = relatedSideA;
         relatedLeftSide = Collections.emptyList();
         rightSideName = relation.getAttribute(SIDE_A_KEY);
      } else if (relatedSideA.size() == 0 && relatedSideB.size() == 0) {
         relatedRightSide = Collections.emptyList();
         relatedLeftSide = Collections.emptyList();
      } else {
         relatedRightSide = relatedSideA;
         relatedLeftSide = relatedSideB;
         rightSideName = relation.getAttribute(SIDE_A_KEY);
         leftSideName = relation.getAttribute(SIDE_B_KEY);
      }

      relationComponent.setLeftName(leftSideName);
      relationComponent.setRightName(rightSideName);

      for (ReadableArtifact rel : relatedLeftSide) {
         WebArtifact id = convertToWebArtifact(rel);
         relationComponent.addLeftRelated(id);
      }
      for (ReadableArtifact rel : relatedRightSide) {
         WebArtifact id = convertToWebArtifact(rel);
         relationComponent.addRightRelated(id);
      }
   }

   protected WebArtifact convertToWebArtifact(ReadableArtifact artifact) {
      WebId branch = new WebId(artifact.getBranch().getGuid(), artifact.getBranch().getName());
      WebArtifact toReturn =
         new WebArtifact(artifact.getGuid(), artifact.getName(), artifact.getArtifactType().getName(),
            getAncestry(artifact), branch);
      return toReturn;
   }

   protected String encode(WebArtifact artifact) {
      StringBuilder sb = new StringBuilder();
      sb.append("/branch=");
      sb.append(artifact.getBranch().getGuid());
      sb.append("&artifact=");
      sb.append(artifact.getGuid());
      return sb.toString();
   }

   protected List<WebArtifact> getAncestry(ReadableArtifact art) {
      ReadableArtifact cur = art.getParent();
      List<WebArtifact> ancestry = new ArrayList<WebArtifact>();
      while (cur != null) {
         ancestry.add(convertToWebArtifact(cur));
         if (cur.hasParent()) {
            cur = cur.getParent();
         } else {
            break;
         }
      }
      return ancestry;
   }

   protected void setErrorMessage(DisplaysErrorComponent component, String message) {
      if (component != null) {
         component.setErrorMessage(message);
      }
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
