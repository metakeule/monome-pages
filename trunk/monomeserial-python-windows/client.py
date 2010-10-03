import OSC
import time

serverAddr = "localhost", 8080
client = OSC.OSCClient()
client.connect(serverAddr)

# send a "/sys/cable 0 top" message
msg = OSC.OSCMessage()
msg.setAddress("/sys/cable")
msg.append(1);
msg.append("right");
client.send(msg)