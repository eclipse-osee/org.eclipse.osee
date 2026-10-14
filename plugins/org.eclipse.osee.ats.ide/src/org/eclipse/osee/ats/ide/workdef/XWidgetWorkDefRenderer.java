/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.workdef;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.hooks.IAtsWorkItemHookIde;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetService;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRendererListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.WidgetDebugProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Instantiation of a XWidgetPage to provide for automatic creation and management of the XWidgets for WorkDefinitions
 *
 * @author Donald G. Dunne
 */
public class XWidgetWorkDefRenderer implements XWidgetSwtRendererListener, WidgetDebugProvider {

   protected XWidgetSwtRenderer xWidgetSwtRenderer;
   protected final WorkDefinition workDefinition;
   private final AbstractWorkflowArtifact awa;
   private final Collection<LayoutItem> layoutItems;
   private final StateDefinition stateDefinition;

   public XWidgetWorkDefRenderer(IAtsWorkItem workItem, WorkDefinition workDefinition, StateDefinition stateDefinition, Collection<LayoutItem> layoutItems) {
      this.workDefinition = workDefinition;
      this.stateDefinition = stateDefinition;
      this.layoutItems = layoutItems;
      this.awa = (AbstractWorkflowArtifact) workItem;
      xWidgetSwtRenderer = new XWidgetSwtRenderer(this);
   }

   public void createBody(IManagedForm managedForm, Composite parent, Artifact artifact, XModifiedListener xModListener,
      boolean isEditable) {
      generateXWidgetDatas();
      xWidgetSwtRenderer.createBody(managedForm, parent, artifact, xModListener, isEditable);
   }

   private void generateXWidgetDatas() {
      generateLayoutDatas(awa, layoutItems);
   }

   private void generateLayoutDatas(AbstractWorkflowArtifact sma, Collection<LayoutItem> layoutItems) {
      // Add static XWidgetDatas to statePage
      for (LayoutItem layoutItem : layoutItems) {

         // Process Attr Widget Definitions
         if (layoutItem instanceof AttrWidgetDef) {
            AttrWidgetDef widgetDef = (AttrWidgetDef) layoutItem;
            // Create xWidget from widgetDef and set the WidgetDef data into it and add to widgetDatas
            processAttrWidgetDefinition(widgetDef);
         }

         // Process Widget Definitions
         else if (layoutItem instanceof WidgetDef) {
            WidgetDef widgetDef = (WidgetDef) layoutItem;
            // Create xWidget from widgetDef and set the WidgetDef data into it and add to widgetDatas
            processWidgetDefinition(widgetDef);
         }

         // Process Composites
         else if (layoutItem instanceof CompositeLayoutItem) {
            CompositeLayoutItem compLayout = (CompositeLayoutItem) layoutItem;
            processComposite(compLayout);
         }

      }
   }

   private void processComposite(CompositeLayoutItem compositeLayoutItem) {
      boolean firstWidget = true;

      // Group Comp is stand-alone renderer item
      boolean inGroupComp = false;
      if (compositeLayoutItem.isGroupComposite()) {
         inGroupComp = true;
         XWidgetData widData = new XWidgetData();
         widData.setLabel(compositeLayoutItem.getName());
         widData.setBeginGroupComposite(compositeLayoutItem.getNumColumns());
         xWidgetSwtRenderer.addXWidgetData(widData);
         widData.setWidgetId(WidgetId.GROUP_NAMED_COMPOSITE);
      }

      List<LayoutItem> layoutItems = compositeLayoutItem.getLayoutItems();
      for (int x = 0; x < layoutItems.size(); x++) {
         boolean lastWidget = x == layoutItems.size() - 1;
         LayoutItem layoutItem = layoutItems.get(x);

         // Process Widget Definitions
         if (layoutItem instanceof WidgetDef || layoutItem instanceof AttrWidgetDef) {
            // Create xWidget from widgetDef and set the WidgetDef data into it and add to widgetDatas
            XWidgetData widData = null;
            if (layoutItem instanceof AttrWidgetDef) {
               widData = processAttrWidgetDefinition((AttrWidgetDef) layoutItem);
            } else {
               widData = processWidgetDefinition((WidgetDef) layoutItem);
            }
            if (firstWidget) {
               if (compositeLayoutItem.getNumColumns() > 0) {
                  if (!compositeLayoutItem.isGroupComposite()) {
                     widData.setBeginComposite(compositeLayoutItem.getNumColumns());
                  }
               }
            }
            if (lastWidget) {
               widData.setEndComposite(true);
            }
         }
         // Process Composite
         else if (layoutItem instanceof CompositeLayoutItem) {
            CompositeLayoutItem compLayoutItem = (CompositeLayoutItem) layoutItem;
            processComposite(compLayoutItem);
         }
         firstWidget = false;
      }

      if (inGroupComp) {
         XWidgetData widData = new XWidgetData();
         widData.setEndGroupComposite(true);
         xWidgetSwtRenderer.addXWidgetData(widData);
      }

   }

