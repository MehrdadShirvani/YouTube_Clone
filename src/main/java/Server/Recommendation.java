package Server;

import Shared.Models.Category;

import java.util.HashMap;
import java.util.Map;

public class Recommendation {
    private Long channelId;

    public Recommendation(Long channelId) {
        this.channelId = channelId;
    }

    public HashMap<Category, Double> dataConversion(HashMap<Category, Integer>  data, double percentage) throws Exception {
        HashMap<Category , Double> result = new HashMap<>();
        for (Map.Entry<Category , Integer> set : data.entrySet()) {
            result.put(set.getKey(), (set.getValue() / (double) data.get("sum")) * percentage);

        }
        return result;
    }
}
