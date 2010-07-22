package org.eclipse.osee.framework.core.model.access;

public final class PermissionStatus {
	private final StringBuilder reason;
	private boolean matchedPermission;

	public PermissionStatus() {
		this.reason = new StringBuilder();
		this.matchedPermission = true;
	}

	public boolean matches() {
		return matchedPermission;
	}

	public String getReason() {
		return reason.toString();
	}

	public void setReason(String reason2) {
	}

	void setMatches(boolean matchedPermission) {
		this.matchedPermission = matchedPermission;
	}
}