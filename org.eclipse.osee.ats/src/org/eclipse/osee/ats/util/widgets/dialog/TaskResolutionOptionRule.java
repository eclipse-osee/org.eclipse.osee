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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.AXml;
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

   public TaskResolutionOptionRule(String name, String id) {
      super(name, id, null);
   }

   public static TaskResolutionOptionRule getTaskResolutionOptions(WorkPageDefinition workPageDefinition) throws Exception {
      WorkItemDefinition workItemDefinition = workPageDefinition.getWorkItemDefinition(ATS_TASK_OPTIONS_TAG);
      if (workItemDefinition instanceof TaskResolutionOptionRule) {
         return (TaskResolutionOptionRule) workItemDefinition;
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

   public void fromXml(String xmlStr) {
      String optionsXml = AXml.getTagData(xmlStr, ATS_TASK_OPTIONS_TAG);
      Matcher m =
            Pattern.compile(
                  "<" + TaskResOptionDefinition.ATS_TASK_OPTION_TAG + ">.*?</" + TaskResOptionDefinition.ATS_TASK_OPTION_TAG + ">",
                  Pattern.DOTALL | Pattern.MULTILINE).matcher(optionsXml);
      while (m.find()) {
         TaskResOptionDefinition trd = new TaskResOptionDefinition();
         trd.setFromXml(m.group());
         options.add(trd);
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
