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
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.actions.wizard.IAtsWizardItem;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlabelActionableItemSelection;
import org.eclipse.osee.ats.ide.util.widgets.XHyperlinkChangeTypeSelection;
import org.eclipse.osee.ats.ide.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWfdForEnumAttr;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.ImageManager;
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
public class CreateNewActionBlam extends AbstractBlam implements INewActionListener {
   protected static final String BLAM_DESCRIPTION = "Select options to create new ATS Action";
   protected static final String TITLE = "Title";
   protected static final String PROGRAM = "Program";
   protected final static String DESCRIPTION = "Description";
   protected static final String CHANGE_TYPE = "Change Type";
   protected static final String PRIORITY = "Priority";
   protected static final String NEED_BY = "Need By";
   protected XText titleWidget;
   protected XText descWidget;
   protected XHyperlinkChangeTypeSelection changeWidget;
   protected XHyperlinkWfdForEnumAttr priorityWidget;
   protected XHyperlabelActionableItemSelection aiWidget;
   protected final AtsApi atsApi;
   protected XWidgetBuilder wb;
   private ActionResult actionResult;
   private XWidgetPage page;
   private Composite teamComp;
   private static Set<IAtsWizardItem> wizardExtensionItems = new HashSet<>();
   private final Set<IAtsWizardItem> handledExtensionItems = new HashSet<>();
   private IManagedForm form;
   private Section section;
   private Composite comp;
   private XWidgetPage widgetPage;

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
      boolean valid = true;

      String title = variableMap.getString(TITLE);
      if (Strings.isInValid(title)) {
         log("Enter Title");
         valid = false;
      }

      String desc = variableMap.getString(DESCRIPTION);
      if (Strings.isInValid(desc)) {
         log("Enter Description");
         valid = false;
      }

      ChangeTypes cType = (ChangeTypes) variableMap.getValue(CHANGE_TYPE);
      if (cType == null || cType == ChangeTypes.None) {
         log("Invalid Change Type");
      }

      String priority = variableMap.getString(PRIORITY);
      if (Strings.isInValid(priority)) {
         log("Select Priority");
         valid = false;
      }
      Date needBy = (Date) variableMap.getValue(NEED_BY);

      Collection<IAtsActionableItem> actionableItems = aiWidget.getSelectedActionableItems();
      if (actionableItems.isEmpty()) {
         valid = false;
         log("Must select Actionable Item(s)");
      }

      if (!isValidEntry() || !valid) {
         return;
      }

      IAtsChangeSet changes = atsApi.createChangeSet(getName());
      actionResult = atsApi.getActionService().createAction(atsApi.getUserService().getCurrentUser(), title, desc,
         cType, priority, false, needBy, actionableItems, new Date(), atsApi.getUserService().getCurrentUser(),
         Collections.singleton(this), changes);

      if (actionResult.getResults().isErrors()) {
         XResultDataUI.report(actionResult.getResults(), getTitle());
         return;
      }

      for (IAtsWizardItem wizardItem : handledExtensionItems) {
         wizardItem.wizardCompleted(actionResult, changes);
      }

