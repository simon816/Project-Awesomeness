import time

class Client:
    def __init__(self, compnum):
        self.compnum = compnum
    def setText(self, text):
        print(self.compnum, 'text', text)

clients = {
    #0 : Client(0),
    #1 : Client(1),
    2 : Client(2),
    3 : Client(3),
    4 : Client(4),
    5 : Client(5),
    6 : Client(6),
    7 : Client(7),
    8 : Client(8),
    9 : Client(9),
    10 : Client(10),
    11 : Client(11),
    12 : Client(12),
    13 : Client(13)
}
text = "text"
finished = []

keys = list(clients.keys())
keys.sort()
keys.reverse()
print(keys)

done = []
for updates in range(1, len(text) + 1):
    for i in range(updates, 0, -1):
        clients[keys[i - 1]].setText(text[updates - i])
    print(updates)
done.append(keys[0])

print(done)

shift = 0
for char in text:
    shift += 1
    clients[keys[shift]].setText(char)


##for i in range(len(text)):
##    clients[keys[i]].setText(text[i])

##done = []
##pdone = []
##for i in range(len(keys)):
##    for j in range(len(text)):
##        lkeys = keys[i:i + j + 2]
##        ckeys = []
##        for k in lkeys:
##            if k in done:
##                continue
##            if k in pdone:
##                done.append(k)
##            ckeys.append(k)
##        if len(ckeys) == 0:
##            continue
##        print('',ckeys)
##        pdone.append(len(keys) - i + 1)
##    print (len(keys) - i + 1)
##for updates in range(1, max(len(clients), len(text)) * 2):
##    for i in range(updates):
##        print(updates, len(clients) - i)
##        continue
##        if not cnum in clients or cnum in finished:
##            continue
##        if i < len(text):
##            t = text[i]
##        else:
##            t = None
##            finished.append(cnum)
##        clients[cnum].setText(t)
##    print()

"""
 3 -> t

 3 -> e
 2 -> t

 3 -> x
 2 -> e
 1 -> t

 3 -> t
 2 -> x
 1 -> e
 0 -> t

"""
