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
package org.eclipse.osee.framework.ui.service.control.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.jini.core.lookup.ServiceItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.jdk.core.type.TreeObject;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.ui.service.control.ControlPlugin;
import org.eclipse.osee.framework.ui.service.control.data.CategoryParent;
import org.eclipse.osee.framework.ui.service.control.data.GroupParent;
import org.eclipse.osee.framework.ui.service.control.data.ServiceNode;
import org.eclipse.osee.framework.ui.service.control.managers.ConnectionManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class ServicesViewer extends Composite {

   private static final Image CONFIG_IMAGE = ControlPlugin.getInstance().getImage("config.gif");
   private static final Image FOLDER_IMAGE =
         PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
   private static final Image GROUP_IMAGE = ControlPlugin.getInstance().getImage("group.gif");
   private static final Image CONNECT_FOLDER_IMAGE = ControlPlugin.getInstance().getImage("connect_folder.gif");
   private static final Image CONNECTED_IMAGE = ControlPlugin.getInstance().getImage("connected_plug.gif");

   private StructuredViewer viewer;
   private Map<Class<?>, Image> serviceIconMap;

   public ServicesViewer(Composite parent, int style) {
      super(parent, style);
      create();
      serviceIconMap = new HashMap<Class<?>, Image>();
   }

   private void create() {
      this.setLayout(new GridLayout());
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      createTreeArea(this);
   }

   private void createTreeArea(Composite parent) {
      viewer = new TreeViewer(parent, SWT.SINGLE | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      viewer.setContentProvider(new TreeContentProvider());
      viewer.setLabelProvider(new TreeLabelProvider());
      viewer.setSorter(new ViewerSorter());
      viewer.setInput(new ArrayList<String>());
      viewer.getControl().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
   }

   private class TreeLabelProvider extends LabelProvider {

      public Image getImage(Object obj) {
         Image toReturn = null;
         if (obj instanceof GroupParent) {
            toReturn = GROUP_IMAGE;
         } else if (obj instanceof CategoryParent) {
            toReturn = FOLDER_IMAGE;
            CategoryParent categoryParent = (CategoryParent) obj;
            if (categoryParent.hasChildren()) {
               Object child = categoryParent.getChildren()[0];
               if (child instanceof ServiceNode) {
                  ServiceNode serviceNode = (ServiceNode) child;
                  if (ConnectionManager.getInstance().isAllowedConnectionType(serviceNode.getServiceItem())) {
                     toReturn = CONNECT_FOLDER_IMAGE;
                  }
               }
            }
         } else if (obj instanceof ServiceNode) {
            ServiceNode node = ((ServiceNode) obj);
            if (node.isConnected()) {
               toReturn = CONNECTED_IMAGE;
            } else {
               toReturn = CONFIG_IMAGE;
               ServiceItem serviceItem = node.getServiceItem();
               for (Class<?> classType : serviceIconMap.keySet()) {
                  if (classType.isInstance(serviceItem.service)) {
                     Image image = serviceIconMap.get(classType);
                     if (image != null) {
                        toReturn = image;
                     }
                     break;
                  }
               }
            }
         }
         return toReturn;
      }

      public String getText(Object obj) {
         return obj.toString();
      }
   }

   private class TreeContentProvider implements ITreeContentProvider {

      public void dispose() {
      }

      public Object[] getChildren(Object parentElement) {
         if (parentElement != null && parentElement instanceof TreeParent) {
            TreeParent parent = (TreeParent) parentElement;
            if (parent.hasChildren()) {
               return parent.getChildren();
            }
         }
         return new Object[0];
      }

      public Object[] getElements(Object inputElement) {
         if (inputElement != null && inputElement instanceof Collection) {
            Collection<?> elementArray = (Collection<?>) inputElement;
            return elementArray.toArray();
         }
         return new Object[0];
      }

      public Object getParent(Object element) {
         if (element != null && element instanceof TreeObject) {
            TreeObject child = (TreeObject) element;
            return child.getParent();
         }
         return new Object();
      }

      public boolean hasChildren(Object element) {
         if (element instanceof TreeParent) {
            TreeParent parent = (TreeParent) element;
            return parent.hasChildren();
         }
         return false;
      }

      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      }
   }

   public boolean setFocus() {
      return this.viewer.getControl().setFocus();
   }

   public StructuredViewer getViewer() {
      return viewer;
   }

   public void setInput(List<TreeParent> input) {
      viewer.setInput(input);
   }

   public void refresh() {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            if (viewer != null && !viewer.getControl().isDisposed()) {
               viewer.refresh();
            }
         }
      });
   }

   public void dispose() {
      viewer.getControl().dispose();
      super.dispose();
   }

   public void registerImage(Class<?> serviceType, ImageDescriptor icon) {
      serviceIconMap.put(serviceType, icon.createImage());
   }

}
