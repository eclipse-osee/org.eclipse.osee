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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Theron Virgin
 */
public class VbaWordDiffGenerator implements IVbaDiffGenerator {

   private final static String header =
      "Option Explicit\n\nDim oWord\nDim baseDoc\nDim compareDoc\nDim authorName\nDim detectFormatChanges\nDim ver1\nDim ver2\nDim diffPath\nDim wdCompareTargetSelectedDiff\nDim wdCompareTargetSelectedMerge\nDim wdFormattingFromCurrent\nDim wdFormatXML\nDim wdDoNotSaveChanges\nDim mainDoc\n\nPublic Sub main()\n On error resume next\n    wdCompareTargetSelectedDiff = 0\n    wdCompareTargetSelectedMerge = 1\n    wdDoNotSaveChanges = 0\n    wdFormattingFromCurrent = 3\n    wdFormatXML = 11\n\n    authorName = \"OSEE Doc compare\"\n    set oWord = WScript.CreateObject(\"Word.Application\")\n    oWord.Visible = False\n    detectFormatChanges = ";

   private final static String comparisonCommand =
      "    baseDoc.Compare ver2, authorName, wdCompareTargetSelectedDiff, detectFormatChanges, False, False\n    set compareDoc = oWord.ActiveDocument\n\n";
   private final static String comparisonCommandFirst =
      "    set mainDoc = compareDoc\n    baseDoc.close\n    set baseDoc = Nothing\n";

   private final static String comparisonCommandOthers =
      "    mainDoc.Range(mainDoc.Range.End-1, mainDoc.Range.End-1).FormattedText =  compareDoc.Range.FormattedText\n\n    baseDoc.close wdDoNotSaveChanges\n    set baseDoc = Nothing\n\n    compareDoc.close wdDoNotSaveChanges\n    set compareDoc = Nothing\n\n";

   private final static String mergeCommand =
      "    baseDoc.Merge ver2, wdCompareTargetSelectedMerge, detectFormatChanges, wdFormattingFromCurrent, False\n    oWord.ActiveDocument.SaveAs diffPath, wdFormatXML, , , False\n\n";

   private boolean finalized;
   private final String resultPath;
   private boolean first;
   private final StringBuilder builder;
   private final boolean show;

   public VbaWordDiffGenerator(boolean show, boolean detectFormatChanges, String resultPath) {
      finalized = false;
      first = true;
      this.show = show;
      this.resultPath = resultPath;

      builder = new StringBuilder();
      builder.append(header);

      builder.append("");
      builder.append(Boolean.toString(detectFormatChanges));
      builder.append("\n\n");
   }

   @Override
   public void addComparison(IFile baseFile, IFile newerFile, String diffPath, boolean merge) throws OseeStateException {
      if (finalized) {
         throw new OseeStateException("Diff generation has already been finalized.");
      }
      builder.append("    ver1 = \"");
      builder.append(baseFile.getLocation().toOSString());

      builder.append("\"\n    ver2 = \"");
      builder.append(newerFile.getLocation().toOSString());

      builder.append("\"\n    diffPath = \"");
      builder.append(diffPath);

      builder.append("\"\n\n    set baseDoc = oWord.Documents.Open (ver1)\n");

      if (merge) {
         builder.append(mergeCommand);
      } else {
         builder.append(comparisonCommand);
         if (first) {
            builder.append(comparisonCommandFirst);
            first = false;
         } else {
            builder.append(comparisonCommandOthers);
         }
      }
   }

   @Override
   public void finish(String vbScriptPath) throws OseeCoreException {
      finalized = true;
      builder.append("    oWord.NormalTemplate.Saved = True\n");

      if (show) {
         builder.append("oWord.Visible = True\n");
      }

      builder.append("    mainDoc.SaveAs \"" + resultPath + "\", wdFormatXML, , , False\n\n");

      if (!show) {
         builder.append("        oWord.Quit()\n");
         builder.append("        set oWord = Nothing\n");
      }

      builder.append("End Sub\n\nmain");
      executeScript(getFile(vbScriptPath));
   }

   private File getFile(String path) throws OseeCoreException {
      File file = new File(path);
      try {
         Lib.writeStringToFile(builder.toString(), file);
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return file;
   }

   private void executeScript(File vbDiffScript) throws OseeCoreException {
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
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, message);
            }

         };
         Thread outputCatcher = new StreamLogger(process.getInputStream()) {

            @Override
            protected void log(String message) {
               OseeLog.log(SkynetGuiPlugin.class, Level.INFO, message);
            }
         };
         errorCatcher.start();
         outputCatcher.start();

         process.waitFor();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
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
