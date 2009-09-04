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
package org.eclipse.osee.framework.types.bridge.wizards;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.URI;

/**
 * @author Roberto E. Escobar
 */
public class LinkNode {
   public LinkNode nodeParent;
   public URI nodeURI;
   public List<LinkNode> children;
   public boolean resolved;

   public LinkNode(URI nodeURI) {
      this.nodeURI = nodeURI;
      this.children = new ArrayList<LinkNode>();
      this.resolved = true;
   }

   public void addChild(LinkNode node) {
      nodeParent = this;
      children.add(node);
   }

   public URI getUri() {
      return nodeURI;
   }

   public LinkNode getParent() {
      return nodeParent;
   }

   public boolean hasChildren() {
      return !getChildren().isEmpty();
   }

   public List<LinkNode> getChildren() {
      return children;
   }

   public void setIsResolved(boolean resolved) {
      this.resolved = resolved;
   }

   public boolean isResolved() {
      return resolved;
   }
}