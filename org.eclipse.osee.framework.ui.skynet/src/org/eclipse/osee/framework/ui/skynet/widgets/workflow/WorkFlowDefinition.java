/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class WorkFlowDefinition extends WorkItemDefinition {

   public static String ARTIFACT_NAME = "Work Flow Definition";

   public static enum TransitionType {
      // Normal transition; will be provided as option in transition pulldown
      ToPage,
      // Allows the page to "return" to an earlier state; allows transition w/o page completion;
      // will be provided as an option in transition pulldown (don't need to register as ToPage)
      ToPageAsReturn,
      // ToPage that will also be registered as the default selected transition state
      ToPageAsDefault,
   }
   // fromPageId --- TransitionType ---> toPageIds
   // Contains locally defined transitions
   private Map<String, Map<TransitionType, Set<String>>> pageIdToPageIdsViaTransitionType =
         new HashMap<String, Map<TransitionType, Set<String>>>();
   // Contains locally and inherited transitions 
   protected Map<String, Map<TransitionType, Set<String>>> inheritedPageIdToPageIdsViaTransitionType;
   // Contains local and inherited pageNameToPageIds
   protected Map<String, String> pageNameToPageId;
   protected String startPageId;
   protected String resolvedStartPageId;

   public WorkFlowDefinition(String name, String id, String parentId) {
      super(name, id, parentId);
   }

   public WorkFlowDefinition(Artifact artifact) throws OseeCoreException, SQLException {
      this(artifact.getDescriptiveName(), artifact.getSoleAttributeValue(
            WorkItemAttributes.WORK_ID.getAttributeTypeName(), ""), artifact.getSoleAttributeValue(
            WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName(), (String) null));
      setType(artifact.getSoleAttributeValue(WorkItemAttributes.WORK_TYPE.getAttributeTypeName(), (String) null));

      // Add local transitions from this artifact
      addTransitionsFromArtifact(artifact, pageIdToPageIdsViaTransitionType, getId());

      // Read in this workflow's start page
      startPageId =
            artifact.getSoleAttributeValue(WorkItemAttributes.START_PAGE.getAttributeTypeName(), null, String.class);
   }

   @Override
   public Artifact toArtifact(WriteType writeType) throws OseeCoreException, SQLException {
      Artifact art = super.toArtifact(writeType);
      // Make sure start page is defined in this or parent's definition
      if (getResolvedStartPageId() == null) {
         throw new IllegalStateException(
               "For WorkFlowDefinition " + getId() + ".  Start Page not defined.  Must be in this or a parent's WorkFlowDefinition.");
      }
      // Only store start page if it's part of this definition
      if (startPageId != null) {
         art.setSoleAttributeFromString(WorkItemAttributes.START_PAGE.getAttributeTypeName(), startPageId);
      }
      // Store transition items declared as part of this definition
      List<String> transitionItems = new ArrayList<String>();
      for (Entry<String, Map<TransitionType, Set<String>>> pageToTransEntry : pageIdToPageIdsViaTransitionType.entrySet()) {
         for (Entry<TransitionType, Set<String>> transToPageIdsEntry : pageIdToPageIdsViaTransitionType.get(
               pageToTransEntry.getKey()).entrySet()) {
            for (String toPage : transToPageIdsEntry.getValue()) {
               transitionItems.add(pageToTransEntry.getKey() + ";" + transToPageIdsEntry.getKey().name() + ";" + toPage);
            }
         }
      }
      if (transitionItems.size() > 0) {
         art.setAttributeValues(WorkItemAttributes.TRANSITION.getAttributeTypeName(), transitionItems);
      }
      return art;
   }

   public Collection<String> getPageNames() throws OseeCoreException, SQLException {
      loadPageData();
      return pageNameToPageId.keySet();
   }

   public static void loadInheritedData(WorkFlowDefinition workFlowDefinition, String workflowId, Map<String, Map<TransitionType, Set<String>>> inheritedPageIdToPageIdsViaTransitionType) throws OseeCoreException, SQLException {
      addTransitionsFromArtifact(WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(workFlowDefinition.getId()),
            inheritedPageIdToPageIdsViaTransitionType, workflowId);
      if (workFlowDefinition.hasParent()) {
         WorkFlowDefinition.loadInheritedData((WorkFlowDefinition) workFlowDefinition.getParent(), workflowId,
               inheritedPageIdToPageIdsViaTransitionType);
      }
   }

   public synchronized void loadPageData() throws OseeCoreException, SQLException {
      if (inheritedPageIdToPageIdsViaTransitionType == null) {
         inheritedPageIdToPageIdsViaTransitionType = new HashMap<String, Map<TransitionType, Set<String>>>();
         WorkFlowDefinition.loadInheritedData(this, getId(), inheritedPageIdToPageIdsViaTransitionType);
      }
      resolvedStartPageId = getResolvedStartPageId(this, getId());
      if (pageNameToPageId == null) {
         pageNameToPageId = new HashMap<String, String>();
         for (String pageNameOrId : inheritedPageIdToPageIdsViaTransitionType.keySet()) {
            WorkPageDefinition workPageDefinition =
                  (WorkPageDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(getFullPageId(pageNameOrId));
            pageNameToPageId.put(workPageDefinition.getPageName(), workPageDefinition.id);
            for (Map<TransitionType, Set<String>> transTypeToPageIds : inheritedPageIdToPageIdsViaTransitionType.values()) {
               for (TransitionType transType : transTypeToPageIds.keySet()) {
                  for (String pageId2 : transTypeToPageIds.get(transType)) {
                     workPageDefinition =
                           (WorkPageDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(getFullPageId(pageId2));
                     pageNameToPageId.put(workPageDefinition.getPageName(), workPageDefinition.id);
                  }
               }
            }
         }
      }
   }

   /**
    * Since transitions can be defined by full ids or just page name (eg "Endorse"), check if pageId has namespace
    * characters and thus it's full name or add id to given pageId
    * 
    * @param pageId
    * @return
    */
   private String getFullPageId(String pageId) {
      return (pageId.contains(".")) ? pageId : getId() + "." + pageId;
   }

   public WorkPageDefinition getWorkPageDefinitionByName(String name) throws OseeCoreException, SQLException {
      loadPageData();
      return getWorkPageDefinitionById(pageNameToPageId.get(name));
   }

   public WorkPageDefinition getWorkPageDefinitionById(String id) throws OseeCoreException, SQLException {
      loadPageData();
      return (WorkPageDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(id);
   }

   /**
    * @return Returns the defaultToPage.
    */
   public WorkPageDefinition getDefaultToPage(WorkPageDefinition workPageDefinition) throws OseeCoreException, SQLException {
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
      addPageTransition(pageIdToPageIdsViaTransitionType, fromPageId, toPageId, TransitionType.ToPage);
      addPageTransition(pageIdToPageIdsViaTransitionType, toPageId, fromPageId, TransitionType.ToPageAsReturn);
   }

   public static void addTransitionsFromArtifact(Artifact artifact, Map<String, Map<TransitionType, Set<String>>> pageIdToPageIdsViaTransitionType, String workflowId) throws OseeCoreException, SQLException {
      if (artifact == null) return;
      // Read in this workflow's transition information
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
         TransitionType transType = TransitionType.valueOf(strs[1]);
         // Since workflows can be defined by stateName or pageId, resolve any stateName to pageId so only dealing with one index
         // eg (Endorse -> <workflow id>.Endorse -> osee.ats.Endorse  AND osee.ats.Endorse -> osee.ats.Endorse)
         String fromPage = strs[0].contains(".") ? strs[0] : workflowId + "." + strs[0];
         String toPage = strs[2].contains(".") ? strs[2] : workflowId + "." + strs[2];
         addPageTransition(pageIdToPageIdsViaTransitionType, fromPage, toPage, transType);
      }

   }

   /**
    * Register transition for from and to pages. The use of simple page names (eg "Endorse") allows for other workflows
    * to inherit this workflow by just using the same state names. id will be prepended to name prior to retrieving the
    * WorkPageDefinitions
    * 
    * @param fromPageId either page Name "Endorse" or full namespace "osee.ats.Endorse"
    * @param toPageId either page Name "Endorse" or full namespace "osee.ats.Endorse"
    * @param transitionType
    */
   public void addPageTransition(String fromPageId, String toPageId, TransitionType... transitionType) {
      WorkFlowDefinition.addPageTransition(pageIdToPageIdsViaTransitionType, fromPageId, toPageId, transitionType);
   }

   /**
    * Register transition for from and to pages. The use of simple page names (eg "Endorse") allows for other workflows
    * to inherit this workflow by just using the same state names. id will be prepended to name prior to retrieving the
    * WorkPageDefinitions
    * 
    * @param fromPageId either page Name "Endorse" or full namespace "osee.ats.Endorse"
    * @param toPageId either page Name "Endorse" or full namespace "osee.ats.Endorse"
    * @param transitionType
    */
   public static void addPageTransition(Map<String, Map<TransitionType, Set<String>>> pageIdToPageIdsViaTransitionType, String fromPageId, String toPageId, TransitionType... transitionType) {
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

   /**
    * Return all registered page definitions including those inherited from parent(s)
    * 
    * @param fromPageId
    * @param transitionType
    * @return
    * @throws Exception
    */
   public List<WorkPageDefinition> getPageDefinitions(String fromPageId, TransitionType... transitionType) throws OseeCoreException, SQLException {
      return getPageDefinitions(this, fromPageId, true, transitionType);
   }

   public Map<TransitionType, Set<String>> getTransitionTypeToPageIds(String fromPageId) throws OseeCoreException, SQLException {
      loadPageData();
      return pageIdToPageIdsViaTransitionType.get(fromPageId);
   }

   public Map<TransitionType, Set<String>> getInheritedTransitionTypeToPageIds(String fromPageId) throws OseeCoreException, SQLException {
      loadPageData();
      return inheritedPageIdToPageIdsViaTransitionType.get(fromPageId);
   }

   public static List<WorkPageDefinition> getPageDefinitions(WorkFlowDefinition workFlowDefinition, String fromPageId, boolean includeInherited, TransitionType... transitionType) throws OseeCoreException, SQLException {
      Map<TransitionType, Set<String>> transitionTypeToPageIds = null;
      if (includeInherited) {
         transitionTypeToPageIds = workFlowDefinition.getInheritedTransitionTypeToPageIds(fromPageId);
      } else {
         transitionTypeToPageIds = workFlowDefinition.getTransitionTypeToPageIds(fromPageId);
      }
      List<WorkPageDefinition> workPageDefs = new ArrayList<WorkPageDefinition>();
      if (transitionTypeToPageIds != null) {
         for (TransitionType transType : transitionType) {
            Set<String> toPageIds = transitionTypeToPageIds.get(transType);
            if (toPageIds == null) {
               continue;
            }
            for (WorkItemDefinition def : WorkItemDefinitionFactory.getWorkItemDefinitions(toPageIds)) {
               workPageDefs.add((WorkPageDefinition) def);
            }
         }
      }
      return workPageDefs;
   }

   public List<WorkPageDefinition> getPagesOrdered() throws OseeCoreException, SQLException {
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

   private void getOrderedPages(WorkPageDefinition workPageDefinition, List<WorkPageDefinition> pages) throws OseeCoreException, SQLException {
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
   public List<WorkPageDefinition> getToPages(WorkPageDefinition workPageDefinition) throws OseeCoreException, SQLException {
      return getPageDefinitions(workPageDefinition.getId(), TransitionType.ToPage, TransitionType.ToPageAsDefault,
            TransitionType.ToPageAsReturn);
   }

   /**
    * @return Returns the returnPages for given workPageDefinition.
    */
   public List<WorkPageDefinition> getReturnPages(WorkPageDefinition workPageDefinition) throws OseeCoreException, SQLException {
      return getPageDefinitions(workPageDefinition.getId(), TransitionType.ToPageAsReturn);
   }

   public boolean isReturnPage(WorkPageDefinition fromWorkPageDefinition, WorkPageDefinition toWorkPageDefinition) throws OseeCoreException, SQLException {
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

   public WorkPageDefinition getStartPage() throws OseeCoreException, SQLException {
      loadPageData();
      if (resolvedStartPageId != null) {
         return (WorkPageDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(resolvedStartPageId);
      }
      return null;
   }

   public void setStartPageId(String startPageId) {
      this.startPageId = startPageId;
   }

   public String getStartPageId() throws OseeCoreException, SQLException {
      loadPageData();
      return startPageId;
   }

   public String getResolvedStartPageId() throws OseeCoreException, SQLException {
      loadPageData();
      return resolvedStartPageId;
   }

   private static String getResolvedStartPageId(WorkFlowDefinition workFlowDefinition, String workflowId) throws OseeCoreException, SQLException {
      if (workFlowDefinition.startPageId != null) {
         return workFlowDefinition.startPageId.contains(".") ? workFlowDefinition.startPageId : workflowId + "." + workFlowDefinition.startPageId;
      }
      if (workFlowDefinition.hasParent()) {
         return getResolvedStartPageId((WorkFlowDefinition) workFlowDefinition.getParent(), workflowId);
      }
      return null;
   }
}
