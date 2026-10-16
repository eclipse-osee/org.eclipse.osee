/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXListWidget extends XXAbstractBaseXWidget<String> {

   public static final WidgetId ID = WidgetId.XXListWidget;

   private List swtList;
   private ScrolledComposite scrolledComposite;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXListWidget() {
      this(ID, "");
   }

   public XXListWidget(String label) {
      this(ID, label);
   }

   public XXListWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
      set(XOption.VAL_MULTI_SEL);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   // ------------------------------------------------------------------
   // UI construction
   // ------------------------------------------------------------------
   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      if (!isVerticalLabel() && horizontalSpan < 2) {
         horizontalSpan = 2;
      }
      comp = new Composite(parent, SWT.NONE);
      comp.setLayout(new GridLayout(1, false));
      GridData compGd = new GridData(SWT.FILL, SWT.FILL, true, true);
      compGd.horizontalSpan = horizontalSpan;
      comp.setLayoutData(compGd);
      if (getToolkit() != null) {
         getToolkit().adapt(comp);
      }
      createLabelWidget();
      // Border goes on the ScrolledComposite (the fixed viewport) so it frames the visible area and stays put. If the
      // border were on the scrolling List content instead, its top/bottom edges would scroll out of view.
      scrolledComposite = new ScrolledComposite(comp, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      scrolledComposite.setExpandHorizontal(true);
      scrolledComposite.setExpandVertical(true);
      GridData scrollGd = new GridData(SWT.FILL, SWT.FILL, true, true);
      scrollGd.heightHint = 60;
      scrolledComposite.setLayoutData(scrollGd);
      swtList = new List(scrolledComposite, SWT.MULTI);
      scrolledComposite.setContent(swtList);
      scrolledComposite.addListener(SWT.Resize, new Listener() {
         @Override
         public void handleEvent(Event event) {
            Point size = swtList.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
            scrolledComposite.setMinSize(size);
         }
      });
      swtList.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            syncSelectionFromList();
            notifyXModifiedListeners();
         }
      });
      swtList.setEnabled(isEditable());
      // Honor the base createControls() contract (load then paint). This widget fully overrides createControls(), so
      // it must run the load (initializeSelected) and paint (refresh -> populateList) tail itself; otherwise
      // relation/attribute-backed subclasses that load in initializeSelected() would never populate.
      initializeSelected();
      refresh();
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public String getSentinel() {
      return "";
   }

   @Override
   protected boolean handleSelection() {
      // Selection is handled directly by the SWT List control
      return false;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   @Override
   public void refresh() {
      populateList();
      validate();
   }

   @Override
   public Control getControl() {
      return swtList;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   // ------------------------------------------------------------------
   // List population and sync
   // ------------------------------------------------------------------
   /** Rebuilds the SWT List from selectable items and restores selection state. */
   private void populateList() {
      if (swtList == null || swtList.isDisposed()) {
         return;
      }
      swtList.removeAll();
      java.util.List<String> sortedItems = new ArrayList<>(getSelectable());
      java.util.Collections.sort(sortedItems);
      for (String item : sortedItems) {
         swtList.add(item);
      }
      // Restore selection
      Set<String> selectedNames = new HashSet<>();
      for (String s : getSelected()) {
         selectedNames.add(s);
      }
      for (int i = 0; i < swtList.getItemCount(); i++) {
         if (selectedNames.contains(swtList.getItem(i))) {
            swtList.select(i);
         }
      }
   }

   /** Reads the SWT List selection and updates the {@code selected} collection. */
   private void syncSelectionFromList() {
      if (swtList == null || swtList.isDisposed()) {
         return;
      }
      java.util.List<String> strs = new ArrayList<String>();
      for (String sel : swtList.getSelection()) {
         strs.add(sel);
      }
      setSelected(strs, false);
   }

   /** Exposes the base class setArtifact for subclasses that need to bypass attribute-based population. */
   protected void superSetArtifact(Artifact artifact) {
      super.setArtifact(artifact);
   }

   @Override
   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      if (Widgets.isAccessible(swtList)) {
         swtList.setEnabled(enabled);
      }
   }

   /** @return the underlying SWT List control */
   public List getSwtList() {
      return swtList;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   protected void initializeSelected() {
      if (xxArt.isArtifact() && getAttributeType().isValid()) {
         Collection<String> attrValues = getArtifact().getAttributesToStringList(getAttributeType());
         setSelected(new ArrayList<>(attrValues), false);
      }
   }

}
