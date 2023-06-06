/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.jdk.core.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.result.table.XResultTable;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Used to log Info, Warning and Errors to multiple locations (logger, stderr/out and XResultView). Upon completion, a
 * call to report(title) will open results in the ResultsView
 *
 * @author Donald G. Dunne
 */

public class XResultData {

   public static enum Type {
      Severe,
      Warning,
      Info,
      Success,
      ConsoleErr,
      ConsoleOut;
   }

   public static XResultData EMPTY_RD = new XResultData();
   public static final Pattern ErrorPattern = Pattern.compile("Error: ");
   public static final Pattern WarningPattern = Pattern.compile("Warning: ");
   public static final XResultData OK_STATUS = EMPTY_RD;

   @JsonIgnore
   public List<IResultDataListener> listeners;
   public String title;
   private List<String> results = new LinkedList<>();
   private List<String> ids = new LinkedList<>();
   // Use primitives for serialization
   private int errorCount;
   private int warningCount;
   private int infoCount;
   @JsonIgnore
   private boolean logToSysErr;
   public List<XResultTable> tables = new ArrayList<XResultTable>();
   private String txId = "";
   private final CountingMap<String> keyToTimeSpentMap = new CountingMap<>();
   private final Map<String, Date> keyTimeStart = new HashMap<>();

   public XResultData() {
      this(false);
   }

   public XResultData(boolean enableOseeLog) {
      super();
      this.logToSysErr = enableOseeLog;
      clear();
   }

   public XResultData(boolean logToSysErr, IResultDataListener... listeners) {
      super();
      this.logToSysErr = logToSysErr;
      clear();
      if (listeners != null && listeners.length > 0) {
         this.listeners = Arrays.asList(listeners);
      }
   }

   public void clear() {
      results.clear();
      errorCount = 0;
      warningCount = 0;
      infoCount = 0;
   }

   public void combine(XResultData other) {
      if (this.equals(other)) {
         return;
      }
      this.errorCount += other.errorCount;
      this.warningCount += other.warningCount;
      this.infoCount += other.infoCount;
      this.results.addAll(other.results);
      this.ids.addAll(other.ids);
      if (other.listeners != null) {
         this.listeners.addAll(other.listeners);
      }
   }

   public void addRaw(String str) {
      results.add(str);
   }

   /**
    * Adds string with newline to log
    */
   public void log(String str) {
      logStr(Type.Info, str + "\n");
   }

   public void logf(String formatStr, Object... objs) {
      logStr(Type.Info, String.format(formatStr, objs));
   }

   public void success(String formatStr, Object... objs) {
      logStr(Type.Success, String.format(formatStr, objs));
   }

   /**
    * Adds string with newline to log as error
    */
   public void error(String str) {
      logStr(Type.Severe, str + "\n");
   }

   public void errorf(String formatStr, Object... objs) {
      logStr(Type.Severe, String.format(formatStr, objs));
   }

   /**
    * Adds string with newline to log as warning
    */
   public void warning(String str) {
      logStr(Type.Warning, str + "\n");
   }

   public void warningf(String formatStr, Object... objs) {
      logStr(Type.Warning, String.format(formatStr, objs));
   }

   public boolean isEmpty() {
      return toString().equals("");
   }

   public void bumpCount(Type type, int byAmt) {
      if (type == Type.Severe) {
         errorCount++;
      } else if (type == Type.Warning) {
         warningCount++;
      } else {
         infoCount++;
      }
   }

   public void logStr(Type type, final String format, Object... data) {
      bumpCount(type, 1);
      String resultStr = "";
      if (type == Type.Warning) {
         // If no data, do not send back into formatter or exceptions could occur
         if (data.length == 0) {
            resultStr = "Warning: " + format;
         } else {
            resultStr = "Warning: " + String.format(format, data);
         }
      } else if (type == Type.Severe) {
         // If no data, do not send back into formatter or exceptions could occur
         if (data.length == 0) {
            resultStr = "Error: " + format;
         } else {
            resultStr = "Error: " + String.format(format, data);
         }
      } else if (type == Type.Success) {
         // If no data, do not send back into formatter or exceptions could occur
         if (data.length == 0) {
            resultStr = "Success: " + format;
         } else {
            resultStr = "Success: " + String.format(format, data);
         }
      } else {
         // If no objs, do not send back into formatter or exceptions could occur
         if (data.length == 0) {
            resultStr = format;
         } else {
            resultStr = String.format(format, data);
         }
      }
      addRaw(resultStr);
      if (listeners != null) {
         for (IResultDataListener listener : listeners) {
            listener.log(type, resultStr);
         }
      }
      if (isLogToSysErr()) {
         /**
          * This is the only valid use of err.println. It allows XResultData to be used to log to console during
          * development. Log statements can remain but enableOseeLog turned off for commit.
          */
         if (type == Type.Severe || type == Type.ConsoleErr) {
            System.err.print(resultStr);
         } else {
            System.out.print(resultStr);
         }
      }
   }

   public void dispose() {
      // provided for subclass implementation
   }

   @Override
   public String toString() {
      return Collections.toString("", results);
   }

   private int getCount(Type type) {
      if (type == Type.Severe) {
         return errorCount;
      } else if (type == Type.Warning) {
         return warningCount;
      } else {
         return infoCount;
      }
   }

   public int getNumErrors() {
      return getCount(Type.Severe);
   }

   /**
    * XResultData counts number of errors logged with logError, however users can insert their own "Error: " strings to
    * produce errors. This counts based on these occurrences.
    */
   public int getNumErrorsViaSearch() {
      return Lib.getMatcherCount(ErrorPattern, toString());
   }

