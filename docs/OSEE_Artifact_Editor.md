This page should be a carbon-copy of
[OSEE/Users_Guide/Features\#Artifact_Editor](/docs/OSEE/Users_Guide/Features.md#Artifact_Editor "wikilink")
which is just repeated on a separate page for convenience. It is
possible that it has become out of date due to a new release of OSEE
and, if you suspect this, please follow the above link.

The Artifact editor provides specialized features for editing artifacts
(this is the default editor for editing attributes and relations). The
editor can be opened through Artifact Explorer, by double-clicking on
any artifact or right-clicking on an artifact and selecting to Open With
"Artifact Editor".

Associated with the editor is an Artifact-specific Outline view, which
shows the structure of the active artifact. It is updated as the user
edits the artifact.

![artifacteditor.jpg](/docs/images/artifacteditor.jpg "artifacteditor.jpg")

The Artifact Editor is divided into the following sections *(some
sections can be expanded and collapsed by clicking on the section's
title bar)*.

### Title Area

1.  **Artifact Name** - the artifact's name attribute
2.  **Message Area** - reports issues that need to be addressed by the
    user before saving is allowed. Click on the message to open a
    message summary window. From the message summary window, click on
    any message to jump to the item associated with the message.
3.  **Toolbar Area**

| Icon                                                                          | Description                                                                                                                                                             |
| ----------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ![image:refresh.gif](/docs/images/refresh.gif "image:refresh.gif")                         | Refreshes the artifact editor. Displaying the artifact's current data. NOTE: Changes made to the form will be lost unless data is saved before clicking on this button. |
| ![image:bug.gif](/docs/images/bug.gif "image:bug.gif")                                     | Opens an action against the Artifact Editor.                                                                                                                            |
| ![image:open.gif](/docs/images/open.gif "image:open.gif")                                  | Opens the artifact or if clicking on the down-arrow, displays the open with sub-menu.                                                                                   |
| ![image:delete.gif](/docs/images/delete.gif "image:delete.gif")                            | Deletes the artifact and closes the editor.                                                                                                                             |
| ![image:outline_co.gif](/docs/images/outline_co.gif "image:outline_co.gif")               | Displays the outline view.                                                                                                                                              |
| ![image:dbiconblue.gif](/docs/images/dbiconblue.gif "image:dbiconblue.gif")                | Opens the artifact's resource history.                                                                                                                                  |
| ![image:magnify.gif](/docs/images/magnify.gif "image:magnify.gif")                         | Displays the artifact in an Artifact Explorer view.                                                                                                                     |
| ![image:branch.gif](/docs/images/branch.gif "image:branch.gif")                            | Open the Branch Manager View and highlights the artifact's branch.                                                                                                      |
| ![image:authenticated.gif](/docs/images/authenticated.gif "image:authenticated.gif")       | Locks or unlocks the artifact for editing. Locking an artifact prevents other users from making changes to it.                                                          |
| ![image:copytoclipboard.gif](/docs/images/copytoclipboard.gif "image:copytoclipboard.gif") | Copies a link to the latest version of the artifact to the clipboard.                                                                                                   |

### Artifact Information Area

Displays the artifact's branch, artifact type, and human readable id.

### Attributes Section

Displays attribute types to be edited. Attribute types can be added or
deleted by clicking on the appropriate toolbar button *located on the
upper-right of the attributes section title bar*. **Note: The following
operations follow min/max occurrence rules defined by the attribute's
type.**

| Icon                                                        | Description                                                                                                                                                                                                 |
| ----------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ![image:back.gif](/docs/images/back.gif "image:back.gif")                | Opens an attribute type selection dialog; the selected attribute type instances will be reset to their default value.                                                                                       |
| ![image:greenplus.gif](/docs/images/greenplus.gif "image:greenplus.gif") | Opens a dialog displaying attribute types that can be added to the artifact.                                                                                                                                |
| ![image:delete.gif](/docs/images/delete.gif "image:delete.gif")          | Opens a dialog displaying attribute types that can be deleted from the artifact. '''Note: Data entered for the attribute type to be deleted will be lost as soon as the dialog's **OK** button is selected. |

### Relations Section

All relations that are defined as being valid for the artifact are
shown. Relations can be added by dragging any set of artifacts into the
appropriate relation group. Opening relation groups will show all
artifacts that are currently related. Double-clicking a related artifact
will open it in its default editor (normally the Artifact Editor).

**Pop-up Menu** - To display, expand the **Relations** section, select
one or more artifacts, and perform a right-click.

| Command                    | Description                                                           |
| -------------------------- | --------------------------------------------------------------------- |
| Open                       | Opens the selected relation using the default editor.                 |
| Edit                       | Opens the Artifact for editing.                                       |
| Mass Edit                  | Opens the Artifact Mass Editor populated with the selected artifacts. |
| View Relation Table Report | Opens an HTML report of the relation tree.                            |
| Order Relation             | (Future Capability)                                                   |
| Delete Relation            | Deletes the selected relations.                                       |
| Expand All                 | Expands all tree nodes containing relations to this artifact.         |
| Select All                 | Selects all tree nodes.                                               |
| Delete Artifact            | Deletes the selected artifact and its relation to this artifact.      |

### Details Section

Displays artifact meta-data.