/*
 * Created on Sep 23, 2006
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.HashMap;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class SetRequirementCategory extends AbstractBlam {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Generating Reports", 100);

      Branch branch = variableMap.getBranch("Branch");
      String excelMlPath = variableMap.getString("ExcelML Priority File");

      ExtractReqPriority extractor = new ExtractReqPriority(excelMlPath);
      HashMap<String, String> reqPriorities = extractor.getReqPriorities();
      for (String requirementName : reqPriorities.keySet()) {
         try {
            Artifact requirement =
                  ArtifactQuery.getArtifactFromTypeAndName(Requirements.SOFTWARE_REQUIREMENT, requirementName, branch);
            requirement.setSoleAttributeValue("Category", reqPriorities.get(requirementName));
            requirement.persistAttributes();
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XText\" displayName=\"ExcelML Priority File\" /><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /></xWidgets>";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getDescriptionUsage()
    */
   public String getDescriptionUsage() {
      return "Sets the Category attribute on software requirements.";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#wrapOperationForBranch(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap)
    */
   @Override
   public Branch wrapOperationForBranch(BlamVariableMap variableMap) {
      return variableMap.getBranch("Branch");
   }
}