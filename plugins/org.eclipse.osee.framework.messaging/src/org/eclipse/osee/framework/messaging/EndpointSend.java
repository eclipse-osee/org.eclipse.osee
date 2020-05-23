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

package org.eclipse.osee.framework.messaging;

import java.util.Properties;

/**
 * @author Andrew M. Finkbeiner
 */
public interface EndpointSend {
   public void start(Properties properties);

   public void send(Message message, ExceptionHandler exceptionHandler);
}
