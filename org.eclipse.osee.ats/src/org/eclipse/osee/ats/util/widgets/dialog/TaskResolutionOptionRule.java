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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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

   private final List<TaskResOptionDefinition> options = new ArrayList<TaskResOptionDefinition>();
   public static String ATS_TASK_OPTIONS_TAG = "AtsTaskOptions";
   public static String WORK_TYPE = "AtsTaskResolutionOptions";
   public static List<TaskResOptionDefinition> EMPTY_TASK_RESOLUTION_OPTIONS = new ArrayList<TaskResOptionDefinition>();

   public TaskResolutionOptionRule(String name, String id, String value) {
      super(name, id, null, WORK_TYPE);
      addWorkDataKeyValue(ATS_TASK_OPTIONS_TAG, value);
   }

   /**
    * @param artifact
    * @throws Exception
    */
   public TaskResolutionOptionRule(Artifact artifact) throws OseeCoreException {
      super(artifact);
      fromXml(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName(), ""));
   }

   public static List<TaskResOptionDefinition> getTaskResolutionOptions(WorkPageDefinition workPageDefinition) throws OseeCoreException {
      TaskResolutionOptionRule taskResolutionOptionRule = getTaskResolutionOptionRule(workPageDefinition);
      if (taskResolutionOptionRule != null) return taskResolutionOptionRule.getOptions();
      return EMPTY_TASK_RESOLUTION_OPTIONS;
   }

   public static TaskResolutionOptionRule getTaskResolutionOptionRule(WorkPageDefinition workPageDefinition) throws OseeCoreException {
      List<WorkItemDefinition> wids =
            workPageDefinition.getWorkItemDefinitionsByType(TaskResolutionOptionRule.WORK_TYPE);
      if (wids.size() == 0) return null;
      WorkItemDefinition workItemDefinition = wids.iterator().next();
      if (workItemDefinition != null) {
         TaskResolutionOptionRule taskResolutionOptionRule =
               new TaskResolutionOptionRule(null, GUID.generateGuidStr(), null);
         taskResolutionOptionRule.fromXml(workItemDefinition.getWorkDataValue(ATS_TASK_OPTIONS_TAG));
         return taskResolutionOptionRule;
      }
      return null;
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

   public void fromXml(String xmlStr) throws OseeCoreException {
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

   /**
    * Return the order index number of the given option name. Used for comparisons of resolutions like < and > by
    * getting both indexes and doing a mathmatical comparison.
    * 
    * @param name
    * @return index number (starting at 1) or null if not found
    */
   public Integer getResolutionOptionOrderIndex(String name) {
      int x = 1;
      for (TaskResOptionDefinition option : options) {
         if (option.getName().equals(name)) return x;
         x++;
      }
      return null;
   }
}
