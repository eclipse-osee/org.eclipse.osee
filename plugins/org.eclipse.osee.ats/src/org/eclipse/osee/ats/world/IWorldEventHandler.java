/*
 * Created on Jun 30, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.Collection;

/**
 * @author Donald G. Dunne
 */
public interface IWorldEventHandler {

   public WorldXViewer getWorldXViewer();

   public void removeItems(Collection<? extends Object> objects);

   public boolean isDisposed();

}
