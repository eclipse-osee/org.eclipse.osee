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
package org.eclipse.osee.doors.connector.ui.viewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.doors.connector.core.Requirement;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.skynet.core.utility.JsonArtifactRepresentation;
import org.eclipse.osee.framework.skynet.core.utility.JsonAttributeRepresentation;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;

/**
 * @author David W. Miller
 */
public class RdfExplorerDragAndDrop {
   private DragSource dragSource;

   private final List<Requirement> reqs = new ArrayList<>();

   public RdfExplorerDragAndDrop(Control source) {

      if (source != null) {
         dragSource = new DragSource(source, DND.DROP_MOVE | DND.DROP_COPY);
         setupDragSupport();
      }
   }

   private void setupDragSupport() {
      dragSource.setTransfer(new Transfer[] {TextTransfer.getInstance()});
      dragSource.addDragListener(new DragSourceListener() {

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
      if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
         textTransferDragSetData(event);
      }
   }

   public void textTransferDragSetData(DragSourceEvent event) {
      List<JsonArtifactRepresentation> reqsOut = new ArrayList<>();
      for (Requirement reqt : reqs) {
         reqsOut.add(makeJsonArtifactRepresentation(reqt));
      }

      try {
         event.data = JsonUtil.getMapper().writerWithDefaultPrettyPrinter().writeValueAsString(reqsOut);
      } catch (IOException ex) {
         //
      }
   }

   private JsonArtifactRepresentation makeJsonArtifactRepresentation(Requirement reqt) {
      JsonArtifactRepresentation artRep = new JsonArtifactRepresentation();
      artRep.setArtifactTypeId(CoreArtifactTypes.Url.getId());
      artRep.setName(reqt.getShortName());
      List<JsonAttributeRepresentation> attrs = new ArrayList<>();
      attrs.add(new JsonAttributeRepresentation(CoreAttributeTypes.Description.getId(), reqt.getName()));
      attrs.add(new JsonAttributeRepresentation(CoreAttributeTypes.ContentUrl.getId(), reqt.getPath()));
      artRep.setAttrs(attrs);
      return artRep;
   }

   public void clearRequirements() {
      if (reqs.size() > 0) {
         reqs.clear();
      }
   }

   public void addRequirement(Requirement req) {
      reqs.add(req);
   }

   public void performDragOver(DropTargetEvent event) {
      event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
      event.detail = DND.DROP_NONE;
   }

}
