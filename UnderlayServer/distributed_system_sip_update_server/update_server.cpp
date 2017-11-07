#include <cstdlib>
#include <vector>
#include <iostream>
#include <boost/bind.hpp>
#include <boost/asio.hpp>
#include <boost/shared_ptr.hpp>
#include <boost/enable_shared_from_this.hpp>
#include <boost/thread/thread.hpp>
#include <boost/unordered_map.hpp>
#include <boost/date_time/posix_time/posix_time.hpp>
#include <string>
#include <map>
#include <fstream>
#include <string>
#include <sys/time.h>
#include "mysql_server.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <linux/fs.h>
#include <pthread.h>

#include "message.h"
#include "update_server.h"
#include "protocol.h"

using namespace std;


int GetCurrentSystemTime(void)
{
    struct timeval tp;
    struct timezone tzp;
    static int basetime;
    gettimeofday(&tp, &tzp);
    if (!basetime)
    {
        basetime = tp.tv_sec;
        return tp.tv_usec / 1000;
    }
    return (tp.tv_sec - basetime) * 1000 + tp.tv_usec / 1000;
}



int openudpsocket()
{
    struct sockaddr_in myaddr;

    if ((SendUpdateSocket = socket(AF_INET, SOCK_DGRAM, 0)) < 0)
    {
        perror("cannot create socket");
        return EXIT_FAILURE;
    }

    memset((void *)&myaddr, 0, sizeof(myaddr));
    myaddr.sin_family = AF_INET;
    myaddr.sin_addr.s_addr = htonl(INADDR_ANY);
    myaddr.sin_port = htons(0);

    if (bind(SendUpdateSocket, (struct sockaddr *)&myaddr, sizeof(myaddr)) < 0)
    {
        perror("bind failed");
        return EXIT_FAILURE;
    }
    return EXIT_SUCCESS;

}


//-----------------------------TURN Server-----------------------------
update_server::update_server(boost::asio::io_service& io_service, short port)
    : socket_(io_service, udp::endpoint(udp::v4(), port)), myport(port)
{
    connecttomysql("127.0.0.1","root","@dminCronos2008","sip");
    th_mysql_pinger = new boost::thread(mysqlpinger);
    truncateusersfrommysql();
    truncateparentsfrommysql();
    th_send_priodic_update = new boost::thread(SendPreiodicUpdates);
    th_get_commands = new boost::thread(getcommands);

    read_msg_.Init(read_msg_.incomingData, sizeof(read_msg_.incomingData));
    socket_.async_receive_from(boost::asio::buffer(read_msg_.data, max_length), sender_endpoint_,
        boost::bind(&update_server::handle_receive_from, this,
            boost::asio::placeholders::error,
            boost::asio::placeholders::bytes_transferred));
}

void update_server::handle_receive_from(const boost::system::error_code& error, size_t bytes_recvd)
{
    if (!error && bytes_recvd > 0)
    {
        read_msg_.SetSize(bytes_recvd);
        parse_packet(&read_msg_);
        read_msg_.Clear();
    }
    else
    {
        socket_.async_receive_from(
            boost::asio::buffer(read_msg_.data, max_length), sender_endpoint_,
            boost::bind(&update_server::handle_receive_from, this,
                boost::asio::placeholders::error,
                boost::asio::placeholders::bytes_transferred));
    }
}

void update_server::handle_send_to(const boost::system::error_code& /*error*/, size_t /*bytes_sent*/)
{
}


