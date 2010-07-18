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

import org.eclipse.osee.coverage.model.ICoverage;

/**
 * @author Donald G. Dunne
 */
public abstract class CoverageStore implements ICoverageStore {

   protected final ICoverage coverage;

   public CoverageStore(ICoverage coverage) {
      this.coverage = coverage;
   }

}
