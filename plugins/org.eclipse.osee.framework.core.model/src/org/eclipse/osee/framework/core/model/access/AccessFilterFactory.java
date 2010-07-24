/*
 * Created on Jul 21, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.access;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.model.access.exp.IAccessFilter;

public class AccessFilterFactory {

	public Collection<IAccessFilter> createFilter() {
		List<IAccessFilter> filters = new ArrayList<IAccessFilter>();

		//		Collection<IOseeBranch> allowedBranches = new List<IOseeBranch>();
		//		Collection<IBasicArtifact<T>> allowedArtifactType = new List<IArtifactType>();
		//		Collection<IArtifactType> allowedArtifactType = new List<IArtifactType>();
		//
		//		filters.add(new BranchAccessFilter(artifact, branchPermission));
		//		filters.add(new ArtifactAccessFilter());
		//		filters.add(new ArtifactTypeFilter());
		//		filters.add(new AttributeTypeFilter());
		//		filters.add(new RelationTypeFilter());

		return filters;
	}
}
