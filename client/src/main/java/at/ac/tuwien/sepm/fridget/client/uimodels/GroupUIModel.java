package at.ac.tuwien.sepm.fridget.client.uimodels;

import at.ac.tuwien.sepm.fridget.common.entities.Group;
import javafx.beans.property.*;

import java.util.List;

public class GroupUIModel extends Group {
    private IntegerProperty id;
    private StringProperty name;
    private ObjectProperty<List<Integer>> members;
    private IntegerProperty baseCurrencyID;


    public GroupUIModel() {
        this.id = new SimpleIntegerProperty(-1);
        this.name = new SimpleStringProperty();
        this.members = new SimpleObjectProperty<>();
        this.baseCurrencyID = new SimpleIntegerProperty();
    }

    @Override
    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    @Override
    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @Override
    public List<Integer> getMembers() {
        return members.get();
    }

    public ObjectProperty<List<Integer>> membersProperty() {
        return members;
    }

    public void setMembers(List<Integer> members) {
        this.members.set(members);
    }

    public void addMember(int member) {
        if (member > 0) {
            if (this.members.get() != null) {

                this.members.get().add(member);

            }
        }
    }

    public int getBaseCurrencyID() {
        return baseCurrencyID.get();
    }

    public IntegerProperty baseCurrencyIDProperty() {
        return baseCurrencyID;
    }

    public void setBaseCurrencyID(int baseCurrencyID) {
        this.baseCurrencyID.set(baseCurrencyID);
    }
}
