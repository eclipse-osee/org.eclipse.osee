/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeTypeWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.LabelAfterWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XOption;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Jeff C. Phillips
 */
public class SwtXWidgetRenderer {
   public static final String XWIDGET = "XWidget";

   private final Set<XWidgetRendererItem> datas = new LinkedHashSet<>();
   private final Map<String, XWidgetRendererItem> nameToLayoutData = new HashMap<>();

   private final Collection<ArrayList<String>> orRequired = new ArrayList<>();
   private final Collection<ArrayList<String>> xorRequired = new ArrayList<>();

   private final IDynamicWidgetLayoutListener dynamicWidgetLayoutListener;
   private final IXWidgetOptionResolver optionResolver;
   private final Collection<XWidget> xWidgets = new ArrayList<>();

   public SwtXWidgetRenderer() {
      this(null, new DefaultXWidgetOptionResolver());
   }

   public SwtXWidgetRenderer(IDynamicWidgetLayoutListener dynamicWidgetLayoutListener, IXWidgetOptionResolver optionResolver) {
      this.dynamicWidgetLayoutListener = dynamicWidgetLayoutListener;
      this.optionResolver = optionResolver;
   }

   private Composite createComposite(Composite parent, FormToolkit toolkit) {
      return createComposite(parent, toolkit, false);
   }

   private Composite createComposite(Composite parent, FormToolkit toolkit, boolean border) {
      if (border) {
         return toolkit != null ? toolkit.createComposite(parent, SWT.WRAP | SWT.BORDER) : new Composite(parent,
            SWT.NONE | SWT.BORDER);
      }
      return toolkit != null ? toolkit.createComposite(parent, SWT.WRAP) : new Composite(parent, SWT.NONE);
   }

   private Group buildGroupComposite(Composite parent, String name, int numColumns, FormToolkit toolkit) {
      Group groupComp = new Group(parent, SWT.None);
      if (Strings.isValid(name)) {
         groupComp.setText(name);
      }
      groupComp.setLayout(new GridLayout(numColumns, false));
      groupComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      groupComp.setFont(FontManager.getCourierNew12Bold());
      if (toolkit != null) {
         toolkit.adapt(groupComp);
      }
      return groupComp;
   }

   private Composite buildChildComposite(Composite parent, int numColumns, FormToolkit toolkit, boolean border) {
      Composite outComp = createComposite(parent, toolkit, border);
      GridLayout zeroMarginLayout = ALayout.getZeroMarginLayout(numColumns, false);
      zeroMarginLayout.marginWidth = 4;
      zeroMarginLayout.horizontalSpacing = 8;
      outComp.setLayout(zeroMarginLayout);
      outComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      if (toolkit != null) {
         toolkit.adapt(outComp);
      }
      return outComp;
   }

