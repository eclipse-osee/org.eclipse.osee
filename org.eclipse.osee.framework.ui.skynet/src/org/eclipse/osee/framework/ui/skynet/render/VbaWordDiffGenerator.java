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
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.io.streams.StreamCatcher;

/**
 * @author Theron Virgin
 */
public class VbaWordDiffGenerator implements IVbaDiffGenerator {
   private final static String header =
         "Option Explicit\n\nDim oWord\nDim baseDoc\nDim compareDoc\nDim authorName\nDim detectFormatChanges\nDim ver1\nDim ver2\nDim diffPath\nDim wdCompareTargetSelectedDiff\nDim wdCompareTargetSelectedMerge\nDim wdFormattingFromCurrent\nDim wdFormatXML\nDim wdDoNotSaveChanges\nDim visible\nDim mainDoc\n\nPublic Sub main()\n    wdCompareTargetSelectedDiff = 0\n    wdCompareTargetSelectedMerge = 1\n    wdDoNotSaveChanges = 0\n    wdFormattingFromCurrent = 3\n    wdFormatXML = 11\n\n    authorName = \"OSEE Doc compare\"\n\n    detectFormatChanges = True\n\n    set oWord = WScript.CreateObject(\"Word.Application\")\n    oWord.Visible = False\n\n";

   private final static String comparisonCommand =
         "    baseDoc.Compare ver2, authorName, wdCompareTargetSelectedDiff, detectFormatChanges, False, False\n    set compareDoc = oWord.ActiveDocument\n\n";
   private final static String comparisonCommandFirst =
         "    set mainDoc = compareDoc\n    baseDoc.close\n    set baseDoc = Nothing\n";

   private final static String comparisonCommandOthers =
         "    mainDoc.Range(mainDoc.Range.End-1, mainDoc.Range.End-1).FormattedText =  compareDoc.Range.FormattedText\n\n    baseDoc.close wdDoNotSaveChanges\n    set baseDoc = Nothing\n\n    compareDoc.close wdDoNotSaveChanges\n    set compareDoc = Nothing\n\n";

   private final static String mergeCommand =
         "    baseDoc.Merge ver2, wdCompareTargetSelectedMerge, detectFormatChanges, wdFormattingFromCurrent, False\n    oWord.ActiveDocument.SaveAs diffPath, wdFormatXML, , , False\n\n";

   private final static String tail = "    If visible Then\n        oWord.Visible = True\n     Else\n";
   private final static String tail2 =
         "         oWord.Quit()\n        set oWord = Nothing\n    End If\n\nEnd Sub\n\nmain";

   private StringBuilder builder;
   private boolean finalized;
   private boolean initialized;
   private boolean first;
   private String diffPath = null;
   private boolean merge = false;

   public VbaWordDiffGenerator() {
      initialized = false;
      finalized = false;
      first = true;
   }

   public boolean initialize(boolean visible, boolean detectFormatChanges) {
      if (initialized) {
         return false;
      }
      initialized = true;
      builder = new StringBuilder();
      builder.append(header);
      if (visible) {
         builder.append("    visible = True\n\n");
      } else {
         builder.append("    visible = False\n\n");
      }
      if (detectFormatChanges) {
         builder.append("    detectFormatChanges = True\n\n");
      } else {
         builder.append("    detectFormatChanges = False\n\n");
      }
      return true;
   }

   public boolean addComparison(IFile baseFile, IFile newerFile, String diffPath, boolean merge) {
      if (finalized) {
         return false;
      }
      this.merge = merge;
      builder.append("   oWord.Visible = False\n");
      builder.append("    ver1 = \"");
      builder.append(baseFile.getLocation().toOSString());
      builder.append("\"\n");

      builder.append("    ver2 = \"");
      builder.append(newerFile.getLocation().toOSString());
      builder.append("\"\n");

      builder.append("    diffPath = \"");
      builder.append(diffPath);
      builder.append("\"\n\n");

      builder.append("    set baseDoc = oWord.Documents.Open (ver1)\n");

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
      return true;
   }

   @Override
   public void finish(String vbScriptPath, boolean show) throws OseeWrappedException {
      finalized = true;
      if (show) {
         builder.append("    visible = True\n");
         builder.append("         mainDoc.SaveAs diffPath, wdFormatXML, , , False\n\n");
      }
      builder.append(tail);
      if (!show && !merge) {
         builder.append("         mainDoc.SaveAs diffPath, wdFormatXML, , , False\n\n");
      }
      builder.append(tail2);
      compare(getFile(vbScriptPath));
   }

   @Override
   public File getFile(String path) throws OseeWrappedException {
      if (!finalized) {
         return null;
      }
      try {
         FileOutputStream out = new FileOutputStream(path != null ? path : "c:\\UserData\\compareDocs.vbs");
         out.write(builder.toString().getBytes(), 0, builder.toString().getBytes().length);
         out.close();
         return new File(path != null ? path : "c:\\UserData\\compareDocs.vbs");
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private void compare(File vbDiffScript) throws OseeWrappedException {
      try {
         String cmd[] = {"cmd", "/s /c", "\"" + vbDiffScript.getPath() + "\""};

         Process proc = Runtime.getRuntime().exec(cmd);

         StreamCatcher errorCatcher = new StreamCatcher(proc.getErrorStream(), "ERROR");
         StreamCatcher outputCatcher = new StreamCatcher(proc.getInputStream(), "OUTPUT");

         errorCatcher.start();
         outputCatcher.start();
         proc.waitFor();
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      } catch (InterruptedException ex) {
         throw new OseeWrappedException(ex);
      }
   }
}
