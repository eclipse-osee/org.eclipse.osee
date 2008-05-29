/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class WorkItemDefinitionFactory {

   private static Map<String, WorkItemDefinition> itemIdToDefinition = new HashMap<String, WorkItemDefinition>();

   static {
      // Add all work item definitions provided through extension points
      for (IWorkDefinitionProvider provider : getWorkDefinitionProviders()) {
         for (WorkItemDefinition def : provider.getWorkItemDefinitions()) {
            addItemDefinition(def);
         }
      }
      // TODO Add all work item definitions provided through the database
   }

   public static void addItemDefinition(WorkItemDefinition workItemDefinition) {
      if (workItemDefinition.getId() == null) throw new IllegalArgumentException("Item Id can't be null");
      if (itemIdToDefinition.containsKey(workItemDefinition.getId())) throw new IllegalArgumentException(
            "Item Id must be unique.  Already page with id \"" + workItemDefinition.getId() + "\"");
      itemIdToDefinition.put(workItemDefinition.getId(), workItemDefinition);
   }

   public static WorkItemDefinition getWorkItemDefinition(String id) {
      return itemIdToDefinition.get(id);
   }

   public static List<WorkItemDefinition> getWorkItemDefinition(java.util.Collection<String> ids) {
      List<WorkItemDefinition> defs = new ArrayList<WorkItemDefinition>();
      for (String id : ids) {
         WorkItemDefinition def = getWorkItemDefinition(id);
         if (def == null) throw new IllegalArgumentException("Page Id \"" + id + "\" is not a defined work item");
         defs.add(def);
      }
      return defs;
   }

   public static List<WorkItemDefinition> getWorkItemDefinitions(java.util.Collection<String> pageids) {
      List<WorkItemDefinition> defs = new ArrayList<WorkItemDefinition>();
      for (String itemId : pageids) {
         WorkItemDefinition def = getWorkItemDefinition(itemId);
         if (def == null) throw new IllegalArgumentException("Item Id \"" + itemId + "\" is not a defined item");
         defs.add(def);
      }
      return defs;
   }

   public static WorkItemDefinition getWorkItemDefinitionFromXml(String xml) {
      WorkItemDefinition def = null;
      if (WorkItemBooleanDefinition.isWorkItemBoolean(xml))
         def = WorkItemBooleanDefinition.createFromXml(xml);
      else if (WorkItemStringDefinition.isWorkItemString(xml))
         def = WorkItemStringDefinition.createFromXml(xml);
      else
         throw new IllegalArgumentException("WorkItemDefinition doesn't exist for \"" + xml + "\"");
      return def;
   }

   private static List<IWorkDefinitionProvider> workDefinitionProviders;

   private static List<IWorkDefinitionProvider> getWorkDefinitionProviders() {
      workDefinitionProviders = new ArrayList<IWorkDefinitionProvider>();
      for (IConfigurationElement el : ExtensionPoints.getExtensionElements(
            "org.eclipse.osee.framework.ui.skynet.WorkDefinitionProvider", "WorkDefinitionProvider")) {
         String classname = null;
         String bundleName = null;
         if (el.getName().equals("WorkDefinitionProvider")) {
            classname = el.getAttribute("classname");
            bundleName = el.getContributor().getName();
            if (classname != null && bundleName != null) {
               Bundle bundle = Platform.getBundle(bundleName);
               try {
                  Class<?> taskClass = bundle.loadClass(classname);
                  Object obj = taskClass.newInstance();
                  workDefinitionProviders.add((IWorkDefinitionProvider) obj);
               } catch (Exception ex) {
                  OSEELog.logException(SkynetGuiPlugin.class, "Error loading WorkDefinitionProvider extension", ex,
                        true);
               }
            }

         }
      }
      return workDefinitionProviders;
   }

}
