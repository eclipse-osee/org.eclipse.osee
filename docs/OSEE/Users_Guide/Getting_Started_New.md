__NOTOC__

## Installation and Initialization

### Requirements

  - System with at least 4GB of RAM
  - (what else do we need?)

### OSEE Installation

  - Download and install the latest JDK
      - Website for download: [**Eclipse
        Temurin**](https://projects.eclipse.org/projects/adoptium.temurin/downloads)

<!-- end list -->

  - Download and install git
      - Website for download: [**Git - Downloading
        Package**](https://git-scm.com/download/win)
      - **PLACE SCREEN CAP HERE...**
      - Adjusting your PATH environment
          - Use Git and optional Unix tools from the Command Prompt
      - Choosing HTTPS transport backend
          - Use the OpenSSL library
      - Configuring the line ending conversions
          - Checkout as-is, commit as-is
      - Configuring the terminal emulator to use with Git Bash
          - Use MinTTY
      - **Leave the rest of the settings as default**

<!-- end list -->

  - Set up the proxy for git
      - If you haven't registered with eclipse, register and log in to
        [**eclipse.org**](https://www.eclipse.org/)
      - Clone the repository
          - $ cd /c/Code/
          - $ mkdir git_main
          - $ cd git_main
          - $ git clone
            https://\<username\>@git.eclipse.org/r/osee/org.eclipse.osee.git
            --branch dev --depth 1
              - **Note:** Replace <username> with your eclipse username

<!-- end list -->

  - Download and install the org.eclipse OSEE from the OSEE site
      - Website for download: [**Eclipse
        Downloads**](https://download.eclipse.org/technology/osee/downloads)
          - Choose the last directory with a version with a date on it
            (that will match the current source code the best) **REWORD
            THIS\!\!\!\!\!**
          - **PLACE SCREEN CAP HERE...**

<!-- end list -->

  - Make an Eclipse folder
      - For example: /c/Code/Eclipse

<!-- end list -->

  - Extract all of the files from the eclipse download into your Eclipse
    folder

<!-- end list -->

  - When you run eclipse, it will ask to make a workspace. Make a
    workspaces folder.
      - For example: /c/Code/Eclipse/workspaces
      - **Tip:** Placing all workspaces within a single folder makes it
        easier to locate each workspace.
  - Make a folder to use for your first workspace.
      - For example: /c/Code/Eclipse/workspaces/first_git_main

<!-- end list -->

  - Run the eclipse instance by changing to that directory and double
    clicking osee.exe