package at.ac.tuwien.sepm.fridget.common.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Group {

    private int id;
    private String name;
    private List<Integer> memberIds;
    private int baseCurrencyID;

    public Group() {
        this(-1, null, -1, 1);
    }

    public Group(int id) {
        this(id, null, -1, 1);
    }

    public Group(int id, String name, int creator, int baseCurrencyID) {
        this.id = id;
        this.name = name;
        addMembers(creator);
        this.baseCurrencyID = baseCurrencyID;
    }

    public Group(int id, String name, int baseCurrencyId) {
        this.id = id;
        this.name = name;
        this.baseCurrencyID = baseCurrencyId;
    }

    public Group(String name, int creator) {
        this.name = name;
        addMembers(creator);
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

    public List<Integer> getMembers() {
        return memberIds;
    }

    public void setMembers(List<Integer> members) {
        this.memberIds = members;
    }

    public void addMembers(int member) {
        if (member > 0) {
            if (this.memberIds == null) {
                this.memberIds = new LinkedList<>();
            }
            this.memberIds.add(member);
        }
    }

    public int getBaseCurrencyID() {
        return baseCurrencyID;
    }

    public void setBaseCurrencyID(int baseCurrencyID) {
        this.baseCurrencyID = baseCurrencyID;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return id == group.id &&
            Objects.equals(name, group.name) &&
            Objects.equals(memberIds, group.memberIds);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, memberIds);
    }
}
