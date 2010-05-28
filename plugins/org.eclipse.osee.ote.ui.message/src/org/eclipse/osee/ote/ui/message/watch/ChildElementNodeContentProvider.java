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
package org.eclipse.osee.ote.ui.message.watch;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.RecordElement;
import org.eclipse.osee.ote.message.elements.RecordMap;
import org.eclipse.osee.ote.ui.message.tree.WatchedMessageNode;

/**
 * @author Andrew M. Finkbeiner
 */
public class ChildElementNodeContentProvider implements ITreeContentProvider {
   private WatchedMessageNode input;

   @Override
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof RecordMap<?>) {
         RecordMap<?> recordMap = (RecordMap<?>) parentElement;
         List<Element> list = new ArrayList<Element>();
         for (int i = 0; i < recordMap.length(); i++) {
            RecordElement element = recordMap.get(i);
            if (recordHasChildrenNotListening(element)) {
               list.add(element);
            }
         }
         return list.toArray();
      } else if (parentElement instanceof RecordElement) {
         RecordElement recordElement = (RecordElement) parentElement;
         List<Element> list = new ArrayList<Element>();
         for (final Element element : recordElement.getElementMap().values()) {
            if (!(element instanceof RecordElement)) {
               if (!input.hasDescendant(new ElementPath(element.getElementPath()))) {
                  list.add(element);
               }
            } else {
               if (recordHasChildrenNotListening((RecordElement) element)) {
                  list.add(element);
               }
            }
         }
         return list.toArray();
      } else if (parentElement instanceof Element) {
         return new Object[0];
      }
      return new Object[0];
   }

   /**
    * @param element
    * @return
    */
   private boolean recordHasChildrenNotListening(RecordElement element) {
      if (element instanceof RecordMap<?>) {
         RecordMap<?> recordMap = (RecordMap<?>) element;
         for (int i = 0; i < recordMap.length(); i++) {
            RecordElement recelement = recordMap.get(i);
            if (recordHasChildrenNotListening(recelement)) {
               return true;
            }
         }

      } else {
         for (Element recelement : element.getElementMap().values()) {
            if (!(recelement instanceof RecordElement)) {
               if (!input.hasDescendant(new ElementPath(recelement.getElementPath()))) {
                  return true;
               }
            } else {
               if (recordHasChildrenNotListening((RecordElement) recelement)) {
                  return true;
               }
            }

         }
      }
      return false;
   }

   @Override
   public Object getParent(Object element) {
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof RecordElement) {
         return getChildren(element).length > 0;
      } else {
         return false;
      }
   }

   @Override
   public Object[] getElements(Object inputElement) {
      WatchedMessageNode data = (WatchedMessageNode) inputElement;
      List<Element> list = new ArrayList<Element>();

      Message<?, ?, ?> message = data.getSubscription().getMessage();
      for (final Element element : message.getElements()) {
         if (!(element instanceof RecordElement)) {
            if (data.findChildElement(new ElementPath(element.getElementPath())) == null) {
               list.add(element);
            }
         } else {
            list.add(element);
         }
      }
      return list.toArray();
   }

   @Override
   public void dispose() {
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      input = (WatchedMessageNode) newInput;
   }

}
