/*
 * Created on Aug 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.test.nonproduction;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.eclipse.osee.framework.database.DatabaseActivator;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.test.Activator;

/**
 * @author Andrew M Finkbeiner
 *
 */
public class RelationDeletionTest extends TestCase {

	private static final String ARTIFACT_TYPE = "Folder";
	
	  protected void setUp() throws Exception {
	      // This test should only be run on test db
	      assertFalse(DatabaseActivator.getInstance().isProductionDb());
	   }

	
	public void testDeleteRelationPersistsBothSides() {
		SevereLoggingMonitor monitor = new SevereLoggingMonitor();
		OseeLog.registerLoggerListener(monitor);
		try {
			Artifact parent = ArtifactTypeManager.getType(ARTIFACT_TYPE).makeNewArtifact(BranchPersistenceManager.getDefaultBranch());
			Artifact child1 = ArtifactTypeManager.getType(ARTIFACT_TYPE).makeNewArtifact(BranchPersistenceManager.getDefaultBranch());
			Artifact child2 = ArtifactTypeManager.getType(ARTIFACT_TYPE).makeNewArtifact(BranchPersistenceManager.getDefaultBranch());
			Artifact child3 = ArtifactTypeManager.getType(ARTIFACT_TYPE).makeNewArtifact(BranchPersistenceManager.getDefaultBranch());
			parent.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, child1);
			parent.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, child2);
			parent.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, child3);
			parent.persistRelations();
			
			assertTrue("Failed to add all three children", parent.getRelatedArtifacts(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD).size() == 3);
			
			child1.deleteRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__PARENT, parent);
			
			assertTrue("We removed a relation so it should still be dirty.", child1.isDirty(true));
			assertTrue("Parent artifact should be marked as dirty since it's relation has changed.", parent.isDirty(true));
			
			child1.persistRelations();
			
			assertFalse("Parent artifact should be clean now.", parent.isDirty(true));
			assertFalse("Child artifact should also be clean.", child1.isDirty(true));
			
			List<Artifact> children = parent.getRelatedArtifacts(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD);
			
			assertTrue("The deleted child was not successfully removed.", children.size() == 2);
			
			assertTrue("Child2 is not the first in the list and it should be.", children.get(0) == child2);
			
			List<RelationLink> relations = RelationManager.getRelations(parent, CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD.getRelationType(), CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD.getSide());
			
			assertTrue("The order should point to the head '-1'", relations.get(0).getOrder(RelationSide.SIDE_B) == -1);
			assertTrue("The second item in the list is not pointing to the first.", relations.get(1).getOrder(RelationSide.SIDE_B) == relations.get(0).getArtifactId(RelationSide.SIDE_B));
		} catch (SQLException ex) {
			OseeLog.log(Activator.class, Level.SEVERE, ex);
		} catch (OseeCoreException ex) {
			OseeLog.log(Activator.class, Level.SEVERE, ex);
		}
		assertTrue(monitor.toString(), monitor.getSevereLogs().size() == 0);
	}
}
