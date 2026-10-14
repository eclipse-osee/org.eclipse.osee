XWidget -> XXWidget Migration Review Packet - Generators
========================================================

TEMPORARY / THROWAWAY. These classes exist only to generate the review packet for the
one-time XWidget -> XXWidget migration (commit feature[TW29349]). Delete this entire
package (org.eclipse.osee.ats.ide.integration.tests.coderev.packet) once that change has
been reviewed and merged. Nothing in the product depends on it.

What they produce
-----------------
Running the tooling writes a self-contained review packet into ~/CodeXWidgetReview/
(created on first use with its own README). All links inside the packet are relative, so
the whole folder can be zipped and opened anywhere - no server, no external libraries, no
network. The packet contains:
  - XWidget_Overview.html .............. the entry page; links everything below
  - XWidgets_XWidgetReview_Rename/Diff/Testing.html
  - XWidgets_BlamReview_Renamed/Diff/Testing.html
  - XWidgets_WorkDefReview_Rename/Diff/Testing.html
  - *_classes_ggg.json / *_classes_try.json (interim scan data)
  - old_* / new_* subfolders of highlighted source copies the diffs link to

How to run
----------
Right-click GenerateFullReviewPacket.java -> Run As -> Java Application.
That is the one entry point: it runs every generator in the right order and then verifies
the output (all reports present, non-empty, well-formed) and prints a PASS/FAIL summary.

It scans two workspace roots side by side:
  - C:/Tools/git_ggg  (new / "ggg")
  - C:/Tools/git_try  (old / "try")
The git_try checkout of org.eclipse.osee must be present, or the "old" side of each report
is skipped (the run still completes and the verifier reports a warning).

You can also run any single generator standalone the same way:
  - ReportXWidgetChanges.java   - XWidget JSON + source copies + Rename/Diff/Testing reports
  - ReportBlamChanges.java      - BLAM JSON + source copies + Renamed/Diff/Testing reports
  - ReportWorkDefChange.java    - Work Definition JSON + source copies + Rename/Diff/Testing
  - ReportXWidgetOverview.java  - the XWidget_Overview.html entry page (run after the others)
  - ReportXWidgetMapping.java   - the curated old->new widget/class name map used by the above

Note
----
CodeQualityUtil (output-folder + README machinery) stays in the parent package
org.eclipse.osee.ats.ide.integration.tests.coderev - it is shared with the durable
CodeReview/CodeClean tooling and must NOT be deleted with this package.

The durable old->new mapping reference lives in
docs/ai/ide/ide-xwidget-to-xxwidget-migration.md, which is the source of truth and should
be kept after this tooling is removed.
