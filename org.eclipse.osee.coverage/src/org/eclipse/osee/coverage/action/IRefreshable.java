/*
 * Created on Oct 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.action;


/**
 * @author Donald G. Dunne
 */
public interface IRefreshable {

   public void refresh(Object element);

   public void update(Object element);

   public void remove(Object element);
}
