/*
 * Created on Jun 1, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.util.AttributeDoesNotExist;

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
      this(id, id, null);
   }

   /**
    * Instantiate rule with no value. This is for self describing rules such as atsAllowCommit.
    * 
    * @param name
    * @param id
    */
   public WorkRuleDefinition(String name, String id) {
      this(name, id, null);
   }

   /**
    * @param name
    * @param id
    * @param parentId
    */
   public WorkRuleDefinition(String name, String id, String value) {
      super(name, id, null);
      if (value != null && value.equals("")) throw new IllegalArgumentException(
            "value must be either null or length>0.  value can not be \"\".  Invalid for WorkRuleDefinition " + id);
      if (value != null) setData(value);
   }

   public WorkRuleDefinition(Artifact artifact) throws Exception {
      this(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_NAME.getAttributeTypeName(), ""),
            artifact.getDescriptiveName(), null);
      try {
         setData(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_NAME.getAttributeTypeName()));
      } catch (AttributeDoesNotExist ex) {
         // do nothing
      }
   }

   public String get() {
      return (String) getData();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition#toArtifact()
    */
   @Override
   public Artifact toArtifact(WriteType writeType) throws Exception {
      Artifact ruleArt = super.toArtifact(writeType);
      if (get() != null) ruleArt.setSoleAttributeValue(WorkItemAttributes.WORK_NAME.getAttributeTypeName(), get());
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
