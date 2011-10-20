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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.display.api.components.ArtifactHeaderComponent;
import org.eclipse.osee.display.api.components.AttributeComponent;
import org.eclipse.osee.display.api.components.DisplaysErrorComponent;
import org.eclipse.osee.display.api.components.RelationComponent;
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.api.data.SearchResultMatch;
import org.eclipse.osee.display.api.data.StyledText;
import org.eclipse.osee.display.api.data.ViewArtifact;
import org.eclipse.osee.display.api.data.ViewId;
import org.eclipse.osee.display.api.data.ViewSearchParameters;
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
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author John Misinco
 */
public class SearchPresenterImpl<T extends SearchHeaderComponent> implements SearchPresenter<T> {

   protected final ArtifactProvider artifactProvider;

   private final static String SIDE_A_KEY = "sideAName";
   private final static String SIDE_B_KEY = "sideBName";

   public SearchPresenterImpl(ArtifactProvider artifactProvider) {
      this.artifactProvider = artifactProvider;
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
         setErrorMessage(searchResultsComp, Lib.exceptionToString(ex));
         return;
      }
      if (searchResults != null && searchResults.size() > 0) {
         try {
            processSearchResults(searchResults, searchResultsComp, params);
         } catch (Exception ex) {
            setErrorMessage(searchResultsComp, Lib.exceptionToString(ex));
            return;
         }
      }
   }

   private void processSearchResults(List<Match<ReadableArtifact, ReadableAttribute<?>>> searchResults, SearchResultsListComponent searchResultsComp, SearchParameters params) throws OseeCoreException {
      for (Match<ReadableArtifact, ReadableAttribute<?>> match : searchResults) {
         ReadableArtifact matchedArtifact = match.getItem();
         ViewArtifact viewArtifact = convertToViewArtifact(matchedArtifact);

         SearchResultComponent searchResult = searchResultsComp.createSearchResult();
         searchResult.setArtifact(viewArtifact);
         if (params.isVerbose()) {
            for (ReadableAttribute<?> element : match.getElements()) {
               List<MatchLocation> matches = match.getLocation(element);
               String data = String.valueOf(element.getValue());
               List<StyledText> text = Utility.getMatchedText(data, matches);
               SearchResultMatch srm =
                  new SearchResultMatch(element.getAttributeType().getName(), matches.size(), text);
               searchResult.addSearchResultMatch(srm);
            }
         }
      }
   }

   @Override
   public void selectArtifact(String url, ViewArtifact artifact, SearchNavigator oseeNavigator) {
      Map<String, String> params = new HashMap<String, String>();
      params.put("branch", artifact.getBranch().getGuid());
      params.put("artifact", artifact.getGuid());
      String value;
      try {
         value = getParametersAsEncodedUrl(params);
         oseeNavigator.navigateArtifactPage("/" + value);
      } catch (UnsupportedEncodingException ex) {
         //         setErrorMessage(artifact, Lib.exceptionToString(ex));
      }

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

      ViewArtifact artifact = null;
      try {
         artifact = convertToViewArtifact(displayArt);
      } catch (Exception e) {
         setErrorMessage(artHeaderComp, String.format("Error while converting [%s] from branch:[%s]", art, branch));
         return;
      }

      artHeaderComp.setArtifact(artifact);

      relComp.clearAll();
      relComp.setArtifact(artifact);
      Collection<RelationType> relationTypes = null;
      try {
         relationTypes = artifactProvider.getValidRelationTypes(displayArt);
      } catch (Exception e) {
         setErrorMessage(relComp, String.format("Error loading relation types for: [%s]", displayArt.getName()));
         return;
      }
      for (RelationType relTypeSide : relationTypes) {
         ViewId toAdd = new ViewId(relTypeSide.getGuid().toString(), relTypeSide.getName());
         toAdd.setAttribute(SIDE_A_KEY, relTypeSide.getSideAName());
         toAdd.setAttribute(SIDE_B_KEY, relTypeSide.getSideBName());
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
   public void selectRelationType(ViewArtifact artifact, ViewId relation, RelationComponent relationComponent) {
      relationComponent.clearRelations();
      if (artifact == null || relation == null) {
         setErrorMessage(relationComponent, "Error: Null detected in selectRelationType parameters");
         return;
      }
      String relGuid = relation.getGuid();

      IRelationType type = TokenFactory.createRelationType(Long.parseLong(relGuid), relation.getName());
      IOseeBranch branch = TokenFactory.createBranch(artifact.getBranch().getGuid(), "");
      ReadableArtifact sourceArt;
      Collection<ReadableArtifact> relatedSideA = Collections.emptyList();
      Collection<ReadableArtifact> relatedSideB = Collections.emptyList();
      try {
         sourceArt = artifactProvider.getArtifactByGuid(branch, artifact.getGuid());
         relatedSideA =
            artifactProvider.getRelatedArtifacts(sourceArt,
               TokenFactory.createRelationTypeSide(RelationSide.SIDE_A, type.getGuid(), type.getName()));
         relatedSideB =
            artifactProvider.getRelatedArtifacts(sourceArt,
               TokenFactory.createRelationTypeSide(RelationSide.SIDE_B, type.getGuid(), type.getName()));
      } catch (Exception ex) {
         setErrorMessage(relationComponent,
            String.format("Error loading relations for artifact[%s]", artifact.getGuid()));
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
         for (ReadableArtifact rel : relatedSideA) {
            ViewArtifact id = convertToViewArtifact(rel);
            relationComponent.addLeftRelated(id);
         }
         for (ReadableArtifact rel : relatedSideB) {
            ViewArtifact id = convertToViewArtifact(rel);
            relationComponent.addRightRelated(id);
         }
      } catch (Exception ex) {
         setErrorMessage(relationComponent,
            String.format("Error adding artifact[%s] to relation relation component", artifact.getGuid()));
         return;
      }
   }

   protected ViewArtifact convertToViewArtifact(ReadableArtifact artifact) throws OseeCoreException {
      ViewId branch = new ViewId(artifact.getBranch().getGuid(), artifact.getBranch().getName());
      ViewArtifact toReturn =
         new ViewArtifact(artifact.getGuid(), artifact.getName(), artifact.getArtifactType().getName(),
            getAncestry(artifact), branch);
      return toReturn;
   }

   protected List<ViewArtifact> getAncestry(ReadableArtifact art) throws OseeCoreException {
      ReadableArtifact cur = artifactProvider.getParent(art);
      List<ViewArtifact> ancestry = new ArrayList<ViewArtifact>();
      while (cur != null) {
         ancestry.add(convertToViewArtifact(cur));
         cur = artifactProvider.getParent(cur);
      }
      return ancestry;
   }

   protected void setErrorMessage(DisplaysErrorComponent component, String message) {
      if (component != null) {
         component.setErrorMessage(message);
      }
   }

   private ArtifactParameters decodeArtifactUrl(String url) {
      Map<String, String> data = Utility.decode(url);
      String branch = data.get("branch");
      String artifact = data.get("artifact");
      return new ArtifactParameters(branch, artifact);
   }

   private SearchParameters decodeSearchUrl(String url) {
      Map<String, String> data = Utility.decode(url);

      String branch = data.get("branch");
      String vValue = data.get("verbose");
      boolean verbose = vValue == null ? false : vValue.equalsIgnoreCase("true");
      String nValue = data.get("nameOnly");
      boolean nameOnly = nValue == null ? false : nValue.equalsIgnoreCase("true");
      String searchPhrase = data.get("search");
      return new SearchParameters(branch, nameOnly, searchPhrase, verbose);
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
      private final boolean nameOnly, verbose;
      private final String searchPhrase;

      public SearchParameters(String branchId, boolean nameOnly, String searchPhrase, boolean verbose) {
         this.branchId = branchId;
         this.nameOnly = nameOnly;
         this.searchPhrase = searchPhrase;
         this.verbose = verbose;
      }

      public boolean isVerbose() {
         return verbose;
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

   protected String getParametersAsEncodedUrl(Map<String, String> keyValues) throws UnsupportedEncodingException {
      StringBuilder sb = new StringBuilder();
      for (Entry<String, String> entry : keyValues.entrySet()) {
         String key = entry.getKey();
         sb.append(Utility.encode(key));
         sb.append("=");
         sb.append(Utility.encode(entry.getValue()));
         sb.append("&");
      }
      if (sb.length() - 1 >= 0) {
         // Delete the last unnecessary '&'
         sb.deleteCharAt(sb.length() - 1);
      }
      return sb.toString();
   }

}
