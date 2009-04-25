/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.artifact.editor.pages;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.BaseArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.implementations.NewArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.sections.AttributeTypeUtil;
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
                  getTreeViewer().refresh();
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
      getTreeViewer().refresh();
   }

   private final class InternalLabelProvider extends LabelProvider {

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
       */
      @Override
      public String getText(Object element) {
         if (element instanceof BaseArtifactEditorInput) {
            return ((BaseArtifactEditorInput) element).getName();
         } else if (element instanceof AttributeTypeContainer) {
            return ((AttributeTypeContainer) element).setName;
         }
         return String.valueOf(element);
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
       */
      @Override
      public Image getImage(Object element) {
         if (element instanceof BaseArtifactEditorInput) {
            return ((BaseArtifactEditorInput) element).getImage();
         } else if (element instanceof AttributeTypeContainer) {
            String name = ((AttributeTypeContainer) element).setName;
            if (name.contains("Editting")) {
               return SkynetGuiPlugin.getInstance().getImage("edit_artifact.gif");
            } else {
               return SkynetGuiPlugin.getInstance().getImage("add.gif");
            }
         } else if (element instanceof AttributeType) {
            return SkynetGuiPlugin.getInstance().getImage("attribute.gif");
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
               items.add(new AttributeTypeContainer("Editting", AttributeTypeUtil.getTypesWithData(artifact)));
               items.add(new AttributeTypeContainer("Additional", AttributeTypeUtil.getEmptyTypes(artifact)));
            } catch (OseeCoreException ex) {
               items.add(Lib.exceptionToString(ex));
            }
         } else if (element instanceof AttributeTypeContainer) {
            return ((AttributeTypeContainer) element).types;
         } else if (element instanceof AttributeType) {
            System.out.println("Here");
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
            return ((AttributeTypeContainer) element).types.length > 0;
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
      private static final AttributeType[] EMPTY_TYPES = new AttributeType[0];
      AttributeType[] types;
      String setName;

      AttributeTypeContainer(String setName, AttributeType... data) {
         this.setName = setName;
         this.types = data;
         if (types == null) {
            this.types = EMPTY_TYPES;
         }
      }
   }

}
