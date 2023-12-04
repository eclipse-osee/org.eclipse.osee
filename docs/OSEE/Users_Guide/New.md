__NOTOC__

# Planned Capabilities for OSEE Release 0.8.2

This release of OSEE concentrated on major bug fixes. However, an effort
to improve our ability to support our customer base had also been
initiated. This effort required selected customers to reevalute the
actions they have originated and determine if these actions can be
retracted (cancelled), particularly if they fall into any of the
following categories:

1.  *Overcome by events/no longer valid.*
2.  *Not backed up by a requirement (e.g. development of an
    emulator/model).*
3.  *Of relatively low importance or priority.*
4.  *Limited applicability to the user community.*
5.  *Duplicates.*

Thanks to all who participated and helped us bring down the number of
active actions so we can reasonably assess their relative importance and
subsequently, get them into a release sometime in the future.

**Results:** Total Actions: 529; Total Cancelled: 324; Total
Completed:188; Pending: 17

# Highlighted Changes

## ATS - Action Tracking System

Highlight of ATS bug fixes:

  - Corrected Task in task search window not saving after refresh
  - Fixed issue where the zoom out icon is identical to the zoom in icon
  - Created ability to subscribe for emails when action is written
    against an Actionable Item or Team Definition
  - Fixed Promoted Date not being updated issue
  - Added trailing "/" in search for view comparison view
  - Allow way to reset an attribute to its default value
  - Fixed issue where favorite branches are not saved when OSEE is
    restarted

## Define

  - Merge Manager Changes
      - Issues when merging Native Artifacts have been resolved.
      - A bug in the GUI not being able to display a conflict where an
        attribute was deleted and modified.
      - Side by side comparison for merging string attributes other than
        Word content
      - A bug that introduced false positives conflicts has been
        identified and resolved.
  - Improved Revision History View with filtering.
  - Native artifacts now have a preview menu option.
  - Resolved Remote Event Service client synchronization issues
  - Fixed tracked change errors causing infinite loop on saving

## Help System

  - The OSEE Online Help is now dynamically generated from the [Wiki
    page content](http://wiki.eclipse.org/OSEE) as part of the build
    process.

