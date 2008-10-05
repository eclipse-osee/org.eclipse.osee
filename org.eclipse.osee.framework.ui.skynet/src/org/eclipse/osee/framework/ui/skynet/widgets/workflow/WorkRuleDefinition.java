/*
 * Created on Jun 1, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.Map;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class WorkRuleDefinition extends WorkItemDefinition {

   public static String ARTIFACT_NAME = "Work Rule Definition";

   /**
    * Instantiate rule with no value where name and id are same.
    * 
    * @param name
    * @param id
    */
   public WorkRuleDefinition(String id) {
      this(id, id, null, null);
   }

   /**
    * Instantiate rule with no value. This is for self describing rules such as atsAllowCommit.
    * 
    * @param name
    * @param id
    */
   public WorkRuleDefinition(String name, String id) {
      this(name, id, null, null);
   }

   public WorkRuleDefinition(String name, String id, Map<String, String> workDataKeyValueMap, String type) {
      super(name, id, null, type);
      if (workDataKeyValueMap != null) {
         setWorkDataKeyValueMap(workDataKeyValueMap);
      }
   }

   public WorkRuleDefinition(Artifact artifact) throws OseeCoreException {
      this(artifact.getDescriptiveName(), artifact.getSoleAttributeValue(
            WorkItemAttributes.WORK_ID.getAttributeTypeName(), ""), null, null);
      setDescription(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_DESCRIPTION.getAttributeTypeName(), ""));
      setType(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_TYPE.getAttributeTypeName(), (String) null));
      loadWorkDataKeyValueMap(artifact);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition#toArtifact()
    */
   @Override
   public Artifact toArtifact(WriteType writeType) throws OseeCoreException {
      Artifact ruleArt = super.toArtifact(writeType);
      return ruleArt;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition#getArtifactTypeName()
    */
   @Override
   public String getArtifactTypeName() {
      return ARTIFACT_NAME;
   }

}
