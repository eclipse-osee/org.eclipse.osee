/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.internal.wordupdate;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author David W. Miller
 */
public class WordExtractorData {
   private Element parent;
   private String guid;

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public void addParent(Element parent) {
      this.parent = parent;
   }

   public void addChild(Node child) {
      parent.appendChild(child);
   }

   public String getGuid() {
      return guid;
   }

   public Element getParentEelement() {
      return parent;
   }

}
