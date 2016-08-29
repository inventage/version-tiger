Version Tiger
==============

Are you tired of the renaming orgy every time your project changes the version? Try 'Version Tiger', 
an Eclipse/Maven plugin and command line tool for easy and fast version administration (snapshot/release) of Maven artifacts.

Features
-

* Updates version information in all known project files and their dependencies
* Supports Maven (>= v2), Tycho projects (eclipse-plugin, eclipse-test-plugin, eclipse-feature, eclipse-repository, eclipse-update-site, eclipse-application) and Apache Karaf features
* Comprehensive UI for version administration
* Command line interface with scripting engine
* Own breed of script files for automated processing of large number of changes
* Maven plugin for executing batch files

Quick Start
-

### How to install and use the command line tool

You can download the CLI tool and run it to get a shell or run batch files:

> https://github.com/inventage/version-tiger-repos/raw/master/cli/com.inventage.tools.versiontiger-cli.jar

If you just like to execute a batch script, you may use the maven plugin:

    mvn com.inventage.tools.versiontiger:versiontiger-maven-plugin:execute -DstatementsFile=myVersionings.versiontiger

See [Batch File Usage](https://github.com/inventage/version-tiger/wiki/Batch+File+Usage) to see all available commands.

### How to install and use the plugin in Eclipse

Search for [Version Tiger](https://marketplace.eclipse.org/node/637830) in your Eclipse Marketplace client
and click install.

Alternatively, you may install with "Help -> Install new software..." menu and enter our release URL as software site:

> https://raw.github.com/inventage/version-tiger-repos/master/releases/

Start the versioning dialog by right clicking on a Maven/Tycho project and choosing the "Update version..." item.

<a href='https://raw.github.com/wiki/inventage/version-tiger/update_version_context_menu_entry.png'>
  <img src='https://raw.github.com/wiki/inventage/version-tiger/update_version_context_menu_entry.png' width='480px'/>
</a>

It brings up the dialog which lets you select the desired new version in the chosen projects.

<a href='https://raw.github.com/wiki/inventage/version-tiger/update_version_dialog.png'>
  <img src='https://raw.github.com/wiki/inventage/version-tiger/update_version_dialog.png' width='480px'/>
</a>

Documentation
-

For exhaustive documentation about the project -- be it for the user or developer -- visit the [Version Tiger wiki page](https://github.com/inventage/version-tiger/wiki).

See [Release Notes](https://github.com/inventage/version-tiger/releases) for new and noteworthy features and bugfixes in latest releases.
