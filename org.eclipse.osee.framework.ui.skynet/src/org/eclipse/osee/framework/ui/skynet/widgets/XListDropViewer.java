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
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Ryan D. Brooks
 */
public class XListDropViewer extends XListViewer {

   /**
    * @param displayLabel
    */
   public XListDropViewer(String displayLabel) {
      super(displayLabel);
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new ArtifactLabelProvider());
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
   public void createWidgets(Composite parent, int horizontalSpan) {
      super.createWidgets(parent, horizontalSpan);

      // the viewer must be initialized first so the control is not null.
      new XDragAndDrop();
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
