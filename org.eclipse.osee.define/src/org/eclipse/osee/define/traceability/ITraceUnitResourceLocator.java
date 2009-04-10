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
package org.eclipse.osee.define.traceability;

import java.nio.CharBuffer;
import org.eclipse.osee.define.utility.IResourceLocator;

/**
 * @author Roberto E. Escobar
 */
public interface ITraceUnitResourceLocator extends IResourceLocator {

   public String UNIT_TYPE_UNKNOWN = "Unknown";

   public String getTraceUnitType(String name, CharBuffer fileBuffer);

}
