--------------------------
-Concurrent Socket Server-
--------------------------
Due Apr 11 by 11:59pm 
Points 150 
Submitting a file upload File Type zip 
Available Jan 6 at 8am - May 2 at 11:59pm
-------------------------------------------------------

Project Description

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
This project requires students to implement a concurrent (multi-threaded) server for use in a client-server configuration to examine, analyze, and study the effects a concurrent server has on the efficiency (average turn-around time) of processing client requests.

Instructions

Students may refer to this Java ServerSocket TutorialLinks to an external site. and Running System Commands TutorialLinks to an external site. to help them create the server and client programs. Students must demonstrate their client and server programs, as well as collect data, using the provided Course Servers. Students should refer to the Accessing the Course Servers page for instructions on connecting to and using the two (2) Course Servers.

Create two (2) programs: 1) a concurrent (multi-threaded) server that accepts requests from clients, and 2) a multi-threaded client capable of spawning numerous client sessions that connect to the server
The server and client programs must connect to the same network address and port
The concurrent (multi-threaded) server should spawn a new server instance for each client request (in parallel)
The concurrent (multi-threaded) server must support the following client requests:
Date and Time - the date and time on the server
Uptime - how long the server has been running since last boot-up
Memory Use - the current memory usage on the server
Netstat - lists network connections on the server
Current Users - list of users currently connected to the server
Running Processes - list of programs currently running on the server
Create a multi-threaded client that transmits requests to the server on a specified network port
The client program must be able to spawn multiple client sessions
When the server program is launched, the server should
Listen for client requests on the specified network address and port
When a client request is received, the server should
Spawn a new server instance to handle the request
Determine which operation is being requested
Perform the requested operation and collect the resulting output
Reply to the client request with the output from the operation performed
Destroy the server instance
Perform any necessary clean up activities
When the client program is launched, the client should
Request the network address and port to which to connect
Request the operation to request (refer to the list above)
Request how many client requests to generate (1, 5, 10, 15, 20, 25 and 100)
Collect the following data
Turn-around Time (elapsed time) for each client request
The time required for the client request to travel to the server, be processed by the server, and return to the client
Total Turn-around Time (sum of the turn-around times for all the client requests)
Average Turn-around Time (Total Turn-around Time divided by the number client requests)
Deliverables

Program (50 points)

The source code, as well as any other software required, of the client and server programs. 

Demonstration (50 points)

Teams will demonstrate the operations of their client and server programs to the professor in one of the School of Computing labs.

Report (50 points)

The report must contain a minimum of 1,000 words (not counting the Title Page). The report must contain the following sections (at a minimum):

Title Page (must include the following at a minimum, in some order):
Title of the assignment (i.e., Concurrent Socket Server)
Names of two (2) students
Name of the class (i.e., CNT4504 - Computer Networks & Distributed Processing)
Professor's Name
Introduction
Describe the purpose of the project in your own words
Describe the goals of the project in your own words
Introduce readers to what they will find in the remaining sections of the paper
Client-Server Setup and Configuration
Describe the design of the Client and Server programs
Discuss the design decisions that were made
Explain the basic operation of the Client and Server programs
Testing and Data Collection
Explain how the concurrent server was tested
List and explain the data collected
Provide at least one (1) chart or graph for the data collected for each of the six (6) operations
Include the charts or graphs created for the Iterative Socket Server assignment
Data Analysis
Answer the following questions:
What affect, if any, does increasing the number of clients have on the Turn-around Time for individual clients?
What affect, if any, does increasing the number of clients have on the Average Turn-around Time?
How do the Average Turn-around Time results contrast between the iterative server and the concurrent server?
In which situation(s) might you want to use an iterative server?
In which situation(s) might you want to use a concurrent server?
Conclusion
Explain the conclusion(s) you can draw from your data analysis
Lessons Learned
List any lessons you learned during the Concurrent Server assignment
Some examples may inclue
Writing the Client and Server programs
Having a program execute Linux commands
Collecting data
Problems you had to overcome
