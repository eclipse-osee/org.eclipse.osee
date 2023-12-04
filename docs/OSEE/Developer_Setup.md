## Initial Setup

1.  Create a Tools Folder on your C: Drive - C:\\Tools
2.  Copy the [Engineering
    Notebook](https://wiki.eclipse.org/OSEE/Engineering_Notebook) and
    save to your C:\\Tools. Open and read contents then clear examples
    to start using.
3.  Install Programs
    1.  [Google Chrome](https://www.google.com/chrome/)
    2.  [Java
        JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
    3.  [DBeaver](https://dbeaver.io/)
    4.  [Notepad++](https://notepad-plus-plus.org/)
4.  Create an [Eclipse](https://www.eclipse.org/) account using your
    email address
    1.  Sign Eclipse Contributor Agreement by going to View Profile then
        under Status select Eclipse Contributor Agreement
5.  Code Reviews - the following links provide access to review code,
    add links to your browser favorites
    1.  Read [Gerrit
        Tutorial](https://www.mediawiki.org/wiki/Gerrit/Tutorial)
    2.  Bookmark and log into: [Gerrit at
        Eclipse.org](https://git.eclipse.org/r/#/q/project:osee/org.eclipse.osee)
        using eclipse.org un/pw.
    3.  When reviewing code, use the [Developer Peer Review
        Checklist](http://wiki.eclipse.org/OSEE/Software_Development_Process/Peer_Review_Checklist)
6.  Email your mentor to add you as an
    OSEE Admin
7.  Log into [Bugzilla](https://bugs.eclipse.org/bugs/) using Eclipse
    username/password
8.  Sign-up to
    [osee-developers](https://dev.eclipse.org/mailman/listinfo/osee-dev)
    mailing list
9.  Add logback-dev.zip to C:/Tools/
    1.  Go to ![<File:Logback-dev.zip>](Logback-dev.zip
        "File:Logback-dev.zip") and save the file
    2.  Open the .zip file and copy the logback-dev.xml file
    3.  Paste the .xml file into C:/Tools/

## Git Setup

1.  Install [Git](https://git-scm.com/downloads) using the following
    installation settings
      - Adjusting your PATH environment
          - Use git from the windows command prompt
      - Choosing HTTPS transport backend
          - Use the OpenSSL library
      - Configuring the line ending conversions
          - Check as-is, commit as-is
      - Configuring the terminal emulator to use with Git Bash
          - Use MinTTY
      - Leave the rest of the settings as default
2.  Create a folder in Tools called git_main so the path is
    C:/Tools/git_main
3.  Open the following link and download the zip file ![<File:Gitconfig>
    global.zip](Gitconfig_global.zip "File:Gitconfig global.zip")
    1.  Open the zip file and copy the gitconfig_global file into the
        Tools Folder
    2.  Execute the following command, replacing <user> with your user
        directory
          - cp C:/Tools/gitconfig_global C:/Users/<user>/.gitconfig
          - Edit the .gitconfig and replace your name and email (Edit in
            text editor such as Notepad++)
4.  Open Git Bash from the Start Menu
5.  Execute the following commands
    1.  cd C:/Tools/git_main
    2.  git clone
        <https://><your_gerrit_username>@git.eclipse.org/r/osee/org.eclipse.osee.git
    3.  cd org.eclipse.osee
    4.  git checkout dev
6.  Setup Local_hooks by downloading ![<File:Local>
    hooks.zip](Local_hooks.zip "File:Local hooks.zip")
    1.  Open the zip file and copy the local_hooks folder to C:/Tools
    2.  Execute the following command
          - cp C:/Tools/local_hooks/\*
            C:/Tools/git_main/org.eclipse.osee/.git/hooks/
          - You can now delete the folder C:/Tools/local_hooks
    3.  Add your name to usersList.txt in
        C:/Tools/git_main/org.eclipse.osee/.git/hooks/usersList.txt

## OSEE Workspace Setup

**OSEE Setup**

1.  Install OSEE/Eclipse (TBD - replace with how to create OSEE dev
    workbench that compiles)
2.  Latest Dev Alpha Kit may be found at
    <https://ci.eclipse.org/osee/job/osee_nightly/lastSuccessfulBuild/artifact/org.eclipse.osee/plugins/org.eclipse.osee.client.all.product/target/products/>
3.  Unzip it to an empty target folder
4.  If your PC environment's security software prevents running
    executable code from your user profile, add this line to the
    osee.ini file in the target folder you just unzipped into:
5.  \-Duser.home=C:\\Tools\\userhome

**Importing Git Projects**

1.  Start an OSEE version that matches the code you want to develop
    (e.g. dev alpha to develop in dev)
2.  Create a workspace
3.  In OSEE, follow the menu Project-\>Build Automatically, and uncheck
    this option
4.  Open the Git Perspective by following the menu
    Window-\>Perspective-\>Open Perspective
5.  Click the first "GIT" icon labeled, Add an existing local Git
    Repository
6.  Browse to select the git directory (created in a previous section
    above, e.g. C:\\Tools\\git_main), then click Search
7.  In the Search and select Git repositories on your local file system
    dialog, choose org.eclipse.osee
8.  Click the Add button
9.  Right-Click on the org.eclipse.osee repository now showing in the
    Git Repositories view and select Import Projects...
10. In the dialog that pops up if the first project is
    "org.eclipse.osee", uncheck that box
11. Make sure all other projects are checked
12. Click Finish

**Configure Workspace**

1.  Double check to make sure the OSEE environment you are pulling code
    into matches the code you have.
      - e.g. if you are developing for the dev line, make sure your osee
        is a dev version.
2.  Import OSEE Team Preferences
      - Switch back to the Java Perspective, then File-\>Import, in the
        import wizard, select General-\>Preferences and click Next
      - In the From preference file: input box, enter the path to the
        preferences (substitute your directory for <git dir>) in the
        From preference file:
      - Browse to
        <git dir>\\org.eclipse.osee\\plugins\\org.eclipse.osee.support.config
        then select the preference file: osee_team_preferences.epf and
        click Open.
      - Leave "Import All" checked and Choose Finish. Accept the prompt
        to restart the IDE.
3.  Set Target Platform
    1.  Select Window -\> Preferences
    2.  Expand Plug-in Development -\> Target Platform
    3.  Check OSEE Client Server Target Platform
    4.  Click Apply then Apply and Close
4.  Turn Project-\>Build Automatically back on
5.  Remove errors that we do not care about
      - In the Problems tab at the bottom of OSEE, select the white
        triangle icon on the right side
      - Choose Filters...
      - Uncheck Show all items
      - Choose New under Configurations
      - Deselect "Errors / Warnings on Project", then make sure New
        Configuration is both selected and highlighted
      - Under Types, click Deselect All for the errors
      - Go through the list of types selecting all Java, Javascript, and
        Plug-in Problems
      - Click Apply and Close
6.  Remove Null Analysis Errors
      - Choose Window-\>Preferences-\>Java-\>Compiler-\>Errors/Warnings
      - Expand Null Analysis and set the following to Warning instead of
        Error
      - Null pointer access
      - Potential null pointer access
      - Redundant null check
      - Uncheck Enable annotation-based null analysis
      - Click Apply (which will prompt to rebuild all) then Apply and
        Close
      - After the rebuild there should no longer be any errors,
        otherwise ask your Mentor

**Other OSEE Settings**

  - Package Explorer \> White Pull down Arrow \> Package Presentation \>
    Change to Hierarchical from Flat (if preferred)

**OSEE Setup Checks**

1.  Run Test Suites / DB Init
    1.  Under Run-\>Debug Configurations, OSGi Framework, select
        "OSEE_Application_Server\[HSQLDB\]" and click Debug to run it
    2.  In the Console output scroll from the application server, look
        for these two lines to verify correct startup:
    3.  Registered servlet '/osee/client/loopback'
    4.  Registered servlet '/osee/console'
    5.  Once server is loaded, go to Run-\>Debug Configurations, JUnit
        Plug-in Test, select "AtsIde_Integration_TestSuite" and click
        Debug to run it
    6.  You can generally safely click on "Continue" if you get a
        validation pop-up (eg. "rest.assured"). These typically come
        from incompletely cleaned-up removed dependencies.
    7.  Once \~100 tests have loaded and passed in JUnit, stop the
        tests, terminate and relaunch the Application Server
    8.  Run "OSEE_IDE_\[localhost\]" under Eclipse Application in
        Debug Configurations
    9.  OSEE IDE should load without issue

## Other Programs & System Configuration

  - Wiki for Documentation
      - Bookmark and log into Eclipse.org Wiki
  - Log in and bookmark [Jenkins Build
    Page](https://ci.eclipse.org/osee/)

## Reading & Training Material

1.  Read important Eclipse training at [Eclipse
    Help](https://help.eclipse.org/kepler/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Freference%2Fref-43.htm)
      - Read "Workbench User Guide" sections
          - Getting Started
          - Tips and Tricks
      - Read "Java development user guide" sections:
          - Getting Started
      - Read "Platform Plug-in Developers Guide" sections:
          - Welcome to Eclipse
          - Simple plug-in example
          - Runtime overview
      - Read "Plug-in Development Environment Guide" sections:
          - PDE Overview
          - Concepts
2.  Watch videos on [How Do I Author CM Managed
    Changes](https://wiki.eclipse.org/OSEE/Training#ATS_-_How_do_I_Author_Configuration_Managed_Changes)
3.  Read [Agile Tutorial](https://www.tutorialspoint.com/agile/)
4.  Read [REST Tutorial](https://www.tutorialspoint.com/restful/)
5.  Read [OSEE Software Development
    Process](https://wiki.eclipse.org/OSEE/Software_Development_Process)
6.  Read [OSEE Developer
    Guidelines](https://wiki.eclipse.org/OSEE/Developer_Guidelines)
7.  Read [OSEE Acronyms](https://wiki.eclipse.org/OSEE/Acronyms)
8.  Read [OSEE Architecture](https://wiki.eclipse.org/OSEE/Architecture)
9.  Bookmark and browse [User
    Training](https://wiki.eclipse.org/OSEE/Training)
10. Watch short [User
    Training](https://wiki.eclipse.org/OSEE/Training#All_OSEE_Training_Videos)
    videos
11. Bookmark and watch all (TBD) Developer Videos

## Git Information & Support

When making your first push to Gerrit OSEE you will need to sign-in

1.  After making submitting the push in Git Bash, a Windows log-in
    screen will pop up
2.  Go to the OSEE Gerrit Page, and then your account settings
3.  Open 'HTTP Password, click 'Generate Password'
4.  Use this username and password as the log-in that was prompted by
    git bash

The OSEE Git repository contains the following main branches:

  - **master:** *origin/master* is the main branch where *HEAD* always
    reflects a production ready state.
  - **dev:** *origin/dev* is the main branch where *HEAD* always
    reflects a state with the latest delivered development changes for
    integration.
  - **0.XX.X:** *origin/0.XX.X* is the main branch where *HEAD* always
    reflects a state with the latest changes from development that have
    been tested/built and ready for customer assurance testing. This
    branch reflects the last development changes that have been
    incorporated into a release candidate.

<!-- end list -->

  - Developers wanting to incorporate a change for a specific build
    should make the change in that build's development branch (ex.
    dev).

<!-- end list -->

  -
    ☞ **Developers should not make changes on master branches**

**Additional Git Commands** Collection of Useful Git Commands

<table>
<thead>
<tr class="header">
<th><p>Name</p></th>
<th><p>Description</p></th>
<th><p>Commands</p></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><p><strong>Stash</strong></p></td>
<td><p>Stores changes into a stack</p></td>
<td><p><code>git stash save </code><message><br />
<code>git stash list</code><br />
<code>git stash show -p stash@{0}</code><br />
<code>git stash drop stash@{0}</code><br />
<code>git stash pop</code></p></td>
</tr>
<tr class="even">
<td><p><strong>Selective Push</strong></p></td>
<td><p>Push only commits from a certain point (HASH) and prior</p></td>
<td><p><code>git push origin HASH:remoate_branch_name</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>Show Modified File Names</strong></p></td>
<td><p>Lists files modified</p></td>
<td><p><code>git show --name-only HASH path</code></p></td>
</tr>
<tr class="even">
<td><p><strong>Show changes introduced by each commit</strong></p></td>
<td><p>shows commit change information</p></td>
<td><p><code>git whatchanged --name-only</code><br />
<code>git whatchanged -2 --name-only #to display the last two</code><br />
<code>git whatchanged --format=email --name-only #display in email format</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>Undo</strong></p></td>
<td><p>Undo local changes</p></td>
<td><p><code>git rebase -i HEAD~5</code><br />
<code>git reset --hard HEAD</code><br />
<code>git reset --hard HEAD~3</code><br />
<code>git reset --hard </code><specific_commit_id></p></td>
</tr>
<tr class="even">
<td><p><strong>Show Commit File Differences</strong></p></td>
<td><p>Compares files from current commit with previous commit</p></td>
<td><p><code>git diff HASH</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>Compare two commits</strong></p></td>
<td><p>Compares files between two commits</p></td>
<td><p><code>git diff OLDHASH NEWHASH</code></p></td>
</tr>
<tr class="even">
<td><p><strong>Compare two branches</strong></p></td>
<td><p>Compares changes made between two branches</p></td>
<td><p><code>git diff branch1..branch2</code><br />
<code>git diff localBranchName..origin/remoteName</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>Find common ancestor</strong></p></td>
<td><p>finds best common ancestor(s) between two commits</p></td>
<td><p><code>git merge-base branch1..branch2</code></p></td>
</tr>
<tr class="even">
<td><p><strong>Git Log</strong></p></td>
<td><p>view history for a given path even if it has been moved/renamed</p></td>
<td><p><code>git log --follow [current path]</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>Git Log</strong></p></td>
<td><p>vew current branch activity</p></td>
<td><p><code>git log --name-only HASH</code><br />
<code>git log --graph --pretty=format:'%Cred%h%Creset</code><br />
<code> -%C(yellow)%d%Creset %s %Cgreen(%cr) %C(bold blue)&lt;%an&gt;%Creset'</code><br />
<code> --abbrev-commit --date=relative</code></p></td>
</tr>
<tr class="even">
<td><p><strong>Git Log</strong></p></td>
<td><p>Print single log entry</p></td>
<td><p><code>git log -1 branch_name</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>Git Log</strong></p></td>
<td><p>Show full history even with removed commits</p></td>
<td><p><code>git reflog show [log-options] master</code></p></td>
</tr>
<tr class="even">
<td><p><strong>Create a local branch</strong></p></td>
<td><p>Creates a branch and switches working tree to new branch</p></td>
<td><p><code>git checkout -b newLocalBranchName sourceBranch</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>Create a local tracking branch</strong></p></td>
<td><p>Creates a local tracking branch called your_branch_name_here and switches working tree</p></td>
<td><p><code>git checkout -b your_branch_name_here origin/0.9.7_dev</code></p></td>
</tr>
<tr class="even">
<td><p><strong>Switch branches</strong></p></td>
<td><p>switches the working tree to a different branch</p></td>
<td><p><code>git checkout [master|94|...]</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>Sharing a local work branch with others</strong></p></td>
<td><p>Switch to working branch, Push working branch to remote</p></td>
<td><p><code>git checkout </code><working_branch_name><br />
<code>git push origin </code><working_branch_name><code>:refs/heads/</code><working_branch_name></p></td>
</tr>
<tr class="even">
<td><p><strong>Merge onto current branch</strong></p></td>
<td><p>Merge branch into current branch, if there is nothing to merge a fast-forward will be performed. HEAD pointer moved to HEAD of branch</p></td>
<td><p><code>git merge branch</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>Branching and Merging</strong></p></td>
<td><p>To replace with incoming file so you can re-make your changes (or abandon them)</p></td>
<td><p><code>git checkout --[theirs|our] </code><path></p></td>
</tr>
<tr class="even">
<td><p><strong>Merging Again</strong></p></td>
<td><p>What to do if you are in the middle of a rebase and don't want the previous merge resolution</p></td>
<td><p><code>git checkout --conflict=merge -- </code><file you want to remerge></p></td>
</tr>
<tr class="odd">
<td><p><strong>Continue rebasing</strong></p></td>
<td><p>After conflicts have been resolved</p></td>
<td><p><code>git rebase --continue</code></p></td>
</tr>
<tr class="even">
<td><p><strong>Patches</strong></p></td>
<td><p>Generating individual patches (one per commit) to submit by email (or bug report), exports all commits made to current branch, from trunk to HEAD "-o ../" puts them one level above in individual .patch files</p></td>
<td><p><code>git format-patch -o ../ trunk..HEAD</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>Patches</strong></p></td>
<td><p>Create a patch with the top n number of commits</p></td>
<td><p><code>git format-patch -n</code></p></td>
</tr>
<tr class="even">
<td><p><strong>Local Change Patch</strong></p></td>
<td><p>Generate a big patch for your local changes, (good for backup), In this case Z:\ is mounted as a shared windows folder, so if you do not have faith in your local backup software, this could help you quickly restore your work.</p></td>
<td><p><code>git diff trunk HEAD -p &gt; Z:/backup_date_time.patch</code></p></td>
</tr>
<tr class="odd">
<td><p>''' Splitting commits '''</p></td>
<td><p>In case you have multiple, conceptually unrelated changes in a single commit you can split them by doing an interactive rebase</p></td>
<td><p><code>--Rebase current working tree</code><br />
<code>git rebase -i HASH_WHERE_TO_START_FROM</code><br />
<code>-- mark 'e' or 'edit' the commit you want to Edit/Split.</code><br />
<code>-- When you get dropped back into the shell, issue:</code><br />
<code>git reset HEAD^</code><br />
<code>--which effectively undoes that</code><br />
<code>-- commit and leaves the modified files unstaged </code><a href="http://progit.org/book/ch6-4.html"><code>ProGit</code><code> </code><code>Book</code><code> </code><code>Chapter</code><code> </code><code>6-4</code></a><br />
<code>-- Afterwards you can 'add' and 'commit' changes as appr.</code><br />
<code>--To resume rebase operation issue:</code><br />
<code> git rebase --continue</code></p></td>
</tr>
<tr class="even">
<td><p><strong>Change author name/email of a commit with yours</strong></p></td>
<td><p>Edit commit author, email, or comments using the following. This should only be performed on commits that have not been pushed to the remote repository. If you just need to change the author name you can change --reset-author with --author AUTHOR NAME</p></td>
<td><p><code>git rebase -i SHA_OF_COMMIT_PREVIOUS_TO_TARGET</code><br />
<code>git commit --amend --reset-author -- Repeat this and the next step for each file</code><br />
<code>git rebase --continue</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>Clean up untracked files from repository</strong></p></td>
<td><p>Remove untracked files and directories from the working tree</p></td>
<td><p><code>git clean -fdx</code></p></td>
</tr>
<tr class="even">
<td><p><strong>Tagging a Build</strong></p></td>
<td><p>Create, and Push a Tag</p></td>
<td><p><code>git tag -a -m "0.9.6.v201009271203 Release Candidate" 0.9.6.v201009271203_RC </code><COMMIT_ID TO TAG><br />
<code>git push origin 0.9.6.v201009271203_RC</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>Deleting a Tag</strong></p></td>
<td><p>Delete a Tag locally and push change to remote</p></td>
<td><p><code>git tag -d 0.9.6.v201009271203_RC</code><br />
<code>git push origin :refs/tags/0.9.6.v201009271203_RC</code></p></td>
</tr>
<tr class="even">
<td><p><strong>delete remote branch</strong></p></td>
<td><p>different than just deleting a local branch</p></td>
<td><p><code>git push origin --delete branchName</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>List all Tags</strong></p></td>
<td><p>List tags</p></td>
<td><p><code>git tag -l</code></p></td>
</tr>
<tr class="even">
<td><p><strong>Create empty branch</strong></p></td>
<td><p>Create an empty branch for disjoint histories</p></td>
<td><p><code>git symbolic-ref HEAD refs/heads/newbranch</code><br />
<code>rm .git/index</code><br />
<code>git clean -fdx</code><br />
<do work><br />
<code>git add your files</code><br />
<code>git commit -m 'Initial commit'</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>Create Mapped Drive Git Repo</strong></p></td>
<td><p>Use mapped drive to host remote Repo for personal use</p></td>
<td><p>Note: in this example i is mapped to a remote disk share</p>
<p><code>git init --bare --shared=0600 /i/git/user.git</code><br />
<code>cd /c/Tools/git</code><br />
<code>git clone /i/git/user.git/</code><br />
<code>cd user</code><br />
<code>copy your files in</code><br />
<code>git add -A</code><br />
<code>git commit -m "initial commit"</code><br />
<code>git push origin master</code></p></td>
</tr>
<tr class="even">
<td><p><strong>Create Git Repo using SSH</strong></p></td>
<td><p>Use SSH to host remote Repo for personal use</p></td>
<td><p><code>on the remote host:</code><br />
<code>git init --bare --shared=0600 /</code><path><code>/user.git</code><br />
<code>chmod 700 /</code><path><code>/user.git</code></p>
<p><code>on local machine:</code><br />
<code>cd /c/Tools/git</code><br />
<code>git clone </code><a href="ssh://"><code>ssh://</code></a><user_name><code>@</code><host><code>/</code><path><code>/user.git</code><br />
<code>cd user</code><br />
<code>copy your files in</code><br />
<code>git add -A</code><br />
<code>git commit -m "initial commit"</code><br />
<code>git push origin master</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>Remove stale remote branches</strong></p></td>
<td><p>Use Prune to remove stale remote branches (add --dry-run to preview what will be changed)</p></td>
<td><p><code>git remote prune origin</code></p></td>
</tr>
<tr class="even">
<td><p><strong>Find dangling Commits</strong></p></td>
<td><p>Find dangling commits</p></td>
<td><p><code>git fsck --full --no-reflogs | grep commit</code><br />
<code>-- or to show the names of each run:</code><br />
<code>for i in `git fsck --full --no-reflogs | grep commit | awk '{print $3}'`;</code><br />
<code>   do git show $i --name-only; done</code></p></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td></td>
</tr>
<tr class="even">
<td><p><strong>Fast Export/Fast Import</strong></p></td>
<td><p>Use to move a project from one repo to another repo preserving history</p></td>
<td><p><code>git fast-export branchToExport pathOfSourceProject(s) | (cd destinationRepo &amp;&amp; git fast-import)</code><br />
<code>-- done from source repo</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>Remove all instances of a file from history</strong></p></td>
<td><p>Run through all commits in history and remove the named file/s. Also delete commits that would be empty once the file is deleted. Useful for things like large binary files that are taking up too much space and shouldn't have been committed.</p></td>
<td><p><code>git filter-branch --index-filter 'git ls-tree --name-only --full-tree $GIT_COMMIT |</code><br />
<code>   grep "folder/my.file" |</code><br />
<code>   xargs git rm --cached -r --ignore-unmatch test.txt ' --prune-empty -f -- --all</code></p></td>
</tr>
</tbody>
</table>


**Git Resources**

  - **Git Commands**
      - [Git
        Magic](http://www-cs-students.stanford.edu/~blynn/gitmagic/)
      - [Git
        Tips](https://git.wiki.kernel.org/index.php/GitTips#head-1cdd4ab777e74f12d1ffa7f0a793e46dd06e5945)
      - [GIT for Eclipse
        Users](http://wiki.eclipse.org/EGit/Git_For_Eclipse_Users)

<!-- end list -->

  - **Git Workflows**
      - [GIT Branches, RC, etc
        Workflow](http://reinh.com/blog/2009/03/02/a-git-workflow-for-agile-teams.html)
      - [GIT Branching Model](http://nvie.com/git-model)

<!-- end list -->

  - **Git Conflicts/Merging**
      - [Understanding conflict
        marks](http://www.kernel.org/pub/software/scm/git/docs/git-merge.html#_how_conflicts_are_presented)

<!-- end list -->

  - **Git Migration Help**
      - [Git for SVN Users](http://git.or.cz/course/svn.html)
      - [Git for Eclipse
        Committers](http://wiki.eclipse.org/Git_for_Committers)
      - [GIT,SSH Setup,Repository Migration, & Committer
        Info](http://wiki.eclipse.org/Git)