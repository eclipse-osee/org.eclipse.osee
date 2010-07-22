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
package org.eclipse.osee.framework.core.model.access;

import java.util.Collection;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public class AccessData {

	private final CompositeKeyHashMap<Object, Object, AccessDetail<?>> accessMap =
				new CompositeKeyHashMap<Object, Object, AccessDetail<?>>();

	public AccessData() {
	}

	public void add(Object key, AccessDetail<?> data) throws OseeCoreException {
		Conditions.checkNotNull(key, "access key");
		Conditions.checkNotNull(data, "access data");

		AccessDetail<?> access = accessMap.get(key, data.getAccessObject());
		if (access == null) {
			accessMap.put(key, data.getAccessObject(), data);
		} else {
			PermissionEnum original = access.getPermission();
			PermissionEnum newPermission = data.getPermission();
			PermissionEnum netPermission = PermissionEnum.getMostRestrictive(original, newPermission);
			access.setPermission(netPermission);
		}
	}

	public Collection<AccessDetail<?>> getAccess(Object key) throws OseeCoreException {
		Conditions.checkNotNull(key, "access key");
		// TODO clone each access data object? or Hide Method
		return accessMap.getValues(key);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Entry<Pair<Object, Object>, AccessDetail<?>> entry : accessMap.entrySet()) {
			builder.append(entry.getKey().getFirst());
			builder.append(entry.getValue());
			builder.append(",\n");
		}
		return builder.toString();
	}
}