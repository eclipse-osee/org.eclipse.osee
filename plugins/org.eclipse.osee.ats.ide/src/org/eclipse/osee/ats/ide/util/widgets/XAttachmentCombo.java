/*
 * Created on Sep 29, 2020
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;

public abstract class XAttachmentCombo extends XCombo implements IArtifactWidget {

   protected IAtsWorkItem workItem;

   public XAttachmentCombo(String displayLabel) {
      super(displayLabel);
   }

   protected abstract String getFileList();

   private void fillCombo() {

      String fileListStr = getFileList();

      // split by newline

      List<String> fileNames = new ArrayList<String>();
      // split by semicolon
      // add to fileNames

      setDataStrings(fileNames);
   }

   // method to load file

   // method store file in OSEE on Common branch

   // relate to workflow

   // button to open for read / edit

   @Override
   public Artifact getArtifact() {
      return null;
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public Result isDirty() {
      // do nothing
      return Result.FalseResult;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact instanceof IAtsWorkItem) {
         workItem = (IAtsWorkItem) artifact;
      }
   }

}
