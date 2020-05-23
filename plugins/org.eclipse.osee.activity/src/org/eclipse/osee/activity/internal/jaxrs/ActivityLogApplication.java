/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
