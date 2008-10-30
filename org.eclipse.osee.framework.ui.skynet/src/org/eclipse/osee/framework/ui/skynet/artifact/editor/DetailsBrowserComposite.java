/*
 * Created on Oct 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;

/**
 * @author Donald G. Dunne
 */
public class DetailsBrowserComposite extends BrowserComposite {

   /**
    * @param parent
    * @param style
    */
   public DetailsBrowserComposite(Artifact artifact, Composite parent, int style, ToolBar toolBar) {
      super(parent, style, toolBar);
      StringBuffer sb =
            new StringBuffer(AHTML.getLabelValueStr("Name", artifact.getDescriptiveName()) + AHTML.newline());
      try {
         sb.append(AHTML.getLabelValueStr("GUID", artifact.getGuid()) + AHTML.newline());
         sb.append(AHTML.getLabelValueStr("Branch", artifact.getBranch().toString()) + AHTML.newline());
         sb.append(AHTML.getLabelValueStr("Branch Id", String.valueOf(artifact.getBranch().getBranchId())) + AHTML.newline());
         sb.append(AHTML.getLabelValueStr("Artifact Id", String.valueOf(artifact.getArtId())) + AHTML.newline());
         sb.append(AHTML.getLabelValueStr("Artifact Type Name", artifact.getArtifactTypeName()) + AHTML.newline());
         sb.append(AHTML.getLabelValueStr("Artifact Type Id", String.valueOf(artifact.getArtTypeId())) + AHTML.newline());
         sb.append(AHTML.getLabelValueStr("Gamma Id", String.valueOf(artifact.getGammaId())) + AHTML.newline());
         sb.append(AHTML.getLabelValueStr("Historical", String.valueOf(artifact.isHistorical())) + AHTML.newline());
         sb.append(AHTML.getLabelValueStr("Revision", String.valueOf(artifact.getTransactionNumber())) + AHTML.newline());
         sb.append(AHTML.getLabelValueStr("Last Modified", String.valueOf(artifact.getLastModified())) + AHTML.newline());
         sb.append(AHTML.getLabelValueStr("Last Modified By", String.valueOf(artifact.getLastModifiedBy())) + AHTML.newline());
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         sb.append(AHTML.getLabelStr("Exception in rendering details: ", ex.getLocalizedMessage()));
      }
      setHtml(AHTML.simplePage(sb.toString()));
   }

}
