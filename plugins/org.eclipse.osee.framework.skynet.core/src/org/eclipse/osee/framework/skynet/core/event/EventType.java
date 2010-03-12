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
package org.eclipse.osee.framework.skynet.core.event;

/**
 * @author Donald G. Dunne
 */
public enum EventType {

   // Event is only sent locally and not sent remotely
   LocalOnly,

   // Event is not send locally, but is sent remotely
   RemoteOnly,

   // Event is sent both locally and remotely
   LocalAndRemote
}
