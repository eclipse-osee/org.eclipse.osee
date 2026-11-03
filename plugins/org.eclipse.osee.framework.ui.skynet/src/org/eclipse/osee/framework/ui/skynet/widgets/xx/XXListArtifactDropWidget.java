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
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.ClientLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.blam.operation.StringGuidsToArtifactListOperation;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.IXWidgetInputAddable;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXListArtifactDropWidget extends XXAbstractBaseXWidget<Artifact> implements IXWidgetInputAddable {

   public static final WidgetId ID = WidgetId.XXListArtifactDropWidget;

   private Table table;
   private final ArtifactLabelProvider artLabelProvider = new ArtifactLabelProvider();

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXListArtifactDropWidget() {
      this(ID, "Artifacts");
   }

   public XXListArtifactDropWidget(String label) {
      this(ID, label);
   }

   public XXListArtifactDropWidget(WidgetId widgetId, String label) {
      super(widgetId, label, new ArtifactLabelProvider());
      set(XOption.VAL_MULTI_TEXT, XOption.INLINE_EDIT, XOption.CLEAR);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      comp = new Composite(parent, SWT.NONE);
      GridLayout layout = new GridLayout(1, false);
      layout.marginHeight = 1;
      comp.setLayout(layout);
      GridData compGd = new GridData(SWT.FILL, SWT.FILL, true, true);
      compGd.horizontalSpan = horizontalSpan;
      comp.setLayoutData(compGd);
      if (getToolkit() != null) {
         getToolkit().adapt(comp);
      }

      createLabelWidget();

      table = new Table(comp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
      GridData tableGd = new GridData(SWT.FILL, SWT.FILL, true, true);
      tableGd.heightHint = 100;
      table.setLayoutData(tableGd);

      // Right-click menu
      createTableRightClickMenu();

      // Drag and drop
      new ListDragAndDrop();

      // Populate table from any pre-set selected items
      refreshTable();
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public Artifact getSentinel() {
      return Artifact.SENTINEL;
   }

   /** Adds artifacts to the current selection and refreshes the table. */
   public void addToInput(Artifact... artifacts) {
      List<Object> objects = new ArrayList<>();
      for (Artifact art : artifacts) {
         objects.add(art);
      }
      addToInput(objects);
   }

   @Override
   public void addToInput(final Collection<Object> objects) {
      if (!objects.isEmpty()) {
         Displays.ensureInDisplayThread(() -> {
            for (Object obj : objects) {
               if (obj instanceof Artifact && !getSelected().contains(obj)) {
                  getSelected().add((Artifact) obj);
               }
            }
            refreshTable();
            notifyXModifiedListeners();
         });
      }
   }

   /** Convenience method to get selected items cast to {@link Artifact}. */
   public List<Artifact> getSelectedArtifacts() {
      return Collections.castAll(Artifact.class, new ArrayList<>(getSelected()));
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof Artifact;
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
      refreshTable();
      validate();
   }

   @Override
   public Control getControl() {
      return table;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   private void createTableRightClickMenu() {
      Menu menu = new Menu(table);
      table.setMenu(menu);

      MenuItem removeItem = new MenuItem(menu, SWT.PUSH);
      removeItem.setText("Remove");
      removeItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            removeSelectedFromTable();
         }
      });

      MenuItem pasteItem = new MenuItem(menu, SWT.PUSH);
      pasteItem.setText("Paste GUIDs from Clipboard");
      pasteItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            pasteFromClipboard();
         }
      });

      // Remove button below the table
      Button removeBtn = new Button(comp, SWT.PUSH);
      removeBtn.setText("Remove Selected");
      removeBtn.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            removeSelectedFromTable();
         }
      });
   }

   @Override
   public Object getData() {
      return super.getSelected();
   }

   private void refreshTable() {
      if (table == null || table.isDisposed()) {
         return;
      }
      table.removeAll();
      for (ArtifactToken token : getSelected()) {
         TableItem item = new TableItem(table, SWT.NONE);
         item.setText(artLabelProvider.getText(token));
         item.setData(token);
      }
   }

   private void removeSelectedFromTable() {
      if (table == null || table.isDisposed()) {
         return;
      }
      int[] indices = table.getSelectionIndices();
      List<ArtifactToken> toRemove = new ArrayList<>();
      for (int idx : indices) {
         toRemove.add((ArtifactToken) table.getItem(idx).getData());
      }
      Collection<Artifact> selected = getSelected();
      selected.removeAll(toRemove);
      refreshTable();
      notifyXModifiedListeners();
   }

   private void pasteFromClipboard() {
      Displays.ensureInDisplayThread(() -> {
         Clipboard cb = new Clipboard(table.getDisplay());
         try {
            String data = (String) cb.getContents(TextTransfer.getInstance());
            if (data != null) {
               BranchId branch = BranchSelectionDialog.getBranchFromUser();
               Operations.executeAsJob(
                  new StringGuidsToArtifactListOperation(new ClientLogger(Activator.class), data, branch, this), true);
            }
         } finally {
            cb.dispose();
         }
      });
   }

   private class ListDragAndDrop extends SkynetDragAndDrop {

      public ListDragAndDrop() {
         super(null, table, "viewId");
      }

      @Override
      public void performDragOver(DropTargetEvent event) {
         if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {
            event.detail = DND.DROP_COPY;
         }
      }

      @Override
      public Artifact[] getArtifacts() {
         return null;
      }

      @Override
      public void performArtifactDrop(Artifact[] dropArtifacts) {
         addToInput(dropArtifacts);
      }
   }

}
