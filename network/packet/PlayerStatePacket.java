package network.packet;

// An information class to be serialized and sent from client handlers to clients, who will deserialize the class

// The first packet type is just a message packet from the server, whether it's from another client or from the server itself.
// The client that receives this type will just print out the message to the player.
public class PlayerStatePacket extends Packet {

    private static final long serialVersionUID = 5455552431L;

    public int x;
    public int y;

    public PlayerStatePacket(int x, int y) {
        this.x = x;
        this.y = y;
    }
}