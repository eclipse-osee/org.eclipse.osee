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
package org.eclipse.osee.ats.util.widgets.dialog;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemAttributes;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Donald G. Dunne
 */
public class TaskResolutionOptionRule extends WorkRuleDefinition {

   private List<TaskResOptionDefinition> options = new ArrayList<TaskResOptionDefinition>();
   public static String ATS_TASK_OPTIONS_TAG = "AtsTaskOptions";
   public static String WORK_TYPE = "AtsTaskResolutionOptions";

   public TaskResolutionOptionRule(String name, String id, String value) {
      super(name, id, null, WORK_TYPE);
      if (value != null && !value.equals("")) setData(value);
   }

   /**
    * @param artifact
    * @throws Exception
    */
   public TaskResolutionOptionRule(Artifact artifact) throws OseeCoreException, SQLException {
      super(artifact);
      fromXml(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName(), ""));
   }

   public static List<TaskResOptionDefinition> getTaskResolutionOptions(WorkPageDefinition workPageDefinition) throws SQLException, OseeCoreException {
      List<WorkItemDefinition> wids =
            workPageDefinition.getWorkItemDefinitionsByType(TaskResolutionOptionRule.WORK_TYPE);
      if (wids.size() == 0) return Collections.emptyList();
      if (wids.size() > 1) throw new IllegalArgumentException(
            "Expected on 1 " + TaskResolutionOptionRule.WORK_TYPE + ", found " + wids.size());
      WorkItemDefinition workItemDefinition = wids.iterator().next();
      if (workItemDefinition != null) {
         TaskResolutionOptionRule taskResolutionOptionRule =
               new TaskResolutionOptionRule(null, GUID.generateGuidStr(), null);
         taskResolutionOptionRule.fromXml((String) workItemDefinition.getData());
         return taskResolutionOptionRule.getOptions();
      }
      return new ArrayList<TaskResOptionDefinition>();
   }

   public void setFromDoc(Document doc) {
      NodeList nodes = doc.getElementsByTagName(TaskResOptionDefinition.ATS_TASK_OPTION_TAG);
      if (nodes.getLength() > 0) {
         for (int x = 0; x < nodes.getLength(); x++) {
            Element element = (Element) nodes.item(x);
            TaskResOptionDefinition trd = new TaskResOptionDefinition();
            trd.setFromElement(element);
            options.add(trd);
         }
      }
   }

   public void fromXml(String xmlStr) throws OseeCoreException, SQLException {
      try {
         setFromDoc(Jaxp.readXmlDocument(xmlStr));
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   public String toXml() {
      StringBuffer sb = new StringBuffer();
      sb.append("<" + TaskResOptionDefinition.ATS_TASK_OPTION_TAG + ">\n");
      for (TaskResOptionDefinition def : options) {
         sb.append(def.toXml() + "\n");
      }
      sb.append("</" + TaskResOptionDefinition.ATS_TASK_OPTION_TAG + ">\n");
      return sb.toString();
   }

   public List<TaskResOptionDefinition> getOptions() {
      return options;
   }

}
