/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BinaryBackedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.LongAttribute;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Donald G. Dunne
 */
public class XTextFlatDam extends XFlatWidget<String> implements IAttributeWidget {
   private Artifact artifact;
   private AttributeTypeToken attributeType;
   private final Map<String, XWidget> xWidgets;
   private final XModifiedListener xModifiedListener;
   public static final String WIDGET_ID = XTextFlatDam.class.getSimpleName();

   public XTextFlatDam() {
      this("");
   }

   public XTextFlatDam(String displayLabel) {
      super(displayLabel);
      this.xWidgets = new LinkedHashMap<>();
      this.artifact = null;
      this.xModifiedListener = new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            notifyXModifiedListeners();
         };
      };
   }

   @Override
   public Artifact getArtifact() {
      return artifact;
   }

   @Override
   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   @Override
   public void setAttributeType(Artifact artifact, AttributeTypeToken attributeType) {
      this.artifact = artifact;
      this.attributeType = attributeType;

      int minOccurrence = AttributeTypeManager.getMinOccurrences(attributeType);
      int maxOccurrence = AttributeTypeManager.getMaxOccurrences(attributeType);

      if (minOccurrence == 0) {
         minOccurrence = 1;
      }
      setPageRange(minOccurrence, maxOccurrence);
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
               if (widget instanceof XText) {
                  ((XText) widget).set(dataIterator.next());
               } else if (widget instanceof XDate) {
                  ((XDate) widget).setDate(toDate(dataIterator.next()));
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

   public List<String> getInput() {
      List<String> data = new ArrayList<>();
      for (XWidget widget : xWidgets.values()) {
         if (widget instanceof XText) {
            data.add(((XText) widget).get());
         } else if (widget instanceof XDate) {
            Date date = ((XDate) widget).getDate();
            if (date != null) {
               data.add(String.valueOf(date.getTime()));
            }
         }
      }
      return data;
   }

   public Collection<String> getStored()  {
      return artifact.getAttributesToStringList(getAttributeType());
   }

   @Override
   public Result isDirty()  {
      if (isEditable()) {
         try {
            Collection<String> enteredValues = new ArrayList<>();//getSelected();
            Collection<String> storedValues = getStored();
            if (!Collections.isEqual(enteredValues, storedValues)) {
               return new Result(true, getAttributeType() + " is dirty");
            }
         } catch (NumberFormatException ex) {
            // do nothing
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void revert()  {
      setAttributeType(artifact, getAttributeType());
   }

   @Override
   public void saveToArtifact()  {
      artifact.setAttributeValues(getAttributeType(), getInput());
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

   private XWidget getWidget(AttributeTypeId attributeType, Composite parent, String initialInput)  {
      XWidget xWidget = null;

      if (AttributeTypeManager.isBaseTypeCompatible(IntegerAttribute.class, attributeType)) {
         XInteger xInteger = new XInteger("");
         xInteger.setFillHorizontally(true);
         xInteger.createWidgets(getManagedForm(), parent, 2);
         if (Strings.isValid(initialInput)) {
            xInteger.setText(initialInput);
         }
         xWidget = xInteger;
      } else if (AttributeTypeManager.isBaseTypeCompatible(LongAttribute.class, attributeType)) {
         XLong xLong = new XLong("");
         xLong.setFillHorizontally(true);
         xLong.createWidgets(getManagedForm(), parent, 2);
         if (Strings.isValid(initialInput)) {
            xLong.setText(initialInput);
         }
         xWidget = xLong;
      } else if (AttributeTypeManager.isBaseTypeCompatible(DateAttribute.class, attributeType)) {
         XDate xDate = new XDate("");
         xDate.setFillHorizontally(true);
         xDate.createWidgets(getManagedForm(), parent, 2);
         if (Strings.isValid(initialInput)) {
            xDate.setDate(toDate(initialInput));
         }
         xWidget = xDate;
      } else if (AttributeTypeManager.isBaseTypeCompatible(FloatingPointAttribute.class, attributeType)) {
         XFloat xFloat = new XFloat("");
         xFloat.setFillHorizontally(true);
         xFloat.createWidgets(getManagedForm(), parent, 2);
         if (Strings.isValid(initialInput)) {
            xFloat.setText(initialInput);
         }
         xWidget = xFloat;
      } else if (AttributeTypeManager.isBaseTypeCompatible(BinaryBackedAttribute.class, attributeType)) {
         XLabel xLabel = new XLabel("");
         xLabel.setFillHorizontally(true);
         xLabel.createWidgets(getManagedForm(), parent, 2);
         if (Strings.isValid(initialInput)) {
            xLabel.setLabel(initialInput);
         }
         xWidget = xLabel;
      }

      if (xWidget == null) {
         XText xTextWidget = new XTextInternalWidget("");
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

   private final class XTextInternalWidget extends XText {

      public XTextInternalWidget(String label) {
         super(label);
      }

      @Override
      protected int getTextStyle() {
         int styleBase = SWT.NONE;
         if (isEditable()) {
            styleBase |= SWT.READ_ONLY;
         }
         return styleBase | (fillVertically ? SWT.WRAP | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL : SWT.SINGLE);
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
}
