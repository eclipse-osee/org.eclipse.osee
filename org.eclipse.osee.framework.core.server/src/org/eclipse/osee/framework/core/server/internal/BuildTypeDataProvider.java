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
package org.eclipse.osee.framework.core.server.internal;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.OseeInfo;

/**
 * @author Roberto E. Escobar
 */
public class BuildTypeDataProvider {

   private static final String BUILD_DATA_KEY = "osee.build.designation";

   public String getData() throws OseeCoreException {
      return OseeInfo.getValue(BUILD_DATA_KEY);
   }

}