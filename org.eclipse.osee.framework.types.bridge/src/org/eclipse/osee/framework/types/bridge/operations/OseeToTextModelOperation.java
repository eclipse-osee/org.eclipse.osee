package org.eclipse.osee.framework.types.bridge.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.types.bridge.Activator;

public class OseeToTextModelOperation extends AbstractOperation {
	private final java.net.URI resource;

	public OseeToTextModelOperation(java.net.URI resource) {
		super("OSEE to Text Model", Activator.PLUGIN_ID);
		this.resource = resource;
	}

	@Override
	protected void doWork(IProgressMonitor monitor) throws Exception {
		// TODO Add work here
		AttributeTypeManager.getAllTypes();
		RelationTypeManager.getAllTypes();
		ArtifactTypeManager.getAllTypes();
		
//		TypeValidityManager.getAttributeTypesFromArtifactType(artifactType, branch);
		
	}

}
