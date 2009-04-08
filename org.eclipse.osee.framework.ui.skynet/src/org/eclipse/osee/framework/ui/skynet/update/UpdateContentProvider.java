/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.update;

import java.util.Collection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Jeff C. Phillips
 *
 */
public class UpdateContentProvider implements ITreeContentProvider{

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
    */
   @Override
   public Object[] getChildren(Object parentElement) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
    */
   @Override
   public Object getParent(Object element) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
    */
   @Override
   public boolean hasChildren(Object element) {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
    */
   @Override
   public Object[] getElements(Object inputElement) {
      if (inputElement instanceof Object[]) {
         return (Object[]) inputElement;
      }
        if (inputElement instanceof Collection) {
         return ((Collection) inputElement).toArray();
      }
        return new Object[0];
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IContentProvider#dispose()
    */
   @Override
   public void dispose() {
      // TODO Auto-generated method stub
      
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
    */
   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // TODO Auto-generated method stub
      
   }

}
