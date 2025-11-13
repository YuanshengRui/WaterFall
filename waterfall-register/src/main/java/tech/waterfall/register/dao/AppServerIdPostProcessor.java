package tech.waterfall.register.dao;

import java.net.UnknownHostException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import tech.waterfall.register.exception.BaseException;

public class AppServerIdPostProcessor implements BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(AppServerIdPostProcessor.class);
    private AppServerIdAccessor appServerIdAccessor;
    private int appServerId;

    public AppServerIdPostProcessor(AppServerIdAccessor appServerIdAccessor, int appServerId) {
        this.appServerIdAccessor = appServerIdAccessor;
        this.appServerId = appServerId;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof AppServerIdAware) {
            try {
                ((AppServerIdAware)bean).setAppServerId(this.getAppServerId());
            } catch (Exception e) {
                throw new BaseException("can not retrieve appServerId", e);
            }
        }

        return bean;
    }

    private int getAppServerId() throws UnknownHostException {
        if (this.appServerId == 0) {
            this.appServerId = ((Long)this.appServerIdAccessor.getAppServerId().block()).intValue();
        }

        return this.appServerId;
    }
}
