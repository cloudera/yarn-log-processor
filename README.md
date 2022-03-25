# Yarn Log Processor

## Prerequisite

1. Java 8 must be installed  
2. ssh must be installed and sshd must be running  

## Summary

Yarn Log Processor (YALP) is a CLI tool for developers, helping them to effectively analyze YARN logs.  
Analyzing the data helps to find the reason for application or test failures.  

## Usage

Run the processor with the following 3 steps:  

1. Clone the repository and navigate to its root folder  
2. Build project by `mvn clean package`
3. Run the start script by  
    ```bash
    ./start.sh <parameters>
    ```
4. Execute various subshell commands (see Subshell commands section)  
  
Steps 2. and 3. can be executed with one single command, by building the project and running the start script:
  
    ./start.sh <parameters> --build

## Input options

There are three different ways to provide input for YALP:  

1. Run with direct URL input:  

    ```bash
    ./start.sh  --url <direct url> --logFolder <log folder> [--keep] [--shell] 
    ```
    YALP downloads a zip archive from the given URL address and extracts its content.  
    The archive file and the folder of the extracted files will be named after the current time.  

2. Run with local archive input:  

    ```bash
    ./start.sh  --local <local file path> --logFolder <log folder> [--keep] [--shell] 
    ```
    YALP extracts a zip archive on the provided file path and filters YARN related log files.  
    The folder of the extracted files will be named after the input archive file.  

3. Run on an already extracted log folder: (if no `--url` or `--local` was provided)  
    ```bash
    ./start.sh  --logFolder <log folder> --shell
    ```
   YALP uses the files provided in the defined folder and filters YARN related log files.  
   This makes it possible to only extract and filter the log files once and analyze them multiple times.    
   If we use an already extracted bundle, `--shell` needs to be always provided and `--keep` should not be provided.  
   
   Keep in mind that with input options 1. or 2. we moved the extracted files in a subfolder which needs to be specified here, for example after:  
   ```bash
   ./start.sh  --logFolder someFolder --local fileName
   ```
   
   The logs will be generated in `someFolder/fileName` and for that reason when you want to further examine these logs without extracting them again, you need to specify:  
    ```bash
    ./start.sh  --logFolder someFolder/fileName --shell
    ```
**About the extraction process:**  
    The tool is able to extract zip and gz archive files recursively. Given another archive format, the user is required to convert the input to zip format before using it.  

### Modifier options

1. Build option: needs to be specified once after downloading to build the project  
    ```bash
    ./start.sh <parameters> --build
    ```
   
2. Keep option: needs to be specified for keeping the input archive file (this is an invalid command if the input is an already extracted bundle)  
    ```bash
    ./start.sh <parameters> --keep
    ```

3. Shell option: needs to be specified every time to open the subshell and analyze the log files:  
    ```bash
    ./start.sh <parameters> --shell
    ```
   
4. Command option: executes a command without opening the subshell:  
    ```bash
    ./start.sh <parameters> --command <arg>
    ```
   
