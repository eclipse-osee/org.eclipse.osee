/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.artifact.editor.panels;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactEditorOutlinePage extends Page implements IContentOutlinePage {
   private Composite composite;
   private TreeViewer viewer;
   private AbstractArtifactEditor editor;

   public ArtifactEditorOutlinePage(AbstractArtifactEditor editor) {
      this.editor = editor;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.Page#createControl(org.eclipse.swt.widgets.Composite)
    */
   @Override
   public void createControl(Composite parent) {
      composite = new Composite(parent, SWT.BORDER);
      composite.setLayout(new FillLayout(SWT.VERTICAL));

      viewer = new TreeViewer(composite, SWT.BORDER | SWT.MULTI);
      viewer.getTree().setLayoutData(new FillLayout(SWT.VERTICAL));
      viewer.setContentProvider(new InternalContentProvider());
      viewer.setLabelProvider(new InternalLabelProvider());
      viewer.setInput(editor.getEditorInput());
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.Page#getControl()
    */
   @Override
   public Control getControl() {
      return composite;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.Page#setFocus()
    */
   @Override
   public void setFocus() {
      if (getControl() != null) {
         getControl().setFocus();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
    */
   @Override
   public void addSelectionChangedListener(ISelectionChangedListener listener) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
    */
   @Override
   public ISelection getSelection() {
      return StructuredSelection.EMPTY;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
    */
   @Override
   public void removeSelectionChangedListener(ISelectionChangedListener listener) {

   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
    */
   @Override
   public void setSelection(ISelection selection) {
   }

   public void setInput(Object editorInput) {
      viewer.setInput(editorInput);
   }

   public void refresh() {
      viewer.refresh();
   }

   private final class InternalLabelProvider extends LabelProvider implements ITableLabelProvider {
      public String getColumnText(Object obj, int index) {
         return String.valueOf(obj);
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
       */
      @Override
      public Image getImage(Object element) {
         if (element instanceof Artifact) {
            return ((Artifact) element).getImage();
         } else if (element instanceof AttributeType) {
            //            AttributeType type = (AttributeType) obj;
            //            Class<?> clazz = type.getBaseAttributeClass();
            //            ISharedImages.
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
         }
         return null;
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
       */
      @Override
      public Image getColumnImage(Object element, int columnIndex) {
         if (columnIndex == 0) {
            return getImage(element);
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
         if (element instanceof String) {
            return new Object[] {element};
         } else if (element instanceof ArtifactEditorInput) {
            Artifact artifact = ((ArtifactEditorInput) element).getArtifact();
            return new Object[] {artifact};
         } else if (element instanceof Artifact) {
            Artifact artifact = (Artifact) element;
            try {
               return artifact.getAttributeTypes().toArray();
            } catch (OseeCoreException ex) {
               ex.printStackTrace();
            }
         }
         return null;
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
       */
      @Override
      public Object getParent(Object element) {
         return null;
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
       */
      @Override
      public boolean hasChildren(Object element) {
         if (element instanceof String) {
            return false;
         } else if (element instanceof ArtifactEditorInput) {
            Artifact artifact = ((ArtifactEditorInput) element).getArtifact();
            return artifact != null;
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

}
