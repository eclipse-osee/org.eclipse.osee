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
package org.eclipse.osee.framework.core.dsl.ui.integration.internal;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessPermissionEnum;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactInstanceRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeOfArtifactTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AttributeTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.HierarchyRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.ObjectRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.PermissionRule;
import org.eclipse.osee.framework.core.dsl.oseeDsl.RelationTypeRestriction;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactRef;
import org.eclipse.osee.framework.core.dsl.oseeDsl.util.OseeDslSwitch;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.access.AccessDetail;

/**
 * @author Roberto E. Escobar
 */
public class OseeAccessModelInterpreter implements AccessModelInterpreter {

	public static interface ObjectDataAccessor {

		Collection<Identity> getHierarchy(Object object);

		Collection<IAttributeType> getAttributeTypes(Object object);

		Collection<IArtifactType> getArtifactSuperTypes(Object object);
	}

	public OseeAccessModelInterpreter() {
	}

	@Override
	public AccessContext getContext(Collection<AccessContext> contexts, AccessContextId contextId) {
		AccessContext toReturn = null;
		for (AccessContext accessContext : contexts) {
			if (contextId.equals(accessContext.getTypeGuid())) {
				toReturn = accessContext;
			}
		}
		return toReturn;
	}

	@Override
	public void computeAccessDetails(AccessContext context, Object objectToCheck, Collection<AccessDetail<?>> details) {
		computeAccess(context, objectToCheck, details);
		for (AccessContext superContext : context.getSuperAccessContexts()) {
			computeAccess(superContext, objectToCheck, details);
		}
	}

	private void computeAccess(AccessContext context, Object objectToCheck, Collection<AccessDetail<?>> details) {
		context.getAccessRules();

		Collection<HierarchyRestriction> restrictions = context.getHierarchyRestrictions();
		for (HierarchyRestriction restriction : restrictions) {
			XArtifactRef artifactRef = restriction.getArtifact();
			// Apply childrenOf Rule;
			boolean isApplicable = false;
			if (isApplicable) {
				restriction.getAccessRules();
			}
		}

	}

	private AccessPermissionEnum getLeastRestrictive(AccessPermissionEnum permission1, AccessPermissionEnum permission2) {
		if (permission1 == AccessPermissionEnum.ALLOW) {

		}
		return permission1;
	}

	public static void checkRuleConflict(Collection<PermissionRule> rules) throws OseeCoreException {
		ObjectRestrictionSwitch checker = new ObjectRestrictionSwitch();
		for (PermissionRule rule : rules) {
			ObjectRestriction restriction = rule.getObjectRestriction();
			checker.doSwitch(restriction);
		}
		if (checker.hasConflicts()) {

		}
	}

	public static PermissionEnum toCorePermission(AccessPermissionEnum modelPermission) {
		PermissionEnum toReturn = PermissionEnum.READ;
		if (modelPermission == AccessPermissionEnum.ALLOW) {
			toReturn = PermissionEnum.WRITE;
		}
		return toReturn;
	}

	private static final class ObjectRestrictionSwitch extends OseeDslSwitch<Object> {
		Collection<Identity> artifactInstances;

		//		Collection<Identity> 
		@Override
		public Object caseArtifactInstanceRestriction(ArtifactInstanceRestriction object) {
			return object;
		}

		@Override
		public Object caseArtifactTypeRestriction(ArtifactTypeRestriction object) {
			return object;
		}

		@Override
		public Object caseRelationTypeRestriction(RelationTypeRestriction object) {
			return object;
		}

		@Override
		public Object caseAttributeTypeRestriction(AttributeTypeRestriction object) {
			return object;
		}

		@Override
		public Object caseAttributeTypeOfArtifactTypeRestriction(AttributeTypeOfArtifactTypeRestriction object) {
			object.getArtifactType();
			object.getAttributeType();

			return object;
		}

		public boolean hasConflicts() {
			return false;
		}
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
	// Branch allows Type
	//	deny contextId "lba.requirementer" edit
	//	attributeType "Qualification Method" of artifactType "Software Requirement"
	//	under "Software Requirements"
}
