
# File Validation

The CI pipeline includes a file validation script that checks for keywords (defined in GitLab environment variables) within a given list of files. This is run to ensure certain words/terms do not make it into the OSEE git repository.

## Running locally

In order to run locally, you must have Python 3 installed. Please refer to your organization's instructions on how to install Python on your machine.

The scripts are located at `tools/validation/`.

Navigate to the root of this repository and run the following:

```
python tools/validation/validateFiles.py {keywords} {files}
```

- keywords is a semicolon-delimited list of keywords to search for in the given files.
- files is a list of files, with paths relative to the current location.

Example:

### Setup:
To run the validateFiles script locally, you will need to set up a couple of variables to determine what keywords you want to exclude as well as what files to look through. The validateFiles script expects two arguments, the first being the excluded keywords and the second being the list of files to check. 

### EXCLUDED_KEYWORDS:
- Just make a text file with the list of keywords you are wanting to exclude
- Text file should just include the list of keywords seperated by semicolons
- Example Txt Contents: "keyword1;keyword2;keyword3"
- Example bash command: ``` $EXCLUDED_KEYWORDS=$(cat EXCLUDED_KEYWORDS.txt) ```

### FILES:
- Determine what files you want to check for the list of keywords and make sure they are staged
- Example bash command: ``` $FILES=$(git diff --name-only --staged) ```
- Examples of other possible $FILES:
  - For files changed between a given SHA and your current commit:
```git diff --name-only <starting SHA> HEAD ```
  - If you want to compare the last two commits:
```git diff --name-only HEAD HEAD~1 ```
  - If you want to include changed-but-not-yet-committed files:
```git diff --name-only <starting SHA> ```
  - If you want only to compare staged to recent commit:
```git diff --name-only --staged ```

### Running the validateFiles Script:
- Once you have your variables set up, you can run the script as below
- ``` $py validateFiles.py $EXCLUDED_KEYWORDS $FILES ```

### Validate Entire Repository

There is a script called `validateAllFiles.py` in the validation folder that will check every file in the repository for keywords in the file names, file contents, and will look for missing distribution statements.

```
python tools/validation/validateAllFiles.py {keywords}
```

### Add Missing Distribution Statements

If there are a lot of missing distribution statements in the repository, they can be added by running `addDistStatements.py`.

```
python tools/validation/addDistStatements.py
```

There is a file in the validation folder called `no_dist_statement.txt` that contains a list of file names that should not contain distribution statements. If you have a file that is being caught by the validation script that does not need a dist statement, add it to that file. Adding the full file path will catch that one specific file. Adding just the file name will catch any file in the repo with that name.
