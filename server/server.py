from jio import *
from packets import *
from sockio import *
import threaded_server
import socketserver
import time

class ClientManager(socketserver.BaseRequestHandler):
    clients = {}
    def handle(self):
        self.newClient(self.request)
        self.server.shutdown_request(self.request)

    def newClient(self, requ):
        client = ClientConnection(requ, self)
        client.run()

    def _updateNeighbours(self, compnum, action):
        n = 0
        left, right = self.getNeighbours(compnum)
        if left:
            left.setNeighbours(action(left, 2))
            n += 1
        if right:
            right.setNeighbours(action(right, 1))
            n += 2
        return n

    def getNeighbours(self, compnum):
        if compnum < 0:
            # -ve have no neighbours
            return None, None
        left, right = None, None
        keys = list(self.clients.keys())
        if compnum - 1 in keys:
            left = self.clients[compnum - 1]
        if compnum + 1 in keys:
            right = self.clients[compnum + 1]
        return left, right
        
        

    def clientConnected(self, client, compnum):
        self.clients[compnum] = client
        return self._updateNeighbours(compnum, lambda client, side:
                                      client.neighbours | side)

    def clientDisconnected(self, compnum):
        self._updateNeighbours(compnum, lambda client, side:
                                      (client.neighbours | side) - side)
        del self.clients[compnum]


class ClientConnection:
    def __init__(self, requ, manager):
        self.dis = DataInputStream(SocketInputStream(requ))
        self.dos = DataOutputStream(SocketOutputStream(requ))
        self.alive = True
        self.manager = manager
        self.compname = ""
        self.compnum = None
        self.neighbours = 0

    def run(self):
        while self.alive:
            try:
                if not self.readPackets():
                    break
            except Exception as e:
                import traceback
                traceback.print_exc()
                break
        if self.compnum is not None:
            self.manager.clientDisconnected(self.compnum)

    def readPackets(self):
        id = self.dis.read()
        p = Packet.getNewPacket(id)
        print(self.compname, 'Incomming', p)
        p.readData(self.dis)
        p.process(self)
        return True

    def sendPackets(self, *packets):
        print(self.compname, 'Outgoing', packets)
        self.dos.write(len(packets))
        for packet in packets:
            self.dos.write(packet.getPacketId())
            packet.writeData(self.dos)

    def prepare(self, compname):
        self.compname = compname
        threading.current_thread().name = "Thread-" + self.compname
        try:
            compnum = int(compname.split('-')[-1])
        except:
            compnum = -len(self.manager.clients)
        print("Computer Connected:", compname)
        self.compnum = compnum
        self.neighbours = self.manager.clientConnected(self, compnum)
        self.sendPackets(PacketEstablish())
        def ensureNeighbours(manager, cl, num):
            time.sleep(1)
            manager.clientConnected(cl, num)
        launchThread(ensureNeighbours, self.manager, self, compnum)
        self.sendPackets(PacketNeighbourUpdate(self.neighbours))

    def setNeighbours(self, n):
        self.neighbours = n
        self.sendPackets(PacketNeighbourUpdate(n))

    def disconnect(self):
        self.alive = False

    def triggerNextCircle(self):
        left, _ = self.manager.getNeighbours(self.compnum)
        if left:
            left.sendPackets(PacketSendCircle())

    def doRandom(self, side):
        left, right = self.manager.getNeighbours(self.compnum)
        if side == 1 and left:
            left.sendPackets(PacketRandom())
        elif side == 2 and right:
            right.sendPackets(PacketRandom())

    def checkNeighbours(self, n):
        left, right = self.manager.getNeighbours(self.compnum)
        newn = 0
        if left: newn += 1
        if right: newn += 2
        if n != newn:
            self.setNeighbours(newn)

    def pongGameOver(self):
        print ("game over")
        for client in self.manager.clients.values():
            client.sendPackets(PacketPong(0))

    def pongToNeighbour(self, neighbour, yPos, vel):
        left, right = self.manager.getNeighbours(self.compnum)
        reciever = None
        if neighbour == 1:
            reciever = left
            xPos = -1
        elif neighbour == 2:
            reciever = right
            xPos = 0
        if not reciever:
            return False
        reciever.sendPackets(PacketPong(2, (xPos, yPos), vel))
        return True
        

def getAndSaveIP():
    ip = socket.gethostbyname(socket.gethostname())
    port = 9001
    import http.client
    con = http.client.HTTPConnection('simon816.hostzi.com', 80)
    con.request('POST', '/dev/awesome.php?action=updateIP',
                'ip=' + ip + '%3A' + str(port),
                {"Content-Type": "application/x-www-form-urlencoded"})
    con.getresponse()
    con.close()
    return ip, port

