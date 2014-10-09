import abc
import os
import struct

class InputStream(object, metaclass=abc.ABCMeta):
    closed = False

    @abc.abstractmethod
    def _read(len):
      return

    def read(self, b=None, off=None, len=None):
        if self.closed:
            raise IOError("Tried to read on a closed file")
        if b is None and off is not None:
            raise TypeError('off must be null if b is null')
        elif b is None and len is not None:
            raise TypeError('len must be null if b is null')
        elif off is not None and len is None:
            raise TypeError('len cannot be null if off is not null')
        read_one = False
        if b is None:
            read_one = True
            b = ['']
        if off is None:
            off = 0
        if len is None:
            len = b.__len__()
        bytearr = self._read(len)
        blen = bytearr.__len__()
        if blen == 0:
            raise EOFError("Tried to read past the end of the file")
        for i in range(blen):
            b[off + i] = chr(bytearr[i])
        if read_one:
            return ord(b[0])
        return blen

    def close(self):
        self._close()
        self.closed = True

    @abc.abstractmethod
    def _close(self):
        return

    @abc.abstractmethod
    def skip(self, n):
        return

class ByteArrayInputStream(InputStream):
    def __init__(self, buf):
        if type(buf) is not list:
            raise TypeError("Buffer must be list")
        self.buf = []
        for i in range(len(buf)):
            if type(buf[i]) is int:
                b = chr(buf[i])
            elif type(buf[i]) is str:
                b = buf[i]
            else:
                raise TypeError("Invalid type at index %d %s" % (i, type(buf[i])))
            self.buf.append(b)
        self.mark = 0
        self.pos = 1

    def _read(self, len):
        data = ''.join(self.buf[self.mark:self.mark + len])
        self.mark += len
        return data

    def skip(self, n):
        self.mark += n
        self.pos += n

    def _close(self):
        pass

    def close(self):
        pass

class FileInputStream(InputStream):
    def __init__(self, filename):
        if type(filename) is str:
            self.file = open(filename, 'rb')
        elif type(filename) is file:
            self.file = filename
        else:
            print((repr(type(filename))))
            raise TypeError('Invalid file type')

    def _read(self, len):
        return self.file.read(len)

    def skip(self, n):
        self.file.seek(n, os.SEEK_CUR)

    def _close(self):
        self.file.close()

class DataInputStream(InputStream):
    def __init__(self, file):
        if not isinstance(file, InputStream):
            raise TypeError('Invalid file')
        self.file = file

    def _read(self, len):
        return self.file._read(len)

    def _unpack(self, fmt):
        len = struct.calcsize(fmt)
        buf = [None] * len
        self.read(buf)
        return struct.unpack('!' + fmt, bytes(map(ord, buf)))

    def readBoolean(self):
        return self.read() != 0

    def readByte(self):
        return self.read()

    def readChar(self):
        st = self._unpack('c')
        return st[0]

    def readDouble(self):
        st = self._unpack('d')
        return st[0]

    def readFloat(self):
        st = self._unpack('f')
        return st[0]

    def readFully(self, b=None, off=None, len=None):
        raise NotImplementedError()

    def readInt(self):
        st = self._unpack('i')
        return st[0]

    def readLong(self):
        st = self._unpack('l')
        return st[0]

    def readShort(self):
        st = self._unpack('h')
        return st[0]

    def readUnsignedByte(self):
        return self.read() & 0xFF

    def readUnsignedShort(self):
        st = self._unpack('H')
        return st[0]

    def readUTF(self):
        len = self.readUnsignedShort()
        return self._read(len).decode('utf-8')

    def skipBytes(self, n):
        self.skip(n)

    def skip(self, n):
        self.file.skip(n)

    def _close(self):
        self.file.close()

    def readSpecial(self, special):
        raise NotImplementedError()
