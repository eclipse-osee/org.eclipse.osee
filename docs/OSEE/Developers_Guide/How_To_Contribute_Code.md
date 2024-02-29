# How To Contribute Code

## Prerequisites
This guide assumes you have already completed the [Getting Started](/docs/OSEE/Users_Guide/Getting_Started.md) guide in which the repository and development environment are set up.

## Determine Your Changes
If you wish to contribute but do not know where to start, reach out and the team will happily provide some backlog items for you to dig into.

## Make Your Changes
Changes made to the web portion of our code base (plugins/org.eclipse.osee.web) can be made using an IDE of your choosing, whereas any other changes should be made with an OSEE Eclipse IDE set up as described in the [Getting Started](/docs/OSEE/Users_Guide/Getting_Started.md) guide. This will ensure that your workspace has been properly configured and will help to automatically enforce code style standards agreed upon by the OSEE team and set in osee_team_preferences.epf.

Additionally, please ensure your changes comply with the [Eclipse Contributor Agreement](https://www.eclipse.org/legal/ECA.php) (ECA) and [Developer Certificate of Origin](https://www.eclipse.org/legal/DCO.php) (DCO).

## Test Your Changes
When you have completed coding your changes, it is advisable to test them locally before submitting them for review to avoid unnecessary repeat reviews.

To test, there are several options available depending on the kind of changes you have made.

For changes not tied to the web (plugins/org.eclipse.osee.web), you have an option to test through the OSEE Eclipse IDE or through a Maven build. Web changes should be tested though angular. In cases where the change affects both the web and java code, IDE/Maven testing along with angular testing should be used.

#### Test Through The OSEE Eclipse IDE
Note - A local client and server started in this fashion will automatically have all saved changes made in your workspace. Some changes can even be hot applied while the server and client are running.
1. Run a local OSEE Server.
    1. Select the debug icon → Debug Configurations → OSGi Framework → OSEE_Application_Server_\[HSQLDB\] → Debug
    2. Wait until you see "Registered servlet '/osee/client/loopback'" and "Registered servlet '/osee/console'" in the console window.
2. Run the AtsIde_Integration_TestSuite
    1. Select the debug icon → Debug Configurations → JUnit Plug-in Test → AtsIde_Integration_TestSuite → debug
        1. If the test suite pauses at any point you can safely select "Resume".
        2. If this is your first time running this test suite, you may need to run the application sever and test suite twice. The first time the application server starts up it will not have a database to connect to. Running the test suite against the server will initialize the database, but tests will most likely fail. Terminating the application server and restarting it, then running the AtsIde_Integration_TestSuite for a second time should yield the desired results.
3. Run manual tests using a local runtime client and server to ensure your change behaves as expected in a built client and server.
    1. Run a local OSEE server as described in step one.
        1. debug icon → Debug Configurations → OSGi Framework → OSEE_Application_Server_\[HSQLDB\] → Debug
    2. Start at local client that connects to the local server.
        1. debug icon → Debug Configurations → Eclipse Application → OSEE_IDE_\[localhost\]
    3. Exercise your changes in the client and confirm they behave as expected.

#### Test Through A Maven Build
1. Follow the [OSEE build instructions](/docs/OSEE/Developers_Guide/How_To_Build_OSEE.md) and ensure the final maven command does not include "-DskipTests".
2. It is still advisable to test manually using a local client and server. This can be done through the IDE instructions above, or by unzipping and running the server and client produced through the OSEE build instructions. Ensure a local database has already been initialized.

#### Test Through Angular
1. Follow the [OSEE build instructions](/docs/OSEE/Developers_Guide/How_To_Build_OSEE.md) which cover how to do angular tests and builds.
2. Manual tests can be performed by starting a local server through the IDE and running `ng serve --open` in the `plugins/org.eclipse.osee.web` directory or by running the fully built server and client produced through the OSEE build instructions.

## Submit Your Changes For Review
NOTE - The following is valid for Gerrit. It will no longer apply after OSEE is migrated.
1. Make sure your changes are ready for review.
    1. Changes made through the OSEE IDE configured with osee_team_preferences.epf should automatically conform to code style requirements upon saving. Web changes made outside of the OSEE IDE will have been style checked through the angular tests.
    2. If you have added a new testable feature, ensure that test cases have also been created to accompany the change.
    3. If you have created any new files, ensure they have a Copyright and License Header in typical Eclipse style.
    4. Your submission will be reviewed based on the [Peer Review Checklist](/docs/OSEE/Software_Development_Process/Peer_Review_Checklist.md) along with completeness, accuracy, and integration.
2. Stage your changes.
    1. `git add -u` (stages all, but does not add new files) OR
    2. `git add -A` (stages all AND adds new files) OR
    3. Selectively stage desired files.
3. Commit your staged files.
    1. `git commit -s -m "your_commit_message_here"`
    2. Your commit message should be descriptive, but as succinct as possible.
4. Ensure you have the latest changes from the remote.
    1. `git pull --rebase`
5. Push your commit for review.
    1. `git push origin HEAD:refs/for/dev`

## Have Your Changes Approved And Merged
Your changes will require validation from the OSEE Bot which runs a full build and all applicable tests, approval from two teammates, and finally to be approved and merged by someone of committer level on the OSEE team.

1. Navigate to our [Gerrit webpage](https://git.eclipse.org/r/q/project:osee/org.eclipse.osee) and select your review request.
2. Select the pencil icon and add reviewers.
    - It is often best to send a direct email to the reviewers you select to request a review. People often set filers on the notifications automatically generated by Gerrit so this is the best way to ensure your review gets seen promptly. You can find the reviewers email by hovering over the chiclet containing their name.
3. Secure at least two positive reviews from teammates.
4. Receive a positive review from the OSEE Bot.
5. Have someone on the team of committer level supply a "+2" approval and merge your commit.
    - This can be the same person as one of your reviewers, someone new, or even yourself if you are of committer level.
6. Congratulations! You have contributed to one of the largest and longest running projects on Eclipse!
