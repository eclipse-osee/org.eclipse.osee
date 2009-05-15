/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.artifact.editor.pages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.BaseArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.implementations.NewArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.AttributeTypeUtil;
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

   private NewArtifactEditor editor;

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.Page#createControl(org.eclipse.swt.widgets.Composite)
    */
   @Override
   public void createControl(Composite parent) {
      super.createControl(parent);

      Tree tree = getTreeViewer().getTree();
      tree.setLayout(new FillLayout(SWT.VERTICAL));
      getTreeViewer().setContentProvider(new InternalContentProvider());
      getTreeViewer().setLabelProvider(new InternalLabelProvider());
      setInput(editor != null ? editor : "No Input Available");

      getSite().getActionBars().getToolBarManager().add(
            new Action("Refresh", SkynetGuiPlugin.getInstance().getImageDescriptor("refresh.gif")) {
               public void run() {
                  refresh();
               }
            });
      getSite().getActionBars().getToolBarManager().update(true);
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.views.contentoutline.ContentOutlinePage#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
    */
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
      if (input instanceof NewArtifactEditor) {
         this.editor = (NewArtifactEditor) input;
         if (getTreeViewer() != null) {
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
      private Image EDIT_ATTRIBUTE_IMAGE;
      private Image NON_EDIT_ATTRIBUTE_IMAGE;
      private Image ATTRIBUTE_IMAGE;
      private Image NON_ACTIVE_ATTRIBUTE_IMAGE;

      private List<AttributeTypeContainer> containers;

      public InternalLabelProvider() {
         this.containers = new ArrayList<AttributeTypeContainer>();
      }

      private void checkImages() {
         if (EDIT_ATTRIBUTE_IMAGE == null) {
            EDIT_ATTRIBUTE_IMAGE = SkynetGuiPlugin.getInstance().getImage("edit_artifact.gif");
            NON_EDIT_ATTRIBUTE_IMAGE = SkynetGuiPlugin.getInstance().getImage("add.gif");
            ATTRIBUTE_IMAGE = SkynetGuiPlugin.getInstance().getImage("attribute.gif");
            NON_ACTIVE_ATTRIBUTE_IMAGE = SkynetGuiPlugin.getInstance().getImage("disabled_attribute.gif");
         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
       */
      @Override
      public String getText(Object element) {
         if (element instanceof BaseArtifactEditorInput) {
            return ((BaseArtifactEditorInput) element).getName();
         } else if (element instanceof AttributeTypeContainer) {
            return ((AttributeTypeContainer) element).getName();
         }
         return String.valueOf(element);
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
       */
      @Override
      public Image getImage(Object element) {
         checkImages();
         if (element instanceof BaseArtifactEditorInput) {
            containers.clear();
            return ((BaseArtifactEditorInput) element).getImage();
         } else if (element instanceof AttributeTypeContainer) {
            AttributeTypeContainer container = ((AttributeTypeContainer) element);
            containers.add(container);
            return container.isEditable() ? EDIT_ATTRIBUTE_IMAGE : NON_EDIT_ATTRIBUTE_IMAGE;
         } else if (element instanceof AttributeType) {
            AttributeType type = (AttributeType) element;
            for (AttributeTypeContainer container : containers) {
               if (container.contains(type)) {
                  return container.isEditable() ? ATTRIBUTE_IMAGE : NON_ACTIVE_ATTRIBUTE_IMAGE;
               }
            }
         }
         return null;
      }
   }

   private final class InternalContentProvider implements ITreeContentProvider {

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.IContentProvider#dispose()
       */
      @Override
      public void dispose() {
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
       */
      @Override
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
       */
      @Override
      public Object[] getChildren(Object element) {
         List<Object> items = new ArrayList<Object>();

         if (element instanceof NewArtifactEditor) {
            BaseArtifactEditorInput editorInput = ((NewArtifactEditor) element).getEditorInput();
            items.add(editorInput);
         } else if (element instanceof BaseArtifactEditorInput) {
            try {
               Artifact artifact = ((BaseArtifactEditorInput) element).getArtifact();
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

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
       */
      @Override
      public Object getParent(Object element) {
         if (element instanceof BaseArtifactEditorInput) {
            return editor;
         } else if (element instanceof String) {
            return editor;
         } else if (element instanceof AttributeType) {
         }
         return null;
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
       */
      @Override
      public boolean hasChildren(Object element) {
         if (element instanceof String) {
            return false;
         } else if (element instanceof BaseArtifactEditorInput) {
            return ((BaseArtifactEditorInput) element).getArtifact() != null;
         } else if (element instanceof AttributeTypeContainer) {
            return !((AttributeTypeContainer) element).getTypes().isEmpty();
         } else if (element instanceof Artifact) {
            try {
               Artifact artifact = (Artifact) element;
               return !artifact.getAttributeTypes().isEmpty();
            } catch (OseeCoreException ex) {
               ex.printStackTrace();
            }
         }
         return false;
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
       */
      @Override
      public Object[] getElements(Object inputElement) {
         return getChildren(inputElement);
      }
   }

   private final static class AttributeTypeContainer {
      private List<AttributeType> types;
      private String name;
      private boolean editable;

      public AttributeTypeContainer(String name, boolean editable, AttributeType... data) {
         this.name = name;
         this.editable = editable;
         if (data == null) {
            this.types = Collections.emptyList();
         } else {
            this.types = Arrays.asList(data);
         }
      }

      public String getName() {
         return name;
      }

      public List<AttributeType> getTypes() {
         return types;
      }

      public boolean isEditable() {
         return editable;
      }

      public boolean contains(AttributeType type) {
         return getTypes().contains(type);
      }
   }

}
