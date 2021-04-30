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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XArtifactTypeComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public abstract class CreateNewChangeRequestBlam extends AbstractBlam implements INewActionListener {
   private static final String BLAM_DESCRIPTION =
      "Create program top level Demo Change Request for any new feature or problem found.\n" //
         + "This will mature into all the work for all teams needed to resolve this request.";
   protected static final String TITLE = "Title";
   protected static final String PROGRAM = "Program";
   protected final static String DESCRIPTION = "Description";
   protected static final String CHANGE_TYPE = "Change Type";
   protected static final String PRIORITY = "Priority";
   protected static final String NEED_BY = "Need By";
   protected XText titleWidget;
   protected XText descWidget;
   protected XCombo changeWidget;
   protected XCombo priorityWidget;
   protected final AtsApi atsApi;
   protected XWidgetBuilder wb;

   public CreateNewChangeRequestBlam(String name) {
      super(name, BLAM_DESCRIPTION, null);
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
      String programAi = variableMap.getString(PROGRAM);
      if (Strings.isInValid(programAi)) {
         log("Select Program");
         valid = false;
      }
      String desc = variableMap.getString(DESCRIPTION);
      if (Strings.isInValid(desc)) {
         log("Enter Description");
         valid = false;
      }
      String changeType = variableMap.getString(CHANGE_TYPE);
      ChangeType cType = null;
      if (Strings.isInValid(changeType) || XArtifactTypeComboViewer.SELECT_STR.equals(changeType)) {
         log("Select Change type");
         valid = false;
      } else {
         try {
            cType = (ChangeType.valueOf(changeType));
         } catch (Exception ex) {
            valid = false;
            log("Invalid Change Type");
         }
      }
      String priority = variableMap.getString(PRIORITY);
      if (Strings.isInValid(priority) || "--select--".equals(priority)) {
         log("Select Priority");
         valid = false;
      }
      Date needBy = (Date) variableMap.getValue(NEED_BY);

      if (!isValidEntry() || !valid) {
         return;
      }

      IAtsChangeSet changes = atsApi.createChangeSet(getName());
      IAtsActionableItem ai = null;
      for (IAtsActionableItem ai2 : getProgramCrAis()) {
         if (ai2.getName().equals(programAi)) {
            ai = ai2;
            break;
         }
      }

      ActionResult actionResult = atsApi.getActionService().createAction(atsApi.getUserService().getCurrentUser(),
         title, desc, cType, priority, false, needBy, Collections.singleton(ai), new Date(),
         atsApi.getUserService().getCurrentUser(), Collections.singleton(this), changes);
      changes.execute();
      if (actionResult.getResults().isErrors()) {
         log(actionResult.getResults().toString());
         return;
      }
      IAtsTeamWorkflow teamWf = actionResult.getFirstTeam();
      WorkflowEditor.edit(teamWf);
   }

   abstract public Collection<IAtsActionableItem> getProgramCrAis();

   @Override
   public List<XWidgetRendererItem> getXWidgetItems() {
      wb = new XWidgetBuilder();
      wb.andXCombo(PROGRAM, Collections.emptyList()).andRequired().endWidget();
      wb.andXText(TITLE).andRequired().endWidget();
      wb.andXText(AtsAttributeTypes.Description).andHeight(80).andRequired().endWidget();
      wb.andXCombo(AtsAttributeTypes.ChangeType).andComposite(getChangeTypeRowColumns()).andRequired().endWidget();
      wb.andXCombo(getPriorityAttr()).andRequired().endWidget();
      addWidgetAfterPriority();
      wb.andXDate(AtsAttributeTypes.NeedBy).endComposite().endWidget();
      return wb.getItems();
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

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, xModListener, isEditable);
      if (xWidget.getLabel().equals(TITLE)) {
         titleWidget = (XText) xWidget;
      } else if (xWidget.getLabel().equals(DESCRIPTION)) {
         descWidget = (XText) xWidget;
      } else if (xWidget.getLabel().equals(CHANGE_TYPE)) {
         changeWidget = (XCombo) xWidget;
      } else if (xWidget.getLabel().equals(PRIORITY)) {
         priorityWidget = (XCombo) xWidget;
      } else if (xWidget.getLabel().equals(PROGRAM)) {
         XCombo programCombo = (XCombo) xWidget;
         List<String> aiStrs = new ArrayList<String>();
         for (IAtsActionableItem ai : getProgramCrAis()) {
            aiStrs.add(ai.getName());
         }
         programCombo.setDataStrings(aiStrs);
         if (aiStrs.size() == 1) {
            programCombo.getComboBox().select(1);
         }
         programCombo.getLabelWidget().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent event) {
               if (event.button == 3) {
                  handlePopulateWithDebugInfo();
               }
            }

         });
      }
   }

   /**
    * Method is used to quickly create a unique title for debug purposes
    */
   protected void handlePopulateWithDebugInfo() {
      try {
         titleWidget.set("New CR " + atsApi.getRandomNum());
         descWidget.set("Description...");
         changeWidget.getComboBox().select(1);
         priorityWidget.getComboBox().select(1);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
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
   public boolean showInBlamSection() {
      return false;
   }

   @Override
   public Collection<String> getCategories() {
      return Collections.singleton("ATS");
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(AtsImage.CHANGE_REQUEST);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.CHANGE_REQUEST);
   }

}