# StackOffline Data Reader
StackOffline Data Reader takes your downloaded StackOverflow data and 
shows it in a convinient way.
It takes `*.zip` files with `stack/data/*.json` files in it.

### How does it work?
Data reader takes your downloaded data and creates fast to use
metadata file which contain all question titles and it's ids.
That way, **Apache Lucene** can index and query all titles and return id path to them.

### How does it look?
![](https://i.imgur.com/AOIfpeV.png)

![](https://i.imgur.com/dEUZ8CG.png)

### Installation
It's very simple. Just download the `stack-reader-1.0-SNAPSHOT.jar` file from
the Releases tab. 
Then just download complete `.bat` or `.sh` executables, OR make your own.

You need a `.zip` file with all Stackoverflow data.
You can either make one yourself using `stackoffline-downloader`, or download one.

[Click here to download: Java tag, approx. 700.000 questions, 1.2GB](http://www.mediafire.com/file/msrp97mhlm7l97q/data.zip/file)

### Configuration
StackOffline Data Reader takes only 1 argument, which is your path to `.zip` data file.
```batch
@echo off
title StackOffline Data Reader
java -jar stack-reader-1.0-SNAPSHOT.jar data.zip
pause
```
##### This is for Windows with batch file support only, search for Linux equivalent in Releases tab 
where `data.zip` is the path/file name.

#### Disclaimer
It's very important to use console emulator which supports ANSI coloring.
Currently tested console emulators working out of the box:
* Windows Command Prompt
* Ubuntu Shell
* Git bash
* cmder

### License
Project is fully open-source and licensed under MIT License.