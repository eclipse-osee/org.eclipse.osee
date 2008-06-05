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
import java.util.Map.Entry;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class WorkFlowDefinition extends WorkItemDefinition {

   public static String ARTIFACT_NAME = "Work Flow Definition";

   // fromPageId --- TransitionType ---> toPageIds
   private Map<String, Map<TransitionType, Set<String>>> pageIdToPageIdsViaTransitionType =
         new HashMap<String, Map<TransitionType, Set<String>>>();
   public static enum TransitionType {
      // Normal transition; will be provided as option in transition pulldown
      ToPage,
      // Allows the page to "return" to an earlier state; allows transition w/o page completion;
      // will be provided as an option in transition pulldown (don't need to register as ToPage)
      ToPageAsReturn,
      // ToPage that will also be registered as the default selected transition state
      ToPageAsDefault,
   }
   private static List<WorkPageDefinition> EMPTY_PAGE_DEFS = new ArrayList<WorkPageDefinition>();
   private Map<String, WorkPageDefinition> pageNamesToPageDef;
   private Map<String, WorkPageDefinition> pageIdsToPageDef;
   private WorkPageDefinition startPage;
   protected String startPageId;

   public WorkFlowDefinition(String name, String id, String parentId) {
      super(name, id, parentId);
   }

   public WorkFlowDefinition(Artifact artifact) throws Exception {
      this(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_NAME.getAttributeTypeName(), ""),
            artifact.getDescriptiveName(), artifact.getSoleAttributeValue(
                  WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName(), (String) null));
      loadFromArtifact(artifact);
   }

   public void loadFromArtifact(Artifact artifact) throws Exception {
      for (String transition : artifact.getAttributesToStringList(WorkItemAttributes.TRANSITION.getAttributeTypeName())) {
         String[] strs = transition.split(";");
         if (strs.length != 3) {
            OSEELog.logException(
                  SkynetGuiPlugin.class,
                  new IllegalStateException(
                        "Transition attribute from artifact " + artifact.getHumanReadableId() + " is invalid.  Must be <fromState>;<transitionType>;<toState>"),
                  false);
            continue;
         }
         TransitionType transitionType = TransitionType.valueOf(strs[1]);
         addPageTransition(strs[0], strs[2], transitionType);
      }
      startPageId = artifact.getSoleAttributeValue(WorkItemAttributes.START_PAGE.getAttributeTypeName(), "");
   }

   @Override
   public Artifact toArtifact(WriteType writeType) throws Exception {
      Artifact art = super.toArtifact(writeType);
      if (getStartPageId() == null) throw new IllegalStateException(
            "For WorkFlowDefinition " + getId() + ".  Start Page not defined.");
      art.setSoleAttributeFromString(WorkItemAttributes.START_PAGE.getAttributeTypeName(), getStartPageId());
      List<String> transitionItems = new ArrayList<String>();
      for (Entry<String, Map<TransitionType, Set<String>>> pageToTransEntry : pageIdToPageIdsViaTransitionType.entrySet()) {
         for (Entry<TransitionType, Set<String>> transToPageIdsEntry : pageIdToPageIdsViaTransitionType.get(
               pageToTransEntry.getKey()).entrySet()) {
            for (String toPage : transToPageIdsEntry.getValue()) {
               transitionItems.add(pageToTransEntry.getKey() + ";" + transToPageIdsEntry.getKey().name() + ";" + toPage);
            }
         }
      }
      art.setAttributeValues(WorkItemAttributes.TRANSITION.getAttributeTypeName(), transitionItems);
      return art;
   }

   public Set<String> getPageNames() throws Exception {
      loadPageData();
      return pageNamesToPageDef.keySet();
   }

   public Set<String> getPageIds() throws Exception {
      loadPageData();
      return pageIdsToPageDef.keySet();
   }

   public void loadPageData() throws Exception {
      if (pageNamesToPageDef == null) {
         pageNamesToPageDef = new HashMap<String, WorkPageDefinition>();
         pageIdsToPageDef = new HashMap<String, WorkPageDefinition>();
         for (String pageId : pageIdToPageIdsViaTransitionType.keySet()) {
            WorkPageDefinition workPageDefinition =
                  (WorkPageDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(pageId);
            pageNamesToPageDef.put(workPageDefinition.name, workPageDefinition);
            pageIdsToPageDef.put(workPageDefinition.id, workPageDefinition);
            for (Map<TransitionType, Set<String>> transTypeToPageIds : pageIdToPageIdsViaTransitionType.values()) {
               for (TransitionType transType : transTypeToPageIds.keySet()) {
                  for (String pageId2 : transTypeToPageIds.get(transType)) {
                     workPageDefinition = (WorkPageDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(pageId2);
                     pageNamesToPageDef.put(workPageDefinition.name, workPageDefinition);
                     pageIdsToPageDef.put(workPageDefinition.id, workPageDefinition);
                  }
               }
            }
         }
      }
   }

   public WorkPageDefinition getWorkPageDefinitionByName(String name) throws Exception {
      loadPageData();
      return pageNamesToPageDef.get(name);
   }

   public WorkPageDefinition getWorkPageDefinitionById(String id) throws Exception {
      loadPageData();
      return pageIdsToPageDef.get(id);
   }

   /**
    * @return Returns the defaultToPage.
    */
   public WorkPageDefinition getDefaultToPage(WorkPageDefinition workPageDefinition) {
      if (getPageDefinitions(workPageDefinition.getId(), TransitionType.ToPageAsDefault).size() > 0) return getPageDefinitions(
            workPageDefinition.getId(), TransitionType.ToPageAsDefault).iterator().next();
      return null;
   }

   /**
    * This convenience method registers a transition forward and back. Useful for final/hold states like cancelled or
    * completed that can be returned from<br>
    * 1) fromPageId, toPageId, ToPage<br>
    * 2) toPageId, fromPageId, ToPageAsReturn
    * 
    * @param fromPageId
    * @param toPageId
    * @param transitionType
    */
   public void addPageTransitionToPageAndReturn(String fromPageId, String toPageId) {
      addPageTransition(fromPageId, toPageId, TransitionType.ToPage);
      addPageTransition(toPageId, fromPageId, TransitionType.ToPageAsReturn);
   }

   /**
    * Register transition for from and to pages
    * 
    * @param fromPageId
    * @param toPageId
    * @param transitionType
    */
   public void addPageTransition(String fromPageId, String toPageId, TransitionType... transitionType) {
      List<Object> transTypes = Collections.getAggregate((Object[]) transitionType);
      Map<TransitionType, Set<String>> transitionTypeToPageIds = pageIdToPageIdsViaTransitionType.get(fromPageId);
      if (transitionTypeToPageIds == null) {
         transitionTypeToPageIds = new HashMap<TransitionType, Set<String>>();
      }
      for (TransitionType transType : transitionType) {
         Set<String> toPageIds = transitionTypeToPageIds.get(transType);
         if (toPageIds == null) {
            toPageIds = new HashSet<String>();
         }
         if (transTypes.contains(TransitionType.ToPageAsDefault) && toPageIds.size() > 0) {
            throw new IllegalArgumentException("Only allowed ONE DefaultToPage");
         }
         toPageIds.add(toPageId);

         transitionTypeToPageIds.put(transType, toPageIds);
         pageIdToPageIdsViaTransitionType.put(fromPageId, transitionTypeToPageIds);
      }
   }

   public List<WorkPageDefinition> getPageDefinitions(String fromPageId, TransitionType... transitionType) {
      Map<TransitionType, Set<String>> transitionTypeToPageIds = pageIdToPageIdsViaTransitionType.get(fromPageId);
      if (transitionTypeToPageIds == null) {
         return EMPTY_PAGE_DEFS;
      }
      List<WorkPageDefinition> workPageDefs = new ArrayList<WorkPageDefinition>();
      for (TransitionType transType : transitionType) {
         Set<String> toPageIds = transitionTypeToPageIds.get(transType);
         if (toPageIds == null) {
            continue;
         }
         try {
            for (WorkItemDefinition def : WorkItemDefinitionFactory.getWorkItemDefinitions(toPageIds)) {
               workPageDefs.add((WorkPageDefinition) def);
            }
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, false);
         }
      }
      return workPageDefs;
   }

   public List<WorkPageDefinition> getPagesOrdered() throws Exception {
      WorkPageDefinition startWorkPageDefinition = getStartPage();
      if (startWorkPageDefinition == null) throw new IllegalArgumentException(
            "Can't locate Start WorkPageDefinition for workflow " + getName());

      // Get ordered pages starting with start page
      List<WorkPageDefinition> orderedPages = new ArrayList<WorkPageDefinition>();
      getOrderedPages(startWorkPageDefinition, orderedPages);

      // Move completed to the end if it exists
      WorkPageDefinition completedPage = null;
      for (WorkPageDefinition workPageDefinition : orderedPages)
         if (workPageDefinition.isCompletePage()) completedPage = workPageDefinition;
      if (completedPage != null) {
         orderedPages.remove(completedPage);
         orderedPages.add(completedPage);
      }
      // for (WorkPage wPage : orderedPages)
      //    System.out.println("Ordered Page: - " + wPage);
      return orderedPages;
   }

   private void getOrderedPages(WorkPageDefinition workPageDefinition, List<WorkPageDefinition> pages) {
      // Add this page first
      if (!pages.contains(workPageDefinition)) pages.add(workPageDefinition);
      // Add default page
      if (getDefaultToPage(workPageDefinition) != null) getOrderedPages(
            (WorkPageDefinition) getDefaultToPage(workPageDefinition), pages);
      // Add remaining pages
      for (WorkPageDefinition wPage : getToPages(workPageDefinition))
         if (!pages.contains(wPage)) getOrderedPages((WorkPageDefinition) wPage, pages);
   }

   /**
    * @return Returns the toPages for given workPageDefinition including default and return toPages.
    */
   public List<WorkPageDefinition> getToPages(WorkPageDefinition workPageDefinition) {
      return getPageDefinitions(workPageDefinition.getId(), TransitionType.ToPage, TransitionType.ToPageAsDefault,
            TransitionType.ToPageAsReturn);
   }

   /**
    * @return Returns the returnPages for given workPageDefinition.
    */
   public List<WorkPageDefinition> getReturnPages(WorkPageDefinition workPageDefinition) {
      return getPageDefinitions(workPageDefinition.getId(), TransitionType.ToPageAsReturn);
   }

   public boolean isReturnPage(WorkPageDefinition fromWorkPageDefinition, WorkPageDefinition toWorkPageDefinition) {
      return getReturnPages(fromWorkPageDefinition).contains(toWorkPageDefinition);
   }

   public String toString() {
      try {
         return id + " - " + name + (parentId != null ? " - Parent: " + parentId : "") + getPageNames().toString();
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
         return id + " - " + name;
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition#getArtifactTypeName()
    */
   @Override
   public String getArtifactTypeName() {
      return ARTIFACT_NAME;
   }

   public WorkPageDefinition getStartPage() throws Exception {
      if (startPage == null) {
         startPage = (WorkPageDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(getStartPageId());
      }
      return startPage;
   }

   public String getStartPageId() {
      return startPageId;
   }

}
