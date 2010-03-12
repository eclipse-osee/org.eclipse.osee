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
import java.util.Iterator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Ryan D. Brooks
 */
public class XListDropViewer extends XListViewer {
   private MenuItem removeFromMenuItem;
   private TableViewer myTableViewer;
   private ArrayContentProvider myArrayContentProvider = null;
   private ArtifactLabelProvider myArtifactLabelProvider = null;

   /**
    * @param displayLabel
    */
   public XListDropViewer(String displayLabel) {
      super(displayLabel);
      this.myArrayContentProvider = new ArrayContentProvider();
      setContentProvider(this.myArrayContentProvider);
      this.myArtifactLabelProvider = new ArtifactLabelProvider();
      setLabelProvider(this.myArtifactLabelProvider);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      Menu popupMenu = new Menu(parent);
      setMultiSelect(true);
      super.setListMenu(popupMenu);
      super.createControls(parent, horizontalSpan);
      new XDragAndDrop();
      this.myTableViewer = super.getTableViewer();
      //      popupMenu.addMenuListener(new MenuEnablingListener());
      createRemoveFromMenuItem(popupMenu);
      myTableViewer.getTable().setMenu(popupMenu);
   }

   private void createRemoveFromMenuItem(Menu popupMenu) {
      removeFromMenuItem = new MenuItem(popupMenu, SWT.PUSH);
      removeFromMenuItem.setText("Remove From This Blam's Parameters ");
      removeFromMenuItem.addSelectionListener(new SelectionListener() {

         public void widgetSelected(SelectionEvent event) {
            IStructuredSelection structuredSelection = (IStructuredSelection) myTableViewer.getSelection();
            Iterator<?> iterator = structuredSelection.iterator();

            Object orginalInput = getInput();
            Collection<Object> modList = getCollectionInput();

            while (iterator.hasNext()) {
               modList.remove(iterator.next());
            }

            myArrayContentProvider.inputChanged(myTableViewer, orginalInput, modList);
            refresh();
         }

         public void widgetDefaultSelected(SelectionEvent ev) {
         }
      });
   }

   /**
    * Adds artifacts to the viewer's input.
    * 
    * @param artifacts
    */
   public void addToInput(Artifact... artifacts) {
      ArrayList<Object> objects = new ArrayList<Object>();

      for (Artifact artifact : artifacts) {
         objects.add((Object) artifact);
      }

      if (getInput() == null) {
         setInput(objects);
      } else {
         add(objects);
         updateListWidget();
      }
      notifyXModifiedListeners();
   }

   @Override
   public Object getData() {
      return getInput();
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
      }
   }
}
