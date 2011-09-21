/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.vaadin;

/**
 * @author Roberto E. Escobar
 */
public final class ApplicationConstants {

   private ApplicationConstants() {
      // Constants Class
   }

   public static final String APP_REGISTRATION_EVENT = "org/eclipse/osee/vaadin/event/APP_REGISTRATION";

   public static final String APP_DEREGISTRATION_EVENT = "org/eclipse/osee/vaadin/event/APP_DEREGISTRATION";

   public static final String APP_CONTEXT_NAME = "context.name";

   public static final String APP_COMPONENT_NAME = "component.name";
}
