/*
 * Created on Oct 24, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.group;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class GroupTreeViewer extends TreeViewer {

   private final GroupExplorer groupExplorer;

   /**
    * @param parent
    */
   public GroupTreeViewer(GroupExplorer groupExplorer, Composite parent) {
      super(parent);
      this.groupExplorer = groupExplorer;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.StructuredViewer#refresh()
    */
   @Override
   public void refresh() {
      super.refresh();
      //      System.out.println("TreeViewer: refresh");
      groupExplorer.restoreExpandedAndSelection();
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.StructuredViewer#refresh(boolean)
    */
   @Override
   public void refresh(boolean updateLabels) {
      super.refresh(updateLabels);
      //      System.out.println("TreeViewer: refresh(updateLabels)");
      groupExplorer.restoreExpandedAndSelection();
   }

}
