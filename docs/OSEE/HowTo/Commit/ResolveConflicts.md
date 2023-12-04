## Resolution Of Conflicts


For Word Formatted Content conflicts see the section below. This section
addresses all other conflicts.

### Informational Conflicts

Informational conflicts are identified by the ![Image:OSEE
issue.gif](OSEE_issue.gif "Image:OSEE issue.gif") icon in the conflict
resolution column in the GUI. Informational conflicts require no action
by the user, and no actions are provided in the GUI other than the
ability to use the right click menu to examine the artifact using the
tools provided there. An informational conflict is generated when the
Source branch deletes an Artifact or an Attribute and that same Artifact
or Attribute was modified on the Destination Branch. This is to allow
the user the opportunity to review a change that was made on the
Destination Branch that might make them want to take some action in
regards to their deletion.



### Un-resolvable Conflicts

Un-resolvable Conflicts are identified by the ![Image:OSEE red
light.gif](OSEE_red_light.gif "Image:OSEE red light.gif") icon in the
conflict resolution column of the GUI. This conflicts require the user
to revert the Artifact or Attribute that caused the conflict on the
Source Branch. An Un-resolvable conflict is caused when the Destination
Branch deletes an Artifact or Attribute while the Source Branch modifies
that same Artifact, Attribute. The reason the user must revert their
changes is that committing in their changes would essentially undo that
deletion and bring that item back into existence. If the deletion should
not have happened the user needs to talk with the committer of the
deletion to resolve the issue.



### Attribute Conflicts

Attribute Conflicts occur when both the Destination and Source branch
modify an attribute. This section will cover all attributes except Word
Formatted Content Attributes. The resolution of these Attribute values
provide three options. Use the Source attribute value, use the
destination attribute value, use a modified value that is some
combination of the source and destination values. In order to use the
Source Value the user may left click on the ![Image:OSEE green
s.gif](OSEE_green_s.gif "Image:OSEE green s.gif") icon in the Source
Value column. This will copy the ![Image:OSEE green
s.gif](OSEE_green_s.gif "Image:OSEE green s.gif") icon and the value
displayed in the Source Value column into the Merged Value Column. In
order to use the Destination Value the user may left click on
the ![image:osee blue d.gif](/docs/images/osee_blue_d.gif
"image:osee blue d.gif") icon in the Destination Value column. This
will copy the ![image:osee blue d.gif](/docs/images/osee_blue_d.gif
"image:osee blue d.gif") icon and the value displayed in the Source
Value column into the Merged Value Column. Both of these options are
also available from the Merge Wizard (Left click on the icon in the
Merge Value column) with the "Load Source Data" and "Load Destination
Value" buttons. In order to modify the value to some combination the
user must bring up the Merge Wizard which has an embedded editor
specific to the attribute that needs to be modified. Once the value is
accurately entered in the editor the user may than select "Finish" This
will place a ![image:osee yellow m.gif](/docs/images/osee_yellow_m.gif
"image:osee yellow m.gif") icon in the Merged Value column along with
the new value. The user then right clicks on the ![Image:OSEE chkbox
disabled.gif](OSEE_chkbox_disabled.gif
"Image:OSEE chkbox disabled.gif") in the Conflict Status Column so that
the ![image:osee accept.gif](/docs/images/osee_accept.gif
"image:osee accept.gif") icon is displayed. The conflict is resolved
and will allow the Source Branch to be committed.



## Resolution Of Conflicts (Word Formatted Content)

Resolution of conflicts is provided in two different ways. They can
either copy and paste the changes into their Merge Artifact document or
they can generate a Three Way Merge and accept the changes that show up
in the generated document. Both approaches have their advantages and
disadvantages and are best suited for different situations. They can
also be combined where the situation warrants it, however the three way
merge must always be done first if this is the case.

### Manual Merging

#### Usage

When one version of the artifact has many changes and the other version
has very few changes
When both files have formatting changes
When three way merging generates a complex document
When both versions edit the same text in multiple places
Manual Merging is the process of combining the Source Branch changes and
the destination branch changes manually by copying and pasting them into
the Merge Artifact document. The Merge Artifact is a separate version of
the artifact that will preserve the details of the Merge, and will be
reviewable in the Merge Manager after an artifact is committed.
IMPORTANT: If the user makes the changes to their Source Branch instead
of on the Merge Artifact the Merge Manager will incorrectly represent
the merge in future reviews.

The following procedure illustrates the functionality available to
facilitate a manual merge.
The user will first either launch the Merge Wizard by left clicking on
the icon in the Merge Value column of the GUI or they may select the
functionality from the right click menu for the conflict in question.
The first thing to do is to bring up a word document comparison of both
the Source Branch Version and the Destination Branch Version. These
documents will show all of the changes that have been made to these two
artifacts since the Source Branch was created. To launch these
difference's the user either select "Show Source Diff" and "Show
Destination Diff" from the wizard or "Differences"-\>"Show Source Branch
Differences" and "Differences"-\>"Show Destination Branch Differences"
from the right click menu. These will bring up the two difference's in
different Word instances with window labels to allow the user to
differentiate the files. The intention of bringing up these difference's
is twofold. Firstly, it allows the user to identify the file that has
the most changes. Secondly, it will come in use later when the user
copy's and paste's changes into the Merge document.

