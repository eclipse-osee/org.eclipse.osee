# File Validation

The CI pipeline includes a file validation script that checks for keywords (defined in GitLab environment variables) within a given list of files. This is run to ensure certain words/terms do not make it into the OSEE git repository.

## Running locally

In order to run locally, you must have Python 3 installed. Please refer to your organization's instructions on how to install Python on your machine.

Navigate to the root of this repository and run the following:

```
python validateFiles.py <keywords> <files>
```

- keywords is a semicolon-delimited list of keywords to search for in the given files.
- files is a list of files, with paths relative to the current location.

Example:

```
python validateFiles.py "keyword1;keyword2;keyword3" file1 file2 file3
```
