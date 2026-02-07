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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileFeatureGroup;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.Priorities;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionResult;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsEditors;
import org.eclipse.osee.ats.ide.util.widgets.XAgileFeatureHyperlinkWidget;
import org.eclipse.osee.ats.ide.util.widgets.XAssigneesHyperlinkWidget;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelActionableItemSelection;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkChangeTypeSelection;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkPrioritySelection;
import org.eclipse.osee.ats.ide.util.widgets.XOriginatorHyperlinkWidget;
import org.eclipse.osee.ats.ide.util.widgets.XSprintHyperlinkWidget;
import org.eclipse.osee.ats.ide.util.widgets.XTargetedVersionHyperlinkWidget;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.widget.XWidgetData;
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
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class CreateNewActionBlam extends AbstractBlam {
   protected static final String BLAM_DESCRIPTION = "Select options to create new ATS Action";
   protected static final String TITLE = "Title";
   protected static final String PROGRAM = "Program";
   protected final static String DESCRIPTION = "Description";
   protected static final String CHANGE_TYPE = "Change Type";
   protected static final String PRIORITY = "Priority";
   protected static final String NEED_BY = "Need By";
   protected XText titleWidget;
   protected XText descWidget;
   protected XHyperlinkChangeTypeSelection changeTypeWidget;
   protected XHyperlinkPrioritySelection priorityWidget;
   protected XHyperlabelActionableItemSelection aiWidget;
   protected final AtsApi atsApi;
   protected XWidgetBuilder mainWb;
   protected XWidgetBuilder teamWb;
   private Composite teamComp;
   private static Set<CreateNewActionProvider> providerExtensionItems = new HashSet<>();
   private final Set<CreateNewActionProvider> handledExtensionItems = new HashSet<>();
   private IManagedForm form;
   private Section section;
   private Composite comp;
   private final Collection<XWidget> teamXWidgets = new ArrayList<>();
   private final HashCollection<IAtsTeamDefinition, XWidget> teamDefToWidgets = new HashCollection<>();
   private NewActionData data;
   private NewActionData newData;

   public CreateNewActionBlam() {
      this("Create New Action", BLAM_DESCRIPTION);
   }

   public CreateNewActionBlam(String name, String desc) {
      super(name, desc, null);
      this.atsApi = AtsApiService.get();
   }

   // For subclass validation of widgets
   protected boolean isValidEntry() {
      return true;
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      this.variableMap = variableMap;
      AtomicBoolean valid = new AtomicBoolean(true);

      String title = variableMap.getString(TITLE);
      if (Strings.isInValid(title)) {
         log("Enter Title");
         valid.set(false);
      }

      String desc = variableMap.getString(DESCRIPTION);
      if (Strings.isInValid(desc)) {
         log("Enter Description");
         valid.set(false);
      }

      ChangeTypes cType = (ChangeTypes) variableMap.getValue(CHANGE_TYPE);
      if (cType == null || cType == ChangeTypes.None) {
         log("Invalid Change Type");
         valid.set(false);
      }

      Priorities priority = (Priorities) variableMap.getValue(PRIORITY);
      if (priority == null) {
         log("Select Priority");
         valid.set(false);
      }

      Date needBy = (Date) variableMap.getValue(NEED_BY);

      Collection<IAtsActionableItem> actionableItems = aiWidget.getSelectedActionableItems();
      if (actionableItems.isEmpty()) {
         log("Must select Actionable Item(s)");
         valid.set(false);
      }

      String priorityStr = Priorities.Three.name();
      if (priority != null) {
         priorityStr = priority.getName();
      }

      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            for (XWidget widget : teamXWidgets) {
               IStatus status = widget.isValid();
               if (!status.isOK()) {
                  log(status.getMessage());
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

      data = atsApi.getActionService().createActionData(getName(), title, desc, cType, priorityStr) //
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

      newData = atsApi.getActionService().createAction(data);
      if (newData.getRd().isErrors()) {
         // Error Editor already opened, don't need to open another
         // Log to the BLAM results section
         log(newData.getRd().toString());
         return;
      }

      TransactionId tx = newData.getActResult().getTransaction();

      actionCreated(newData.getActResult(), tx);

      AtsEditors.openResults(newData);

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
         if (widget.getAttributeType().isValid()) {
            AttributeTypeToken attrType = widget.getAttributeType();
            if (widget.isMultiSelect()) {
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
         } else if (widget.getLabel().equals("Originator")) {
            XOriginatorHyperlinkWidget orig = (XOriginatorHyperlinkWidget) widget;
            AtsUser originator = orig.getSelected();
            if (originator != null) {
               data.setCreatedByUserArtId(originator.getIdString());
            }
         } else if (widget.getLabel().equals("Assignees")) {
            XAssigneesHyperlinkWidget assign = (XAssigneesHyperlinkWidget) widget;
            Collection<AtsUser> assignees = assign.getSelected();
            if (assignees != null) {
               data.setAssigneesArtIds(AtsObjects.toIdsString(",", assignees));
            }
         } else if (widget.getLabel().equals("Sprint")) {
            XSprintHyperlinkWidget sprintWidget = (XSprintHyperlinkWidget) widget;
            IAgileSprint sprint = sprintWidget.getSelected();
            if (sprint != null) {
               data.setSprint(sprint.getIdString());
            }
         } else if (widget.getLabel().equals("Targeted Version")) {
            XTargetedVersionHyperlinkWidget verWidget = (XTargetedVersionHyperlinkWidget) widget;
            IAtsVersion version = verWidget.getSelected();
            if (version != null) {
               data.setVersionId(version.getArtifactId());
            }
         } else if (widget.getLabel().equals("Feature Group")) {
            XAgileFeatureHyperlinkWidget featureWidget = (XAgileFeatureHyperlinkWidget) widget;
            Collection<IAgileFeatureGroup> features = featureWidget.getFeatures();
            if (!features.isEmpty()) {
               data.setFeatureGroup(features.iterator().next().getIdString());
            }
         }
      }
      for (CreateNewActionProvider provider : getCreateNewActionProviderExtensions()) {
         provider.teamCreating(data, teamXWidgets);
      }
   }

   @Override
   public List<XWidgetData> getXWidgetItems() {
      mainWb = new XWidgetBuilder();
      mainWb.andXText(TITLE, CoreAttributeTypes.Name).andRequired().endWidget();
      mainWb.andXHyperlinkActionableItemActive().andRequired().endWidget();
      mainWb.andXText(AtsAttributeTypes.Description).andHeight(80).andRequired().endWidget();
      addWidgetsAfterDescription(mainWb);
      mainWb.andChangeType(ChangeTypes.DEFAULT_CHANGE_TYPES).andRequired().endWidget();
      mainWb.andPriority().andRequired().endWidget();
      addWidgetAfterPriority();
      mainWb.andXHyperLinkDate(AtsAttributeTypes.NeedBy.getUnqualifiedName()).endComposite().endWidget();
      return mainWb.getXWidgetDatas();
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

         teamWb = new XWidgetBuilder();
         for (CreateNewActionProvider item : getCreateNewActionProviderExtensions()) {
            if (!handledExtensionItems.contains(item)) {
               boolean hasProviderXWidgetExtensions =
                  item.hasProviderXWidgetExtensions(aiWidget.getSelectedActionableItems());
               if (hasProviderXWidgetExtensions) {
                  teamWb.andXLabel(
                     String.format("------- Additional Items for [%s] ------- ", item.getClass().getSimpleName()));
                  for (IAtsTeamDefinition teamDef : uniqueTeamDefs) {
                     teamWb.andXLabel(String.format("      --- Items for Team Def [%s] ---", teamDef.getName()));
                     /*
                      * Extensions MUST align each widget with it's related andTeamId in case there are multiple
                      * different team defs impacted
                      */
                     item.getAdditionalXWidgetItems(teamWb, teamDef);
                  }

                  handledExtensionItems.add(item);
               }
            }
         }
         List<XWidgetData> widDatas = teamWb.getXWidgetDatas();

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
               XWidgetUtility.setLabelFontsBold(widgetPage.getSwtXWidgetRenderer().getXWidgets());
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
         Collection<IAtsTeamDefinition> teamDefs = atsApi.getTeamDefinitionService().getImpactedTeamDefInherited(ai);
         uniqueTeamDefs.addAll(teamDefs);
      }
      return uniqueTeamDefs;
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer swtXWidgetRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, swtXWidgetRenderer, xModListener, isEditable);
      if (xWidget.getLabel().equals(TITLE)) {
         titleWidget = (XText) xWidget;
         titleWidget.getLabelWidget().addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               if (event.button == 3) {
                  handlePopulateWithDebugInfo();
               }
            }
         });

      } else if (xWidget.getLabel().equals(DESCRIPTION)) {
         descWidget = (XText) xWidget;
      } else if (xWidget.getLabel().equals(CHANGE_TYPE)) {
         changeTypeWidget = (XHyperlinkChangeTypeSelection) xWidget;
         setChangeTypeWidget(changeTypeWidget);
      } else if (xWidget.getLabel().equals(PRIORITY)) {
         priorityWidget = (XHyperlinkPrioritySelection) xWidget;
      } else if (xWidget instanceof XHyperlabelActionableItemSelection) {
         aiWidget = (XHyperlabelActionableItemSelection) xWidget;
         aiWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               Collection<IAtsActionableItem> ais = aiWidget.getSelectedActionableItems();
               updateTeamComposites(ais);

               if (!ais.isEmpty()) {
                  IAtsActionableItem ai = ais.iterator().next();

                  List<ChangeTypes> changeTypeOptions = atsApi.getWorkItemService().getChangeTypeOptions(ai);
                  changeTypeWidget.setSelectable(changeTypeOptions);

                  List<Priorities> priorityOptions = atsApi.getWorkItemService().getPrioritiesOptions(ai);
                  priorityWidget.setSelectable(priorityOptions);
               }

            }
         });
      }
      for (CreateNewActionProvider provider : getCreateNewActionProviderExtensions()) {
         provider.widgetCreated(xWidget, toolkit, art, swtXWidgetRenderer, xModListener, isEditable);
      }
   }

   private Collection<ChangeTypes> setChangeTypeWidget(XHyperlinkChangeTypeSelection changeWidget) {
      Collection<IAtsActionableItem> ais = aiWidget.getSelectedActionableItems();
      if (ais == null || ais.isEmpty()) {
         logf("Must Select Actionable Item(s) First");
         return Collections.emptyList();
      }

      IAtsActionableItem ai = ais.iterator().next();
      Collection<ChangeTypes> changeTypes = atsApi.getWorkItemService().getChangeTypeOptions(ai);

      changeWidget.setSelectable(changeTypes);
      return changeTypes;
   }

   public void handlePopulateWithDebugInfo() {
      String title = "New Action " + atsApi.getRandomNum();
      handlePopulateWithDebugInfo(title);
   }

   /**
    * Method is used to quickly create a unique title for debug purposes. Should only be used for tests.
    */
   public void handlePopulateWithDebugInfo(String title) {
      try {
         Collection<IAtsActionableItem> ais = new ArrayList<IAtsActionableItem>();
         String defaultAiId = atsApi.getUserConfigValue(AtsUtil.DEFAULT_AI_KEY);
         if (Strings.isNumeric(defaultAiId)) {
            IAtsActionableItem ai =
               atsApi.getConfigService().getConfigurations().getIdToAi().get(Long.valueOf(defaultAiId));
            if (ai != null) {
               ais.add(ai);
            }
         }
         if (ais.isEmpty()) {
            for (IAtsActionableItem ai : atsApi.getConfigService().getConfigurations().getIdToAi().values()) {
               if (ai.getName().equals("Framework")) {
                  ais.add(ai);
               } else if (ai.getName().equals("SAW Requirements")) {
                  ais.add(ai);
               }
            }
         }
         aiWidget.setSelectedAIs(ais);
         Collection<ChangeTypes> cTypes = setChangeTypeWidget(changeTypeWidget);
         if (cTypes.isEmpty()) {
            changeTypeWidget.setSelected(ChangeTypes.Improvement);
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

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavItemCat.TOP);
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(AtsImage.NEW_ACTION);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.NEW_ACTION);
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
         XResultDataUI.report(newData.getDebugRd(), TITLE + " Debug");
      }
   }

   @Override
   public boolean isDebugRunAvailable() {
      return true;
   }

}
