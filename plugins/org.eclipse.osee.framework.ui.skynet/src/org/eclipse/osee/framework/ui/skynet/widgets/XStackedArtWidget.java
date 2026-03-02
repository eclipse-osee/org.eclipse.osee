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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeMapEntry;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.MapEntryAttributeUtil;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.MapEntryAttribute;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchIdEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactTopicEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.BranchIdTopicEventFilter;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.component.annotations.Component;

/**
 * @author Roberto E. Escobar
 */
@Component(service = XWidget.class, immediate = true)
public class XStackedArtWidget extends XAbstractStackedWidget<String> implements IArtifactEventListener, IArtifactTopicEventListener {

   public static final WidgetId ID = WidgetId.XStackedArtWidget;
   /**
    * StyledText has performance bug that gets bad after about 5000 characters. Word-Wrap is a nice-to-have, so turn off
    * if greater than character limit.
    */
   private static final int SWT_STYLEDTEXT_WORDWRAP_LIMIT = 5000;

   private final XModifiedListener xModifiedListener;
   private List<BranchIdEventFilter> eventFilters;
   private List<BranchIdTopicEventFilter> topicEventFilters;
   private Composite parent;

   public XStackedArtWidget() {
      super(ID, "");
      this.xModifiedListener = new XModifiedListener() {
         @Override
         public void widgetModified(XWidget widget) {
            notifyXModifiedListeners();
         };
      };
   }

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      if (getAttributeType().isInvalid()) {
         int minOccurrence = getArtifact().getArtifactType().getMin(getAttributeType());
         int maxOccurrence = getArtifact().getArtifactType().getMax(getAttributeType());

         if (minOccurrence < 0) {
            minOccurrence = 0;
         }
         if (maxOccurrence < 0) {
            maxOccurrence = 0;
         }
         setPageRange(minOccurrence, maxOccurrence);
         OseeEventManager.addListener(this);
         refresh();
      }
   }

   @Override
   protected void createPages(Composite parent, int horizontalSpan) {
      this.parent = parent;
      try {
         setNotificationsAllowed(false);
         loadPageValues = false;
         for (Attribute<Object> attribute : getArtifact().getAttributes(getAttributeType())) {
            XStackedWidgetAttrPage page = new XStackedWidgetAttrPage(attribute);
            addPage(page);
         }
         setNotificationsAllowed(true);
         loadPageValues = true;
         onPageChange(stackedControl.getPages().iterator().next());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void addPage() {
      XStackedWidgetAttrPage page = new XStackedWidgetAttrPage();
      page.setObject(getArtifact());
      addPage(page);
   }

   @Override
   public void createPageWidget(XStackedWidgetPage page, Composite parent) {
      XWidget widget = createLazyPage(parent, page);
      page.setWidget(widget);
   }

   @Override
   protected void updatePageText(XStackedWidgetPage page) {
      Object value = page.getValue();
      if (value == null) {
         Attribute<?> attr = ((XStackedWidgetAttrPage) page).getAttribute();
         if (attr != null) {
            Object val = setWidgetValue(page.getWidget(), attr);
            page.setValue(val);
         }
      }
   }

   public Attribute<?> getStored(int attrId) {
      for (Attribute<?> attribute : getArtifact().getAttributes(getAttributeType())) {
         if (attribute.getId() == attrId) {
            return attribute;
         }
      }
      return null;
   }

   @Override
   public void refresh() {
      if (Widgets.isAccessible(parent)) {
         stackedControl.removeAllPages();
         stackedControl.clearCurrentPage();
         createPages();
         validate();
      }
   }

   protected XWidget createLazyPage(Composite parent, XStackedWidgetPage page) {
      XWidget xWidget = null;
      try {
         xWidget = getWidget(getAttributeType(), parent, "");
         xWidget.setEditable(isEditable());
         xWidget.addXModifiedListener(xModifiedListener);
         parent.layout();
         parent.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
               handleDisposed();
            }

         });
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return xWidget;
   }

   private void handleDisposed() {
      OseeEventManager.removeListener(this);
   }

   @Override
   public void addPage(XStackedWidgetPage page) {
      super.addPage(page);
   }

   @Override
   protected void onPageChange(XStackedWidgetPage page) {
      if (page != null && page instanceof XStackedWidgetAttrPage) {
         XStackedWidgetAttrPage attrPage = (XStackedWidgetAttrPage) page;
         Attribute<?> attr = attrPage.getAttribute();
         if (attr != null) {
            attrPage.setLoaded(true);
            setWidgetValue(page.getWidget(), attr);
         }
      }
   }

   @Override
   protected void onRemovePage(XStackedWidgetPage page) {
      getArtifact().deleteAttribute((AttributeId) page.getObjectId());
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         for (XStackedWidgetPage page : stackedControl.getPages()) {
            if (page.getWidget() != null && Widgets.isAccessible(page.getWidget().getControl())) {
               status = page.getWidget().isValid();
               if (status.isOK()) {
                  XResultData rd = OseeValidator.getInstance().validate(IOseeValidator.SHORT, getArtifact(),
                     getAttributeType(), page.getWidget().getData());
                  if (rd.isErrors()) {
                     status = new Status(IStatus.ERROR, getClass().getSimpleName(), rd.toString());
                  }
               }
               if (!status.isOK()) {
                  break;
               }
            }
         }
      }
      return status;
   }

   @Override
   public void validate() {
      super.validate();
      XStackedWidgetPage page = getCurrentPage();
      if (page != null) {
         XWidget widget = page.getWidget();
         widget.validate();
      }
   }

   private static Date toDate(String value) {
      try {
         var date = new Date(Long.parseLong(value));
         return date;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return new Date();
   }

   private static Map.Entry<String, String> toMapEntry(Attribute<?> attribute) {

      if (!(attribute instanceof MapEntryAttribute)) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Unexpected attrbute type.");
         return MapEntryAttributeUtil.EMPTY_MAP_ENTRY;
      }

      var mapEntryAttribute = (MapEntryAttribute) attribute;

      try {

         var mapEntry = mapEntryAttribute.getValue();

         return mapEntry;

      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return MapEntryAttributeUtil.EMPTY_MAP_ENTRY;

   }

   private Object setWidgetValue(XWidget xWidget, Attribute<?> attribute) {

      try {

         xWidget.setNotificationsAllowed(false);

         if (!(attribute instanceof MapEntryAttribute)) {

            String value = attribute.getDisplayableString();

            if (Strings.isValid(value)) {

               if (xWidget instanceof XIntegerWidget) {
                  ((XIntegerWidget) xWidget).set(value);
               } else if (xWidget instanceof XLongWidget) {
                  ((XLongWidget) xWidget).set(value);
               } else if (xWidget instanceof XDateWidget) {
                  ((XDateWidget) xWidget).setDate(XStackedArtWidget.toDate(value));
               } else if (xWidget instanceof XFloatTextWidget) {
                  ((XFloatTextWidget) xWidget).setText(value);
               } else if (xWidget instanceof XLabelWidget) {
                  xWidget.setLabel(value);
               } else if (xWidget instanceof XTextWidget) {
                  // Turn off word wrap if string is too large (see above comment)
                  if (value.length() > SWT_STYLEDTEXT_WORDWRAP_LIMIT) {
                     ((XTextWidget) xWidget).getStyledText().setWordWrap(false);
                  }
                  ((XTextWidget) xWidget).setText(value);
               }

            }

            return value;

         } else {

            var value = XStackedArtWidget.toMapEntry(attribute);

            var mapEntryAttribute = (MapEntryAttribute) attribute;
            var mapEntryAttributeType = (AttributeTypeMapEntry) mapEntryAttribute.getAttributeType();
            var xMapEntry = (XMapEntryWidget) xWidget;

            xMapEntry.setMapEntry(value);
            xMapEntry.setToolTips(mapEntryAttributeType.getKeyDescription(),
               mapEntryAttributeType.getValueDescription());

            return value;
         }

      } finally {
         xWidget.setNotificationsAllowed(true);
      }
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
      } else if (attributeType.isMapEntry()) {
         XMapEntryWidget xMapEntry = new XMapEntryWidget();
         xMapEntry.setFillVertically(true);
         xMapEntry.createWidgets(this.getManagedForm(), parent, 2);
         if (Strings.isValidAndNonBlank(initialInput)) {
            Map.Entry<String, String> mapEntry = MapEntryAttributeUtil.jsonDecode(initialInput);
            xMapEntry.setMapEntry(mapEntry);
         }
         xWidget = xMapEntry;
      }

      if (xWidget == null) {
         XTextWidget xTextWidget = new XTextInternalWidget("");
         if (Strings.isValid(initialInput)) {
            xTextWidget.setText(initialInput);
         }
         xTextWidget.addXTextSpellModifyDictionary(new SkynetSpellModifyDictionary());
         xTextWidget.setFillHorizontally(false);
         xTextWidget.setFillVertically(true);
         xTextWidget.createWidgets(getManagedForm(), parent, 2);
         xWidget = xTextWidget;
      }
      parent.layout();
      return xWidget;
   }

   private final class XTextInternalWidget extends XTextWidget {

      public XTextInternalWidget(String label) {
         super(ID, label);
      }

      @Override
      protected int getTextStyle() {
         int styleBase = SWT.NONE;
         if (!isEditable()) {
            styleBase |= SWT.READ_ONLY;
         }
         return styleBase | (isFillVertically() ? SWT.WRAP | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL : SWT.SINGLE);
      }

      @Override
      public void createControls(Composite parent, int horizontalSpan, boolean fillText) {
         super.createControls(parent, horizontalSpan, fillText);
         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
         gd.widthHint = 200;
         gd.heightHint = 200;
         sText.setLayoutData(gd);
         sText.setWordWrap(true);
         sText.setEditable(isEditable());
         if (!isEditable()) {
            sText.setBackground(Displays.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
         }
      }

      @Override
      public IStatus isValid() {
         IStatus status = super.isValid();
         if (status.isOK()) {
            try {
               if (getArtifact() != null && getAttributeType() != null) {
                  XResultData rd = OseeValidator.getInstance().validate(IOseeValidator.SHORT, getArtifact(),
                     getAttributeType(), get());
                  if (rd.isErrors()) {
                     status = new Status(IStatus.ERROR, getClass().getSimpleName(), rd.toString());
                  }
               }
            } catch (OseeCoreException ex) {
               status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error getting Artifact", ex);
            }
         }
         return status;
      }

   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      if (eventFilters == null && getArtifact() != null) {
         eventFilters = Collections.singletonList(new BranchIdEventFilter(getArtifact().getBranch()));
      }
      return eventFilters;
   }

   @Override
   public List<? extends ITopicEventFilter> getTopicEventFilters() {
      if (topicEventFilters == null && getArtifact() != null) {
         topicEventFilters = Collections.singletonList(new BranchIdTopicEventFilter(getArtifact().getBranch()));
      }
      return topicEventFilters;
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (!getArtifact().isDeleted() && artifactEvent.isHasEvent(getArtifact())) {
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               if (Widgets.isAccessible(getControl())) {
                  refresh();
               }
            }
         });
      }
   }

   @Override
   public void handleArtifactTopicEvent(ArtifactTopicEvent artifactTopicEvent, Sender sender) {
      if (!getArtifact().isDeleted() && artifactTopicEvent.isHasEvent(getArtifact())) {
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               if (Widgets.isAccessible(getControl())) {
                  refresh();
               }
            }
         });
      }
   }
}
