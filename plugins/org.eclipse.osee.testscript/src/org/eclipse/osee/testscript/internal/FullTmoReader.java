/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript.internal;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Ryan T. Baldwin
 */
public class FullTmoReader {

   private TMO_LEVEL currentLevel = TMO_LEVEL.TESTSCRIPT;

   private boolean inAttention = false;
   private boolean inExecutedBy = false;
   private boolean inOteLog = false;
   private boolean inRuntimeVersions;
   private boolean inTracability = false;
   private boolean inVersionInformation = false;
   private boolean inWitnesses = false;

   private ScriptDefToken currentScript;
   private ScriptResultToken currentScriptResult;
   private TestCaseToken currentTestCase;
   private Stack<TestPointToken> currentTestPoints;
   private AttentionLocationToken currentAttentionLocation;
   private StackTraceToken currentStackTrace;
   private ScriptLogToken currentOteLog;
   private TraceToken currentTrace;
   private LoggingSummaryToken currentLoggingSummary;
   private ErrorEntryToken currentErrorEntry;
   private InfoToken currentInfo;
   private InfoGroupToken currentInfoGroup;

   private final SimpleDateFormat executionDateFormat =
      new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a z", Locale.ENGLISH); // August 31, 2023 10:52:01 PM MST
   private final SimpleDateFormat timeSummaryDateFormat =
      new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH); // Thu Aug 31 22:43:54 MST 2023

   public ScriptDefToken getScriptDefinition(InputStream stream) {
      // Start reading assuming it is a new script definition. Once the script name is read, we will query
      // for an existing definition with that name and use that if it is found.
      currentScript = new ScriptDefToken(-1L, "");
      currentScriptResult = new ScriptResultToken(-1L, "");
      currentScript.getScriptResults().add(currentScriptResult);
      currentTestPoints = new Stack<>();

      try {
         SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
         saxParser.parse(stream, new TmoHandler());
      } catch (ParserConfigurationException | SAXException | IOException ex) {
         System.out.println(ex);
      }

      return currentScript;
   }

   private class TmoHandler extends AbstractSaxHandler {

      private final Map<String, AbstractTmoHandler> handlers = new HashMap<>();

      public TmoHandler() {
         handlers.put("Actual", new ActualHandler());
         handlers.put("Attention", new AttentionHandler());
         handlers.put("CheckGroup", new CheckGroupHandler());
         handlers.put("CheckPoint", new TestPointHandler());
         handlers.put("Config", new ConfigHandler());
         handlers.put("CurrentProcessor", new CurrentProcessorHandler());
         handlers.put("ElapsedTime", new ElapsedTimeHandler());
         handlers.put("Environment", new EnvironmentHandler());
         handlers.put("ExecutedBy", new ExecutedByHandler());
         handlers.put("ExecutionDate", new ExecutionDateHandler());
         handlers.put("Expected", new ExpectedHandler());
         handlers.put("GroupName", new GroupNameHandler());
         handlers.put("Info", new InfoHandler());
         handlers.put("InfoGroup", new InfoGroupHandler());
         handlers.put("isInteractive", new isInteractiveHandler());
         handlers.put("Location", new LocationHandler());
         handlers.put("Message", new MessageHandler());
         handlers.put("Name", new NameHandler());
         handlers.put("Number", new NumberHandler());
         handlers.put("NumberOfTransmissions", new NumberOfTransmissionsHandler());
         handlers.put("OteLog", new OteLogHandler());
         handlers.put("Qualification", new QualificationHandler());
         handlers.put("Result", new ResultHandler());
         handlers.put("RetryGroup", new RetryGroupHandler());
         handlers.put("RuntimeVersions", new RuntimeVersionsHandler());
         handlers.put("ScriptInit", new ScriptInitHandler());
         handlers.put("ScriptName", new ScriptNameHandler());
         handlers.put("ScriptResult", new NoOpHandler());
         handlers.put("ScriptVersion", new ScriptVersionHandler());
         handlers.put("SoftKeyInfoGroup", new InfoGroupHandler());
         handlers.put("Stacktrace", new StackTraceHandler());
         handlers.put("Summary", new SummaryHandler());
         handlers.put("SystemInfo", new SystemInfoHandler());
         handlers.put("TestCase", new TestCaseHandler());
         handlers.put("TestPoint", new TestPointHandler());
         handlers.put("TestPointName", new NameHandler());
         handlers.put("TestPointResults", new TestPointResultsHandler());
         handlers.put("TestScript", new TestScriptHandler());
         handlers.put("Throwable", new ThrowableHandler());
         handlers.put("Time", new TimeHandler());
         handlers.put("TimeSummary", new TimeSummaryHandler());
         handlers.put("Tracability", new TracabilityHandler());
         handlers.put("User", new UserHandler());
         handlers.put("UutErrorEntry", new UutErrorEntryHandler());
         handlers.put("UutLoggingInfo", new NoOpHandler());
         handlers.put("Version", new VersionHandler());
         handlers.put("VersionInformation", new VersionInformationHandler());
         handlers.put("Witnesses", new WitnessesHandler());
      }

      @Override
      public void startElementFound(String uri, String localName, String qName, Attributes attributes)
         throws Exception {
         AbstractTmoHandler handler = handlers.get(qName);
         if (handler != null) {
            handler.startElementFound(uri, localName, qName, attributes);
         } else {
            //            throw new UnsupportedOperationException(qName + " does not have a handler");
            System.out.println("No handler for: " + qName);
         }
      }

      @Override
      public void endElementFound(String uri, String localName, String qName) throws Exception {
         AbstractTmoHandler handler = handlers.get(qName);
         if (handler != null) {
            handler.endElementFound(uri, localName, qName, this.getContents().trim());
         } else {
            //            throw new UnsupportedOperationException(qName + " does not have a handler");
         }
      }

      // ----- HANDLER CLASSES -----

      private class ActualHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (Strings.isValid(content)) {
               currentTestPoints.peek().setActual(content);
            }
         }

      }

      private class AttentionHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            inAttention = true;
            currentAttentionLocation = new AttentionLocationToken();
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (currentLevel.equals(TMO_LEVEL.TESTSCRIPT)) {
               currentScriptResult.getAttentionMessages().add(currentAttentionLocation);
            } else if (currentLevel.equals(TMO_LEVEL.TESTCASE)) {
               currentTestCase.getAttentionMessages().add(currentAttentionLocation);
            }
            currentAttentionLocation = null;
            inAttention = false;
         }

      }

      private class CheckGroupHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentLevel = TMO_LEVEL.TESTPOINT;
            currentTestPoints.push(new TestPointToken(-1L, ""));
            currentTestPoints.peek().setGroupType("CheckGroup");
            String mode = attributes.getValue("Mode");
            if (Strings.isValid(mode)) {
               currentTestPoints.peek().setGroupOperator(mode);
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            TestPointToken testPoint = currentTestPoints.pop();
            if (currentTestPoints.size() > 0) {
               currentTestPoints.peek().getSubTestPoints().add(testPoint);
               currentLevel = TMO_LEVEL.TESTPOINT;
            } else {
               currentTestCase.getTestPoints().add(testPoint);
               currentLevel = TMO_LEVEL.TESTCASE;
            }
         }

      }

      private class ConfigHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            String machineName = attributes.getValue("machineName");
            if (Strings.isValid(machineName)) {
               currentScriptResult.setMachineName(machineName);
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            // Do nothing
         }

      }

      private class CurrentProcessorHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            String proc = attributes.getValue("proc");
            if (Strings.isValid(proc)) {
               currentScriptResult.setProcessorId(proc);
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            // Do nothing
         }

      }

      private class ElapsedTimeHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (Strings.isValid(content)) {
               currentTestPoints.peek().setElapsedTime(Integer.parseInt(content));
            }
         }

      }

      private class EnvironmentHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (Strings.isValid(content)) {
               currentScript.setExecutionEnvironment(content);
               currentScriptResult.setExecutionEnvironment(content);
            }
         }

      }

      private class ExecutedByHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            inExecutedBy = true;
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            inExecutedBy = false;
         }

      }

      private class ExecutionDateHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            try {
               currentScriptResult.setExecutionDate(executionDateFormat.parse(content));
            } catch (ParseException ex) {
               System.out.println("Could not parse Execution Date");
               System.out.println(ex);
            }
         }

      }

      private class ExpectedHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (Strings.isValid(content)) {
               currentTestPoints.peek().setExpected(content);
            }
         }

      }

      private class GroupNameHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            currentTestPoints.peek().setGroupName(content);
         }

      }

      private class InfoHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentInfo = new InfoToken(-1L, "");
            String title = attributes.getValue("title");
            if (Strings.isValid(title)) {
               currentInfo.setName(title);
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            currentInfoGroup.getInfo().add(currentInfo);
            currentInfo = null;
         }

      }

      private class InfoGroupHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentInfoGroup = new InfoGroupToken(-1L, "");
            currentInfoGroup.setGroupType(qName);
            String title = attributes.getValue("title");
            if (Strings.isValid(title)) {
               currentInfoGroup.setName(title);
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            currentTestPoints.peek().getInfoGroups().add(currentInfoGroup);
            currentInfoGroup = null;
         }

      }

      private class isInteractiveHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (currentLevel.equals(TMO_LEVEL.TESTPOINT)) {
               currentTestPoints.peek().setInteractive(Boolean.parseBoolean(content));
            }
         }

      }

      private class LocationHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentAttentionLocation = new AttentionLocationToken(-1L, "");
            String id = attributes.getValue("id");
            if (Strings.isValid(id)) {
               currentAttentionLocation.setLocationId(id);
            }
            inAttention = true;
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            currentTestPoints.peek().getLocations().add(currentAttentionLocation);
            currentAttentionLocation = null;
            inAttention = false;
         }

      }

      private class MessageHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (inAttention) {
               currentAttentionLocation.setAttentionMessage(content);
               return;
            }
            if (inOteLog) {
               currentOteLog.setLogMessage(content);
               return;
            }
         }

      }

      private class NameHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (inExecutedBy) {
               currentScriptResult.setExecutedBy(content);
               return;
            }
            if (inWitnesses) {
               currentScriptResult.getWitnesses().add(content);
               return;
            }
            if (currentLevel.equals(TMO_LEVEL.TESTCASE)) {
               currentTestCase.setName(content);
               return;
            }
            if (currentLevel.equals(TMO_LEVEL.TESTPOINT)) {
               currentTestPoints.peek().setName(content);
            }
         }

      }

      private class NoOpHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            // Do nothing
         }

      }

      private class NumberHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            double number = Double.parseDouble(content);
            if (currentLevel.equals(TMO_LEVEL.TESTCASE)) {
               currentTestCase.setTestNumber(number);
            }
            if (currentLevel.equals(TMO_LEVEL.TESTPOINT)) {
               currentTestPoints.peek().setTestNumber(number);
            }
         }

      }

      private class NumberOfTransmissionsHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (Strings.isValid(content)) {
               currentTestPoints.peek().setTransmissionCount(Integer.parseInt(content));
            }
         }

      }

      private class OteLogHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            inOteLog = true;
            currentOteLog = new ScriptLogToken(-1L, "Script Log");
            currentOteLog.setLogLevel(attributes.getValue("Level"));
            currentOteLog.setLogger(attributes.getValue("Logger"));
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (inTracability) {
               currentTrace.getLogs().add(currentOteLog);
            } else if (currentLevel.equals(TMO_LEVEL.TESTSCRIPT)) {
               currentScriptResult.getLogs().add(currentOteLog);
            } else if (currentLevel.equals(TMO_LEVEL.TESTCASE)) {
               currentTestCase.getLogs().add(currentOteLog);
            }
            currentOteLog = null;
            inOteLog = false;
         }

      }

      private class QualificationHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentScriptResult.setQualificationLevel(attributes.getValue("Level"));
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            // Do nothing
         }

      }

      private class ResultHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (currentLevel.equals(TMO_LEVEL.TESTPOINT)) {
               currentTestPoints.peek().setResult(content);
            }
         }

      }

      private class RetryGroupHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentLevel = TMO_LEVEL.TESTPOINT;
            currentTestPoints.push(new TestPointToken(-1L, ""));
            currentTestPoints.peek().setGroupType("RetryGroup");
            String mode = attributes.getValue("Mode");
            if (Strings.isValid(mode)) {
               currentTestPoints.peek().setGroupOperator(mode);
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            TestPointToken testPoint = currentTestPoints.pop();
            if (currentTestPoints.size() > 0) {
               currentTestPoints.peek().getSubTestPoints().add(testPoint);
               currentLevel = TMO_LEVEL.TESTPOINT;
            } else {
               currentTestCase.getTestPoints().add(testPoint);
               currentLevel = TMO_LEVEL.TESTCASE;
            }
         }

      }

      private class RuntimeVersionsHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            inRuntimeVersions = true;

         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            inRuntimeVersions = false;
         }

      }

      private class ScriptInitHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentLevel = TMO_LEVEL.TESTCASE;
            currentTestCase = new TestCaseToken(-1L, "");
            currentTestCase.setInitial(true);
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            currentScriptResult.getTestCases().add(currentTestCase);
            currentTestCase = null;
            currentLevel = TMO_LEVEL.TESTSCRIPT;
         }

      }

      private class ScriptNameHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (Strings.isValid(content)) {
               String[] split = content.split("\\.");
               String name = content;
               if (split.length > 1) {
                  name = split[split.length - 1];
               }
               currentScript.setName(name);
               currentScript.setFullScriptName(content);
            }
         }

      }

      private class ScriptVersionHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            String revision = attributes.getValue("revision");
            String repositoryType = attributes.getValue("repositoryType");
            String lastAuthor = attributes.getValue("lastAuthor");
            String lastModified = attributes.getValue("lastModified");
            String modifiedFlag = attributes.getValue("modifiedFlag");
            String url = attributes.getValue("url");
            if (Strings.isValid(revision)) {
               currentScript.setRevision(revision);
            }
            if (Strings.isValid(repositoryType)) {
               currentScript.setRepositoryType(repositoryType);
            }
            if (Strings.isValid(lastAuthor)) {
               currentScript.setLastAuthor(lastAuthor);
            }
            if (Strings.isValid(lastModified)) {
               System.out.println("LAST MODIFIED: " + currentScript.getName()); // TODO
               //               currentScript.setLastModified(lastModified); // TODO
            }
            if (Strings.isValid(modifiedFlag)) {
               currentScript.setModifiedFlag(modifiedFlag);
            }
            if (Strings.isValid(url)) {
               currentScript.setRepositoryUrl(url);
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            // Do nothing
         }

      }

      private class StackTraceHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentStackTrace = new StackTraceToken(-1L, "");
            String source = attributes.getValue("source");
            String line = attributes.getValue("line");
            if (Strings.isValid(source)) {
               currentStackTrace.setSource(source);
            }
            if (Strings.isValid(line)) {
               currentStackTrace.setLine(line);
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (inAttention) {
               currentAttentionLocation.getStackTraces().add(currentStackTrace);
            }
            currentStackTrace = null;
         }

      }

      private class SummaryHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentLoggingSummary = new LoggingSummaryToken(-1L, "");
            String nodeId = attributes.getValue("NODE_ID");
            String startNumber = attributes.getValue("START_NUMBER");
            String informationalCount = attributes.getValue("INFORMATIONAL_COUNT");
            String minorCount = attributes.getValue("MINOR_COUNT");
            String seriousCount = attributes.getValue("SERIOUS_COUNT");
            String criticalCount = attributes.getValue("CRITICAL_COUNT");
            String exceptionCount = attributes.getValue("EXCEPTION_COUNT");
            if (Strings.isValid(nodeId)) {
               currentLoggingSummary.setSummaryId(Integer.parseInt(nodeId));
            }
            if (Strings.isValid(startNumber)) {
               currentLoggingSummary.setStartNumber(Integer.parseInt(startNumber));
            }
            if (Strings.isValid(informationalCount)) {
               currentLoggingSummary.setInformationalCount(Integer.parseInt(informationalCount));
            }
            if (Strings.isValid(minorCount)) {
               currentLoggingSummary.setMinorCount(Integer.parseInt(minorCount));
            }
            if (Strings.isValid(seriousCount)) {
               currentLoggingSummary.setSeriousCount(Integer.parseInt(seriousCount));
            }
            if (Strings.isValid(criticalCount)) {
               currentLoggingSummary.setCriticalCount(Integer.parseInt(criticalCount));
            }
            if (Strings.isValid(exceptionCount)) {
               currentLoggingSummary.setExceptionCount(Integer.parseInt(exceptionCount));
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            currentScriptResult.getLoggingSummaries().add(currentLoggingSummary);
            currentLoggingSummary = null;
         }

      }

      private class SystemInfoHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            String osName = attributes.getValue("osName");
            String osVersion = attributes.getValue("osVersion");
            String osArch = attributes.getValue("osArch");
            String oseeVersion = attributes.getValue("oseeVersion");
            String javaVersion = attributes.getValue("javaVersion");
            String oseeServerTitle = attributes.getValue("oseeServerTitle");
            if (Strings.isValid(osName)) {
               currentScriptResult.setOsName(osName);
            }
            if (Strings.isValid(osVersion)) {
               currentScriptResult.setOsVersion(osVersion);
            }
            if (Strings.isValid(osArch)) {
               currentScriptResult.setOsArchitecture(osArch);
            }
            if (Strings.isValid(oseeVersion)) {
               currentScriptResult.setOseeVersion(oseeVersion);
            }
            if (Strings.isValid(javaVersion)) {
               currentScriptResult.setJavaVersion(javaVersion);
            }
            if (Strings.isValid(oseeServerTitle)) {
               currentScriptResult.setOseeServer(oseeServerTitle);
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            // Do nothing
         }

      }

      private class TestCaseHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentLevel = TMO_LEVEL.TESTCASE;
            currentTestCase = new TestCaseToken(-1L, "");
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            currentScriptResult.getTestCases().add(currentTestCase);
            currentTestCase = null;
            currentLevel = TMO_LEVEL.TESTSCRIPT;
         }

      }

      private class TestPointHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentLevel = TMO_LEVEL.TESTPOINT;
            currentTestPoints.push(new TestPointToken(-1L, ""));
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            TestPointToken testPoint = currentTestPoints.pop();
            if (currentTestPoints.size() > 0) {
               currentTestPoints.peek().getSubTestPoints().add(testPoint);
               currentLevel = TMO_LEVEL.TESTPOINT;
            } else {
               currentTestCase.getTestPoints().add(testPoint);
               currentLevel = TMO_LEVEL.TESTCASE;
            }
         }

      }

      private class TestPointResultsHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            String pass = attributes.getValue("pass");
            String fail = attributes.getValue("fail");
            String aborted = attributes.getValue("aborted");
            if (Strings.isValid(pass)) {
               currentScriptResult.setPassedCount(Integer.parseInt(pass));
            }
            if (Strings.isValid(fail)) {
               currentScriptResult.setFailedCount(Integer.parseInt(fail));
            }
            if (Strings.isValid(aborted)) {
               currentScriptResult.setScriptAborted(Boolean.parseBoolean(aborted));
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            // Do nothing
         }

      }

      private class TestScriptHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentLevel = TMO_LEVEL.TESTSCRIPT;
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            // Do nothing
         }

      }

      private class TimeHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (inAttention) {
               currentAttentionLocation.setLocationTime(content);
            }
         }

      }

      private class TimeSummaryHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            String milliseconds = attributes.getValue("milliseconds");
            String startDate = attributes.getValue("startDate");
            String endDate = attributes.getValue("endDate");
            if (Strings.isValid(milliseconds)) {
               currentScriptResult.setElapsedTime(Integer.parseInt(milliseconds));
               try {
                  currentScriptResult.setStartDate(timeSummaryDateFormat.parse(startDate));
               } catch (ParseException ex) {
                  System.out.println("Could not parse Start Date");
                  System.out.println(ex);
               }
               try {
                  currentScriptResult.setEndDate(timeSummaryDateFormat.parse(endDate));
               } catch (ParseException ex) {
                  System.out.println("Could not parse End Date");
                  System.out.println(ex);
               }
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            // Do nothing
         }

      }

      private class ThrowableHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (inOteLog) {
               currentOteLog.setLogThrowable(content);
               return;
            }
         }

      }

      private class TracabilityHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentTrace = new TraceToken(-1L, "");
            inTracability = true;
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (currentLevel.equals(TMO_LEVEL.TESTCASE)) {
               currentTestCase.getTrace().add(currentTrace);
            }
            currentTrace = null;
            inTracability = false;
         }

      }

      private class UserHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            String name = attributes.getValue("name");
            String email = attributes.getValue("email");
            String id = attributes.getValue("id");
            if (Strings.isValid(name)) {
               currentScriptResult.setUserName(name);
            }
            if (Strings.isValid(email)) {
               currentScriptResult.setEmail(email);
            }
            if (Strings.isValid(id)) {
               currentScriptResult.setUserId(id);
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            // Do nothing
         }

      }

      private class UutErrorEntryHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentErrorEntry = new ErrorEntryToken(-1L, "");
            String nodeId = attributes.getValue("NODE_ID");
            String severity = attributes.getValue("severity");
            String version = attributes.getValue("version");
            String count = attributes.getValue("count");
            if (Strings.isValid(nodeId)) {
               currentErrorEntry.setSummaryId(Integer.parseInt(nodeId));
            }
            if (Strings.isValid(severity)) {
               currentErrorEntry.setErrorSeverity(severity);
            }
            if (Strings.isValid(version)) {
               currentErrorEntry.setErrorVersion(version);
            }
            if (Strings.isValid(count)) {
               currentErrorEntry.setErrorCount(Integer.parseInt(count));
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            currentLoggingSummary.getErrorEntries().add(currentErrorEntry);
            currentErrorEntry = null;
         }

      }

      private class VersionHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            if (inVersionInformation) {
               VersionInformationToken versionToken = new VersionInformationToken(-1L, "");
               String name = attributes.getValue("name");
               String version = attributes.getValue("version");
               String versionUnit = attributes.getValue("versionUnit");
               String underTest = attributes.getValue("underTest");

               if (Strings.isValid(name)) {
                  versionToken.setName(name);
               }
               if (Strings.isValid(version)) {
                  versionToken.setVersionInfo(version);
               }
               if (Strings.isValid(versionUnit)) {
                  versionToken.setVersionUnit(versionUnit);
               }
               if (Strings.isValid(underTest)) {
                  versionToken.setUnderTest(Boolean.parseBoolean(underTest));
               }

               currentScriptResult.getVersionInformation().add(versionToken);
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (inRuntimeVersions) {
               if (Strings.isValid(content)) {
                  currentScriptResult.getRuntimeVersions().add(content);
               }
            }
         }

      }

      private class VersionInformationHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            inVersionInformation = true;

         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            inVersionInformation = false;
         }

      }

      private class WitnessesHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            inWitnesses = true;
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            inWitnesses = false;
         }

      }

      private abstract class AbstractTmoHandler {
         public abstract void startElementFound(String uri, String localName, String qName, Attributes attributes);

         public abstract void endElementFound(String uri, String localName, String qName, String content);
      }

   }

   private enum TMO_LEVEL {
      TESTSCRIPT,
      TESTCASE,
      TESTPOINT,
   }

}
