package org.eclipse.osee.ote.version.git;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class GitVersion {

	private final File file;
	
	public GitVersion(File file){
		this.file = file;
	}
	
	public String getURL() throws MalformedURLException{
		return this.file.toURI().toURL().toString();
	}
	
	public boolean getModified() throws IOException, NoWorkTreeException, GitAPIException{
		if(!file.exists()){
			return false;
		}
		File gitFolder = findGitDirUp(file);
		if(gitFolder == null){
			return false;
		}
		Repository repository = buildRepository(gitFolder);
		Git git = new Git(repository);
		StatusCommand status = git.status();
		String pathFilter = getPathFilterFromFullPathAndGitFolder(file, gitFolder);
		Status result = status.call();
		Set<String> modified = result.getModified();
		if(modified.contains(pathFilter)){
			return true;
		} else {
			return false;
		}
	}
	
	public  RevCommit getLastCommit() throws IOException, NoHeadException, GitAPIException{
		if(!file.exists()){
			return null;
		}
		File gitFolder = findGitDirUp(file);
		if(gitFolder == null){
			return null;
		}
		Repository repository = buildRepository(gitFolder);
		Git git = new Git(repository);
		LogCommand log = git.log();
		String pathFilter = getPathFilterFromFullPathAndGitFolder(file, gitFolder);
		log.addPath(pathFilter);
		Iterable<RevCommit> iterable = log.call();
		Iterator<RevCommit> it = iterable.iterator();
		if(it.hasNext()){
			return it.next(); 
		} else {
			return null;
		}
	}

	private  String getPathFilterFromFullPathAndGitFolder(File file, File gitFolder) {
		String path = file.getAbsolutePath().replace(gitFolder.getParentFile().getAbsolutePath(), "");
		if(path.startsWith("\\") || path.startsWith("/")){
			path = path.substring(1);
		}
		path = path.replaceAll("\\\\", "/");
		return path;
	}

	private  Repository buildRepository(File gitFolder) throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(gitFolder).readEnvironment().findGitDir().build();
		repository.resolve("HEAD");
		return repository;
	}

	private  File findGitDirUp(File file){
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
