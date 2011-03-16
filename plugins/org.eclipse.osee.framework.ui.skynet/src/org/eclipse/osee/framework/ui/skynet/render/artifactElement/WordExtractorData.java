/*
 * Created on Mar 14, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render.artifactElement;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Jeff C. Phillips
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