      changes.execute();
      if (actionResult.getResults().isErrors()) {
         log(actionResult.getResults().toString());
         return;
      }
      Collection<IAtsTeamWorkflow> teamWfs = actionResult.getTeamWfs();
      if (teamWfs.size() == 1) {
         WorkflowEditor.edit(teamWfs.iterator().next());
      } else {
         WorldEditor.open(new WorldEditorSimpleProvider("New Action Workflows", AtsObjects.getArtifacts(teamWfs)));
      }
   }

   @Override
   public List<XWidgetRendererItem> getXWidgetItems() {
      wb = new XWidgetBuilder();
      wb.andXText(TITLE).andRequired().endWidget();
      wb.andXHyperlinkActionableItemActive().andRequired().endWidget();
      wb.andXText(AtsAttributeTypes.Description).andHeight(80).andRequired().endWidget();
      addWidgetsAfterDescription(wb);
      wb.andChangeType(ChangeTypes.DEFAULT_CHANGE_TYPES).andRequired().endWidget();
      wb.andXHyperLinkEnumAttr(getPriorityAttr()).andRequired().endWidget();
      addWidgetAfterPriority();
      wb.andXHyperLinkDate(AtsAttributeTypes.NeedBy.getUnqualifiedName()).endComposite().endWidget();
      return wb.getItems();
   }

   protected void addWidgetsAfterDescription(XWidgetBuilder wb) {
      // for extensibility
   }

   /**
    * Create widgets for specific teams
    */
   @Override
   public void createWidgets(Composite comp, IManagedForm form, Section section, XWidgetPage widgetPage) {
      this.comp = comp;
      this.form = form;
      this.section = section;
      this.widgetPage = widgetPage;
      teamComp = new Composite(comp, SWT.NONE);
      teamComp.setLayout(new GridLayout(1, false));
      teamComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
   }

   private void updateTeamComposites() {
      try {
         teamComp.dispose();
         createWidgets(comp, form, section, widgetPage);
         handledExtensionItems.clear();

         getWizardXWidgetExtensions();

         List<XWidget> allWidgets = new ArrayList<>();

         // Add XWidgets declared via widget xml
         StringBuffer stringBuffer = new StringBuffer(500);
         stringBuffer.append("<WorkPage>");
         IDynamicWidgetLayoutListener dynamicWidgetLayoutListener = null;
         for (IAtsWizardItem item : wizardExtensionItems) {
            boolean hasWizardXWidgetExtensions = item.hasWizardXWidgetExtensions(aiWidget.getSelectedActionableItems());
            if (hasWizardXWidgetExtensions) {
               stringBuffer.append(
                  "<XWidget displayName=\"--- Extra fields for " + item.getName() + " ---\" xwidgetType=\"XLabel\" horizontalLabel=\"true\" toolTip=\"These fields are available for only the team workflow specified here.\"/>");
               try {
                  if (item instanceof IDynamicWidgetLayoutListener) {
                     dynamicWidgetLayoutListener = (IDynamicWidgetLayoutListener) item;
                  }
                  item.getWizardXWidgetExtensions(aiWidget.getSelectedActionableItems(), stringBuffer);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
         stringBuffer.append("</WorkPage>");
         page = new XWidgetPage(stringBuffer.toString(), ATSXWidgetOptionResolver.getInstance(),
            dynamicWidgetLayoutListener);
         page.createBody(null, teamComp, null, null, true);
         allWidgets.addAll(page.getDynamicXWidgetLayout().getXWidgets());

         // Add XWidgets directly added to composite
         for (IAtsWizardItem item : wizardExtensionItems) {
            if (!handledExtensionItems.contains(item)) {
               boolean hasWizardXWidgetExtensions =
                  item.hasWizardXWidgetExtensions(aiWidget.getSelectedActionableItems());
               if (hasWizardXWidgetExtensions) {
                  item.getWizardXWidgetExtensions(aiWidget.getSelectedActionableItems(), teamComp);
                  handledExtensionItems.add(item);
               }
            }
         }

         teamComp.getParent().layout(true, true);
         teamComp.getParent().getParent().layout(true, true);
         form.reflow(true);
         section.layout(true, true);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, xModListener, isEditable);
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
         changeWidget = (XHyperlinkChangeTypeSelection) xWidget;
         setChangeTypeWidget(changeWidget);
      } else if (xWidget.getLabel().equals(PRIORITY)) {
         priorityWidget = (XHyperlinkWfdForEnumAttr) xWidget;
      } else if (xWidget instanceof XHyperlabelActionableItemSelection) {
         aiWidget = (XHyperlabelActionableItemSelection) xWidget;
         aiWidget.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               updateTeamComposites();
            }
         });
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
         for (IAtsActionableItem ai : atsApi.getConfigService().getConfigurations().getIdToAi().values()) {
            if (ai.getName().equals("ATS")) {
               ais.add(ai);
            }
            if (ai.getName().equals("SAW Requirements")) {
               ais.add(ai);
            }
         }
         aiWidget.setSelectedAIs(ais);
         titleWidget.set(title);
         descWidget.set("Description...");
         Collection<ChangeTypes> cTypes = setChangeTypeWidget(changeWidget);
         changeWidget.setSelected(cTypes.iterator().next().name());
         priorityWidget.setSelected("1");
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @SuppressWarnings("deprecation")
   private Set<IAtsWizardItem> getWizardXWidgetExtensions() {
      if (!wizardExtensionItems.isEmpty()) {
         return wizardExtensionItems;
      }

      IExtensionPoint point =
         Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.ide.AtsWizardItem");
      if (point == null) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Can't access AtsWizardItem extension point");
         return wizardExtensionItems;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("AtsWizardItem")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class<?> taskClass = bundle.loadClass(classname);
                     Object obj = taskClass.newInstance();
                     wizardExtensionItems.add((IAtsWizardItem) obj);
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error loading AtsWizardItem extension", ex);
                  }
               }

            }
         }
      }
      return wizardExtensionItems;
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

   public ActionResult getActionResult() {
      return actionResult;
   }

   protected void addWidgetAfterPriority() {
      // For sub-class extension
   }

   protected int getChangeTypeRowColumns() {
      return 6;
   }

   protected AttributeTypeToken getPriorityAttr() {
      return AtsAttributeTypes.Priority;
   }

}