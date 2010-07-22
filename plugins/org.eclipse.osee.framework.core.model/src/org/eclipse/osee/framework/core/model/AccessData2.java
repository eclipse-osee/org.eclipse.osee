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
package org.eclipse.osee.framework.core.model;

import java.util.Collection;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public class AccessData2 {

	private final CompositeKeyHashMap<Object, Object, Access<?>> accessMap =
				new CompositeKeyHashMap<Object, Object, Access<?>>();

	public AccessData2() {
	}

	public void add(Object key, Access<?> data) throws OseeCoreException {
		Conditions.checkNotNull(key, "access key");
		Conditions.checkNotNull(data, "access data");

		Access<?> access = accessMap.get(key, data.getId());
		if (access == null) {
			accessMap.put(key, data.getId(), data);
		} else {
			PermissionEnum original = access.getPermission();
			PermissionEnum newPermission = data.getPermission();
			PermissionEnum netPermission = PermissionEnum.getMostRestrictive(original, newPermission);
			access.setPermission(netPermission);
		}
	}

	public Collection<Access<?>> getAccess(Object key) throws OseeCoreException {
		Conditions.checkNotNull(key, "access key");
		// TODO clone each access data object? or Hide Method
		return accessMap.getValues(key);
	}

	public boolean matches(PermissionEnum toMatch, IBasicArtifact<?> artifact, IAttributeType attrTypeToMatch1, IRelationType relationTypeToMatch1) throws OseeCoreException {
		boolean result = false;

		// Filter 1 - Branch - input Branch
		// input branch
		// output matched
		MutableBoolean matched = new MutableBoolean(true);
		Branch branchToMatch = artifact.getBranch();
		for (Access<?> data : getAccess(branchToMatch)) {
			Object object = data.getAccessObject();
			if (branchToMatch.equals(object)) {
				matched.setValue(data.getPermission().matches(toMatch));
				break;
			}
		}

		// Filter 5 - RelationType
		// input artifact, relationType
		// output matched
		IRelationType relationTypeToMatch = relationTypeToMatch1;
		for (Access<?> data : getAccess(artifact)) {
			Object object = data.getAccessObject();
			if (relationTypeToMatch.equals(object)) {
				matched.setValue(data.getPermission().matches(toMatch));
				break;
			}
		}

		if (matched.getValue()) {
			if (matched.getValue()) {
				// Filter 2  - Artifact Type
				// input artifact, artifactType
				// output matched
				IArtifactType typeToMatch = artifact.getArtifactType();
				for (Access<?> data : getAccess(artifact)) {
					Object object = data.getAccessObject();
					if (typeToMatch.equals(object)) {
						matched.setValue(data.getPermission().matches(toMatch));
						break;
					}
				}

				if (matched.getValue()) {
					// Filter 3 - Artifact
					// input artifact
					// output matched
					for (Access<?> data : getAccess(artifact)) {
						Object object = data.getAccessObject();
						if (artifact.equals(object)) {
							matched.setValue(data.getPermission().matches(toMatch));
							break;
						}
					}

					if (matched.getValue()) {
						// Filter 4 - Attribute Type
						// input artifact, attributeType
						// output matched
						IAttributeType attrTypeToMatch = attrTypeToMatch1;
						for (Access<?> data : getAccess(artifact)) {
							Object object = data.getAccessObject();
							if (attrTypeToMatch.equals(object)) {
								matched.setValue(data.getPermission().matches(toMatch));
								break;
							}
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Entry<Pair<Object, Object>, Access<?>> entry : accessMap.entrySet()) {
			builder.append(entry.getKey());
			builder.append(entry.getValue());
			builder.append(",\n");
		}
		return builder.toString();
	}
	public static class Access<T> {
		private PermissionEnum permission;
		private final T accessObject;

		public Access(T accessObject, PermissionEnum permission) {
			this.accessObject = accessObject;
			this.permission = permission;
		}

		public T getId() {
			return accessObject;
		}

		public PermissionEnum getPermission() {
			return permission;
		}

		public T getAccessObject() {
			return accessObject;
		}

		public void setPermission(PermissionEnum permission) {
			this.permission = permission;
		}

		@Override
		public int hashCode() {
			return accessObject.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			return accessObject.equals(obj);
		}

		@Override
		public String toString() {
			return "Access [accessObject=" + accessObject + ",permission=" + permission + "]";
		}
	}

	public static void main(String[] args) throws OseeCoreException {
		IOseeBranch branchToCheck = CoreBranches.SYSTEM_ROOT;
		IArtifactType artifactType = CoreArtifactTypes.AbstractSoftwareRequirement;
		IAttributeType attributeType = CoreAttributeTypes.PARAGRAPH_NUMBER;

		IBasicArtifact<?> artifactToCheck = new DefaultBasicArtifact(12, GUID.create(), "Hello");

		AccessData2 data = new AccessData2();
		data.add(branchToCheck, new Access<IOseeBranch>(branchToCheck, PermissionEnum.READ));

		data.add(artifactToCheck, new Access<IBasicArtifact<?>>(artifactToCheck, PermissionEnum.READ));
		data.add(artifactToCheck, new Access<IBasicArtifact<?>>(artifactToCheck, PermissionEnum.WRITE));

		data.add(artifactToCheck, new Access<IArtifactType>(artifactType, PermissionEnum.WRITE));

		data.add(artifactToCheck, new Access<IAttributeType>(attributeType, PermissionEnum.WRITE));

		System.out.println(data);
	}

	//
	// RequirementFolder1 art
	// 	
	// art.getParent... 
	// if not matches SoftwareRequirements -- 
	//             PermissionEnum - Full --- Store what?
	// else 
	//		if ( art.getArtifactType isOfType("SoftwareRequirements")){
	//            data.add(art, new Access<IAttributeType>(QualificationMethod), DENY);
	//    } else {
	//			
	//    }
	// endif 
	//
	//	deny contextId "lba.requirementer" edit 
	//	attributeType "Qualification Method" of artifactType "Software Requirement" 
	//	under "Software Requirements"

	//	Branch
	// IBasicArtifact<?>
	// ArtifactType
	// AttributeType(s)
	// RelationType(s)

	// Branch
	// Artifact, ArtifactType
	// AttributeType
	// Branch, ArtifactType
	// AttributeType ?

}
