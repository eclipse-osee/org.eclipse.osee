/*
 * Created on Dec 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

public interface IAtsWorkDefinitionProvider {

   WorkDefinition loadWorkFlowDefinitionFromFile(WorkDefinitionSheet sheet) throws OseeCoreException;

   WorkDefinition getWorkFlowDefinition(String id) throws OseeCoreException;

   Artifact importWorkDefinitionSheetToDb(WorkDefinitionSheet sheet, SkynetTransaction transaction) throws OseeCoreException;

   void importAIsAndTeamsToDb(WorkDefinitionSheet sheet, SkynetTransaction transaction) throws OseeCoreException;

   void convertAndOpenAtsDsl(WorkDefinition workDef, XResultData resultData, String filename) throws OseeCoreException;

   void convertAndOpenAIandTeamAtsDsl(XResultData resultData) throws OseeCoreException;

   public WorkDefinition loadTeamWorkDefFromFileOldWay();

   public WorkDefinition loadTeamWorkDefFromFileNewWay();

}
