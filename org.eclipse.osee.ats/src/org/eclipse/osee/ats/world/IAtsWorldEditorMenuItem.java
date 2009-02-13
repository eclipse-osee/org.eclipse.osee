/*
 * Created on Feb 13, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorldEditorMenuItem {

   public String getName() throws OseeCoreException;

   public void run(WorldEditor worldEditor) throws OseeCoreException;

}
