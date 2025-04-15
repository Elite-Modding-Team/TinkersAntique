# [Tinkers' Antique](https://www.curseforge.com/minecraft/mc-mods/tinkers-antique)

![Tinkers' Antique Banner](https://raw.githubusercontent.com/Elite-Modding-Team/TinkersAntique/refs/heads/1.12/banner.png)

Modify all the things, then do it again!  
Melt down any metals you find.  
Power the world with spinning wind!

### IMC

Tinkers' Antique supports several IMCs to allow mods to integrate themselves.
The [Wiki](https://github.com/SlimeKnights/TinkersConstruct/wiki/IMC) contains a page with further information.
Anything that is not possible via IMC has to be integrated via Code through the API/library package.

(old 1.7.10 IMCs can be found here: https://gist.github.com/bonii-xx/e46f9d9e81e29d796b1b)

## Setting up a Workspace/Compiling from Source

Note: Git MUST be installed and in the system path to use our scripts.

* Setup: Run [gradle]in the repository root: `gradlew[.bat] [setupDevWorkspace|setupDecompWorkspace] [eclipse|idea]`
* Build: Run [gradle]in the repository root: `gradlew[.bat] build`
* If obscure Gradle issues are found try running `gradlew clean` and `gradlew cleanCache`

## Issue reporting

Please include the following:

* Tinkers' Antique version
* Forge/Cleanroom version/build
* Versions of any mods potentially related to the issue
* Any relevant screenshots are greatly appreciated.
* For crashes:
    * Steps to reproduce
    * latest.log (the FML log) from the logs folder of the client

## Licenses

Code, textures and binaries of the original mod (Tinkers' Construct) are licensed under
the [MIT License](https://tldrlegal.com/license/mit-license).
Code, textures and binaries of new changes in this fork (Tinkers' Antique) are licensed
under the [LGPLv3 License](https://www.tldrlegal.com/license/gnu-lesser-general-public-license-v3-lgpl-3).

You are allowed to use the mod in your modpack.
Any modpack which uses Tinkers' Antique takes **full** responsibility for user support queries. For anyone else, we
only support official builds from the main CI server, not custom-built jars. We also do not take bug reports for
other builds of Minecraft than 1.12.2.

Any alternate licenses are noted where appropriate.

## Jar Signing

Some jars from our build servers may be signed. Under no circumstances does anyone have permission to verify the
signatures on those jars from other mods. The signing is for informational purposes only.
