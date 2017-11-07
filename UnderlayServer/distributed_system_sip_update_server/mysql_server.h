#ifndef MYSQL_SERVER_H
#define MYSQL_SERVER_H

#include <stdio.h>
#include <mysql.h>
#include <string>
#include <boost/unordered_map.hpp>
#include <boost/thread.hpp>

#include<sstream>

#include <list>
#include <string>

#ifndef WIN32
#define BYTE u_int8_t
#endif

#define MONEYUNIT 10000

extern MYSQL    *MySQLConRet;
extern MYSQL    *MySQLConnection;
extern boost::mutex mysqlmutex;
extern boost::mutex logfilemutex;
extern FILE *logfile;

struct Pend
{
    u_short T_ID;
    char teamname[32];
};

extern boost::unordered_map<std::string,std::list<Pend> *> pending;

template <typename T>
std::string to_string(T value)
{
  //create an output string stream
  std::ostringstream os ;

  //throw the value into the string stream
  os << value ;

  //convert the string stream into a string and return
  return os.str() ;
}

struct TournomentInfo
{
    u_short T_ID;
    char T_NAME[32];
    BYTE gametype;
    char startdate[12];
    char teamname[32];
    char players[5][64];
    BYTE players_status[5];
    BYTE maxgroupmembers;
    bool tournomentType;//0 for team and 1 for single
    int numofplayers;
    BYTE payment_status;
};

class FFError
{
public:
    std::string    Label;

    FFError( ) { Label = (char *)"Generic Error"; }
    FFError( char *message ) { Label = message; }
    ~FFError() { }
    inline const char*   GetMessage  ( void )   { return Label.c_str(); }
};



int connecttomysql(std::string hostname,std::string username,std::string password,std::string database);
int truncateusersfrommysql();
int truncateparentsfrommysql();
int addusertomysql(std::string username,std::string IP);
int addparenttomysql(std::string ip,bool isprimary);
int removeuserfrommysql(std::string username);
int changeparentflaginmysql(std::string ip,bool flag);
void mysqlpinger();





#endif
