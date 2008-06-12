/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IWorkDefinitionProvider {

   /**
    * Return WorkItemDefinitions to be contributed programatically to the WorkDefinitionFactory. This includes Page and
    * Workflow Definitions. This should only be used for development as all WorkItemDefinitions should be imported into
    * the DB using ImportWorkItemDefinitions.
    * 
    * @return
    * @throws Exception TODO
    */
   public Collection<WorkItemDefinition> getProgramaticWorkItemDefinitions() throws OseeCoreException, SQLException;

   /**
    * Return WorkFlowDefinition to use for the given state machine artifact.
    * 
    * @param artifact
    * @return
    * @throws Exception TODO
    */
   public WorkFlowDefinition getWorkFlowDefinition(Artifact artifact) throws OseeCoreException, SQLException;

   /**
    * Dynamic Work Item Definitions will be collected only when widgets are being drawn for the given workflow and
    * workpage. This allows for dynamic widgets to be added conditionally and/or configured programatically.
    * 
    * @param workFlowDefinition current workflow
    * @param workPageDefinition current workpage
    * @param data information provided to the extending plugins to determine if work item definitions should be added
    * @return
    * @throws Exception TODO
    */
   public Collection<WorkItemDefinition> getDynamicWorkItemDefinitionsForPage(WorkFlowDefinition workFlowDefinition, WorkPageDefinition workPageDefinition, Object data) throws OseeCoreException, SQLException;

}
