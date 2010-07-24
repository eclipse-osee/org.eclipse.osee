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
package org.eclipse.osee.coverage.util;

import java.util.Collection;
import org.eclipse.osee.coverage.model.CoverageImport;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface ISaveable {

   public Result save() throws OseeCoreException;

   public Result save(Collection<ICoverage> coverages) throws OseeCoreException;

   public Result saveImportRecord(SkynetTransaction transaction, CoverageImport coverageImport) throws OseeCoreException;

   public Result isEditable();

   public Branch getBranch() throws OseeCoreException;
}
