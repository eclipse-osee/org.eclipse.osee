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
package org.eclipse.osee.ats.presenter.mock.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.ats.ui.api.data.AtsSearchParameters;
import org.eclipse.osee.ats.ui.api.search.AtsSearchPresenter;
import org.eclipse.osee.ats.ui.api.view.AtsSearchHeaderComponent;
import org.eclipse.osee.display.api.components.ArtifactHeaderComponent;
import org.eclipse.osee.display.api.components.AttributeComponent;
import org.eclipse.osee.display.api.components.DisplayOptionsComponent;
import org.eclipse.osee.display.api.components.DisplaysErrorComponent.MsgType;
import org.eclipse.osee.display.api.components.RelationComponent;
import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.api.data.DisplayOptions;
import org.eclipse.osee.display.api.data.SearchResultMatch;
import org.eclipse.osee.display.api.data.StyledText;
import org.eclipse.osee.display.api.data.ViewArtifact;
import org.eclipse.osee.display.api.data.ViewId;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.presenter.mocks.MockSearchPresenter;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Shawn F. Cook
 */
public class MockAtsWebSearchPresenter<T extends AtsSearchHeaderComponent, K extends AtsSearchParameters> extends MockSearchPresenter<T, K> implements AtsSearchPresenter<T, K> {

   private static final AtsSearchPresenter<AtsSearchHeaderComponent, AtsSearchParameters> atsBackend =
      new MockAtsWebSearchPresenter<AtsSearchHeaderComponent, AtsSearchParameters>();

   // *** TEST DATA ***
   ViewId build0 = new ViewId("baseline_guid", "Baseline");
   ViewId build1 = new ViewId("bld_1_guid", "Bld_1");
   ViewId build2 = new ViewId("ftb0_guid", "FTP0");
   ViewId build3 = new ViewId("FTB1_guid", "FTB1");
   ViewId build4 = new ViewId("FTB2_guid", "FTB2");
   ViewId build5 = new ViewId("FTB3_guid", "FTB3");
   ViewId build6 = new ViewId("EB0_guid", "EB0");
   ViewId build7 = new ViewId("EB1_guid", "EB1");

   ViewId program0 = new ViewId("blk3_guid", "Blk 3");
   ViewId program1 = new ViewId("v131_guid", "V13.1");
   ViewId program2 = new ViewId("taiwan_guid", "Taiwan");

   ViewArtifact defaultroot = new ViewArtifact("defaultHierarchRoot_GUID", "Default Hierarchy Root", "Root Artifact");
   ViewArtifact swreqs = new ViewArtifact("SWReq_GUID", "Software Requirements", "Folder", Arrays.asList(defaultroot),
      new ViewId("branch_id1", "branch_id1"));
   ViewArtifact crewIntreqs = new ViewArtifact("CrewInt_GUID", "Crew Interface", "Folder", Arrays.asList(swreqs,
      defaultroot), new ViewId("branch_id2", "branch_id2"));
   ViewArtifact commSubSysCrewIntreqs = new ViewArtifact("commSubSysCrewInt_GUID",
      "Communication Subsystem Crew Interface", "Heading", Arrays.asList(crewIntreqs, swreqs, defaultroot), new ViewId(
         "branch_id3", "branch_id3"));
   ViewArtifact comm_page_Intreqs = new ViewArtifact("com_page_GUID", "{COM_PAGE}", "Software Requirement",
      Arrays.asList(commSubSysCrewIntreqs, crewIntreqs, swreqs, defaultroot), new ViewId("branch_id4", "branch_id4"));

   private final Map<ViewId, Collection<ViewId>> programsAndBuilds = new HashMap<ViewId, Collection<ViewId>>();
   private final Map<String, ViewArtifact> artifacts = new HashMap<String, ViewArtifact>();