   /**
    * XResultData counts number of warnings logged with logWarning, however users can insert their own "Error: " strings
    * to produce errors. This counts based on these occurrences.
    */
   public int getNumWarningsViaSearch() {
      return Lib.getMatcherCount(WarningPattern, toString());
   }

   public int getNumWarnings() {
      return getCount(Type.Warning);
   }

   public boolean isErrors() {
      return getNumErrors() > 0;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public void setResults(List<String> results) {
      this.results = results;
   }

   public List<String> getResults() {
      return results;
   }

   public int getErrorCount() {
      return errorCount;
   }

   public void setErrorCount(int errorCount) {
      this.errorCount = errorCount;
   }

   public int getWarningCount() {
      return warningCount;
   }

   public void setWarningCount(int warningCount) {
      this.warningCount = warningCount;
   }

   public int getInfoCount() {
      return infoCount;
   }

   public void setInfoCount(int infoCount) {
      this.infoCount = infoCount;
   }

   public void validateNotNullOrEmpty(String value, String message, Object... data) {
      if (!Strings.isValid(value)) {
         errorf(message + " can not be null or emtpy", data);
      }
   }

   public void validateTrue(boolean value, String message, Object... data) {
      if (!value) {
         errorf(message, data);
      }
   }

   public void validateNotNull(Date date, String message, Object... data) {
      if (date == null) {
         errorf(message, data);
      }
   }

   public void validateNotNull(String str, String message, Object... data) {
      if (str == null) {
         errorf(message, data);
      }
   }

   public void validateNotNull(Integer value, String message, Object... data) {
      if (value == null) {
         errorf(message, data);
      }
   }

   public List<String> getIds() {
      return ids;
   }

   public void setIds(List<String> ids) {
      this.ids = ids;
   }

   public boolean isSuccess() {
      return !isErrors();
   }

   public boolean isLogToSysErr() {
      return logToSysErr;
   }

   public void setLogToSysErr(boolean logToSysErr) {
      this.logToSysErr = logToSysErr;
   }

   public boolean isFailed() {
      return !isSuccess();
   }

   public List<XResultTable> getTables() {
      return tables;
   }

   public void setTables(List<XResultTable> tables) {
      this.tables = tables;
   }

   public void addResultsTag() {
      log("PUT_RESULTS_HERE");
   }

   @JsonIgnore
   public String getHtml() {
      String html = toString();
      html = AHTML.textToHtml(html);
      html = html.replaceFirst("PUT_RESULTS_HERE", getResultsString());
      html = html.replaceAll("Error:", Matcher.quoteReplacement(AHTML.color("RED", "Error:")));
      html = html.replaceAll("Warning:", Matcher.quoteReplacement(AHTML.color("BLUE", "Warning:")));
      html = html.replaceAll("\n", "<br/>");
      return html;
   }

   private String getResultsString() {
      return String.format("Error: %s, Warning: %s", getErrorCount(), getWarningCount());
   }

   public void merge(XResultData rd) {
      this.errorCount += rd.getErrorCount();
      this.warningCount += rd.getWarningCount();
      this.infoCount += rd.getInfoCount();
      addRaw(rd.toString());
   }

   public String getTxId() {
      return txId;
   }

   public void setTxId(String txId) {
      this.txId = txId;
   }

   public void exceptionIfErrors(String title) {
      if (isErrors()) {
         throw new OseeCoreException(title + " - " + toString());
      }
   }

   public boolean isWarnings() {
      return getNumWarnings() > 0;
   }

   public void sortResults() {
      java.util.Collections.sort(results);
   }

   public void addTimeMapToResultData() {
      addTimeMapToResultData("");
   }

   public void addTimeMapToResultData(String... testPrefix) {
      log("\n\n<b>Time Spent in Tests</b>");
      long totalTime = 0;
      List<String> testNames = new ArrayList<>();
      testNames.addAll(keyToTimeSpentMap.keySet());
      java.util.Collections.sort(testNames);
      log(AHTML.beginMultiColumnTable(95, 2));
      // Sort tests
      for (String prefix : testPrefix) {
         for (String testName : testNames) {
            if (testName.startsWith(prefix)) {
               int testTime = keyToTimeSpentMap.get(testName);
               totalTime += testTime;
               logf(AHTML.addRowMultiColumnTable(testName,
                  (testTime / 60000) + " min or " + (testTime / 1000) + " sec or " + testTime + " millisec"));
            }
         }
      }
      log(AHTML.endMultiColumnTable());
      log("TOTAL (Test Time) - " + (totalTime / 60000) + " min or " + (totalTime / 1000) + " sec");
      log("\n");
   }

   public void logTimeStart(String key) {
      keyTimeStart.put(key, new Date());
   }

   public int logTimeSpent(String key) {
      Date now = new Date();
      Date start = keyTimeStart.get(key);
      if (start == null) {
         throw new IllegalArgumentException("No Start Key: " + key);
      }
      int spent = Long.valueOf(now.getTime() - start.getTime()).intValue();
      keyToTimeSpentMap.put(key, spent); // This adds time to existing
      return spent;
   }

   public boolean isOK() {
      return isSuccess();
   }

   public static XResultData valueOf(Type type, String messageId, String message, Exception ex) {
      XResultData rd = new XResultData();
      rd.logStr(type, "%s: %s - %s", messageId, message, Lib.exceptionToString(ex));
      return rd;
   }

   public static XResultData valueOf(Type type, String messageId, String message) {
      XResultData rd = new XResultData();
      rd.logStr(type, "%s: %s", messageId, message);
      return rd;
   }

   public void addTimeSpentAndClearTimeForKey(String key) {
      logTimeSpent(key);
      keyTimeStart.remove(key);
      System.err.println("keyTimeStart " + keyTimeStart);
      System.err.println("keyToTimeSpentMap " + keyToTimeSpentMap);
   }

}
