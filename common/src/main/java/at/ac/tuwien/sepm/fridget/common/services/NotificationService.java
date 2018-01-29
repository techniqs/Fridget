package at.ac.tuwien.sepm.fridget.common.services;

import at.ac.tuwien.sepm.fridget.common.entities.Group;
import at.ac.tuwien.sepm.fridget.common.entities.User;

public interface NotificationService {
    /**
     * Return and argumentparameters are not final
     */

    void showAllGroupChanges();

    void showAllBillChanges();

    void notificationsForMember();

    void debtInformation(Group group, User user);

}
