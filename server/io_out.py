import abc
import struct

class OutputStream(object):

    @abc.abstractmethod
    def _write(self, data):
        return

    def write(self, b, off=None, len=None):
        if type(b) is int:
            b = [chr(b)]
        if type(b) is not list:
            raise TypeError("Buffer must be a list")
        if off is None and len is None:
            off = 0
            len = b.__len__()
        if (off is None and len is not None) or (len is None and off is not None):
            raise TypeError('off and len both need to be specified')
        d = ""
        for i in range(off, len + off):
            if type(b[i]) is int:
                d += chr(b[i])
            elif type(b[i]) is str:
                d += b[i]
            else:
                raise TypeError("Invalid type at position %d." % i)
        self._write(d)

    def flush(self):
        pass

    @abc.abstractmethod
    def close(self):
        return

class ByteArrayOutputStream(OutputStream):
    def __init__(self):
        self.buf = []

    def _write(self, data):
        self.buf.extend(data.split())

    def toByteArray(self):
        size = self.size()
        byte_array = []
        for i in range(size):
            byte_array.append(ord(self.buf[i]))
        return byte_array

    def size(self):
        return len(self.buf)

    def toString(self):
        return ''.join(self.buf)

    def close(self):
        pass

class FileOutputStream(OutputStream):
    def __init__(self, filename):
        if type(filename) is str:
            self.file = open(filename, 'wb')
        elif type(filename) is file:
            self.file = filename
        else:
            raise TypeError('Invalid file type')

    def _write(self, data):
        self.file.write(data)

    def close(self):
        self.file.close()

class DataOutputStream(OutputStream):
    def __init__(self, file):
        if not isinstance(file, OutputStream):
            raise TypeError('Invalid file')
        self.file = file

    def _write(self, data):
        self.file._write(data)

    def _pack(self, fmt, data):
        self._write(struct.pack('!' + fmt, data).decode('latin1'))

    def writeBoolean(self, v):
        if v:
            self._write('\1')
        else:
            self._write('\0')

    def writeByte(self, v):
        self.write(int(v))

    def writeBytes(self, s):
        self.write(str(s))

    def writeChar(self, v):
        self._pack('c', v)

    def writeChars(self, s):
        a = s.split()
        len = len(a)
        for i in range(len):
            self.writeChar(a[i])

    def writeDouble(self, v):
        self._pack('d', v)

    def writeFloat(self, v):
        self._pack('f', v)

    def writeInt(self, v):
        self._pack('i', v)

    def writeLong(self, v):
        self._pack('l', v)

    def writeShort(self, v):
        self._pack('h', v)

    def writeUTF(self, str):
        self.writeShort(len(str))
        self._write(str)

    def close(self):
        self.file.close()

    def writeSpecial(self, *args):
        raise NotImplementedError()
