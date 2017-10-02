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
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.ui.skynet.HTMLTransferFormatter;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.HTMLTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

/**
 * @author Jeff C. Phillips
 */
public abstract class SkynetDragAndDrop {
   private final String viewId;
   private DragSource source;
   private DropTarget target;

   public SkynetDragAndDrop(Control dragAndDropControl, String viewId) {
      this(dragAndDropControl, dragAndDropControl, viewId);
   }

   /**
    * Caller may optionally pass null for either the dragSource or dropTarget when both are not needed
    */
   public SkynetDragAndDrop(Control dragSource, Control dropTarget, String viewId) {
      this.viewId = viewId;
      if (dragSource != null) {
         source = new DragSource(dragSource, DND.DROP_MOVE | DND.DROP_COPY);
         setupDragSupport();
      }
      if (dropTarget != null) {
         target = new DropTarget(dropTarget, DND.DROP_MOVE | DND.DROP_COPY);
         setupDropSupport();
      }
   }

   private void setupDragSupport() {
      source.setTransfer(
         new Transfer[] {HTMLTransfer.getInstance(), ArtifactTransfer.getInstance(), TextTransfer.getInstance()});
      source.addDragListener(new DragSourceListener() {

         @Override
         public void dragFinished(DragSourceEvent event) {
            // do nothing
         }

         @Override
         public void dragSetData(DragSourceEvent event) {
            performDataTransafer(event);
         }

         @Override
         public void dragStart(DragSourceEvent event) {
            // do nothing
         }
      });
   }

   private void performDataTransafer(DragSourceEvent event) {
      if (HTMLTransfer.getInstance().isSupportedType(event.dataType)) {
         htmlTransferDragSetData(event);
      } else if (ArtifactTransfer.getInstance().isSupportedType(event.dataType)) {
         artifactTransferDragSetData(event);
      } else if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
         textTransferDragSetData(event);
      }
   }

   private void setupDropSupport() {
      target.setTransfer(
         new Transfer[] {FileTransfer.getInstance(), TextTransfer.getInstance(), ArtifactTransfer.getInstance()});
      target.addDropListener(new DropTargetAdapter() {

         @Override
         public void dragOperationChanged(DropTargetEvent event) {
            operationChanged(event);
         }

         @Override
         public void drop(DropTargetEvent event) {
            performDrop(event);
         }

         @Override
         public void dragOver(DropTargetEvent event) {
            try {
               performDragOver(event);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }

         @Override
         public void dropAccept(DropTargetEvent event) {
            // do nothing
         }
      });
   }

   public void performDragOver(DropTargetEvent event)  {
      // provided for subclass implementation
   }

   public void artifactTransferDragSetData(DragSourceEvent event) {
      try {
         if (getArtifacts() != null && getArtifacts().length > 0) {
            event.data = new ArtifactData(getArtifacts(), "work", viewId);
         }
      } catch (Exception ex) {
         //         OSEELog.logException(ChangeReportView.class, ex, true);
      }
   }

   public void htmlTransferDragSetData(DragSourceEvent event) {
      try {
         if (getArtifacts() != null && getArtifacts().length > 0) {
            event.data = HTMLTransferFormatter.getHtml(getArtifacts());
         }
      } catch (Exception ex) {
         //         OSEELog.logException(ChangeReportView.class, ex, true);
      }
   }

   public void textTransferDragSetData(DragSourceEvent event) {
      try {
         if (getArtifacts() != null && getArtifacts().length > 0) {
            Artifact[] artifacts = getArtifacts();
            Collection<String> names = new ArrayList<>(artifacts.length);

            for (Artifact artifact : artifacts) {
               names.add(artifact.getName());
            }

            event.data = Collections.toString(names, null, ", ", null);
         }
      } catch (Exception ex) {
         //         OSEELog.logException(ChangeReportView.class, ex, true);
      }
   }

   /**
    * Override this method to supply the base class with artifacts to be used for drag and drop.
    */
   public abstract Artifact[] getArtifacts() throws Exception;

   /**
    * Override this method to implement the drop operation.
    */
   public void performDrop(DropTargetEvent event) {
      if (event.data instanceof ArtifactData) {
         performArtifactDrop(((ArtifactData) event.data).getArtifacts());
      } else if (event.data instanceof String[]) {
         performFileDrop((String[]) event.data);
      } else if (event.data instanceof String) {
         performTextDrop((String) event.data);
      }
   }

   public void operationChanged(DropTargetEvent event) {
      // provided for subclass implementation
   }

   /**
    * override this method and its cousins rather than performDrop in order to have the drop data preprocessed and
    * passed in the desired form
    */
   public void performTextDrop(String text) {
      // provided for subclass implementation
   }

   /**
    * override this method and its cousins rather than performDrop in order to have the drop data preprocessed and
    * passed in the desired form
    */
   public void performArtifactDrop(Artifact[] dropArtifacts) {
      // provided for subclass implementation
   }

   /**
    * override this method and its cousins rather than performDrop in order to have the drop data preprocessed and
    * passed in the desired form
    */
   public void performFileDrop(String[] fileNames) {
      // provided for subclass implementation
   }
}
