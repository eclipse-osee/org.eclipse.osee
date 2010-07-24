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

import java.util.Collection;
import java.util.HashMap;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.DiscreteElement;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.NumericElement;
import org.eclipse.osee.ote.message.elements.RecordMap;
import org.eclipse.osee.ote.ui.message.messageXViewer.MessageXViewerFactory;
import org.eclipse.osee.ote.ui.message.watch.ElementPath;

/**
 * @author Ken J. Aguilar
 */
public class WatchedElementNode extends ElementNode {

   private Object value = "???";

   private final HashMap<XViewerColumn, Object> columnValues = new HashMap<XViewerColumn, Object>();
   private Element element;

   public WatchedElementNode(ElementPath elementName) {
      super(elementName);
   }

   @Override
   public String getLabel(XViewerColumn columns) {
      if (columns == null) {
         return "";
      }
      if (columns.equals(MessageXViewerFactory.name)) {
         return getElementName();
      }
      if (columns.equals(MessageXViewerFactory.value)) {
         return value.toString();
      }
      Object obj = columnValues.get(columns);
      return obj == null ? "" : obj.toString();
   }

   public String getHex() {
      if (element != null && element instanceof NumericElement<?>) {
         NumericElement<?> e = (NumericElement<?>) element;
         return String.format("%08X", e.getNumericBitValue());
      }
      return "--";
   }

   public IMessageSubscription getSubscription() {
      return ((WatchedMessageNode) getMessageNode()).getSubscription();
   }

   public Integer getByteOffset() {
      Object obj = columnValues.get(MessageXViewerFactory.byteOffset);
      if (obj != null) {
         return (Integer) obj;
      }
      return null;
   }

   public Integer getMsb() {
      Object obj = columnValues.get(MessageXViewerFactory.msb);
      if (obj != null) {
         return (Integer) obj;
      }
      return null;
   }

   public Integer getLsb() {
      Object obj = columnValues.get(MessageXViewerFactory.lsb);
      if (obj != null) {
         return (Integer) obj;
      }
      return null;
   }

   public void setResolved(boolean isResolved) {
      if (isResolved) {
         Message<?, ?, ?> message = getSubscription().getMessage();
         element = message.getElement(getElementPath().getElementPath(), getSubscription().getMemType());

         if (element == null) {
            columnValues.clear();
            value = "???";
            setEnabled(false);
            setDisabledReason("could not find the element " + getElementPath().getElementName());
            return;
         }
         if (element.isNonMappingElement()) {
            columnValues.clear();
            value = "???";
            setEnabled(false);
            setDisabledReason("this element does not map in " + getSubscription().getMemType());
            return;
         }
         columnValues.put(MessageXViewerFactory.lsb, Integer.valueOf(element.getLsb()));
         columnValues.put(MessageXViewerFactory.msb, Integer.valueOf(element.getMsb()));
         columnValues.put(MessageXViewerFactory.bitSize, Integer.valueOf(element.getBitLength()));
         if (element instanceof DiscreteElement<?>) {
            value = ((DiscreteElement<?>) element).getValue();
            columnValues.put(MessageXViewerFactory.byteOffset, Integer.valueOf(element.getByteOffset()));
         } else if (element instanceof RecordMap<?>) {
            value = "";
            columnValues.put(MessageXViewerFactory.byteOffset,
               Integer.valueOf(((RecordMap<?>) element).get(0).getByteOffset()));
         } else {
            value = "";
            columnValues.put(MessageXViewerFactory.byteOffset, Integer.valueOf(element.getByteOffset()));
         }
      } else {
         columnValues.clear();
         value = "???";
         element = null;
      }
      for (ElementNode child : getChildren()) {
         ((WatchedElementNode) child).setResolved(isResolved);
      }
   }

   public void determineDeltas(Collection<AbstractTreeNode> deltas) {
      if (!isEnabled() || element == null) {
         return;
      }

      if (element instanceof DiscreteElement<?>) {
         MessageData data = element.getMessage().getActiveDataSource();
         int headerSize = data.getMsgHeader() == null ? 0 : data.getMsgHeader().getHeaderSize();
         if (element.getByteOffset() >= data.getCurrentLength() - headerSize) {
            value = "???";
            deltas.add(this);
         } else {
            DiscreteElement<?> discrete = (DiscreteElement<?>) element;
            Object newValue = discrete.getValue();
            if (!newValue.equals(value)) {
               value = newValue;
               deltas.add(this);
            }
         }
      }
      for (ElementNode node : getChildren()) {
         ((WatchedElementNode) node).determineDeltas(deltas);
      }
   }

   @Override
   protected void setParent(MessageNode node) {
      super.setParent(node);
      setResolved(getSubscription().isResolved());
   }

   @Override
   protected void setParent(ElementNode node) {
      super.setParent(node);
      setResolved(getSubscription().isResolved());
   }

}