void update_server::parse_packet(Message *msg)
{
    BYTE command = (BYTE)msg->ReadByte();
    switch (command)
    {
    case MESS_UPDATE_ADDCLIENT:
    {
        std::string tmpip = sender_endpoint_.address().to_string();
        std::string tmpusername (msg->ReadString());
        if(!users.count(tmpusername) && users[tmpusername] != tmpip)
        {
            addusertomysql(tmpusername,tmpip);
        }
        users[tmpusername] = tmpip;
        users_last_update[tmpusername] = GetCurrentSystemTime();
    }
    break;
    case MESS_UPDATE_REMOVECLIENT:
    {
        std::string tmpip = sender_endpoint_.address().to_string();
        std::string tmpusername (msg->ReadString());
        if(users.count(tmpusername))
        {
            if(users[tmpusername] ==  tmpip)
            {
                users.erase(tmpusername);
                users_last_update.erase(tmpusername);
                removeuserfrommysql(tmpusername);
            }
        }
    }
    break;
    case MESS_UPDATE_KEEPALIVE:
    {
        Message msg;
        msg.Init(msg.outgoingData,sizeof(msg.outgoingData));
        msg.WriteByte(MESS_UPDATE_KEEPALIVE_REPLY);
        socket_.async_send_to(
            boost::asio::buffer(msg.data, msg.GetSize()), udp::endpoint(boost::asio::ip::address_v4::from_string(sender_endpoint_.address().to_string()), 4444),
            boost::bind(&update_server::handle_send_to, this,
                boost::asio::placeholders::error,
                boost::asio::placeholders::bytes_transferred));
    }
        break;
    case MESS_UPDATE_KEEPALIVE_REPLY:
    {
        parents_last_keepalive[sender_endpoint_.address().to_string()] = GetCurrentSystemTime();
    }
        break;
    default:
        break;
    }

    socket_.async_receive_from(
        boost::asio::buffer(read_msg_.data, max_length), sender_endpoint_,
        boost::bind(&update_server::handle_receive_from, this,
            boost::asio::placeholders::error,
            boost::asio::placeholders::bytes_transferred));

}



void update_server::handle_write(const boost::system::error_code& error)
{
    if (!error)
    {
    }
    else
    {
    }
}

/////////////////////////////////////////////////////////////////////////////


