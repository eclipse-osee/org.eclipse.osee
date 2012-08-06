package org.eclipse.osee.ote.version;

public interface FileVersion {
	String getVersion();
	String getURL();
	String getVersionControlSystem();
	String getModifiedFlag();
	String getLastModificationDate();
	String getLastAuthor();
}
