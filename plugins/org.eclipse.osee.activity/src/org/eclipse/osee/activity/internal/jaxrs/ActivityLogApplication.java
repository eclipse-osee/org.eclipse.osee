/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.activity.internal.jaxrs;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.activity.api.ActivityLog;

/**
 * @author Ryan D. Brooks
 */
@ApplicationPath("/")
public final class ActivityLogApplication extends Application {

   private Set<Object> singletons;

   private ActivityLog activityLog;

   public void setActivityLogger(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }

   public void start() {
      singletons = new HashSet<>();
      singletons.add(new ActivityLogResource(activityLog));
   }

   public void stop() {
      singletons.clear();
      singletons = null;
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

}