   public MockAtsWebSearchPresenter() {
      super();

      // populate test data
      programsAndBuilds.put(program0, Arrays.asList(build0, build1, build2));
      programsAndBuilds.put(program1, Arrays.asList(build3, build4, build5));
      programsAndBuilds.put(program2, Arrays.asList(build6, build7));

      artifacts.put(defaultroot.getGuid(), defaultroot);
      artifacts.put(swreqs.getGuid(), swreqs);
      artifacts.put(crewIntreqs.getGuid(), crewIntreqs);
      artifacts.put(commSubSysCrewIntreqs.getGuid(), commSubSysCrewIntreqs);
      artifacts.put(comm_page_Intreqs.getGuid(), comm_page_Intreqs);

      Set<Entry<String, ViewArtifact>> artifactsSet = artifacts.entrySet();
      for (Entry<String, ViewArtifact> entry : artifactsSet) {
         ViewArtifact artifact = entry.getValue();
         artifact.setAttr_Category("B");
         artifact.setAttr_DevAssurLevel("E");
         artifact.setAttr_ImpoParaNum("3.2.1.1");
         artifact.setAttr_Partition("CND, DP, SP, WP");
         artifact.setAttr_QualMethod("Test");
         artifact.setAttr_Subsystm("Communications");
         artifact.setAttr_TechPerfParam("False");
      }

      //Generate large data set
      for (int i = 5; i < 150; i++) {
         ViewArtifact art =
            new ViewArtifact(String.format("bulkArt_GUID_%d", i), String.format("Bulk Artifact %d", i),
               "Software Requirement", Arrays.asList(swreqs, defaultroot), new ViewId(String.format("branch_id%d", i),
                  String.format("branch_id%d", i)));
         artifacts.put(art.getGuid(), art);
      }
   }

   @Override
   public void selectDisplayOptions(final String url, final DisplayOptions options, final SearchNavigator navigator) {
      if (navigator != null && options != null) {
         Map<String, String> parameters = new HashMap<String, String>();

         if (options.getVerboseResults() != null) {
            parameters.put(UrlParamNameConstants.PARAMNAME_SHOWVERBOSE, options.getVerboseResults() ? "true" : "false");
         }

         if (parameters.size() > 0) {
            String newurl = parameterMapToRequestString(parameters, url);
            navigator.navigateSearchResults(newurl);
         }
      }
   }

   @Override
   public void selectCancel() {
      fireSearchCancelledEvent();
   }

   @Override
   public void selectSearch(final String url, final K params, final SearchNavigator navigator) {

      if (navigator != null && params != null) {
         Map<String, String> parameters = new HashMap<String, String>();

         if (params.getProgram() != null) {
            parameters.put(UrlParamNameConstants.PARAMNAME_PROGRAM, params.getProgram().getGuid());
         }
         if (params.getBuild() != null) {
            parameters.put(UrlParamNameConstants.PARAMNAME_BUILD, params.getBuild().getGuid());
         }
         if (params.isNameOnly() != null) {
            parameters.put(UrlParamNameConstants.PARAMNAME_NAMEONLY, params.isNameOnly() ? "true" : "false");
         }
         if (params.getSearchString() != null) {
            parameters.put(UrlParamNameConstants.PARAMNAME_SEARCHPHRASE, params.getSearchString());
         }
         if (parameters.size() > 0) {
            String newurl = parameterMapToRequestString(parameters, url);
            navigator.navigateSearchResults(newurl);
         }
      }

   }

   private void updateSearchDisplayOptions(String url, DisplayOptionsComponent optionsComp) {
      if (optionsComp != null) {

         Map<String, String> params = requestStringToParameterMap(url);
         if (params != null && params.size() > 0) {
            String verboseStr = params.get(UrlParamNameConstants.PARAMNAME_SHOWVERBOSE);
            Boolean verbose = false;

            if (verboseStr != null) {
               verbose = verboseStr.equalsIgnoreCase("true");
            }

            DisplayOptions options = new DisplayOptions(verbose);
            optionsComp.setDisplayOptions(options);
         }
      }
   }

