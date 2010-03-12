/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management.exchange.transform;

import org.eclipse.osee.framework.branch.management.exchange.ImportController;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Ryan D. Brooks
 */
public interface IOseeDbExportTransformer {
   public abstract String applyTransform(ImportController importController) throws Exception;

   public abstract boolean isApplicable(String exportVersion) throws OseeCoreException;

   public abstract void finalizeTransform(ImportController importController) throws Exception;
}
