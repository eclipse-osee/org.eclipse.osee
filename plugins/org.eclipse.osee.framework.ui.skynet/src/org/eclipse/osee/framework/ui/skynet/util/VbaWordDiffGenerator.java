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
package org.eclipse.osee.framework.ui.skynet.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.osee.framework.core.exception.OperationTimedoutException;
import org.eclipse.osee.framework.core.model.change.CompareData;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.OseeInfo;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Theron Virgin
 */
public class VbaWordDiffGenerator implements IVbaDiffGenerator {

   private static final String OSEE_WORD_DIFF_SLEEP_MS = "osee.word.diff.sleep.ms";

   private final static String header_begin =
      "Option Explicit\n\nDim oWord\nDim baseDoc\nDim compareDoc\nDim authorName\nDim detectFormatChanges\nDim ver1\nDim ver2\ndim wdGranularityWordLevel\nDim wdCompareTargetSelectedDiff\nDim wdCompareTargetSelectedMerge\nDim wdFormattingFromCurrent\nDim wdFormatXML\nDim wdDoNotSaveChanges\nDim wdFieldCodeChanges\nDim mainDoc\ndim newDoc\n\nPublic Sub main()\n ";

   private final static String header_end =
      "wdCompareTargetSelectedDiff = 2\n    wdGranularityWordLevel = 1\n    wdDoNotSaveChanges = 0\n    wdFormattingFromCurrent = 3\n    wdFormatXML = 11\n\n    authorName = \"OSEE Doc compare\"\n    set oWord = WScript.CreateObject(\"Word.Application\")\n    oWord.Visible = False\n    detectFormatChanges = ";

   private final static String diff_field_codes = "    wdFieldCodeChanges = ";

   private static final String Skip_Errors = "On error resume next\n ";

   private final static String comparisonCommand =
      // The true/false flags define what to compare: Formats, Case, Whitespace, Tables, Headers, Footers, TextBox, Field values (wdFieldCodeChanges), Comments
      "    set newDoc = oWord.CompareDocuments (baseDoc, compareDoc, wdCompareTargetSelectedDiff, wdGranularityWordLevel, true, true, true, true, true, true, true, wdFieldCodeChanges, true, true, authorName) \n    compareDoc.close \n    newDoc.Activate\n    set compareDoc = oWord.ActiveDocument\n\n";
   private final static String comparisonCommandFirst =
      "    set mainDoc = compareDoc\n    baseDoc.close\n    set baseDoc = Nothing\n";

   private final static String comparisonCommandOthers =
      "    mainDoc.Range(mainDoc.Range.End-1, mainDoc.Range.End-1).FormattedText =  compareDoc.Range.FormattedText\n\n    baseDoc.close wdDoNotSaveChanges\n    set baseDoc = Nothing\n\n    compareDoc.close wdDoNotSaveChanges\n    set compareDoc = Nothing\n\n";

   private final static String altComparisonCommandOthers =
      "    mainDoc.Range(mainDoc.Range.End-1, mainDoc.Range.End-1).FormattedText =  compareDoc.Range.FormattedText\n\n    baseDoc.close wdDoNotSaveChanges\n    set baseDoc = Nothing\n\n    set compareDoc = Nothing\n\n";

   private final static String mergeCommand =
      "    compareDoc.close \n    baseDoc.Merge ver2, wdCompareTargetSelectedMerge, detectFormatChanges, wdFormattingFromCurrent, False\n    oWord.ActiveDocument.SaveAs %s, wdFormatXML, , , False\n\n";

   private final static String altMergeCommand =
      "    compareDoc.close \n    baseDoc.Merge ver2, wdCompareTargetSelectedMerge, detectFormatChanges, wdFormattingFromCurrent, False\n    set compareDoc = oWord.ActiveDocument\n\n";

   private final boolean merge;
   private final boolean show;
   private final boolean diffFieldCode;
   private final boolean detectFormatChanges;
   private final boolean executeVbScript;
   private final boolean skipErrors;
   private Long timeoutMs;

   public VbaWordDiffGenerator(boolean merge, boolean show, boolean detectFormatChanges, boolean executeVbScript, boolean skipErrors, boolean diffFieldCode) {
      this.merge = merge;
      this.show = show;
      this.detectFormatChanges = detectFormatChanges;
      this.executeVbScript = executeVbScript;
      this.skipErrors = skipErrors;
      this.diffFieldCode = diffFieldCode;

      timeoutMs = Long.MAX_VALUE;
      String timeout = null;
      try {
         timeout = OseeInfo.getValue("osee.vba.word.diff.timeout");
         if (Strings.isValid(timeout)) {
            timeoutMs = Long.parseLong(timeout);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.INFO,
            String.format("Timeout lookup failed in %s, set to default %s", this.getClass().getSimpleName(), timeout));
      }
   }

