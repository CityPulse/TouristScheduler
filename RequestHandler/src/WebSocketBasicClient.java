import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;

class WebSocketBasicClient {

    private String webSocketEndPoint;
    private String payload;
    private String webSocketResponse = null;

    public WebSocketBasicClient(String webSocketEndPoint, String payload) {
        super();
        this.webSocketEndPoint = webSocketEndPoint;
        this.payload = payload;
    }

    public String sendWebsocketRequest() {

        final CountDownLatch messageLatch = new CountDownLatch(1);

        ClientEndpointConfig cec = ClientEndpointConfig.Builder.create()
                .build();

        ClientManager clientManager = ClientManager.createClient();

        Endpoint clientEndpoint = new Endpoint() {

            @Override
            public void onOpen(Session session, EndpointConfig config) {

                session.addMessageHandler(new MessageHandler.Whole<String>() {

                    public void onMessage(String message) {

                        webSocketResponse = message;

                        messageLatch.countDown();

                    }

                });

                try {
                    session.getBasicRemote().sendText(payload);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        };

        try {
            Session session = clientManager.connectToServer(clientEndpoint, cec, new URI(
                    webSocketEndPoint));

            session.setMaxIdleTimeout(200000000);

            System.out.println("Waiting the answer");

            long initial_time = System.currentTimeMillis();
            messageLatch.await(120, TimeUnit.SECONDS);

            System.out.println("Waiting time for the response " + (System.currentTimeMillis() - initial_time));

            session.close();

            System.out.println("Finished the waiting period");

        } catch (DeploymentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return webSocketResponse;

    }

}