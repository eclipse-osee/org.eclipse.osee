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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Ryan T. Baldwin
 */
public class ImportTmoReader {

   private boolean inExecutedBy = false;
   private boolean inWitnesses = false;

   private ScriptDefToken currentScript;
   private ScriptResultToken currentScriptResult;
   private TestPointToken currentTestPoint;
   private TestPointToken currentCheckPoint;
   private TestPointToken currentCheckGroup;

   private final SimpleDateFormat executionDateFormat =
      new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss a z", Locale.ENGLISH); // August 31, 2023 10:52:01 PM MST
   private final SimpleDateFormat timeSummaryDateFormat =
      new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH); // Thu Aug 31 22:43:54 MST 2023

   public ScriptDefToken getScriptDefinition(File file, ArtifactId ciSet) {
      try {
         return this.getScriptDefinition(new FileInputStream(file), ciSet);
      } catch (IOException ex) {
         System.out.println(ex);
      }
      return new ScriptDefToken();
   }

   public ScriptDefToken getScriptDefinition(InputStream stream, ArtifactId ciSet) {
      // Start reading assuming it is a new script definition. Once the script name is read, we will query
      // for an existing definition with that name and use that if it is found.
      currentScript = new ScriptDefToken(-1L, "");
      currentScriptResult = new ScriptResultToken(-1L, "");
      currentScriptResult.setSetId(ciSet.getIdString());
      currentScript.getScriptResults().add(currentScriptResult);

      try {
         SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
         saxParser.parse(stream, new TmoHandler());
      } catch (ParserConfigurationException | SAXException | IOException ex) {
         System.out.println(ex);
      }

      currentScriptResult.setName(currentScript.getName());

      return currentScript;
   }

   private class TmoHandler extends AbstractSaxHandler {

      private final Map<String, AbstractTmoHandler> handlers = new HashMap<>();

      public TmoHandler() {
         handlers.put("Actual", new ActualHandler());
         handlers.put("CheckGroup", new CheckGroupHandler());
         handlers.put("CheckPoint", new CheckPointHandler());
         handlers.put("Config", new ConfigHandler());
         handlers.put("CurrentProcessor", new CurrentProcessorHandler());
         handlers.put("ElapsedTime", new ElapsedTimeHandler());
         handlers.put("Environment", new EnvironmentHandler());
         handlers.put("ExecutedBy", new ExecutedByHandler());
         handlers.put("ExecutionDate", new ExecutionDateHandler());
         handlers.put("Expected", new ExpectedHandler());
         handlers.put("GroupName", new GroupNameHandler());
         handlers.put("isInteractive", new isInteractiveHandler());
         handlers.put("Name", new NameHandler());
         handlers.put("Number", new NumberHandler());
         handlers.put("NumberOfTransmissions", new NumberOfTransmissionsHandler());
         handlers.put("Qualification", new QualificationHandler());
         handlers.put("Result", new ResultHandler());
         handlers.put("RetryGroup", new CheckGroupHandler());
         handlers.put("ScriptName", new ScriptNameHandler());
         handlers.put("ScriptVersion", new ScriptVersionHandler());
         handlers.put("SystemInfo", new SystemInfoHandler());
         handlers.put("TestPoint", new TestPointHandler());
         handlers.put("TestPointName", new NameHandler());
         handlers.put("TestPointResults", new TestPointResultsHandler());
         handlers.put("TimeSummary", new TimeSummaryHandler());
         handlers.put("User", new UserHandler());
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
            //            System.out.println("No handler for: " + qName);
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
               currentCheckPoint.setActual(content);
            }
         }

      }

      private class CheckGroupHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentCheckGroup = new TestPointToken();
            String mode = attributes.getValue("Mode");
            if (Strings.isValid(mode)) {
               currentCheckGroup.setGroupOperator(mode);
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            currentCheckGroup = null;
         }

      }

      private class CheckPointHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentCheckPoint = new TestPointToken(-1L, "");
            currentCheckPoint.setTestNumber(currentTestPoint.getTestNumber());
            currentCheckPoint.setInteractive(currentTestPoint.getInteractive());
            currentCheckPoint.setOverallResult(currentTestPoint.getResult());
            if (currentCheckGroup != null) {
               currentCheckPoint.setGroupName(currentCheckGroup.getGroupName());
               currentCheckPoint.setGroupOperator(currentCheckGroup.getGroupOperator());
            }
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            currentTestPoint.getSubTestPoints().add(currentCheckPoint);
            currentCheckPoint = null;
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
               currentCheckPoint.setElapsedTime(Integer.parseInt(content));
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
               currentCheckPoint.setExpected(content);
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
            currentCheckGroup.setGroupName(content);
         }

      }

      private class isInteractiveHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            // Do nothing
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            if (currentTestPoint != null) {
               currentTestPoint.setInteractive(Boolean.parseBoolean(content));
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
            if (currentCheckPoint != null) {
               currentCheckPoint.setName(content);
            }
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
            if (currentTestPoint != null) {
               currentTestPoint.setTestNumber(number);
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
               currentCheckPoint.setTransmissionCount(Integer.parseInt(content));
            }
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
            if (currentCheckPoint != null) {
               currentCheckPoint.setResult(content);
            } else if (currentTestPoint != null) {
               currentTestPoint.setResult(content);
            }
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

      private class TestPointHandler extends AbstractTmoHandler {

         @Override
         public void startElementFound(String uri, String localName, String qName, Attributes attributes) {
            currentTestPoint = new TestPointToken(-1L, "");
         }

         @Override
         public void endElementFound(String uri, String localName, String qName, String content) {
            currentScriptResult.getTestPoints().addAll(currentTestPoint.getSubTestPoints());
            currentTestPoint = null;
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

}
