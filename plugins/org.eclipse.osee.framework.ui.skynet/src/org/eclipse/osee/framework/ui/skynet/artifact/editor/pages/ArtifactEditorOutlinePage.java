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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.AttributeTypeUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactEditorOutlinePage extends ContentOutlinePage {

   private ArtifactEditor editor;

   @Override
   public void createControl(Composite parent) {
      super.createControl(parent);

      Tree tree = getTreeViewer().getTree();
      tree.setLayout(new FillLayout(SWT.VERTICAL));
      getTreeViewer().setContentProvider(new InternalContentProvider());
      getTreeViewer().setLabelProvider(new InternalLabelProvider());
      setInput(editor != null ? editor : "No Input Available");

      getSite().getActionBars().getToolBarManager().add(
         new Action("Refresh", ImageManager.getImageDescriptor(PluginUiImage.REFRESH)) {
            @Override
            public void run() {
               refresh();
            }
         });
      getSite().getActionBars().getToolBarManager().update(true);
   }

   @Override
   public void selectionChanged(SelectionChangedEvent event) {
      ISelection selection = event.getSelection();
      if (selection instanceof IStructuredSelection) {
         IStructuredSelection sSelection = (IStructuredSelection) selection;
         if (!sSelection.isEmpty()) {
            System.out.println("Outline Selection");
         }
      }
   }

   public void setInput(Object input) {
      if (input instanceof ArtifactEditor) {
         this.editor = (ArtifactEditor) input;
         if (getTreeViewer() != null && Widgets.isAccessible(getTreeViewer().getTree())) {
            getTreeViewer().setInput(editor != null ? editor : "No Input Available");
         }
      }
   }

   public void refresh() {
      TreeViewer viewer = getTreeViewer();
      if (viewer != null && Widgets.isAccessible(viewer.getTree())) {
         viewer.refresh();
      }
   }

   private final class InternalLabelProvider extends LabelProvider {

      private final List<AttributeTypeContainer> containers;

      public InternalLabelProvider() {
         this.containers = new ArrayList<>();
      }

      @Override
      public String getText(Object element) {
         if (element instanceof ArtifactEditorInput) {
            return ((ArtifactEditorInput) element).getName();
         } else if (element instanceof AttributeTypeContainer) {
            return ((AttributeTypeContainer) element).getName();
         }
         return String.valueOf(element);
      }

      @Override
      public Image getImage(Object element) {
         if (element instanceof ArtifactEditorInput) {
            containers.clear();
            return ((ArtifactEditorInput) element).getImage();
         } else if (element instanceof AttributeTypeContainer) {
            AttributeTypeContainer container = (AttributeTypeContainer) element;
            containers.add(container);
            return container.isEditable() ? ImageManager.getImage(FrameworkImage.EDIT_ARTIFACT) : ImageManager.getImage(
               FrameworkImage.ADD_GREEN);
         } else if (element instanceof AttributeTypeId) {
            AttributeTypeId type = (AttributeTypeId) element;
            for (AttributeTypeContainer container : containers) {
               if (container.contains(type)) {
                  return container.isEditable() ? ImageManager.getImage(
                     FrameworkImage.ATTRIBUTE_SUB_A) : ImageManager.getImage(FrameworkImage.ATTRIBUTE_DISABLED);
               }
            }
         }
         return null;
      }
   }

   private final class InternalContentProvider implements ITreeContentProvider {

      @Override
      public void dispose() {
         // do nothing
      }

      @Override
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
         // do nothing
      }

      @Override
      public Object[] getChildren(Object element) {
         List<Object> items = new ArrayList<>();

         if (element instanceof ArtifactEditor) {
            ArtifactEditorInput editorInput = ((ArtifactEditor) element).getEditorInput();
            items.add(editorInput);
         } else if (element instanceof ArtifactEditorInput) {
            try {
               Artifact artifact = ((ArtifactEditorInput) element).getArtifact();
               boolean isEditable = !artifact.isReadOnly();
               items.add(new AttributeTypeContainer(isEditable ? "Editable" : "Readable", true,
                  AttributeTypeUtil.getTypesWithData(artifact)));
               items.add(new AttributeTypeContainer(isEditable ? "Add to form before editing" : "Empty Types", false,
                  AttributeTypeUtil.getEmptyTypes(artifact)));
            } catch (OseeCoreException ex) {
               items.add(Lib.exceptionToString(ex));
            }
         } else if (element instanceof AttributeTypeContainer) {
            return ((AttributeTypeContainer) element).getTypes().toArray();
         } else if (element instanceof String) {
            items.add(element);
         }
         return items.toArray(new Object[items.size()]);
      }

      @Override
      public Object getParent(Object element) {
         if (element instanceof ArtifactEditorInput) {
            return editor;
         } else if (element instanceof String) {
            return editor;
         }
         return null;
      }

      @Override
      public boolean hasChildren(Object element) {
         if (element instanceof String) {
            return false;
         } else if (element instanceof ArtifactEditorInput) {
            return ((ArtifactEditorInput) element).getArtifact() != null;
         } else if (element instanceof AttributeTypeContainer) {
            return !((AttributeTypeContainer) element).getTypes().isEmpty();
         } else if (element instanceof Artifact) {
            try {
               Artifact artifact = (Artifact) element;
               return !artifact.getAttributeTypes().isEmpty();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
         return false;
      }

      @Override
      public Object[] getElements(Object inputElement) {
         return getChildren(inputElement);
      }
   }

   private final static class AttributeTypeContainer {
      private final List<AttributeTypeToken> types;
      private final String name;
      private final boolean editable;

      public AttributeTypeContainer(String name, boolean editable, List<AttributeTypeToken> types) {
         this.name = name;
         this.editable = editable;
         this.types = types;
      }

      public String getName() {
         return name;
      }

      public List<AttributeTypeToken> getTypes() {
         return types;
      }

      public boolean isEditable() {
         return editable;
      }

      public boolean contains(AttributeTypeId type) {
         return getTypes().contains(type);
      }
   }
}