/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.event;

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
