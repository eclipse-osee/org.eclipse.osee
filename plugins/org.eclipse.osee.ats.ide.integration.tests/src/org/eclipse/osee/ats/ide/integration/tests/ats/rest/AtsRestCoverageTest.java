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

package org.eclipse.osee.ats.ide.integration.tests.ats.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.junit.Assert;

/**
 * Calculate and report test coverage of ATS REST calls. <br/>
 * Actual REST calls are retrieved from the OSEE_ACTIVITY table that logs all server calls. <br/>
 * Expected come from the ats_?wadl which shows available REST calls
 *
 * @author Donald G. Dunne
 */
public class AtsRestCoverageTest {

   RestData data = new RestData();
   XResultData rd = new XResultData();
   /**
    * This number should not be reduced, ask if you do not know how to create tests for new REST calls.<br/>
    * Please increase this number as percent coverage goes up.
    */
   private final float MINIMUM_PERCENT_COVERAGE = Float.valueOf(44);

   @org.junit.Test
   public void test() {
      getActualPathsFromTests();
      getExpectedPathsFromServices();
      setSkips();
      matchup();
      float percentCoverage = report();
      Assert.assertTrue("ATS REST Coverage should not drop below " + MINIMUM_PERCENT_COVERAGE + ". Add REST tests!\n",
         percentCoverage > MINIMUM_PERCENT_COVERAGE);
   }

   private float report() {
      int match = 0;
      for (ActualUrl actUrl : data.actuals) {
         if (actUrl.isMatch()) {
            match++;
         }
      }

      List<String> matchedItems = new ArrayList<>();
      for (ExpectedUrl expectedUrl : data.expected) {
         if (expectedUrl.isMatch()) {
            matchedItems.add( //
               "\n  -- Expected: " + expectedUrl.getExpectedUrl() + //
                  "\n  -- Matched: " + expectedUrl.getActualMatch().getActualUrl() + "\n");
         } else {
            matchedItems.add("Unmatched: " + expectedUrl.getExpectedUrl() + "\n");
         }
      }
      matchedItems.sort(Comparator.naturalOrder());
      rd.log(matchedItems.toString());

      XResultData rd2 = new XResultData();
      rd2.log("ATS REST Test Coverage Report (AtsRestCoverageTest)\n\n");
      rd2.log("This report is to help improve ATS REST testing over time.");
      rd2.log("Actuals come from OSEE_ACIVITY where calls are logged during tests");
      rd2.log("Expected come from ats_?wadl which is scrubbed for the available REST calls for ATS");
      rd2.log("See \"Unmatched\" at the end to improve coverage");
      rd2.logf("\nExpected: %s\n", data.expected.size());
      rd2.logf("Matched: %s\n", match);
      rd2.logf("Unmatched: %s\n\n", data.expected.size() - match);

      float percent = 0;
      if (match != 0) {
         percent = ((float) match / (float) data.expected.size()) * 100;
      }
      rd2.logf("Percent Coverage: %2.1f\n\n", percent);
      rd2.merge(rd);

      XResultDataUI.reportAndOpen(rd2, "ATS REST Test Coverage", "atsCoverage.html");
      return percent;
   }

   private void setSkips() {
      List<ActualUrl> skips = new ArrayList<>();
      for (ActualUrl actUrl : data.actuals) {
         for (String skipStr : Arrays.asList("datastore", "session", "branch", "resources", "branches")) {
            if (actUrl.actualUrl.startsWith(skipStr)) {
               skips.add(actUrl);
               rd.logf("Skipped %s\n", actUrl);
               break;
            }
         }
      }
      rd.logf("Skipped %s urls\n", skips.size());
      data.actuals.removeAll(skips);

      for (ActualUrl rUrl : data.actuals) {
         rd.logf("Unskipped Actual: %s\n", rUrl);
      }
   }

   private void matchup() {
      for (ExpectedUrl expUrl : data.expected) {
         String expUrlStr = expUrl.expectedUrl;
         rd.logf("Ful Expected: %s\n", expUrlStr);
         String cleanUrl = expUrlStr;
         cleanUrl = cleanUrl.replaceFirst("http:.*?/ats/", "");
         rd.logf("Cln Expected: %s\n", cleanUrl);
         // if {} is in expected, try to matchup with path params / ids
         if (cleanUrl.contains("{")) {
            String cleanPatternStr = cleanUrl;
            cleanPatternStr = cleanPatternStr.replaceAll("\\{.*?\\}", "[0-9a-z]+");
            cleanPatternStr = cleanPatternStr + "$";
            rd.logf("Cln Pattern: %s\n", cleanPatternStr);
            Pattern cleanPattern = Pattern.compile(cleanPatternStr);
            for (ActualUrl actUrl : data.actuals) {
               if (actUrl.isSkip() || actUrl.isMatch()) {
                  continue;
               }
               Matcher m = cleanPattern.matcher(actUrl.getActualUrl());
               if (m.find()) {
                  rd.logf("Match Regex: \n  --- exp: %s\n  --- act: %s\n", cleanUrl, actUrl.actualUrl);
                  actUrl.setMatch();
                  expUrl.setActualMatch(actUrl);
                  break;
               }
            }
         } else {
            for (ActualUrl actUrl : data.actuals) {
               if (actUrl.isSkip() || actUrl.isMatch()) {
                  continue;
               }
               if (actUrl.getActualUrl().endsWith(cleanUrl)) {
                  rd.logf("Match EndsWith: \n  --- exp: %s\n  --- act: %s\n", cleanUrl, actUrl.actualUrl);
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
      String urlPageHtml = AHTML.getUrlPageHtml(AtsApiService.get().getApplicationServerBase() + "/ats?_wadl");
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
      List<Map<String, String>> results = AtsApiService.get().getQueryService().query(
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
