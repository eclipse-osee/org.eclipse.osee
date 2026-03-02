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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.progress.UIJob;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XTextFlatArtWidget extends XAbstractFlatWidget<String> {

   public static final WidgetId ID = WidgetId.XTextFlatArtWidget;

   private final Map<String, XWidget> xWidgets;
   private final XModifiedListener xModifiedListener;

   public XTextFlatArtWidget() {
      this(ID, "");
   }

   public XTextFlatArtWidget(String displayLabel) {
      this(ID, displayLabel);
   }

   public XTextFlatArtWidget(WidgetId widgetId, String displayLabel) {
      super(widgetId, displayLabel);
      this.xWidgets = new LinkedHashMap<>();
      this.xModifiedListener = new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            notifyXModifiedListeners();
         };
      };
   }

   @Override
   public void refresh() {
      super.refresh();
      int minOccurrence = getArtifact().getArtifactType().getMin(getAttributeType());
      int maxOccurrence = getArtifact().getArtifactType().getMax(getAttributeType());

      if (minOccurrence < 0) {
         minOccurrence = 0;
      }
      if (maxOccurrence < 0) {
         maxOccurrence = 0;
      }

      setPageRange(minOccurrence, maxOccurrence);
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      refresh();
   }

   @Override
   protected int getTotalItems() {
      return xWidgets.size();
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      final Collection<String> values = new ArrayList<>();
      try {
         values.addAll(getStored());
         for (int index = 0; index < values.size(); index++) {
            addPage("");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      Job job = new UIJob("Update Flat XText") {

         @Override
         public IStatus runInUIThread(IProgressMonitor monitor) {
            setNotificationsAllowed(false);
            Iterator<String> dataIterator = values.iterator();
            Iterator<XWidget> widgetIterator = xWidgets.values().iterator();
            while (dataIterator.hasNext() && widgetIterator.hasNext()) {
               XWidget widget = widgetIterator.next();
               String value = dataIterator.next();
               if (widget instanceof XTextWidget) {
                  ((XTextWidget) widget).set(getDisplayValue(value));
               } else if (widget instanceof XDateWidget) {
                  ((XDateWidget) widget).setDate(toDate(getDisplayValue(value)));
               }
            }
            values.clear();
            setNotificationsAllowed(true);
            refresh();
            return Status.OK_STATUS;
         }

      };
      Jobs.startJob(job);
   }

   protected String getDisplayValue(String value) {
      return value;
   }

   public List<String> getInput() {
      List<String> data = new ArrayList<>();
      for (XWidget widget : xWidgets.values()) {
         if (widget instanceof XTextWidget) {
            data.add(((XTextWidget) widget).get());
         } else if (widget instanceof XDateWidget) {
            Date date = ((XDateWidget) widget).getDate();
            if (date != null) {
               data.add(String.valueOf(date.getTime()));
            }
         }
      }
      return data;
   }

   public Collection<String> getStored() {
      return getArtifact().getAttributesToStringList(getAttributeType());
   }

   @Override
   protected void createPage(final String id, Composite parent, String initialInput) {
      if (!xWidgets.containsKey(id)) {
         try {
            XWidget xWidget = getWidget(getAttributeType(), parent, initialInput);
            xWidget.setEditable(isEditable());
            xWidgets.put(id, xWidget);

            xWidget.addXModifiedListener(xModifiedListener);
            parent.layout();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   @Override
   protected void onRemovePage(String id) {
      xWidgets.remove(id);
   }

   @Override
   public IStatus isValid() {
      for (XWidget widget : xWidgets.values()) {
         IStatus status = widget.isValid();
         if (!status.isOK()) {
            return status;
         }
      }
      return Status.OK_STATUS;
   }

   @Override
   public void validate() {
      for (String id : getPageIds()) {
         if (Strings.isValid(id)) {
            XWidget widget = xWidgets.get(id);
            widget.validate();
         }
      }
   }

   private Date toDate(String value) {
      try {
         return new Date(Long.parseLong(value));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return new Date();
   }

   private XWidget getWidget(AttributeTypeToken attributeType, Composite parent, String initialInput) {
      XWidget xWidget = null;

      if (attributeType.isInteger()) {
         XIntegerWidget xInteger = new XIntegerWidget();
         xInteger.setFillHorizontally(true);
         xInteger.createWidgets(getManagedForm(), parent, 2);
         if (Strings.isValid(initialInput)) {
            xInteger.setText(initialInput);
         }
         xWidget = xInteger;
      } else if (attributeType.isLong()) {
         XLongWidget xLong = new XLongWidget();
         xLong.setFillHorizontally(true);
         xLong.createWidgets(getManagedForm(), parent, 2);
         if (Strings.isValid(initialInput)) {
            xLong.setText(initialInput);
         }
         xWidget = xLong;
      } else if (attributeType.isDate()) {
         XDateWidget xDate = new XDateWidget();
         xDate.setFillHorizontally(true);
         xDate.createWidgets(getManagedForm(), parent, 2);
         if (Strings.isValid(initialInput)) {
            xDate.setDate(toDate(initialInput));
         }
         xWidget = xDate;
      } else if (attributeType.isDouble()) {
         XFloatTextWidget xFloat = new XFloatTextWidget("");
         xFloat.setFillHorizontally(true);
         xFloat.createWidgets(getManagedForm(), parent, 2);
         if (Strings.isValid(initialInput)) {
            xFloat.setText(initialInput);
         }
         xWidget = xFloat;
      } else if (attributeType.isInputStream() || attributeType.isJavaObject()) {
         XLabelWidget xLabel = new XLabelWidget("");
         xLabel.setFillHorizontally(true);
         xLabel.createWidgets(getManagedForm(), parent, 2);
         if (Strings.isValid(initialInput)) {
            xLabel.setLabel(initialInput);
         }
         xWidget = xLabel;
      }

      if (xWidget == null) {
         XTextWidget xTextWidget = new XTextInternalWidget("");
         if (Strings.isValid(initialInput)) {
            xTextWidget.setText(initialInput);
         }
         xTextWidget.addXTextSpellModifyDictionary(new SkynetSpellModifyDictionary());
         xTextWidget.setFillHorizontally(false);
         xTextWidget.setFillVertically(false);
         xTextWidget.createWidgets(getManagedForm(), parent, 2);
         xWidget = xTextWidget;
      }
      return xWidget;
   }

   private final class XTextInternalWidget extends XTextWidget {

      public XTextInternalWidget(String label) {
         super(ID, label);
      }

      @Override
      protected int getTextStyle() {
         int styleBase = SWT.NONE;
         if (isEditable()) {
            styleBase |= SWT.READ_ONLY;
         }
         return styleBase | (isFillVertically() ? SWT.WRAP | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL : SWT.SINGLE);
      }

      @Override
      public void createControls(Composite parent, int horizontalSpan, boolean fillText) {
         super.createControls(parent, horizontalSpan, fillText);
         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
         gd.widthHint = 200;
         sText.setLayoutData(gd);
         sText.setWordWrap(true);
         sText.setEditable(isEditable());
         if (!isEditable()) {
            sText.setBackground(Displays.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
         }
      }
   }

   @Override
   protected Action getMultiLineAction() {
      if (getAttributeType().isString()) {
         return new MultiLineEdit();
      }
      return null;
   }

   private final class MultiLineEdit extends Action {
      public MultiLineEdit() {
         super();
         setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.EDIT));
         setToolTipText("Multi-Line Edit");
      }

      @Override
      public void run() {
         AttributeTypeToken attrType = getAttributeType();
         StringBuffer sb = new StringBuffer();
         for (String value : getArtifact().getAttributesToStringList(attrType)) {
            sb.append(value + "\n");
         }
         EntryDialog dialog = new EntryDialog("Enter Values", "Enter Values (one per line)");
         dialog.setFillVertically(true);
         dialog.setEntry(sb.toString());
         if (dialog.open() == Window.OK) {
            Collection<Object> objs = new ArrayList<>();
            for (String line : dialog.getEntry().split("\n")) {
               objs.add(line);
            }
            getArtifact().setAttributeFromValues(attrType, objs);
            getArtifact().persist("Multi-Edit Values");
         }
      }
   }

}
