/*
 * Created on Jun 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.group;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IGroupExplorerProvider {

   public String getGroupExplorerName() throws OseeCoreException;
}
