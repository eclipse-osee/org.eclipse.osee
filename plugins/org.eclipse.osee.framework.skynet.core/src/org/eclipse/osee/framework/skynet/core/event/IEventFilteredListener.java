/*
 * Created on Jun 28, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event;

import java.util.List;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;

/**
 * @author Donald G. Dunne
 */
public interface IEventFilteredListener extends IEventListener {

   public List<? extends IEventFilter> getEventFilters();
}