   protected XWidget setupXWidget(XWidgetRendererItem xWidgetRenderItem, boolean isEditable) {
      XWidget xWidget = xWidgetRenderItem.getXWidget();
      xWidgets.add(xWidget);

      if (Strings.isValid(xWidgetRenderItem.getName())) {
         setName(xWidget, xWidgetRenderItem.getName());
      } else if (Strings.isValid(xWidget.getLabel())) {
         setName(xWidget, xWidget.getLabel());
      }

      if (Strings.isValid(xWidgetRenderItem.getToolTip())) {
         xWidget.setToolTip(xWidgetRenderItem.getToolTip());
      }

      xWidget.setRequiredEntry(xWidgetRenderItem.isRequired());
      xWidget.setEditable(xWidgetRenderItem.getXOptionHandler().contains(XOption.EDITABLE) && isEditable);
      xWidget.setNoSelect(xWidgetRenderItem.getXOptionHandler().contains(XOption.NO_SELECT));
      xWidget.setSingleSelect(xWidgetRenderItem.getXOptionHandler().contains(XOption.SINGLE_SELECT));
      xWidget.setMultiSelect(xWidgetRenderItem.getXOptionHandler().contains(XOption.MULTI_SELECT));
      xWidget.setAutoSave(xWidgetRenderItem.getXOptionHandler().contains(XOption.AUTO_SAVE));
      xWidget.setFillHorizontally(xWidgetRenderItem.getXOptionHandler().contains(XOption.FILL_HORIZONTALLY));
      xWidget.setValidateDate(xWidgetRenderItem.getXOptionHandler().contains(XOption.VALIDATE_DATE));
      xWidget.setFillVertically(xWidgetRenderItem.getXOptionHandler().contains(XOption.FILL_VERTICALLY));
      if (xWidget instanceof LabelAfterWidget) {
         ((LabelAfterWidget) xWidget).setLabelAfter(
            xWidgetRenderItem.getXOptionHandler().contains(XOption.LABEL_AFTER));
      }
      if (xWidgetRenderItem.getDefaultValueObj() != null) {
         xWidget.setDefaultValueObj(xWidgetRenderItem.getDefaultValueObj());
      }
      xWidget.setValueProvider(xWidgetRenderItem.getValueProvider());

      xWidget.setArtifactType(xWidgetRenderItem.getArtifactType());
      xWidget.setAttributeType(xWidgetRenderItem.getAttributeType());
      xWidget.setTeamId(xWidgetRenderItem.getTeamId());
      xWidget.setValues(xWidgetRenderItem.getValues());
      xWidget.setConditions(xWidgetRenderItem.getConditions());

      return xWidget;
   }

   protected void setName(XWidget xWidget, String name) {
      if (Strings.isValid(name)) {
         xWidget.setLabel(name);
      }
   }