   private XWidgetData processAttrWidgetDefinition(AttrWidgetDef widgetDef) {
      // NOTE: Here, we have to merge 3 widgetDefs; AttrXWidgetProvider, XWidget and WidgetDef

      AttributeTypeToken attrType = widgetDef.getAttributeType();

      // Get WidgetId and XOptions from Attr Defaults
      XWidgetData attrWidData = XWidgetService.get().getDefaultAttrXWidgetData(awa.getArtifactType(), attrType);
      attrWidData.setAttributeType(attrType);
      attrWidData.setToolTip(attrType.getDescription());

      if (widgetDef.isNot(XOption.NO_CLEAR)) {
         attrWidData.set(XOption.CLEAR);
      }

      WidgetId widgetId = attrWidData.getWidgetId();
      Conditions.assertTrue(widgetId.isValid(), "(1) - WidgetId can not be null here for [%s]", widgetDef.getName());

      // Instantiate the desired widget at the beginning of the Widget Definition, so only overridden options can be applied.
      XWidget xWidget = XWidgetService.get().createXWidget(widgetId);
      XWidgetData constructorWidData = xWidget.getWidData();

      // Overwrite xWidget's WidgetData with Attribute WidgetData
      xWidget.internalSetWidData(attrWidData);

      // Add VAL_ if attr/workdef didn't specify
      if (!xWidget.getWidData().getXOptionHandler().hasValOption()) {
         XOption valOption = constructorWidData.getXOptionHandler().getValOption();
         if (valOption != null) {
            xWidget.getWidData().set(valOption);
         }
      }

      // Take Attr widDef from xWidget and overlay WidgetDef options on top
      XWidgetData mergedWidData = mergeXWidgetWithWidgetDef(widgetDef, xWidget);

      return mergedWidData;

   }

   private XWidgetData processWidgetDefinition(WidgetDef widgetDef) {

      /**
       * Architecture of WorkDefs and XWidgets: <br>
       * XWidgets have their own hierarchy of capabilities and settings. Widget Definitions should NOT try to recreate
       * that hierarchy, but instead only override settings when necessary. <br>
       * <br>
       * Example: XOptions should be set correctly for the default behavior of the chosen widget. If different features
       * are needed, it's possible that a different widget should be chosen. So, the XWidgetOptions will be cleared for
       * Widget Definitions and only the additional options will be added to the default XWidget options. <br>
       * <br>
       * So: Once the XWidget is instantiated with it's default XOpions (and other defaults). That XWidgetData is taken
       * as the base. Then any additional settings from the WidgetDefinion's XWidgetData will be applied. <br>
       * <br>
       * NOTE: Additional XWidgets should NOT need to be created. Default should be able to be used and XOptions
       * overridden as necessary to provide the desired result.
       */

      // Get desired widget to use, this MUST be specified in WidgetDefinition
      WidgetId widgetId = widgetDef.getWidData().getWidgetId();
      Conditions.assertTrue(widgetId.isValid(), "(2) - WidgetId can not be null here for [%s]", widgetDef.getName());

      // Instantiate the desired widget at the beginning of the Widget Definition, so only overridden options can be applied.
      XWidget xWidget = XWidgetService.get().createXWidget(widgetId);

      // Take default widDef from xWidget and overlay WidgetDef options on top
      XWidgetData mergedWidData = mergeXWidgetWithWidgetDef(widgetDef, xWidget);

      return mergedWidData;
   }

