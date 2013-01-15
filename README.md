Snapshere Khan
==============

Are you tired of the renaming orgy every time your project changes the version? Try 'Snapshere Khan', 
an Eclipse plugin/command line tool for easy and fast version administration (snapshot/release) of Maven artifacts. 
Incrementing the version in all your dependent projects is now a matter of a single click.

<a href='https://raw.github.com/wiki/inventage/snapshere-khan/update_version_dialog.png'>
  <img src='https://raw.github.com/wiki/inventage/snapshere-khan/update_version_dialog.png' width='480px'/>
</a>

Features
-

* Updates version information in all known project files and their dependencies
* Supports Maven (>= v2) and Tycho projects (eclipse-plugin, eclipse-test-plugin, eclipse-feature, eclipse-repository, eclipse-update-site, eclipse-application)
* Comprehensive UI for version administration
* Command line interface with scripting engine
* Own breed of script files for automated processing of large number of changes

Quick Start
-

### 1. Install the plugin in Eclipse
To do so, open the "Help -> Install new software..." menu and enter our release URL as software site:

    https://raw.github.com/inventage/snapshere-khan-repos/master/releases/

Then, check the plugin and install it.

### 2. Version your projects
Start the versioning dialog by right clicking on a Maven/Tycho project and choosing the "Update version..." item.

<a href='https://raw.github.com/wiki/inventage/snapshere-khan/update_version_context_menu_entry.png'>
  <img src='https://raw.github.com/wiki/inventage/snapshere-khan/update_version_context_menu_entry.png' width='480px'/>
</a>

It brings up the dialog which lets you select the desired new version in the chosen projects.


Documentation
-

For exhaustive documentation about the project -- be it for the user or developer -- visit the github wiki page: https://github.com/inventage/snapshere-khan/wiki.
