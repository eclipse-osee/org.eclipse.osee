/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.model.change.CompareData;

/**
 * @author Theron Virgin
 */
public interface IVbaDiffGenerator {

   public void generate(IProgressMonitor monitor, CompareData compareData);
}