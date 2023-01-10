# File Validation

The CI pipeline includes a file validation script that checks for keywords (defined in GitLab environment variables) within a given list of files. This is run to ensure certain words/terms do not make it into the OSEE git repository.

## Running locally

In order to run locally, you must have Python 3 installed. Please refer to your organization's instructions on how to install Python on your machine.

The scripts are located at `plugins/org.eclipse.osee.support.dev/validationScripts/`.

Navigate to the root of this repository and run the following:

```
python plugins/org.eclipse.osee.support.dev/validationScripts/validateFiles.py <keywords> <files>
```

- keywords is a semicolon-delimited list of keywords to search for in the given files.
- files is a list of files, with paths relative to the current location.

Example:

```
python plugins/org.eclipse.osee.support.dev/validationScripts/validateFiles.py "keyword1;keyword2;keyword3" file1 file2 file3
```

Alternatively, you can navigate to the script location and run from there.

### Validate Entire Repository

There is a script called `validateAllFiles.py` in the validationScripts folder that will check every file in the repository for keywords in the file names, file contents, and will look for missing distribution statements.

```
python plugins/org.eclipse.osee.support.dev/validationScripts/validateAllFiles.py <keywords>
```

### Add Missing Distribution Statements

If there are a lot of missing distribution statements in the repository, they can be added by running `addDistStatements.py`.

```
python plugins/org.eclipse.osee.support.dev/validationScripts/addDistStatements.py
```

There is a file in the validationScripts folder called `no_dist_statement.txt` that contains a list of file names that should not contain distribution statements. If you have a file that is being caught by the validation script that does not need a dist statement, add it to that file. Adding the full file path will catch that one specific file. Adding just the file name will catch any file in the repo with that name.
