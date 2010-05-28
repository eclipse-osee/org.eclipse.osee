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
package org.eclipse.osee.ote.ui.message.util;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.ote.message.ElementPath;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.RecordElement;
import org.eclipse.osee.ote.message.elements.RecordMap;
import org.eclipse.osee.ote.service.IMessageDictionary;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.ui.message.util.internal.Activator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ken J. Aguilar
 */
public class MessageElementSelectionDialog extends ElementListSelectionDialog {

   public MessageElementSelectionDialog(Shell parent, Message<?, ?, ?> msg) {
      this(parent, msg, null);
   }

   public MessageElementSelectionDialog(Shell parent, String msg, ElementFilter filter) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalStateException {
      this(parent, getMessage(msg), filter);
   }

   public MessageElementSelectionDialog(Shell parent, Message<?, ?, ?> msg, ElementFilter filter) {
      super(parent, new LabelProvider());
      LinkedList<Element> topLevelElements = new LinkedList<Element>();
      LinkedList<Element> filterElements = new LinkedList<Element>();
      msg.getAllElements(topLevelElements);

      process(filter, topLevelElements, filterElements);
      setElements(filterElements.toArray());
      setMessage("Select a message element. Use * as the wild card character");
      setTitle("Message Element Selection");
   }

   private static Message<?, ?, ?> getMessage(String msg) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      ServiceTracker tracker =
            new ServiceTracker(Activator.getDefault().getBundle().getBundleContext(),
                  IOteClientService.class.getName(), null);
      tracker.open(true);
      try {
         IMessageDictionary dictionary = ((IOteClientService) tracker.waitForService(1000)).getLoadedDictionary();
         if (dictionary == null) {
            throw new IllegalStateException("no dictionary loaded");
         }
         return dictionary.lookupMessage(msg).newInstance();
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         throw new IllegalStateException("interrupted", e);
      } finally {
         tracker.close();
      }
   }

   private static final class LabelProvider implements ILabelProvider {

      @Override
      public Image getImage(Object element) {
         return null;
      }

      @Override
      public String getText(Object element) {
         Element msgElement = (Element) element;
         return new ElementPath(msgElement.getElementPath()).toString();
      }

      @Override
      public void addListener(ILabelProviderListener listener) {
      }

      @Override
      public void dispose() {
      }

      @Override
      public boolean isLabelProperty(Object element, String property) {
         return false;
      }

      @Override
      public void removeListener(ILabelProviderListener listener) {
      }

   };

   private void process(ElementFilter filter, List<Element> list, List<Element> destinationList) {
      for (Element element : list) {
         processElement(filter, element, destinationList);
      }
   }

   private void processElement(ElementFilter filter, Element element, List<Element> destinationList) {
      if (element instanceof RecordMap<?>) {
         processRecordMap(filter, (RecordMap<?>) element, destinationList);
      } else if (element instanceof RecordElement) {
         processRecordElement(filter, (RecordElement) element, destinationList);
      } else {
         if (filter == null || filter.accept(element)) {
            destinationList.add(element);
         }
      }
   }

   private void processRecordMap(ElementFilter filter, RecordMap<?> map, List<Element> destinationList) {
      for (int i = 0; i < map.length(); i++) {
         processRecordElement(filter, map.get(i), destinationList);
      }

   }

   private void processRecordElement(ElementFilter filter, RecordElement element, List<Element> destinationList) {
      if (element instanceof RecordMap<?>) {
         processRecordMap(filter, (RecordMap<?>) element, destinationList);
      } else {
         for (Element childElement : element.getElementMap().values()) {
            processElement(filter, childElement, destinationList);
         }
      }
   }

}
