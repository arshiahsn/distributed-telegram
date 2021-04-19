
[![LinkedIn][linkedin-shield]][linkedin-url]



<!-- PROJECT LOGO -->
<br />
<p align="center">

  <h3 align="center">FTP Server</h3>

  <p align="center">
    An FTP server that receives a file from an FTP client using Java IO.
    <br />
    <a href="https://github.com/arshiahsn/StopAndWait"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/arshiahsn/StopAndWait">View Demo</a>
    ·
    <a href="https://github.com/arshiahsn/StopAndWait/issues">Report Bug</a>
    ·
    <a href="https://github.com/arshiahsn/StopAndWait/issues">Request Feature</a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

Implementation of an FTP client that works based on the TCP/IP stop-and-wait protocol.


### Built With

* [JDK8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)



<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple steps.

### Network Configuration
1. Make sure that there are only two interfaces lo and enps03
2. To make sure use ifconfig command and remove other interfaces with following commands:

```sh
sudo ifconfig INTERFACE_NAME down
```
```sh
sudo brctl delbr INTERFACE_NAME
```

### Installation
1. Clone the repo
   ```sh
   git clone https://github.com/arshiahsn/distributed-telegram.git
   ```
2. Install Boost Library for C++ and MySQL
3. Go to the UnderlayServer and run following commands:
```sh 
mysql -u root -p sip < sip.sql
cp distributed_system_sip_update_server distributed_system_sip_update_command /usr/src/
cd /usr/src/distributed_system_sip_update_server
```
4. Compile the underlay server using following command:
```sh 
g++ -I/usr/include/mysql -L/usr/lib64/mysql -I/root/Downloads/boost_1_60_0 -L/root/Downloads/boost_1_60_0/stage/lib -lmysqlclient -lboost_system -lboost_thread -lboost_date_time update_server.cpp message.cpp mysql_server.cpp -o server
```
5. Go to the next folder and run the command:
```sh 
cd /usr/src/distributed_system_sip_update_command
g++ main.cpp -o update_server
```
6. Make symlinks to run:
```sh 
cd /usr/bin/
ln -s /usr/src/distributed_system_sip_update_command/update_server update_server
ln -s /usr/src/distributed_system_sip_update_server/server serve
```
7. Copy Serverv4.jar in the command folder:
```sh 
cp Server.jar /usr/src/distributed_system_sip_update_command/
(Make sure that both Server.jar and Client.jar are on the VM if you are testing)
```
   
### Running
#### Server

1. Use the following command to run the underlay server:
```sh 
server
```
2. Use this command to run the sip server:
```sh 
cd /usr/src/distributed_system_sip_update_command
java -jar Server.jar
```
#### Client

1. Use the following command to run the client:
```sh 
java -jar Client.jar
```
## Features
1. Decentralized and hierarchical design 
2. Scalable and reliable
3. Supporting redundancy
4. Delivery report, registration success report, User not found report
5. Fast and optimized

<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.



<!-- CONTACT -->
## Contact

Your Name - [@400_bad_req](https://twitter.com/400_bad_req) - arshiahsn@gmail.com

Project Link: [https://github.com/arshiahsn/simple_api](https://github.com/arshiahsn/simple_api)




<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/arshiahsn/repo.svg?style=for-the-badge
[contributors-url]: https://github.com/arshiahsn/repo/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/arshiahsn/repo.svg?style=for-the-badge
[forks-url]: https://github.com/arshiahsn/repo/network/members
[stars-shield]: https://img.shields.io/github/stars/arshiahsn/repo.svg?style=for-the-badge
[stars-url]: https://github.com/arshiahsn/repo/stargazers
[issues-shield]: https://img.shields.io/github/issues/arshiahsn/repo.svg?style=for-the-badge
[issues-url]: https://github.com/arshiahsn/repo/issues
[license-shield]: https://img.shields.io/github/license/arshiahsn/repo.svg?style=for-the-badge
[license-url]: https://github.com/arshiahsn/simple_api/blob/main/LICENSE
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/arshiahsn

