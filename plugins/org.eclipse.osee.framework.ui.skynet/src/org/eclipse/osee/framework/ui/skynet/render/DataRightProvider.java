/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.render;

import org.eclipse.osee.define.report.api.DataRightInput;
import org.eclipse.osee.define.report.api.DataRightResult;

/**
 * @author Angel Avila
 */
public interface DataRightProvider {

   DataRightResult getDataRights(DataRightInput request);

   DataRightInput createRequest();

}