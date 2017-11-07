#include "mysql_server.h"

MYSQL    *MySQLConRet;
MYSQL    *MySQLConnection = NULL;
boost::mutex mysqlmutex;
boost::mutex logfilemutex;
FILE *logfile;

int connecttomysql(std::string hostname,std::string username,std::string password,std::string database)
{
    MySQLConnection = mysql_init( NULL );
    try
    {
        MySQLConRet = mysql_real_connect( MySQLConnection,
                                          hostname.c_str(),
                                          username.c_str(),
                                          password.c_str(),
                                          database.c_str(),
                                          0,
                                          NULL,
                                          0 );

        if ( MySQLConRet == NULL )
        {
            throw FFError( (char*) mysql_error(MySQLConnection) );
        }


    }
    catch ( FFError e )
    {
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int addusertomysql(std::string username, std::string IP)
{
    int mysqlStatus = 0;
    MYSQL_RES   *mysqlResult = NULL;
    try
    {
        std::string sqlSelStatement = "insert into users(user,ip) values ('"+username+"' , '"+IP+"')";
        mysqlmutex.lock();
        mysqlStatus = mysql_query( MySQLConnection, sqlSelStatement.c_str() );

        if (mysqlStatus)
            throw FFError( (char*)mysql_error(MySQLConnection) );
        else
            mysqlResult = mysql_store_result(MySQLConnection); // Get the Result Set

         // print query results

        if(mysqlResult)
        {
            mysql_free_result(mysqlResult);
            mysqlResult = NULL;
        }
        mysqlmutex.unlock();
    }
    catch ( FFError e )
    {
        mysql_close(MySQLConnection);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int removeuserfrommysql(std::string username)
{
    int mysqlStatus = 0;
    MYSQL_RES   *mysqlResult = NULL;
    try
    {
        std::string sqlSelStatement = "DELETE FROM users where user='"+username+"'";
        mysqlmutex.lock();
        mysqlStatus = mysql_query( MySQLConnection, sqlSelStatement.c_str() );

        if (mysqlStatus)
            throw FFError( (char*)mysql_error(MySQLConnection) );
        else
            mysqlResult = mysql_store_result(MySQLConnection); // Get the Result Set

         // print query results

        if(mysqlResult)
        {
            mysql_free_result(mysqlResult);
            mysqlResult = NULL;
        }
        mysqlmutex.unlock();
    }
    catch ( FFError e )
    {

        mysql_close(MySQLConnection);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int truncateusersfrommysql()
{

    int mysqlStatus = 0;
    MYSQL_RES   *mysqlResult = NULL;
    try
    {
        std::string sqlSelStatement = "truncate table users;";
        mysqlmutex.lock();
        mysqlStatus = mysql_query( MySQLConnection, sqlSelStatement.c_str() );

        if (mysqlStatus)
            throw FFError( (char*)mysql_error(MySQLConnection) );
        else
            mysqlResult = mysql_store_result(MySQLConnection); // Get the Result Set

         // print query results

        if(mysqlResult)
        {
            mysql_free_result(mysqlResult);
            mysqlResult = NULL;
        }
        mysqlmutex.unlock();
    }
    catch ( FFError e )
    {
        mysql_close(MySQLConnection);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int truncateparentsfrommysql()
{
    int mysqlStatus = 0;
    MYSQL_RES   *mysqlResult = NULL;
    try
    {
        std::string sqlSelStatement = "truncate table parents;";
        mysqlmutex.lock();
        mysqlStatus = mysql_query( MySQLConnection, sqlSelStatement.c_str() );

        if (mysqlStatus)
            throw FFError( (char*)mysql_error(MySQLConnection) );
        else
            mysqlResult = mysql_store_result(MySQLConnection); // Get the Result Set

         // print query results

        if(mysqlResult)
        {
            mysql_free_result(mysqlResult);
            mysqlResult = NULL;
        }
        mysqlmutex.unlock();
    }
    catch ( FFError e )
    {
        mysql_close(MySQLConnection);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int addparenttomysql(std::string ip,bool isprimary)
{
    std::string str_isprimary;
    if(isprimary)
        str_isprimary="true";
    else
        str_isprimary="false";
    int mysqlStatus = 0;
    MYSQL_RES   *mysqlResult = NULL;
    try
    {
        std::string sqlSelStatement = "insert into parents(IP,flag) values ('"+ip+"' , "+str_isprimary+")";
        mysqlmutex.lock();
        mysqlStatus = mysql_query( MySQLConnection, sqlSelStatement.c_str() );

        if (mysqlStatus)
            throw FFError( (char*)mysql_error(MySQLConnection) );
        else
            mysqlResult = mysql_store_result(MySQLConnection); // Get the Result Set

         // print query results

        if(mysqlResult)
        {
            mysql_free_result(mysqlResult);
            mysqlResult = NULL;
        }
        mysqlmutex.unlock();
    }
    catch ( FFError e )
    {
        mysql_close(MySQLConnection);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

int changeparentflaginmysql(std::string ip,bool flag)
{
    std::string str_flag;
    if(flag)
        str_flag="true";
    else
        str_flag="false";
    int mysqlStatus = 0;
    MYSQL_RES   *mysqlResult = NULL;
    try
    {
        std::string sqlSelStatement = "UPDATE parents SET flag="+str_flag+" WHERE IP='"+ip+"'";
        mysqlmutex.lock();
        mysqlStatus = mysql_query( MySQLConnection, sqlSelStatement.c_str() );

        if (mysqlStatus)
            throw FFError( (char*)mysql_error(MySQLConnection) );
        else
            mysqlResult = mysql_store_result(MySQLConnection); // Get the Result Set

         // print query results

        if(mysqlResult)
        {
            mysql_free_result(mysqlResult);
            mysqlResult = NULL;
        }
        mysqlmutex.unlock();
    }
    catch ( FFError e )
    {
        mysql_close(MySQLConnection);
        return EXIT_FAILURE;
    }

    return EXIT_SUCCESS;
}

void mysqlpinger()
{
    while(true)
    {
        mysqlmutex.lock();
        if(mysql_ping(MySQLConnection))
        {
            break;
        }
        mysqlmutex.unlock();
        sleep(30);
    }
}
