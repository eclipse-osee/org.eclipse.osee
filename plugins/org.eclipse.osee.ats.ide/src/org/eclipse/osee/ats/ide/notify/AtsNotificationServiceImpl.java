/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.notify;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.core.notify.AbstractAtsNotificationService;
import org.eclipse.osee.framework.core.util.OseeEmail;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmailIde;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationServiceImpl extends AbstractAtsNotificationService {

   public AtsNotificationServiceImpl(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   protected OseeEmail createOseeEmail() {
      return OseeEmailIde.create();
   }

}
