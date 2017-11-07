# DistributedMessagingSystem
A fully distributed messaging system using SIP protocol
Authors:
-Arshia Hosseini, Maryam Amini, Hosseini Nassiri, Razieh Roustaei
*********************************
This project consists of a java SIP library (Jain SIP) and an underlay network using boost C++ library to handle very fast updates on databases.
*********************************
How to prepare the server:
Network Config:
1. Make sure that there are only two interfaces lo and enps03
2. To make sure use ifconfig command and remove other interfaces with following commands:
#sudo ifconfig INTERFACE_NAME down
#sudo brctl delbr INTERFACE_NAME
*********************************
Code Config:
1. Make sure that Boost Library for C++ is installed on your system
2. Go to the UnderlayServer and run following commands: 
#mysql -u root -p sip < sip.sql
#cp distributed_system_sip_update_server distributed_system_sip_update_command /usr/src/
#cd /usr/src/distributed_system_sip_update_server
3. Compile the underlay server using following command:
#g++ -I/usr/include/mysql -L/usr/lib64/mysql -I/root/Downloads/boost_1_60_0 -L/root/Downloads/boost_1_60_0/stage/lib -lmysqlclient -lboost_system -lboost_thread -lboost_date_time update_server.cpp message.cpp mysql_server.cpp -o server
4. Go to the next folder and run the command:
#cd /usr/src/distributed_system_sip_update_command
#g++ main.cpp -o update_server
5. Make symlinks to run:
#cd /usr/bin/
#ln -s /usr/src/distributed_system_sip_update_command/update_server update_server
#ln -s /usr/src/distributed_system_sip_update_server/server serve
6. Now copy Serverv4.jar in the command folder:
#cp Server.jar /usr/src/distributed_system_sip_update_command/
(Make sure that both Server.jar and Client.jar are on the VM if you are testing)
*********************************
How to run the server:
1. Use the following command to run the underlay server:
#server
2. Use this command to run the sip server:
#cd /usr/src/distributed_system_sip_update_command
#java -jar Server.jar
*********************************
How to run the Client:
1. Use the following command to run the client:
#java -jar Client.jar
*********************************
Features:
1. Decentralized and hierarchical design 
2. scalable and reliable
3. Supporting redundancy
4. Delivery report, registration success report, User not found report
5. Fast and optimized 
