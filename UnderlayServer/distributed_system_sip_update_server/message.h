#ifndef MESSAGE_H
#define MESSAGE_H

#include <cstdio>
#include <cstdlib>
#include <cstring>


class Message
{
private:
    bool overFlow;
    int maxSize;
    int size;
    int readCount;
    char *GetNewPoint(int length);
    size_t body_length_;

public:
    void Init(char *d, int length);
    void Clear(void);
    void Write(void *d, int length);
    void WriteByte(char c);
    void WriteShort(short c);
    void WriteLong(long c);
    void WriteLongLong(long long c);
    void WriteFloat(float c);
    void WriteString(char *s);
    void BeginReading(void);
    void BeginReading(int s);
    char *Read(int s);
    char ReadByte(void);
    short ReadShort(void);
    long ReadLong(void);
    long long ReadLongLong(void);
    float ReadFloat(void);
    char *ReadString(void);
    bool decode_header();
    void encode_header();
    bool GetOverFlow(void) { return overFlow; }
    int GetSize(void) const { return size; }
    void SetSize(int s) { size = s; }
    void SetBodySize(size_t s) { body_length_ = s; }
    size_t GetBodySize(void) { return body_length_; }
    char *data;
    char outgoingData[1400];
    char incomingData[1400];
    char LoginData[64];
    char newsdata[3];
    char messageinfo[9];
    char messagedelivery[13];
    char TypeOfPacket[1];
    char messagedata[1400];
    enum { header_length = 4 };
    enum { MaxBodyLenght = 1396 };

};


#endif
