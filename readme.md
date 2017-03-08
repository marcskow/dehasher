# Distributed password cracker

Application written in Scala/Akka as an academic project. Project's purpose was to write application performing distributed computation using Actor paradigm.

It allows  to crack given hash  in one of 3 possible algorithms:
- SHA-256
- SHA-1
- MD5

### Features: 
- distributing tasks on many nodes 
- ability do dynamically attach new working nodes to running task
- gracefully handling failure of working node

## Running on single computer

Perform every step on separate terminal in shown order:

1. Start queue: `sbt "run queue"`
2. Start one or more coordinators:  `sbt "run"`
3. Start CLI client : `sbt "run client"` and follow instructions displayed on terminal


## Running on multiple computers

Adjust  `common.conf` on every computer you want to run application:
* Change `hostname`  to your public IP
* Adjust `queuePath` to reference computer on which queue will be run 
* Perform every step from  instruction in previous section on separate computer, but in shown order
