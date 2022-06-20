package network.packet;

import java.io.Serializable;

// An information class to be serialized and sent from client handlers to clients, who will deserialize the class

// The plan is for two types of packets to ever be sent (although they could be changed in the future).

public class Packet implements Serializable {
    private static final long serialVersionUID = 4L;
}