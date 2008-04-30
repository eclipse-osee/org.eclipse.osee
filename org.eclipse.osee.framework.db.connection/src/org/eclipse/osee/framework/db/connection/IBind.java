/*
 * Created on Apr 25, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.db.connection;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IBind {
   void bind(Object obj);

   void unbind(Object obj);
}
