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
package org.eclipse.osee.ote.define.parser.handlers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.define.internal.Activator;
import org.eclipse.osee.ote.define.parser.IDataListener;
import org.eclipse.osee.ote.define.parser.ISaxElementHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractParseHandler implements ISaxElementHandler {
   private final Set<IDataListener> listeners;

   protected AbstractParseHandler() {
      this.listeners = Collections.synchronizedSet(new HashSet<IDataListener>());
   }

   @Override
   public void addListener(IDataListener listener) {
      if (!listeners.contains(listener)) {
         listeners.add(listener);
      }
   }

   @Override
   public void removeListener(IDataListener listener) {
      if (listeners.contains(listener)) {
         listeners.remove(listener);
      }
   }

   public void notifyOnDataEvent(String name, String value) {
      for (final IDataListener listener : listeners) {
         listener.notifyDataEvent(name, value);
      }
   }

   @Override
   public void processSaxChunkCollectorData(String currentLocalName, String xmlData) {
      try {
         Document doc = Jaxp.readXmlDocument(xmlData);
         Element root = doc.getDocumentElement();
         if (root != null) {
            processSaxChunk(root);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   protected abstract void processSaxChunk(Element element);
}
