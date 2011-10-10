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
package org.eclipse.osee.ats.view.web;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponentInterface;
import org.eclipse.osee.ats.api.search.AtsSearchPresenter;
import org.eclipse.osee.display.api.components.ArtifactHeaderComponent;
import org.eclipse.osee.display.api.components.AttributeComponent;
import org.eclipse.osee.display.api.components.RelationComponent;
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.api.data.SearchResultMatch;
import org.eclipse.osee.display.api.data.WebArtifact;
import org.eclipse.osee.display.api.data.WebId;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.view.web.UrlParamNameConstants;

/**
 * @author Shawn F. Cook
 */
public class AtsWebSearchPresenter_TestBackend implements AtsSearchPresenter {

   private static final AtsSearchPresenter atsBackend = new AtsWebSearchPresenter_TestBackend();

   //*** TEST DATA ***
   WebId build0 = new WebId("baseline_guid", "Baseline");
   WebId build1 = new WebId("bld_1_guid", "Bld_1");
   WebId build2 = new WebId("ftb0_guid", "FTP0");
   WebId build3 = new WebId("FTB1_guid", "FTB1");
   WebId build4 = new WebId("FTB2_guid", "FTB2");
   WebId build5 = new WebId("FTB3_guid", "FTB3");
   WebId build6 = new WebId("EB0_guid", "EB0");
   WebId build7 = new WebId("EB1_guid", "EB1");

   WebId program0 = new WebId("blk3_guid", "Blk 3");
   WebId program1 = new WebId("v131_guid", "V13.1");
   WebId program2 = new WebId("taiwan_guid", "Taiwan");

   WebArtifact defaultroot = new WebArtifact("defaultHierarchRoot_GUID", "Default Hierarchy Root", "Root Artifact");
   WebArtifact swreqs = new WebArtifact("SWReq_GUID", "Software Requirements", "Folder",
      Arrays.asList(defaultroot.getWebId()), new WebId("branch_id1", "branch_id1"));
   WebArtifact crewIntreqs = new WebArtifact("CrewInt_GUID", "Crew Interface", "Folder", Arrays.asList(
      swreqs.getWebId(), defaultroot.getWebId()), new WebId("branch_id2", "branch_id2"));
   WebArtifact commSubSysCrewIntreqs = new WebArtifact("commSubSysCrewInt_GUID",
      "Communication Subsystem Crew Interface", "Heading", Arrays.asList(crewIntreqs.getWebId(), swreqs.getWebId(),
         defaultroot.getWebId()), new WebId("branch_id3", "branch_id3"));
   WebArtifact comm_page_Intreqs =
      new WebArtifact("com_page_GUID", "{COM_PAGE}", "Software Requirement", Arrays.asList(
         commSubSysCrewIntreqs.getWebId(), crewIntreqs.getWebId(), swreqs.getWebId(), defaultroot.getWebId()),
         new WebId("branch_id4", "branch_id4"));

   private final Map<WebId, Collection<WebId>> programsAndBuilds = new HashMap<WebId, Collection<WebId>>();
   private final Map<String, WebArtifact> artifacts = new HashMap<String, WebArtifact>();

   public AtsWebSearchPresenter_TestBackend() {
      super();

      //populate test data
      programsAndBuilds.put(program0, Arrays.asList(build0, build1, build2));
      programsAndBuilds.put(program1, Arrays.asList(build3, build4, build5));
      programsAndBuilds.put(program2, Arrays.asList(build6, build7));

      //      Map<RelationType, Collection<Artifact>> swreqsRelations = new HashMap<RelationType, Collection<Artifact>>();
      //      swreqsRelations.put(RelationType.PARENT, Arrays.asList(defaultroot));
      //      Artifact swreqs = new Artifact("SWReq_GUID", "Software Requirements", "Folder", swreqsRelations);
      //
      //      Map<RelationType, Collection<Artifact>> crewIntRelations = new HashMap<RelationType, Collection<Artifact>>();
      //      crewIntRelations.put(RelationType.PARENT, Arrays.asList(swreqs));
      //      Artifact crewIntreqs = new Artifact("CrewInt_GUID", "Crew Interface", "Folder", crewIntRelations);
      //
      //      Map<RelationType, Collection<Artifact>> commSubSysCrewIntRelations =
      //         new HashMap<RelationType, Collection<Artifact>>();
      //      commSubSysCrewIntRelations.put(RelationType.PARENT, Arrays.asList(crewIntreqs));
      //      Artifact commSubSysCrewIntreqs =
      //         new Artifact("commSubSysCrewInt_GUID", "Communication Subsystem Crew Interface", "Heading",
      //            commSubSysCrewIntRelations);
      //
      //      Map<RelationType, Collection<Artifact>> comm_page_Relations = new HashMap<RelationType, Collection<Artifact>>();
      //      comm_page_Relations.put(RelationType.PARENT, Arrays.asList(commSubSysCrewIntreqs));
      //      Artifact comm_page_Intreqs =
      //         new Artifact("com_page_GUID", "{COM_PAGE}", "Software Requirement", comm_page_Relations);

      artifacts.put(defaultroot.getGuid(), defaultroot);
      artifacts.put(swreqs.getGuid(), swreqs);
      artifacts.put(crewIntreqs.getGuid(), crewIntreqs);
      artifacts.put(commSubSysCrewIntreqs.getGuid(), commSubSysCrewIntreqs);
      artifacts.put(comm_page_Intreqs.getGuid(), comm_page_Intreqs);

      Set<Entry<String, WebArtifact>> artifactsSet = artifacts.entrySet();
      for (Entry<String, WebArtifact> entry : artifactsSet) {
         WebArtifact artifact = entry.getValue();
         artifact.setAttr_Category("B");
         artifact.setAttr_DevAssurLevel("E");
         artifact.setAttr_ImpoParaNum("3.2.1.1");
         artifact.setAttr_Partition("CND, DP, SP, WP");
         artifact.setAttr_QualMethod("Test");
         artifact.setAttr_Subsystm("Communications");
         artifact.setAttr_TechPerfParam("False");
      }
   }

