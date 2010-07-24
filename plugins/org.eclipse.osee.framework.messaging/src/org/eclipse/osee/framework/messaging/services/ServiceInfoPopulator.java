/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.services;

import java.util.List;
import org.eclipse.osee.framework.messaging.services.messages.ServiceDescriptionPair;

/**
 * @author Andrew M. Finkbeiner
 */
public interface ServiceInfoPopulator {
   void updateServiceInfo(List<ServiceDescriptionPair> serviceDescription);
}
