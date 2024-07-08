package Server;

import Shared.Models.Category;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;

public class Recommendation {
    private Long channelId;

    public Recommendation(Long channelId) {
        this.channelId = channelId;
    }

    private HashMap<Category, Double> dataConversion(HashMap<Category, Integer>  data, double percentage) throws Exception {
        HashMap<Category , Double> result = new HashMap<>();
        for (Map.Entry<Category , Integer> set : data.entrySet()) {
            result.put(set.getKey(), (set.getValue() / (double) data.get("sum")) * percentage);

        }
        return result;
    }

    private Date calculateStartDate(Date endDate, int field, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(field, amount);
        return calendar.getTime();
    }
}
