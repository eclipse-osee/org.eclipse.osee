/*
 * Created on Apr 24, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.db.connection;

/**
 * @author b1528444
 */
public interface IDbConnectionListener {

   /**
    * @param open
    */
   public void onConnectionStatusUpdate(boolean open);

}
