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
package org.eclipse.osee.ats.core.config;

import java.util.List;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsConfig {

   <A extends IAtsConfigObject> List<A> getByTag(String tag, Class<A> clazz) throws OseeCoreException;

   <A extends IAtsConfigObject> A getSoleByTag(String tag, Class<A> clazz) throws OseeCoreException;

   <A extends IAtsConfigObject> List<A> get(Class<A> clazz) throws OseeCoreException;

   <A extends IAtsConfigObject> A getSoleByUuid(long uuid, Class<A> clazz) throws OseeCoreException;

   IAtsConfigObject getSoleByUuid(long uuid) throws OseeCoreException;

   void getReport(XResultData rd) throws OseeCoreException;

   void invalidate(IAtsConfigObject configObject) throws OseeCoreException;

   <A extends IAtsConfigObject> List<A> getById(long id, Class<A> clazz);
}