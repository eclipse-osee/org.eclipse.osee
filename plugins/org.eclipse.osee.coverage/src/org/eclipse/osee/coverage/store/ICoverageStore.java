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
package org.eclipse.osee.coverage.store;

import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface ICoverageStore {

   public abstract void load(CoverageOptionManager coverageOptionManager) throws OseeCoreException;

   public abstract Result save() throws OseeCoreException;

   public void delete(boolean purge) throws OseeCoreException;

}
