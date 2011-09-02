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
package org.eclipse.osee.ats.core.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workdef.RuleDefinition;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Donald G. Dunne
 */
public class TaskResolutionOptionRule {

   private final List<TaskResOptionDefinition> options = new ArrayList<TaskResOptionDefinition>();
   public final static String ATS_TASK_OPTIONS_TAG = "AtsTaskOptions";
   public final static String WORK_TYPE = "AtsTaskResolutionOptions";
   public final static List<TaskResOptionDefinition> EMPTY_TASK_RESOLUTION_OPTIONS =
      new ArrayList<TaskResOptionDefinition>();
   public final static Map<StateDefinition, List<TaskResOptionDefinition>> stateDefToTaskResOptDefCache =
      new HashMap<StateDefinition, List<TaskResOptionDefinition>>();

   public static List<TaskResOptionDefinition> getTaskResolutionOptions(StateDefinition stateDefinition) throws OseeCoreException {
      if (!stateDefToTaskResOptDefCache.containsKey(stateDefinition)) {
         TaskResolutionOptionRule taskResolutionOptionRule = getTaskResolutionOptionRule(stateDefinition);
         if (taskResolutionOptionRule != null) {
            stateDefToTaskResOptDefCache.put(stateDefinition, taskResolutionOptionRule.getOptions());
         } else {
            stateDefToTaskResOptDefCache.put(stateDefinition, EMPTY_TASK_RESOLUTION_OPTIONS);
         }
      }
      return stateDefToTaskResOptDefCache.get(stateDefinition);
   }

   public static TaskResolutionOptionRule getTaskResolutionOptionRule(StateDefinition stateDefinition) throws OseeCoreException {
      List<RuleDefinition> wids = new ArrayList<RuleDefinition>();
      for (RuleDefinition ruleDef : stateDefinition.getRules()) {
         if (ruleDef.getName().contains("taskResolutionOptions")) {
            wids.add(ruleDef);
            break;
         }
      }
      if (wids.isEmpty()) {
         return null;
      }
      RuleDefinition ruleDefinition = wids.iterator().next();
      if (ruleDefinition != null) {
         TaskResolutionOptionRule taskResolutionOptionRule = new TaskResolutionOptionRule();
         taskResolutionOptionRule.fromXml(getTaskResolutionRuleXml(ruleDefinition));
         return taskResolutionOptionRule;
      }
      return null;
   }

   private static String getTaskResolutionRuleXml(RuleDefinition ruleDefinition) throws OseeCoreException {
      // If this rule was converted from WorkRuleDefinition, it will have task options
      String xml = ruleDefinition.getWorkDataValue(ATS_TASK_OPTIONS_TAG);
      if (Strings.isValid(xml)) {
         return xml;
      }
      // Else, look for a GeneralData artifact of same name as rule to retrieve options xml string
      Artifact artifact = null;
      try {
         artifact =
            ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.GeneralData, ruleDefinition.getName(),
               AtsUtilCore.getAtsBranch());
         return artifact.getSoleAttributeValue(CoreAttributeTypes.GeneralStringData, "");
      } catch (ArtifactDoesNotExist ex) {
         OseeLog.logf(Activator.class, Level.SEVERE, ex, "GeneralData artifact named [%s] does not exist",
            ruleDefinition.getName());
      }
      return "";
   }

   private void setFromDoc(Document doc) throws OseeCoreException {
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

   private void fromXml(String xmlStr) throws OseeCoreException {
      try {
         setFromDoc(Jaxp.readXmlDocument(xmlStr));
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   private String toXml() throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      sb.append("<" + TaskResOptionDefinition.ATS_TASK_OPTION_TAG + ">\n");
      for (TaskResOptionDefinition def : options) {
         sb.append(def.toXml());
         sb.append("\n");
      }
      sb.append("</");
      sb.append(TaskResOptionDefinition.ATS_TASK_OPTION_TAG);
      sb.append(">\n");
      return sb.toString();
   }

   public List<TaskResOptionDefinition> getOptions() {
      return options;
   }

   /**
    * Return the order index number of the given option name. Used for comparisons of resolutions like < and > by
    * getting both indexes and doing a mathmatical comparison.
    * 
    * @return index number (starting at 1) or null if not found
    */
   public Integer getResolutionOptionOrderIndex(String name) {
      int x = 1;
      for (TaskResOptionDefinition option : options) {
         if (option.getName().equals(name)) {
            return x;
         }
         x++;
      }
      return null;
   }

   public static void clearCaches() {
      if (stateDefToTaskResOptDefCache != null) {
         stateDefToTaskResOptDefCache.clear();
      }
   }
}
