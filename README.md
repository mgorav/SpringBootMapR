# Micro Service With MapR-DB (OJAI) Using Spring Boot

## Introduction

A Spring Boot service which connect to MapR cluster and demonstrate querying using
 Open JSON Application Interface. OJAI  provides native integration of JSON-like 
 document processing in Hadoop-style scale-out clusters. 
 
##Prerequisites

* MapR Converged Data Platform 6.0.1 using MapR Dev Docker Container [MapR Container for Developers](https://maprdocs.mapr.com/home/MapRContainerDevelopers/MapRContainerDevelopersOverview.html).
* JDK 8
* Maven 3.x
* Spring Boot 2.0.2
* Setup [MapR 6.0 client](http://package.mapr.com/releases/v6.0.0/mac/)  in the folder /opt
* Docker (make sure atleast 8 GB of memory is allocated with 4 GB of swap memory)

## Setting up MapR Container For Developers

MapR Container For Developers is a docker image that enables quick deploy to a MapR environment 
from a developer machine (enclosed script mapr_devenv_docker_setup.sh is tested on
mac os)

* Step 1 
````bash
  ./mapr_devenv_docker_setup.sh 
```` 
This step can take upto 5 to 10 minutes ... be patient!! 

Verify that MapR cluster installation successful using following steps:
1. Log into docker images
````bash
  ssh root@localhost -p 2222 
```` 

2. Run JPS and make sure all the services are running as shown below:

````bash
  jps
  2883 QuorumPeerMain
  9860 Drillbit
  2869 CLDB
  2069 WardenMain
  10198 AdminApplication
  32552 Jps
  12856 DataAccessGatewayApplication
  10602 Gateway
````

3. Double check the Docker logs to see docker image IP has bee assigned successfully:

 * Get the container ID 
````bash
  CONTAINER ID        IMAGE                                   COMMAND                  CREATED             STATUS              PORTS                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  NAMES
  689e3f5ff7e0        maprtech/dev-sandbox-container:latest   "/bin/sh -c /usr/bin…"   2 hours ago         Up 2 hours          0.0.0.0:2049->2049/tcp, 0.0.0.0:3000->3000/tcp, 0.0.0.0:4040->4040/tcp, 0.0.0.0:4242->4242/tcp, 0.0.0.0:5181->5181/tcp, 0.0.0.0:5660-5661->5660-5661/tcp, 0.0.0.0:5692-5693->5692-5693/tcp, 0.0.0.0:5724->5724/tcp, 0.0.0.0:5756->5756/tcp, 0.0.0.0:7077->7077/tcp, 0.0.0.0:7221-7222->7221-7222/tcp, 0.0.0.0:8002->8002/tcp, 0.0.0.0:8032->8032/tcp, 0.0.0.0:8042->8042/tcp, 0.0.0.0:8044->8044/tcp, 0.0.0.0:8047->8047/tcp, 0.0.0.0:8080-8081->8080-8081/tcp, 0.0.0.0:8088->8088/tcp, 0.0.0.0:8090->8090/tcp, 0.0.0.0:8188->8188/tcp, 0.0.0.0:8190->8190/tcp, 0.0.0.0:8443->8443/tcp, 0.0.0.0:8888->8888/tcp, 0.0.0.0:9001-9002->9001-9002/tcp, 0.0.0.0:9997-9998->9997-9998/tcp, 0.0.0.0:10000-10001->10000-10001/tcp, 0.0.0.0:10020->10020/tcp, 0.0.0.0:11000->11000/tcp, 0.0.0.0:11443->11443/tcp, 0.0.0.0:12000->12000/tcp, 0.0.0.0:14000->14000/tcp, 0.0.0.0:18080->18080/tcp, 0.0.0.0:18630->18630/tcp, 0.0.0.0:19888->19888/tcp, 0.0.0.0:19890->19890/tcp, 0.0.0.0:31010-31011->31010-31011/tcp, 0.0.0.0:50000-50050->50000-50050/tcp, 0.0.0.0:50060->50060/tcp, 0.0.0.0:2222->22/tcp, 0.0.0.0:112->111/tcp   maprsandbox
````

 * See logs using following:
 ````bash
   docker logs 689e3f5ff7e0
 ````
 Verify following output i.e. the line container container IP address
 
  ````bash
    This container IP : 172.17.0.2
 ````


* Step 2 Download big data sample from [Yelp](https://www.yelp.com/dataset_challenge)  
         

Unzip and copy the user.json file to the container using container ID obtained above,
as shown below:

  ````bash
    docker cp user.json 689e3f5ff7e0:/root/
 ````
 
* Step 3 Import the user.json into MapR-DB JSON tables
 ````bash
mapr importJSON -idField user_id -src /tmp/user.json -dst /apps/user -mapreduce false
 ````
 
* Step 4 Give permission to access above MapR DB table

 ````bash
maprcli table cf edit -path /apps/user -cfname default -readperm p -writeperm p
````

Verify that Step 2 to 4 is done correctly as shown below:

``` bash
mapr dbshell

maprdb mapr:> jsonoptions --pretty true --withtags false

maprdb mapr:> find /apps/user --limit 2

```


### Play Time

Hit the url http://localhost:1234/{since} 

where since = 2010-07-09

NOTE: For simplify, limit of 1 is added to the MapRDB query