def getServer():
    #HOST, PORT = ('localhost', 9001)
    HOST, PORT = getAndSaveIP()
    server = threaded_server.ThreadingTCPServer((HOST, PORT), ClientManager)
    server.allow_reuse_address = True
    return server

def run():
    server = getServer()
    try:
        server.serve_forever()
    except (KeyboardInterrupt, SystemExit):
        server.stop()
        if input("Press enter to restart."):
            run()

def scrollText(text):
    dynUpdate = False
    keys = list(ClientManager.clients.keys())
    keys.sort()
    keys.reverse()
    finished = []
    for updates in range(1, (max(len(keys), len(text)) * 2) + 1):
        for i in range(updates):
            if dynUpdate:
                keys = list(ClientManager.clients.keys())
                keys.sort()
                keys.reverse()
            cnum = updates - 1 - i
            if cnum in finished:
                continue
            if i < len(text):
                t = text[i]
            else:
                t = None
                finished.append(cnum)
            try:
                keys[cnum]
            except IndexError:
                continue
            ClientManager.clients[keys[cnum]].sendPackets(PacketSetText(t))
        time.sleep(0.5)

def sendColor(color):
    if color is None:
        for client in ClientManager.clients.values():
            client.sendPackets(PacketShowColor(None))
        return
    try:
        r = int(color[0:2], 16)
        g = int(color[2:4], 16)
        b = int(color[4:6], 16)
    except:
        return
    for client in ClientManager.clients.values():
        client.sendPackets(PacketShowColor(r, g, b))
    

def showColor(tcolor):
    colors = tcolor.split(',')
    for color in colors:
        spl = color.split(':')
        delay = 5
        if len(spl) == 2:
            delay = float(spl[1])
        sendColor(spl[0])
        time.sleep(delay)
    sendColor(None)

def showCircle():
    clients = ClientManager.clients
    if len(clients) > 0:
        keys = list(clients.keys())
        keys.sort()
        clients[keys[-1]].sendPackets(PacketSendCircle())

def rand():
    clients = ClientManager.clients
    if len(clients) > 0:
        keys = list(clients.keys())
        import random
        r = random.randrange(0, len(keys))
        clients[keys[r]].sendPackets(PacketRandom())

def pong():
    xPos = tkinter.simpledialog.askinteger("Configure", "Start X position", initialvalue=100)
    if xPos is None:
        return
    yPos = tkinter.simpledialog.askinteger("Configure", "Start Y position", initialvalue=100)
    if yPos is None:
        return
    velX = tkinter.simpledialog.askinteger("Configure", "X velocity", initialvalue=10)
    if velX is None:
        return
    velY = tkinter.simpledialog.askinteger("Configure", "Y velocity", initialvalue=10)
    if velY is None:
        return
    for client in ClientManager.clients.values():
        l, r = client.manager.getNeighbours(client.compnum)
        n = 3
        if r: n ^= 2
        if l: n ^= 1
        client.sendPackets(PacketPong(1, sideBars=n))
    c = ClientManager.clients
    if not len(c):
        return
    k = list(c.keys())
    k.sort()
    c[k[0]].sendPackets(PacketPong(2, (xPos, yPos), (velX, velY)))

def launchThread(fn, *args):
    thread = threading.Thread(target=fn, args=args)
    thread.daemon = True
    thread.start()
    return thread

def rungui():
    server = getServer()
    launchThread(server.serve_forever)
    def text():
        text = tkinter.simpledialog.askstring("Show Text", "Enter text")
        if text is None or text == '':
            return
        launchThread(scrollText, text)
    def color():
        color = tkinter.simpledialog.askstring("Set Color", "Enter RRGGBB")
        if color is None or color == '':
            return
        launchThread(showColor, color)
    def circle():
        launchThread(showCircle)
    root = Tk()
    root.title("Awesomeness Controller")
    root.geometry("200x200")
    frame = Frame(root)
    Button(frame, text="Scroll Text", command=text).pack()
    Button(frame, text="Set Color", command=color).pack()
    Button(frame, text="Fire Circle", command=circle).pack()
    Button(frame, text="Random Thing", command=rand).pack()
    Button(frame, text="Pong", command=pong).pack()
    frame.pack(padx=20, pady=20)
    root.mainloop()
    
if __name__ == "__main__":
    #run()
    from tkinter import *
    import tkinter.simpledialog
    import threading
    rungui()
