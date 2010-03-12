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
package org.eclipse.osee.ote.core.framework.saxparse;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public abstract class ElementHandlers {

   private String name;
   private List<IBaseSaxElementListener> listeners = new ArrayList<IBaseSaxElementListener>();
   
   public ElementHandlers(String name){
      this.name = name;
   }
   
   public final String getElementName(){
      return this.name;
   }
   
   public final void addListener(IBaseSaxElementListener listener){
      listeners.add(listener);
   }
   
   public final void removeListener(IBaseSaxElementListener listener){
      listeners.remove(listener);
   }
   
   void endElementFound(String uri, String localName, String name, String content) throws SAXException {
      if(listeners.size() == 0) return;
      Object obj = createEndElementFoundObject(uri, localName, name, content);
      for(IBaseSaxElementListener listener:listeners){
         listener.onEndElement(obj);
      }
   }
   
   void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
      if(listeners.size() == 0) return;
      Object obj = createStartElementFoundObject(uri, localName, name, attributes);
      for(IBaseSaxElementListener listener:listeners){
         listener.onStartElement(obj);
      }
   }
   
   public abstract Object createStartElementFoundObject(String uri, String localName, String name, Attributes attributes);
   
   public Object createEndElementFoundObject(String uri, String localName, String name, String content) {
      return content;
   }
}
