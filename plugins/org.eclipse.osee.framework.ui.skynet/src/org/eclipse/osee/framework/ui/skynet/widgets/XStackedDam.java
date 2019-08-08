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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BinaryBackedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.LongAttribute;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchIdEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.DslGrammarManager;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Roberto E. Escobar
 */
public class XStackedDam extends XStackedWidget<String> implements IAttributeWidget, IArtifactEventListener {
   public static final String WIDGET_ID = XStackedDam.class.getSimpleName();
   private Artifact artifact;
   private AttributeTypeToken attributeType;
   private final XModifiedListener xModifiedListener;
   private List<BranchIdEventFilter> eventFilters;

   public XStackedDam(String displayLabel) {
      super(displayLabel);
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
   protected String getPostfixPageLabel(XStackedWidgetPage page) {
      if (page != null && page.getObjectId() != null) {
         return String.format(" -  Attribute Id (%s)", page.getObjectId().getId());
      }
      return "";
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
      OseeEventManager.addListener(this);
   }

   @SuppressWarnings("deprecation")
   @Override
   protected void createPages(Composite parent, int horizontalSpan) {
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
      page.setObject(artifact);
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

   @SuppressWarnings("deprecation")
   public Attribute<?> getStored(int attrId) {
      for (Attribute<?> attribute : artifact.getAttributes(getAttributeType())) {
         if (attribute.getId() == attrId) {
            return attribute;
         }
      }
      return null;
   }

   @SuppressWarnings("deprecation")
   @Override
   public Result isDirty() {
      if (isEditable()) {
         for (XStackedWidgetPage page : stackedControl.getPages()) {
            if (page.getObjectId() == null) {
               return new Result(true,
                  String.format("Attribute Type " + getAttributeType() + " is dirty; attribute added"));
            } else if (page instanceof XStackedWidgetAttrPage) {
               XStackedWidgetAttrPage attrPage = (XStackedWidgetAttrPage) page;
               // page not dirty till loaded
               if (attrPage.isLoaded()) {
                  Object enteredValue = attrPage.getWidget().getData();
                  Object storedValue = attrPage.getValue();
                  if (!enteredValue.equals(storedValue)) {
                     return new Result(true,
                        String.format("Attribute Type " + getAttributeType() + " is dirty; attribute modified"));
                  }
               }
            } else if (artifact.isDirty()) {
               for (Attribute<?> attr : artifact.getAttributes(attributeType)) {
                  if (attr.isDeleted()) {
                     return new Result(true,
                        String.format("Attribute Type " + getAttributeType() + " is dirty; attribute deleted"));
                  }
               }
            }
         }
      }
      return Result.FalseResult;
   }

   @Override
   public void refresh() {
      stackedControl.removeAllPages();
      stackedControl.clearCurrentPage();
      createPages();
      validate();
   }

   @Override
   public void revert() {
      setAttributeType(getArtifact(), getAttributeType());
   }

   @Override
   public void saveToArtifact() {
      for (XStackedWidgetPage page : stackedControl.getPages()) {
         if (page.getObjectId() == null) {
            artifact.addAttribute(attributeType, page.getWidget().getData());
         } else if (page instanceof XStackedWidgetAttrPage || page.getObjectId() instanceof AttributeId) {
            XStackedWidgetAttrPage attrPage = (XStackedWidgetAttrPage) page;
            if (attrPage.isLoaded()) {
               attrPage.getAttribute().setFromString(page.getWidget().getData().toString());
            }
         }
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
         attrPage.setLoaded(true);
         Attribute<?> attr = attrPage.getAttribute();
         if (attr != null) {
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
                  status = OseeValidator.getInstance().validate(IOseeValidator.SHORT, getArtifact(), getAttributeType(),
                     page.getWidget().getData());
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

   private Date toDate(String value) {
      try {
         return new Date(Long.parseLong(value));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return new Date();
   }

   private Object setWidgetValue(XWidget xWidget, Attribute<?> attribute) {
      String value = attribute.getDisplayableString();
      if (Strings.isValid(value)) {
         xWidget.setNotificationsAllowed(false);
         if (xWidget instanceof XInteger) {
            ((XInteger) xWidget).set(value);
         } else if (xWidget instanceof XLong) {
            ((XLong) xWidget).set(value);
         } else if (xWidget instanceof XDate) {
            ((XDate) xWidget).setDate(toDate(value));
         } else if (xWidget instanceof XFloat) {
            ((XFloat) xWidget).setText(value);
         } else if (xWidget instanceof XLabel) {
            ((XLabel) xWidget).setLabel(value);
         } else if (xWidget instanceof XDslEditorWidgetDam) {
            ((XDslEditorWidgetDam) xWidget).setText(value);
         } else if (xWidget instanceof XText) {
            ((XText) xWidget).setText(value);
         }
         xWidget.setNotificationsAllowed(true);
      }
      return value;
   }

   private XWidget getWidget(AttributeTypeId attributeType, Composite parent, String initialInput) {
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
      } else if (DslGrammarManager.isDslAttributeType(attributeType)) {
         XDslEditorWidgetDam xEditor = new XDslEditorWidgetDam("");
         xEditor.setFillHorizontally(true);
         xEditor.setFillVertically(true);
         xEditor.createWidgets(getManagedForm(), parent, 2);
         if (Strings.isValid(initialInput)) {
            xEditor.setText(initialInput);
         }
      }

      if (xWidget == null) {
         XText xTextWidget = new XTextInternalWidget("");
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

   private final class XTextInternalWidget extends XText {

      public XTextInternalWidget(String label) {
         super(label);
      }

      @Override
      protected int getTextStyle() {
         int styleBase = SWT.NONE;
         if (!isEditable()) {
            styleBase |= SWT.READ_ONLY;
         }
         return styleBase | (fillVertically ? SWT.WRAP | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL : SWT.SINGLE);
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
                  status = OseeValidator.getInstance().validate(IOseeValidator.SHORT, getArtifact(), getAttributeType(),
                     get());
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
      if (eventFilters == null && artifact != null) {
         eventFilters = Collections.singletonList(new BranchIdEventFilter(artifact.getBranch()));
      }
      return eventFilters;
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      if (!artifact.isDeleted() && artifactEvent.isHasEvent(artifact)) {
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
