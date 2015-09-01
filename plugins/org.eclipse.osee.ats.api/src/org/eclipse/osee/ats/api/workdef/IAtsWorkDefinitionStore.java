/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkDefinitionStore {

   boolean isWorkDefinitionExists(String workDefId) throws OseeCoreException;

   String loadWorkDefinitionString(String workDefId) throws OseeCoreException;

   String loadRuleDefinitionString() throws OseeCoreException;

   List<Pair<String, String>> getWorkDefinitionStrings() throws OseeCoreException;
}
