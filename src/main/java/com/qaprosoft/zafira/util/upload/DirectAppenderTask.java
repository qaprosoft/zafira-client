package com.qaprosoft.zafira.util.upload;

import com.qaprosoft.zafira.listener.ZafiraEventRegistrar;
import com.qaprosoft.zafira.log.BaseAppenderTask;
import com.qaprosoft.zafira.log.domain.MetaInfoMessage;
import com.qaprosoft.zafira.log.log4j.layout.DirectJsonLayout;
import com.qaprosoft.zafira.log.log4j.level.MetaInfoLevel;

final class DirectAppenderTask extends BaseAppenderTask<MetaInfoMessage> {

    private final MetaInfoMessage message;
    private final DirectJsonLayout layout;

    DirectAppenderTask(MetaInfoMessage message, DirectJsonLayout layout) {
        this.message = message;
        this.layout = layout;
    }

    @Override
    protected String getTestId() {
        String ciTestIdHeaderValue = message.getHeaders().get("CI_TEST_ID");
        return ciTestIdHeaderValue != null ? ciTestIdHeaderValue : ZafiraEventRegistrar.getThreadCiTestId();
    }

    @Override
    protected String getJsonPayload() {
        return layout.format(message);
    }

    @Override
    protected String getEventType() {
        return MetaInfoLevel.META_INFO.toString();
    }

    @Override
    protected MetaInfoMessage getEventObject() {
        return message;
    }
}
