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
package org.eclipse.osee.framework.skynet.core.transaction;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AccessData;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Jeff C. Phillips
 */
public class SkynetTransactionAccessHandler extends SkynetTransactionHandler {

   public final IAccessControlService service;

   public SkynetTransactionAccessHandler(IAccessControlService service) {
      super();
      this.service = service;
   }

   @Override
   public IStatus onCheck(IProgressMonitor monitor) {
      IStatus status = Status.OK_STATUS;
      try {
         AccessData accessData = service.getAccessData(getUserArtifact(), getItemsToPersist());
      } catch (OseeCoreException ex) {
         status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error");
      }
      return status;
   }
}
