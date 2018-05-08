package me.mzorro.rpc.test;

import java.lang.reflect.Method;
import java.net.InetAddress;

import org.junit.Test;

import me.mzorro.rpc.api.Invocation;
import me.mzorro.rpc.api.Response;
import me.mzorro.rpc.api.remote.RequestHandler;
import me.mzorro.rpc.api.remote.Transporter;
import me.mzorro.rpc.api.remote.client.Client;
import me.mzorro.rpc.api.remote.server.Server;
import me.mzorro.rpc.impl.remote.aio.AIOTransporter;
import me.mzorro.rpc.impl.remote.nio.NIOTransporter;
import me.mzorro.rpc.impl.remote.vertx.VertxTransporter;
import me.mzorro.rpc.test.demo.impl.DemoServiceImpl;

/**
 * Created On 05/07 2018
 *
 * @author mzorrox@gmail.com
 */
public class DemoTest {

    protected int port = 8909;

    private static class Demo implements RequestHandler {
        private Object serviceRef = new DemoServiceImpl();

        protected final Transporter transporter;

        public Demo(Transporter transporter) {
            this.transporter = transporter;
        }

        @Override
        public Object accept(Object message) {
            if (message instanceof Invocation) {
                Invocation inv = (Invocation) message;
                try {
                    Method method = serviceRef.getClass().getMethod(inv.getMethodName(), inv.getArgTypes());
                    return method.invoke(serviceRef, inv.getArgs());
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }
            throw new UnsupportedOperationException();
        }
    }

    private void test(Transporter transporter) throws Throwable {
        Demo demo = new Demo(transporter);
        Server server = demo.transporter.listen(port, demo);
        System.out.println("server started:" + server.recreate());
        Client client = demo.transporter.connect(InetAddress.getLocalHost(), port);
        Invocation invocation = new Invocation();
        invocation.setMethodName("sayHello");
        invocation.setArgTypes(new Class<?>[]{ String.class });
        invocation.setArgs(new Object[]{ "mz" });
        Response response = client.request(invocation).get();
        System.out.println("response received:" + response.recreate());
        server.close();
    }

    @Test
    public void testNIO() throws Throwable {
        test(new NIOTransporter());
    }

    @Test
    public void testAIO() throws Throwable {
        test(new AIOTransporter());
    }

    @Test
    public void testVertx() throws Throwable {
        test(new VertxTransporter());
    }
}
