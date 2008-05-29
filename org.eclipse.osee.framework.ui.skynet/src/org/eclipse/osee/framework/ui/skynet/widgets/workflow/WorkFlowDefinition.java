/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Donald G. Dunne
 */
public abstract class WorkFlowDefinition extends WorkItemDefinition {

   // fromPageId --- TransitionType ---> toPageIds
   private Map<String, Map<TransitionType, Set<String>>> pageIdToPageIdsViaTransitionType =
         new HashMap<String, Map<TransitionType, Set<String>>>();
   public static enum TransitionType {
      ToPage, ReturnPage, DefaultToPage, ToFromPage
   }
   private static List<WorkPageDefinition> EMPTY_PAGE_DEFS = new ArrayList<WorkPageDefinition>();
   private static Set<String> pageNames;

   public WorkFlowDefinition(String name, String id, String parentId) {
      super(name, id, parentId);
   }

   public Set<String> getPageNames() {
      if (pageNames == null) {
         pageNames = new HashSet<String>();
         for (String pageId : pageIdToPageIdsViaTransitionType.keySet()) {
            pageNames.add(WorkItemDefinitionFactory.getWorkItemDefinition(pageId).name);
            for (Map<TransitionType, Set<String>> transTypeToPageIds : pageIdToPageIdsViaTransitionType.values()) {
               for (TransitionType transType : transTypeToPageIds.keySet()) {
                  for (String pageId2 : transTypeToPageIds.get(transType)) {
                     pageNames.add(WorkItemDefinitionFactory.getWorkItemDefinition(pageId2).name);
                  }
               }
            }
         }
      }
      return pageNames;
   }

   public abstract WorkPageDefinition getStartWorkPage();

   public void addPageTransition(String fromPageId, TransitionType transitionType, String toPageId) {
      if (transitionType == TransitionType.ToFromPage) {
         addPageTransition(fromPageId, TransitionType.ToPage, toPageId);
         addPageTransition(toPageId, TransitionType.ToPage, fromPageId);
      }
      Map<TransitionType, Set<String>> transitionTypeToPageIds = pageIdToPageIdsViaTransitionType.get(fromPageId);
      if (transitionTypeToPageIds == null) {
         transitionTypeToPageIds = new HashMap<TransitionType, Set<String>>();
      }
      Set<String> toPageIds = transitionTypeToPageIds.get(transitionType);
      if (toPageIds == null) {
         toPageIds = new HashSet<String>();
      }
      if (transitionType == TransitionType.DefaultToPage && toPageIds.size() > 0) {
         throw new IllegalArgumentException("Only allowed ONE DefaultToPage");
      }
      toPageIds.add(toPageId);
      transitionTypeToPageIds.put(transitionType, toPageIds);
      pageIdToPageIdsViaTransitionType.put(fromPageId, transitionTypeToPageIds);

      // Add defaultToPage as a ToPage
      if (transitionType == TransitionType.DefaultToPage) {
         addPageTransition(fromPageId, TransitionType.ToPage, toPageId);
      }
   }

   public List<WorkPageDefinition> getPageDefinitions(String fromPageId, TransitionType transitionType) {
      Map<TransitionType, Set<String>> transitionTypeToPageIds = pageIdToPageIdsViaTransitionType.get(fromPageId);
      if (transitionTypeToPageIds == null) {
         return EMPTY_PAGE_DEFS;
      }
      Set<String> toPageIds = transitionTypeToPageIds.get(transitionType);
      if (toPageIds == null) {
         return EMPTY_PAGE_DEFS;
      }
      List<WorkPageDefinition> workPageDefs = new ArrayList<WorkPageDefinition>();
      for (WorkItemDefinition def : WorkItemDefinitionFactory.getWorkItemDefinitions(transitionTypeToPageIds.get(transitionType))) {
         workPageDefs.add((WorkPageDefinition) def);
      }
      return workPageDefs;
   }

   public String getPageId() {
      return id;
   }

}