   private XWidgetData mergeXWidgetWithWidgetDef(WidgetDef widgetDef, XWidget xWidget) {
      // At this point xWidget has the XWidgetDef defaults from contructor.  This is taken as base.
      XWidgetData widgetWidData = xWidget.getWidData();

      // Note: Default WidgetDefinition.XWidgetData.XOptions are cleared in WidgetDefinition instantiation

      // Store off widget so don't re-create when layout happens, now it just needs to be laid out after everything is resolved.
      // Future: Might make sense to create the final layout / widgets as we process the Workflow Definition which would make debugging easier.
      widgetWidData.setXWidget(xWidget);
      if (xWidget instanceof XXAbstractBaseXWidget<?>) {
         XXAbstractBaseXWidget<?> abstractBaseXWidget = (XXAbstractBaseXWidget<?>) xWidget;
         abstractBaseXWidget.setOrigWidData(widgetWidData);
      }

      // Now, get the XWidgetData from the WidgetDefinition
      XWidgetData workDefWidData = widgetDef.getWidData();
      if (xWidget instanceof XXAbstractBaseXWidget<?>) {
         XXAbstractBaseXWidget<?> abstractBaseXWidget = (XXAbstractBaseXWidget<?>) xWidget;
         abstractBaseXWidget.setIncomingWidData(workDefWidData);
         abstractBaseXWidget.setDebugProvider(this);
      }

      XWidgetData mergedWidData = widgetWidData.clone();

      if (awa != null) {
         mergedWidData.setArtifact(awa);
      }

      // Copy name back to the WidgetDef if it came from the XWidget constructor; Helps debugging
      if (Strings.isInvalid(widgetDef.getName()) && Strings.isValid(xWidget.getLabel())) {
         widgetDef.setName(xWidget.getLabel());
      }
      // Set WidgetDef for debugging purposes
      mergedWidData.setObject(widgetDef);

      // For whatever is set in WidgetDefinition, override the base
      try {
         if (workDefWidData.getId() != null) {
            mergedWidData.setId(workDefWidData.getId());
         }
         if (Strings.isValid(workDefWidData.getLabel())) {
            mergedWidData.setLabel(workDefWidData.getLabel());
         }
         if (workDefWidData.getDefaultValue() != null) {
            mergedWidData.setDefaultValue(workDefWidData.getDefaultValue());
         }
         if (Strings.isValid(workDefWidData.getToolTip())) {
            mergedWidData.setToolTip(workDefWidData.getToolTip());
         }
         if (widgetDef.getAttributeType().isValid()) {
            mergedWidData.setAttributeType(workDefWidData.getAttributeType());

            // Use Attr Type name if not already specified in XWidget
            if (mergedWidData.getLabel().isEmpty()) {
               mergedWidData.setLabel(mergedWidData.getAttributeType().getUnqualifiedName());
            }

         }
         if (widgetDef.getAttributeType2().isValid()) {
            mergedWidData.setAttributeType2(widgetDef.getAttributeType2());
         }
         if (widgetDef.getUserGroup().isValid()) {
            mergedWidData.setUserGroup(widgetDef.getUserGroup());
         }
         if (widgetDef.getOseeImage() != null) {
            mergedWidData.setOseeImage(widgetDef.getOseeImage());
         }
         if (widgetDef.getEnumeratedArt().isValid()) {
            mergedWidData.setEnumeratedArt(widgetDef.getEnumeratedArt());
         }
         // Carry the relation from the WidgetDef (e.g. new WidgetDef("Related Tasks", TeamWfToTask_Task, ...)).
         // Relation-backed widgets like XXListRelationWidget need this to resolve/display the related artifacts;
         // without it getRelationTypeSide() is SENTINEL and the widget renders empty.
         if (workDefWidData.getRelationTypeSide() != null && workDefWidData.getRelationTypeSide().isValid()) {
            mergedWidData.setRelationTypeSide(workDefWidData.getRelationTypeSide());
         }
         // Carry conditions (e.g. AttrWidgetDef.andCondition(new EnableIfAttrValueCondition(...))) so validate()
         // can enable/disable the widget. Without this the WFE drops them (ArtEd clone() carries them).
         if (!workDefWidData.getConditions().isEmpty()) {
            mergedWidData.getConditions().addAll(workDefWidData.getConditions());
         }
         for (XOption widgetOpt : widgetDef.getOptions()) {
            mergedWidData.set(widgetOpt);
         }
         mergedWidData.getxModListeners().addAll(workDefWidData.getxModListeners());
         mergedWidData.getSwtValueListeners().addAll(workDefWidData.getSwtValueListeners());
         mergedWidData.getParameters().putAll(workDefWidData.getParameters());

         // Add to render to be rendered after all pre-processing is done
         xWidgetSwtRenderer.addXWidgetData(mergedWidData);

      } catch (Exception ex) {

         // Gracefully handle the exception by creating a default label widget with the error
         widgetWidData = new XWidgetData();
         widgetWidData.setId(Lib.generateId());
         widgetWidData.setWidgetId(WidgetId.XXLabelWidget);
         widgetWidData.setLabel("Error: " + widgetDef.getName() + " (double-click to view error)");
         widgetWidData.setToolTip("Double-click to see error.");
         widgetWidData.setDoubleClickText(Lib.exceptionToString(ex));
         OseeLog.logf(XWidgetWorkDefRenderer.class, Level.SEVERE, ex, "Exception processing widget [%s]",
            widgetDef.getName());
         widgetWidData.setObject(widgetDef);

         // Add to render to be rendered after all pre-processing is done
         xWidgetSwtRenderer.addXWidgetData(widgetWidData);
         return widgetWidData;

      }

      // set merged back to XWidget
      xWidget.internalSetWidData(mergedWidData);
      return mergedWidData;
   }

