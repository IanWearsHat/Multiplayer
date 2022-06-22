package network.packet;
// An information class to be serialized and sent from client handlers to clients, who will deserialize the class

// The second packet type is for updating a player's position and state in the (hopefully) multiplayer game.
// This second packet type would have the coordinates as well as any other state information (like attackState or something like that).
// The client that receives this would have a function in the game class (or whatever class handles players) that updates
// the senderClient's position and states on the receivingClient's end.
public class PlayerInputPacket extends Packet {

    private static final long serialVersionUID = 96024L;

    public int clientID;        // what client sent this packet
    public boolean left;
    public boolean right;
    public boolean up;
    public boolean down;
    // public boolean attackState; // placeholder boolean just to test if multiple data types are sent correctly

    public PlayerInputPacket(int clientID, boolean left, boolean right, boolean up, boolean down) {
        this.clientID = clientID;
        this.left = left;
        this.right = right;
        this.up = up;
        this.down = down;
        // this.attackState = attackState;
    }
    
}