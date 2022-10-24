/*********************************************************************
 * Copyright (c) 2015 Boeing
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
