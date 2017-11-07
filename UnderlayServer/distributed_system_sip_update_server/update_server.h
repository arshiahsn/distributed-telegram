#ifndef UPDATE_SERVER_H
#define UPDATE_SERVER_H

using boost::asio::ip::tcp;
using boost::asio::ip::udp;
#define NUMOFSERVERS 1
#define UPDATE_PORT 4444
int SendUpdateSocket;

class update_server;
typedef boost::shared_ptr<update_server> update_server_ptr;
typedef std::list<update_server_ptr> update_server_list;

boost::unordered_map<std::string,std::string> users;
boost::unordered_map<std::string,int> users_last_update;
boost::unordered_map<std::string,bool> parents;
boost::unordered_map<std::string,bool> parent_isalive;
boost::unordered_map<std::string,int> parents_last_keepalive;

int openudpsocket();

void send_add_user(std::string user,std::string ip);
void send_remove_user(std::string user,std::string ip);
void SendPreiodicUpdates();
void getcommands();
void SendKeepAlive(std::string ip);

class update_server
{
public:
    update_server(boost::asio::io_service& io_service, short port);
    void handle_receive_from(const boost::system::error_code& error, size_t bytes_recvd);
    void handle_send_to(const boost::system::error_code& /*error*/, size_t /*bytes_sent*/);
    void parse_packet(Message *msg);
    void handle_write(const boost::system::error_code& error);
private:
    Message read_msg_;
    udp::socket socket_;
    udp::endpoint sender_endpoint_;
    enum { max_length = 1500 };
    char data_[max_length];
    boost::unordered_map < u_int, std::string   > usernames;
    std::vector<udp::endpoint> endpoints_list;
    u_int baseip;
    u_short myport;
    boost::thread *th_mysql_pinger;
    boost::thread *th_send_priodic_update;
    boost::thread *th_get_commands;
};



int GetCurrentSystemTime(void);



#endif // UPDATE_SERVER_H
