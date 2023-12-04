The [OSEE](http://www.eclipse.org/osee) Multi-Project Design

## Configuration of ATS for Multi-Project committing...

Branch Configuration Attributes:
**ats.Allow Branch Create** - activates ability to create working
branches from parent
**ats.Allow Branch Commit** - activates ability to commit working
branches to parent
**ats.Parent Branch Id** - set to the branch id of the branch to
create/commit
If ATS - Team Definition attribute
"ats.Team Using Versions" == false, then ATS uses branch configuration
attributes from Team Definition.

Else If ATS - Team Definition
attribute "ats.Team Using Versions" == true, then ATS uses branch
configuration attributes from targeted ATS - Version
Artifact. This means that the ATS -
Team Workflow must be targeted to a
version before branching can occur