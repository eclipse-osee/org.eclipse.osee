/*
 * Created on Mar 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event.filter;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.event.IEventListener;

/**
 * @author Donald G. Dunne
 */
public class FilteredEventListener implements IEventListener {

   private final IEventListener eventListener;
   private final Collection<IEventFilter> eventFilters;

   public FilteredEventListener(IEventListener eventListener, IEventFilter... eventFilters) {
      this.eventListener = eventListener;
      this.eventFilters = Collections.getAggregate(eventFilters);
   }

   public boolean isOfType(Class<?> clazz) {
      return clazz.isAssignableFrom(getClass());
   }

   public IEventListener getEventListener() {
      return eventListener;
   }

   public Collection<IEventFilter> getEventFilters() {
      return eventFilters;
   }
}
