/*
 * Created on Dec 26, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workflow.editor.model;

import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class CancelledWorkPageShape extends WorkPageShape {

   /**
    * @param workPageDefinition
    * @throws OseeCoreException
    */
   public CancelledWorkPageShape() {
      super(null);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.model.WorkPageShape#getName()
    */
   @Override
   public String getName() {
      return "Cancelled";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.model.WorkPageShape#getToolTip()
    */
   @Override
   public String getToolTip() {
      return "Cancelled State";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.model.WorkPageShape#toString()
    */
   @Override
   public String toString() {
      return "Cancelled";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.config.editor.model.WorkPageShape#getId()
    */
   @Override
   public String getId() {
      return AtsCancelledWorkPageDefinition.ID;
   }

}
