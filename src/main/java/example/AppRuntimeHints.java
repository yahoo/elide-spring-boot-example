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
import org.eclipse.jetty.ee10.websocket.jakarta.common.JakartaWebSocketSession;
import org.eclipse.jetty.ee10.websocket.jakarta.common.decoders.BooleanDecoder;
import org.eclipse.jetty.ee10.websocket.jakarta.common.decoders.ByteArrayDecoder;
import org.eclipse.jetty.ee10.websocket.jakarta.common.decoders.ByteBufferDecoder;
import org.eclipse.jetty.ee10.websocket.jakarta.common.decoders.CharacterDecoder;
import org.eclipse.jetty.ee10.websocket.jakarta.common.decoders.DoubleDecoder;
import org.eclipse.jetty.ee10.websocket.jakarta.common.decoders.FloatDecoder;
import org.eclipse.jetty.ee10.websocket.jakarta.common.decoders.IntegerDecoder;
import org.eclipse.jetty.ee10.websocket.jakarta.common.decoders.LongDecoder;
import org.eclipse.jetty.ee10.websocket.jakarta.common.decoders.ShortDecoder;
import org.eclipse.jetty.ee10.websocket.jakarta.common.decoders.StringDecoder;
import org.eclipse.jetty.ee10.websocket.jakarta.common.messages.DecodedBinaryMessageSink;
import org.eclipse.jetty.ee10.websocket.jakarta.common.messages.DecodedBinaryStreamMessageSink;
import org.eclipse.jetty.ee10.websocket.jakarta.common.messages.DecodedTextMessageSink;
import org.eclipse.jetty.ee10.websocket.jakarta.common.messages.DecodedTextStreamMessageSink;
import org.eclipse.jetty.util.ClassMatcher;
import org.eclipse.jetty.websocket.core.messages.PartialByteArrayMessageSink;
import org.eclipse.jetty.websocket.core.messages.PartialByteBufferMessageSink;
import org.eclipse.jetty.websocket.core.messages.PartialStringMessageSink;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;

import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import liquibase.database.LiquibaseTableNamesFactory;
import liquibase.parser.SqlParserFactory;
import liquibase.report.ShowSummaryGeneratorFactory;
import liquibase.ui.LoggerUIService;

public class AppRuntimeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.resources().registerPattern("db/changelog/changelog.xml");

        hints.resources().registerPattern("analytics/models/tables/artifactDownloads.hjson");

        hints.reflection().registerType(PhysicalNamingStrategyStandardImpl.class,
                MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        /*
         * Logback Access Spring
         */
        hints.resources().registerPattern("dev/akkinoc/spring/boot/logback/access/logback-access-spring.xml");

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

        /*
         * Spring Framework springframework.jms.connection.SingleConnectionFactory 
         */
        hints.proxies().registerJdkProxy(jakarta.jms.Connection.class, jakarta.jms.QueueConnection.class,
                jakarta.jms.TopicConnection.class);

        /*
         * Jetty 12 WebSocket
         *
         * @see org.eclipse.jetty.ee10.websocket.jakarta.common.JakartaWebSocketFrameHandlerFactory
         * @see https://github.com/oracle/graalvm-reachability-metadata/blob/master/metadata/org.eclipse.jetty/jetty-server/12.0.1/reflect-config.json
         */
        hints.reflection().registerType(JakartaWebSocketSession.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);

        hints.reflection().registerType(BooleanDecoder.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(ByteArrayDecoder.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(ByteBufferDecoder.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(CharacterDecoder.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(DoubleDecoder.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(FloatDecoder.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(IntegerDecoder.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(LongDecoder.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(ShortDecoder.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(StringDecoder.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);

        hints.reflection().registerType(DecodedTextMessageSink.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(DecodedBinaryMessageSink.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(DecodedTextStreamMessageSink.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(DecodedBinaryStreamMessageSink.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(PartialStringMessageSink.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(PartialByteBufferMessageSink.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(PartialByteArrayMessageSink.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);

        /*
         * @see https://github.com/oracle/graalvm-reachability-metadata/pull/495/files 
         */
        hints.reflection().registerType(ClassMatcher.ByPackageOrName.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(ClassMatcher.ByLocationOrModule.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        
        /*
         * Liquibase
         * @see https://github.com/oracle/graalvm-reachability-metadata/issues/431
         */
        hints.reflection().registerType(LoggerUIService.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(SqlParserFactory.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(LiquibaseTableNamesFactory.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        hints.reflection().registerType(ShowSummaryGeneratorFactory.class, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
    }
}