   @Override
   public void selectSearch(WebId program, WebId build, boolean nameOnly, String searchPhrase, SearchNavigator atsNavigator) {
      if (atsNavigator != null) {
         Map<String, String> parameters = new HashMap<String, String>();
         if (program != null) {
            parameters.put(UrlParamNameConstants.PARAMNAME_PROGRAM, program.getGuid());
         }
         if (build != null) {
            parameters.put(UrlParamNameConstants.PARAMNAME_BUILD, build.getGuid());
         }
         parameters.put(UrlParamNameConstants.PARAMNAME_NAMEONLY, nameOnly ? "true" : "false");
         if (searchPhrase != null) {
            parameters.put(UrlParamNameConstants.PARAMNAME_SEARCHPHRASE, searchPhrase);
         }
         if (parameters.size() > 0) {
            String url = parameterMapToRequestString(parameters);
            atsNavigator.navigateSearchResults(url);
         }
      }
   }

   @Override
   public void initSearchHome(AtsSearchHeaderComponentInterface headerComponent) {
      if (headerComponent != null) {
         headerComponent.clearAll();
         Set<Entry<WebId, Collection<WebId>>> entrySet = programsAndBuilds.entrySet();
         if (entrySet != null) {
            for (Entry<WebId, Collection<WebId>> entry : entrySet) {
               headerComponent.addProgram(entry.getKey());
            }
         }
         //      headerComponent.setProgram(program0);
      }
   }

   @Override
   public void initSearchHome(SearchHeaderComponent searchHeaderComp) {
      searchHeaderComp.clearAll();
   }

   @Override
   public void selectArtifact(WebArtifact artifact, SearchNavigator oseeNavigator) {
      String url = String.format("/artifact/%s", artifact.getGuid());
      oseeNavigator.navigateArtifactPage(url);
   }

   @Override
   public void initArtifactPage(String url, SearchHeaderComponent searchHeaderComp, ArtifactHeaderComponent artHeaderComp, RelationComponent relComp, AttributeComponent attrComp) {
      initSearchHome(searchHeaderComp);
      setSearchHeaderCriteria(url, (AtsSearchHeaderComponentInterface) searchHeaderComp);
      artHeaderComp.clearAll();
      Map<String, String> params = requestStringToParameterMap(url);
      if (params != null && params.size() > 0) {
         String artGuid = params.get(UrlParamNameConstants.PARAMNAME_ARTIFACT);
         if (artGuid != null && !artGuid.isEmpty()) {
            WebArtifact artifact = artifacts.get(artGuid);
            if (artifact != null) {
               artHeaderComp.setArtifact(artifact);

               relComp.clearAll();
               WebId parentRelationType = new WebId("guid1", "Parent");
               WebId childRelationType = new WebId("guid2", "Child");
               WebId swReqRelationType = new WebId("guid3", "SW Requirement");
               relComp.addRelationType(parentRelationType);
               relComp.addRelationType(childRelationType);
               relComp.addRelationType(swReqRelationType);

               attrComp.clearAll();
               attrComp.addAttribute("type1", "value1");
            }
         }
      }
   }

   @Override
   public void selectRelationType(WebId id, RelationComponent relationComponent) {
      relationComponent.clearRelations();
      Set<Entry<String, WebArtifact>> artifactsSet = artifacts.entrySet();
      for (Entry<String, WebArtifact> entry : artifactsSet) {
         WebArtifact artifact = entry.getValue();
         relationComponent.addRelation(artifact);
      }
   }

   public static AtsSearchPresenter getInstance() {
      return atsBackend;
   }

