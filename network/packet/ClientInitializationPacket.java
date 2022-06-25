package network.packet;

public class ClientInitializationPacket extends Packet {
    private static final long serialVersionUID = 12L;

    private int id;

    public ClientInitializationPacket(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }
}