   private void updateSearchHeader(String url, AtsSearchHeaderComponent searchHeaderComp) {
      if (searchHeaderComp != null) {
         searchHeaderComp.clearAll();
         Set<Entry<ViewId, Collection<ViewId>>> entrySet = programsAndBuilds.entrySet();
         if (entrySet != null) {
            for (Entry<ViewId, Collection<ViewId>> entry : entrySet) {
               searchHeaderComp.addProgram(entry.getKey());
            }
         }

         Map<String, String> params = requestStringToParameterMap(url);
         if (params != null && params.size() > 0) {

            ViewId program, build;
            program = getProgramWithGuid(params.get(UrlParamNameConstants.PARAMNAME_PROGRAM));
            build = getBuildWithGuid(params.get(UrlParamNameConstants.PARAMNAME_BUILD));
            String nameOnlyStr = params.get(UrlParamNameConstants.PARAMNAME_NAMEONLY);
            Boolean nameOnly = false;
            if (nameOnlyStr != null) {
               nameOnly = nameOnlyStr.equalsIgnoreCase("true");
            }

            AtsSearchParameters atsParams =
               new AtsSearchParameters(params.get(UrlParamNameConstants.PARAMNAME_SEARCHPHRASE), nameOnly, build,
                  program);
            searchHeaderComp.setSearchCriteria(atsParams);
         }
      }
   }

   @Override
   public void selectArtifact(String url, ViewArtifact artifact, SearchNavigator oseeNavigator) {
      if (url != null && artifact != null && oseeNavigator != null) {
         Map<String, String> params = new HashMap<String, String>();
         params.put("artifact", artifact.getGuid());
         String newurl = parameterMapToRequestString(params, url);
         oseeNavigator.navigateArtifactPage(newurl);
      }
   }

   @Override
   public void initArtifactPage(String url, T searchHeaderComp, ArtifactHeaderComponent artHeaderComp, RelationComponent relComp, AttributeComponent attrComp, DisplayOptionsComponent options) {
      updateSearchHeader(url, searchHeaderComp);
      updateSearchDisplayOptions(url, options);
      artHeaderComp.clearAll();
      Map<String, String> params = requestStringToParameterMap(url);
      if (params != null && params.size() > 0) {
         String artGuid = params.get(UrlParamNameConstants.PARAMNAME_ARTIFACT);
         if (artGuid != null && !artGuid.isEmpty()) {
            ViewArtifact artifact = artifacts.get(artGuid);
            if (artifact != null) {
               artHeaderComp.setArtifact(artifact);

               relComp.clearAll();
               ViewId hierRelationType = new ViewId("guid1", "Default Hierarchy");
               ViewId swReqRelationType = new ViewId("guid3", "Traceability");
               relComp.addRelationType(hierRelationType);
               relComp.addRelationType(swReqRelationType);

               attrComp.clearAll();
               attrComp.addAttribute(CoreAttributeTypes.Category.getName(), "B");
               attrComp.addAttribute(CoreAttributeTypes.DevelopmentAssuranceLevel.getName(), "E");
               attrComp.addAttribute(CoreAttributeTypes.ParagraphNumber.getName(), "3.2.1.1");
               attrComp.addAttribute(CoreAttributeTypes.Partition.getName(), "CND");
               attrComp.addAttribute(CoreAttributeTypes.QualificationMethod.getName(), "Test");
               attrComp.addAttribute(CoreAttributeTypes.Subsystem.getName(), "Communications");
               attrComp.addAttribute(CoreAttributeTypes.TechnicalPerformanceParameter.getName(), "False");
               attrComp.addAttribute(
                  CoreAttributeTypes.WordTemplateContent.getName(),
                  "Type: Graphic Format (Location): {MAP_overlay_UIG} {FLIGHT_UIG} {WEAPON_UIG} Local Data Definition: [{RF_HANDOVER_TARGET}.DELTA_NORTH_BETWEEN_AIRCRAFT]: (floating point number) meters Mode: North_Dist returned from {FIND_NORTH_EAST_DIST_BETWEEN_TWO_POINTS}( [RFHO_DATA.RFHO_R_LAT_(MSW,LSW)]LM -> Pt1.Lat, [RFHO_DATA.RFHO_R_LONG_(MSW,LSW)]LM -> Pt1.Long, [.AIRCRAFT_LATITUDE] -> Pt2.Lat, [.AIRCRAFT_LONGITUDE] -> Pt2.Long)");
            } else {
               artHeaderComp.setErrorMessage("No artifact found with ID:" + artGuid, "[no more information]",
                  MsgType.MSGTYPE_WARNING);
            }
         }
      }
   }