   public String getHtml(String backgroundColor, String preHtml, String postHtml) {
      return getHtml(backgroundColor, preHtml, postHtml, "");
   }

   public String getHtml(String backgroundColor, String preHtml, String postHtml, String name) {
      StringBuffer sb = new StringBuffer();
      int defaultWidth = 100;
      sb.append(AHTML.startBorderTable(defaultWidth, backgroundColor, name));
      if (preHtml != null) {
         sb.append(preHtml);
      }
      for (XWidget widget : xWidgetSwtRenderer.getXWidgets()) {
         sb.append(widget.toHTML(AHTML.LABEL_FONT));
         sb.append(AHTML.newline());
         if (postHtml != null) {
            sb.append(postHtml);
         }
      }
      sb.append(AHTML.endBorderTable());
      return sb.toString();
   }

   @Override
   public XResultData getWidgetDebug(XXAbstractBaseXWidget<?> abstractBaseXWidget, XResultData rd) {
      rd.logf("Work Def: %s\n", workDefinition.toStringWithId());
      rd.log(XWidgetUtility.debugWidDataToHtml(abstractBaseXWidget.getLabel(), "XWidget Constructor",
         abstractBaseXWidget.getOrigWidData(), "Work Def", abstractBaseXWidget.getIncomingWidData(),
         abstractBaseXWidget.getWidData()).toString());
      return rd;
   }

   public Collection<? extends XWidget> getXWidgets() {
      return xWidgetSwtRenderer.getXWidgets();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      if (art != null) {
         for (IAtsWorkItemHookIde item : AtsApiService.get().getWorkItemServiceIde().getWorkItemHooksIde()) {
            item.xWidgetCreated(xWidget, toolkit, stateDefinition, art, isEditable);
         }
      }
   }

   @Override
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener xModListener, boolean isEditable) {
      if (art != null) {
         for (IAtsWorkItemHookIde item : AtsApiService.get().getWorkItemServiceIde().getWorkItemHooksIde()) {
            Result result = item.xWidgetCreating(xWidget, toolkit, stateDefinition, art, isEditable);
            if (result.isFalse()) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error in page creation => " + result.getText());
            }
         }
      }
   }

}
