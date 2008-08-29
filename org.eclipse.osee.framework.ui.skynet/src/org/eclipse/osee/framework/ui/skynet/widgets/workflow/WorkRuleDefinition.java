/*
 * Created on Jun 1, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

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
      this(name, id, value, null);
   }

   public WorkRuleDefinition(String name, String id, String value, String type) {
      super(name, id, null, type);
      if (value != null && value.equals("")) throw new IllegalArgumentException(
            "value must be either null or length>0.  value can not be \"\".  Invalid for WorkRuleDefinition " + id);
      if (value != null) setData(value);
   }

   public WorkRuleDefinition(Artifact artifact) throws OseeCoreException, SQLException {
      this(artifact.getDescriptiveName(), artifact.getSoleAttributeValue(
            WorkItemAttributes.WORK_ID.getAttributeTypeName(), ""), null);
      setDescription(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_DESCRIPTION.getAttributeTypeName(), ""));
      setType(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_TYPE.getAttributeTypeName(), (String) null));

      try {
         setData(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_DATA.getAttributeTypeName()));
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
   public Artifact toArtifact(WriteType writeType) throws OseeCoreException, SQLException {
      Artifact ruleArt = super.toArtifact(writeType);
      if (get() != null) ruleArt.setSoleAttributeValue(WorkItemAttributes.WORK_DATA.getAttributeTypeName(), get());
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