   @Override
   public void selectRelationType(ViewArtifact artifact, ViewId relation, RelationComponent relationComponent) {
      if (relationComponent != null && relation != null) {
         relationComponent.clearRelations();
         String relGuid = relation.getGuid();
         if (relGuid.equals("guid1")) {
            //Default Hierarchy
            relationComponent.addLeftRelated(crewIntreqs);
            relationComponent.addRightRelated(commSubSysCrewIntreqs);
            relationComponent.setLeftName("Parent");
            relationComponent.setRightName("Child");
         }

         if (relGuid.equals("guid3")) {
            //Default Hierarchy
            relationComponent.addLeftRelated(null);
            relationComponent.setLeftName(null);
            relationComponent.setRightName("SW Requirement");
            Set<Entry<String, ViewArtifact>> entrySet = artifacts.entrySet();
            for (Entry<String, ViewArtifact> entry : entrySet) {
               ViewArtifact art = entry.getValue();
               relationComponent.addRightRelated(art);
            }
         }
      }
   }

   public static AtsSearchPresenter<?, ?> getInstance() {
      return atsBackend;
   }

   @Override
   public void selectProgram(ViewId program, T headerComponent) {
      if (program != null && headerComponent != null) {
         Collection<ViewId> builds = programsAndBuilds.get(program);
         headerComponent.clearBuilds();
         if (builds != null) {
            for (ViewId build : builds) {
               headerComponent.addBuild(build);
            }
            // headerComponent.setBuild(builds.iterator().next());
         }
      }
   }

   @Override
   public void initSearchResults(final String url, final T searchHeaderComp, final SearchResultsListComponent searchResultsComp, final DisplayOptionsComponent options) {
      fireSearchInProgressEvent();

      Thread thread = new Thread(new Runnable() {
         @Override
         public void run() {

            updateSearchHeader(url, searchHeaderComp);
            updateSearchDisplayOptions(url, options);

            if (searchResultsComp != null) {
               String searchPhrase = "";
               boolean nameOnly = false;
               boolean verbose = false;
               Map<String, String> params = requestStringToParameterMap(url);
               if (params != null && params.size() > 0) {
                  searchPhrase = params.get(UrlParamNameConstants.PARAMNAME_SEARCHPHRASE);
                  if (searchPhrase == null) {
                     searchPhrase = "";
                  }

                  String nameOnly_str = params.get(UrlParamNameConstants.PARAMNAME_NAMEONLY);
                  if (nameOnly_str != null && !nameOnly_str.isEmpty()) {
                     nameOnly = nameOnly_str.equalsIgnoreCase("true");
                  }

                  String verbose_str = params.get(UrlParamNameConstants.PARAMNAME_SHOWVERBOSE);
                  if (verbose_str != null && !verbose_str.isEmpty()) {
                     verbose = verbose_str.equalsIgnoreCase("true");
                  }
               }
               searchResultsComp.clearAll();
               if (!searchPhrase.isEmpty()) {
                  Set<Entry<String, ViewArtifact>> entrySet = artifacts.entrySet();
                  for (Entry<String, ViewArtifact> artifactEntry : entrySet) {
                     ViewArtifact artifact = artifactEntry.getValue();
                     if (artifact.getArtifactName().toLowerCase().contains(searchPhrase.toLowerCase())) {
                        SearchResultComponent searchResultComp = searchResultsComp.createSearchResult();
                        if (searchResultComp != null) {
                           searchResultComp.setArtifact(artifact);

                           DisplayOptions dispOptions = new DisplayOptions(verbose);
                           searchResultComp.setDisplayOptions(dispOptions);
                           if (!nameOnly && verbose) {
                              StyledText matchHintText = new StyledText("...{COM_PAGE}...", true);
                              searchResultComp.addSearchResultMatch(new SearchResultMatch("Word Template Content", 10,
                                 Arrays.asList(matchHintText)));
                           }
                        }
                     }
                  }
               }
            }
            fireSearchCompletedEvent();
         }
      }, "thread1");

      thread.start();
   }

