package org.eclipse.osee.ote.version.git;

import java.io.File;
import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Assert;
import org.junit.Test;

public class GitVersionTest {

	private static final String validGitFile = "";
	private static final String validModifiedGitFile = "";
	private static final String invalidGitFile = "";
	private static final String invalidNoGitFile = "";
	private static final String timedGitFile = "";
	private long start;
	private long end;

	@Test
	public void testGetLastCommit() throws NoHeadException, IOException, GitAPIException {
		GitVersion gitVersion = new GitVersion(new File(validGitFile));

		RevCommit commit = gitVersion.getLastCommit();
		Assert.assertNotNull(commit);
		System.out.println(commit.getCommitterIdent().getName());
		System.out.println(commit.getCommitterIdent().getWhen());
		System.out.println(commit.getId().getName());
	}

	@Test
	public void testGetLastCommitFailFile() throws NoHeadException, IOException, GitAPIException {
		GitVersion gitVersion = new GitVersion(new File(invalidGitFile));
		RevCommit commit = gitVersion.getLastCommit();
		Assert.assertNull(commit);
	}

	@Test
	public void testGetLastCommitFailRepo() throws NoHeadException, IOException, GitAPIException {
		GitVersion gitVersion = new GitVersion(new File(invalidNoGitFile));
		RevCommit commit = gitVersion.getLastCommit();
		Assert.assertNull(commit);
	}

	@Test
	public void testModified() throws NoHeadException, IOException, GitAPIException{
		GitVersion gitVersion = new GitVersion(new File(validGitFile));
		Assert.assertFalse(gitVersion.getModified());

		gitVersion = new GitVersion(new File(invalidNoGitFile));
		Assert.assertFalse(gitVersion.getModified());

		gitVersion = new GitVersion(new File(invalidGitFile));
		Assert.assertFalse(gitVersion.getModified());

		gitVersion = new GitVersion(new File(validModifiedGitFile));
		Assert.assertTrue(gitVersion.getModified());

	}

	@Test
	public void testTime() throws NoHeadException, IOException, GitAPIException{
		GitVersion gitVersion = new GitVersion(new File(timedGitFile));

		startTimer();
		gitVersion.getLastCommit();
		endTimer();
		System.out.println(getElapsedTimeInSec());

		startTimer();
		gitVersion.getModified();
		endTimer();
		System.out.println(getElapsedTimeInSec());

	}

	private void startTimer(){
		start = System.nanoTime();
	}

	private void endTimer(){
		end = System.nanoTime();
	}

	private long getElapsedTime(){
		return end - start;
	}

	private String getElapsedTimeInSec(){
		return String.format("%f03", (((float)getElapsedTime())/1000000000f));
	}

}
