import socket
import time
from jio import *
from sockio import *

def do():
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_KEEPALIVE, 1)
    sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    sock.connect(("127.0.0.1", 9001))
    dis = DataInputStream(SocketInputStream(sock))
    dos = DataOutputStream(SocketOutputStream(sock))
    while True:
        try:
            action = input("Read, Write or Close (r/w/c) > ")
            if action == 'r':
                fn = 'read' + input("dis.read* > ")
                try:
                    print(getattr(dis, fn)())
                except AttributeError:
                    print('No such function ', fn)
            elif action == 'w':
                fn = 'write' + input("ds.write* > ")
                data = input('dos.' + fn + '(data) where data = ')
                if fn in ['writeInt', 'writeShort', 'writeLong', 'write']:
                    print('int data')
                    data = int(data)
                try:
                    getattr(dos, fn)(data)
                except AttributeError:
                    print('No such function ', fn)
            elif action == 'c':
                break
        except Exception as e:
            print(type(e))
            print(e.args)
            print(e)
            break
        except KeyboardInterrupt:
            break
    try:
        sock.close()
    except:
        pass
    input("Press Enter To Quit")
do()

