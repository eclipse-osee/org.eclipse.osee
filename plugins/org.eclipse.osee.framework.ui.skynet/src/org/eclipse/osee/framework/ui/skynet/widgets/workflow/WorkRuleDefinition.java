/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class WorkRuleDefinition extends WorkItemDefinition {

   public static String ARTIFACT_NAME = "Work Rule Definition";

   /**
    * Instantiate rule with no value where name and id are same.
    * 
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
      this(artifact.getName(), artifact.getSoleAttributeValue(
            WorkItemAttributes.WORK_ID.getAttributeTypeName(), ""), null, null);
      setDescription(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_DESCRIPTION.getAttributeTypeName(), ""));
      setType(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_TYPE.getAttributeTypeName(), (String) null));
      loadWorkDataKeyValueMap(artifact);
   }

   @Override
   public Artifact toArtifact(WriteType writeType) throws OseeCoreException {
      Artifact ruleArt = super.toArtifact(writeType);
      return ruleArt;
   }

   @Override
   public String getArtifactTypeName() {
      return ARTIFACT_NAME;
   }

}
