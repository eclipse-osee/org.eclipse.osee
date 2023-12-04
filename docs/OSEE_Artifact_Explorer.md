This page should be a carbon-copy of
[OSEE/Users_Guide/Features\#Artifact_Explorer_View](/docs/OSEE/Users_Guide/Features.md#Artifact_Explorer_View "wikilink")
which is just repeated on a separate page for convenience. It is
possible that it has become out of date due to a new release of OSEE
and, if you suspect this, please follow the above link.

The Artifact Explorer view, shown by default in the Define perspective,
shows the artifact hierarchy of the selected branch. The artifact
hierarchy is derived from the artifact's default hierarchy relation. By
default, all branches have a default hierarchy root artifact which is
the hierarchy tree's base element.

![artifactexplorer.jpg](/docs/images/artifactexplorer.jpg "artifactexplorer.jpg")

### Toolbar buttons

| Icon                                                                                 | Description                                                                                                                              |
| ------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------- |
| ![image:collapseall.gif](/docs/images/collapseall.gif "image:collapseall.gif")                    | Collapses all tree nodes.                                                                                                                |
| ![image:up.gif](/docs/images/up.gif "image:up.gif")                                               | Navigates to the parent container of the artifacts that are currently displayed at the top level in the view.                            |
| ![image:artifact_explorer.gif](/docs/images/artifact_explorer.gif "image:artifact_explorer.gif") | Opens a new instance of the Artifact Explorer view.                                                                                      |
| ![image:branch_change.gif](/docs/images/branch_change.gif "image:branch_change.gif")             | Opens the Change Report View for the Artifact Explorer's selected branch. This report will show all changes made to the selected branch. |
| ![image:artifact_search.gif](/docs/images/artifact_search.gif "image:artifact_search.gif")       | Opens the Quick Search View for the selected branch.                                                                                     |
| ![image:bug.gif](/docs/images/bug.gif "image:bug.gif")                                            | Opens an action against the Artifact Explorer view.                                                                                      |

### Pop-up Menu

To display, select one or more artifacts and perform a right-click.

| Command                  | Description                                                                                                                                   |
| ------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------- |
| Open                     | Opens the selected artifact using the default editor.                                                                                         |
| Open With                | Opens a sub-menu listing the available editor's for this artifact.                                                                            |
|                          |                                                                                                                                               |
| Reveal on Another Branch | Open's a new instance of Artifact Explorer for a user selected branch. Expands tree elements to display the selected artifact to be revealed. |
|                          |                                                                                                                                               |
| New Child                | Creates a new artifact and places it directly under the selected artifact.                                                                    |
| Go Into                  | Sets the selected artifact as the base of the artifact hierarchy tree hiding artifacts except child artifacts.                                |
| Mass Edit                | Opens the Artifact Mass Editor populated with the selected artifacts.                                                                         |
| Sky Walker               |                                                                                                                                               |
|                          |                                                                                                                                               |
| Delete Artifact          | Deletes the selected artifacts.                                                                                                               |
| Purge Artifact(s)        | Purges the selected artifacts from the data store.                                                                                            |
| Rename Artifact          | Allows a user to quickly change the artifact's name attribute.                                                                                |
|                          |                                                                                                                                               |
| Show Resource History    | Opens the Resource History view for the selected artifact. This will display all transactions for this artifact.                              |
|                          |                                                                                                                                               |
| Import                   | Opens Eclipse's import dialog.                                                                                                                |
| Export                   | Opens Eclipse's export dialog.                                                                                                                |
|                          |                                                                                                                                               |
| Lock                     | Locks the artifact so the current user is the only one allowed to make changes to its attributes.                                             |
|                          |                                                                                                                                               |
| Copy                     | Copies the artifact.                                                                                                                          |
| Paste                    | Pastes the artifact.                                                                                                                          |
| Expand All               | Expands all tree nodes from the selected artifact down.                                                                                       |
| Select All               | Selects all open tree nodes.                                                                                                                  |
|                          |                                                                                                                                               |
| Access Control           | Opens the access control dialog.                                                                                                              |

### Operations

Operations that can be performed on an Artifact Explorer.

| Command                     | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| --------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Cross Branch Drag and Drop  | Artifacts can be referenced from other branches by dragging an artifact from one branch and dropping in onto another Artifact Explorer. The outcome will be one of two actions. If the artifact already existed on the branch it will be updated with the source artifacts state data. If the artifact did not exist on the destination branch it will be introduced to that branch. Meaning it will show up in the change report as an introduced artifact. |
| File Document Drag and Drop | Artifacts can be created by dragging and dropping files directly onto the parent artifact. After the drop is performed, the Artifact Import Wizard should open. Select the import method and artifact type to convert file into. This should create a child artifact directly under the artifact where the file was dropped.                                                                                                                                 |