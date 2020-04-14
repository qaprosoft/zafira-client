package com.qaprosoft.zafira.log.log4j.layout;

import com.qaprosoft.zafira.log.domain.MetaInfoMessage;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class DirectJsonLayout {

    /**
     * format a given {@link MetaInfoMessage} to a string, in this case JSONField string
     *
     * @param message - direct meta info message
     * @return String representation of meta info message
     */
    public String format(MetaInfoMessage message) {
        String result = null;
        JSONObject object = new JSONObject();
        try {
            result = new LoggingEventBuilder(object).writeDirect(message)
                                                    .build();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