   private static Map<String, String> requestStringToParameterMap(String requestedDataId) {
      Map<String, String> parameters = new HashMap<String, String>();

      // TODO: Need better error detection for malformed parameter strings
      // here.

      if (requestedDataId != null) {
         String[] tokens = requestedDataId.split("/");
         if (tokens.length > 1) {
            for (int i = 0; i < tokens.length; i++) {
               String paramName = tokens[i];
               if (paramName != null && !paramName.isEmpty()) {
                  i++;
                  String paramValue = tokens[i];
                  parameters.put(paramName, paramValue);
               }
            }
         }
      }

      return parameters;
   }

   private static String parameterMapToRequestString(Map<String, String> parameters, String oldurl) {
      String requestedDataId = "/";

      Map<String, String> oldParameters = requestStringToParameterMap(oldurl);

      Set<Entry<String, String>> oldKeyValuePairs = oldParameters.entrySet();
      for (Iterator<Entry<String, String>> iter = oldKeyValuePairs.iterator(); iter.hasNext();) {
         Entry<String, String> pair = iter.next();
         if (pair.getKey() != null && !pair.getKey().isEmpty() && pair.getValue() != null && !pair.getValue().isEmpty()) {
            //Check for new state for this key
            String newValue = parameters.get(pair.getKey());
            if (newValue == null) {
               newValue = pair.getValue();
            }
            requestedDataId = String.format("%s%s/%s/", requestedDataId, pair.getKey(), newValue);
         }
      }

      Set<Entry<String, String>> keyValuePairs = parameters.entrySet();
      for (Iterator<Entry<String, String>> iter = keyValuePairs.iterator(); iter.hasNext();) {
         Entry<String, String> pair = iter.next();
         if (pair.getKey() != null && !pair.getKey().isEmpty() && pair.getValue() != null && !pair.getValue().isEmpty()) {
            //Check for new key value pair that does not yet exist in the old state
            if (oldParameters.get(pair.getKey()) == null) {
               requestedDataId = String.format("%s%s/%s/", requestedDataId, pair.getKey(), pair.getValue());
            }
         }
      }

      if (requestedDataId.endsWith("/")) {
         requestedDataId.substring(0, requestedDataId.length() - 1);
      }

      return requestedDataId;
   }

   private ViewId getProgramWithGuid(String programGuid) {
      if (programGuid != null && !programGuid.isEmpty()) {
         for (ViewId program : programsAndBuilds.keySet()) {
            if (program.getGuid().equals(programGuid)) {
               return program;
            }
         }
      }

      return null;
   }

   private ViewId getBuildWithGuid(String buildGuid) {
      if (buildGuid != null && !buildGuid.isEmpty()) {
         Set<Entry<ViewId, Collection<ViewId>>> entrySet = programsAndBuilds.entrySet();
         for (Entry<ViewId, Collection<ViewId>> programAndBuilds : entrySet) {
            Collection<ViewId> builds = programAndBuilds.getValue();
            for (ViewId build : builds) {
               if (build.getGuid().equals(buildGuid)) {
                  return build;
               }
            }
         }
      }

      return null;
   }

   public class UrlParamNameConstants {
      public final static String PARAMNAME_ARTIFACT = "artifact";
      public final static String PARAMNAME_PROGRAM = "program";
      public final static String PARAMNAME_BUILD = "build";
      public final static String PARAMNAME_NAMEONLY = "nameonly";
      public final static String PARAMNAME_SEARCHPHRASE = "searchphrase";
      public final static String PARAMNAME_SHOWVERBOSE = "showverbose";
   }

}
