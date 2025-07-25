package tech.aiflowy.datacenter.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tech.aiflowy.common.util.SpringContextUtil;

@Component
public class DbHandleManager {

    @Value("${aiflowy.datacenter.handler:defaultDbHandleService}")
    private String dbHandler;

    public DbHandleService getDbHandler() {
        return SpringContextUtil.getBean(dbHandler);
    }
}
