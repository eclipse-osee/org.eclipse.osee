package org.eclipse.osee.framework.core.model.access;

import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public class AccessDetail<T> {
	private PermissionEnum permission;
	private final T accessObject;
	private final String reason;

	public AccessDetail(T accessObject, PermissionEnum permission) {
		this.accessObject = accessObject;
		this.permission = permission;
		this.reason = Strings.emptyString();
	}

	public AccessDetail(T accessObject, PermissionEnum permission, String reason) {
		this.accessObject = accessObject;
		this.permission = permission;
		this.reason = reason;
	}

	public String getReason() {
		return reason;
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
		return String.format("Access [ accessObject=[%s] permission=[%s] reason=[%s]]", getAccessObject(),
					getPermission(), getReason());
	}
}