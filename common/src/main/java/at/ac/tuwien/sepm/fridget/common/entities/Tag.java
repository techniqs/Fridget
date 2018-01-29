package at.ac.tuwien.sepm.fridget.common.entities;

public class Tag {

    private int id = -1;
    private String name;
    private int groupId = -1;

    public Tag() {
    }

    public Tag(String name, int groupId) {
        this.name = name;
        this.groupId = groupId;
    }

    public Tag(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        if (getId() != tag.getId()) return false;
        if (getGroupId() != tag.getGroupId()) return false;
        if (tag.getName() == null) return false;
        return getName().equals(tag.getName());
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getGroupId();
        return result;
    }

    @Override
    public String toString() {
        return getName();
    }
}
