package at.ac.tuwien.sepm.fridget.common.util;

import at.ac.tuwien.sepm.fridget.common.entities.Bill;
import at.ac.tuwien.sepm.fridget.common.entities.Transaction;

import java.util.List;

public interface ShareTechnique {

    /**
     * returns a ShareTechnique object for given id
     * @param techniqueId a number which indicates the sharetechnique
     */

    static ShareTechnique fromId(int techniqueId) {
        ShareTechniqueId id = ShareTechniqueId.fromId(techniqueId);
        return id == null ? null : id.getShareTechnique();
    }

    /**
     * returns a all computed billshares for a bill
     * @param bill a bill wich should be shared
     */
    List<Transaction> computeTransactions(Bill bill);

}
