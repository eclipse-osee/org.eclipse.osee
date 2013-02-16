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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkDefinitionStore {

   public abstract String loadWorkDefinitionString(String workDefId);

   public abstract IAttributeResolver getAttributeResolver();

   public abstract IUserResolver getUserResolver();

   public abstract List<Pair<String, String>> getWorkDefinitionStrings() throws OseeCoreException;
}
