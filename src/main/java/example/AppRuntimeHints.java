package example;

import org.apache.activemq.artemis.api.core.client.loadbalance.RoundRobinConnectionLoadBalancingPolicy;
import org.apache.activemq.artemis.core.client.ActiveMQClientLogger_impl;
import org.apache.activemq.artemis.core.client.ActiveMQClientMessageBundle_impl;
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMAcceptorFactory;
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMConnectorFactory;
import org.apache.activemq.artemis.core.server.ActiveMQMessageBundle_impl;
import org.apache.activemq.artemis.core.server.ActiveMQServerLogger_impl;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.apache.activemq.artemis.logs.AuditLogger_impl;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.AbstractReferenceCountedByteBuf;

public class AppRuntimeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("db/changelog/changelog.xml");

        hints.resources().registerPattern("analytics/models/tables/artifactDownloads.hjson");

        hints.reflection().registerType(PhysicalNamingStrategyStandardImpl.class,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        /*
         * ActiveMQ
         */
        hints.resources().registerPattern("activemq-version.properties");

        hints.reflection().registerType(RoundRobinConnectionLoadBalancingPolicy.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        hints.reflection().registerType(ActiveMQClientMessageBundle_impl.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        hints.reflection().registerType(ActiveMQClientLogger_impl.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        hints.reflection().registerType(InVMAcceptorFactory.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        hints.reflection().registerType(InVMConnectorFactory.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        hints.reflection().registerType(ActiveMQMessageBundle_impl.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        hints.reflection().registerType(ActiveMQServerLogger_impl.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        hints.reflection().registerType(ActiveMQConnectionFactory.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        hints.reflection().registerType(AuditLogger_impl.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        hints.reflection().registerType(AbstractByteBufAllocator.class, MemberCategory.INVOKE_DECLARED_METHODS);

        hints.reflection().registerType(AbstractReferenceCountedByteBuf.class, MemberCategory.DECLARED_FIELDS);

        hints.reflection().registerType(
                TypeReference.of("org.jctools.queues.BaseMpscLinkedArrayQueueColdProducerFields"),
                MemberCategory.DECLARED_FIELDS);

        hints.reflection().registerType(TypeReference.of("org.jctools.queues.BaseMpscLinkedArrayQueueConsumerFields"),
                MemberCategory.DECLARED_FIELDS);

        hints.reflection().registerType(TypeReference.of("org.jctools.queues.BaseMpscLinkedArrayQueueProducerFields"),
                MemberCategory.DECLARED_FIELDS);
    }
}