to use the depends.target
1. run: mvn clean verify in org.eclipse.osee.dep.parent
2. setup string substitution parameters:
	Window->pref->filter text (string sub)
	target_install=<path to eclipse install you want to build against, i.e. mars M6>
	osee_dep=<path to the built p2 site, C:\path to repo\plugins\org.eclipse.osee.dep.p2\target\repository\plugins>
3. open depends target -> set as target platform