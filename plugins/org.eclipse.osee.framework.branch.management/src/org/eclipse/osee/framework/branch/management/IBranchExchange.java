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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.resource.management.IResourceLocator;

/**
 * @author Roberto E. Escobar
 */
public interface IBranchExchange {

   public IResourceLocator exportBranch(String exportName, PropertyStore options, List<Integer> branchIds) throws Exception;

   public void importBranch(IResourceLocator fileToImport, PropertyStore options, List<Integer> branchIds, OperationLogger logger) throws OseeCoreException;

   public IResourceLocator checkIntegrity(IResourceLocator fileToCheck) throws Exception;
}
