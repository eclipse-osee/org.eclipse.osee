/*
 * Created on Dec 17, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef.provider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.dsl.atsDsl.AttrWidget;
import org.eclipse.osee.ats.dsl.atsDsl.Composite;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewDef;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewOpt;
import org.eclipse.osee.ats.dsl.atsDsl.DecisionReviewRef;
import org.eclipse.osee.ats.dsl.atsDsl.FollowupRef;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutCopy;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutDef;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutItem;
import org.eclipse.osee.ats.dsl.atsDsl.LayoutType;
import org.eclipse.osee.ats.dsl.atsDsl.PeerReviewDef;
import org.eclipse.osee.ats.dsl.atsDsl.PeerReviewRef;
import org.eclipse.osee.ats.dsl.atsDsl.StateDef;
import org.eclipse.osee.ats.dsl.atsDsl.ToState;
import org.eclipse.osee.ats.dsl.atsDsl.UserByName;
import org.eclipse.osee.ats.dsl.atsDsl.UserByUserId;
import org.eclipse.osee.ats.dsl.atsDsl.UserRef;
import org.eclipse.osee.ats.dsl.atsDsl.WidgetDef;
import org.eclipse.osee.ats.dsl.atsDsl.WidgetRef;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workdef.CompositeStateItem;
import org.eclipse.osee.ats.workdef.DecisionReviewDefinition;
import org.eclipse.osee.ats.workdef.DecisionReviewOption;
import org.eclipse.osee.ats.workdef.PeerReviewDefinition;
import org.eclipse.osee.ats.workdef.ReviewBlockType;
import org.eclipse.osee.ats.workdef.RuleDefinition;
import org.eclipse.osee.ats.workdef.StateDefinition;
import org.eclipse.osee.ats.workdef.StateEventType;
import org.eclipse.osee.ats.workdef.StateItem;
import org.eclipse.osee.ats.workdef.WidgetDefinition;
import org.eclipse.osee.ats.workdef.WidgetOption;
import org.eclipse.osee.ats.workdef.WorkDefinition;
import org.eclipse.osee.ats.workdef.WorkDefinitionSheet;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.PluginUtil;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.AttributeXWidgetManager;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IAttributeXWidgetProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;
import org.eclipse.osee.framework.ui.ws.AWorkspace;

/**
 * Loads Work Definitions from database or file ATS DSL
 * 
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionProvider {

   private static AtsWorkDefinitionProvider provider = new AtsWorkDefinitionProvider();

   public static AtsWorkDefinitionProvider get() {
      return provider;
   }

   public WorkDefinition getWorkFlowDefinition(String id) throws OseeCoreException {
      WorkDefinition workDef = loadWorkDefinitionFromArtifact(id);
      return workDef;
   }

   public void importAIsAndTeamsToDb(WorkDefinitionSheet sheet, SkynetTransaction transaction) throws OseeCoreException {
      String modelName = sheet.getFile().getName();
      AtsDsl atsDsl = loadAtsDslFromFile(modelName, sheet);
      ImportAIsAndTeamDefinitionsToDb importer = new ImportAIsAndTeamDefinitionsToDb(modelName, atsDsl, transaction);
      importer.execute();
   }

   public Artifact importWorkDefinitionSheetToDb(WorkDefinitionSheet sheet, SkynetTransaction transaction) throws OseeCoreException {
      String modelName = sheet.getFile().getName();
      AtsDsl atsDsl = loadAtsDslFromFile(modelName, sheet);
      WorkDefinition workDef = loadWorkDefinition(modelName, atsDsl);
      Artifact artifact = null;
      if (workDef != null) {
         if (!sheet.getName().equals(workDef.getName())) {
            throw new OseeStateException("WorkDefinitionSheet [%s] internal name [%s] does not match sheet name",
               sheet.getName(), workDef.getName());
         }
         if (!sheet.getName().equals(workDef.getIds().iterator().next())) {
            throw new OseeStateException("WorkDefinitionSheet [%s] internal id [%s] does not match sheet name", sheet,
               workDef.getIds().iterator().next());
         }
         try {
            artifact =
               ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.WorkDefinition, sheet.getName(),
                  AtsUtil.getAtsBranch());
            if (artifact != null) {
               throw new OseeStateException("WorkDefinition [%s] already loaded into database", sheet);
            }
         } catch (ArtifactDoesNotExist ex) {
            // do nothing; this is what we want
         }
         artifact =
            ArtifactTypeManager.addArtifact(AtsArtifactTypes.WorkDefinition, AtsUtil.getAtsBranch(), sheet.getName());
         artifact.setSoleAttributeValue(AtsAttributeTypes.DslSheet, loadWorkFlowDefinitionStringFromFile(sheet));
         artifact.persist(transaction);

      }

      ImportAIsAndTeamDefinitionsToDb importer = new ImportAIsAndTeamDefinitionsToDb(modelName, atsDsl, transaction);
      importer.execute();

      return artifact;
   }

   public String loadWorkFlowDefinitionStringFromFile(WorkDefinitionSheet sheet) throws OseeCoreException {
      if (!sheet.getFile().exists()) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, String.format("WorkDefinition [%s]", sheet));
         return null;
      }
      try {
         return Lib.fileToString(sheet.getFile());
      } catch (IOException ex) {
         throw new OseeWrappedException(String.format("Error loading workdefinition sheet[%s]", sheet), ex);
      }
   }

   public WorkDefinition loadTeamWorkDefFromFileOldWay() {
      WorkDefinition oldWorkDef = loadWorkDefinitionFromFileOld("WorkDef_Team_Default.ats");
      if (oldWorkDef == null) {
         System.err.println("OLD: Didn't load WorkDef_Team_Default.ats");
      } else {
         System.out.println("OLD: Load successful WorkDef_Team_Default.ats");
      }
      return oldWorkDef;
   }

   public WorkDefinition loadTeamWorkDefFromFileNewWay() {
      WorkDefinition oldWorkDef = loadWorkDefinitionFromFileNew("WorkDef_Team_Default.ats");
      if (oldWorkDef == null) {
         System.err.println("NEW: Didn't load WorkDef_Team_Default.ats");
      } else {
         System.out.println("NEW: Load successful WorkDef_Team_Default.ats");
      }
      return oldWorkDef;
   }

   private WorkDefinition loadWorkDefinitionFromFileOld(String SHEET_NAME) {
      try {
         IResource resource = AWorkspace.findWorkspaceFile(SHEET_NAME);
         if (resource == null) {
            System.err.println(String.format("Can not load SHEET_NAME [%s] from file", SHEET_NAME));
            return null;
         }
         File file = resource.getRawLocation().toFile();
         if (file.getAbsolutePath().contains("osee.data")) {
            System.err.println(String.format("File [%s] comming from osee.data, delete and retry", SHEET_NAME));
         }
         AtsDsl atsDsl = loadAtsDsl(SHEET_NAME, Lib.fileToString(file));
         WorkDefinition workDef = loadWorkDefinition(SHEET_NAME, atsDsl);
         workDef.setDescription(String.format("loaded from file [%s]", SHEET_NAME));
         return workDef;
      } catch (Exception ex) {
         throw new WrappedException(String.format("Error loading Work Definition from sheet [%s]", SHEET_NAME), ex);
      }
   };

   private WorkDefinition loadWorkDefinitionFromFileNew(String SHEET_NAME) {
      try {
         PluginUtil util = new PluginUtil("org.eclipse.osee.ats");
         String filename = "support/" + SHEET_NAME;
         File file = util.getPluginFile(filename);
         if (!file.exists()) {
            System.err.println(String.format("Can not load filename [%s] from file", filename));
            return null;
         }
         AtsDsl atsDsl = loadAtsDsl(SHEET_NAME, Lib.fileToString(file));
         WorkDefinition workDef = loadWorkDefinition(SHEET_NAME, atsDsl);
         workDef.setDescription(String.format("loaded from file [%s]", SHEET_NAME));
         return workDef;
      } catch (Exception ex) {
         throw new WrappedException(String.format("Error loading Work Definition from sheet [%s]", SHEET_NAME), ex);
      }
   };

   public AtsDsl loadAtsDslFromFile(String modelName, WorkDefinitionSheet sheet) {
      try {
         AtsDsl atsDsl = loadAtsDsl(modelName, loadWorkFlowDefinitionStringFromFile(sheet));
         return atsDsl;
      } catch (Exception ex) {
         throw new WrappedException(ex);
      }
   }

   public WorkDefinition loadWorkFlowDefinitionFromFile(WorkDefinitionSheet sheet) {
      try {
         String modelName = sheet.getFile().getName();
         AtsDsl atsDsl = loadAtsDslFromFile(modelName, sheet);
         WorkDefinition workDef = loadWorkDefinition(modelName, atsDsl);
         workDef.setDescription(String.format("Loaded WorkDefinitionSheet [%s]", sheet));
         return workDef;
      } catch (Exception ex) {
         throw new WrappedException(ex);
      }
   }

   private WorkDefinition loadWorkDefinitionFromArtifact(String name) throws OseeCoreException {
      Artifact artifact = null;
      try {
         artifact =
            ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.WorkDefinition, name,
               BranchManager.getCommonBranch());
         String modelText = artifact.getAttributesToString(AtsAttributeTypes.DslSheet);
         String modelName = name + ".ats";
         AtsDsl atsDsl = loadAtsDsl(modelName, modelText);
         return loadWorkDefinition(modelName, atsDsl);
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return null;
   };

   private AtsDsl loadAtsDsl(String name, String modelText) throws OseeCoreException {
      AtsDsl atsDsl = ModelUtil.loadModel(name, modelText);
      return atsDsl;
   }

   public String unQuote(String name) {
      return name.replaceFirst("^\"", "").replaceFirst("\"$", "");
   }

   private WorkDefinition loadWorkDefinition(String name, AtsDsl atsDsl) {
      if (atsDsl.getWorkDef() == null) {
         return null;
      }
      WorkDefinition workDef = new WorkDefinition(unQuote(atsDsl.getWorkDef().getName()));
      for (String id : atsDsl.getWorkDef().getId()) {
         workDef.getIds().add(id);
      }

      List<WidgetDefinition> widgetDefs = retrieveWigetDefs(atsDsl, name);
      Map<StateDefinition, String> copyLayoutFromMap = new HashMap<StateDefinition, String>();

      // Process and define all states
      for (StateDef dslState : atsDsl.getWorkDef().getStates()) {
         String stateName = Strings.unquote(dslState.getName());
         StateDefinition stateDef = workDef.getOrCreateState(stateName);
         stateDef.setWorkDefinition(workDef);

         // Process state settings
         stateDef.setOrdinal(dslState.getOrdinal());
         WorkPageType workPageType = WorkPageType.Working;
         try {
            workPageType = WorkPageType.valueOf(dslState.getPageType());
         } catch (IllegalArgumentException ex) {
            // do nothing
         }
         stateDef.setWorkPageType(workPageType);
         stateDef.setPercentWeight(dslState.getPercentWeight());

         // Process widgets
         LayoutType layout = dslState.getLayout();
         if (layout instanceof LayoutDef) {
            processLayoutItems(name, widgetDefs, stateDef.getStateItems(), ((LayoutDef) layout).getLayoutItems());
         } else if (layout instanceof LayoutCopy) {
            copyLayoutFromMap.put(stateDef, Strings.unquote(((LayoutCopy) layout).getState().getName()));
         }

         // process rules
         for (String ruleName : dslState.getRules()) {
            stateDef.addRule(new RuleDefinition(ruleName), "Dsl StateDef Rule");
         }

      }

      // Process States needing layoutCopy
      for (Entry<StateDefinition, String> entry : copyLayoutFromMap.entrySet()) {
         StateDefinition fromStateDef = workDef.getStateByName(entry.getValue());
         StateDefinition toStateDef = entry.getKey();
         for (StateItem item : fromStateDef.getStateItems()) {
            toStateDef.getStateItems().add(item);
         }
      }

      // Process and define all transitions
      for (StateDef dslState : atsDsl.getWorkDef().getStates()) {
         StateDefinition stateDef = workDef.getStateByName(Strings.unquote(dslState.getName()));
         // Process transitions
         for (ToState dslToState : dslState.getTransitionStates()) {
            StateDefinition toStateDef = workDef.getStateByName(Strings.unquote(dslToState.getState().getName()));
            stateDef.getToStates().add(toStateDef);
            for (String dslTransOption : dslToState.getOptions()) {
               if ("AsDefault".equals(dslTransOption)) {
                  stateDef.setDefaultToState(toStateDef);
               }
               if ("OverrideAttributeValidation".equals(dslTransOption)) {
                  stateDef.getOverrideAttributeValidationStates().add(toStateDef);
               }
            }
         }
      }

      // Process all decision reviews
      for (StateDef dslState : atsDsl.getWorkDef().getStates()) {
         StateDefinition stateDef = workDef.getStateByName(Strings.unquote(dslState.getName()));
         for (DecisionReviewRef dslRevRef : dslState.getDecisionReviews()) {
            DecisionReviewDef dslRevDef = dslRevRef.getDecisionReview();
            DecisionReviewDefinition revDef = convertDslDecisionReview(dslRevDef);
            if (!Strings.isValid(revDef.getRelatedToState())) {
               revDef.setRelatedToState(stateDef.getName());
            }
            stateDef.getDecisionReviews().add(revDef);
         }
      }

      // Process all peer reviews
      for (StateDef dslState : atsDsl.getWorkDef().getStates()) {
         StateDefinition stateDef = workDef.getStateByName(Strings.unquote(dslState.getName()));
         for (PeerReviewRef peerRevRef : dslState.getPeerReviews()) {
            PeerReviewDef dslRevDef = peerRevRef.getPeerReview();
            PeerReviewDefinition revDef = convertDslPeerReview(dslRevDef);
            if (!Strings.isValid(revDef.getRelatedToState())) {
               revDef.setRelatedToState(stateDef.getName());
            }
            stateDef.getPeerReviews().add(revDef);
         }
      }

      // Set the start state
      workDef.setStartState(workDef.getStateByName(Strings.unquote(atsDsl.getWorkDef().getStartState().getName())));

      return workDef;
   }

   private PeerReviewDefinition convertDslPeerReview(PeerReviewDef dslRevDef) {
      PeerReviewDefinition revDef = new PeerReviewDefinition(dslRevDef.getName());
      revDef.setReviewTitle(dslRevDef.getTitle());
      revDef.setDescription(dslRevDef.getDescription());
      revDef.setLocation(dslRevDef.getLocation());

      String dslBlockType = dslRevDef.getBlockingType().getName();
      ReviewBlockType blockType = ReviewBlockType.None;
      try {
         blockType = ReviewBlockType.valueOf(dslBlockType);
      } catch (IllegalArgumentException ex) {
         OseeLog.log(AtsPlugin.class, Level.WARNING,
            String.format("Unknown ReviewBlockType [%s]; Defaulting to None", dslBlockType));
      }
      revDef.setBlockingType(blockType);

      String dslEventType = dslRevDef.getStateEvent().getName();
      StateEventType eventType = StateEventType.None;
      try {
         eventType = StateEventType.valueOf(dslEventType);
      } catch (IllegalArgumentException ex) {
         OseeLog.log(AtsPlugin.class, Level.WARNING,
            String.format("Unknown StateEventType [%s]; Defaulting to None", dslEventType));
      }
      revDef.setStateEventType(eventType);
      Collection<String> userIds = getAssigneesFromUserRefs(dslRevDef.getAssigneeRefs());
      revDef.getAssignees().addAll(userIds);
      return revDef;
   }

   private Collection<String> getAssigneesFromUserRefs(EList<UserRef> UserRefs) {
      Set<String> userIds = new HashSet<String>();
      for (UserRef UserRef : UserRefs) {
         if (UserRef instanceof UserByName) {
            UserByName byName = (UserByName) UserRef;
            String name = byName.getName();
            if (!Strings.isValid(name)) {
               OseeLog.log(AtsPlugin.class, Level.WARNING, String.format("Unhandled UserByName name [%s]", name));
               continue;
            }
            try {
               User user = UserManager.getUserByName(name);
               userIds.add(user.getUserId());
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.WARNING,
                  String.format("No user by name [%s] [%s]", name, ex.getLocalizedMessage()));
            }
         } else if (UserRef instanceof UserByUserId) {
            UserByUserId byUserId = (UserByUserId) UserRef;
            String userId = byUserId.getUserId();
            if (!Strings.isValid(userId)) {
               OseeLog.log(AtsPlugin.class, Level.WARNING, String.format("Unhandled UserByUserId id [%s]", userId));
               continue;
            }
            try {
               User user = UserManager.getUserByUserId(userId);
               userIds.add(user.getUserId());
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.WARNING,
                  String.format("No user by id [%s] [%s]", userId, ex.getLocalizedMessage()));
            }
         } else {
            OseeLog.log(AtsPlugin.class, Level.WARNING, String.format("Unhandled UserRef type [%s]", UserRef));
         }
      }
      return userIds;
   }

   private DecisionReviewDefinition convertDslDecisionReview(DecisionReviewDef dslRevDef) {
      DecisionReviewDefinition revDef = new DecisionReviewDefinition(dslRevDef.getName());
      revDef.setReviewTitle(dslRevDef.getTitle());
      revDef.setDescription(dslRevDef.getDescription());

      String dslBlockType = dslRevDef.getBlockingType().getName();
      ReviewBlockType blockType = ReviewBlockType.None;
      try {
         blockType = ReviewBlockType.valueOf(dslBlockType);
      } catch (IllegalArgumentException ex) {
         OseeLog.log(AtsPlugin.class, Level.WARNING,
            String.format("Unknown ReviewBlockType [%s]; Defaulting to None", dslBlockType));
      }
      revDef.setBlockingType(blockType);

      String dslEventType = dslRevDef.getStateEvent().getName();
      StateEventType eventType = StateEventType.None;
      try {
         eventType = StateEventType.valueOf(dslEventType);
      } catch (IllegalArgumentException ex) {
         OseeLog.log(AtsPlugin.class, Level.WARNING,
            String.format("Unknown StateEventType [%s]; Defaulting to None", dslEventType));
      }
      revDef.setStateEventType(eventType);
      revDef.setAutoTransitionToDecision(BooleanDefUtil.get(dslRevDef.getAutoTransitionToDecision(), false));

      for (DecisionReviewOpt dslOpt : dslRevDef.getOptions()) {
         DecisionReviewOption revOpt = new DecisionReviewOption(dslOpt.getName());
         FollowupRef followupRef = dslOpt.getFollowup();
         if (followupRef == null) {
            revOpt.setFollowupRequired(false);
         } else {
            revOpt.getUserIds().addAll(UserRefUtil.getUserIds(followupRef.getAssigneeRefs()));
            revOpt.getUserNames().addAll(UserRefUtil.getUserNames(followupRef.getAssigneeRefs()));
         }
         revDef.getOptions().add(revOpt);
      }

      Collection<String> userIds = getAssigneesFromUserRefs(dslRevDef.getAssigneeRefs());
      revDef.getAssignees().addAll(userIds);
      return revDef;
   }

   private void processLayoutItems(String SHEET_NAME, List<WidgetDefinition> widgetDefs, List<StateItem> stateItems, EList<LayoutItem> layoutItems) {
      for (LayoutItem layoutItem : layoutItems) {
         if (layoutItem instanceof WidgetDef) {
            WidgetDefinition widgetDef = convertDslWidgetDef((WidgetDef) layoutItem, SHEET_NAME);
            stateItems.add(widgetDef);
         } else if (layoutItem instanceof WidgetRef) {
            String widgetName = Strings.unquote(((WidgetRef) layoutItem).getWidget().getName());
            boolean found = false;
            for (WidgetDefinition wd : widgetDefs) {
               if (wd.getName().equals(widgetName)) {
                  stateItems.add(wd);
                  found = true;
               }
            }
            if (!found) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE,
                  String.format("Could not find WidgetRef [%s] in WidgetDefs", widgetName));
            }
         } else if (layoutItem instanceof AttrWidget) {
            AttrWidget attrWidget = (AttrWidget) layoutItem;
            String attributeName = attrWidget.getAttributeName();
            try {
               AttributeType attributeType = AttributeTypeManager.getType(attributeName);
               if (attributeType == null) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE,
                     String.format("Invalid attribute name [%s] in WorkDefinition [%s]", attributeName, SHEET_NAME));
               } else {
                  WidgetDefinition widgetDef = new WidgetDefinition(attributeType.getUnqualifiedName());
                  widgetDef.setAttributeName(attributeType.getName());

                  setXWidgetNameBasedOnAttribute(attributeType, widgetDef);
                  extractDslWidgetDefOptions(attrWidget.getOption(), SHEET_NAME, widgetDef);
                  stateItems.add(widgetDef);
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex,
                  "Error resolving attribute [%s] to WorkDefinition in [%s]", attributeName, SHEET_NAME);
            }
         } else if (layoutItem instanceof Composite) {
            Composite composite = (Composite) layoutItem;
            CompositeStateItem compStateItem = new CompositeStateItem(composite.getNumColumns());
            if (!composite.getLayoutItems().isEmpty()) {
               processLayoutItems(SHEET_NAME, widgetDefs, compStateItem.getStateItems(), composite.getLayoutItems());
            }
            stateItems.add(compStateItem);
         }
      }

   }

   private void setXWidgetNameBasedOnAttribute(AttributeType attributeType, WidgetDefinition widgetDef) throws OseeCoreException {
      IAttributeXWidgetProvider xWidgetProvider = AttributeXWidgetManager.getAttributeXWidgetProvider(attributeType);
      List<DynamicXWidgetLayoutData> concreteWidgets = xWidgetProvider.getDynamicXWidgetLayoutData(attributeType);
      widgetDef.setXWidgetName(concreteWidgets.iterator().next().getXWidgetName());
   }

   private List<WidgetDefinition> retrieveWigetDefs(AtsDsl atsDsl, String SHEET_NAME) {
      List<WidgetDefinition> widgetDefs = new ArrayList<WidgetDefinition>();
      for (WidgetDef dslWidgetDef : atsDsl.getWorkDef().getWidgetDefs()) {
         WidgetDefinition widgetDef = convertDslWidgetDef(dslWidgetDef, SHEET_NAME);
         widgetDefs.add(widgetDef);
      }
      return widgetDefs;
   }

   private WidgetDefinition convertDslWidgetDef(WidgetDef dslWidgetDef, String SHEET_NAME) {
      WidgetDefinition widgetDef = new WidgetDefinition(Strings.unquote(dslWidgetDef.getName()));
      widgetDef.setAttributeName(dslWidgetDef.getAttributeName());
      widgetDef.setDescription(dslWidgetDef.getDescription());
      if (Strings.isValid(dslWidgetDef.getXWidgetName())) {
         widgetDef.setXWidgetName(dslWidgetDef.getXWidgetName());
      } else {
         String attributeName = dslWidgetDef.getAttributeName();
         if (Strings.isValid(attributeName)) {
            try {
               AttributeType attributeType = AttributeTypeManager.getType(attributeName);
               if (attributeType == null) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE,
                     String.format("Invalid attribute name [%s] in WorkDefinition [%s]", attributeName, SHEET_NAME));
               } else {
                  setXWidgetNameBasedOnAttribute(attributeType, widgetDef);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(
                  AtsPlugin.class,
                  Level.SEVERE,
                  String.format("Error resolving attribute name [%s] in WorkDefinition [%s]", attributeName, SHEET_NAME));
            }
         } else {
            OseeLog.log(AtsPlugin.class, Level.SEVERE,
               String.format("Invalid attribute name [%s] in WorkDefinition [%s]", attributeName, SHEET_NAME));
         }
      }

      widgetDef.setHeight(dslWidgetDef.getHeight());
      widgetDef.setDefaultValue(dslWidgetDef.getDefaultValue());
      extractDslWidgetDefOptions(dslWidgetDef.getOption(), SHEET_NAME, widgetDef);
      return widgetDef;
   }

   private void extractDslWidgetDefOptions(EList<String> options, String SHEET_NAME, WidgetDefinition widgetDef) {
      for (String value : options) {
         WidgetOption option = null;
         try {
            option = WidgetOption.valueOf(value);
            widgetDef.getOptions().add(option);
         } catch (IllegalArgumentException ex) {
            OseeLog.log(AtsPlugin.class, Level.WARNING, ex, "Unexpected value [%s] in WorkDefinition [%s] ", value,
               SHEET_NAME);
         }
      }
   }

   public void convertAndOpenAtsDsl(WorkDefinition workDef, XResultData resultData, String filename) throws OseeCoreException {
      ConvertWorkDefinitionToAtsDsl converter = new ConvertWorkDefinitionToAtsDsl(workDef, resultData);
      AtsDsl atsDsl = converter.convert(filename.replaceFirst("\\.ats", ""));
      File file = OseeData.getFile(filename);
      try {
         FileOutputStream outputStream = new FileOutputStream(file);
         ModelUtil.saveModel(atsDsl, "ats:/ats_fileanme" + Lib.getDateTimeString() + ".ats", outputStream, false);
         String contents = Lib.fileToString(file);

         contents = cleanupContents(atsDsl, workDef, contents);

         Lib.writeStringToFile(contents, file);
         IFile iFile = OseeData.getIFile(filename);
         AWorkspace.openEditor(iFile);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   public void convertAndOpenAIandTeamAtsDsl(XResultData resultData) throws OseeCoreException {
      ConvertAIsAndTeamsToAtsDsl converter = new ConvertAIsAndTeamsToAtsDsl(resultData);
      AtsDsl atsDsl = converter.convert("AIsAndTeams");
      String filename = "AIsAndTeams.ats";
      File file = OseeData.getFile("AIsAndTeams.ats");
      try {
         FileOutputStream outputStream = new FileOutputStream(file);
         ModelUtil.saveModel(atsDsl, "ats:/ats_fileanme" + Lib.getDateTimeString() + ".ats", outputStream, false);
         String contents = Lib.fileToString(file);

         //         contents = cleanupContents(atsDsl, null, contents);

         Lib.writeStringToFile(contents, file);
         IFile iFile = OseeData.getIFile(filename);
         AWorkspace.openEditor(iFile);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private String cleanupContents(AtsDsl atsDsl, WorkDefinition workDef, String contents) {

      String copyOfContents = new String(contents);

      for (String name : Arrays.asList("widgetDefinition", "state")) {
         Matcher m = Pattern.compile(name + " (.*) \\{").matcher(copyOfContents);
         while (m.find()) {
            String srchStr = name + " " + m.group(1) + " \\{";
            String replStr = name + " \"" + m.group(1) + "\" {";
            contents = contents.replaceAll(srchStr, replStr);
         }
      }

      // Fix lines with "<keyword> this and that $"
      for (String name : Arrays.asList("widget", "startState")) {
         Matcher m = Pattern.compile(name + " (.*) *$", Pattern.MULTILINE).matcher(copyOfContents);
         while (m.find()) {
            String srchStr = name + " " + m.group(1);
            String replStr = name + " \"" + m.group(1) + "\"";
            contents = contents.replaceAll(srchStr, replStr);
         }
      }

      Matcher m = Pattern.compile("widget Decision Review Options$", Pattern.MULTILINE).matcher(contents);
      contents = m.replaceAll("widget \"Decision Review Options\"");

      if (workDef != null) {
         for (StateDefinition stateDef : workDef.getStates()) {
            String srchStr = "to " + stateDef.getName();
            String replStr = "to \"" + stateDef.getName() + "\"";
            contents = contents.replaceAll(srchStr, replStr);
         }
      }

      m = Pattern.compile("      option NONE\\s", Pattern.MULTILINE).matcher(contents);
      contents = m.replaceAll("");

      m = Pattern.compile("      option EDITABLE\\s", Pattern.MULTILINE).matcher(contents);
      contents = m.replaceAll("");

      m = Pattern.compile("      height 9999\\s", Pattern.MULTILINE).matcher(contents);
      contents = m.replaceAll("");

      m = Pattern.compile("      option ALIGN_LEFT\\s", Pattern.MULTILINE).matcher(contents);
      contents = m.replaceAll("");

      m = Pattern.compile("      defaultValue \"\"\\s", Pattern.MULTILINE).matcher(contents);
      contents = m.replaceAll("");

      m = Pattern.compile("      attributeName \"ats.Working Branch\"\\s", Pattern.MULTILINE).matcher(contents);
      contents = m.replaceAll("");

      m =
         Pattern.compile("      attributeName \"ats.Validate Requirement Changes\"\\s", Pattern.MULTILINE).matcher(
            contents);
      contents = m.replaceAll("");

      m = Pattern.compile("      attributeName \"ats.Commit Manager\"\\s", Pattern.MULTILINE).matcher(contents);
      contents = m.replaceAll("");

      m =
         Pattern.compile("      attributeName \"ats.OperationalImpact.required\"\\s", Pattern.MULTILINE).matcher(
            contents);
      contents = m.replaceAll("");

      m =
         Pattern.compile("      attributeName \"ats.OperationalImpactWithWorkaround.required\"\\s", Pattern.MULTILINE).matcher(
            contents);
      contents = m.replaceAll("");

      m =
         Pattern.compile("      attributeName \"ats.Show CDB Differences Report\"\\s", Pattern.MULTILINE).matcher(
            contents);
      contents = m.replaceAll("");

      m = Pattern.compile("      attributeName \"ats.Check Signals Via CDB\"\\s", Pattern.MULTILINE).matcher(contents);
      contents = m.replaceAll("");

      m = Pattern.compile("      attributeName \"ats.Create Code/Test Tasks\"\\s", Pattern.MULTILINE).matcher(contents);
      contents = m.replaceAll("");

      return contents;
   }

}
