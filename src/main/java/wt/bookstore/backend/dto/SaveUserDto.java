package wt.bookstore.backend.dto;

/**
 * Data Transfer Object for the {@link wt.bookstore.backend.domains.User} class that is sent from the frontend to the
 * backend. The fields in this class should contain the information needed to create a user object.
 */
public class SaveUserDto {

    private String firstName;

    private String lastName;

    private String eMailAddress;

    private boolean admin;

    public String geteMailAddress() {
        return eMailAddress;
    }

    public void seteMailAddress(String eMailAddress) {
        this.eMailAddress = eMailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

<<<<<<< Updated upstream
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
=======
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }
>>>>>>> Stashed changes
}
