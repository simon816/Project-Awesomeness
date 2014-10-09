import socket
from jio import (InputStream,
                 OutputStream)

class SockFileInputStream(InputStream):
    def __init__(self, sockfile):
        if type(sockfile) is not socket._fileobject:
            raise TypeError('Invalid socket file type')
        self.file = sockfile

    def _read(self, len):
        return self.file.read(len)

    def _close(self):
        self.file.close()

    def skip(self, n):
        raise IOError('Cannot skip a socket file')

class SockFileOutputStream(OutputStream):
    def __init__(self, sockfile):
        if type(sockfile) is not socket._fileobject:
            raise TypeError('Invalid socket file type')
        self.file = sockfile

    def _write(self, data):
        self.file.write(data)

    def close(self):
        self.file.close()

    def flush(self):
        self.file.flush()
        

class SocketInputStream(InputStream):
    def __init__(self, socket):
        self.sock = socket

    def _read(self, len):
        return self.sock.recv(len)

    def _close(self):
        self.sock.close()

    def skip(self, n):
        raise IOError('Cannot skip a socket')

class SocketOutputStream(OutputStream):
    def __init__(self, socket):
        self.sock = socket

    def _write(self, data):
        self.sock.sendall(bytes(map(ord, data)))

    def close(self):
        self.sock.close()
        

