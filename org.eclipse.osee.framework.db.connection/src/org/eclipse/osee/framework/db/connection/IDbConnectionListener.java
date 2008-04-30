/*
 * Created on Apr 24, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.db.connection;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IDbConnectionListener {

   /**
    * @param open
    */
   public void onConnectionStatusUpdate(boolean open);

}
