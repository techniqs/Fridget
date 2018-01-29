package at.ac.tuwien.sepm.fridget.common.entities;

import java.util.HashMap;
import java.util.Map;

public enum EventType {

    INVITATION_SENT(1),
    INVITATION_ACCEPTED(2),
    MEMBER_LEFT(3),
    MODIFY_BILL(4),
    CREATE_BILL(5),
    PENDING_INVITATION(6);

    private static final Map<Integer, EventType> map = new HashMap<>();

    static {
        for (EventType eventType : EventType.values()) {
            map.put(eventType.getId(), eventType);
        }
    }

    private int id;

    EventType(int id) {
        this.id = id;
    }

    public static EventType fromId(int id) {
        return map.get(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
