package org.eclipse.osee.ote.version;

import java.io.File;

public interface FileVersionInformation {
	
	FileVersion getFileVersion(File file);
	
}
