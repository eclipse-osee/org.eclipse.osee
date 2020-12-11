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

package org.eclipse.osee.mail.api;

import org.eclipse.osee.framework.core.util.MailStatus;

/**
 * @author Roberto E. Escobar
 */
public interface MailCallback {

   void onCancelled(String uuid);

   void onSuccess(String uuid, MailStatus status);

   void onFailure(String uuid, Throwable throwable);

}
