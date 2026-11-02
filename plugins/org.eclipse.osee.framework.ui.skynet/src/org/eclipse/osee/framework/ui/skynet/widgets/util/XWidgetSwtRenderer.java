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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.validate.XWidgetStatus;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractSTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXLabelValueWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXLabelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.WidgetDebugProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
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

/**
 * This class takes a
 *
 * @author Jeff C. Phillips
 * @author Donald G. Dunne
 */
public class XWidgetSwtRenderer implements WidgetDebugProvider {

   public static final String XWIDGET = "XWidget";
   private final Set<XWidgetData> xWidgetDatas = new LinkedHashSet<>();
   private final XWidgetSwtRendererListener xWidgetSwtRendererListener;
   private final Collection<XWidget> xWidgets = new ArrayList<>();
   private boolean createIfMissing = false;;

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
      XWidgetUtility.debugLayout(topLevelComp, SWT.COLOR_YELLOW);

      if (toolkit != null) {
         toolkit.adapt(topLevelComp);
      }

      boolean inChildComposite = false;
      boolean inGroupComposite = false;
      Composite childComp = null;
      Group groupComp = null;

      // Create XWidgets
      Set<XWidgetData> widDatas = getXWidgetDatas();
      for (XWidgetData widData : widDatas) {

         Composite currentComp = null;

         // first, check if this one is a group, if so, we set the group up and are done with this loop iteration
         int i = widData.getBeginGroupComposite();
         if (i > 0) {
            inGroupComposite = true;
            groupComp = buildGroupComposite(topLevelComp, widData.getLabel(), i, toolkit);
            XWidgetUtility.debugLayout(groupComp, SWT.COLOR_CYAN);
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
         XWidgetUtility.debugLayout(currentComp, SWT.COLOR_GREEN);

         if (widData.is(XOption.FILL_VERT)) {
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
         } else if (widData.is(XOption.HORZ_LABEL)) {
            currentComp = createComposite(topLevelComp, toolkit);
            currentComp.setLayout(ALayout.getZeroMarginLayout(2, false));
            GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
            currentComp.setLayoutData(gd2);

            if (toolkit != null) {
               toolkit.adapt(currentComp);
            }
         }

         if (!(widData.getxWidget() instanceof XWidget) && createIfMissing) {
            try {
               WidgetId widgetId = resolveWidgetId(artifact, widData);
               if (widgetId.isValid()) {
                  XWidget xWidget = XWidgetService.get().createXWidget(widgetId);
                  if (xWidget == null) {
                     throw new OseeStateException("Widget can not be resolved for WidgetId [%s]", widData);
                  }

                  // Default from XWdiget constructor - base
                  XWidgetData xWidgetWidData = xWidget.getWidData();
                  if (xWidget instanceof XXAbstractBaseXWidget<?>) {
                     XXAbstractBaseXWidget<?> abstractBaseXWidget = (XXAbstractBaseXWidget<?>) xWidget;
                     abstractBaseXWidget.setOrigWidData(xWidgetWidData);
                     abstractBaseXWidget.setDebugProvider(this);
                  }

                  // From WidgetBuilder - merge on top of base
                  XWidgetData builderWidData = widData;
                  builderWidData.setXWidget(xWidget);
                  if (xWidget instanceof XXAbstractBaseXWidget<?>) {
                     XXAbstractBaseXWidget<?> abstractBaseXWidget = (XXAbstractBaseXWidget<?>) xWidget;
                     abstractBaseXWidget.setIncomingWidData(builderWidData);
                  }

                  XWidgetData mergedWidData = builderWidData.clone();

                  // Apply incoming WidgetBuilder options on top of default from XWidget constructor
                  Set<XOption> widgetXOptions = xWidgetWidData.getXOptionHandler().getXOptions();
                  Set<XOption> buildXOptions = mergedWidData.getXOptionHandler().getXOptions();
                  mergedWidData.getXOptionHandler().getXOptions().clear();
                  mergedWidData.getXOptionHandler().add(widgetXOptions);
                  mergedWidData.getXOptionHandler().add(buildXOptions);

                  if (Strings.isInvalid(mergedWidData.getLabel()) && Strings.isValid(xWidgetWidData.getLabel())) {
                     mergedWidData.setLabel(xWidgetWidData.getLabel());
                  }
                  if (Strings.isInvalid(mergedWidData.getToolTip()) && Strings.isValid(xWidgetWidData.getToolTip())) {
                     mergedWidData.setToolTip(xWidgetWidData.getToolTip());
                  }
                  if (mergedWidData.getAttributeType().isInvalid() && xWidgetWidData.getAttributeType().isValid()) {
                     mergedWidData.setAttributeType(xWidgetWidData.getAttributeType());
                  }
                  if (Strings.isInvalid(mergedWidData.getLabel()) && mergedWidData.getAttributeType().isValid()) {
                     mergedWidData.setLabel(mergedWidData.getAttributeType().getUnqualifiedName());
                  }
                  if (mergedWidData.getWidgetId().isInvalid() && xWidgetWidData.getWidgetId().isValid()) {
                     mergedWidData.setWidgetId(xWidgetWidData.getWidgetId());
                  }
                  if (xWidgetWidData.getEnumeratedArt().isValid()) {
                     mergedWidData.setEnumeratedArt(xWidgetWidData.getEnumeratedArt());
                  }
                  mergedWidData.getxModListeners().addAll(xWidgetWidData.getxModListeners());
                  mergedWidData.getSwtValueListeners().addAll(xWidgetWidData.getSwtValueListeners());

                  // Give originating UI ability to manipulate merged XWidgetData
                  xWidgetSwtRendererListener.updateMergedXWidgetData(mergedWidData);

                  xWidget.internalSetWidData(mergedWidData);

                  addXWidgetData(mergedWidData);
               } else {
                  throw new OseeStateException("(3) - WidgetId can not be null here for Widget label [%s]", widData);
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, "Error resolving widget for: " + widData.getLabel(), ex);
               createErrorWidget(managedForm, widData, currentComp);
               continue;
            }
         }

         XWidget xWidget = (XWidget) widData.getxWidget();
         if (xWidget == null) {
            OseeLog.log(Activator.class, Level.SEVERE, "Widget is null for: " + widData.getLabel());
            createErrorWidget(managedForm, widData, currentComp);
            continue;
         }
         // Disable notifications and refresh
         xWidget.setNotificationsAllowed(false);
         xWidget.setListenerRefresh(false);

         try {
            if (artifact != null) {
               xWidget.setArtifact(artifact);
            }
            xWidgets.add(xWidget);

            if (xWidgetSwtRendererListener != null) {
               xWidgetSwtRendererListener.widgetCreating(xWidget, toolkit, artifact, this, xModListener, isEditable);
            }

            if (xWidget instanceof XXTextWidget) {
               XXTextWidget xText = (XXTextWidget) xWidget;
               xText.setDynamicallyCreated(true);
            } else if (xWidget instanceof XXAbstractSTextWidget) {
               XXAbstractSTextWidget xText = (XXAbstractSTextWidget) xWidget;
               xText.setDynamicallyCreated(true);
            }

            xWidget.createWidgets(managedForm, currentComp, 2);

            if (xModListener != null) {
               xWidget.addXModifiedListener(xModListener);
            }
            for (Object listener : widData.getxModListeners()) {
               if (listener instanceof XModifiedListener) {
                  xWidget.addXModifiedListener((XModifiedListener) listener);
               } else {
                  throw new OseeArgumentException("listener must be XModifiedListener for %s", widData);
               }
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
         } catch (Exception ex) {
            OseeLog.logf(Activator.class, Level.SEVERE, "Error creating widget for: [%s]\n\n%s", widData.getLabel(),
               Lib.exceptionToString(ex));
            createErrorWidget(managedForm, widData, currentComp);
         }
      }
      topLevelComp.layout();

      validateAndDecorateWidgets(managedForm);

      // Enable notifications and refresh
      for (XWidget widget : getXWidgets()) {
         widget.setNotificationsAllowed(true);
         widget.setListenerRefresh(true);
      }
   }

   private void createErrorWidget(IManagedForm managedForm, XWidgetData widData, Composite currentComp) {
      XXLabelValueWidget errorWidget =
         new XXLabelValueWidget(widData.getLabel(), "Error creating widget... see error log");
      errorWidget.createWidgets(managedForm, currentComp, 1);
      errorWidget.setValueFgColor(SWT.COLOR_RED);
   }

   private WidgetId resolveWidgetId(Artifact artifact, XWidgetData widData) {
      WidgetId widgetId = widData.getWidgetId();
      if (widgetId.isInvalid()) {
         if (artifact != null && widData.getAttributeType().isValid()) {
            XWidgetData attrWidData =
               XWidgetService.get().getDefaultAttrXWidgetData(artifact.getArtifactType(), widData.getAttributeType());
            if (attrWidData.getWidgetId().isValid()) {
               widgetId = attrWidData.getWidgetId();
               if (Strings.isValid(widData.getLabel())) {
                  widData.setLabel(attrWidData.getLabel());
               }
            }
         }
      }
      return widgetId;
   }

   public XWidget createXWidget(XWidgetData widData) {

      XWidget.debugXWidgets.syserr("\nSwtRenderer: Name: [%s] WidgetId: [%s]\n", widData.getLabel(),
         widData.getWidgetId());

      XWidget xWidget = null;
      try {
         // See if we already have a xWidget and widgetId
         xWidget = (XWidget) widData.getxWidget();
         WidgetId widgetId = widData.getWidgetId();
         Conditions.assertTrue(widgetId.isValid(), "WidgetId should be valid here for [%s]", widData.getLabel());

      } catch (Exception ex) {
         String msg = String.format("Error creating widget for [%s] exception: [%s] (see error log for details)",
            widData, ex.getLocalizedMessage());
         OseeLog.log(Activator.class, Level.SEVERE, msg, ex);
         xWidget = new XXLabelWidget(msg);
      }

      return xWidget;
   }

   private Composite createComposite(Composite parent, FormToolkit toolkit) {
      return createComposite(parent, toolkit, false);
   }

   private Composite createComposite(Composite parent, FormToolkit toolkit, boolean border) {
      XWidgetUtility.debugLayout(parent, SWT.COLOR_MAGENTA);
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
      XWidgetUtility.debugLayout(groupComp, SWT.COLOR_GRAY);
      if (toolkit != null) {
         toolkit.adapt(groupComp);
      }
      return groupComp;
   }

   private Composite buildChildComposite(Composite parent, int numColumns, FormToolkit toolkit, boolean border) {
      Composite childComp = createComposite(parent, toolkit, border);
      GridLayout layout = ALayout.getZeroMarginLayout(numColumns, false);
      layout.marginRight = 4; // marginLeft stays at 0 so it doesn't indent
      layout.horizontalSpacing = 8;
      childComp.setLayout(layout);
      childComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      if (toolkit != null) {
         toolkit.adapt(childComp);
      }
      XWidgetUtility.debugLayout(childComp, SWT.COLOR_BLUE);
      return childComp;
   }

   private void validateAndDecorateWidgets(IManagedForm managedForm) {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            try {
               if (managedForm != null) {
                  managedForm.getMessageManager().setAutoUpdate(false);
               }
               for (XWidget widget : getXWidgets()) {
                  if (widget != null) {
                     widget.validate();
                  }
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

   public XResultData isPageComplete() {
      try {
         for (XWidget widget : getXWidgets()) {
            XResultData valid = widget.isValid();
            if (valid.isErrors()) {
               return valid;
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return XWidgetStatus.OK;
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
         if (widData.getLabel().equals(displayName)) {
            return widData;
         }
      }
      return null;
   }

   public Collection<XWidget> getXWidgets() {
      return xWidgets;
   }

   public XWidget getXWidget(String widgetLabel) {
      for (XWidget widget : getXWidgets()) {
         if (widget.getLabel().equals(widgetLabel)) {
            return widget;
         }
      }
      return null;
   }

   public XWidget getXWidget(WidgetId widgetId) {
      for (XWidget widget : getXWidgets()) {
         if (widget.isWidget(widgetId)) {
            return widget;
         }
      }
      return null;
   }

   public void createIfMissing() {
      this.createIfMissing = true;
   }

   public XWidget getWidget(Long id) {
      for (XWidget widget : xWidgets) {
         if (id.equals(widget.getId())) {
            return widget;
         }
      }
      return null;
   }

   @Override
   public XResultData getWidgetDebug(XXAbstractBaseXWidget<?> abstractBaseXWidget, XResultData rd) {
      rd.log(XWidgetUtility.debugWidDataToHtml(abstractBaseXWidget.getLabel(), "XWidget Constructor",
         abstractBaseXWidget.getOrigWidData(), "WidgetBuilder", abstractBaseXWidget.getIncomingWidData(),
         abstractBaseXWidget.getWidData()).toString());
      return rd;
   }

}
