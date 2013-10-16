/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.reports;

import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Roberto E. Escobar
 */
public interface AtsReport<IN, OUT> {

   String getName();

   KeyedImage getKeyedImage();

   IN getInputParameters() throws OseeCoreException;

   OUT createOutputParameters() throws OseeCoreException;

   IOperation createReportOperation(IN input, OUT output, TableLoadOption... tableLoadOptions) throws OseeCoreException;

   void displayResults(OUT output) throws OseeCoreException;
}
