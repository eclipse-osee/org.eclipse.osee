/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.tree;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.OverlayImage;
import org.eclipse.osee.framework.ui.swt.OverlayImage.Location;
import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.message.commands.RecordCommand.MessageRecordDetails;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.ui.message.OteMessageImage;
import org.eclipse.osee.ote.ui.message.messageXViewer.MessageXViewerFactory;
import org.eclipse.osee.ote.ui.message.watch.ElementPath;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ken J. Aguilar
 */
public class WatchedMessageNode extends MessageNode {

   private static final Image readerImg =
      new OverlayImage(MessageNode.normalImg,
         ImageDescriptor.createFromImage(ImageManager.getImage(OteMessageImage.MSG_READ_IMG)), Location.BOT_RIGHT).createImage();
   private static final Image writerImg =
      new OverlayImage(MessageNode.normalImg,
         ImageDescriptor.createFromImage(ImageManager.getImage(OteMessageImage.MSG_WRITE_IMG)), Location.BOT_RIGHT).createImage();
   private final RecordingState recordingState = new RecordingState();
   private final IMessageSubscription subscription;
   private final AtomicLong numUpdates = new AtomicLong(0);
   private long lastUpdateNumber = 0;

   public WatchedMessageNode(IMessageSubscription subscription) {
      super(subscription.getMessageClassName());
      this.subscription = subscription;
   }

   public IMessageSubscription getSubscription() {
      return subscription;
   }

   public RecordingState getRecordingState() {
      return this.recordingState;
   }

   public MessageRecordDetails createRecordingDetails() {
      return null;
   }

   public DataType getMemType() {
      return subscription.getMemType();
   }

   public void determineDeltas(Collection<AbstractTreeNode> deltas) {
      if (!isEnabled()) {
         return;
      }
      long currentUpdate = numUpdates.get();
      if (currentUpdate != lastUpdateNumber) {
         deltas.add(this);
         for (ElementNode node : getChildren()) {
            ((WatchedElementNode) node).determineDeltas(deltas);
         }
         lastUpdateNumber = currentUpdate;
      }
   }

   public void incrementCounter() {
      numUpdates.incrementAndGet();
   }

   public void clearUpdateCounter() {
      numUpdates.set(0);
   }

   public void setResolved(boolean isResolved) {
      for (ElementNode child : getChildren()) {
         WatchedElementNode elementNode = (WatchedElementNode) child;
         elementNode.setResolved(isResolved);
      }
   }

   @Override
   public String getLabel(XViewerColumn columns) {
      if (columns == null) {
         return "";
      }
      if (columns.equals(MessageXViewerFactory.name)) {
         return getName();
      }
      if (columns.equals(MessageXViewerFactory.psUpdateCount)) {
         return numUpdates.toString();
      }
      return "";
   }

   public class RecordingState {
      private final List<ElementPath> headerElements = new ArrayList<ElementPath>();
      private final List<ElementPath> bodyElements = new ArrayList<ElementPath>();
      private boolean headerDump = false;
      private boolean bodyDump = false;

      public void reset() {
         headerElements.clear();
         bodyElements.clear();
         headerDump = false;
         bodyDump = false;
      }

      public void setHeaderDump(boolean header) {
         headerDump = header;
      }

      public void setBodyDump(boolean body) {
         bodyDump = body;
      }

      public void addHeader(ElementPath path) {
         headerElements.add(path);
      }

      public void addBody(ElementPath path) {
         bodyElements.add(path);
      }

      public boolean getBodyDump() {
         return bodyDump;
      }

      public boolean getHeaderDump() {
         return headerDump;
      }

      public List<ElementPath> getHeaderElements() {
         return headerElements;
      }

      public List<ElementPath> getBodyElements() {
         return bodyElements;
      }

      public void write(OutputStreamWriter writer) throws IOException {
         if (bodyDump) {
            writer.write(String.format("#rec#,%s,bodyHex,true", getMessageClassName()));
            writer.write("\n");
         }
         if (headerDump) {
            writer.write(String.format("#rec#,%s,headerHex,true", getMessageClassName()));
            writer.write("\n");
         }
         for (ElementPath path : headerElements) {
            writer.write(String.format("#rec#,%s,header,%s", getMessageClassName(), path.asString()));
            writer.write("\n");
         }
         for (ElementPath path : bodyElements) {
            writer.write(String.format("#rec#,%s,body,%s", getMessageClassName(), path.asString()));
            writer.write("\n");
         }
      }
   }

   @Override
   protected void dispose() {
      subscription.cancel();
      super.dispose();
   }

   @Override
   public Image getImage() {
      if (!isEnabled()) {
         return MessageNode.errorImg;
      }
      switch (getSubscription().getMessageMode()) {
         case READER:
            return readerImg;
         case WRITER:
            return writerImg;
      }
      return MessageNode.normalImg;
   }
}
