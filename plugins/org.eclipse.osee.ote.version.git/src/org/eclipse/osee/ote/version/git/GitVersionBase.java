/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.version.git;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitVersionBase {
	
	protected String getPathFilterFromFullPathAndGitFolder(File file, File gitFolder) {
		String path = file.getAbsolutePath().replace(gitFolder.getParentFile().getAbsolutePath(), "");
		if(path.startsWith("\\") || path.startsWith("/")){
			path = path.substring(1);
		}
		path = path.replaceAll("\\\\", "/");
		return path;
	}

	protected Repository buildRepository(File gitFolder) throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(gitFolder).readEnvironment().findGitDir().build();
		repository.resolve("HEAD");
		return repository;
	}

	protected File findGitDirUp(File file){
		if(file == null){
			return null;
		}
		File parent = file.getParentFile();
		if(parent == null){
			return null;
		}
		File[] dirs = parent.listFiles(new FileFilter(){
			public boolean accept(File file){
				if(file.isDirectory()){
					return true;
				} else {
					return false;
				}
			}
		});
		for(File dir:dirs){
			if(dir.getName().equals(".git")){
				return dir;
			}
		}
		return findGitDirUp(parent);
	}

}
