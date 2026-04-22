#!/usr/bin/env python3
##############################################################################
# Copyright (c) 2025 Boeing
#
# This program and the accompanying materials are made
# available under the terms of the Eclipse Public License 2.0
# which is available at https://www.eclipse.org/legal/epl-2.0/
#
# SPDX-License-Identifier: EPL-2.0
#
# Contributors:
#     Boeing - initial API and implementation
##############################################################################
"""Parse SpotBugs XML report and produce a Markdown summary.

Usage:
    spotbugs-to-markdown.py <spotbugs-report.xml> [changed-files.txt] > comment.md

If changed-files.txt is provided, the output splits bugs into two sections:
  1. Bugs in files changed by the PR (expanded)
  2. All other bugs (collapsed)
"""

import re
import sys

PRIORITY_LABELS = {
    "1": ":red_circle: High",
    "2": ":orange_circle: Medium",
    "3": ":yellow_circle: Low",
}

TABLE_HEADER = (
    "| # | Category | Priority | Bug Type | Class | Message |\n"
    "|---|----------|----------|----------|-------|---------|"
)


def parse_changed_files(path):
    """Build lookup sets from the changed-files list.

    Returns (source_paths, class_names) where:
      - source_paths contains entries like 'org/eclipse/osee/foo/Bar.java'
      - class_names contains entries like 'org.eclipse.osee.foo.Bar'
    """
    source_paths = set()
    class_names = set()

    try:
        with open(path) as f:
            for line in f:
                fpath = line.strip()
                if not fpath.endswith(".java"):
                    continue

                # Extract portion after /src/ or /src-gen/
                for marker in ("/src/", "/src-gen/"):
                    idx = fpath.find(marker)
                    if idx != -1:
                        src_rel = fpath[idx + len(marker) :]
                        source_paths.add(src_rel)
                        dotted = src_rel.replace("/", ".").removesuffix(".java")
                        class_names.add(dotted)
                        break
    except FileNotFoundError:
        pass

    return source_paths, class_names


def _attr(text, name):
    """Extract an XML attribute value by name from a text block."""
    m = re.search(rf'{name}="([^"]*)"', text)
    return m.group(1) if m else ""


def _tag_text(text, tag):
    """Extract the text content of the first occurrence of <tag>...</tag>."""
    m = re.search(rf"<{tag}>([^<]*)</{tag}>", text)
    return m.group(1) if m else ""


def parse_bugs(xml_path):
    """Parse SpotBugs XML and return a list of bug dicts."""
    with open(xml_path) as f:
        content = f.read()

    bugs = []
    for m in re.finditer(r"<BugInstance\b.*?</BugInstance>", content, re.DOTALL):
        block = m.group(0)

        # Attributes on the opening <BugInstance ...> tag
        opening = block[: block.index(">") + 1]
        category = _attr(opening, "category")
        priority = _attr(opening, "priority")
        bug_type = _attr(opening, "type")

        # classname from the first element that has one (Class or SourceLine)
        classname = _attr(block, "classname")

        # sourcepath from the first SourceLine element
        sl_match = re.search(r"<SourceLine\b[^>]*>", block)
        sourcepath = _attr(sl_match.group(0), "sourcepath") if sl_match else ""

        # ShortMessage text
        message = _tag_text(block, "ShortMessage")

        bugs.append(
            {
                "category": category,
                "priority": priority,
                "type": bug_type,
                "classname": classname,
                "sourcepath": sourcepath,
                "message": message.replace("|", "\\|"),
            }
        )

    return bugs


def bug_in_changed_files(bug, source_paths, class_names):
    """Check whether a bug belongs to a file changed in the PR."""
    if bug["sourcepath"] and bug["sourcepath"] in source_paths:
        return True
    if bug["classname"] and bug["classname"] in class_names:
        return True
    # Also match inner classes: org.eclipse.osee.Foo$Inner -> org.eclipse.osee.Foo
    if "$" in bug["classname"]:
        outer = bug["classname"].split("$")[0]
        if outer in class_names:
            return True
    return False


def format_row(index, bug):
    p = PRIORITY_LABELS.get(bug["priority"], bug["priority"])
    return f'| {index} | {bug["category"]} | {p} | {bug["type"]} | `{bug["classname"]}` | {bug["message"]} |'


def format_table(bugs):
    lines = [TABLE_HEADER]
    for i, bug in enumerate(bugs, 1):
        lines.append(format_row(i, bug))
    return "\n".join(lines)


def main():
    if len(sys.argv) < 2:
        print(__doc__, file=sys.stderr)
        sys.exit(1)

    xml_path = sys.argv[1]
    changed_files_path = sys.argv[2] if len(sys.argv) > 2 else None

    try:
        bugs = parse_bugs(xml_path)
    except FileNotFoundError as e:
        print("## :x: SpotBugs Analysis")
        print(f"Failed to parse SpotBugs report: {e}")
        sys.exit(0)

    if not bugs:
        print("## :white_check_mark: SpotBugs Analysis — No Bugs Found")
        print()
        print("SpotBugs analyzed the `org.eclipse.osee` packages and found no issues.")
        sys.exit(0)

    # Split into changed-file bugs and others
    changed_bugs = []
    other_bugs = []

    if changed_files_path:
        source_paths, class_names = parse_changed_files(changed_files_path)
        for bug in bugs:
            if bug_in_changed_files(bug, source_paths, class_names):
                changed_bugs.append(bug)
            else:
                other_bugs.append(bug)
    else:
        other_bugs = bugs

    total = len(bugs)
    print(f"## :mag: SpotBugs Analysis — {total} Bug(s) Found")
    print()

    # Section 1: Bugs in changed files (expanded)
    if changed_files_path:
        if changed_bugs:
            print(f"### :pushpin: Bugs in Changed Files — {len(changed_bugs)} issue(s)")
            print()
            print(format_table(changed_bugs))
            print()
        else:
            print("### :pushpin: Bugs in Changed Files")
            print()
            print(":white_check_mark: No SpotBugs issues found in files changed by this PR.")
            print()

    # Section 2: All other bugs (collapsed when there's a changed-files section)
    if other_bugs:
        if changed_files_path:
            print(f"### :file_folder: All Other Bugs — {len(other_bugs)} issue(s)")
            print()
            print("<details>")
            print("<summary>Click to expand</summary>")
            print()
            print(format_table(other_bugs))
            print()
            print("</details>")
        else:
            print(format_table(other_bugs))
    print()
    print("> **Priority Legend:** :red_circle: High &nbsp; :orange_circle: Medium &nbsp; :yellow_circle: Low")


if __name__ == "__main__":
    main()
