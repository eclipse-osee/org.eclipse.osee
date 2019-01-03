/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workdef;

import java.util.Collection;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkDefinitionSheetProvider {

   public Collection<WorkDefinitionSheet> getWorkDefinitionSheets();
}
