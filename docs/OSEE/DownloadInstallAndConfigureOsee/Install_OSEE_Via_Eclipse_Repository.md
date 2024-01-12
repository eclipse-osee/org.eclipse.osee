1.  Download the latest version of Eclipse from the [Current Eclipse
    Release](http://www.eclipse.org/downloads/) page.
2.  [Follow this link to the OSEE Downloads
    page](http://www.eclipse.org/osee/downloads/).
3.  Click on the OSEE Incubation Update Site link.
![Image:OseeScreenShotClientIncubationUpdateSiteLink.png](/docs/images/OseeScreenShotClientIncubationUpdateSiteLink.png
    "Image:OseeScreenShotClientIncubationUpdateSiteLink.png")
4.  Click on any one of the Eclipse mirror site links.
![Image:OseeScreenShotMirrorDownloadLink.png](/docs/images/OseeScreenShotMirrorDownloadLink.png
    "Image:OseeScreenShotMirrorDownloadLink.png")
5.  Choose 'Save File' and click OK.
![Image:OseeScreenShotSaveAsDialog.png](/docs/images/OseeScreenShotSaveAsDialog.png
    "Image:OseeScreenShotSaveAsDialog.png")
6.  Wait for download to complete.
7.  Start Eclipse and select the menu item **Help \> Install New
    Software...**
8.  Click the *Add...* button.
    ![add_site.png](/docs/images/add_site.png "add_site.png")
![Image:New_update_site.png](/docs/images/New_update_site.png
    "Image:New_update_site.png")
9.  On the *Add Site* dialog copy the URL for the *OSEE Client
    Incubation Update Site* from
    [downloads](http://www.eclipse.org/osee/downloads/) page. **Please
    note that the use of the software you are about to access may be
    subject to third party terms and conditions and you are responsible
    for abiding by such terms and conditions.**
10. Click on the *OK* button to store update site information.
11. Select the OSEE update site entry and all features listed under its
    category. Click the *Install* button.
![Image:Install_updates.png](/docs/images/Install_updates.png
    "Image:Install_updates.png")
12. The update manager calculates dependencies and offers you a list of
    features to install. Select the needed ones and click the *Next*
    button.![image:install.png](/docs/images/install.png "image:install.png")
13. Accept terms of license agreement and click the *Finish* button in
    order to start the download of selected
    features.![Image:Install_license.png](Install_license.png
    "Image:Install_license.png")
14. To apply installation changes click on the *No* button and shutdown
    Eclipse. It is important that you don't restart Eclipse until you
    have completed the database initialization steps below.

![image:restart_dialog.png](/docs/images/restart_dialog.png "image:restart_dialog.png")

1.  Before you can use OSEE you will need to install a relational
    database. Follow the instructions at [Supported
    Databases](#Supported_Databases "wikilink") to complete this step.
2.  Initialize the database with default OSEE data. See [Database
    Initialization](#Database_Initialization "wikilink")
3.  Setup config.ini and launch eclipse to start using OSEE [Launch and
    Configuration](#Launch_.26_Configuration "wikilink")
4.  You can find different OSEE perspectives, such as Define and ATS,
    and views in correspondent dialogs, activated by menu items *Window
    \> Open Perspective \> Other...* and *Window \> Show View \>
    Other...*.

<table border="0" cellpadding="5" cellspacing="0">

<tr>

<td valign="top">

![image:open_perspective.png](/docs/images/open_perspective.png "image:open_perspective.png")

</td>

<td valign="top">

![image:show_view.png](/docs/images/show_view.png "image:show_view.png")

</td>

</tr>

</table>