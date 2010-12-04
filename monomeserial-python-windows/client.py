import OSC
import time

serverAddr = "192.168.1.103", 8080
client = OSC.OSCClient()
client.connect(serverAddr)

# send a "/sys/cable 0 top" message
msg = OSC.OSCMessage()
msg.setAddress("/sys/prefix")
msg.append(1)
msg.append("/7up");
client.send(msg)

#msg = OSC.OSCMessage()
#msg.setAddress("/sys/offset")
#msg.append(0);
#msg.append(0);
#msg.append(8)
#client.send(msg)

msg = OSC.OSCMessage()
msg.setAddress("/sys/cable")
msg.append(1)
msg.append("left")
client.send(msg)

#msg = OSC.OSCMessage()
#msg.setAddress("/testprefix/led")
#msg.append(0)
#msg.append(0)
#msg.append(1)
#client.send(msg)