Upon identifying the branch that has the most changes the user should
then set the Merge Artifact to contain that branches value. This is done
by either selecting "Populate with Source Data" or "Populate with
Destination Data" from the Merge Wizard or left clicking on
the ![image:osee_green_s.gif](/docs/images/osee_green_s.gif
"image:osee_green_s.gif") icon or
the ![image:osee_blue_d.gif](/docs/images/osee_blue_d.gif
"image:osee_blue_d.gif") icon in the Source and Destination Value
columns in the Merge Manager GUI. The user can then bring up the Merge
Artifact for editing by clicking on "Edit Merge Artifact" in the Merge
Wizard or in the right click menu. The Document that comes up contains
the Merge Artifact and any changes made to it will be reflected when the
Source Branch is committed. The user can than begin to copy the changes
from the diff report that showed the fewest changes (opposite of the one
chosen as the baseline). After all changes have been migrated into the
Merge Artifact document the user than saves the document, which will
preserve the Merge Artifact value. The user should be aware that any
changes they do not wish to preserve from either the Source or
Destination version of the Artifact need to be omitted on the Merge
Artifact.

The user then right clicks on
the ![image:osee_chkbox_disabled.gif](/docs/images/osee_chkbox_disabled.gif
"image:osee_chkbox_disabled.gif") in the Conflict Status Column so that
the ![image:osee_accept.gif](/docs/images/osee_accept.gif
"image:osee_accept.gif") icon is displayed. The conflict is resolved
and will allow the Source Branch to be committed.



### Three Way Merge

#### Usage

When both versions have many changes or both versions have few changes.

When only one file has formatting changes (Must be combined with Manual
Merging in this case)

When three way merging generates an understandable document

Three Way Merging leverages Microsoft Word's ability to merge documents.
At the beginning of any Word Formatted Content merge it is recommended
that user generate a Three Way Merge and check the complexity of the
document. In most cases Three Way Merging is a quicker way to merge two
documents, however in some cases the Three Way Merge will generate a
document that is difficult to use and understand. This usually arises
when the Source and Destination branches have edited the same text or if
one of the branches has touched a large percentage of the file. As it
runs fairly quickly it is always a good idea to run it at the beginning
of a Merge to check if it is useful. Three Way Merging only allows the
user to maintain format changes from one of the documents. If format
changes are made on both documents the Three Way Merge will prompt the
user as to which format changes they would like to maintain, the user
will then need to copy the format changes from the other document into
the Merge Artifact document manually.

A Three Way Merge is generated by selecting Generate Three Way Merge
from either the Merge Wizard or the right click menu. IMPORTANT:
Generating a Three Way Merge will discard any changes made to the Merge
Artifact, therefore a prompt will make sure this is the intended
operation. If a user had started a Three Way Merge previously but had
not completed the Merge the user is also given the option of continuing
the previous Merge in the prompt (Selecting Edit Merge Artifact will
also have this effect). The following is an example of a Three Way Merge
in Word.

 ![image:osee_merge_3.gif](/docs/images/osee_merge_3.gif "image:osee_merge_3.gif") 

A Three Way Merge

The changes made by the Source Branch and Destination Branch are shown
in different colors in the Word Document. In this particular case the
changes made in Red were done by the Source Branch and the changes made
in Blue were done on the Destination Branch. The color scheme is not
consistent and the user needs to verify which color equates to which
changes by hovering there mouse over one of the changes. A popup will be
shown which will identify the author. The following Guide will explain
how to resolve the changes in the document. IMPORTANT: All changes must
be either accepted or rejected before the conflict can be marked as
resolved. After the user has resolved all the changes it is a good idea
to do generate a difference document between the Source Artifact and the
Merge Artifact, and the Destination Artifact and the Merge Artifact by
selecting "Show Source/Merge Diff" and "Show Destination/Merge Diff"
from the merge Wizard or "Differences"-\>"Show Source/Merge Differences"
and "Differences"-\>"Show Destination/Merge Differences" from the right
click menu. These views will show the differences between the branch
artifact and the merge artifact. For the Source/Merge difference this
will show everything that is different between the source document and
the Merge document. In the case where the user accepts all changes from
the source and destination branches this diff will highlight all of the
changes that occurred on the destination branch. In the
Destination/Merge diff it will highlight all of the changes that
happened on the source branch. It is always possible to use Manual
Merging techniques in conjunction with Three Way Merging.

The user then right clicks on the in the Conflict Status Column so that
the icon is displayed. The conflict is resolved and will allow the
Source Branch to be committed.


Word Formatted Content Merge Wizard

### Additional Features

The Merge Wizard contains a "Clear the Merge Artifact" that is not
available from the right click menu and only available for Word
Formatted Content. This will empty out the Merge artifact and allow the
user to start with an empty document for editing. It will also place a
icon in the merge value column for that conflict.