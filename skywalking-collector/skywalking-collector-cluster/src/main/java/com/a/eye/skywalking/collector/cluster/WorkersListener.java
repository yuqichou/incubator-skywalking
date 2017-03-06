package com.a.eye.skywalking.collector.cluster;

import akka.actor.ActorRef;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <code>WorkersListener</code> listening the register message from workers
 * implementation of the {@link com.a.eye.skywalking.collector.actor.AbstractWorker}
 * and terminated message from akka cluster.
 * <p>
 * when listened register message then begin to watch the state for this worker
 * and register to {@link WorkersRefCenter}.
 * <p>
 * when listened terminate message then unregister from {@link WorkersRefCenter}.
 *
 * @author pengys5
 */
public class WorkersListener extends UntypedActor {

    private Logger logger = LogManager.getFormatterLogger(WorkersListener.class);

    public static final String WorkName = "WorkersListener";

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof WorkerListenerMessage.RegisterMessage) {
            WorkerListenerMessage.RegisterMessage register = (WorkerListenerMessage.RegisterMessage) message;
            ActorRef sender = getSender();
            getContext().watch(sender);

            logger.info("register worker of role: %s, path: %s", register.getWorkRole(), sender.toString());

            WorkersRefCenter.INSTANCE.register(sender, register.getWorkRole());
        } else if (message instanceof Terminated) {
            Terminated terminated = (Terminated) message;
            WorkersRefCenter.INSTANCE.unregister(terminated.getActor());
        } else {
            unhandled(message);
        }
    }
}