   @Override
   public void selectProgram(WebId program, AtsSearchHeaderComponentInterface headerComponent) {
      if (program != null && headerComponent != null) {
         Collection<WebId> builds = programsAndBuilds.get(program);
         headerComponent.clearBuilds();
         if (builds != null) {
            for (WebId build : builds) {
               headerComponent.addBuild(build);
            }
            //      headerComponent.setBuild(builds.iterator().next());
         }
      }
   }

   @Override
   public void initSearchResults(String url, SearchHeaderComponent searchHeaderComp, SearchResultsListComponent searchResultsComp) {
      //Do nothing
   }

   private void setSearchHeaderCriteria(String url, AtsSearchHeaderComponentInterface searchHeaderComponent) {
      Map<String, String> params = requestStringToParameterMap(url);
      WebId program = new WebId("", "");
      WebId build = new WebId("", "");
      boolean nameOnly = false;
      String searchPhrase = "";

      if (params != null) {
         String programGuid = params.get(UrlParamNameConstants.PARAMNAME_PROGRAM);
         if (programGuid != null) {
            program = getProgramWithGuid(programGuid);
         }
         String buildGuid = params.get(UrlParamNameConstants.PARAMNAME_BUILD);
         if (buildGuid != null) {
            build = getBuildWithGuid(buildGuid);
         }
         String nameOnlyStr = params.get(UrlParamNameConstants.PARAMNAME_NAMEONLY);
         if (nameOnlyStr != null) {
            nameOnly = nameOnlyStr.equalsIgnoreCase("true");
         }

         searchPhrase = params.get(UrlParamNameConstants.PARAMNAME_SEARCHPHRASE);
         if (searchPhrase == null) {
            searchPhrase = "";
         }
      }

      if (searchHeaderComponent != null) {
         this.selectProgram(program, searchHeaderComponent);
         searchHeaderComponent.setSearchCriteria(program, build, nameOnly, searchPhrase);
      }
   }

   @Override
   public void initSearchResults(String url, AtsSearchHeaderComponentInterface searchHeaderComponent, SearchResultsListComponent resultsComponent) {

      initSearchHome(searchHeaderComponent);
      setSearchHeaderCriteria(url, searchHeaderComponent);

      if (resultsComponent != null) {
         resultsComponent.clearAll();
         Set<Entry<String, WebArtifact>> entrySet = artifacts.entrySet();
         for (Entry<String, WebArtifact> artifactEntry : entrySet) {
            SearchResultComponent searchResultComp = resultsComponent.createSearchResult();
            if (searchResultComp != null) {
               WebArtifact artifact = artifactEntry.getValue();
               searchResultComp.setArtifact(artifact);
               searchResultComp.addSearchResultMatch(new SearchResultMatch("Word Template Content", "...{COM_PAGE}...",
                  10));
            }
         }
      }
   }

   private static Map<String, String> requestStringToParameterMap(String requestedDataId) {
      Map<String, String> parameters = new HashMap<String, String>();

      //TODO: Need better error detection for malformed parameter strings here.

      if (requestedDataId != null) {
         String[] tokens = requestedDataId.split("/");
         if (tokens.length > 1) {
            for (int i = 0; i < tokens.length; i++) {
               String paramName = tokens[i];
               i++;
               String paramValue = tokens[i];
               parameters.put(paramName, paramValue);
            }
         }
      }

      return parameters;
   }

   private static String parameterMapToRequestString(Map<String, String> parameters) {
      String requestedDataId = "/";

      //TODO: Need to properly encode the URI parameters here.

      Set<Entry<String, String>> keyValuePairs = parameters.entrySet();
      for (Iterator<Entry<String, String>> iter = keyValuePairs.iterator(); iter.hasNext();) {
         Entry<String, String> pair = iter.next();
         if (pair.getKey() != null && !pair.getKey().isEmpty() && pair.getValue() != null && !pair.getValue().isEmpty()) {
            requestedDataId = String.format("%s%s/%s", requestedDataId, pair.getKey(), pair.getValue());
            if (iter.hasNext()) {
               requestedDataId = String.format("%s/", requestedDataId);
            }
         }
      }

      return requestedDataId;
   }

   private WebId getProgramWithGuid(String programGuid) {
      if (programGuid != null && !programGuid.isEmpty()) {
         for (WebId program : programsAndBuilds.keySet()) {
            if (program.getGuid().equals(programGuid)) {
               return program;
            }
         }
      }

      return null;
   }

   private WebId getBuildWithGuid(String buildGuid) {
      if (buildGuid != null && !buildGuid.isEmpty()) {
         Set<Entry<WebId, Collection<WebId>>> entrySet = programsAndBuilds.entrySet();
         for (Entry<WebId, Collection<WebId>> programAndBuilds : entrySet) {
            Collection<WebId> builds = programAndBuilds.getValue();
            for (WebId build : builds) {
               if (build.getGuid().equals(buildGuid)) {
                  return build;
               }
            }
         }
      }

      return null;
   }
}