void send_add_user(std::string user,std::string ip)
{
    Message msg;
    struct sockaddr_in servaddr;
    memset((char*)&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(UPDATE_PORT);
    servaddr.sin_addr.s_addr=inet_addr(ip.c_str());

    msg.Init(msg.outgoingData,sizeof(msg.outgoingData));
    msg.WriteByte(MESS_UPDATE_ADDCLIENT);
    msg.WriteString(const_cast<char*>(user.c_str()));
    if (sendto(SendUpdateSocket, msg.data, msg.GetSize(), 0, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
    {
        perror("sendto failed");
        exit(EXIT_FAILURE);
    }
}

void send_remove_user(std::string user,std::string ip)
{
    Message msg;
    struct sockaddr_in servaddr;
    memset((char*)&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(UPDATE_PORT);
    servaddr.sin_addr.s_addr=inet_addr(ip.c_str());

    msg.Init(msg.outgoingData,sizeof(msg.outgoingData));
    msg.WriteByte(MESS_UPDATE_REMOVECLIENT);
    msg.WriteString(const_cast<char*>(user.c_str()));
    if (sendto(SendUpdateSocket, msg.data, msg.GetSize(), 0, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
    {
        perror("sendto failed");
        exit(EXIT_FAILURE);
    }
}

void SendKeepAlive(std::string ip)
{
    Message msg;
    struct sockaddr_in servaddr;
    memset((char*)&servaddr, 0, sizeof(servaddr));
    servaddr.sin_family = AF_INET;
    servaddr.sin_port = htons(UPDATE_PORT);
    servaddr.sin_addr.s_addr=inet_addr(ip.c_str());

    msg.Init(msg.outgoingData,sizeof(msg.outgoingData));
    msg.WriteByte(MESS_UPDATE_KEEPALIVE);
    if (sendto(SendUpdateSocket, msg.data, msg.GetSize(), 0, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
    {
        perror("sendto failed");
        exit(EXIT_FAILURE);
    }
}


void SendPreiodicUpdates()
{
    while(true)
    {


        for(boost::unordered_map<std::string,bool>::iterator it_p = parents.begin(); it_p!= parents.end(); it_p++)
        {

            SendKeepAlive(it_p->first);
            sleep(1);
            while(GetCurrentSystemTime() - parents_last_keepalive[it_p->first] < 6000)
            {
                if(parents[it_p->first]==false)
                {
                    changeparentflaginmysql(it_p->first,true);
                    parents[it_p->first]=true;
                }
                for(boost::unordered_map<std::string,std::string>::iterator it_u = users.begin(),it_u_tmp;it_u!=users.end();)
                {
                    it_u_tmp = it_u++;
                    if(users_last_update.count(it_u_tmp->first))
                    {
                        if(GetCurrentSystemTime() - users_last_update[it_u_tmp->first] > 6000)
                        {
                            send_remove_user(it_u_tmp->first,it_p->first);
                            removeuserfrommysql(it_u_tmp->first);
                            users_last_update.erase(it_u_tmp->first);
                            users.erase(it_u_tmp->first);
                        }
                        else
                        {
                            send_add_user(it_u_tmp->first,it_p->first);
                        }
                    }
                }
                SendKeepAlive(it_p->first);
                sleep(2);
            }
            changeparentflaginmysql(it_p->first,false);
            parents[it_p->first]=false;

        }



    }
}

void split(const std::string &s, char delim, std::vector<std::string> &elems)
{
    std::stringstream ss;
    ss.str(s);
    std::string item;
    while (std::getline(ss, item, delim))
    {
        elems.push_back(item);
    }
}


void getcommands()
{
    char *socket_path = "\0hidden";

    struct sockaddr_un addr;
    char command_buf[100];
    int fd,cl,rc;


    if ( (fd = socket(AF_UNIX, SOCK_STREAM, 0)) == -1)
    {
          perror("socket error");
          exit(-1);
    }

    memset(&addr, 0, sizeof(addr));
    addr.sun_family = AF_UNIX;
    if (*socket_path == '\0')
    {
          *addr.sun_path = '\0';
          strncpy(addr.sun_path+1, socket_path+1, sizeof(addr.sun_path)-2);
    }
    else
    {
              strncpy(addr.sun_path, socket_path, sizeof(addr.sun_path)-1);
    }


    unlink(socket_path);

    if (bind(fd, (struct sockaddr*)&addr, sizeof(addr)) == -1)
    {
          perror("bind error");
          exit(-1);
    }

    if (listen(fd, 5) == -1)
    {
          perror("listen error");
          exit(-1);
    }

    while (1)
    {
          if ( (cl = accept(fd, NULL, NULL)) == -1)
          {
                perror("accept error");
                continue;
          }

          while ( (rc=read(cl,command_buf,sizeof(command_buf))) > 0)
          {
              std::string command(command_buf);
              std::vector<std::string> elems;
              split(command,' ',elems);
              if(elems[0]=="user")
              {
                  if(!users.count(elems[1]))
                      addusertomysql(elems[1],elems[2]);
                  users[elems[1]] = elems[2];
                  users_last_update[elems[1]] = GetCurrentSystemTime();
              }
              else if (elems[0]=="parent")
              {
                  if(elems[2]=="true")
                  {
                     if(!parents.count(elems[1]))
                     {
                        parents[elems[1]]=true;
                        addparenttomysql(elems[1],true);
                     }

                  }
                  else
                  {
                      if(!parents.count(elems[1]))
                      {
                         parents[elems[1]]=false;
                         addparenttomysql(elems[1],false);
                      }
                  }
              }
          }

          if (rc == -1)
          {
                perror("read");
                exit(-1);
          }
          else if (rc == 0)
          {
                close(cl);
          }
    }

}
//---------------------------------------------------------------------


int main()
{
    openudpsocket();
    boost::asio::io_service update_server_io_service;

    update_server_list update_servers;
    for (int i = 0; i < NUMOFSERVERS; i++)
    {
        update_server_ptr server(new update_server(update_server_io_service, UPDATE_PORT));
        update_servers.push_back(server);
    }
    update_server_io_service.run();

    return 0;
}
