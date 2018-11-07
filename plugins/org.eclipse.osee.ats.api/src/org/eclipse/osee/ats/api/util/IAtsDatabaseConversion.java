/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.util;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.core.util.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface IAtsDatabaseConversion {

   public String getName();

   public void run(XResultData data, boolean reportOnly, AtsApi atsApi);

   public String getDescription();

}
