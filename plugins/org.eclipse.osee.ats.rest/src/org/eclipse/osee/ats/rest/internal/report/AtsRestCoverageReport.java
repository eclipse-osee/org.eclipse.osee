/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.rest.internal.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;

/**
 * Calculate and report test coverage of ATS REST calls. <br/>
 * Actual REST calls are retrieved from the OSEE_ACTIVITY table that logs all server calls. <br/>
 * Expected come from the ats_?wadl which shows available REST calls
 *
 * @author Donald G. Dunne
 */
public class AtsRestCoverageReport {

   /**
    * This number should not be reduced, ask if you do not know how to create tests for new REST calls.<br/>
    * Please increase this number as percent coverage goes up.
    */
   private final float MINIMUM_PERCENT_COVERAGE = Float.valueOf(64);
   RestData data = new RestData();
   XResultData rdDetails = new XResultData();
   XResultData rd = new XResultData();
   private int match;
   private final AtsApi atsApi;

   public AtsRestCoverageReport(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public XResultData run() {
      getActualPathsFromTests();
      getExpectedPathsFromServices();
      setSkips();
      matchup();

      float percentCoverage = report();
      if (percentCoverage > MINIMUM_PERCENT_COVERAGE) {
         String errorMsg =
            String.format("ATS REST Coverage %s should not drop below %s; Actual %s / Expected %s. Add ATS REST tests!",
               percentCoverage, MINIMUM_PERCENT_COVERAGE, data.actuals, data.expected);
         rdDetails.errorf(errorMsg);
      }

      return rd;
   }

   private float report() {
      match = 0;
      for (ActualUrl actUrl : data.actuals) {
         if (actUrl.isMatch()) {
            match++;
         }
      }

      List<String> matchedItems = new ArrayList<>();
      for (ExpectedUrl expectedUrl : data.expected) {
         if (!expectedUrl.isMatch()) {
            matchedItems.add("Unmatched: " + expectedUrl.getExpectedUrl() + "\n");
         }
      }
      matchedItems.sort(Comparator.naturalOrder());
      rdDetails.log(matchedItems.toString());

      rd.log("ATS REST Test Coverage Report (AtsRestCoverageTest)\n\n");
      rd.log("This report is to help improve ATS REST testing over time.");
      rd.log("Actuals come from OSEE_ACIVITY where calls are logged during tests");
      rd.log("Expected come from ats_?wadl which is scrubbed for the available REST calls for ATS");
      rd.log("See \"Unmatched\" at the end to improve coverage");
      rd.logf("\nExpected: %s\n", data.expected.size());
      rd.logf("Matched: %s\n", match);
      rd.logf("Unmatched: %s\n\n", data.expected.size() - match);

      float percent = 0;
      if (match != 0) {
         percent = ((float) match / (float) data.expected.size()) * 100;
      }
      rd.logf("Percent Coverage Actual: %2.1f - Minimum Expected: %2.1f\n\n", percent, MINIMUM_PERCENT_COVERAGE);
      rd.merge(rdDetails);

      return percent;
   }

   private void setSkips() {
      List<ActualUrl> skips = new ArrayList<>();
      List<String> skipStartsWith = Arrays.asList("datastore", "session", "branch", "resources", "branches");
      rdDetails.logf("Skipping strs starts-with %s\n\n", skipStartsWith);
      for (ActualUrl actUrl : data.actuals) {
         for (String skipStr : skipStartsWith) {
            if (actUrl.actualUrl.startsWith(skipStr)) {
               skips.add(actUrl);
               actUrl.setSkip(true);
               // uncomment for detailed skip text
               // rd.logf("Skipped %s\n", actUrl);
               break;
            }
         }
      }
      rdDetails.logf("Skipped %s urls\n", skips.size());
      data.actuals.removeAll(skips);

      for (ActualUrl rUrl : data.actuals) {
         rdDetails.logf("Unskipped Actual: %s\n", rUrl);
      }
   }

   private void matchup() {
      for (ExpectedUrl expUrl : data.expected) {
         String expUrlStr = expUrl.expectedUrl;
         rdDetails.logf("\nFul Expected: %s\n", expUrlStr);
         String cleanExpUrlStr = expUrlStr;
         cleanExpUrlStr = cleanExpUrlStr.replaceFirst("http:.*?/ats/", "");
         expUrl.setCleanExpUrlStr(cleanExpUrlStr);
         rdDetails.logf("Cln Expected: %s\n", cleanExpUrlStr);
      }

      // First try to match exact without reg-exp eg: without "{"; hasParameter == false
      for (ExpectedUrl expUrl : data.expected) {

         String cleanExpUrlStr = expUrl.getCleanExpUrlStr();
         if (expUrl.hasParameter()) {
            continue;
         }
         for (ActualUrl actUrl : data.actuals) {
            if (actUrl.isSkip() || actUrl.isMatch()) {
               continue;
            }
            // Skip if this has parameter like "/2342343/" as no hard-coded URL should have a number
            if (actUrl.hasParameter()) {
               rdDetails.logf("\nSkipping EQUALS cause HAS PARAMETER: %s\n", expUrl.getExpectedUrl());
               continue;
            }
            String cleanActUrlStr = actUrl.getCleanActUrlStr();
            if (cleanActUrlStr.equals(cleanExpUrlStr)) { // switched from endsWith
               rdDetails.logf("\nFul Expected: %s\n", expUrl.getExpectedUrl());
               rdDetails.logf("Cln Expected: %s\n", cleanExpUrlStr);
               rdDetails.logf("Matched CLEAN EQUALS: \n  ");
               rdDetails.logf("---cln exp: %s\n", cleanExpUrlStr);
               rdDetails.logf("---cln act: %s\n", cleanActUrlStr);
               actUrl.setMatch();
               expUrl.setActualMatch(actUrl);
               break;
            }
         }
      }

      // Next, try to match urls with parameters
      for (ExpectedUrl expUrl : data.expected) {
         if (expUrl.hasParameter()) {
            String cleanExpUrlStr = expUrl.getCleanExpUrlStr();
            String cleanPatternStr = cleanExpUrlStr;
            cleanPatternStr = cleanPatternStr.replaceAll("\\{.*?\\}", "[0-9a-zA-Z]+");
            cleanPatternStr = "^" + cleanPatternStr + "$";
            rdDetails.logf("\nCln Pattern: %s\n", cleanPatternStr);
            Pattern cleanPattern = Pattern.compile(cleanPatternStr);
            for (ActualUrl actUrl : data.actuals) {
               if (actUrl.isSkip() || actUrl.isMatch()) {
                  continue;
               }
               Matcher m = cleanPattern.matcher(actUrl.getActualUrl());
               if (m.find()) {
                  rdDetails.logf("Ful Expected: %s\n", expUrl.getExpectedUrl());
                  rdDetails.logf("Cln Expected: %s\n", cleanExpUrlStr);
                  rdDetails.logf("Matched REGEX EQUALS: \n  ");
                  rdDetails.logf("---cln exp: %s\n", cleanExpUrlStr);
                  rdDetails.logf("---cln act: %s\n", actUrl.getActualUrl());
                  actUrl.setMatch();
                  expUrl.setActualMatch(actUrl);
                  break;
               }
            }
         }
      }
   }

   @SuppressWarnings("null")
   private void getExpectedPathsFromServices() {
      String urlPageHtml = AHTML.getUrlPageHtml(atsApi.getApplicationServerBase() + "/ats?_wadl");
      urlPageHtml = urlPageHtml.replaceFirst("Resources.*$", "");
      Pattern p = Pattern.compile("<a href=\".*?\">(.*?)</a>");
      Matcher m = p.matcher(urlPageHtml);
      ExpectedUrl expected = null;
      while (m.find()) {
         String match = m.group(1);
         if (match.contains("http")) {
            expected = new ExpectedUrl();
            expected.setExpectedUrl(match);
         } else {
            expected.addExpectedCrudType(match);
         }
         data.addExpected(expected);
      }
   }

   private void getActualPathsFromTests() {
      List<Map<String, String>> results = atsApi.getQueryServiceServer().query(
         "select msg_args from osee_activity where type_id = ?", CoreActivityTypes.JAXRS_METHOD_CALL);
      List<String> unique = new ArrayList<>();
      for (Map<String, String> map : results) {
         for (String str : map.values()) {
            if (!unique.contains(str)) {
               unique.add(str);
               String[] crudAndUrl = str.split(" ");
               ActualUrl actual = new ActualUrl(crudAndUrl[0], crudAndUrl[1]);
               data.addActual(actual);
            }
         }
      }
   }

}
