/*
 * Created on Jun 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.conflict;

import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.IDiffContainer;

/**
 * @author b1565043
 *
 */
public class MergeNode extends DiffNode implements IEditableContent{

   /**
    * @param parent
    * @param kind
    * @param ancestor
    * @param left
    * @param right
    */
   public MergeNode(IDiffContainer parent, int kind, ITypedElement ancestor, ITypedElement left, ITypedElement right) {
      super(parent, kind, ancestor, left, right);
   }

   /* (non-Javadoc)
    * @see org.eclipse.compare.IEditableContent#isEditable()
    */
   @Override
   public boolean isEditable() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.compare.IEditableContent#replace(org.eclipse.compare.ITypedElement, org.eclipse.compare.ITypedElement)
    */
   @Override
   public ITypedElement replace(ITypedElement dest, ITypedElement src) {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.compare.IEditableContent#setContent(byte[])
    */
   @Override
   public void setContent(byte[] newContent) {
   }

}
