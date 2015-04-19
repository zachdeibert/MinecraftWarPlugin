package com.gitlab.zachdeibert.WarPlugin;

public class ArrayStream {
    final char data[];
    int offset;
    
    public char readChar() {
        return data[offset++];
    }
    
    public void readWhitespace() {
        while ( offset < data.length && (data[offset] == ' ' || data[offset] == '\t' || data[offset] == '\n') ) {
            offset++;
        }
    }
    
    public ArrayStream(char data[]) {
        this.data = data;
    }
}
