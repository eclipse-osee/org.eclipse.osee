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
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.XErrorUnhandledWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextWidget;
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
 * This class takes a
 *
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public class XWidgetSwtRenderer {
   public static final String XWIDGET = "XWidget";

   private final Set<XWidgetData> xWidgetDatas = new LinkedHashSet<>();
   private final Map<String, XWidgetData> nameToLayoutData = new HashMap<>();

   private final XWidgetSwtRendererListener xWidgetSwtRendererListener;
   private final Collection<XWidget> xWidgets = new ArrayList<>();
   private final Map<XWidgetData, XWidget> widDataToXWidget = new HashMap<>();
   private static final boolean debugLayout = false;

   public XWidgetSwtRenderer() {
      this(null);
   }

   public XWidgetSwtRenderer(XWidgetSwtRendererListener xWidgetSwtRendererListener) {
      this.xWidgetSwtRendererListener = xWidgetSwtRendererListener;
   }

   public void createBody(IManagedForm managedForm, Composite parent, Artifact artifact, XModifiedListener xModListener,
      boolean isEditable) {
      final FormToolkit toolkit = managedForm != null ? managedForm.getToolkit() : null;

      Composite topLevelComp = createComposite(parent, toolkit);
      GridLayout layout = new GridLayout();
      layout.marginWidth = 2;
      layout.marginHeight = 2;
      layout.verticalSpacing = 2;
      topLevelComp.setLayout(layout);
      topLevelComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      debugLayout(topLevelComp, SWT.COLOR_YELLOW);

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
            debugLayout(groupComp, SWT.COLOR_CYAN);
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
         debugLayout(currentComp, SWT.COLOR_GREEN);

         if (widData.is(XOption.FILL_VERTICALLY)) {
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
         } else if (widData.is(XOption.HORIZONTAL_LABEL)) {
            currentComp = createComposite(topLevelComp, toolkit);
            currentComp.setLayout(ALayout.getZeroMarginLayout(2, false));
            currentComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (toolkit != null) {
               toolkit.adapt(currentComp);
            }
         }

         XWidget xWidget = createXWidget(widData);
         xWidget.setArtifact(artifact);
         widDataToXWidget.put(widData, xWidget);
         xWidgets.add(xWidget);

         xWidget.setArtifact(artifact);

         if (xWidgetSwtRendererListener != null) {
            xWidgetSwtRendererListener.widgetCreating(xWidget, toolkit, artifact, this, xModListener, isEditable);
         }

         if (xWidget instanceof XTextWidget) {
            XTextWidget xText = (XTextWidget) xWidget;
            xText.setDynamicallyCreated(true);
         }

         xWidget.createWidgets(managedForm, currentComp, 2);

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

         if (xWidgetSwtRendererListener != null) {
            xWidgetSwtRendererListener.widgetCreated(xWidget, toolkit, artifact, this, xModListener, isEditable);
            xWidgetSwtRendererListener.createXWidgetLayoutData(widData, xWidget, toolkit, artifact, xModListener,
               isEditable);
         }
      }
      topLevelComp.layout();

      validateAndDecorateWidgets(managedForm);
   }

   public XWidget createXWidget(XWidgetData widData) {

      XWidget xWidget = null;
      WidgetId widgetId = WidgetId.SENTINEL;

      try {
         widgetId = getWidgetId(widData);
         xWidget = XWidgetFactory.getInstance().createXWidget(widgetId);
         xWidget = handleXWidgetError(xWidget, widgetId);
         /**
          * Any XWidget wanting to set widData options should extend setWidData, call super and then set values as they
          * will be stored in WidData
          */
         xWidget.setWidData(widData);
      } catch (Exception ex) {
         String msg = String.format("Error creating widget for [%s][%s] exception: [%s] (see error log for details)",
            widData, widgetId, ex.getLocalizedMessage());
         OseeLog.log(Activator.class, Level.SEVERE, msg, ex);
         xWidget = new XLabelWidget(msg);
      }

      return xWidget;
   }

   private XWidget handleXWidgetError(XWidget xWidget, WidgetId widgetId) {
      if (xWidget == null) {
         /**
          * If xWidget is null, confirm Widget and ID have same name and that Widget has the XWidet OSGI annotation (See
          * XXUserTokenWidget for annotation)
          */
         String label = ("Error: Unhandled XWidget \"" + widgetId.getName() + "\"");
         xWidget = new XErrorUnhandledWidget(label);
         // Only log once
         if (!XWidgetFactory.getErrorwidgetids().contains(widgetId)) {
            OseeLog.log(XWidgetFactory.class, Level.SEVERE, label);
            XWidgetFactory.getErrorwidgetids().add(widgetId);
         }
      }
      return xWidget;
   }

   private WidgetId getWidgetId(XWidgetData widData) {
      OrcsTokenService tokenService = ServiceUtil.getTokenService();
      WidgetId widgetId = widData.getWidgetId();
      if (widgetId.isInvalid()) {
         // Get WidgetId from artifact/attribute if not already set
         AttributeTypeToken attributeType = AttributeTypeToken.SENTINEL;
         ArtifactTypeToken artType = ArtifactTypeToken.SENTINEL;
         if (widData.getArtifactType().isValid()) {
            artType = tokenService.getArtifactType(widData.getArtifactType().getId());
         }
         if (attributeType.isInvalid() && Strings.isValid(widData.getStoreName())) {
            attributeType = tokenService.getAttributeType(widData.getStoreName());
         }
         widgetId = getAttributeWidgetId(artType, attributeType);
      }
      if (widgetId.isInvalid()) {
         throw new OseeArgumentException("WidgetId can not be Invalid");
      }
      return widgetId;
   }

   private WidgetId getAttributeWidgetId(ArtifactTypeToken artType, AttributeTypeToken attributeType) {
      if (attributeType != AttributeTypeToken.SENTINEL) {
         IAttributeXWidgetProvider xWidgetProvider = AttributeXWidgetProvider.get();
         List<XWidgetData> concreteWidgets = xWidgetProvider.getDynamicXWidgetLayoutData(artType, attributeType);
         return concreteWidgets.iterator().next().getWidgetId();
      }
      return WidgetId.SENTINEL;
   }

   private Composite createComposite(Composite parent, FormToolkit toolkit) {
      return createComposite(parent, toolkit, false);
   }

   private Composite createComposite(Composite parent, FormToolkit toolkit, boolean border) {
      debugLayout(parent, SWT.COLOR_MAGENTA);
      if (border) {
         return toolkit != null ? toolkit.createComposite(parent, SWT.WRAP | SWT.BORDER) : new Composite(parent,
            SWT.NONE | SWT.BORDER);
      }
      return toolkit != null ? toolkit.createComposite(parent, SWT.WRAP) : new Composite(parent, SWT.NONE);
   }

   private void debugLayout(Composite parent, int color) {
      if (debugLayout) {
         parent.setBackground(Displays.getSystemColor(color));
      }
   }

   private Group buildGroupComposite(Composite parent, String name, int numColumns, FormToolkit toolkit) {
      Group groupComp = new Group(parent, SWT.None);
      if (Strings.isValid(name)) {
         groupComp.setText(name);
      }
      groupComp.setLayout(new GridLayout(numColumns, false));
      groupComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      groupComp.setFont(FontManager.getCourierNew12Bold());
      debugLayout(groupComp, SWT.COLOR_GRAY);
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
      debugLayout(outComp, SWT.COLOR_BLUE);
      return outComp;
   }

   private void validateAndDecorateWidgets(IManagedForm managedForm) {
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
