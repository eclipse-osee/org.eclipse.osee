/*
 * Created on Aug 29, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
         "Option Explicit\n\nDim oWord\nDim baseDoc\nDim authorName\nDim detectFormatChanges\nDim ver1\nDim ver2\nDim diffPath\nDim wdCompareTargetSelectedDiff\nDim wdCompareTargetSelectedMerge\nDim wdFormattingFromCurrent\nDim wdFormatXML\nDim visible\n\nPublic Sub main()\n    wdCompareTargetSelectedDiff = 0\n    wdCompareTargetSelectedMerge = 1\n    wdFormattingFromCurrent = 3\n    wdFormatXML = 11\n\n    authorName = \"OSEE Doc compare\"\n\n    detectFormatChanges = True\n\n    set oWord = WScript.CreateObject(\"Word.Application\")\n    oWord.Visible = False\n\n";

   private final static String comparisonCommand =
         "    oWord.ActiveDocument.Compare ver2, authorName, wdCompareTargetSelectedDiff, detectFormatChanges, False, False\n    oWord.ActiveDocument.SaveAs diffPath, wdFormatXML, , , False\n    baseDoc.close()\n    If visible Then\n     Else\n         oWord.ActiveDocument.close()\n    End If\n\n";

   private final static String mergeCommand =
         "    baseDoc.Merge ver2, wdCompareTargetSelectedMerge, detectFormatChanges, wdFormattingFromCurrent, False\n    oWord.ActiveDocument.SaveAs diffPath, wdFormatXML, , , False\n\n";

   private final static String tail =
         "    If visible Then\n        oWord.Visible = True\n     Else\n         oWord.Quit()\n        set oWord = Nothing\n    End If\n\nEnd Sub\n\nmain";

   private StringBuilder builder;
   private boolean finalized;
   private boolean initialized;

   public VbaWordDiffGenerator() {
      initialized = false;
      finalized = false;
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
      }
      return true;
   }

   @Override
   public void finish(String path) throws OseeWrappedException {
      finalized = true;
      builder.append(tail);
      compare(getFile(path));
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
