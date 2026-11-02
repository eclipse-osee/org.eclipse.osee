/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.actions.newaction;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.Description;
import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.NeedBy;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionResult;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.blam.AbstractAtsBlam;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXActionableItemWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXAssigneesWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXChangeTypeWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXOriginatorWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXPointsEnumWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.XXPriorityWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.rel.XXAgileFeatureSelWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.rel.XXAgileSprintSelWidget;
import org.eclipse.osee.ats.ide.util.widgets.xx.rel.XXTargetedVersionWidget;
import org.eclipse.osee.ats.ide.workdef.XWidgetBuilderAts;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.OseeEnum;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.osgi.framework.Bundle;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class CreateNewActionBlam extends AbstractAtsBlam {

   private static final String BLAM_NAME = "Create New Action";
   protected static final String BLAM_DESC = "Select options to create new ATS Action";
   private static Set<CreateNewActionProvider> providerExtensionItems = new HashSet<>();

   protected XXTextWidget titleWidget;
   protected XXTextWidget descWidget;
   protected XXChangeTypeWidget changeTypeWidget;
   protected XXPriorityWidget priorityWidget;
   protected XXActionableItemWidget aiWidget;
   protected XWidgetBuilderAts wba;
   protected XWidgetBuilderAts teamWbA;
   protected XXOriginatorWidget origWidget;
   protected AtsApi atsApi;
   private Composite teamComp;
   private final Set<CreateNewActionProvider> handledExtensionItems = new HashSet<>();
   private IManagedForm form;
   private Section section;
   private Composite comp;
   private final Collection<XWidget> teamXWidgets = new ArrayList<>();
   private final HashCollection<IAtsTeamDefinition, XWidget> teamDefToWidgets = new HashCollection<>();
   private NewActionData data;
   private NewActionData newData;
   private boolean openUponCreation = true;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public CreateNewActionBlam() {
      this(BLAM_NAME, AtsImage.NEW_ACTION);
   }

   public CreateNewActionBlam(String name, OseeImage image) {
      super(name, image);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      wba = createWidgetBuilder();
      wba.andTextWidget(XWidgetLabels.TITLE, Name).andRequired();
      wba.andAiWidget().andRequired().and(XOption.WRAP_VALUES);
      wba.andTextWidget(Description).andHeight(80).andRequired();
      addWidgetsAfterDescription(wba);
      wba.andChangeTypeWidget().andRequired();
      wba.andPriorityWidget().andRequired();
      addWidgetAfterPriority();
      wba.andDateWidget(NeedBy.getUnqualifiedName()).endComp();
      return wba.getXWidgetDatas();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, xWidgetSwtRenderer, xModListener, isEditable);
      if (xWidget.getLabel().equals(XWidgetLabels.TITLE)) {
         titleWidget = (XXTextWidget) xWidget;
         titleWidget.addLabelWidgetListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
               if (e.button == 3) {
                  handlePopulateWithDebugInfo();
               }
            }
         });

      } else if (xWidget.getLabel().equals(XWidgetLabels.DESCRIPTION)) {
         descWidget = (XXTextWidget) xWidget;
      } else if (xWidget.isWidget(WidgetIdAts.XXChangeTypeWidget)) {
         changeTypeWidget = (XXChangeTypeWidget) xWidget;
         setChangeTypeWidget(changeTypeWidget);
      } else if (xWidget.isWidget(WidgetIdAts.XXPriorityWidget)) {
         priorityWidget = (XXPriorityWidget) xWidget;
      } else if (xWidget instanceof XXActionableItemWidget) {
         aiWidget = (XXActionableItemWidget) xWidget;
         aiWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               Collection<IAtsActionableItem> ais = aiWidget.getSelected();
               updateTeamComposites(ais);

               if (!ais.isEmpty()) {
                  IAtsActionableItem ai = ais.iterator().next();

                  List<ChangeTypes> changeTypeOptions = atsApi().getWorkItemService().getChangeTypeOptions(ai);
                  changeTypeWidget.setSelectable(OseeEnum.toStrings(changeTypeOptions));

                  List<Priorities> priorityOptions = atsApi().getWorkItemService().getPrioritiesOptions(ai);
                  priorityWidget.setSelectable(OseeEnum.toStrings(priorityOptions));
               }

            }
         });
      }
      for (CreateNewActionProvider provider : getCreateNewActionProviderExtensions()) {
         provider.widgetCreated(xWidget, toolkit, art, xWidgetSwtRenderer, xModListener, isEditable);
      }
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      this.variableMap = variableMap;
      AtomicBoolean valid = new AtomicBoolean(true);

      String title = variableMap.getString(XWidgetLabels.TITLE);
      if (Strings.isInValid(title)) {
         log("Enter Title");
         valid.set(false);
      }

      String desc = variableMap.getString(XWidgetLabels.DESCRIPTION);
      if (Strings.isInValid(desc)) {
         log("Enter Description");
         valid.set(false);
      }

      String changeType = variableMap.getString(XWidgetLabels.CHANGE_TYPE);
      if (Strings.isInValid(changeType)) {
         log("Invalid Change Type");
         valid.set(false);
      }

      String priority = variableMap.getString(XWidgetLabels.PRIORITY);
      if (Strings.isInvalid(priority)) {
         log("Select Priority");
         valid.set(false);
      }

      Date needBy = (Date) variableMap.getValue(XWidgetLabels.NEED_BY);

      Collection<IAtsActionableItem> actionableItems = aiWidget.getSelected();
      if (actionableItems.isEmpty()) {
         log("Must select Actionable Item(s)");
         valid.set(false);
      }

      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            for (XWidget widget : teamXWidgets) {
               XResultData status = widget.isValid();
               if (!status.isOK()) {
                  log(status.toString());
                  valid.set(false);
               }
            }
         }
      }, true);

      for (CreateNewActionProvider provider : handledExtensionItems) {
         Result result = provider.isActionValidToCreate(actionableItems);
         if (result.isFalse()) {
            log(result.getText());
            valid.set(false);
         }
      }

      if (!isValidEntry() || !valid.get()) {
         return;
      }

      data = atsApi().getActionService().createActionData(getName(), title, desc, changeType, priority) //
         .andAis(actionableItems) //
         .andNeedBy(needBy);

      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            handleTeamXWidgets(data);
         }
      }, true);

      createActionData(data);

      data.setInDebug(isInDebug());

      newData = atsApi().getActionService().createAction(data);
      if (newData.getRd().isErrors()) {
         // Error Editor already opened, don't need to open another
         // Log to the BLAM results section
         log(newData.getRd().toString());
         return;
      }

      TransactionId tx = newData.getActResult().getTransaction();

      actionCreated(newData.getActResult(), tx);

      if (openUponCreation) {
         AtsEditors.openResults(newData);
      }

   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   protected AtsApi atsApi() {
      if (this.atsApi == null) {
         this.atsApi = AtsApiService.get();
      }
      return atsApi;
   }

   // For subclass validation of widgets
   protected boolean isValidEntry() {
      return true;
   }

   /**
    * @param data add additional attributes to data to be created
    */
   public void createActionData(NewActionData data) {
      // for subclass extension
   }

   /**
    * Perform tasks post creation. eg: send events
    *
    * @param tx
    */
   public void actionCreated(NewActionResult results, TransactionId tx) {
      // for subclass extension
   }

   protected void handleTeamXWidgets(NewActionData data) {
      for (XWidget widget : teamXWidgets) {
         if (widget.isWidget(WidgetIdAts.XXAssigneesWidget)) {
            XXAssigneesWidget assign = (XXAssigneesWidget) widget;
            if (!assign.getSelected().isEmpty()) {
               data.setAssigneesArtIds(AtsObjects.toIdsString(",", assign.getSelected()));
            }
         } else if (widget.isWidget(WidgetIdAts.XXAgileSprintSelWidget)) {
            XXAgileSprintSelWidget sprintWidget = (XXAgileSprintSelWidget) widget;
            IAgileSprint sprint = sprintWidget.getSelectedFirst();
            if (sprint != null) {
               data.setSprint(sprint.getIdString());
            }
         } else if (widget.getWidgetId().equals(WidgetIdAts.XXPointsEnumWidget)) {
            XXPointsEnumWidget points = (XXPointsEnumWidget) widget;
            String ptsStr = points.getSelectedFirst();
            if (Strings.isValid(ptsStr)) {
               data.setPoints(ptsStr);
            }
         } else if (widget.isWidget(WidgetIdAts.XXOriginatorWidget)) {
            origWidget = (XXOriginatorWidget) widget;
            UserToken originator = origWidget.getSelectedFirst();
            if (originator != null) {
               data.setCreatedByUserArtId(originator.getIdString());
            }
         } else if (widget.isWidget(WidgetIdAts.XXTargetedVersionWidget)) {
            XXTargetedVersionWidget verWidget = (XXTargetedVersionWidget) widget;
            ArtifactToken versionArt = verWidget.getSelectedFirst();
            if (versionArt != null) {
               IAtsVersion version = atsApi.getVersionService().getVersion(versionArt);
               if (version != null) {
                  data.setVersionId(version.getArtifactId());
               }
            }
         } else if (widget.isWidget(WidgetIdAts.XXAgileFeatureSelWidget)) {
            XXAgileFeatureSelWidget featureWidget = (XXAgileFeatureSelWidget) widget;
            Collection<IAgileFeatureGroup> features = featureWidget.getSelected();
            if (!features.isEmpty()) {
               data.setFeatureGroup(features.iterator().next().getIdString());
            }
         } else if (widget.getAttributeType().isValid()) {
            AttributeTypeToken attrType = widget.getAttributeType();
            if (widget.isValueMultiSelectFromSelectable()) {
               List<Object> objs = new ArrayList<>();
               for (Object obj : widget.getValues()) {
                  objs.add(obj);
               }
               data.addTeamData(widget.getTeamId(), attrType, objs);
            } else {
               Object obj = widget.getData();
               if (obj != null) {
                  if (obj instanceof String && Strings.isInvalid((String) obj)) {
                     continue;
                  }
                  data.addTeamData(widget.getTeamId(), attrType, obj);
               }
            }
         }
      }
      for (CreateNewActionProvider provider : getCreateNewActionProviderExtensions()) {
         provider.teamCreating(data, teamXWidgets);
      }
   }

   protected void addWidgetsAfterDescription(XWidgetBuilder wb) {
      // for extensibility
   }

   @Override
   public void createWidgets(Composite comp, IManagedForm form, Section section, XWidgetPage widgetPage) {
      this.comp = comp;
      this.form = form;
      this.section = section;
   }

   /**
    * Create widgets for specific teams
    */
   private void updateTeamComposites(Collection<IAtsActionableItem> ais) {
      try {
         teamXWidgets.clear();
         teamDefToWidgets.clear();
         if (Widgets.isAccessible(teamComp)) {
            teamComp.dispose();
         }
         teamComp = new Composite(comp, SWT.NONE);
         teamComp.setLayout(new GridLayout(1, false));
         teamComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
         handledExtensionItems.clear();

         Collection<IAtsTeamDefinition> uniqueTeamDefs = getUniqueTeamDefs(ais);

         teamWbA = createWidgetBuilder();
         for (CreateNewActionProvider item : getCreateNewActionProviderExtensions()) {
            if (!handledExtensionItems.contains(item)) {
               boolean hasProviderXWidgetExtensions = item.hasProviderXWidgetExtensions(aiWidget.getSelected());
               if (hasProviderXWidgetExtensions) {
                  teamWbA.andLabelWidget(
                     String.format("------- Additional Items for [%s] ------- ", item.getClass().getSimpleName()));
                  for (IAtsTeamDefinition teamDef : uniqueTeamDefs) {
                     teamWbA.andLabelWidget(String.format("      --- Items for Team Def [%s] ---", teamDef.getName()));
                     /*
                      * Extensions MUST align each widget with it's related andTeamId in case there are multiple
                      * different team defs impacted
                      */
                     item.getAdditionalXWidgetItems(teamWbA, teamDef);
                  }

                  handledExtensionItems.add(item);
               }
            }
         }
         List<XWidgetData> widDatas = teamWbA.getXWidgetDatas();

         if (!widDatas.isEmpty()) {
            try {
               XWidgetPage widgetPage = new XWidgetPage(widDatas);
               widgetPage.createBody(form, teamComp, null, null, true);
               for (XWidget widget : widgetPage.getSwtXWidgetRenderer().getXWidgets()) {
                  teamXWidgets.add(widget);
                  for (CreateNewActionProvider provider : getCreateNewActionProviderExtensions()) {
                     provider.widgetCreated(widget, form.getToolkit(), null, widgetPage.getSwtXWidgetRenderer(), null,
                        true);
                  }
               }
               for (XWidget widget : widgetPage.getSwtXWidgetRenderer().getXWidgets()) {
                  widget.validate();
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

         // Re-validate all default widgets since we had to remove all prior to adding team widgets
         for (XWidget widget : getBlamWidgets()) {
            widget.validate();
         }

         teamComp.getParent().layout(true, true);
         teamComp.getParent().getParent().layout(true, true);
         form.reflow(true);
         section.layout(true, true);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private Collection<IAtsTeamDefinition> getUniqueTeamDefs(Collection<IAtsActionableItem> ais) {
      Set<IAtsTeamDefinition> uniqueTeamDefs = new HashSet<>();
      for (IAtsActionableItem ai : ais) {
         Collection<IAtsTeamDefinition> teamDefs = atsApi().getTeamDefinitionService().getImpactedTeamDefInherited(ai);
         uniqueTeamDefs.addAll(teamDefs);
      }
      return uniqueTeamDefs;
   }

   private Collection<ChangeTypes> setChangeTypeWidget(XXChangeTypeWidget changeWidget) {
      Collection<IAtsActionableItem> ais = aiWidget.getSelected();
      if (ais == null || ais.isEmpty()) {
         logf("Must Select Actionable Item(s) First");
         return Collections.emptyList();
      }

      IAtsActionableItem ai = ais.iterator().next();
      Collection<ChangeTypes> changeTypes = atsApi().getWorkItemService().getChangeTypeOptions(ai);

      changeWidget.setSelectable(OseeEnum.toStrings(changeTypes));
      return changeTypes;
   }

   public void handlePopulateWithDebugInfo() {
      String title = "New Action " + atsApi().getRandomNum();
      handlePopulateWithDebugInfo(title);
   }

   /**
    * Method is used to quickly create a unique title for debug purposes. Should only be used for tests.
    */
   public void handlePopulateWithDebugInfo(String title) {
      try {
         Collection<IAtsActionableItem> ais = new ArrayList<IAtsActionableItem>();
         String defaultAiId = atsApi().getUserConfigValue(AtsUtil.DEFAULT_AI_KEY);
         if (Strings.isNumeric(defaultAiId)) {
            IAtsActionableItem ai =
               atsApi().getConfigService().getConfigurations().getIdToAi().get(Long.valueOf(defaultAiId));
            if (ai != null) {
               ais.add(ai);
            }
         }
         if (ais.isEmpty()) {
            for (IAtsActionableItem ai : atsApi().getConfigService().getConfigurations().getIdToAi().values()) {
               if (ai.getName().equals("Framework")) {
                  ais.add(ai);
               } else if (ai.getName().equals("SAW Requirements")) {
                  ais.add(ai);
               }
            }
         }
         aiWidget.setSelected(ais);
         if (origWidget != null) {
            origWidget.setSelected(atsApi.getUserService().getCurrentUserToken());
         }
         Collection<ChangeTypes> cTypes = setChangeTypeWidget(changeTypeWidget);
         if (cTypes.isEmpty()) {
            changeTypeWidget.setSelected(ChangeTypes.Improvement.name());
         } else {
            changeTypeWidget.setSelected(cTypes.iterator().next().name());
         }
         priorityWidget.setSelected("3");
         for (CreateNewActionProvider provider : getCreateNewActionProviderExtensions()) {
            provider.handlePopulateWithDebugInfo(title);
         }
         titleWidget.set(title);
         descWidget.set("see title");
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @SuppressWarnings("deprecation")
   private Set<CreateNewActionProvider> getCreateNewActionProviderExtensions() {
      if (!providerExtensionItems.isEmpty()) {
         return providerExtensionItems;
      }

      IExtensionPoint point =
         Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.ide.CreateNewActionProvider");
      if (point == null) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't access CreateNewActionProvider extension point");
         return providerExtensionItems;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("CreateNewActionProvider")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class<?> taskClass = bundle.loadClass(classname);
                     Object obj = taskClass.newInstance();
                     providerExtensionItems.add((CreateNewActionProvider) obj);
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP,
                        "Error loading CreateNewActionProvider extension", ex);
                  }
               }

            }
         }
      }
      return providerExtensionItems;
   }

   @Override
   public String getRunText() {
      return "Create New Action";
   }

   @Override
   public String getOutputMessage() {
      return "Not yet run.";
   }

   @Override
   public String getTabTitle() {
      return "New Action";
   }

   @Override
   public String getTitle() {
      return getName();
   }

   public NewActionResult getActionResult() {
      return data.getActResult();
   }

   protected void addWidgetAfterPriority() {
      // For sub-class extension
   }

   protected int getChangeTypeRowColumns() {
      return 6;
   }

   @Override
   protected void handleInDebugAfterExecution() {
      if (isInDebug()) {
         XResultDataUI.report(newData.getDebugRd(), XWidgetLabels.TITLE + " Debug");
      }
   }

   @Override
   public boolean isDebugRunAvailable() {
      return true;
   }

   public boolean isOpenUponCreation() {
      return openUponCreation;
   }

   public void setOpenUponCreation(boolean openUponCreation) {
      this.openUponCreation = openUponCreation;
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavItemCat.TOP);
   }

   @Override
   public String getDescriptionUsage() {
      return BLAM_DESC;
   }

}
