The target definition file in here has a variable {OSEE_REPO} in it that gets replaced during the build.
This file is checked in but changes are not tracked via:

git update-index --assume-unchanged org.eclipse.osee.dep.target.target

to turn on change tracking again, use:


git update-index --no-assume-unchanged org.eclipse.osee.dep.target.target
