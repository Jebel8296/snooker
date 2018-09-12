package com.mstx.framework.task;

import com.mstx.framework.task.task.PayFeeSubmitTask;
import com.mstx.framework.task.worker.RechargeTaskVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = {"classpath:application.properties"})
public class TaskApplication {
    private static Logger logger = LoggerFactory.getLogger(TaskApplication.class);

    @Autowired
    Environment environment;

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
        TaskApplication taskApplication = applicationContext.getBean(TaskApplication.class);
        ClusterManager clusterManager = new ZookeeperClusterManager();
        VertxOptions vertxOptions = new VertxOptions();
        vertxOptions.setClustered(true).setClusterManager(clusterManager).setClusterHost(taskApplication.getClusterHost()).setClusterPublicHost(taskApplication.getClusterPublicHost());
        Vertx.clusteredVertx(vertxOptions, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                DeploymentOptions option = new DeploymentOptions();
                vertx.deployVerticle(applicationContext.getBean(RechargeTaskVerticle.class), option.setWorker(true));
                vertx.deployVerticle(applicationContext.getBean(PayFeeSubmitTask.class), option.setWorker(true));
                logger.info("TaskApplication start success.");
            } else {
                logger.error("TaskApplication start failed.");
            }
        });
    }

    public String getClusterPublicHost() {
        return environment.getProperty("cluster.public.host", "localhost");
    }

    public String getClusterHost() {
        return environment.getProperty("cluster.host", "localhost");
    }
}
