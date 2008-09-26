/*
 * Created on Sep 23, 2006
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class SetRequirementCategory extends AbstractBlam {
   private HashMap<String, String> reqPriorities;
   private final HashMap<String, Artifact> reqs = new HashMap<String, Artifact>();

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Generating Reports", 100);

      Branch branch = variableMap.getBranch("Branch");
      String excelMlPath = variableMap.getString("ExcelML Priority File");
      boolean bulkLoad = variableMap.getValue(Boolean.class, "Bulk Load");

      ExtractReqPriority extractor = new ExtractReqPriority(excelMlPath);
      reqPriorities = extractor.getReqPriorities();

      if (bulkLoad) {
         for (Artifact req : ArtifactQuery.getArtifactsFromType(Requirements.SOFTWARE_REQUIREMENT, branch)) {
            reqs.put(req.getDescriptiveName().trim(), req);
         }
      }

      for (String requirementName : reqPriorities.keySet()) {
         updateCategory(bulkLoad, branch, requirementName.trim());
      }
   }

   private void updateCategory(boolean bulkLoad, Branch branch, String requirementName) throws SQLException, OseeCoreException {
      try {
         Artifact requirement;
         if (bulkLoad) {
            requirement = reqs.get(requirementName);
            if (requirement == null) {
               throw new ArtifactDoesNotExist("cant' find " + requirementName);
            }
         } else {
            requirement =
                  ArtifactQuery.getArtifactFromTypeAndName(Requirements.SOFTWARE_REQUIREMENT, requirementName, branch);
         }

         if (requirement.isOrphan()) {
            throw new MultipleArtifactsExist(requirement.getDescriptiveName());
         } else {
            requirement.setSoleAttributeValue("Category", reqPriorities.get(requirementName));
            requirement.persistAttributes();
         }
      } catch (MultipleArtifactsExist ex) {
         List<Artifact> artiafcts =
               ArtifactQuery.getArtifactsFromTypeAndName(Requirements.SOFTWARE_REQUIREMENT, requirementName, branch);
         for (Artifact requirement : artiafcts) {
            if (requirement.isOrphan()) {
               OseeLog.log(SkynetGuiPlugin.class, Level.INFO, requirement + " is an orphan");
            } else {
               requirement.setSoleAttributeValue("Category", reqPriorities.get(requirementName));
               requirement.persistAttributes();
            }
         }
      } catch (ArtifactDoesNotExist ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.INFO, ex.getLocalizedMessage());
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Bulk Load\" /><XWidget xwidgetType=\"XText\" displayName=\"ExcelML Priority File\" /><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /></xWidgets>";
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