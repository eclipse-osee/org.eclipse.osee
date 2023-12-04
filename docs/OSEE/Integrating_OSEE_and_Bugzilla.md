__NOTOC__

# Installing Mylyn

  - In OSEE, Select **Help \> Install New Software...**
  - In the **Work with:** field, enter:
    <http://download.eclipse.org/tools/mylyn/update/e3.4>
  - Select:
      - **Mylyn Features**
      - **Mylyn Integration**.
  - Click **Next** and proceed with the installation.
  - Restart and Show views "Task List" and "Task Repositories".

# Configuration

  - In the **Task Repositories** view, right click **Eclipse.org** and
    select **New Query**.
      - Select **Create query using form**.
      - Fill in the query values:
          - **Query Title**: OSEE Bugzilla
          - **Product**: OSEE
      - Click **Finish**.

# Submitting a bug

  - Change to the **Task List** view.
  - Right click **OSEE Bugzilla \[Eclipse.org\]** and select **New \>
    Task...**.
      - Select the **Eclipse.org** repository and click **Finish**.
  - Fill out the form describing the bug.
  - Click **Submit**.

# Submitting a Patch

  - Open the bug from the **Task View** if it is not already open.
  - Under **Attachments**, attach the patch file.
      - Include a brief description as necessary and ensure that the
        **Patch** box is checked.
  - Enter a **New Comment** and select an appropriate Action (most
    likely **Resolve as FIXED**).
  - Click **Submit**.

# Applying a Patch

  - Open the bug associated with the patch.
  - In the bug view, open the **Attachments** tab.
      - Right click the patch and select **Apply Patch...**
  - This will bring up the **Review Patch** view.
      - Changes to individual files can be reviewed by double clicking
        that file.
      - When done, click **Finish**.

