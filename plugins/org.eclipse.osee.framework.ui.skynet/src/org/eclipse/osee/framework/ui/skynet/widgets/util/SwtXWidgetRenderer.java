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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
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
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeType2Widget;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeTypeWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.AttributeWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.LabelAfterWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonWithLabelDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBoxDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
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
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Jeff C. Phillips
 */
public class SwtXWidgetRenderer {
   public static final String XWIDGET = "XWidget";

   private final Set<XWidgetData> xWidgetDatas = new LinkedHashSet<>();
   private final Map<String, XWidgetData> nameToLayoutData = new HashMap<>();

   private final IDynamicWidgetLayoutListener dynamicWidgetLayoutListener;
   private final Collection<XWidget> xWidgets = new ArrayList<>();
   private final Map<XWidgetData, XWidget> widDataToXWidget = new HashMap<>();
   private static final FrameworkXWidgetProvider xWidgetFactory = FrameworkXWidgetProvider.getInstance();

   public SwtXWidgetRenderer() {
      this(null);
   }

   public SwtXWidgetRenderer(IDynamicWidgetLayoutListener dynamicWidgetLayoutListener) {
      this.dynamicWidgetLayoutListener = dynamicWidgetLayoutListener;
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

   public XWidget setupXWidget(XWidgetData widData, boolean isEditable) {
      XWidget xWidget = xWidgetFactory.createXWidget(widData, this);
      widDataToXWidget.put(widData, xWidget);

      xWidgets.add(xWidget);

      if (Strings.isValid(widData.getName())) {
         setName(xWidget, widData.getName());
      } else if (Strings.isValid(xWidget.getLabel())) {
         setName(xWidget, xWidget.getLabel());
      }

      if (Strings.isValid(widData.getToolTip())) {
         xWidget.setToolTip(widData.getToolTip());
      }

      xWidget.setRequiredEntry(widData.isRequired());
      xWidget.setEditable(widData.getXOptionHandler().contains(XOption.EDITABLE) && isEditable);
      xWidget.setNoSelect(widData.getXOptionHandler().contains(XOption.NO_SELECT));
      xWidget.setSingleSelect(widData.getXOptionHandler().contains(XOption.SINGLE_SELECT));
      xWidget.setMultiSelect(widData.getXOptionHandler().contains(XOption.MULTI_SELECT));
      xWidget.setAutoSave(widData.getXOptionHandler().contains(XOption.AUTO_SAVE));
      xWidget.setFillHorizontally(widData.getXOptionHandler().contains(XOption.FILL_HORIZONTALLY));
      xWidget.setValidateDate(widData.getXOptionHandler().contains(XOption.VALIDATE_DATE));
      xWidget.setFillVertically(widData.getXOptionHandler().contains(XOption.FILL_VERTICALLY));
      if (xWidget instanceof LabelAfterWidget) {
         ((LabelAfterWidget) xWidget).setLabelAfter(widData.getXOptionHandler().contains(XOption.LABEL_AFTER));
      }
      xWidget.setDefaultValueObj(widData.getDefaultValueObj());
      xWidget.setValueProvider(widData.getValueProvider());
      xWidget.setArtifactType(widData.getArtifactType());
      xWidget.setAttributeType(widData.getAttributeType());
      xWidget.setAttributeType2(widData.getAttributeType2());
      xWidget.setEnumeratedArt(widData.getEnumeratedArt());
      xWidget.setOseeImage(widData.getOseeImage());
      xWidget.setTeamId(widData.getTeamId());
      xWidget.setValues(widData.getValues());
      xWidget.setConditions(widData.getConditions());
      if (xWidget instanceof XButtonWithLabelDam) {
         ((XButtonWithLabelDam) xWidget).setUserGroup(widData.getUserGroup());
      }
      if (xWidget instanceof XCheckBoxDam) {
         ((XCheckBoxDam) xWidget).setUserGroup(widData.getUserGroup());
      }
      xWidget.getWidgetHints().addAll(widData.getWidgetHints());
      xWidget.getParameters().putAll(widData.getParameters());

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
      Set<XWidgetData> widDatas = getXWidgetDatas();
      for (XWidgetData widData : widDatas) {
         Composite currentComp = null;

         // first, check if this one is a group, if so, we set the group up and are done with this loop iteration

         int i = widData.getBeginGroupComposite();
         if (i > 0) {
            inGroupComposite = true;
            groupComp = buildGroupComposite(topLevelComp, widData.getName(), i, toolkit);
            continue;
         }
         if (inGroupComposite) {
            currentComp = groupComp;
            if (widData.isEndGroupComposite()) {
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

         if (widData.getXOptionHandler().contains(XOption.FILL_VERTICALLY)) {
            gd.grabExcessVerticalSpace = true;
         }

         int columns = widData.getBeginComposite();
         boolean border = widData.isBorder();
         if (columns > 0) {
            inChildComposite = true;
            childComp = buildChildComposite(currentComp, columns, toolkit, border);
         }

         if (inChildComposite) {
            currentComp = childComp;
            if (widData.isEndComposite()) {
               inChildComposite = false;
            }
         } else if (widData.getXOptionHandler().contains(XOption.HORIZONTAL_LABEL)) {
            currentComp = createComposite(topLevelComp, toolkit);
            currentComp.setLayout(ALayout.getZeroMarginLayout(2, false));
            currentComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (toolkit != null) {
               toolkit.adapt(currentComp);
            }
         }

         XWidget xWidget = setupXWidget(widData, isEditable);
         xWidget.setId(widData.getId());
         if (widData.getObject() != null) {
            xWidget.setObject(widData.getObject());
         }

         if (dynamicWidgetLayoutListener != null) {
            dynamicWidgetLayoutListener.widgetCreating(xWidget, toolkit, artifact, this, xModListener, isEditable);
         }

         setupArtifactInfo(artifact, widData, xWidget);

         if (xWidget instanceof XText) {
            XText xText = (XText) xWidget;
            if (widData.getXOptionHandler().contains(XOption.FILL_HORIZONTALLY)) {
               xText.setFillHorizontally(true);
            }
            if (widData.getXOptionHandler().contains(XOption.FILL_VERTICALLY)) {
               xText.setFillVertically(true);
            }
            if (widData.isHeightSet()) {
               xText.setHeight(widData.getHeight());
            }
            xText.setDynamicallyCreated(true);
         }

         xWidget.createWidgets(managedForm, currentComp, 2);
         setAttrToolTip(xWidget, widData);

         if (xModListener != null) {
            xWidget.addXModifiedListener(xModListener);
         }

         if (Strings.isValid(widData.getDoubleClickText())) {
            if (Widgets.isAccessible(xWidget.getLabelWidget())) {
               xWidget.getLabelWidget().addMouseListener(new MouseAdapter() {
                  @Override
                  public void mouseDoubleClick(MouseEvent e) {
                     super.mouseDoubleClick(e);
                     ResultsEditor.open("Error", "Error: " + xWidget.getLabel(),
                        AHTML.simplePage(widData.getDoubleClickText()));
                  }
               });
            }
         }

         if (dynamicWidgetLayoutListener != null) {
            dynamicWidgetLayoutListener.widgetCreated(xWidget, toolkit, artifact, this, xModListener, isEditable);
            dynamicWidgetLayoutListener.createXWidgetLayoutData(widData, xWidget, toolkit, artifact, xModListener,
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
               for (XWidgetData widData : getXWidgetDatas()) {
                  XWidget widget = widDataToXWidget.get(widData);
                  widget.validate();
               }
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

   protected void setAttrToolTip(XWidget xWidget, XWidgetData widData) {
      String description = "";
      if (AttributeTypeManager.typeExists(widData.getStoreName())) {
         try {
            AttributeTypeToken type = null;
            if (widData.getStoreId() > 0) {
               type = AttributeTypeManager.getAttributeType(widData.getStoreId());
            }
            if (type == null && Strings.isValid(widData.getStoreName())) {
               try {
                  type = AttributeTypeManager.getType(widData.getStoreName());
               } catch (ItemDoesNotExist ex) {
                  // do nothing
               }
            }
            if (type != null && Strings.isValid(type.getDescription())) {
               description = type.getDescription();
            }
            if (Strings.isValid(description)) {
               xWidget.setToolTip(description);
               widData.setToolTip(description);
            }
         } catch (Exception ex) {
            String msg = String.format("Error setting tooltip for widget [%s].  Error %s (see log for details)",
               xWidget.getLabel(), ex.getLocalizedMessage());
            OseeLog.log(Activator.class, Level.SEVERE, msg, ex);
         }
      }
   }

   private void setupArtifactInfo(Artifact artifact, XWidgetData item, XWidget xWidget) {
      if (artifact == null) {
         return;
      }
      if (xWidget instanceof AttributeWidget || xWidget instanceof AttributeTypeWidget) {
         AttributeTypeToken attributeType = null;
         if (item.getStoreId() > 0) {
            attributeType = AttributeTypeManager.getAttributeType(item.getStoreId());
         }
         if (attributeType == null && Strings.isValid(item.getStoreName())) {
            attributeType = AttributeTypeManager.getType(item.getStoreName());
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
      }
      if (xWidget instanceof AttributeType2Widget) {
         AttributeTypeToken attributeType2 = null;
         if (item.getStoreId2() > 0) {
            attributeType2 = AttributeTypeManager.getAttributeType(item.getStoreId2());
         }
         if (attributeType2 == null && Strings.isValid(item.getStoreName2())) {
            attributeType2 = AttributeTypeManager.getType(item.getStoreName2());
         }
         try {
            if (xWidget instanceof AttributeType2Widget) {
               if (attributeType2 != null) {
                  ((AttributeType2Widget) xWidget).setAttributeType2(attributeType2);
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

   public IStatus isPageComplete() {
      try {
         for (XWidgetData widData : xWidgetDatas) {
            XWidget widget = widDataToXWidget.get(widData);
            IStatus valid = widget.isValid();
            return valid;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return Status.OK_STATUS;
   }

   public Set<XWidgetData> getXWidgetDatas() {
      return xWidgetDatas;
   }

   public void setWidgetDatas(List<XWidgetData> widDatas) {
      this.xWidgetDatas.clear();
      for (XWidgetData widData : widDatas) {
         addXWidgetData(widData);
         this.xWidgetDatas.add(widData);
      }
   }

   public void addXWidgetDatas(List<XWidgetData> widDatas) {
      this.xWidgetDatas.addAll(widDatas);
   }

   public void addXWidgetData(XWidgetData widData) {
      this.xWidgetDatas.add(widData);
   }

   public XWidgetData getXWidgetData(String displayName) {
      for (XWidgetData widData : xWidgetDatas) {
         if (widData.getName().equals(displayName)) {
            return widData;
         }
      }
      return null;
   }

   public void processXWidgetDatas(String xWidgetXml) {
      try {
         Document document = Jaxp.readXmlDocument(xWidgetXml);
         Element rootElement = document.getDocumentElement();

         List<XWidgetData> attrs = XWidgetParser.extractXWidgetDatas(rootElement);
         for (XWidgetData attr : attrs) {
            nameToLayoutData.put(attr.getName(), attr);
            xWidgetDatas.add(attr);
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   public void processXWidgetDatas(Element element) {
      List<XWidgetData> widDatas = XWidgetParser.extractXWidgetDatas(element);
      for (XWidgetData widData : widDatas) {
         nameToLayoutData.put(widData.getName(), widData);
         xWidgetDatas.add(widData);
      }
   }

   public Collection<XWidget> getXWidgets() {
      return xWidgets;
   }

   public XWidget getXWidget(String widgetLabel) {
      XWidgetData widData = getXWidgetData(widgetLabel);
      XWidget widget = widDataToXWidget.get(widData);
      return widget;
   }

   public XWidget getXWidget(XWidgetData widData) {
      return widDataToXWidget.get(widData);
   }
}
