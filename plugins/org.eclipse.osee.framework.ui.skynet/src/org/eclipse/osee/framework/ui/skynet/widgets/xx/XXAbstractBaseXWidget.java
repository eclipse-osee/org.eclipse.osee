/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.data.HasArtifactToken;
import org.eclipse.osee.framework.core.data.Multiplicity;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.conditions.ConditionalRule;
import org.eclipse.osee.framework.core.data.conditions.EnableIfAttrValueCondition;
import org.eclipse.osee.framework.core.widget.ISelectableWidget;
import org.eclipse.osee.framework.core.widget.SwtWidgetListener;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XWidgetStatus;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.result.XResultType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.NoOpViewerComparator;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.util.StringNameComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTree;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTree;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.FilteredCheckboxTreeListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.WidgetDebugProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XWidgetUtility;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * One Widget to rule them all... <br/>
 * <br/>
 * This widget is intended to replace most other widgets. It handles the functionality necessary for most use cases.
 * This includes handling if it resides in an editor backed by and artifact (eg: Artifact Editor) or not (eg: BLAM or
 * Dialog). Most widgets should NOT need to extend this separately. All features should be provided by setting options
 * in base XWidget class and also available through WidgetBuilders.<br/>
 * <br/>
 * Features:<br/>
 * - Selectable hyperlink label if editable<br/>
 * - Non-Selectable link label if readonly<br/>
 * - Ability to set selectable and default selected<br/>
 * - Default of enumerated attribute to provide selectable<br/>
 * - Filterable Dialog with multi-select (checkboxes) or single select (list)<br/>
 * - Ability to have clear button<br/>
 * - Right-click provides clear by default<br/>
 * - Double-click shows tooltip dialog, if tooltip is provided<br/>
 * - Selectable items sorted by default. setComparator for specialized sorting<br/>
 * - Validation provided for most use cases by isValid so don't need separate validators<br/>
 * - Ability to have an icon for a Widget via setOseeImage <br/>
 * <br/>
 * - Lifecycle and performance can be measured through the {@code debugLifecycle(...)} calls using
 * {@link DebugLifecycle}; its class Javadoc documents modes, capture, and paste-to-AI analysis<br/>
 *
 * @author Donald G. Dunne
 */
public abstract class XXAbstractBaseXWidget<T> extends XWidget implements ISelectableWidget {

   private static final int WRAPPED_VALUE_WIDTH_HINT = 200;
   protected static final String READ_ONLY = "This attribute is read-only and can not be directly edited";
   protected static final String SELECT_TO_MODIFY = "Select to Modify";

   protected Text valueWidget;
   protected StyledText valueSText;
   protected Label iconLabel;
   protected Composite comp;
   private Color labelFgBeforeDisable;
   // Do not access directly, use setSelected methods
   private final Collection<T> selected = new ArrayList<>();
   // Do not access directly, use setSelectable or override getSelectable methods
   private final List<T> selectable = new ArrayList<>();
   private WidgetDebugProvider debugProvider;
   private XWidgetData origWidData;
   private XWidgetData incomingWidData;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   /**
    * NOTE: Don't set any widData options during construction as it may get overwritten after construction. Overriding
    * XWidget.setWidData and setting values after super.setWidData is called will work.
    */
   public XXAbstractBaseXWidget(XWidgetData widData) {
      this(widData.getWidgetId(), widData.getLabel(), new StringLabelProvider());
   }

   public XXAbstractBaseXWidget(WidgetId widgetId, String label) {
      this(widgetId, label, new StringLabelProvider());
   }

   public XXAbstractBaseXWidget(WidgetId widgetId, String label, ILabelProvider labelProvider) {
      super(widgetId, label);
      this.labelProvider = labelProvider;
      set(XOption.LABEL_BOLD, XOption.COLON);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   /**
    * Creates<br>
    * - Icon widget, if setOseeImage is valid<br>
    * - Label widget, unless turned off through XOption.NO_LABEL<br>
    * - Value widget<br>
    * Then calls for "selected" to be populated. This should be done through setSelected(collection, false) so refresh
    * is not called<br>
    * And refresh() is called to populate value and icon widgets.
    */
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      int numCols = 2;
      if (isFillVertically() || isVerticalLabel()) {
         numCols = 1;
      }
      if (isIconWidget()) {
         numCols++;
      }
      comp = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(numCols, false);
      layout.marginHeight = 1;
      comp.setLayout(layout);
      if (isFillHorizontally()) {
         GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
         if (isFillVertically()) {
            gd.grabExcessVerticalSpace = true;
            gd.verticalAlignment = SWT.FILL;
         }
         comp.setLayoutData(gd);
      } else {
         comp.setLayoutData(new GridData());
      }
      if (getToolkit() != null) {
         getToolkit().adapt(comp);
      }
      createIconWidget();
      createLabelWidget();
      createValueWidget();
      setLabelToolTip();
      debugControlEvents(comp, "widget-composite");
      debugControlEvents(labelLinkWidget, "label-hyperlink");
      debugControlEvents(labelWidget, "label");
      debugControlEvents(valueWidget, "value-text");
      debugControlEvents(valueSText, "value-styled-text");
      initializeSelected();
      refresh();
      debugLifecycle("createControls EXIT", "");
   }

