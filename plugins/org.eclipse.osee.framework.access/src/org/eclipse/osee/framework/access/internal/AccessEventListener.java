package org.eclipse.osee.framework.access.internal;

import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.access.AccessObject;
import org.eclipse.osee.framework.access.internal.data.ArtifactAccessObject;
import org.eclipse.osee.framework.access.internal.data.BranchAccessObject;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event2.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.event2.artifact.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event2.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;

/**
 * <REM2>
 * 
 * @author Donald G. Dunne
 */
public final class AccessEventListener implements IBranchEventListener, IArtifactsPurgedEventListener, IArtifactEventListener {

	private final AccessControlService service;

	public AccessEventListener(AccessControlService service) {
		this.service = service;
	}

	private void reload() throws OseeCoreException {
		service.reloadCache();
	}

	@Override
	public void handleBranchEventREM1(Sender sender, BranchEventType branchModType, int branchId) {
		try {
			if (branchModType == BranchEventType.Deleted || sender.isLocal() && branchModType == BranchEventType.Purged) {
				BranchAccessObject branchAccessObject = BranchAccessObject.getBranchAccessObject(branchId);
				updateAccessList(sender, branchAccessObject);
			}
		} catch (OseeCoreException ex) {
			OseeLog.log(Activator.class, Level.SEVERE, ex);
		}
	}

	@Override
	public void handleArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) {
		try {
			for (Artifact artifact : loadedArtifacts.getLoadedArtifacts()) {
				ArtifactAccessObject artifactAccessObject = ArtifactAccessObject.getArtifactAccessObject(artifact);
				updateAccessList(sender, artifactAccessObject);
			}
		} catch (OseeCoreException ex) {
			OseeLog.log(Activator.class, Level.SEVERE, ex);
		}
	}

	@Override
	public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
		for (EventBasicGuidArtifact guidArt : artifactEvent.getArtifacts()) {
			if (guidArt.is(EventModType.Added) && guidArt.is(CoreArtifactTypes.User)) {
				try {
					reload();
				} catch (OseeCoreException ex) {
					OseeLog.log(Activator.class, Level.SEVERE, ex);
				}
			}
			if (guidArt.is(EventModType.Purged)) {
				try {
					Artifact cacheArt = ArtifactCache.getActive(guidArt);
					if (cacheArt != null) {
						ArtifactAccessObject artifactAccessObject = ArtifactAccessObject.getArtifactAccessObject(cacheArt);
						updateAccessList(sender, artifactAccessObject);
					}
				} catch (OseeCoreException ex) {
					OseeLog.log(Activator.class, Level.SEVERE, ex);
				}

			}
		}
	}

	@Override
	public List<? extends IEventFilter> getEventFilters() {
		return null;
	}

	@Override
	public void handleBranchEvent(Sender sender, final BranchEvent branchEvent) {
		try {
			if (branchEvent.getEventType() == BranchEventType.Deleted || sender.isLocal() && branchEvent.getEventType() == BranchEventType.Purged) {
				BranchAccessObject branchAccessObject =
							BranchAccessObject.getBranchAccessObject(branchEvent.getBranchGuid());
				updateAccessList(sender, branchAccessObject);
			}
		} catch (OseeCoreException ex) {
			OseeLog.log(Activator.class, Level.SEVERE, ex);
		}
	}

	@Override
	public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
	}

	private void updateAccessList(Sender sender, AccessObject accessObject) throws OseeCoreException {
		List<AccessControlData> acl = service.generateAccessControlList(accessObject);
		for (AccessControlData accessControlData : acl) {
			service.removeAccessControlDataIf(sender.isLocal(), accessControlData);
		}
	}
}