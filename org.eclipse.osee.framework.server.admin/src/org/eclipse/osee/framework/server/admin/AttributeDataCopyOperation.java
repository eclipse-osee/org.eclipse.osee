/*
 * Created on Jun 29, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.server.admin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;

/**
 * @author Ryan Schmitt
 *
 */
public final class AttributeDataCopyOperation extends AbstractOperation {
	/**
	 * @param operationName
	 * @param pluginId
	 */
	public AttributeDataCopyOperation(String operationName, String pluginId) {
		super(operationName, pluginId);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.core.operation.AbstractOperation#doWork(org.eclipse.osee.framework.core.operation.IProgressMonitor)
	 */
	@Override
	protected void doWork(IProgressMonitor monitor) throws Exception {
		ConnectionHandlerStatement chs = new ConnectionHandlerStatement();
		String query = "SELECT oart.guid,  oart.art_id,  oattr.gamma_id,  oattr.uri " +
				"FROM osee_artifact oart,  osee_attribute oattr " +
				"WHERE " +
					"oart.art_id = oattr.art_id " +
					"AND " +
					"oattr.uri IS NOT NULL";
		chs.runPreparedQuery(query);
		
		
	}

//	private static List<Artifact> getArtifactList() {
//		List<Artifact> ret = new ArrayList<Artifact>();
//		File dir = new File(getRootDirectory());
//		String[] children = dir.list();
//		String[][] subdirs = new String[children.length][];
//		for (int i = 0; i < children.length; i++) {
//			subdirs[i] = new File(children[i]).list();
//		}
//		
//		return ret;
//	}
//	
//	private static String getRootDirectory() {
//		String ret = "/";
//		
//		return ret;
//	}
}
