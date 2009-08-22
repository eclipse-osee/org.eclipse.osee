package org.eclipse.osee.framework.types.bridge.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.osee.framework.OseeTypesStandaloneSetup;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.oseeTypes.Import;
import org.eclipse.osee.framework.oseeTypes.Model;
import org.eclipse.osee.framework.oseeTypes.Type;
import org.eclipse.osee.framework.types.bridge.Activator;

public class TextModelToOseeOperation extends AbstractOperation {
	private final java.net.URI resource;

	public TextModelToOseeOperation(java.net.URI resource) {
		super("OSEE Text Model to OSEE", Activator.PLUGIN_ID);
		this.resource = resource;
	}

	@Override
	protected void doWork(IProgressMonitor monitor) throws Exception {
		OseeTypesStandaloneSetup.doSetup();

		ResourceSet resourceSet = new ResourceSetImpl();

		URI uri = URI.createURI(resource.toASCIIString());
		Resource resource = resourceSet.getResource(uri, true);
		Model model = (Model) resource.getContents().get(0);
		for (Import importEntry : model.getImports()) {
			System.out.println("Import: " + importEntry.getImportURI());
		}
		for (Type type : model.getElements()) {
			System.out.println(type.getName());
			for (EObject eObject : type.eContents()) {
				System.out.println(eObject.toString());
			}
		}
	}

}
