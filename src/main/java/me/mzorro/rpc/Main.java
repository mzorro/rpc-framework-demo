package me.mzorro.rpc;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import me.mzorro.rpc.api.Response;
import me.mzorro.rpc.api.Transporter;
import me.mzorro.rpc.api.client.Client;
import me.mzorro.rpc.impl.vertx.VertxTransporter;

/**
 * Created On 04/03 2018
 *
 * @author hzpengjunjian@corp.netease.com
 */
public class Main {

    private static final int PORT = 8012;

    private static final Transporter transporter = new VertxTransporter();

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        transporter.listen(PORT, message -> "hello " + message.toString());

        Thread.sleep(1000);

        Client client = transporter.connect(InetAddress.getLocalHost(), PORT);
        Future<Response> responseFuture = client.request("mz");
        System.out.println(responseFuture.get().getResponse());
    }
}
