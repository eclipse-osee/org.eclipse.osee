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
package org.eclipse.osee.framework.ui.service.control.data;

import org.eclipse.osee.framework.ui.service.control.renderer.IRenderer;
import net.jini.core.lookup.ServiceID;

/**
 * @author Roberto E. Escobar
 */
public interface IJiniService extends IRenderer {

   public ServiceID getServiceID();
}
