package org.eclipse.osee.framework.types.bridge.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.osee.framework.OseeTypesStandaloneSetup;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.oseeTypes.Model;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.OseeData;
import org.eclipse.osee.framework.types.bridge.internal.Activator;

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
		// TypeValidityManager.getAttributeTypesFromArtifactType(artifactType,
		// branch);

	
		// Write Model to File;

		// model.
		// resource.save(outputStream, options);
	}

}