   /**
    * Create a widget to show the icon if XOption.ICON_WIDGET or setOseeImage is called with a valid OseeImage.
    * XOption.ICON_WIDGET is used when an icon can be displayed but may not be immediately shown. Example ATS Hold
    * widget where a warning icon is showed but only when workflow is on hold and then removed when hold is removed.
    */
   private void createIconWidget() {
      if (isIconWidget()) {
         iconLabel = new Label(comp, SWT.NONE);
         iconLabel.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));
         if (getToolkit() != null) {
            getToolkit().adapt(iconLabel, false, false);
         }
      }
   }

   /**
    * Creates either a labelLink widget if widget is writeable, XOption.WRITE. This is the default behavior for artifact
    * based widgets since they are all auto-persisted. This reduces the need for interim writes to the database as
    * things like text fields are continually edited. Also keeps from having to have clunkly listeners to decide when to
    * persist and concurrency issues if there is an auto-save and a editor save. If widget is read-only, XWidget.READ, a
    * normal bold label will be used.
    */
   protected void createLabelWidget() {
      debugLifecycle("createLabelWidget ENTER", "editable=%s noLabel=%s", isEditable(), widData.is(XOption.NO_LABEL));
      if (widData.is(XOption.NO_LABEL)) {
         debugLifecycle("createLabelWidget EXIT", "no label");
         return;
      }
      String labelStr = getLabel() + (widData.is(XOption.COLON) ? ":" : "");
      if (isEditable() && !is(XOption.INLINE_EDIT)) {
         if (getToolkit() == null) {
            labelLinkWidget = new Hyperlink(comp, SWT.NONE);
            labelLinkWidget.setText(labelStr);
            labelLinkWidget.setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
            labelLinkWidget.setUnderlined(true);
         } else {
            labelLinkWidget = getToolkit().createHyperlink(comp, labelStr, SWT.NONE);
         }
         labelLinkWidget.setLayoutData(new GridData());
         // MouseDown so dialog opened over link with double-click close doesn't activate this listener
         labelLinkWidget.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent event) {
               if (event.button == 1) {
                  if (controlBeingHeld(event)) {
                     openWidgetDebug();
                  } else if (is(XOption.BUTTON_HYPER)) {
                     // Action button: handleSelection() is the canonical action. Widgets implement their
                     // action there. (Widgets that also want modified-listener notification can call
                     // notifyXModifiedListeners() from within their handleSelection().)
                     handleSelection();
                  } else {
                     boolean changed = handleSelection();
                     if (changed) {
                        refresh();
                        notifyXModifiedListeners();
                     }
                  }
               } else if (event.button == 3) {
                  if (is(XOption.BUTTON_HYPER)) {
                     notifyRightClickListeners();
                  } else if (is(XOption.CLEAR)) {
                     handleClear();
                  }
               }
            }

         });
      } else {
         if (getToolkit() == null) {
            labelWidget = new Label(comp, SWT.NONE);
            labelWidget.setText(labelStr);
         } else {
            labelWidget = getToolkit().createLabel(comp, labelStr, SWT.NONE);
         }
         if (!widData.is(XOption.NO_LABEL_BOLD)) {
            labelWidget.setFont(FontManager.getDefaultLabelFont());
         }
         labelWidget.setLayoutData(new GridData());
         labelWidget.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent event) {
               if (event.button == 1) {
                  if (controlBeingHeld(event)) {
                     openWidgetDebug();
                  }
               } else if (event.button == 3 && is(XOption.CLEAR)) {
                  handleClear();
               }
            }

         });

         if (widData.getFgLabelColor() != null) {
            labelWidget.setForeground(Displays.getSystemColor(widData.getFgLabelColor()));
         }
         if (widData.is(XOption.LABEL_BLUE)) {
            labelWidget.setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
         }
         if (widData.is(XOption.LABEL_RED)) {
            labelWidget.setForeground(Displays.getSystemColor(SWT.COLOR_RED));
         }
         if (widData.is(XOption.LABEL_YELLOW)) {
            labelWidget.setForeground(Displays.getSystemColor(SWT.COLOR_DARK_YELLOW));
         }
      }
      debugLifecycle("createLabelWidget EXIT", "link=%s label=%s", Widgets.isAccessible(labelLinkWidget),
         Widgets.isAccessible(labelWidget));
   }

   /**
    * Creates the widget to show the value. This widget can also be a hyperlink if XOption.VALUE_HYPER. Example: ATS
    * Hold where label lets you add/remove hold and value select allows the changing of the text reason why there's a
    * hold.
    */
   @SuppressWarnings("unchecked")
   protected void createValueWidget() {
      debugLifecycle("createValueWidget ENTER", "noValue=%s valueHyper=%s", is(XOption.NO_VALUE),
         is(XOption.VALUE_HYPER));
      // NO_VALUE: render label-only
      if (is(XOption.NO_VALUE)) {
         debugLifecycle("createValueWidget EXIT", "no value control");
         return;
      }
      int style = SWT.READ_ONLY | (is(XOption.VALUE_BORDER) ? SWT.BORDER : SWT.NONE);
      if (is(XOption.WRAP_VALUES)) {
         style |= SWT.MULTI | SWT.WRAP;
      }
      valueWidget = new Text(comp, style);
      if (getToolkit() != null) {
         getToolkit().adapt(valueWidget, false, false);
      }
      GridData gd = new GridData();
      if (is(XOption.WRAP_VALUES)) {
         gd.widthHint = WRAPPED_VALUE_WIDTH_HINT;
         gd.grabExcessHorizontalSpace = true;
         gd.horizontalAlignment = SWT.FILL;
      } else if (isFillHorizontally()) {
         gd.grabExcessHorizontalSpace = true;
         gd.horizontalAlignment = SWT.FILL;
      }
      valueWidget.setLayoutData(gd);
      if (widData.getDefaultValue() != null) {
         // Ensure correct type for default value
         if (getSentinel() != null && !widData.getDefaultValue().getClass().isAssignableFrom(
            getSentinel().getClass())) {
            setSelected((T) widData.getDefaultValue());
         }
      }
      for (SwtWidgetListener swtListener : widData.getSwtValueListeners()) {
         valueWidget.addListener(swtListener.getSwtOperation(), (Listener) swtListener.getListener());
      }
      if (is(XOption.VALUE_HYPER)) {
         final XWidget fWidget = this;
         valueWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
               super.mouseDown(e);
               handleValueSelection(fWidget);
            }
         });
      }
      debugLifecycle("createValueWidget EXIT", "valueControl=%s", System.identityHashCode(valueWidget));
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   /**
    * Warning: You should not need to override this method. Add generic editing of values should be handled by this
    * class and managed by XOption / XWidgetData settings.<br/>
    * <br/>
    * Widgets overriding handleSelection should <br/>
    * <br/>
    * - Open dialog to get new selection(s)<br/>
    * - Call setSelected(<value>, false); - to update selections<br/>
    * - Call handleSelectedPersist(); - to persist new selection(s)<br/>
    * - return true; - this will cause a refresh(); widget should not refresh on it's own<br/>
    *
    * @return true if value changed; this will cause a refresh of the widget
    */
   protected boolean handleSelection() {
      debugLifecycle("handleSelection ENTER", "selectable=%s enabled=%s", isSelectable(), !is(XOption.NOT_ENABLED));
      try {
         if (!isSelectable()) {
            return false;
         }
         if (is(XOption.NOT_ENABLED)) {
            return false;
         }
         XResultData rd = validateHandleSelection();
         if (rd.isErrors()) {
            AWorkbench.popup("Selection Error", rd.toString());
            return false;
         }
         // Example: Multi-line Text - like Description
         if (isValueMultiLineText()) {
            return handleMultiLineTextEntry();
         }
         // Example: Single line Text - like Title
         else if (isValueSingleLineText()) {
            return handleSingleLineTextEntry();
         }
         // Example: Checkbox Dialog, select multiple - like Team Definitions or multiple enums
         else if (isValueMultiSelectFromSelectable()) {
            return handleMultiSelectFromSelectable();
         }
         // Example: Mutiple option dialog, select one - like Targeted Version or single enum
         else if (isValueSingleSelectFromSelectable()) {
            return handleSingleSelectFromSelectable();
         }
         // Un-supported for handleSelection
         else {
            OseeLog.logf(this.getClass(), Level.WARNING, "Widget should be MULTI/SINGLE - SELECT/TEXT; not for [%s]",
               widData);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   /**
    * Set value selected. This will cause a refresh of the UI. Use setSelected(selected, false) to set selected during
    * initialization or refresh will be called multiple times.
    *
    * @param selected values of the XWidget object type
    */
   public void setSelected(T selected) {
      setSelected(selected, true);
   }

   /**
    * @param refreshUI - Shouldn't be false unless reload from store or initialize
    */
   @SuppressWarnings("unchecked")
   public void setSelected(T selected, boolean refreshUI) {
      debugLifecycle("setSelected(single) ENTER", "incoming=%s refreshUI=%s", selected, refreshUI);
      this.selected.clear();
      if (selected instanceof Collection) {
         setSelected((Collection<T>) selected, refreshUI);
      } else {
         matchTypeOrException(selected);
         this.selected.add(selected);
      }
      if (is(XOption.VALUE_TOOLTIP)) {
         setValueToolTip(String.valueOf(selected));
      }
      if (refreshUI) {
         refresh();
      }
      debugLifecycle("setSelected(single) EXIT", "refreshUI=%s", refreshUI);
   }

   /**
    * Default implementation of check for setSelected; Can be overridden if SENTINEL doesn't exist or is of a different
    * instanceof the type. eg: XXBranchSelectWidget, getSentinel is UserTokenImpl and so doesn't match instanceof. In
    * these cases, override and provide runtime instanceof check. Should
    */
   public boolean isOfType(Object obj) {
      if (getSentinel() == null) {
         throw new OseeStateException("If [%s].getSentinel() == null, isOfType must be implemented.",
            getClass().getSimpleName());
      }
      if (getSentinel() != null && getSentinel().getClass().isInstance(obj)) {
         return true;
      }
      return false;
   }

   /**
    * Set values selected. This will cause a refresh of the UI. Use setSelected(collection, false) to set selected
    * during initialization or refresh will be called multiple times.
    */
   public void setSelected(Collection<T> selected) {
      this.setSelected(selected, true);
   }

   /**
    * Set default values selected.
    *
    * @param refreshUI refresh UI. Shouldn't be false unless reload from store or initialize
    */
   public void setSelected(Collection<T> selected, boolean refreshUI) {
      debugLifecycle("setSelected(collection) ENTER", "incoming=%s refreshUI=%s", selected, refreshUI);
      this.selected.clear();
      matchTypeOrException(selected);
      this.selected.addAll(selected);
      if (refreshUI) {
         refresh();
      }
      notifyXModifiedListeners();
      debugLifecycle("setSelected(collection) EXIT", "refreshUI=%s", refreshUI);
   }

   /**
    * Clear selected and refresh the UI.
    */
   public void clear() {
      clear(true);
   }

   /**
    * Clear selected.
    */
   public void clear(boolean refreshUI) {
      debugLifecycle("clear", "refreshUI=%s", refreshUI);
      this.selected.clear();
      if (refreshUI) {
         refresh();
      }
   }

   /**
    * Turns selected into display str. XWidgets should try to populate selected so this method does not have to be
    * extended. <br>
    * - Default (preferred): selected (NOT getSelected()), <br>
    * semicolon delimited eg: getPartitions(), getAssignees(), getChangeType()<br>
    * - Extended: - when data object(s) and display are different, eg: getRemainingHours() or getSupporting() where calc
    * needs be done
    */
   public String getDisplayValue() {
      if (selected.isEmpty()) {
         return "";
      }
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString("; ", selected);
   }

   /**
    * List of objects to select from a filterable dialog. This should be overridden (preferred) to only load selectable
    * when user desires or on construction (discouraged for performance issues, no need to load options if user isn't
    * going to change).
    */
   @Override
   public Collection<T> getSelectable() {
      return selectable;
   }

   /**
    * @return Sentinel for this object type or null, if Sentinel is not available. This will be returned in
    * getSelectedFirst if there is nothing selected.
    */
   public abstract T getSentinel();

   @Override
   public boolean handleClear() {
      debugLifecycle("handleClear ENTER", "clearOption=%s", widData.is(XOption.CLEAR));
      if (is(XOption.NOT_ENABLED)) {
         return false;
      }
      if (selected.isEmpty()) {
         return false;
      }
      if (widData.is(XOption.CLEAR) && isSelected()) {
         clear(false);
         if (handleSelectedPersist().isValid()) {
            return true;
         }
         refresh();
      }
      return false;
   }

   protected boolean isSelectable() {
      return true;
   }

   protected TransactionToken handleSelectedPersist() {
      long persistStartNanos = System.nanoTime();
      debugLifecycle("handleSelectedPersist ENTER", "attributeStorage=%s relationStorage=%s", isAttributeStorage(),
         isRelationStorage());
      if (isAttributeStorage()) {
         SkynetTransaction tx = TransactionManager.createTransaction(getArtifact().getBranch(), "Set");
         if (selected.isEmpty()) {
            getArtifact().deleteAttributes(getAttributeType());
         } else {
            getArtifact().setAttributeValues(getAttributeType(), selected);
         }
         tx.addArtifact(getArtifact());
         long transactionStartNanos = System.nanoTime();
         TransactionToken txId = tx.execute();
         debugDuration("transaction.execute", transactionStartNanos, "storage=attribute transaction=%s", txId);
         if (txId != null && txId.isValid()) {
            notifyXModifiedListeners();
         }
         // Never return null; execute() returns null when nothing to persist
         TransactionToken result = txId == null ? TransactionToken.SENTINEL : txId;
         debugDuration("handleSelectedPersist EXIT", persistStartNanos, "storage=attribute transaction=%s", result);
         return result;
      } else if (isRelationStorage()) {
         RelationTypeSide relType = getRelationTypeSide();
         SkynetTransaction tx = TransactionManager.createTransaction(getArtifact().getBranch(), "Set " + getLabel());
         if (selected.isEmpty()) {
            getArtifact().deleteRelations(relType);
         } else {
            List<Artifact> relatedArts = new ArrayList<>();
            for (Object obj : selected) {
               if (obj instanceof Artifact) {
                  relatedArts.add((Artifact) obj);
               } else if (obj instanceof HasArtifactToken) {
                  ArtifactToken artTok = ((HasArtifactToken) obj).getArtifactToken();
                  if (artTok instanceof Artifact) {
                     relatedArts.add((Artifact) artTok);
                  } else {
                     Artifact art = ArtifactQuery.getArtifactFromId(artTok, getArtifact().getBranch());
                     if (art != null) {
                        relatedArts.add(art);
                     }
                  }
               } else if (obj instanceof ArtifactToken) {
                  ArtifactToken artTok = (ArtifactToken) obj;
                  Artifact art = ArtifactQuery.getArtifactFromId(artTok, getArtifact().getBranch());
                  if (art != null) {
                     relatedArts.add(art);
                  }
               }
            }
            getArtifact().setRelations(relType, relatedArts);
         }
         tx.addArtifact(getArtifact());
         long transactionStartNanos = System.nanoTime();
         TransactionToken txId = tx.execute();
         debugDuration("transaction.execute", transactionStartNanos, "storage=relation transaction=%s", txId);
         if (txId != null && txId.isValid()) {
            notifyXModifiedListeners();
         }
         // Never return null; execute() returns null when nothing to persist
         TransactionToken result = txId == null ? TransactionToken.SENTINEL : txId;
         debugDuration("handleSelectedPersist EXIT", persistStartNanos, "storage=relation transaction=%s", result);
         return result;
      }
      debugDuration("handleSelectedPersist EXIT", persistStartNanos, "no storage transaction");
      return TransactionToken.SENTINEL;
   }

   public boolean isAttributeStorage() {
      return getArtifact() != null && getAttributeType().isValid();
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   /**
    * Method called pre-handleSelection call to check if other conditions must be met before normal/generic
    * handleSelection call
    *
    * @return error with text why selection is invalid. eg: Team Def must be selected first before Version selection.
    */
   protected XResultData validateHandleSelection() {
      return validateValuesXOption();
   }

   public XResultData validateValuesXOption() {
      int count = 0;
      for (XOption valOpts : XOption.getRequiredValOptions()) {
         if (is(valOpts)) {
            count++;
         }
      }
      if (count <= 0) {
         return new XResultData(XResultType.Error,
            "XWidget must have ONE XOption.VAL_ for [%s]. Currently none. \n\n%s", getLabel(), widData);
      } else if (count > 1) {
         return new XResultData(XResultType.Error, "XWidget must be ONLY one XOption.VAL_ for [%s]\n\n%s", getLabel(),
            widData);
      }
      return XResultData.OK_STATUS;
   }

   /**
    * Should only need to implement if checks over and above isRequired and isSelected are needed. See .isValid() for
    * default checks.
    *
    * @return validator - custom validator if additional checks need to be made, otherwise return
    * XXNoOpValidator.instance;
    */
   protected abstract IValueValidator getValidator();

   @Override
   public XResultData isValid() {
      debugLifecycle("isValid ENTER", "required=%s", isRequiredEntry());
      XResultData status = XWidgetStatus.OK;
      try {
         if (isRequiredEntry()) {
            if (isNotSelected()) {
               return getIsNotValidResult();
            }
            if (isAttributeStorage()) {
               if (!getArtifact().hasAttribute(getAttributeType())) {
                  return getIsNotValidResult();
               }
            }
         }
         // Make a copy so selected isn't changed
         status = getValidator().isValid(new ArrayList<T>(getSelected()));
         if (status.isErrors()) {
            return status;
         }
      } catch (OseeCoreException ex) {
         status = new XResultData(XResultType.Error, "Error Validating XWidget", ex);
      }
      return status;
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   /**
    * Repaint the value control(s) from what is in "selected". This must NOT load data or run expensive operations -
    * that is {@link #initializeSelected()}'s job. Also sets the icon once if not already set.
    * <p>
    * SWT contract - READ BEFORE CHANGING SIZING OR PAINT BEHAVIOR:
    * <ul>
    * <li><b>Short-circuit unchanged values.</b> The editor intentionally calls every widget's {@code refresh()} because
    * one value may affect a derived widget. No SWT text or layout operation occurs when the displayed value is equal.</li>
    * <li><b>Allow real changes to resize.</b> After {@code setText}, call {@link #relayoutForValueChange()} once. Even a
    * non-fill control's preferred width can change, so every actual value change gets the targeted ancestor-chain
    * layout.</li>
    * <li><b>Never force synchronous paint.</b> Do not add {@code update()}; it defeats SWT paint coalescing. A prior
    * implementation also performed a blanket grandparent layout per changed widget. Removing both measurably improved
    * redraw speed.</li>
    * <li><b>Never layout on a no-op refresh.</b> The resolved WFE flicker was caused by same-value
    * {@code Section.setExpanded(...)} calls invalidating the shared form. Content widgets use the text equality guard;
    * structural widgets must similarly detect add/remove/expand changes before layout or reflow.</li>
    * </ul>
    * Use only targeted {@code layout(Control[])} here. If a whole container structurally changes, that container owns
    * one change-aware layout/reflow, as demonstrated by the attachments composite.
    */
   @Override
   public void refresh() {
      long refreshStartNanos = System.nanoTime();
      debugLifecycle("refresh ENTER", "accessible=%s", Widgets.isAccessible(comp));
      if (!Widgets.isAccessible(comp)) {
         debugDuration("refresh EXIT", refreshStartNanos, "inaccessible");
         return;
      }
      if (Widgets.isAccessible(valueWidget)) {
         String currentValue = getDisplayValue();
         if (!currentValue.equals(valueWidget.getText())) {
            debugLifecycle("refresh setText", "control=valueText old=[%s] new=[%s]", valueWidget.getText(),
               currentValue);
            valueWidget.setText(currentValue);
            if (is(XOption.VALUE_WHITE)) {
               valueWidget.setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));
            }
            relayoutForValueChange(valueWidget);
         }
      }
      if (Widgets.isAccessible(valueSText)) {
         String currentValue = getDisplayValue();
         if (!currentValue.equals(valueSText.getText())) {
            debugLifecycle("refresh setText", "control=valueStyledText old=[%s] new=[%s]", valueSText.getText(),
               currentValue);
            valueSText.setText(currentValue);
            relayoutForValueChange(valueSText);
         }
      }
      // Only need to set once
      if (isIconWidget() && Widgets.isAccessible(
         iconLabel) && getWidgetIcon() != null && iconLabel.getImage() == null) {
         iconLabel.setImage(getWidgetIcon());
      }
      validate();
      debugDuration("refresh EXIT", refreshStartNanos, "");
   }

   /**
    * Recomputes this widget's preferred size after an actual displayed-text change. The caller's equality guard ensures
    * this never runs for a no-op editor refresh. The changed leaf control must be passed to {@code layout(Control[])}
    * so SWT flushes its owning layout's cached size before the managed-form reflow updates the ScrolledForm extent.
    */
   private void relayoutForValueChange(Control changedControl) {
      debugLifecycle("relayoutForValueChange", "control=%s fillHorizontal=%s fillVertical=%s",
         changedControl.getClass().getSimpleName(), isFillHorizontally(), isFillVertically());
      if (Widgets.isAccessible(changedControl) && Widgets.isAccessible(comp) && Widgets.isAccessible(comp.getParent())) {
         comp.getParent().layout(new Control[] {changedControl});
      }
      if (isInForm() && isFormReady()) {
         getManagedForm().reflow(true);
      }
   }

   /**
    * Override if default StringLabelProvider is not sufficient for selection dialogs
    */
   @Override
   public ILabelProvider getLabelProvider() {
      if (labelProvider != null) {
         return labelProvider;
      } else {
         return new StringLabelProvider();
      }
   }

   /**
    * Override if default ViewerComparator is not sufficient for selection dialogs
    */
   protected ViewerComparator getComparator() {
      if (widData.getViewerComparator() != null) {
         return (ViewerComparator) widData.getViewerComparator();
      }
      if (getAttributeType().hasDisplayHint(DisplayHint.InOrder)) {
         // Do not sort, use enum as supplied to dialogs
         return NoOpViewerComparator.instance;
      }
      return StringNameComparator.instance;
   }

   @Override
   public Control getControl() {
      /**
       * getControl was created to apply UI changes globally, this is difficult understand and to debug. This method
       * should eventually be removed.
       */
      return comp;
   }

   @Override
   public void dispose() {
      debugLifecycle("dispose ENTER", "");
      super.dispose();
      if (Widgets.isAccessible(valueWidget)) {
         valueWidget.dispose();
      }
      if (Widgets.isAccessible(valueSText)) {
         valueSText.dispose();
      }
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   protected void openWidgetDebug() {
      XResultData rd = new XResultData();
      rd.log(getClass().getSimpleName() + " - openWidgetDebug\n");
      rd.log(widData.toString());
      // Pull VAL_ mode and Read/Write out onto their own lines for easier debugging
      String valOption = "";
      String editMode = "";
      for (XOption opt : widData.getXOptionHandler().getXOptions()) {
         if (opt.name().startsWith("VAL_")) {
            valOption = opt.name();
         } else if (opt == XOption.READ || opt == XOption.WRITE) {
            editMode = opt.name();
         }
      }
      rd.logf("\nVAL_ Mode: %s\n", valOption);
      rd.logf("Read/Write: %s\n", editMode);
      if (getArtifact() != null) {
         rd.logf("\nArtifact: %s\n", getArtifact().toStringWithId());
         ArtifactTypeToken artType = getArtifact().getArtifactType();
         rd.logf("Artifact Type: %s\n", artType.toStringWithId());
         if (getAttributeType().isValid()) {
            rd.logf("Multiplicity: %s (%d..%s)\n", artType.getMultiplicity(getAttributeType()).getName(),
               artType.getMin(getAttributeType()),
               artType.getMax(getAttributeType()) == Integer.MAX_VALUE ? "n" : artType.getMax(getAttributeType()));
         }
      }
      if (debugProvider != null) {
         debugProvider.getWidgetDebug(this, rd);
      }
      XResultDataUI.report(rd, getLabel());
   }

   private boolean controlBeingHeld(MouseEvent event) {
      return ((event.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL);
   }

   /**
    * Called after FilteredTree is created for dialog. Allows treeViewer to be customized. Example: Adding treeViewer
    * information to a LabelProvider.
    */
   public void filteredTreeCreated(FilteredTree treeViewer) {
      // for extension
   }

   /**
    * Opens a single line text dialog if XOption.SINGLE_SELECT and XOption.SINGLE_LINE
    *
    * @return true of value was changed and may need persist, false if cancelled or not changed
    */
   @SuppressWarnings("unchecked")
   private boolean handleSingleLineTextEntry() {
      EntryDialog dialog = new EntryDialog("Enter " + getLabel(), getDialogMessage(), isClearable());
      dialog.setValidator(getValidator());
      if (is(XOption.NO_SPELL)) {
         dialog.setSpellCheck(false);
      }
      String selected = getSelectedFirstStr();
      if (Strings.isValid(selected)) {
         dialog.setEntry(selected);
      }
      dialog.open();
      if (dialog.isOkSelected()) {
         String newValue = dialog.getEntry();
         XResultData valid = getValidator().isValid(newValue);
         if (valid.isErrors()) {
            AWorkbench.popup(valid.toString());
            return false;
         }
         if (Strings.isInvalid(newValue)) {
            AWorkbench.popup("New " + getLabel() + " must be valid string.");
            return false;
         }
         // Single Line Entry only valid for String
         if (getSentinel() instanceof String) {
            setSelected((T) newValue, false);
         }
         handleSelectedPersist();
         return true;
      } else if (dialog.isClearSelected()) {
         return handleClear();
      }
      return false;
   }

   protected boolean isClearable() {
      return is(XOption.CLEAR);
   }

   /**
    * Opens a multi-line text editor when XOption.MULTI_SELECT and XOption.MULTI_LINE. Example: Description or Proposed
    * Resolution
    *
    * @return true of value was changed and may need persist, false if cancelled or not changed
    */
   @SuppressWarnings("unchecked")
   public boolean handleMultiLineTextEntry() {
      EntryDialog dialog = new EntryDialog("View/Update " + getLabel(), "Enter " + getLabel(), isClearable());
      dialog.setFillVertically(true);
      if (is(XOption.NO_SPELL)) {
         dialog.setSpellCheck(false);
      }
      if (getSelectedFirst() instanceof String) {
         dialog.setEntry((String) getSelectedFirst());
      }
      dialog.open();
      if (dialog.isOkSelected()) {
         // MultiLine Entry only valid for String
         if (getSentinel() instanceof String) {
            setSelected((T) dialog.getEntry(), false);
            handleSelectedPersist();
            return true;
         }
      } else if (dialog.isClearSelected()) {
         return handleClear();
      }
      return false;
   }

   /**
    * Opens a single-select filterable dialog given the input getSelectable() when XOption.SINGLE_SELECT and
    * XOption.MULTI_LINE. eg: Priority or Change Type.
    *
    * @return true of value was changed and may need persist, false if cancelled or not changed
    */
   private boolean handleSingleSelectFromSelectable() {

      Collection<T> selectable = getSelectable();
      if (selectable.isEmpty()) {
         AWorkbench.popupf("No selectable [%s(s)] available.", getLabel());
         return false;
      }

      FilteredTreeDialog dialog = new FilteredTreeDialog("Select " + getLabel(), "Select " + getLabel(),
         getContentProvider(), getLabelProvider(), getComparator(), isClearable());
      dialog.setFilteredTreeListener(new FilteredTreeListener() {
         @Override
         public void treeViewerCreated(FilteredTree treeViewer) {
            filteredTreeCreated(treeViewer);
         }
      });
      dialog.setInput(selectable);
      dialog.setSingleSelect();
      if (widData.getViewerComparator() != null) {
         dialog.setComparator(getComparator());
      }
      String descUrl = widData.getDiagDescUrl();
      dialog.setDescUrl(descUrl);
      T defaultSelected = getDefaultSelected();
      if (defaultSelected != null) {
         dialog.setInitialSelections(Arrays.asList(defaultSelected));
      }
      dialog.open();
      if (dialog.isOkSelected()) {
         setSelected(dialog.getSelected(), false);
         handleSelectedPersist();
         return true;
      } else if (dialog.isClearSelected()) {
         handleClear();
         return true;
      }
      return false;
   }

   /**
    * Opens a multi-select filterable dialog given the input getSelectable() when XOption.MULTI_LINE and
    * XOption.MULTI_SELECT. Example: Impacted Partitions or Assignees.
    *
    * @return true of value was changed and may need persist, false if cancelled or not changed
    */
   public boolean handleMultiSelectFromSelectable() {
      FilteredCheckboxTreeDialog<T> dialog = new FilteredCheckboxTreeDialog<T>("Select " + getLabel(),
         "Select " + getLabel(), getContentProvider(), getLabelProvider(), getComparator(), isClearable());
      dialog.setInput(getSelectable());
      dialog.setMultiSelect(true);
      String descUrl = widData.getDiagDescUrl();
      dialog.setDescUrl(descUrl);
      dialog.setFilteredCheckboxTreeListener(new FilteredCheckboxTreeListener() {
         @Override
         public void treeViewerCreated(FilteredCheckboxTree treeViewer) {
            filteredCheckboxTreeCreated(treeViewer);
         }
      });
      if (isSelected()) {
         dialog.setInitialSelections(getSelected());
      } else {
         T defaultSelected = getDefaultSelected();
         if (defaultSelected != null) {
            dialog.setInitialSelections(Arrays.asList(defaultSelected));
         }
      }
      dialog.open();
      if (dialog.isOkSelected()) {
         setSelected(dialog.getChecked(), false);
         handleSelectedPersist();
         return true;
      } else if (dialog.isClearSelected()) {
         handleClear();
         return true;
      }
      return false;
   }

   /**
    * Will be called if XWidget is XOption.VALUE_HYPER and value is clicked.
    */
   protected void handleValueSelection(XWidget xWidget) {
      // for subclass
   }

   protected boolean isIconWidget() {
      return widData.is(XOption.ICON_WIDGET) || widData.getOseeImage() != null;
   }

   protected Image getWidgetIcon() {
      if (getOseeImage() != null) {
         return ImageManager.getImage(getOseeImage());
      }
      return null;
   }

   /**
    * Called after FilteredCheckboxTree is created for dialog. Allows treeViewer to be customized. Example: Adding
    * treeViewer information to a LabelProvider.
    */
   public void filteredCheckboxTreeCreated(FilteredCheckboxTree treeViewer) {
      // for extension
   }

   /**
    * Override if default ContentProvider is not sufficient for selection dialogs
    */
   public IContentProvider getContentProvider() {
      return new ArrayTreeContentProvider();
   }

   protected String getDialogMessage() {
      return "Enter " + getLabel();
   }

   private String getSelectedFirstStr() {
      if (isSelected()) {
         return String.valueOf(getSelectedFirst());
      }
      return null;
   }

   public void removeSelected(T obj) {
      selected.remove(obj);
   }

   /**
    * @throws OseeArgumentException if doesn't match generic type.
    */
   public void matchTypeOrException(Collection<Object> objects) {
      for (Object obj : objects) {
         matchTypeOrException(obj);
      }
   }

   /**
    * @throws OseeArgumentException if doesn't match generic type.
    */
   @SuppressWarnings("unchecked")
   public void matchTypeOrException(Object obj) {
      Conditions.assertNotNull(obj, "Set object can not be null");
      if (obj instanceof Collection) {
         matchTypeOrException((Collection<Object>) obj);
      } else if (!isOfType(obj)) {
         // Note: XXWidget probably needs to implement isOfType
         throw new OseeArgumentException("Widget[%s].setSelected() labeled [%s]" //
            + " expected widget type [%s] but was [%s].  " //
            + "(Either implement Widget.isOfType or set with correct type)", //
            getClass().getSimpleName(), getLabel(),
            (getSentinel() == null ? "null sentinel" : getSentinel().getClass().getSimpleName()), //
            obj.getClass().getSimpleName());
      }
   }

   // Override to provide default
   public T getDefaultSelected() {
      return null;
   }

   /**
    * Returns first selected or Sentinel, if available, for cases where there's only one value expected.
    */
   public T getSelectedFirst() {
      if (selected.isEmpty()) {
         return getSentinel();
      }
      return selected.iterator().next();
   }

   public boolean hasSelectable() {
      return !getSelectable().isEmpty();
   }

   public boolean isNotSelected() {
      return selected.isEmpty();
   }

   @Override
   public boolean isSelected() {
      return !selected.isEmpty();
   }

   @Override
   public Object getData() {
      if (isValueMultiSelectFromSelectable()) {
         return selected;
      }
      return selected.isEmpty() ? null : selected.iterator().next();
   }

   /**
    * This should NOT normally be overridden; selected should be set/reset during initializeSelected()
    */
   public Collection<T> getSelected() {
      matchTypeOrException(selected);
      return selected;
   }

   /**
    * Set the selectable options for list dialogs. This shouldn't be called in most cases. Override getSelectable() so
    * the values are only loaded if the user decides to modify the widget.
    */
   public void setSelectable(Collection<T> selectable) {
      debugLifecycle("setSelectable", "incoming=%s", selectable);
      this.selectable.clear();
      matchTypeOrException(selectable);
      this.selectable.addAll(selectable);
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelValueStr(AHTML.LABEL_FONT, getLabel(), getDisplayValue());
   }

   /**
    * Adapting controls does unexpected UI things. DO NOT ADAPT CONTROLS all changes should be made by the widgets
    * directly and not globally.
    */
   @Override
   @Deprecated
   public void adaptControls(FormToolkit toolkit) {
      // for subclass
   }

   /**
    * Adds the listener to whichever label control exists: the hyperlink (editable) or the plain label (read-only).
    */
   public void addLabelWidgetListener(MouseListener listener) {
      if (Widgets.isAccessible(labelLinkWidget)) {
         labelLinkWidget.addMouseListener(listener);
      } else if (Widgets.isAccessible(labelWidget)) {
         labelWidget.addMouseListener(listener);
      }
   }

   @Override
   public boolean isValueSingleSelectFromSelectable() {
      if (isAttributeStorage()) {
         Multiplicity multiplicity = getArtifact().getArtifactType().getMultiplicity(getAttributeType());
         if (multiplicity.isValid()) {
            return multiplicity.matches(Multiplicity.EXACTLY_ONE, Multiplicity.ZERO_OR_ONE);
         }
      }
      return super.isValueSingleSelectFromSelectable();
   }

   @Override
   public boolean isValueMultiSelectFromSelectable() {
      if (isAttributeStorage()) {
         Multiplicity multiplicity = getArtifact().getArtifactType().getMultiplicity(getAttributeType());
         if (multiplicity.isValid()) {
            return multiplicity.matches(Multiplicity.ANY) || multiplicity.matches(Multiplicity.AT_LEAST_ONE);
         }
      }
      return super.isValueMultiSelectFromSelectable();
   }

   @Override
   public void validate() {
      long validateStartNanos = System.nanoTime();
      debugLifecycle("validate ENTER", "formReady=%s inForm=%s conditions=%s", isFormReady(), isInForm(),
         getConditions().size());
      Control control = getControl();
      if (!Widgets.isAccessible(control)) {
         debugDuration("validate EXIT", validateStartNanos, "inaccessible");
         return;
      }
      if (isFormReady()) {
         XResultData status = isValid();
         debugLifecycle("validate status", "ok=%s errors=%s status=[%s]", status.isOK(), status.isErrors(), status);
         if (isInForm()) {
            XWidgetUtility.setStatus(status, this);
         }
      }
      if (Widgets.isAccessible(labelWidget)) {
         if (isTooltip() && helpLabelListener == null) {
            helpLabelListener = new MouseAdapter() {
               @Override
               public void mouseDoubleClick(MouseEvent e) {
                  openHelp();
               }
            };
            labelWidget.addMouseListener(helpLabelListener);
         }
      }
      if (Widgets.isAccessible(control) && getConditions().size() > 0) {
         for (ConditionalRule rule : getConditions()) {
            if (rule instanceof EnableIfAttrValueCondition) {
               if (rule.isDisabled((isAttributeStorage() ? getArtifact().getAttributesToStringList(
                  ((EnableIfAttrValueCondition) rule).getAttrType()) : Collections.emptyList()))) {
                  setEnabled(false);
                  setEditable(false);
                  break;
               } else {
                  setEnabled(true);
                  setEditable(true);
               }
            }
         }
      }
      debugDuration("validate EXIT", validateStartNanos, "");
   }

   /**
    * @param swtColor is SWT.COLOR_RED or SWT.NONE to restore default color
    */
   public void setValueFgColor(int swtColor) {
      if (SWT.NONE == swtColor) {
         if (Widgets.isAccessible(getValueWidget())) {
            getValueWidget().setForeground(null);
         }
      } else {
         if (Widgets.isAccessible(getValueWidget())) {
            getValueWidget().setForeground(Displays.getSystemColor(swtColor));
         }
      }
   }

   /**
    * @param swtColor is SWT.COLOR_RED or SWT.NONE to restore default color
    */
   public void setLabelFgColor(int swtColor) {
      if (SWT.NONE == swtColor) {
         if (Widgets.isAccessible(getLabelWidget())) {
            getLabelWidget().setForeground(null);
         }
         if (Widgets.isAccessible(getLabelLinkWidget())) {
            getLabelLinkWidget().setForeground(null);
         }
      } else {
         Color color = Displays.getSystemColor(swtColor);
         if (Widgets.isAccessible(getLabelWidget())) {
            getLabelWidget().setForeground(color);
         }
         if (Widgets.isAccessible(getLabelLinkWidget())) {
            getLabelLinkWidget().setForeground(color);
         }
      }
   }

   public void setLabelFont(Font font) {
      if (Widgets.isAccessible(getLabelWidget())) {
         getLabelWidget().setFont(font);
      }
      if (Widgets.isAccessible(getLabelLinkWidget())) {
         getLabelLinkWidget().setFont(font);
      }
   }

   public Text getValueWidget() {
      return valueWidget;
   }

   public boolean isRelationStorage() {
      return getArtifact() != null && getRelationTypeSide() != null && getRelationTypeSide().isValid();
   }

   public void setEnabled(boolean enabled) {
      debugLifecycle("setEnabled", "enabled=%s previous=%s", enabled, isEnabled());
      set(enabled ? XOption.ENABLED : XOption.NOT_ENABLED);
      if (Widgets.isAccessible(comp)) {
         comp.setEnabled(enabled);
      }
      Control label = getLabelControl();
      if (Widgets.isAccessible(label)) {
         if (enabled) {
            label.setForeground(labelFgBeforeDisable);
            labelFgBeforeDisable = null;
         } else {
            labelFgBeforeDisable = label.getForeground();
            label.setEnabled(true);
            label.setForeground(Displays.getSystemColor(SWT.COLOR_DARK_GRAY));
         }
      }
   }

   private Control getLabelControl() {
      if (Widgets.isAccessible(getLabelLinkWidget())) {
         return getLabelLinkWidget();
      }
      if (Widgets.isAccessible(getLabelWidget())) {
         return getLabelWidget();
      }
      return null;
   }

   public boolean isEnabled() {
      return is(XOption.ENABLED);
   }

   public WidgetDebugProvider getDebugProvider() {
      return debugProvider;
   }

   public void setDebugProvider(WidgetDebugProvider debugProvider) {
      this.debugProvider = debugProvider;
   }

   public XWidgetData getOrigWidData() {
      return origWidData;
   }

   public void setOrigWidData(XWidgetData origWidData) {
      this.origWidData = origWidData;
   }

   public XWidgetData getIncomingWidData() {
      return incomingWidData;
   }

   public void setIncomingWidData(XWidgetData incomingWidData) {
      this.incomingWidData = incomingWidData;
   }

   public Composite getComp() {
      return comp;
   }

   ////////////////////////////////////////////
   // Lifecycle Debugging
   ////////////////////////////////////////////

   /** Delegates lifecycle logging to {@link DebugLifecycle}; see that class for modes and the analysis workflow. */
   private void debugLifecycle(String operation, String detail, Object... data) {
      DebugLifecycle.log(this, selected, operation, detail, data);
   }

   private void debugDuration(String operation, long startNanos, String detail, Object... data) {
      DebugLifecycle.logDuration(this, startNanos, operation, detail, data);
   }

   private void debugControlEvents(Control control, String role) {
      DebugLifecycle.addControlEvents(this, selected, control, role);
   }

   ////////////////////////////////////////////
   // ToolTips for Label and Value
   ////////////////////////////////////////////

   protected void setValueToolTip(String tooltip) {
      if (Widgets.isAccessible(valueWidget)) {
         valueWidget.setToolTipText(tooltip);
      } else if (Widgets.isAccessible(valueSText)) {
         valueSText.setToolTipText(tooltip);
      }
   }

   private void setLabelToolTip() {
      String toolTip = getToolTip();
      if (isEditable()) {
         String selectMsg = getSelectToModifyMsg();
         if (Strings.isValid(toolTip)) {
            if (Strings.isValid(selectMsg)) {
               toolTip = selectMsg + "\n\n" + toolTip;
            }
         } else {
            toolTip = selectMsg;
         }
      } else {
         if (Strings.isValid(toolTip)) {
            toolTip = READ_ONLY + "\n\n" + toolTip;
         } else {
            toolTip = READ_ONLY;
         }
      }
      setLabelToolTip(toolTip);
      setValueToolTip(toolTip);
   }

   protected String getSelectToModifyMsg() {
      return SELECT_TO_MODIFY;
   }

   private void setLabelToolTip(String toolTip) {
      if (Strings.isValid(toolTip)) {
         if (Widgets.isAccessible(labelLinkWidget)) {
            labelLinkWidget.setToolTipText(toolTip);
         } else if (Widgets.isAccessible(labelWidget)) {
            labelWidget.setToolTipText(toolTip);
         }
      }
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   /**
    * Called 3 times to populate "selected"<br>
    * 1) After widgets are created and before refresh is called which updates value widgets<br>
    * 2) After setArtifact so "selected" can be set properly and then refresh called<br>
    * 3) Upon artifact update / reload TBD<br>
    * <br>
    * This should be implemented instead of extending getSelected to set selected. and setArtifact should also call this
    * not set selected itself.
    */
   protected void initializeSelected() {
      debugLifecycle("initializeSelected ENTER", "xxArt=%s attrType=%s", xxArt.isArtifact(), getAttributeType());
      if (xxArt.isArtifact() && getAttributeType().isValid()) {
         setSelected(getArtifact().getAttributeValues(getAttributeType()), false);
      }
      debugLifecycle("initializeSelected EXIT", "");
   }

   @Override
   public void setArtifact(Artifact artifact) {
      debugLifecycle("setArtifact ENTER", "artifact=%s", artifact);
      super.setArtifact(artifact);
      initializeSelected();
      refresh();
      debugLifecycle("setArtifact EXIT", "artifact=%s", artifact);
   }

}
