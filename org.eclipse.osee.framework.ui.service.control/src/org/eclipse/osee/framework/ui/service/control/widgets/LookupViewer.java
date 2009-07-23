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

import static org.eclipse.osee.framework.ui.skynet.ImageManager.getImage;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceRegistrar;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.ui.service.control.ServiceControlImage;
import org.eclipse.osee.framework.ui.service.control.renderer.IRenderer;
import org.eclipse.osee.framework.ui.service.control.renderer.ReggieItemHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Roberto E. Escobar
 */
public class LookupViewer extends Composite {

   private static final Image LOOK_UP_IMAGE = getImage(ServiceControlImage.CONNECTION);//ControlPlugin.getInstance().getImage("connection.gif");
   private static final Image DISCONNECTED_IMAGE = getImage(ServiceControlImage.DISCONNECTED);//ControlPlugin.getInstance().getImage("disconnected.gif");

   private StructuredViewer viewer;
   private Map<ServiceID, IRenderer> handlerMap;

   public LookupViewer(Composite parent, int style) {
      super(parent, style);
      createControl();
   }

   private void createControl() {
      this.setLayout(new GridLayout());
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      viewer = new TableViewer(this, SWT.SINGLE | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      viewer.setLabelProvider(new ListLabelProvider());
      viewer.setContentProvider(new ArrayContentProvider());
      viewer.setSorter(new ViewerSorter());
      viewer.setInput(new String[0]);
   }

   public void refresh() {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            viewer.refresh();
         }
      });
   }

   public void setSelection(final String serviceId) {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            Table table = ((TableViewer) viewer).getTable();
            TableItem[] items = table.getItems();
            for (TableItem item : items) {
               Object object = item.getData();
               if (object instanceof ServiceRegistrar) {
                  if (((ServiceRegistrar) object).getServiceID().toString().equals(serviceId)) {
                     table.setSelection(new TableItem[] {item});
                     return;
                  }
               }
            }
         }
      });
   }

   public StructuredViewer getViewer() {
      return viewer;
   }

   public void setInput(Collection<?> input) {
      viewer.setInput(input);
   }

   public void setRendererMap(Map<ServiceID, IRenderer> map) {
      this.handlerMap = map;
   }

   public IRenderer getRenderer(ServiceID serviceId) {
      if (handlerMap != null) {
         return handlerMap.get(serviceId);
      } else {
         return null;
      }
   }

   private class ListLabelProvider extends LabelProvider {

      public Image getImage(Object element) {
         if (element instanceof ServiceRegistrar) {
            if (ReggieItemHandler.isAllowed((ServiceRegistrar) element)) {
               return LOOK_UP_IMAGE;
            } else {
               return DISCONNECTED_IMAGE;
            }
         }
         return null;
      }

      public String getText(Object element) {
         if (element instanceof ServiceRegistrar) {
            ServiceRegistrar reggie = (ServiceRegistrar) element;
            try {
               return " Jini Lookup: " + reggie.getLocator().getHost() + " : " + reggie.getLocator().getPort();
            } catch (RemoteException ex) {
               ex.printStackTrace();
               return "Jini Lookup: " + "UNABLE TO LOCATE";
            }
         }
         return "";
      }
   }

   public void dispose() {
      viewer.getControl().dispose();
   }
}