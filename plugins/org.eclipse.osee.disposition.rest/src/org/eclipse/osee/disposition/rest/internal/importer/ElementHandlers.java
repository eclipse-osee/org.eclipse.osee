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

package org.eclipse.osee.disposition.rest.internal.importer;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class ElementHandlers {

   private final String name;
   private final List<IBaseSaxElementListener> listeners = new ArrayList<>();

   public ElementHandlers(String name) {
      this.name = name;
   }

   public final String getElementName() {
      return this.name;
   }

   public final void addListener(IBaseSaxElementListener listener) {
      listeners.add(listener);
   }

   public final void removeListener(IBaseSaxElementListener listener) {
      listeners.remove(listener);
   }

   void endElementFound(String uri, String localName, String name, String content) throws Exception {
      if (listeners.isEmpty()) {
         return;
      }
      Object obj = createEndElementFoundObject(uri, localName, name, content);
      for (IBaseSaxElementListener listener : listeners) {
         listener.onEndElement(obj);
      }
   }

   void startElementFound(String uri, String localName, String name, Attributes attributes) throws Exception {
      if (listeners.isEmpty()) {
         return;
      }
      Object obj = createStartElementFoundObject(uri, localName, name, attributes);
      for (IBaseSaxElementListener listener : listeners) {
         listener.onStartElement(obj);
      }
   }

   public abstract Object createStartElementFoundObject(String uri, String localName, String name, Attributes attributes);

   public Object createEndElementFoundObject(String uri, String localName, String name, String content) {
      return content;
   }
}
