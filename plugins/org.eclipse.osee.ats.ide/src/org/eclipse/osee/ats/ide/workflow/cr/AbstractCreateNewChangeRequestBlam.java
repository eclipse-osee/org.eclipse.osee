/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.ats.ide.workflow.cr;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.CogPriority;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.CrashOrBlankDisplay;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Description;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.NeedBy;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionResult;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.blam.AbstractAtsBlam;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXChangeTypeWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXPriorityWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXProgramAiWidget;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.util.BooleanState;
import org.eclipse.osee.framework.core.widget.ISelectableValueProvider;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXBooleanWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXDateSelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsEnumArtWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractCreateNewChangeRequestBlam extends AbstractAtsBlam implements ISelectableValueProvider {

   private static final String BLAM_DESC =
      "Create program top level Demo Change Request for any new feature or problem found.\n" //
         + "This will mature into all the work for all teams needed to resolve this request.";
   public static final String DEBUG_DESCRIPTION = "Description...";
   protected static final String TITLE = "Title";
   protected static final String PROGRAM = "Program";
   protected final static String DESCRIPTION = "Description";
   protected static final String CHANGE_TYPE = "Change Type";
   protected static final String PRIORITY = "Priority";
   protected static final String COG_PRIORITY = "COG Priority";
   protected static final String NEED_BY = NeedBy.getUnqualifiedName();

   protected XXTextWidget titleWidget;
   protected XXTextWidget descWidget;
   protected XXChangeTypeWidget changeTypeWidget;
   protected XXPriorityWidget priorityWidget;
   protected XXStringsEnumArtWidget cogPriorityWidget;
   protected XXProgramAiWidget programWidget;
   private XXBooleanWidget crashWidget;
   private XXDateSelWidget needByWidget;
   private String overrideTitle;
   private NewActionData data;
   private NewActionData newData;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public AbstractCreateNewChangeRequestBlam(String name, OseeImage image) {
      super(name, image);
   }

   public AbstractCreateNewChangeRequestBlam(String name) {
      super(name, AtsImage.CHANGE_REQUEST);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      wba = createWidgetBuilder();

      wba.andWidget("Program", WidgetIdAts.XXProgramAiWidget).andValueProvider(this);
      wba.andTextWidget(TITLE, Name).andRequired();
      wba.andTextWidget(Description).andHeight(80).andRequired();

      // 6 columns
      wba.andChangeTypeWidget().andRequired().startComp(6);
      wba.andPriorityWidget().andRequired();
      wba.andDateWidget(NeedBy.getUnqualifiedName());
      wb.andBooleanWidget(CrashOrBlankDisplay).andRequired();

      return wba.getXWidgetDatas();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, xWidgetSwtRenderer, xModListener, isEditable);
      if (xWidget.getLabel().equals(TITLE)) {
         titleWidget = (XXTextWidget) xWidget;
      } else if (xWidget.getLabel().equals(DESCRIPTION)) {
         descWidget = (XXTextWidget) xWidget;
      } else if (xWidget.getLabel().equals(CHANGE_TYPE)) {
         changeTypeWidget = (XXChangeTypeWidget) xWidget;
      } else if (xWidget.getLabel().equals(PRIORITY)) {
         priorityWidget = (XXPriorityWidget) xWidget;
      } else if (xWidget.getLabel().equals(COG_PRIORITY)) {
         cogPriorityWidget = (XXStringsEnumArtWidget) xWidget;
      } else if (xWidget.getLabel().equals(NEED_BY)) {
         needByWidget = (XXDateSelWidget) xWidget;
      } else if (xWidget.getLabel().equals(CrashOrBlankDisplay.getUnqualifiedName())) {
         crashWidget = (XXBooleanWidget) xWidget;
      } else if (xWidget.isWidget(WidgetIdAts.XXProgramAiWidget)) {

         if (xWidget instanceof XXProgramAiWidget) {
            programWidget = (XXProgramAiWidget) xWidget;
            programWidget.getLabelLinkWidget().addMouseListener(new MouseAdapter() {
               @Override
               public void mouseUp(MouseEvent event) {
                  if (event.button == 3) {
                     handlePopulateWithDebugInfo();
                  }
               }
            });
            Collection<IAtsActionableItem> selectable = programWidget.getSelectable();
            if (selectable.size() == 1) {
               programWidget.setSelected(selectable.iterator().next());
               programWidget.refresh();
            }
         }
      }
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      this.variableMap = variableMap;
      XResultData results = new XResultData();
      String title = variableMap.getString(TITLE);
      if (Strings.isInValid(title)) {
         results.error("Enter Title");
      }

      IAtsActionableItem programAi = (IAtsActionableItem) variableMap.getValue(PROGRAM);
      if (programAi == null || programAi.isInvalid()) {
         results.error("Select Program");
      }

      String desc = variableMap.getString(DESCRIPTION);
      if (Strings.isInValid(desc)) {
         results.error("Enter Description");
      }

      String cType = variableMap.getString(CHANGE_TYPE);
      if (changeTypeWidget != null) {
         if (Strings.isInValid(cType)) {
            if (changeTypeWidget.isRequiredEntry()) {
               results.error("Select Change Type");
            }
         }
      }

      String priority = null;
      if (priorityWidget != null) {
         priority = priorityWidget.getSelectedFirst();
         if (priorityWidget.isRequiredEntry()) {
            if (Strings.isInvalid(priority)) {
               results.error("Select Priority");
            }
         }
      }

      if (cogPriorityWidget != null) {
         String cogPriority = cogPriorityWidget.getSelectedFirst();
         if (Strings.isInvalid(cogPriority) && cogPriorityWidget.isRequiredEntry()) {
            results.error("Select COG Priority");
         }
      }

      if (crashWidget != null) {
         BooleanState crash = crashWidget.getBoolean();
         if (crash.isUnSet()) {
            if (crashWidget.isRequiredEntry()) {
               results.error("Select " + CrashOrBlankDisplay.getUnqualifiedName());
            }
         }
      }
      Date needBy = null;
      if (needByWidget != null) {
         needBy = (Date) variableMap.getValue(NEED_BY);
         if (needByWidget.isRequiredEntry()) {
            results.error("Select Need By");
         }
      }

      // Allow subsclass validation of additional input
      isValidEntry(results);

      // Log all strings to output section
      log(results.toString());

      // Return if failed
      if (results.isErrors()) {
         return;
      }

      data = atsApi.getActionService().createActionData(getName(), title, desc) //
         .andAi(programAi) //
         .andNeedBy(needBy) //
         .andRd(results);
      if (Strings.isValid(priority)) {
         data.andPriority(priority);
      }
      if (cType != null) {
         data.andChangeType(cType);
      }
      if (crashWidget != null) {
         data.andAttr(CrashOrBlankDisplay, crashWidget.getSelected());
      }
      if (cogPriorityWidget != null) {
         data.andAttr(CogPriority, cogPriorityWidget.getSelectedFirst());
      }

      createActionData(data);

      newData = atsApi.getActionService().createAction(data);

      if (newData.getRd().isErrors()) {
         XResultDataUI.report(newData.getRd(), getTitle());
         return;
      }
      Collection<IAtsTeamWorkflow> teamWfs = newData.getActResult().getAtsTeamWfs();

      operationCompleted(newData);

      if (teamWfs.size() == 1) {
         WorkflowEditor.edit(teamWfs.iterator().next());
      } else {
         WorldEditor.open(new WorldEditorSimpleProvider("New CR Workflows", AtsObjects.getArtifacts(teamWfs)));
      }
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   abstract public Collection<IAtsActionableItem> getProgramCrAis();

   protected String getDebugTitle() {
      return "New CR " + atsApi.getRandomNum();
   }

   /**
    * Method is used to quickly create a unique title for debug purposes. Should only be used for tests.
    */
   public void handlePopulateWithDebugInfo() {
      try {
         titleWidget.setSelected(Strings.isValid(overrideTitle) ? overrideTitle : getDebugTitle());
         descWidget.setSelected(DEBUG_DESCRIPTION);
         if (changeTypeWidget != null) {
            changeTypeWidget.setSelected(ChangeTypes.Fix.name());
         }
         if (cogPriorityWidget != null) {
            cogPriorityWidget.setSelected(Arrays.asList("2"));
         }
         if (priorityWidget != null) {
            priorityWidget.setSelected("3");
         }
         if (crashWidget != null) {
            crashWidget.setSelected(BooleanState.Yes);
         }
         if (needByWidget != null) {
            needByWidget.setDate(new Date());
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public boolean includePriority() {
      return true;
   }

   public void operationCompleted(NewActionData newData) {
      // for additional operations after create
   }

   public void createActionData(NewActionData data) {
      // for additional NewActionData entries
   }

   protected Collection<IAtsActionableItem> getNewActionAis(IAtsActionableItem programAi) {
      return Collections.singleton(programAi);
   }

   public Collection<IAtsVersion> getSelectedProgramVersions() {
      IAtsTeamDefinition progTeamDef = getSelectedProgramTeamDefOrSentinel();
      if (progTeamDef.isValid()) {
         return atsApi.getVersionService().getVersionsFromTeamDefHoldingVersions(progTeamDef);
      }
      return Collections.emptyList();
   }

   public IAtsTeamDefinition getSelectedProgramTeamDefOrSentinel() {
      IAtsTeamDefinition progTeamDef = IAtsTeamDefinition.SENTINEL;
      IAtsActionableItem progAi = getSelectedProgramAiOrSentinel();
      if (progAi != null) {
         if (progAi.isValid()) {
            progTeamDef = atsApi.getActionableItemService().getTeamDefinitionInherited(progAi);
         }
      }
      return progTeamDef;
   }

   public IAtsActionableItem getSelectedProgramAiOrSentinel() {
      IAtsActionableItem selected = IAtsActionableItem.SENTINEL;
      if (selected != null) {
         IAtsActionableItem selAi = programWidget.getSelectedFirst();
         if (selAi != null) {
            selected = selAi;
         }
      }
      return selected;
   }

   @Override
   abstract public String getRunText();

   @Override
   public String getOutputMessage() {
      return "Not yet run.";
   }

   @Override
   public String getTabTitle() {
      return "Change Request";
   }

   @Override
   public String getTitle() {
      return getName();
   }

   @Override
   public Collection<Object> getSelectable(Object widget) {
      if (widget instanceof XXProgramAiWidget) {
         return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(getProgramCrAis());
      }
      return Collections.emptyList();
   }

   public void setOverrideTitle(String overrideTitle) {
      this.overrideTitle = overrideTitle;
   }

   public NewActionResult getActionResult() {
      if (newData != null) {
         return newData.getActResult();
      }
      return null;
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavItemCat.TOP_NEW);
   }

   @Override
   public String getDescriptionUsage() {
      return BLAM_DESC;
   }

}