   @Override
   public void generate(IProgressMonitor monitor, CompareData compareData) {
      Writer writer = null;
      try {
         writer = new BufferedWriter(new FileWriter(compareData.getGeneratorScriptPath()));

         writer.append(header_begin);

         if (skipErrors) {
            writer.append(Skip_Errors);
         }
         writer.append(header_end);

         writer.append("");
         writer.append(Boolean.toString(detectFormatChanges));
         writer.append("\n");
         writer.append(diff_field_codes + String.valueOf(diffFieldCode));
         writer.append("\n\n");

         addComparison(monitor, writer, compareData, merge);
         writer.append("    oWord.NormalTemplate.Saved = True\n");

         if (show) {
            writer.append("oWord.Visible = True\n");
         }

         writer.append("    mainDoc.SaveAs \"" + compareData.getOutputPath() + "\", wdFormatXML, , , False\n\n");

         if (!show) {
            writer.append("        oWord.Quit()\n");
            writer.append("        set oWord = Nothing\n");
         }

         writer.append("End Sub\n\nmain");

      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         Lib.close(writer);
      }

      if (executeVbScript) {
         monitor.setTaskName("Executing Diff");
         executeScript(new File(compareData.getGeneratorScriptPath()));
      } else {
         OseeLog.logf(Activator.class, Level.INFO, "Test - Skip launch of [%s]", compareData.getGeneratorScriptPath());
      }
   }

   private void addComparison(IProgressMonitor monitor, Appendable appendable, CompareData compareData, boolean merge) throws IOException {
      boolean first = true;
      double workAmount = 0.20 / compareData.entrySet().size();
      monitor.setTaskName("Creating Diff Script");
      for (Entry<String, String> entry : compareData.entrySet()) {

         if (monitor.isCanceled()) {
            throw new OperationCanceledException();
         }

         //Unfortunately Word seems to need a little extra time to close, otherwise Word 2007 will crash periodically if too many files are being compared.
         String propertyWordDiffSleepMs = "250"; // Quarter second is the default sleep value
         try {
            propertyWordDiffSleepMs = OseeInfo.getValue(OSEE_WORD_DIFF_SLEEP_MS, "250");
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.INFO,
               String.format("Word Diff Sleep lookup failed in %s, set to default %s", this.getClass().getSimpleName(),
                  propertyWordDiffSleepMs));
         }

         if (!propertyWordDiffSleepMs.equals("0")) {
            appendable.append("WScript.sleep(" + propertyWordDiffSleepMs + ")\n");
         }

         appendable.append("    ver1 = \"");
         appendable.append(entry.getKey());

         appendable.append("\"\n    ver2 = \"");
         appendable.append(entry.getValue());

         appendable.append("\"\n\n    set baseDoc = oWord.Documents.Open (ver1)\n");

         appendable.append("    baseDoc.TrackRevisions = false\n");
         appendable.append("    baseDoc.AcceptAllRevisions\n");

         appendable.append("\n\n    set compareDoc = oWord.Documents.Open (ver2)\n");
         appendable.append("    compareDoc.AcceptAllRevisions\n");
         appendable.append("    compareDoc.TrackRevisions = false\n");
         appendable.append("    compareDoc.Save\n");

         boolean mergeFromCompare = compareData.isMerge(entry.getKey());
         if (merge || mergeFromCompare) {
            if (mergeFromCompare) {
               if (first) {
                  appendable.append(comparisonCommand);
               } else {
                  appendable.append(altMergeCommand);
               }
            } else {
               appendable.append(String.format(mergeCommand, "\"" + compareData.getOutputPath() + "\""));
            }
         } else {
            appendable.append(comparisonCommand);
         }
         if (first) {
            appendable.append(comparisonCommandFirst);
            first = false;
         } else {
            if (mergeFromCompare) {
               appendable.append(altComparisonCommandOthers);
            } else {
               appendable.append(comparisonCommandOthers);
            }
         }

         monitor.worked(Operations.calculateWork(Operations.TASK_WORK_RESOLUTION, workAmount));
      }
   }

   private void executeScript(File vbDiffScript) {
      Process process = null;
      try {
         String target = vbDiffScript.getName();
         String cmd[] = {"cmd", "/s", "/c", "\"" + target + "\""};
         ProcessBuilder builder = new ProcessBuilder(cmd);

         File parentDir = vbDiffScript.getParentFile();

         if (parentDir != null) {
            builder.directory(parentDir);
         }
         process = builder.start();

         Thread errorCatcher = new StreamLogger(process.getErrorStream()) {

            @Override
            protected void log(String message) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, message);
            }

         };
         Thread outputCatcher = new StreamLogger(process.getInputStream()) {

            @Override
            protected void log(String message) {
               OseeLog.log(Activator.class, Level.INFO, message);
            }
         };
         errorCatcher.start();
         outputCatcher.start();

         if (!process.waitFor(timeoutMs, TimeUnit.MILLISECONDS)) {
            throw new OperationTimedoutException("The View Word Change Report Timed-out");
         }

      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      } finally {
         if (process != null) {
            process.destroy();
         }
      }
   }

   private static abstract class StreamLogger extends Thread {

      private final InputStream inputStream;

      protected StreamLogger(InputStream inputStream) {
         this.inputStream = inputStream;
      }

      @Override
      public void run() {
         InputStreamReader isr = new InputStreamReader(inputStream);
         BufferedReader br = new BufferedReader(isr);
         try {
            StringBuilder message = new StringBuilder();

            String line = null;
            while ((line = br.readLine()) != null) {
               message.append(line);
               message.append("\n");
            }
            if (message.length() > 0) {
               log(message.toString());
            }
         } catch (IOException ioe) {
            ioe.printStackTrace();
         }
      }

      protected abstract void log(String message);
   }

}
