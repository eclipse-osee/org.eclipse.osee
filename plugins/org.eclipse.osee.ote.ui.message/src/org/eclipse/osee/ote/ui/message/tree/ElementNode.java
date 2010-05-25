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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.OverlayImage;
import org.eclipse.osee.framework.ui.swt.OverlayImage.Location;
import org.eclipse.osee.ote.ui.message.OteMessageImage;
import org.eclipse.osee.ote.ui.message.messageXViewer.MessageXViewerFactory;
import org.eclipse.osee.ote.ui.message.watch.ElementPath;
import org.eclipse.osee.ote.ui.message.watch.recording.IElementPath;
import org.eclipse.swt.graphics.Image;

/**
 * Represents an {@link org.eclipse.osee.ote.message.elements.Element} of a {@link org.eclipse.osee.ote.message.Message}
 * in a tree view
 * 
 * @author Ken J. Aguilar
 */
public class ElementNode extends AbstractTreeNode implements IElementPath {

   private final ElementPath messageElementPath;

   private final Map<String, ElementNode> pathToElementNode = new HashMap<String, ElementNode>();

   private static final Image normalImg = ImageManager.getImage(OteMessageImage.PIPE);
   private static final Image errorImg =
         new OverlayImage(normalImg, ImageDescriptor.createFromImage(ImageManager.getImage(OteMessageImage.ERROR_SM)),
               Location.BOT_RIGHT).createImage();
   private MessageNode messageNode;

   /**
    * Convience Constructor
    * 
    * @param msgName
    * @param elementName
    * @param parent
    */
   public ElementNode(final ElementPath elementName) {
      super(elementName.get(elementName.size() - 1).toString(), normalImg);
      this.messageElementPath = elementName;
   }

   public void addChild(ElementNode node) {
      if (getParent() == null) {
         throw new IllegalStateException("parent not set");
      }
      ElementNode elementNode = node;
      pathToElementNode.put(elementNode.getElementPath().asString(), elementNode);
      node.setParent(this);
   }

   protected void setParent(MessageNode node) {
      messageNode = node;
      super.setParent(node);
   }

   protected void setParent(ElementNode node) {
      messageNode = node.messageNode;
      super.setParent(node);
   }

   public void collectDescendants(Collection<? super ElementNode> descendants) {
      for (ElementNode node : pathToElementNode.values()) {
         descendants.add(node);
         node.collectDescendants(descendants);
      }
   }

   public String getMessageName() {
      return getElementPath().getMessageName();
   }

   public boolean hasDescendant(ElementPath element) {
      if (pathToElementNode.containsKey(element.asString())) {
         return true;
      }
      String path = element.asString();
      for (Map.Entry<String, ElementNode> entry : pathToElementNode.entrySet()) {
         if (entry.getKey().startsWith(path)) {
            if (entry.getValue().hasDescendant(element)) {
               return true;
            }
         }
      }
      return false;
   }

   public ElementNode findDescendant(ElementPath element) {
      ElementNode node = pathToElementNode.get(element.asString());
      if (node == null) {
         String path = element.asString();
         for (Map.Entry<String, ElementNode> entry : pathToElementNode.entrySet()) {
            if (path.startsWith(entry.getKey())) {
               node = entry.getValue().findDescendant(element);
               if (node != null) {
                  return node;
               }
            }
         }
      }
      return node;
   }

   /**
    * gets the name of the element that this tree node represents
    * 
    * @return Returns element name string.
    */
   public String getElementName() {
      return getElementPath().getElementName();
   }

   @Override
   public String getName() {
      return getElementName();
   }

   public MessageNode getMessageNode() {
      return messageNode;
   }

   @Override
   public boolean canSetValue() {
      return true;
   }

   @Override
   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      setImage(enabled ? normalImg : errorImg);
   }

   @Override
   public void setHasMappingToCurrentMemType(boolean isEditable) {
      super.setHasMappingToCurrentMemType(isEditable);
      setImage(isEditable ? normalImg : errorImg);
   }

   @Override
   public String getLabel() {
      return getName();
   }

   @Override
   public <T> T visit(INodeVisitor<T> visitor) {
      return visitor.elementNode(this);
   }

   @Override
   public Image getImage(XViewerColumn column) {
      if (column == null) {
         return null;
      }
      if (column.equals(MessageXViewerFactory.name)) {
         return getImage();
      }
      return null;
   }

   @Override
   public Image getImage() {
      if (hasMappingToCurrentMemType() && isEnabled()) {
         return ElementNode.normalImg;
      } else {
         return ElementNode.errorImg;
      }
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
         return "";
      }
      // Object obj = get(columns);
      return ""; // obj == null ? "" : get(columns).toString();
   }

   public ElementPath getElementPath() {
      return messageElementPath;
   }

   /**
    * @return List<ElementNode>
    */
   public List<ElementNode> getAllChildren() {
      List<ElementNode> allChildren = new ArrayList<ElementNode>();
      for (AbstractTreeNode node : getChildren()) {
         getChildren(node, allChildren);
      }
      return allChildren;
   }

   private void getChildren(AbstractTreeNode node, List<ElementNode> children) {
      if (node instanceof ElementNode) {
         children.add((ElementNode) node);
      }
      for (AbstractTreeNode child : node.getChildren()) {
         getChildren(child, children);
      }
   }

   @Override
   public Collection<ElementNode> getChildren() {
      return pathToElementNode.values();
   }

   @Override
   public boolean hasChildren() {
      return !pathToElementNode.isEmpty();
   }

   @Override
   public void removeAll() {
      for (AbstractTreeNode child : pathToElementNode.values()) {
         child.dispose();
      }
      pathToElementNode.clear();
   }

   @Override
   public void deleteChildren(Collection<AbstractTreeNode> children) {
      for (AbstractTreeNode child : children) {
         pathToElementNode.remove(((ElementNode) child).getElementPath().asString());
         child.dispose();
      }
   }

}
