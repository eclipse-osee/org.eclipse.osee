This guide describes how to set up an Eclipse workspace to develop OSEE.

## Downloading and Configuring Eclipse

Use a new Eclipse installation with the latest stable version of OSEE
that you are developing installed using the updates page. This will
ensure that you have the correct bundles in place to provide the
required dependencies.

## Working with Git

This guide explains how to set up a local OSEE source repository using
Git. When you complete it, you will have an Eclipse workspace populated
with the OSEE projects from your Git working directory, with full tool
support from both the EGit plugin and the original command-line `git`.

` Excellent explanation of the design and architecture of Git - `[`Git``
 ``for``   ``Computer``
 ``Scientists`](http://eagain.net/articles/git-for-computer-scientists/)

## Git Setup & Install

1.  Download GIT for Windows by selecting the [latest version of
    msysGit](https://msysgit.github.com)
    1.  Select **Run Git From the Windows Command Prompt** from the
        **Adjusting Your PATH environment** page
    2.  Select **Checkout as-is, commit as-is** from the **Configuring
        the line ending conversions** page
2.  Install **Diff/Merge Tool** [KDiff3
    HomePage](http://kdiff3.sourceforge.net/)
3.  Configure the Merge Tool (These settings should help make merging
    less painful)
    1.  Launch KDiff3 by clicking **Start-\>All
        Programs-\>KDiff3-\>KDiff3**
    2.  In KDiff3, select on **Settings-\>Configure** to open the
        configuration dialog
    3.  On the **Editor** tab, set **Line end style** to **Unix**
    4.  On the **Diff** tab, check the **Try hard** check box
    5.  On the **Merge** tab, check the **Auto save and quit on merge
        without conflicts** check box
    6.  On the **Directory** tab, un-check the **Backup files (.orig)**
        check box
    7.  On the **Regional Settings** tab:
        1.  Check the '''Use the same encoding for everything check box
        2.  Select **Unicode, 8 bit (UTF-8)** for **File Encoding for
            A**
        3.  Un-Check the first **Auto Detect Unicode** check box
    8.  Click **OK**
4.  Fix Window Home to User Home - *By default, in Windows, Git sets the
    HOME environment variable of its bash to the value of the HOMEDRIVE
    variable. When the HOMEDRIVE is mapped to a network drive, this
    introduces an annoying delay before Git commands execute.*
    1.  In Windows, select **Start-\>Control Panel-\>System** to open
        the system properties dialog
    2.  Select the **Advanced System Settings** button on the left
    3.  Click the **Environment Variables** button
    4.  In the **User variables for...** section perform the following:
        1.  Click on **New** to open the **New User Variable** dialog
        2.  Enter the following:
              -
                Variable name: **HOME**
                Variable value: **C:\\Documents and Settings\\<User>**
        3.  Click **OK**
        4.  Click **OK**
5.  Launch Git Bash Shell by clicking on the Git desktop shortcut
    ![<file:gitshortcut.png>](/docs/images/gitshortcut.png "file:gitshortcut.png")
6.  Configure your Git Window:
    1.  Right-click on the title bar
    2.  Select **Properties** to open the window properties dialog
    3.  Select the **Options** tab
    4.  Under **Edit Options** check **"Quick Edit Mode"**
    5.  Select the **Layout** tab
    6.  Under **Screen Buffer Size** enter **300** for both width and
        height
    7.  Click **OK**
    8.  Select the '''Modify shortcut that started this window" radio
        button
    9.  Click **OK**
7.  Update Git Configuration by copy/pasting the following commands into
    the Git Shell (if behind a firewall add **-x address:port** to curl
    commands)
      -
        **echo "cd \~" \> \~/.bashrc**
        **curl
        <http://git.eclipse.org/c/osee/org.eclipse.osee.git/plain/plugins/org.eclipse.osee.support.config/.gitconfig>
        \> \~/.gitconfig**
8.  Restart Git

### Setup Git Password Management

#### GUI

To avoid typing your password during remote interactions, perform the
following:

1.  Launch Git Bash Shell by clicking on the Git desktop shortcut
    ![image:gitshortcut.png](/docs/images/gitshortcut.png "image:gitshortcut.png")
2.  At the console copy/paste the following commands (if behind a
    firewall add **-x address:port** to curl commands):
    1.  **curl
        <http://git.eclipse.org/c/osee/org.eclipse.osee.git/plain/plugins/org.eclipse.osee.support.config/.managePasswords>
        \> \~/.managePasswords**
    2.  **curl
        <http://git.eclipse.org/c/osee/org.eclipse.osee.git/plain/plugins/org.eclipse.osee.support.config/inputDialog.tcl>
        \> \~/inputDialog.tcl**
    3.  **git config --global core.askpass "$HOME/.managePasswords"**
    4.  **git config --global alias.passwd \\\!"\~/.managePasswords
        update"**

:\*Upon the first Git remote repository interaction, Git will ask for
your password by opening an input dialog.

:\*If you need to change your password type: **git passwd**

#### Simple plain text file

If you would like to store your password in a flat file and edit in
plain text editor like TextEdit, TextMate, Vi(M)??, Emacs you can use
the following file that should reside in your `%HOME%` or `$HOME`
directory.

    /c/Documents and Settings/username/_netrc

    machine your.server.name.without.any.http
    login john.smith
    password smithsonian
    machine another.server.machine.repository.of.which.you.access
    login john.q.smith
    password smithsonian1

You're done.

[Credits](http://confluence.atlassian.com/display/FISHEYE/Permanent+authentication+for+Git+repositories+over+HTTP%28S%29)

### Additional Windows Settings (Optional)

Not happy with the font choices of Lucida Console and Raster Fonts?

1.  Run "regedit" from the Windows Run dialog.
2.  Navigate to HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows
    NT\\CurrentVersion\\Console\\TrueTypeFont
3.  Create a new String Value with consecutively increasing zeros for
    the name; e.g. "00" for the second font, "000" for the third font.
4.  Modify it and put the name of the font you want to use exactly as it
    appears in other font chooser dialog boxes in the data field.
5.  When you're done it should look something like this:

`00 REG_SZ Consolas`
`000 REG_SZ Courier New`

1.  Close Registry Editor and restart git bash.
2.  Go back to the Font tab and select your font\!

## OSEE Git Development Workflow

The goals of the workflow:

  - Fail fast
      - Code conflicts and integration problems should be discovered as
        soon as possible
      - Fix small problems often instead of fixing large problems seldom
  - Always releasable
  - Keep it simple and repeatable

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

Developers wanting to incorporate a change for a specific build should
make the change in that build's development branch (ex. dev).
: ☞ **Developers should not make changes on master branches**

## OSEE GIT Repository

Create a repository to clone the following branches so they can be
imported into OSEE. (ex: /C/UserData/GIT/)

**Committer (write-access)**

In the GIT shell, navigate to the repository you created above and run
the following commands. This will clone the branches you need into your
repository.

`git clone `<https://git.eclipse.org/r/osee/org.eclipse.osee.git>

[`OSEE``   ``GIT``   ``Web``
 ``Portal`](http://git.eclipse.org/c/osee/org.eclipse.osee.git)

**NOTE:** If you are having permission issues cloning these locations,
please see your system administrator to get you access to these
repositories, an LDAP account as well as a GERRIT password.

## Setting up a workspace

### Option 1

1.  Create a new Eclipse workspace.
2.  **File-\>Import...-\>Git-\>Projects from Git** this opens the
    **Import Projects from Git** dialog
3.  Double Click **Existing local repository**
4.  Click on **Add** to add a git repository path
5.  Browse to the location of the local cloned git repositories (ex:
    /c/UserData/GIT)
6.  Select the repositories to import projects from
7.  Click **Finish**
8.  From the **Select a Git Repository** page, click the project you
    wish to import and click **Next**
9.  From the **Select a wizard to use for importing projects** page,
    click **Next** again
10. From the **Import Projects** page
11. Click **Finish** to finish the import

**NOTE:** repeat these steps for each project you wish to import.

### Option 2

1.  Create a new Eclipse workspace.
2.  Open the "Git Repository Exploring" perspective
3.  Click on the "Clone a Git Repository and add the clone to this view"
    button
4.  Put "<http://git.eclipse.org/gitroot/osee/org.eclipse.osee.git>"
    (read-only example) in the URL
5.  Click **Next**
6.  Pick the branch(es) you want to import (e.g. "master")
7.  Click **Next**
8.  Click **Finish**
9.  Wait for the repository to import
10. Right-click on the repository and select **Import Projects...**
11. From the **Select a wizard and decide how to share the imported
    projects** page, select the **Share new projects interactively**
    radio option in the **Method for sharing projects after project
    creation** group
12. Click **Next**
13. From the **Import Projects** page, click the **Select All** button
    to check all the projects
14. Click **Next**
15. From the **Share Projects with Git** page
16. Click **Finish** to finish the import

### Option 3

Acquiring OSEE source code without a GIT repository. This is the
'simplest' but also least flexible since it does not prepare you to push
your changes. This is a great method for quick debugging at a user's
workstation (i.e.: a non-development workstation).

1.  Install OSEE locally
2.  Open OSEE with a brand new workspace
3.  Window--\>Open Perspective--\>Java
4.  Right-click in the Package Explorer
5.  Select Import...
6.  Select Plug-in Development--\>Plug-ins and Fragments
7.  In the "Import As" frame select "Projects with source folders" and
    click Next
8.  In the "Plug-ins and Fragments Found:" list box select
    "org.eclipse.osee.framework.plugin.core(...)"
9.  Click "Add-\>"
10. Click "Required Plug-ins-\>"
11. Click Finish

*Running:*

1.  Wait for "Building workspace" to complete
2.  Select Run--\>Debug Configurations...
3.  Select "Ecplipse Application" from the list on the left side of the
    dialog
4.  Click the "New launch configuration" button in the upper left corner
    of the dialog (see screenshot below)
5.  Click Debug (if you get an error "Errors exist in require
    project(s)" try clicking Proceed)

![image:oseedebugconfigsshot.png](/docs/images/oseedebugconfigsshot.png
"image:oseedebugconfigsshot.png") The preceding instructions for running
will not display the OSEE logo splash screen. Instead the default
Eclipse splash screen will be displayed.

## Local Development

  -
    ☞ **Development should only be performed on a development branch
    (never commit to master)**

## Local Development

  -
    ☞ **Development should only be performed on a development branch
    (never commit to a master branch)**

### Track a development branch

Before starting development, we need to create a local tracking
development branch. You only need to perform this step at the beginning
of the development cycle and when you are first cloning your branches.

To create a tracking development branch: **` git checkout -b
 `<branch>`  origin/ `<branch>**

### Make Change

1.  Keep development branch up-to-date
    1.  Switch to the target build development branch: **` git checkout
         `<branch>**
    2.  Update local development branch with remote changes before any
        changes are made: **`git pull`**
    3.  Optionally, you can inspect the changes pulled in: **`git log`**
2.  Making Changes: When working on a change, you have the option of
    making the changes directly on the development branch or creating a
    local feature/bug branches to encapsulate the work.
      - To create a local branch: **` git checkout -b
        [Bug|Feature|Refactor]_[ID]_description  `<branch>**
      - Check state (index and working tree): **`git status`**
      - Add a new untracked file: **` git add  `<file_path>**
      - Add all untracked and modified files: **`git add -A`**
      - Commit a change - *[See comment conventions
        below](#Comment_Conventions "wikilink")*: **`git commit -a -m
        "Comment"`** or **`git commit -a`** to open editor for comment
        editting
      - Rebase against the remote branch frequently to prevent branch
        from diverging:
    <!-- end list -->
    1.  **` git pull --rebase  `<branch>**
    <!-- end list -->
      -
        <span style="font-variant:small-caps">Tip: By default `git pull`
        performs a merge, you can skip **--rebase** option by running
        `git config branch.`<branch>`.rebase true` to [persist the
        rebase
        setting.](http://www.kernel.org/pub/software/scm/git/docs/git-pull.html#_options_related_to_merging)</span>
    <!-- end list -->
    1.  **`git checkout <Bug|Feature|Refactor Branch>`**
    2.  **` git rebase  `<branch>**

<!-- end list -->

  -
    ☞ **Creating a local working branch allows for better grouping of
    related changes and easier task switching**
    ☞ **Always follow comment and branch naming conventions**

## Incorporate a Finished Working Branch Into Development Branch

1.  Rebase the development branch to ensure it has the latest changes:
    **`git pull --rebase origin 0.9.6_dev`**
2.  Switch to development branch: **`git checkout 0.9.6_dev`**
3.  Merge work branch into development: **` git merge
     `<working_branch_name>`  --no-ff
    ( `[`--ff`](http://www.kernel.org/pub/software/scm/git/docs/git-merge.html#_fast_forward_merge)`
    is usually the default) `**
4.  (Optional) - Once work has been pushed to remote you can delete your
    local development branch: **` git branch -d
     `<working_branch_name>**

## Push local Development changes to remote Development branch

1.  Rebase the development branch to ensure it has the latest changes:
    **`git pull --rebase origin 0.9.6_dev`**
2.  Push Changes to Remote: **`git push origin 0.9.6_dev`**

## Comment Conventions

A commit comment consists of the following written **in present tense**:

  - **a summary line** short (60 chars or less) summary of changes made.
    Should use the following format: `CHANGE_TYPE[ID]: Summary`
  - **a newline** - Always follow your summary with a newline.
  - **an (optional) details section:** Should be wrapped to 72 columns
    (git command such as git log don't wrap).

A useful command for testing for commits that may not follow these
conventions:

` git log --format="%h|%cd|%s" | awk --posix 'BEGIN {FS = "|"} ; {if(!/(bug|refactor|refinement|feature)(`\[(ats|bgz)_.{5,6}\]`)?: [A-Z]/ || /  / || /: [A-z]+ed / || /\.$/){print $1"|"$2"|"$3}}'`

#### **Comment with Details:**

<tt>`bug[ats_ABCDE]: Fix artifact copy/paste copying invalid attribute types`

`Artifact copy/paste code change to check attribute types before creating`
`them in the copied artifact. Additional checks include:`

`- Change one`

`- Change two`

`- Change three`</tt>

#### Summary Change Type Identifiers

  - Bugzilla Number - change originated from a bugzilla item, therefore
    use the following format: **bgz_12314**
  - Action HRID - change originated from an ATS action, therefore use
    the following format: **ats_HRID**

#### Change Type Examples

<table>
<thead>
<tr class="header">
<th><p>Comment Prefix</p></th>
<th><p>Branch Name Prefix</p></th>
<th><p>Description</p></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><p><strong>feature:</strong></p></td>
<td><p><strong>Feature_</strong></p></td>
<td><p><code>New functionality, Non-Trivial, change in code behavior</code><br />
<code>e.g.: comment - feature[bgz_12345]: Add ability to import open office docs</code><br />
<code>      branch - Feature_bgz_12345_open_office_doc_import</code></p></td>
</tr>
<tr class="even">
<td><p><strong>refinement:</strong></p></td>
<td><p><strong>Refinement_</strong></p></td>
<td><p><code>Small change in code behavior</code><br />
<code>Encompasses: performance improvements, memory usage, code robustness, test coverage increase</code><br />
<code>e.g.: comment - refinement: Improve artifact loading performance</code><br />
<code>      comment - refinement: Add test case for presentation type RenderAttribute</code><br />
<code>      branch - Refinement_new_RendererAttribute_test</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>refactor:</strong></p></td>
<td><p><strong>Refactor_</strong></p></td>
<td><p><code>Non-functional changes to improve code design/quality.</code><br />
<code>e.g.: comment - refactor: remove unnecessary exception OseeInvalidSessionException</code><br />
<code>      branch - Refactor_remove_unused_exceptions</code></p></td>
</tr>
<tr class="even">
<td><p><strong>bug:</strong></p></td>
<td><p><strong>Bug_</strong></p></td>
<td><p><code>Change that fixes problems with the existing code.</code><br />
<code>e.g.: comment -  bug: stop export change report from including multiples of the same artifact</code><br />
<code>      comment -  bug[ats_UNJKK]: Implement client-side change to use search request/response messages</code><br />
<code>      branch - Bug_ats_UNJKK_fix_quick_search</code></p></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>



## Additional Git Commands

Collection of Useful Git Commands

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
<code>cd /c/UserData/git</code><br />
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
<code>cd /c/UserData/git</code><br />
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



## Git Scenarios

### Merge Release development branch into master

`git checkout 0.9.7_dev`
`git pull --rebase`
`git rebase -i `<commit id of masters head>
`open git-rebase-todo in text editor`
`git rebase --committer-date-is-author-date 148d414`
`git checkout master`
`git pull --rebase`
`git merge 0.9.7_dev`
`git push`
`git branch -D 0.9.7_dev`
`git push origin :0.9.7_dev`
`git checkout -b 0.9.8_dev master`
`git push origin 0.9.8_dev:refs/heads/0.9.8_dev`

### End-of-line Normalization

`echo "* text=auto\n*.pdf -text" >>.gitattributes`
`rm .git/index     # Remove the index to force git to`
`git reset         # re-scan the working directory`
`git status        # Show files that will be normalized`
`git add -u`
`git add .gitattributes`
`git commit -m "Introduce end-of-line normalization"`

## Git Resources

  - **Git Commands**
      - [Git
        Magic](http://www-cs-students.stanford.edu/~blynn/gitmagic/)
      - [Git PRO Book](http://progit.org/book/)
      - [Git Community Book](http://book.git-scm.com/index.html)
      - [Git
        Tips](https://git.wiki.kernel.org/index.php/GitTips#head-1cdd4ab777e74f12d1ffa7f0a793e46dd06e5945)
      - [Git Cheatsheets](http://devcheatsheet.com/tag/git/)
      - [Git Brain
        Dump](http://people.gnome.org/~federico/misc/git-cheat-sheet.txt)
      - [GIT for Eclipse
        Users](http://wiki.eclipse.org/EGit/Git_For_Eclipse_Users)

<!-- end list -->

  - **Git Workflows**
      - [GIT Branches, RC, etc
        Workflow](http://reinh.com/blog/2009/03/02/a-git-workflow-for-agile-teams.html)
      - [GIT Branching Model](http://nvie.com/git-model)
      - [Sample Git
        Workflow](http://nakedstartup.com/2010/04/simple-daily-git-workflow/)

<!-- end list -->

  - **Git Conflicts/Merging**
      - [Understanding conflict
        marks](http://www.kernel.org/pub/software/scm/git/docs/git-merge.html#_how_conflicts_are_presented)

<!-- end list -->

  - **Git Migration Help**
      - [Git for SVN Users](http://git.or.cz/course/svn.html)
      - [Git for CVS
        Users](http://www.chem.helsinki.fi/~jonas/git_guides/HTML/CVS2git/)
      - [Git for Eclipse
        Committers](http://wiki.eclipse.org/Git_for_Committers)
      - [GIT,SSH Setup,Repository Migration, & Committer
        Info](http://wiki.eclipse.org/Git)

## Source Repositories and Access

<table>
<thead>
<tr class="header">
<th><p>Name</p></th>
<th><p>Description</p></th>
<th><p>Location</p></th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><p><strong>Main OSEE Source</strong></p></td>
<td><p>OSEE Source Code</p></td>
<td><p><code>git clone https://&lt;user.name&gt;@git.eclipse.org/gitroot/osee/org.eclipse.osee.git</code></p>
<p><code>org.eclipse.osee</code><br />
<code>..features/</code><br />
<code>..plugins/</code><br />
<code>..releng/</code></p></td>
</tr>
<tr class="even">
<td><p><strong>Orbit Release Engineering Bundles</strong></p></td>
<td><p>3rd Party Libraries</p></td>
<td><p><code>host:  dev.eclipse.org</code><br />
<code>Repository path: /cvsroot/tools</code><br />
<code>connection type:  extssh</code><br />
<code>org.eclipse.orbit/org.eclipse.orbit.build.feature.set1</code><br />
<code>org.eclipse.orbit/org.eclipse.orbit.releng</code></p></td>
</tr>
<tr class="odd">
<td><p><strong>SWT Nebula Bundles</strong></p></td>
<td><p>XViewer Source Code</p></td>
<td><p><code>host:  dev.eclipse.org</code><br />
<code>Repository path: /cvsroot/technology</code><br />
<code>connection type:  pserver</code><br />
<code>user:  anonymous</code><br />
<code>/cvsroot/technology/org.eclipse.swt.nebula/org.eclipse.nebula.widgets.xviewer</code></p></td>
</tr>
<tr class="even">
<td><p><strong>OSEE Website</strong></p></td>
<td><p>OSEE Eclipse Website Source Code</p></td>
<td><p><code> host:  dev.eclipse.org</code><br />
<code> Repository path: /cvsroot/org.eclipse</code><br />
<code> connection type:  extssh</code></p></td>
</tr>
<tr class="odd">
<td></td>
<td></td>
<td></td>
</tr>
</tbody>
</table>



## XViewer and Nebula Project

XViewer is a fully featured excel spreadsheet-like table-tree viewer
based on the SWT TreeViewer. See more at [Nebula
XViewer](https://www.eclipse.org/nebula/widgets/xviewer/xviewer.php).

XViewer bundles were contributed to the Eclipse Nebula project for use
by more than just OSEE. For this reason, changes must be managed through
that project which currently uses GitHub.

The basic steps for updating:

1.  Create [GitHub](http://github.com/eclipse) account
2.  Fork Nebula for your account
    1.  If already forked go to account pulldown then My Repositories
        then nebula hyperlink
3.  Select Fetch upstream, then Fetch and Merge to update fork
4.  Clone your GitHub account to your local machine
    1.  git clone <https://github.com/dondunne/nebula.git>
        1.  change dondunne to your github username
5.  Import into Eclipse and make changes (see below)
6.  Commit changes to your local git
7.  Push to your GitHub fork (see below)
    1.  git push origin master with github
    2.  username: \<email you used for GitHub\> eg: dondunne@gmail.com
    3.  password: Personal Access Token created on GitHub [Creating A
        Personal Access
        Token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)
8.  Make a GitHup "New Pull Request" which compares your github fork
    with master, performs builds and allows others to review
    1.  Login to GitHub
    2.  Account pulldown \> Your Repositories \> Nebula \> Pull Requests
    3.  New pull request \> Create pull request
    4.  Add description \> Create pull request
9.  Once build/review, "Merge Pull Request" by Nebula committer

### Cloning the <b>XViewer (Nebula)</b> Repository

1.  Go to [Nebula Repository](https://github.com/eclipse/nebula)
2.  If you plan to make any commits, you need to fork the nebula project
    and clone from your fork
    1.  Select fork button on top right of main page
    2.  Follow instructions to fork
    3.  Got to profile picture dropdown in top right
    4.  Select Your Profile
    5.  Select Nebula hyperlink
    6.  Select green Clone or download button
    7.  Copy url
    8.  Skip down to "Clone to your local machine" step below
3.  If you just want source code
    1.  Select green Clone or download button on main screen
    2.  Copy url
    3.  Skip down to "Clone to your local machine" step below
4.  Clone to your local machine
    1.  Open Git Bash
    2.  Go to your repository directly, i.e. C:/Tools/git_main
    3.  Run the following command replacing <repo> with the https repo
        link
        1.  <b>git clone \<repo\></b>
    4.  Open OSEE, Git Perspective,
    5.  Add the nebula repo to your Git Repositories
    6.  Expand nebula \> Working Tree \> widgets \> select xviewer
    7.  Select all but xviewer.integration bundle
    8.  Right-click \> Import Projects
    9.  Switch back to your Java Perspective and xviewer projects should
        be there

### Rebase your XViewer (Nebula) Repository Fork

  - Make sure to add the upstream repository the first time you must do
    this process
      - git remote add upstream <https://github.com/eclipse/nebula.git>
  - Repeat these steps to pull rebase your repository

<!-- end list -->

1.  git fetch upstream
2.  git rebase upstream/master

### Pushing Changes to <b>XViewer (Nebula)</b> Repository

1.  Make sure you've forked github and cloned from your own fork (see
    cloning step above)
2.  Make sure all changes are complete and working in your repository
3.  Commit changes like normal
4.  Create PAT if you haven't already (Requires Authentication as of
    August 13, 2021)
    1.  Log into <https://github.com/>
    2.  Click on user icon on the top right and select settings
    3.  Select Developer Settings from the list on the left
    4.  Select Personal Access Token from the list on the left
    5.  Click on generate new token and enter password
    6.  Give token a name, give yourself full access and select generate
        token
    7.  SAVE token somewhere safe\!
5.  To push run the following command
    1.  <b>git push origin master</b> (The first time pushing you will
        be prompted for username/email and password, but hit cancel)
    2.  Enter your username and hit enter
    3.  Enter your PAT and NOT your github password into the popup (You
        can copy paste from where you saved it)
6.  Go to [eclipse/nebula](https://github.com/eclipse/nebula)
7.  Click <b>Create Pull Request</b>
8.  Select <b>Compare Across Forks</b>
    1.  Base Repo - <b>eclipse/nebula</b>, base - <b>master</b>
    2.  Head Repo - <b>\<your username\>/nebula</b>, base - <b>master</b>
9.  Create Pull Request
10. Wait for build and all checks to pass
11. Ask Don Dunne to merge the pull request

## Configure a Development Runtime

1.  Follow the [PostgreSQL installation
    instructions](http://www.eclipse.org/osee/documentation/installation/postgresql_install.php).
2.  Use the configuration
    `../org.eclipse.osee.support.config/launchConfig/OSEE Demo
    Application Server [localhost].launch` to run an OSEE application
    server
3.  Use the configuration
    `../org.eclipse.osee.ats.config.demo/MasterTestSuite_DemoDbInit.launch`
    to initialize an OSEE database
4.  Use the configuration
    `../org.eclipse.osee.ats.config.demo/MasterTestSuite_DemoDbPopulate.launch`
    to populate the database for demonstration purposes
5.  Use the configuration
    `../org.eclipse.osee.support.config/launchConfig/OSEE Demo product
    [localhost].launch` to run a local OSEE client

