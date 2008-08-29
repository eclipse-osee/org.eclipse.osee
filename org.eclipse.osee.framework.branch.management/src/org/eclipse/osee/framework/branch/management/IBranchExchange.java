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
package org.eclipse.osee.framework.branch.management;

import java.util.List;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public interface IBranchExchange {

   public IResourceLocator exportBranch(String exportName, Options options, int... branchIds) throws Exception;

   public IResourceLocator exportBranch(String exportName, Options options, List<Integer> branchIds) throws Exception;

   public void importBranch(IResourceLocator fileToImport, Options options, int... branchIds) throws Exception;

   public void importBranch(IResourceLocator fileToImport, Options options, List<Integer> branchIds) throws Exception;
}
