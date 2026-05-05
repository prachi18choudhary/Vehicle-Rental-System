import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client/dist/sockjs";

let client = null;

export function connectStomp({ token, userId, onMessage }) {
  if (client && client.active) return client;
  client = new Client({
    webSocketFactory: () => new SockJS(`http://localhost:8085/ws`),
    connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
    reconnectDelay: 5000,
    debug: () => {},
    onConnect: () => {
      client.subscribe(`/topic/user.${userId}`, (msg) => {
        try {
          onMessage(JSON.parse(msg.body));
        } catch (e) {
          console.warn("Failed to parse WS message", e);
        }
      });
    },
  });
  client.activate();
  return client;
}

export function disconnectStomp() {
  if (client) {
    client.deactivate();
    client = null;
  }
}
