# Distributed password cracker

Application written in Scala/Akka as an academic project.  

## Running on single computer

Perform every step on separate terminal in shown order:

1. Start queue: `sbt "run queue"`
2. Start one or more coordinators:  `sbt "run"`
3. Start CLI client : `sbt "run client"` and follow instructions displayed on terminal


## Running on multiple computers

* Change `hostname` in `common.conf` to your public IP
* Adjust `queuePath` to reference computer on which queue will be run 
* Perform every step from  instruction in previous section on separate computer, but in shown order
