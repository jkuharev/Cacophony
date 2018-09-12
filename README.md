# Cacophony

Cacophony - the inharmonious sibling of symphony.

# License agreement: 'THE BEER-WARE LICENSE'
~~~
  The software application Cacophony was written by Jørg Kuharev.
  
  As long as you retain this notice you can do whatever you want
  with this application.
  
  If we meet some day & you think that Cacophony is worth it, 
  you can buy me a beer in return.
~~~
**© Dr. Jørg Kuharev, 2018**

# Abstract
The original application allows to trigger pipelines only from the acquisition pc 
or any other pc to which You have installed MassLynx and Symphony including their licenses. 
However, there is no official way to kick off piplines while ML is acquiring data.
Cacophony mimics the pipeline triggering process and allows to start pipelines
without interrupting the LCMS data acquisition process.

# Quick start
Cacophony is a distributed software split into server and client applications.
You need to run the server component on a pc with a Symphony that has a valid license.
The main user interaction is done by using the client application
that can be run locally on the same pc or on any other pc in the local network.
User can change the configuration of both application parts by editing *cacophony.ini* file
that is automatically created and initialized after the first start of the corresponding application.
You can start the application by `java -jar CacophonyServer.jar` / `java -jar CacophonyClient.jar` 
or doubleclicking the corresponding jar file.
The main window will allow to interact with the application by buttons in one of the toolbars
and will respond with messages in the text area.
The upper toolbar contains following buttons: **[ > ], [ x ], [ i ], [ - ]**.

- **[ > ]** establish / serve network connection,
- **[ x ]** disconnect / stop serving
- **[ i ]** display connection status
- **[ - ]** clear screen

The client application has an additional toolbar on the bottom with following buttons **RESET, PREVIEW, RUN**.

- **RESET** clear the raw file queue
- **PREVIEW** show files in the queue
- **RUN** trigger a pipeline per raw file in the queue

The queue is designed by drag and drop pipeline XML files and raw files over the message area of a client.
