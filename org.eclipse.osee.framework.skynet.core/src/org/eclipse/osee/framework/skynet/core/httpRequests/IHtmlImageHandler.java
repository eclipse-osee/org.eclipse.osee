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
package org.eclipse.osee.framework.skynet.core.httpRequests;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Roberto E. Escobar
 */
public interface IHtmlImageHandler {

   boolean isValid(InputStream is);

   void convert(InputStream is, OutputStream os) throws IOException;

}
