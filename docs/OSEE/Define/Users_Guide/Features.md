# Views and Editors

## Merge Manager

![image:stoppeddialog.jpg](/docs/images/stoppeddialog.jpg "image:stoppeddialog.jpg")

Upon selecting to commit a working branch, OSEE will prompt the user to
perform a merge if conflicts are detected between the changes made on
the working branch and any changes made to the parent branch since the
working branch was created. The Merge Manager in OSEE will be used to
reconcile these differences.

![image:attr.jpg](/docs/images/attr.jpg "image:attr.jpg")

From the *Merge Manager* tab, select the Merged Value icon which will
cause the *Edit the attribute* window to appear. Perform the following
steps for each artifact listed on the *Merge Manager* tab.

![image:3-way.jpg](/docs/images/3-way.jpg "image:3-way.jpg")

1.  Determine which change would be the easiest to re-implement
    (typically the smaller and simpler of the two). This can be done by
    comparing all of the changes made to this UI.
      - **Show Source Diff** displays the changes made on this working
        branch.
      - **Show Destination Diff** displays the changes made on the
        parent branch.
      - **Show Source/Destination Diff** displays changes made on
        destination against changes made on source.
2.  Select the *more complicated* of the two changes to populate the
    Merge Artifact: **Populate with Source Data** or **Populate with
    Destination Data**. The Merged Value column on the *Merge Manager*
    tab and the top-most icon in the *Edit the attribute* window will
    update to display "S" or "D" based upon this selection.
3.  Select **Edit Merge Artifact** to open the merge document for
    editing.
4.  Re-implement the changes from the *simpler* change report.
5.  If at any time the merge effort needs to be cleared or re-started,
    select **Clear the Merge Artifact**.
6.  The following selections may be used to review and confirm the
    changes made during the merge.
      - **Show Source/Merge Diff** displays the additional changes
        beyond those made on the working branch.
      - **Show Destination/Merge Diff** displays the additional changes
        beyond those made on the parent branch.
7.  Select **Finish**
8.  Under the Conflict Resolution column on the *Merge Manager* tab,
    check the box so that the resolution status updates from "Modified"
    to "Resolved".
9.  Relation order conflicts will appear as an attribute conflict. OSEE
    will auto merge these issues with no need for user input. If a
    problem arises please contact your OSEE admin.

Once all artifact conflicts have been addressed, the *Merge Manager* tab
will report "All Conflicts Are Resolved." At this point, the user can
return to the Workflow tab and re-initiate committing the branch.

OSEE will display the *Commit Branch* window to confirm the conflicts
resolved via the Merge Manager. Since all the conflicts have been
resolved, the user can select **Ok** to finish committing the branch.

![image:complete2.jpg](/docs/images/complete2.jpg "image:complete2.jpg")

# Wizards

## Artifact Import Wizard

![image:artifact_import_selection.jpg](/docs/images/artifact_import_selection.jpg "image:artifact_import_selection.jpg")
![image:artifact_import_wizard.jpg](/docs/images/artifact_import_wizard.jpg "image:artifact_import_wizard.jpg")

