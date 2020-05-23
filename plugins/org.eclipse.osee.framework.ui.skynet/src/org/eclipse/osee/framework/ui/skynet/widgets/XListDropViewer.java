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
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.operation.ClientLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.blam.operation.StringGuidsToArtifactListOperation;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Ryan D. Brooks
 */
public class XListDropViewer extends XListViewer implements IXWidgetInputAddable {
   private MenuItem removeFromMenuItem;
   private TableViewer myTableViewer;
   private ArrayContentProvider myArrayContentProvider = null;
   private ArtifactLabelProvider myArtifactLabelProvider = null;

   private Menu popupMenu;

   protected boolean singleItemMode;

   public XListDropViewer(String displayLabel) {
      super(displayLabel);
      this.myArrayContentProvider = new ArrayContentProvider();
      setContentProvider(this.myArrayContentProvider);
      this.myArtifactLabelProvider = new ArtifactLabelProvider();
      setLabelProvider(this.myArtifactLabelProvider);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      popupMenu = new Menu(parent);
      setMultiSelect(true);
      super.setListMenu(popupMenu);
      super.createControls(parent, horizontalSpan);
      new XDragAndDrop();
      this.myTableViewer = super.getTableViewer();
      createRemoveFromMenuItem(popupMenu);
      myTableViewer.getTable().setMenu(popupMenu);
   }

   private void createRemoveFromMenuItem(final Menu popupMenu) {
      removeFromMenuItem = new MenuItem(popupMenu, SWT.PUSH);
      removeFromMenuItem.setText("Remove");
      removeFromMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent event) {
            IStructuredSelection structuredSelection = (IStructuredSelection) myTableViewer.getSelection();
            Iterator<?> iterator = structuredSelection.iterator();

            Collection<Object> items = getCollectionInput();
            if (items != null && !items.isEmpty()) {
               List<Object> modList = new ArrayList<>();

               while (iterator.hasNext()) {
                  modList.remove(iterator.next());
               }

               Object orginalInput = getInput();
               myArrayContentProvider.inputChanged(myTableViewer, orginalInput, modList);
               setInput(modList);
               notifyXModifiedListeners();
               refresh();
            }
         }
      });

      MenuItem paste = new MenuItem(popupMenu, SWT.NONE);
      paste.setText("Paste GUIDs from Clipboard");
      paste.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            Displays.ensureInDisplayThread(new Runnable() {

               @Override
               public void run() {
                  Clipboard cb = new Clipboard(popupMenu.getDisplay());
                  try {
                     TextTransfer transfer = TextTransfer.getInstance();
                     String data = (String) cb.getContents(transfer);
                     BranchId branch = BranchSelectionDialog.getBranchFromUser();
                     Operations.executeAsJob(new StringGuidsToArtifactListOperation(new ClientLogger(Activator.class),
                        data, branch, XListDropViewer.this), true);
                  } finally {
                     cb.dispose();
                  }
               }
            });

         }
      });
   }

   public List<Artifact> getArtifacts() {
      return Collections.castAll(getData());
   }

   public List<Artifact> getSelectedArtifacts() {
      List<Object> selectedArtifacts = new ArrayList<>();
      Collections.flatten(getSelected(), selectedArtifacts);
      return Collections.castAll(Artifact.class, selectedArtifacts);
   }

   /**
    * Adds artifacts to the viewer's input.
    */
   public void addToInput(Artifact... artifacts) {
      List<Object> objects = new ArrayList<>();
      for (Artifact artifact : artifacts) {
         objects.add(artifact);
      }
      addToInput(objects);
   }

   @Override
   public void addToInput(final Collection<Object> objects) {
      if (!objects.isEmpty()) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               if (getInput() == null) {
                  setInput(objects);
               } else {
                  add(objects);
                  updateListWidget();
               }
               notifyXModifiedListeners();
            }
         });
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public Collection<Object> getData() {
      return (Collection<Object>) getInput();
   }

   private class XDragAndDrop extends SkynetDragAndDrop {

      public XDragAndDrop() {
         super(null, getControl(), "viewId");
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
         if (XListDropViewer.this.singleItemMode && dropArtifacts.length > 1) {
            setMessage(XListDropViewer.class.getSimpleName(), String.format("Only 1 [%s] can be present", getLabel()),
               IMessageProvider.ERROR);
         }
         setSelected(dropArtifacts);
         notifyXModifiedListeners();
         refresh();
      }
   }
}
