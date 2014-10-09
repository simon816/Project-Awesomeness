import abc

class Packet(object, metaclass=abc.ABCMeta):
    packetToID = {}
    IDToPacket = {}

    @staticmethod
    def getNewPacket(id):
        return Packet.IDToPacket[id]()

    @staticmethod
    def registerPacket(packet, id):
        Packet.packetToID[packet] = id
        Packet.IDToPacket[id] = packet

    @abc.abstractmethod
    def writeData(self, dos):
        pass

    @abc.abstractmethod
    def readData(self, dis):
        pass

    @abc.abstractmethod
    def process(self, handler):
        pass

    def getPacketId(self):
        return Packet.packetToID[self.__class__]

    def __str__(self):
        return '[%s] ID: %d' % (self.__class__.__name__, self.getPacketId())

    def __repr__(self):
        return self.__str__()

class PacketEstablish(Packet):
    def writeData(self, dos):
        pass

    def readData(self, dis):
        self.compname = dis.readUTF()

    def process(self, handler):
        handler.prepare(self.compname)

class PacketLostConnection(Packet):
    def writeData(self, dos):
        pass

    def readData(self, dis):
        pass

    def process(self, handler):
        handler.disconnect()

class PacketIsAlive(Packet):
    def writeData(self, dos):
        pass

    def readData(self, dis):
        self.neighbours =  dis.read()

    def process(self, handler):
        handler.checkNeighbours(self.neighbours)

class PacketNeighbourUpdate(Packet):
    def __init__(self, neighbours):
        self.neighbours = neighbours

    def writeData(self, dos):
        dos.write(self.neighbours)

    def readData(self, dis):
        pass

    def process(self, handler):
        pass

class PacketSetText(Packet):
    def __init__(self, text):
        self.text = text
    def writeData(self, dos):
        dos.writeBoolean(self.text is not None)
        if self.text is not None:
            dos.writeUTF(self.text)

    def readData(self, dis):
        pass

    def process(self, handler):
        pass

class PacketShowColor(Packet):
    def __init__(self, r, g=0, b=0):
        self.r = r
        self.g = g
        self.b = b
    def writeData(self, dos):
        dos.writeBoolean(self.r is not None)
        if self.r is not None:
            dos.write(self.r)
            dos.write(self.g)
            dos.write(self.b)

    def readData(self, dis):
        pass

    def process(self, handler):
        pass

class PacketSendCircle(Packet):
    def writeData(self, dos):
        pass

    def readData(self, dis):
        pass

    def process(self, handler):
        handler.triggerNextCircle()

class PacketRandom(Packet):
    def writeData(self, dos):
        pass

    def readData(self, dis):
        self.side = dis.read()

    def process(self, handler):
        handler.doRandom(self.side)

class PacketPong(Packet):
    def __init__(self, packetType=0, pos=(None,None), vel=(None,None), sideBars=0):
        self.packetType = packetType
        self.sideBars = sideBars
        self.pos = pos
        self.vel = vel

    def writeData(self, dos):
        dos.write(self.packetType)
        if self.packetType == 1:
            dos.write(self.sideBars)
        elif self.packetType == 2:
            dos.writeInt(self.pos[0])
            dos.writeInt(self.pos[1])
            dos.writeInt(self.vel[0])
            dos.writeInt(self.vel[1])

    def readData(self, dis):
        self.packetType = dis.read()
        if self.packetType == 3:
            self.direction = dis.read()
            self.yPos = dis.readInt()
            self.vel = (dis.readInt(), dis.readInt())

    def process(self, handler):
        if self.packetType == 0:
            handler.pongGameOver()
            return
        if self.packetType == 3:
            handler.pongToNeighbour(self.direction, self.yPos, self.vel)

Packet.registerPacket(PacketEstablish, 1)
Packet.registerPacket(PacketNeighbourUpdate, 2)
Packet.registerPacket(PacketSetText, 3)
Packet.registerPacket(PacketShowColor, 4)
Packet.registerPacket(PacketSendCircle, 5)
Packet.registerPacket(PacketRandom, 6)
Packet.registerPacket(PacketPong, 7)
Packet.registerPacket(PacketIsAlive, 254)
Packet.registerPacket(PacketLostConnection, 255)
