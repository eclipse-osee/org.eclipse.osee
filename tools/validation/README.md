# Validating Files

To run the validateFiles script locally, you will need to set up a couple of variables to determine what keywords you want to exclude as well as what classes to look through. The validateFiles script expects two arguments, the first being the excluded keywords and the second being the list of files to check. 

### EXCLUDED_KEYWORDS:
- Just make a text file with the list of keywords you are wanting to exclude 
- Example bash command: ``` $EXCLUDED_KEYWORDS=$(cat EXCLUDED_KEYWORDS.txt) ```
- Text file should just include the list of keywords seperated by semicolons
- Example Txt Contents: "keyword1;keyword2;keyword3"

### FILES:
- Determine what files you want to check for the list of keywords and make sure they are staged
- Example bash command: ``` $FILES=$(git diff --name-only --staged) ```
- Examples of other possible $FILES:
-For files changed between a given SHA and your current commit:
``` git diff --name-only <starting SHA> HEAD ```
-If you want to compare the last two commits:
``` git diff --name-only HEAD HEAD~1 ```
-If you want to include changed-but-not-yet-committed files:
``` git diff --name-only <starting SHA> ```
-If you want only to compare staged to recent commit:
``` git diff --name-only --staged ```

### Running the validateFiles Script:
- Once you have your variables set up, you can run the script as below
- ``` $py validateFiles.py $EXCLUDED_KEYWORDS $FILES ```