where `<arg>`needs to be a subshell command (defined in [Subshell commands](#Subshell-commands) section)

## Configuration file
  
YALP configuration file can be found at `./src/main/resources/config.json`. 
By default, the configuration file contains the following pieces of information:
```
{
   "regularExpressions": {
     "timeStamp": "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}",
     "logFile": ".*(?<role>(RESOURCEMANAGER|NODEMANAGER))-(?<host>.+)\\.log\\.out",
     "configFile": ".*-site\\.xml"
   },
   "directoryNames": {
     "directoryNameForYarnRelatedLogs": "workspace",
     "subdirectoryNameForNodeLogs": "logs",
     "subdirectoryNameForConfigFiles": "configs"
   },
   "cache": {
     "cacheDirectory": "./.blp/cache",
     "cacheType": "InMemoryLRUCache",
     "cacheItemCapacity": "10"
   }
 }  
```
There is a block for the regular expressions, where we can define the timestamp used in the log files, a regular expression to find the YARN related log files and another for the configuration files.  
The second block defines the name of the directories created by the program. In the next section, we can see the structure of these directories.  
The third section defines cache-related variables. Variable `cacheType` can be either "InMemoryLRUCache" or "GeneralCache". InMemoryLRUCache stores the cache items in-memory and deletes the rarely used elements.GeneralCache stores the cache items in the filesystem and does not delete them. `cacheItemCapacity` is only important in the case of in-memory LRU cache, where it defines the maximum number of items stored.`cacheDirectory` is only important in the case of Generalcache, YALP will store the cache items in this folder.   

## Structure of the log folder

```bash
logfolder/  
├── first/  
│   ├── first/  
│   └── workspace/  
│       ├── configs/  
│       └── logs/  
└── second/  
    ├── second/  
    └── workspace/  
        ├── configs/  
        └── logs/  
```

The example above shows the created log folder with the default configuration file after processing `first.zip` and `second.zip`. `first/first` and `second/second` contains all the log files from the archive file. `workspace/logs` folders contain the relevant YARN related logs. YALP also creates a folder for the config files but the config files are not used in the current version.
The name of `workspace`, `configs` and `logs` folders can be changed in the configuration file (`./src/main/resources/config.json`).  


## Subshell commands 

To launch the subshell append the `--shell` parameter to one of the input options described above ([Input options](#Input-options) chapter).  
The following commands and parameters can be executed in the subshell:  

| COMMAND | DESCRIPTION | EXAMPLE |
| --------------- | --------------- | --------------- |
| `help` | Lists all valid commands operating in the subshell and their expected behaviour | `help` |
| `roles` | Lists all YARN roles in the cluster and the corresponding hosts the roles are running on | `roles` |
| `applications` | Lists all applications in the cluster, along with their owners and submission time | `applications` |
| `appattempts <appId>` | Lists all application attempts of a given application | `appattempts application_1583168158408_0002` |
| `containers --application <appId>` | Lists all containers of a given application | `containers --application application_1583168158408_0002` |
| `containers --appattempt <attemptId>` | Lists all containers of a given appattempt | `containers --appattempt appattempt_1583162983042_1961_000001`  |
| `containers --exiting` | Lists all exiting containers | `containers --exiting` |
| `containers --killed` | Lists all killed containers | `containers --killed` |
| `events --application <appId>` | Lists all events of a given application | `events --application application_1583168158408_0002`  |
| `events --appattempt <attemptId>` | Lists all events of a given appattempt | `events --appattempt appattempt_1583168158408_0002_000001`  |
| `states --application <appId>` | Lists all state changes of a given application | `states --application application_1583168158408_0002`  |
| `states --appattempt <attemptId>` | Lists all state changes of a given appattempt | `states --appattempt appattempt_1583168158408_0002_000001`  |
| `states --container <containerId>` | Lists all state changes of a given container | `states --container container_1583167118773_0001_01_000006`  |
| `grep <expression>` | Lists occurrences of a user-defined regular expression in ResourceManager and NodeManager logs | `grep application_1583168158408_0002`  |
| `grep <expression> [-rm/-nm]` | Lists occurrences of a user-defined regular expression only from ResourceManager/NodeManager logs | `grep application_1583168158408_0002 -rm`  |
| `resources` | Lists all nodes and their resource capabilities | `resources` |
| `exceptions` | lists all exceptions in the logs | `exceptions` |
| `info` | Prints generic information about the cluster | `info` |
| `exit` | Terminates the subshell | `exit` |

### Verbosity modifiers

The output of most of the commands can be modified with verbosity modifiers:  

1. List the found items with `--list` modifier:  
    `events --application application_1583169702218_0001 --list`  
```    
EVENT  
START  
APP_ACCEPTED  
ATTEMPT_REGISTERED  
ATTEMPT_UNREGISTERED
```

2. Default verbosity  
    `events --application application_1583169702218_0001`  
```     
| TIME | EVENT |
| --------------- | --------------- |
| 2020-03-02 09:22:19 | START                |
| 2020-03-02 09:22:19 | APP_ACCEPTED         |
| 2020-03-02 09:22:26 | ATTEMPT_REGISTERED   |
| 2020-03-02 09:22:33 | ATTEMPT_UNREGISTERED |
```

3. Verbose output with `--verbose` modifier:  
    `events --application application_1583169702218_0001 --verbose`  
```     
| TIME | EVENT | FROM STATE | TO STATE |
| --------------- | --------------- | --------------- | --------------- |
| 2020-03-02 09:22:19 | START                | NEW        | NEW_SAVING   |
| 2020-03-02 09:22:19 | APP_ACCEPTED         | SUBMITTED  | ACCEPTED     |
| 2020-03-02 09:22:26 | ATTEMPT_REGISTERED   | ACCEPTED   | RUNNING      |
| 2020-03-02 09:22:33 | ATTEMPT_UNREGISTERED | RUNNING    | FINAL_SAVING |
``` 
4. Print whole lines of logs with `--raw` modifier:  
    `events --application application_1583169702218_0001 --raw`  
``` 
MATCHING LINES IN LOGS  
2020-03-02 09:22:19,969 INFO org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppImpl: application_1583169702218_0001 State change from NEW to NEW_SAVING on event = START  
2020-03-02 09:22:19,998 INFO org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppImpl: application_1583169702218_0001 State change from SUBMITTED to ACCEPTED on event = APP_ACCEPTED  
2020-03-02 09:22:26,582 INFO org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppImpl: application_1583169702218_0001 State change from ACCEPTED to RUNNING on event = ATTEMPT_REGISTERED  
2020-03-02 09:22:33,275 INFO org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppImpl: application_1583169702218_0001 State change from RUNNING to FINAL_SAVING on event = ATTEMPT_UNREGISTERED  
``` 
## Structure of the project

### Structure of the preprocessor

There are separate classes for the four main tasks of preprocessing: 
1. CliParser parses the CLI arguments
2. FileDownloaderDownloading the archive file
3. FileExtractor extracts the archive file and
4. FileFilter filters YARN related log files  

Preprocessor is coordinating the work of these classes.

![Class Diagram](http://www.plantuml.com/plantuml/png/ROyzhi8m48HxdsBBzufSuIcF019T6WwmYXV9ad-izGQ247SdFmQPA8MBcVcycgcJ84llh5ATnPJWFqPuA027nl_ygkVRRvaYXuvI2Zm3JV12Wq1sIxQnfaLEj80d7tiPDRe4JAXBycWixSFDsu0wuvC5Edkx-vdj-iDX4CPEGi7JOgt9yKa-Umr6msNM2pKDhQg_hs6g55FwcR8HbTstyhBgTfpxALMJrxspBm00)

### Structure of the interactive subshell

The interactive subshell's structure is built around pair of classes, where one class is responsible for the formatting (reading/writing) and the other class is responsible for the execution.
1. `CommandLine` parses the input from the CLI and `CommandExecutor` calls the appropriate `Command` to take action.
2. `OptionParser` (belonging to the specified `Command`) parses the parameters and through an `Executable` the `Command` will generate the desired output.
3. `SearchEngine` looks through the log files and finds matches with a specified regular expression and Formatter will generate the output from the matched lines in the logs.

![Class Diagram](http://www.plantuml.com/plantuml/png/RL7BJWCn3BpdAto4G_y0LKMed8eewXTuKtSRaSVYU148yU_ikfisk-8InJFZEEFPP46MFdXZhCUSuR7huCS017Jnxjxy5lpGD_bCtYlvcTmHk9y9db8868yx5qar1s4NMs12nVwHRh_8zfAubfDYD2akJIuAyGajbKPjsFE0O-C9Meh4A5IGsQpNJboAU-HCVFjxDzIZ3A19oHD6i6UoxuCC0mcnKjENMf8Qdtr8BUXCVVkj9_w6Z_4SgkV40KM8uLHkq_nYKyVKHcJX0PiiZ5P0pLGlpZOmlJ-BYv3jUHlR4iVEPNbuGHbyyXei4mfDPRuqZiclLkjhqgPS5AtYdH7BCCsYhZM5KwmEfYQgdPhRLPdQr9AxhiQO3SLZ7_mV)

#### Command classes

Every time a command is executed in the subshell one of the `Command` classes will be called. In the `Subshell` commands section, we already saw the output of these commands. In the following diagram, we can see how these `Command`s can be grouped into four categories.

1. Primitive commands implement the `Command` interface directly. No modifiers can be attached to them, they always display the same output.

Example:  
`exit`

2. `SimpleSearch` is extended by `Roles` and `Info`. These commands don't have modifiers, but their output depends on the content of the log files.

Example:  
`info`

3. `ParameterizedSearch` commands can have various options defined, which modify the output.

Example:  
`containers --application <appId> [--verbose]`

4. `HybridSearch` commands have a compulsory first parameter without a specified option. Options can be defined after the first parameter.

Examples:  
`appattempts <appId> [--verbose]`  
`grep <expression> [-rm] [--verbose]`

![Class Diagram](http://www.plantuml.com/plantuml/png/XP9BQWCn38RtEiLS87T82A7fhdJs1SMZK8CVHjQKFkZTIsg6IfcusNx_PyduzxOAiimnW4z2V4P7mpx7Y6aQlk0R04zL69qCBc2jm-XZ2JGIidkzAHm_JkodVuGdP8max3_9Q2tTbR8JeLYaQlp0LAwqu1sApwdUEzx7-vm4VI9kTC67DqfMFBmxAgjbprJpp8uC70M5hWvxKK-wu2E5SWC_fdCsIt9OnYtsosQZXzf0ZRM1RiuckZARhBi2q7Ckqk-DrxXT7A0DL_nIvHhhYW3KNkterdkIAhMhj8AxSQkm5wlFq5F-VWPiAKrp33y0)

#### Printable classes

The `Command`s always return a `Printable` object to the `Subshell` which will be printed to the CLI. The diagram below shows the types of `Printable` objects.

![Class Diagram](http://www.plantuml.com/plantuml/png/ROx12i8m38RlUOg-mDvX8DuyY7eMigxAe6b7ao8Ylhl33XQYj_1z-U7hMR18fGWm9GdbXZwuOrbk769mBtW3-8ZCRa-pFF-QiopqRXGEB0MzkFPpoe_3nA9dkqnpvZ3gePAnKjBaihriYslNcJ0ZJPknQvMXFs6m_drjAzEefG7q8OsLuWS0)


_Hudáky Márton_
