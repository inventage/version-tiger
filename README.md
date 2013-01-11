snapshere-khan
==============

An Eclipse plugin/command line tool for easy and fast version administration (snapshot/release) of Maven artifacts.

[screenshot]

Features
-

Supported project types and features:
* Maven (>= v2) project support: artifact version, parent version, referenced version in dependencies, dependency management, properties
* Tycho project support:
  * eclipse-plugin: Bundle-Version, referenced version in Require-Bundle and Fragment-Host
  * eclipse-test-plugin: Bundle-Version
  * eclipse-feature: feature version, referenced versions in plugins and includes
  * eclipse-repository: all product files in project root, product version, referenced versions in product features and category file
  * eclipse-update-site: referenced versions (feature version and URL)
  * eclipse-application: product version, referenced versions in workspace product features
* Global universe definition in XML files (to be set up in workspace preferences)
  * XML validation
  * Workspace variable support ('project_loc', 'workspace_loc')
* Comprehensive UI for version administration
* Command line interface with scripting engine
* Own breed of script files for automated processing of large number of changes
 
Users manual
-
-> See respective wiki page (soon)


Developers manual
-
-> See respective wiki page (soon)
