/*
 * Created on Jun 24, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Jeff C. Phillips
 */
public class FindRelationsWithDeletedArtifacts extends AbstractBlam {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Branch branch = variableMap.getBranch("Parent Branch");
      Set<RelationLink> links = new HashSet<RelationLink>();

      for (Artifact artifact : ArtifactQuery.getArtifactsFromBranch(branch, true)) {
         for (RelationLink relationLink : artifact.getRelationsAll()) {
            try {
               if (relationLink.getArtifactA().isDeleted() || relationLink.getArtifactB().isDeleted()) {
                  links.add(relationLink);
               }
            } catch (Exception ex) {
            }
         }
      }
      System.out.println("Number of links to be deleted: " + links.size());
   }

   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XText\" displayName=\"Branch List\" /><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Parent Branch\" /></xWidgets>";
   }
}
