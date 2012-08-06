package org.eclipse.osee.ote.version.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.osee.ote.version.FileVersion;
import org.eclipse.osee.ote.version.FileVersionInformationProvider;

public class GitVersionProvider implements FileVersionInformationProvider {


	@Override
	public FileVersion getFileVersion(File file) {
		try {
			return new GitFileVersion(new GitVersion(file));
		} catch (NoHeadException e) {
		} catch (IOException e) {
		} catch (GitAPIException e) {
		}
		return null;
	}

}
