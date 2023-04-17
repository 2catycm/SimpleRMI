# FastRMI:  Improving the performance of RMI via coroutine after Learning RMI Interface in Depth



<div align="center">
    叶璨铭（YeCanming）, 12011404 <br>
    南方科技大学（SUSTech） <br>
    12011404@mail.sustech.edu.cn <br>
</div>
Note: For time shortage, some parts of this pdf is omitted, if you are interested in these parts, you can view the complete version on [github](https://github.com/2catycm/SimpleRMI/blob/master/report/BetterRMI Implementation——分布式系统作业2.md). 

The source code of this assignment is open-source in this [repository](https://github.com/2catycm/SimpleRMI).

<!-- SUSTech CS Course of Distributed System, Assignment 2 Report, March 2023 -->

> Java RMI, so wonderful, Makes your code more beautiful. It lets your program be distributed, Communicating between JVMs, undisputed(不可争辩的).
>
> Java RMI, so magical, Makes your program more practical. It lets your objects be remotely invoked, Transferring between hosts, no problem evoked.
>
> Java RMI, so charming, Makes your program more alarming. It lets your code be more concise, Interacting between processes, no compromise.
>
> ——new Bing, Java RMI: The Beauty of Distributed Programming

[TOC]

## Part 1 Introduction

> In this part, I will briefly **restate the problem** we are solving in this assignment, articulating the **study objectives** and some **research questions** that will be answered by the experiment and practice.  

### The goal of this project

In this assignment, we are required to use **MPI** to implement **parallel matrix multiplication**. The goal of this project should contain:

- Learn the basic usage of MPI. Configure the environment. 
- Discover the charm of MPI in the scene of parallel matrix multiplication. Survey the algorithms for parallel matrix multiplication. 
- Recognize the patterns of scalability via experiments. 

### Some issues to consider

- 

## Part 2 Methodology

> In this part, we learn and explore the methods to solve problems above by doing a literature review on the internet together. We summarize the key points that are useful for our experiments. 

### Interface of RMI

#### Will Server also create a stub besides a skeleton?



#### The life cycle of Remote Object: Will registry still exist when server process had finished?



#### Will JVM transport the class definition? What happens when interface are different?



#### Will JVM transport the class definition? What happens when arguments are not Serializable



#### Difference of bind and rebind



### Problem of RMI

#### Tedious and non-transparent API

- Tedious Remote Object requirement
  - Not every classes are possible to be remote object. This greatly limits the usage of RMI. 
  - Remote classes are suggested to extends `UnicastRemoteObject`, otherwise a manual `exportObject` must be invoke.
    - As we know, *Java only allows a class to extend exactly one class*. 
    - For the classes that need to extend other class, they cannot extend `UnicastRemoteObject`. 
    - If every time we new an object from these classes, it must call `exportObject`, **then this is surely not transparent**, because it did not hide the detail of implementation. 
  -  
  - 

#### Arbitrary code execution vulnerability

#### Poor Performance if “Thread per request” or “Thread per connection”







## Part 3 Experiment 

### Experiment Infrastructure: The Junit Test Framework

### 

#### Experiment principle



#### Experiment result and conclusion





## Part 4 Conclusion

In this project, I have learnt a lot. 

- We implement the functions of RMI.
- We used coroutine to replace threads, which yields a much faster RMI implementation 
  - that supports 10000 method calls on one remote object simultaneously.
  - that supports 10000 clients using a remote object simultaneously.
  - that supports 10000 Remote Objects exist on the server simultaneously.
- 

# References

[^1]: 