   public void createBody(IManagedForm managedForm, Composite parent, Artifact artifact, XModifiedListener xModListener,
      boolean isEditable) {
      final FormToolkit toolkit = managedForm != null ? managedForm.getToolkit() : null;

      Composite topLevelComp = createComposite(parent, toolkit);
      GridLayout layout = new GridLayout();
      layout.marginWidth = 2;
      layout.marginHeight = 2;
      topLevelComp.setLayout(layout);
      topLevelComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      if (toolkit != null) {
         toolkit.adapt(topLevelComp);
      }

      boolean inChildComposite = false;
      boolean inGroupComposite = false;
      Composite childComp = null;
      Group groupComp = null;
      // Create Attributes
      Set<XWidgetRendererItem> layoutDatas = getLayoutDatas();
      for (XWidgetRendererItem rendererItem : layoutDatas) {
         Composite currentComp = null;

         // first, check if this one is a group, if so, we set the group up and are done with this loop iteration

         int i = rendererItem.getBeginGroupComposite();
         if (i > 0) {
            inGroupComposite = true;
            groupComp = buildGroupComposite(topLevelComp, rendererItem.getName(), i, toolkit);
            continue;
         }
         if (inGroupComposite) {
            currentComp = groupComp;
            if (rendererItem.isEndGroupComposite()) {
               inGroupComposite = false;
               currentComp = topLevelComp;
               // No XWidget associated, so go to next one
               continue;
            }
         } else {
            currentComp = topLevelComp;
         }

         // defaults to grab horizontal, causes scrollbars on items that extend past the provided window space
         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
         currentComp.setLayoutData(gd);

         if (rendererItem.getXOptionHandler().contains(XOption.FILL_VERTICALLY)) {
            gd.grabExcessVerticalSpace = true;
         }

         int columns = rendererItem.getBeginComposite();
         boolean border = rendererItem.isBorder();
         if (columns > 0) {
            inChildComposite = true;
            childComp = buildChildComposite(currentComp, columns, toolkit, border);
         }

         if (inChildComposite) {
            currentComp = childComp;
            if (rendererItem.isEndComposite()) {
               inChildComposite = false;
            }
         } else if (rendererItem.getXOptionHandler().contains(XOption.HORIZONTAL_LABEL)) {
            currentComp = createComposite(topLevelComp, toolkit);
            currentComp.setLayout(ALayout.getZeroMarginLayout(2, false));
            currentComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (toolkit != null) {
               toolkit.adapt(currentComp);
            }
         }

         XWidget xWidget = setupXWidget(rendererItem, isEditable);
         xWidget.setId(rendererItem.getId());
         if (rendererItem.getObject() != null) {
            xWidget.setObject(rendererItem.getObject());
         }
         xWidget.getWidgetHints().addAll(rendererItem.getWidgetHints());

         if (dynamicWidgetLayoutListener != null) {
            dynamicWidgetLayoutListener.widgetCreating(xWidget, toolkit, artifact, this, xModListener, isEditable);
         }

         setupArtifactInfo(artifact, rendererItem, xWidget);

         if (xWidget instanceof XText) {
            XText xText = (XText) xWidget;
            if (rendererItem.getXOptionHandler().contains(XOption.FILL_HORIZONTALLY)) {
               xText.setFillHorizontally(true);
            }
            if (rendererItem.getXOptionHandler().contains(XOption.FILL_VERTICALLY)) {
               xText.setFillVertically(true);
            }
            if (rendererItem.isHeightSet()) {
               xText.setHeight(rendererItem.getHeight());
            }
            xText.setDynamicallyCreated(true);
         }

         xWidget.createWidgets(managedForm, currentComp, 2);
         setAttrToolTip(xWidget, rendererItem);

         if (xModListener != null) {
            xWidget.addXModifiedListener(xModListener);
         }
         xWidget.addXModifiedListener(refreshRequiredModListener);

         if (Strings.isValid(rendererItem.getDoubleClickText())) {
            if (Widgets.isAccessible(xWidget.getLabelWidget())) {
               xWidget.getLabelWidget().addMouseListener(new MouseAdapter() {
                  @Override
                  public void mouseDoubleClick(MouseEvent e) {
                     super.mouseDoubleClick(e);
                     ResultsEditor.open("Error", "Error: " + xWidget.getLabel(),
                        AHTML.simplePage(rendererItem.getDoubleClickText()));
                  }
               });
            }
         }

         if (dynamicWidgetLayoutListener != null) {
            dynamicWidgetLayoutListener.widgetCreated(xWidget, toolkit, artifact, this, xModListener, isEditable);
            dynamicWidgetLayoutListener.createXWidgetLayoutData(rendererItem, xWidget, toolkit, artifact, xModListener,
               isEditable);
         }
      }
      topLevelComp.layout();

      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            try {
               if (managedForm != null) {
                  managedForm.getMessageManager().setAutoUpdate(false);
               }
               for (XWidgetRendererItem xWidgetLayoutData : getLayoutDatas()) {
                  xWidgetLayoutData.getXWidget().validate();
               }
               refreshOrAndXOrRequiredFlags();
               if (managedForm != null) {
                  try {
                     managedForm.getMessageManager().setAutoUpdate(true);
                  }
                  // Bug in MessageManager causes NPE that we do not care about
                  catch (NullPointerException ex) {
                     // do nothing
                  }
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   protected void setAttrToolTip(XWidget xWidget, XWidgetRendererItem layoutData) {
      String description = "";
      if (AttributeTypeManager.typeExists(layoutData.getStoreName())) {
         try {
            AttributeTypeToken type = null;
            if (layoutData.getStoreId() > 0) {
               type = AttributeTypeManager.getAttributeType(layoutData.getStoreId());
            }
            if (type == null && Strings.isValid(layoutData.getStoreName())) {
               try {
                  type = AttributeTypeManager.getType(layoutData.getStoreName());
               } catch (ItemDoesNotExist ex) {
                  // do nothing
               }
            }
            if (type != null && Strings.isValid(type.getDescription())) {
               description = type.getDescription();
            }
            if (Strings.isValid(description)) {
               xWidget.setToolTip(description);
               layoutData.setToolTip(description);
            }
         } catch (Exception ex) {
            String msg = String.format("Error setting tooltip for widget [%s].  Error %s (see log for details)",
               xWidget.getLabel(), ex.getLocalizedMessage());
            OseeLog.log(Activator.class, Level.SEVERE, msg, ex);
         }
      }
   }

   private void setupArtifactInfo(Artifact artifact, XWidgetRendererItem xWidgetLayoutData, XWidget xWidget) {
      if (artifact == null) {
         return;
      }
      if (xWidget instanceof AttributeWidget || xWidget instanceof AttributeTypeWidget) {
         AttributeTypeToken attributeType = null;
         if (xWidgetLayoutData.getStoreId() > 0) {
            attributeType = AttributeTypeManager.getAttributeType(xWidgetLayoutData.getStoreId());
         }
         if (attributeType == null && Strings.isValid(xWidgetLayoutData.getStoreName())) {
            attributeType = AttributeTypeManager.getType(xWidgetLayoutData.getStoreName());
         }
         try {
            if (xWidget instanceof AttributeWidget) {
               if (attributeType != null) {
                  ((AttributeWidget) xWidget).setAttributeType(artifact, attributeType);
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
         try {
            if (xWidget instanceof AttributeTypeWidget) {
               if (attributeType != null) {
                  ((AttributeTypeWidget) xWidget).setAttributeType(attributeType);
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      } else if (xWidget instanceof ArtifactWidget) {
         try {
            ((ArtifactWidget) xWidget).setArtifact(artifact);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }
   private final XModifiedListener refreshRequiredModListener = new XModifiedListener() {
      @Override
      public void widgetModified(XWidget widget) {
         try {
            refreshOrAndXOrRequiredFlags();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   };

   /**
    * Required flags are set per XWidget and the labels change from Red to Black when the widget has been edited
    * successfully. When a page is made up of two or more widgets that need to work together, these required flags need
    * to be set/unset whenever a widget from the group gets modified.
    */
   private void refreshOrAndXOrRequiredFlags() {
      // Handle orRequired
      for (Collection<String> orReq : orRequired) {
         // If group is complete, change all to black, else all red
         boolean isComplete = isOrGroupFromAttrNameComplete(orReq.iterator().next());
         for (String aName : orReq) {
            XWidgetRendererItem layoutData = getLayoutData(aName);
            Label label = layoutData.getXWidget().getLabelWidget();
            if (label != null && !label.isDisposed()) {
               label.setForeground(isComplete ? null : Displays.getSystemColor(SWT.COLOR_RED));
            }
         }
      }
      // Handle xorRequired
      for (Collection<String> xorReq : xorRequired) {
         // If group is complete, change all to black, else all red
         boolean isComplete = isXOrGroupFromAttrNameComplete(xorReq.iterator().next());
         for (String aName : xorReq) {
            XWidgetRendererItem layoutData = getLayoutData(aName);
            Label label = layoutData.getXWidget().getLabelWidget();
            if (label != null && !label.isDisposed()) {
               label.setForeground(isComplete ? null : Displays.getSystemColor(SWT.COLOR_RED));
            }
         }
      }
   }

   public IStatus isPageComplete() {
      try {
         for (XWidgetRendererItem data : datas) {
            IStatus valid = data.getXWidget().isValid();
            if (!valid.isOK()) {
               // Check to see if widget is part of a completed OR or XOR group
               if (!isOrGroupFromAttrNameComplete(data.getStoreName()) && !isXOrGroupFromAttrNameComplete(
                  data.getStoreName())) {
                  return valid;
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return Status.OK_STATUS;
   }

   public Set<XWidgetRendererItem> getLayoutDatas() {
      return datas;
   }

   public void setLayoutDatas(List<XWidgetRendererItem> datas) {
      this.datas.clear();
      for (XWidgetRendererItem data : datas) {
         data.setDynamicXWidgetLayout(this);
         this.datas.add(data);
      }
   }

   public void addWorkLayoutDatas(List<XWidgetRendererItem> datas) {
      this.datas.addAll(datas);
   }

   public void addWorkLayoutData(XWidgetRendererItem data) {
      this.datas.add(data);
   }

   public XWidgetRendererItem getLayoutData(String displayName) {
      for (XWidgetRendererItem layoutData : datas) {
         if (layoutData.getName().equals(displayName)) {
            return layoutData;
         }
      }
      return null;
   }

   public boolean isOrRequired(String attrName) {
      return !getOrRequiredGroup(attrName).isEmpty();
   }

   public boolean isXOrRequired(String attrName) {
      return !getXOrRequiredGroup(attrName).isEmpty();
   }

   private Collection<String> getOrRequiredGroup(String attrName) {
      return getRequiredGroup(orRequired, attrName);
   }

   private Collection<String> getXOrRequiredGroup(String attrName) {
      return getRequiredGroup(xorRequired, attrName);
   }

   private Collection<String> getRequiredGroup(Collection<ArrayList<String>> requiredList, String attrName) {
      for (Collection<String> list : requiredList) {
         for (String aName : list) {
            if (aName.equals(attrName)) {
               return list;
            }
         }
      }
      return Collections.emptyList();
   }

   /**
    * @return true if ANY item in group is entered
    */
   public boolean isOrGroupFromAttrNameComplete(String name) {
      for (String aName : getOrRequiredGroup(name)) {
         XWidgetRendererItem layoutData = getLayoutData(aName);
         if (layoutData.getXWidget() != null && layoutData.getXWidget().isValid().isOK()) {
            return true;
         }
      }
      return false;
   }

   /**
    * @return true if only ONE item in group is entered
    */
   public boolean isXOrGroupFromAttrNameComplete(String attrName) {
      boolean oneFound = false;
      for (String aName : getXOrRequiredGroup(attrName)) {
         XWidgetRendererItem layoutData = getLayoutData(aName);
         if (layoutData.getXWidget() != null && layoutData.getXWidget().isValid().isOK()) {
            // If already found one, return false
            if (oneFound) {
               return false;
            } else {
               oneFound = true;
            }
         }
      }
      return oneFound;
   }

   protected void processOrRequired(String instr) {
      ArrayList<String> names = new ArrayList<>();
      for (String attr : instr.split(";")) {
         if (!attr.contains("[ \\s]*")) {
            names.add(attr);
         }
      }
      orRequired.add(names);
   }

   protected void processXOrRequired(String instr) {
      ArrayList<String> names = new ArrayList<>();
      for (String attr : instr.split(";")) {
         if (!attr.contains("[ \\s]*")) {
            names.add(attr);
         }
      }
      xorRequired.add(names);
   }

   public void processlayoutDatas(String xWidgetXml) {
      try {
         Document document = Jaxp.readXmlDocument(xWidgetXml);
         Element rootElement = document.getDocumentElement();

         List<XWidgetRendererItem> attrs = XWidgetParser.extractlayoutDatas(this, rootElement);
         for (XWidgetRendererItem attr : attrs) {
            nameToLayoutData.put(attr.getName(), attr);
            datas.add(attr);
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   public void processLayoutDatas(Element element) {
      List<XWidgetRendererItem> layoutDatas = XWidgetParser.extractlayoutDatas(this, element);
      for (XWidgetRendererItem layoutData : layoutDatas) {
         nameToLayoutData.put(layoutData.getName(), layoutData);
         datas.add(layoutData);
      }
   }

   public IXWidgetOptionResolver getOptionResolver() {
      return optionResolver;
   }

   public Collection<XWidget> getXWidgets() {
      return xWidgets;
   }

}
