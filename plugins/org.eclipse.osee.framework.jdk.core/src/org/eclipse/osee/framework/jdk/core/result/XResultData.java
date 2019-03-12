/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.jdk.core.result;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
      Info;
   }

   public static final Pattern ErrorPattern = Pattern.compile("Error: ");
   public static final Pattern WarningPattern = Pattern.compile("Warning: ");
   @JsonIgnore
   public List<IResultDataListener> listeners;
   public String title;
   private List<String> results = new LinkedList<>();
   private List<Id> ids = new LinkedList<>();
   // Use primitives for serialization
   private int errorCount;
   private int warningCount;
   private int infoCount;
   @JsonIgnore
   private boolean enableOseeLog;

   public XResultData() {
      this(true);
   }

   public XResultData(boolean enableOseeLog) {
      super();
      this.enableOseeLog = enableOseeLog;
      clear();
   }

   public XResultData(boolean enableOseeLog, IResultDataListener... listeners) {
      super();
      this.enableOseeLog = enableOseeLog;
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

   /**
    * Adds string with newline to log as error
    */
   public void error(String str) {
      logStr(Type.Severe, str + "\n");
   }

   public void errorf(String formatStr, Object... objs) {
      logStr(Type.Severe, String.format(formatStr + "\n", objs));
   }

   /**
    * Adds string with newline to log as warning
    */
   public void warning(String str) {
      logStr(Type.Warning, str + "\n");
   }

   public void warningf(String formatStr, Object... objs) {
      logStr(Type.Warning, String.format(formatStr + "\n", objs));
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

   public void logStr(Type type, final String str) {
      bumpCount(type, 1);
      String resultStr = "";
      if (type == Type.Warning) {
         resultStr = "Warning: " + str;
      } else if (type == Type.Severe) {
         resultStr = "Error: " + str;
      } else {
         resultStr = str;
      }
      addRaw(resultStr);
      if (listeners != null) {
         for (IResultDataListener listener : listeners) {
            listener.log(type, resultStr);
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

   public boolean isEnableOseeLog() {
      return enableOseeLog;
   }

   public void setEnableOseeLog(boolean enableOseeLog) {
      this.enableOseeLog = enableOseeLog;
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

   public List<Id> getIds() {
      return ids;
   }

   public void setIds(List<Id> ids) {
      this.ids = ids;
   }

}
