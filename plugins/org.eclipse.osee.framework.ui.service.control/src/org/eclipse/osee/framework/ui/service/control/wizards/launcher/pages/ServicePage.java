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
package org.eclipse.osee.framework.ui.service.control.wizards.launcher.pages;

import java.util.Collection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.ServiceLaunchingInformation;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.data.ServiceItem;
import org.eclipse.osee.framework.ui.service.control.wizards.launcher.data.ServiceLaunchConfig;
import org.eclipse.osee.framework.ui.swt.DynamicWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class ServicePage extends DynamicWizardPage {

   private ListViewer listViewer;
   private ServiceLaunchingInformation serviceInfo;
   private Collection<ServiceItem> localList;
   private Collection<ServiceItem> remoteList;
   private Group group;
   private boolean isLocal;

   public ServicePage(String pageName, String previous, String next, ServiceLaunchingInformation serviceInfo) {
      super(pageName, previous, next);
      this.serviceInfo = serviceInfo;
      this.isLocal = true;
      setTitle("Service Selection");
      setDescription("Please select the service you would like to launch.");
      setPageComplete(false);
   }

   public void setIsLocal(boolean isLocal) {
      this.isLocal = isLocal;
      manageInputList();
   }

   public void createControl(Composite parent) {
      group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      group.setText("Service Selection");

      listViewer = new ListViewer(group, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      GridData d = new GridData(SWT.FILL, SWT.FILL, true, true);
      listViewer.getControl().setLayoutData(d);
      listViewer.setContentProvider(new ListContentProvider());
      listViewer.setLabelProvider(new ListLabelProvider());
      listViewer.setSorter(new ListSorter());

      listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            if (selection != null && !selection.isEmpty()) {
               ServiceItem item = (ServiceItem) selection.getFirstElement();
               serviceInfo.setServiceItem(item);
               serviceInfo.setAvailableHosts(item.getHosts());
               setPageComplete(true);
            } else {
               setPageComplete(false);
            }
         }
      });

      localList = ServiceLaunchConfig.getInstance().getLocalServiceItems();
      remoteList = ServiceLaunchConfig.getInstance().getRemoteServiceItems();
      manageInputList();
      setControl(group);
   }

   private void manageInputList() {
      Collection<ServiceItem> listToSet = this.localList;
      if (!isLocal) {
         listToSet = this.remoteList;
      }
      if (listViewer != null && !listViewer.getControl().isDisposed()) {
         listViewer.setInput(listToSet);
         serviceInfo.setServiceItem(null);
      }
   }

   private class ListLabelProvider extends LabelProvider {
      public Image getImage(Object element) {
         return null;
      }

      public String getText(Object element) {
         return ((ServiceItem) element).getName();
      }
   }

   private class ListContentProvider implements IStructuredContentProvider {
      public Object[] getElements(Object inputElement) {
         Collection<?> collection = (Collection<?>) inputElement;
         return collection.toArray();
      }

      public void dispose() {
      }

      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      }
   }

   public class ListSorter extends ViewerSorter {
      public int compare(Viewer viewer, Object e1, Object e2) {
         ServiceItem item1 = (ServiceItem) e1;
         ServiceItem item2 = (ServiceItem) e2;
         return item1.compareTo(item2);
      }
   }

   @Override
   public void dispose() {
      super.dispose();
      if (listViewer != null) {
         listViewer.getList().dispose();
      }
   }

   @Override
   public boolean onNextPressed() {
      boolean toReturn = false;
      IStructuredSelection selection = (IStructuredSelection) listViewer.getSelection();
      if (selection != null && !selection.isEmpty()) {
         ServiceItem item = (ServiceItem) selection.getFirstElement();
         serviceInfo.setServiceItem(item);
         serviceInfo.setAvailableHosts(item.getHosts());
         toReturn = true;
      }
      return toReturn;
   }

}
