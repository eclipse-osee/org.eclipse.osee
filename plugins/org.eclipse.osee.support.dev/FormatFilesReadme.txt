- Reformat and organize inputs on codebase 
   - get jdt.core.prefs: select ats.ide bundle > right-click > properties >
   java code style > formatter > enable bundle proper; this creates .settings
   with prefs in it
   - run this command: -data <workspace> can be anything and deleted after run
/C/Tools/OSEE\ AMS\ Nightly/eclipse -nosplash -data workspace_format -consolelog -debug -application org.eclipse.jdt.core.JavaCodeFormatter -verbose -config /C/Tools/org.eclipse.jdt.core.prefs /C/Tools/git_zzz/org.eclipse.osee/plugins
   - delete created ats/ide/.settings and <workspace> before checkin
   - ??? - don't understand what it's doing to method newline; commit shows
   changed, but edit file and didn't?? eg: AtsXDateValidator, commit shows it
   broke line at StateManager, but when edit in eclipse, doesn't
