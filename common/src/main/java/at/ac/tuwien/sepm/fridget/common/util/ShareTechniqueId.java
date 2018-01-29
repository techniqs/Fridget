package at.ac.tuwien.sepm.fridget.common.util;

import java.util.HashMap;
import java.util.Map;

public enum ShareTechniqueId {
    EVEN(1, new EvenShareTechnique()), PERCENTAGE(2, new PercentageShareTechnique()), MANUAL(3, new ManualShareTechnique());

    private static final Map<Integer, ShareTechniqueId> map = new HashMap<>();

    static {
        for (ShareTechniqueId shareTechniqueId : ShareTechniqueId.values()) {
            map.put(shareTechniqueId.getId(), shareTechniqueId);
        }
    }


    private final int id;
    private final ShareTechnique shareTechnique;

    ShareTechniqueId(int id, ShareTechnique shareTechnique) {
        this.id = id;
        this.shareTechnique = shareTechnique;
    }

    public static ShareTechniqueId fromId(int id) {
        return map.get(id);
    }

    public int getId() {
        return id;
    }

    public ShareTechnique getShareTechnique() {
        return shareTechnique;
    }

}
