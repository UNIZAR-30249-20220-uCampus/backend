package es.ucampus.demo.adapter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
//import org.postgresql.core.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import dtoObjects.entity.EspacioDTO;
import dtoObjects.entity.ReservaDTO2;
import dtoObjects.valueObject.CriteriosBusquedaDTO;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.List;

public class AdapterReservas {

	@Autowired
	private final static String QUEUE_ENVIAR = "WebASpringReservas";
	private final static String QUEUE_RECIBIR = "SpringAWebReservas";
	private final static String ENV_AMQPURL_NAME = "CLOUDAMQP_URL";
	private final static String CredencialCloudAMQP = "amqp://laxmuumj:ivRgGMHAsnl088kdlEWhskufGJSGsbkf@stingray.rmq.cloudamqp.com/laxmuumj";
	private Connection connection;
	private Channel channel;
	private QueueingConsumer consumer;

	public AdapterReservas() throws IOException {
		// Conexión al broker RabbitMQ broker (prueba en la URL de
		// la variable de entorno que se llame como diga ENV_AMQPURL_NAME
		// o sino en localhost)
		ConnectionFactory factory = new ConnectionFactory();
		String amqpURL = System.getenv().get(ENV_AMQPURL_NAME) != null ? System.getenv().get(ENV_AMQPURL_NAME)
				: CredencialCloudAMQP;
		try {
			factory.setUri(amqpURL);
		} catch (Exception e) {
			System.out.println(" [*] AQMP broker not found in " + amqpURL);
			System.exit(-1);
		}
		System.out.println(" [*] AQMP broker found in " + amqpURL);
		connection = factory.newConnection();
		// Con un solo canal
		channel = connection.createChannel();

		// Declaramos dos colas en el broker a través del canal
		// recién creado llamada QUEUE_ENVIAR y QUEUE_RECIBIR
		// idempotente: solo se creará si no existe ya)
		// Se crea tanto en el servidorWeb como en spring, porque no
		// sabemos cuál se lanzará antes.
		channel.queueDeclare(QUEUE_ENVIAR, false, false, false, null); // Cola donde se actuará de emisor
		channel.queueDeclare(QUEUE_RECIBIR, false, false, false, null); // Cola donde se actuará de receptor

		// El objeto consumer guardará los mensajes que lleguen
		// a la cola QUEUE_RECIBIR hasta que los usemos
		consumer = new QueueingConsumer(channel);
	}

	public void cerrarConexionAMQP() throws IOException {
		channel.close();
		connection.close();
	}

	////////////////// ENVIAR ///////////////////////////


	public void enviarReserva(String espacio, ReservaDTO2 reserva) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		// Java object to JSON string
		String jsonString = mapper.writeValueAsString(reserva);
		String messageString = "crear-reserva/" + espacio + "/" + jsonString;
		channel.basicPublish("", QUEUE_ENVIAR, null, messageString.getBytes());
		System.out.println(" [x] Enviado '" + messageString + "'");
	}

	public void enviarAceptarReserva(String reserva) throws IOException {
		String messageString = "aceptar-reserva/" + reserva;
		channel.basicPublish("", QUEUE_ENVIAR, null, messageString.getBytes());
		System.out.println(" [x] Enviado '" + messageString + "'");
	}

	public void enviarCancelarReserva(String reserva) throws IOException {
		String messageString = "cancelar-reserva/" + reserva;
		channel.basicPublish("", QUEUE_ENVIAR, null, messageString.getBytes());
		System.out.println(" [x] Enviado '" + messageString + "'");
	}

	public void enviarGetReservas(String espacio) throws IOException {
		// Java object to JSON string
		String messageString = "reservas/" + espacio;
		channel.basicPublish("", QUEUE_ENVIAR, null, messageString.getBytes());
		System.out.println(" [x] Enviado '" + messageString + "'");
	}

	public void enviarGetReservasUsuario(String usuario) throws IOException {
		// Java object to JSON string
		String messageString = "reservas-usuario/" + usuario;
		channel.basicPublish("", QUEUE_ENVIAR, null, messageString.getBytes());
		System.out.println(" [x] Enviado '" + messageString + "'");
	}


	////////////////// RECIBIR ///////////////////////////

	public String recibirReserva() throws Exception {
		channel.basicConsume(QUEUE_RECIBIR, true, consumer);
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		String message = new String(delivery.getBody());

		System.out.println(" [x] Recibido '" + message + "'");
		return message;
	}

	public String recibirAceptarReserva() throws Exception {
		channel.basicConsume(QUEUE_RECIBIR, true, consumer);
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		String message = new String(delivery.getBody());

		System.out.println(" [x] Recibido '" + message + "'");
		return message;
	}

	public String recibirCancelarReserva() throws Exception {
		channel.basicConsume(QUEUE_RECIBIR, true, consumer);
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		String message = new String(delivery.getBody());

		System.out.println(" [x] Recibido '" + message + "'");
		return message;
	}

	public JSONArray recibirGetReservas() throws Exception {
		channel.basicConsume(QUEUE_RECIBIR, true, consumer);
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		String message = new String(delivery.getBody());

		JSONParser parser = new JSONParser();
		JSONArray json = (JSONArray) parser.parse(message);

		System.out.println(" [x] Recibido '" + message + "'");
		return json;
	}

	public JSONArray recibirGetReservasUsuario() throws Exception {
		channel.basicConsume(QUEUE_RECIBIR, true, consumer);
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		String message = new String(delivery.getBody());

		JSONParser parser = new JSONParser();
		JSONArray json = (JSONArray) parser.parse(message);

		System.out.println(" [x] Recibido '" + message + "'");
		return json;
	}

}