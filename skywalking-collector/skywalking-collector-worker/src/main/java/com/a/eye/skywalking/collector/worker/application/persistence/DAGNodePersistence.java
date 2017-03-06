package com.a.eye.skywalking.collector.worker.application.persistence;

import com.a.eye.skywalking.collector.actor.AbstractWorkerProvider;
import com.a.eye.skywalking.collector.worker.PersistenceWorker;
import com.a.eye.skywalking.collector.worker.WorkerConfig;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

/**
 * @author pengys5
 */
public class DAGNodePersistence extends PersistenceWorker<DAGNodePersistence.Metric> {

    private Logger logger = LogManager.getFormatterLogger(DAGNodePersistence.class);

    @Override
    public String esIndex() {
        return "application";
    }

    @Override
    public String esType() {
        return "dag_node";
    }

    @Override
    public void analyse(Object message) throws Throwable {
        if (message instanceof Metric) {
            Metric metric = (Metric) message;
            JsonObject propertyJsonObj = new JsonObject();
            propertyJsonObj.addProperty("code", metric.code);
            propertyJsonObj.addProperty("component", metric.component);
            propertyJsonObj.addProperty("layer", metric.layer);

            logger.debug("dag node: %s", propertyJsonObj.toString());
            putData(metric.code, propertyJsonObj);
        } else {
            logger.error("message unhandled");
        }
    }

    public static class Factory extends AbstractWorkerProvider {
        public static Factory INSTANCE = new Factory();

        @Override
        public Class workerClass() {
            return DAGNodePersistence.class;
        }

        @Override
        public int workerNum() {
            return WorkerConfig.Worker.DAGNodePersistence.Num;
        }
    }

    public static class Metric implements Serializable {
        private final String code;
        private final String component;
        private final String layer;

        public Metric(String code, String component, String layer) {
            this.code = code;
            this.component = component;
            this.layer = layer;
        }
    }
}
