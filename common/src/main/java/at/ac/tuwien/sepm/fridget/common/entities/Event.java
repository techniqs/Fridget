package at.ac.tuwien.sepm.fridget.common.entities;

import java.time.LocalDateTime;

public class Event {

    private int id;
    private EventType type;
    private boolean undone;
    private LocalDateTime occuredAt;
    private String description;
    private User source;
    private User target;
    private Bill billBefore;
    private Bill billAfter;
    private Group group;

    public Event() {
    }

    public Event(EventType type, boolean undone, LocalDateTime occuredAt, String description, User source, User target, Bill billBefore, Bill billAfter, Group group) {
        this.type = type;
        this.undone = undone;
        this.occuredAt = occuredAt;
        this.description = description;
        this.source = source;
        this.target = target;
        this.billBefore = billBefore;
        this.billAfter = billAfter;
        this.group = group;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public boolean isUndone() {
        return undone;
    }

    public void setUndone(boolean undone) {
        this.undone = undone;
    }

    public LocalDateTime getOccuredAt() {
        return occuredAt;
    }

    public void setOccuredAt(LocalDateTime occuredAt) {
        this.occuredAt = occuredAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getSource() {
        return source;
    }

    public void setSource(User source) {
        this.source = source;
    }

    public User getTarget() {
        return target;
    }

    public void setTarget(User target) {
        this.target = target;
    }

    public Bill getBillBefore() {
        return billBefore;
    }

    public void setBillBefore(Bill billBefore) {
        this.billBefore = billBefore;
    }

    public Bill getBillAfter() {
        return billAfter;
    }

    public void setBillAfter(Bill billAfter) {
        this.billAfter = billAfter;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        Event otherEvent = (Event) other;
        if (id == otherEvent.getId())
            return true;
        else
            return false;
    }
}
