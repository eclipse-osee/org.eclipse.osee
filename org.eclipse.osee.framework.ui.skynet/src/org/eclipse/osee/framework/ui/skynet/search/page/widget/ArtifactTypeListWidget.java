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
package org.eclipse.osee.framework.ui.skynet.search.page.widget;

import java.util.LinkedList;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class ArtifactTypeListWidget extends Composite implements IViewer {

   private ListViewer artTypeList;

   public ArtifactTypeListWidget(Composite parent, int style) {
      super(parent, style);
      createControl();
   }

   public void createControl() {
      this.setLayout(new GridLayout());
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      artTypeList = new ListViewer(this, SWT.CHECK | SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
      artTypeList.setContentProvider(new ContentProvider());
      artTypeList.setLabelProvider(new ArtTypeLabelProvider());
      artTypeList.setSorter(new ListSorter());

      GridData gd = new GridData(GridData.FILL, GridData.FILL, true, true);
      gd.heightHint = 300;
      gd.widthHint = 200;
      artTypeList.getControl().setLayoutData(gd);
      artTypeList.setInput(null);
   }

   public ListViewer getListViewer() {
      return artTypeList;
   }

   public void addSelectionListener(ISelectionChangedListener listener) {
      artTypeList.addSelectionChangedListener(listener);
   }

   public class ListSorter extends ViewerSorter {
      @SuppressWarnings("unchecked")
      @Override
      public int compare(Viewer viewer, Object e1, Object e2) {
         return getComparator().compare(((ArtifactSubtypeDescriptor) e1).getName(),
               ((ArtifactSubtypeDescriptor) e2).getName());
      }
   }

   public class ContentProvider implements IStructuredContentProvider {
      @SuppressWarnings("unchecked")
      public Object[] getElements(Object arg0) {
         return ((LinkedList) arg0).toArray();
      }

      public void dispose() {
      }

      public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
      }
   }

   public class ArtTypeLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return null;
      }

      public String getText(Object arg0) {
         return ((ArtifactSubtypeDescriptor) arg0).getName();
      }

      public void addListener(ILabelProviderListener arg0) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      public void removeListener(ILabelProviderListener arg0) {
      }
   }

   public class ArtLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return null;
      }

      public String getText(Object arg0) {
         return ((Artifact) arg0).getHumanReadableId();
      }

      public void addListener(ILabelProviderListener arg0) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      public void removeListener(ILabelProviderListener arg0) {
      }
   }

   public void dispose() {
      this.artTypeList.getList().dispose();
   }

   public void refresh() {
      this.artTypeList.refresh();
   }
}
