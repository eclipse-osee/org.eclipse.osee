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
package org.eclipse.osee.ats.api.workflow.state;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkStateFactory {

   public abstract String toStoreStr(IAtsStateManager stateMgr, String stateName) throws OseeCoreException;

   public abstract WorkState fromStoreStr(String storeStr) throws OseeCoreException;

   public abstract String getStorageString(Collection<IAtsUser> users) throws OseeCoreException;

   public abstract List<IAtsUser> getUsers(String sorageString);

}