package at.ac.tuwien.sepm.fridget.common.entities;

public class User {

    private int id;
    private String name;
    private String email;
    private String password;

    public User() {
        this(-1, null, null, null);
    }

    public User(int id) {
        this(id, null, null, null);
    }

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof User) {
            User user = (User) obj;
            return user.getId() == this.id;
        }
        return false;
    }
}
