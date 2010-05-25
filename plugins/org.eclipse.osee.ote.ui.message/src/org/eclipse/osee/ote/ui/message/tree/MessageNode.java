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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.OverlayImage;
import org.eclipse.osee.framework.ui.swt.OverlayImage.Location;
import org.eclipse.osee.ote.ui.message.OteMessageImage;
import org.eclipse.osee.ote.ui.message.messageXViewer.MessageXViewerFactory;
import org.eclipse.osee.ote.ui.message.watch.ElementPath;
import org.eclipse.swt.graphics.Image;

/**
 * Defines a node in a {@link org.eclipse.swt.widgets.Tree} that maintains detials on
 * {@link org.eclipse.osee.ote.message.Message} for display
 * 
 * @author Ken J. Aguilar
 */
public class MessageNode extends AbstractTreeNode {

   private final Map<String, ElementNode> pathToElementNode = new HashMap<String, ElementNode>();

   protected static final Image normalImg = ImageManager.getImage(OteMessageImage.GEAR);
   @SuppressWarnings("unused")
   private static final Image wireAIUImg = ImageManager.getImage(OteMessageImage.WIRE_AIU);
   protected static final Image errorImg =
         new OverlayImage(normalImg, ImageDescriptor.createFromImage(ImageManager.getImage(OteMessageImage.ERROR_SM)),
               Location.BOT_RIGHT).createImage();
   private final String shortenedMessageName;
   private final String packageName;
   private final String type;

   public MessageNode(String msgClassName, Image image) {
      super(msgClassName, image);
      shortenedMessageName = msgClassName.substring(msgClassName.lastIndexOf('.') + 1);
      packageName = msgClassName.substring(0, msgClassName.lastIndexOf('.'));
      type = packageName.substring(packageName.lastIndexOf('.') + 1);
   }

   public MessageNode(String msgClassName) {
      this(msgClassName, normalImg);
   }

   public String getType() {
      return type;
   }

   public String getPackageName() {
      return packageName;
   }

   @Override
   public boolean canSetValue() {
      return false;
   }

   @Override
   public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      if (!enabled) {
         setImage(errorImg);
      }
   }

   @Override
   String getLabel() {
      return getName();
   }

   @Override
   public <T> T visit(INodeVisitor<T> visitor) {
      return visitor.messageNode(this);
   }

   @Override
   public Image getImage(XViewerColumn columns) {
      if (columns == null) {
         return null;
      }
      if (columns.equals(MessageXViewerFactory.name)) {
         return getImage();
      }
      return null;
   }

   @Override
   public String getLabel(XViewerColumn columns) {
      if (columns == null) {
         return "";
      }
      if (columns.equals(MessageXViewerFactory.name)) {
         return getName();
      }
      return "";
   }

   /**
    * @param element
    */
   public ElementNode findChildElement(ElementPath element) {
      return pathToElementNode.get(element.asString());
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
      String path = element.asString();
      ElementNode node = pathToElementNode.get(path);
      if (node == null) {
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

   @Override
   public String getName() {
      return shortenedMessageName;
   }

   public String getMessageClassName() {
      return super.getName();
   }

   public void addChild(ElementNode node) {
      pathToElementNode.put(node.getElementPath().asString(), node);
      node.setParent(this);
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

   public void collectDescendants(Collection<? super ElementNode> descendants) {
      for (ElementNode node : pathToElementNode.values()) {
         descendants.add(node);
         node.collectDescendants(descendants);
      }
   }
}
