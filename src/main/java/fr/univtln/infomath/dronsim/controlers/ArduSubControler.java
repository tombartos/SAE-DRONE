package fr.univtln.infomath.dronsim.controlers;

import java.io.IOException;
import java.lang.ref.Cleaner;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.univtln.infomath.dronsim.util.SharedStatic;

public class ArduSubControler implements Controler {
    private static final Logger log = LoggerFactory.getLogger(ArduSubControler.class);

    public static ArduSubControler create(int id) {
        var bridge = new Bridge();
        Runnable cleanup = () -> {
            bridge.stop();
        };
        var controler = new ArduSubControler(cleanup, id);
        bridge.ctrl = new WeakReference<>(controler);
        bridge.start();
        return controler;
    }

	private Cleaner.Cleanable cleanable;
	private int id;
    private int[] pwm = new int[32];

    private ArduSubControler(Runnable cleanup, int id) {
        this.cleanable = SharedStatic.cleaner.register(this, cleanup);
        this.id = id;
    }

    private static class Bridge {
        WeakReference<ArduSubControler> ctrl;
        private Thread inputThread;
        private DatagramSocket inputSocket;

		public void start() {
            this.inputThread = Thread.ofVirtual().start(() -> {
                try (var inputSocket = createInputSocket()) {
                    this.inputSocket = inputSocket;
                    inputLoop();
                }
            });
        }

        public void stop() {
            // TODO
            inputThread.interrupt();
        }

        private DatagramSocket createInputSocket() {
            DatagramSocket inputSocket;
            try {
                // TODO take port as argument or let system allocate port
                inputSocket = new DatagramSocket(9003, InetAddress.getLocalHost());
            } catch (java.net.UnknownHostException e) {
                throw new RuntimeException("No localhost", e);
            } catch (SocketException e) {
                // FIXME Probably merits something more graceful, as this could be caused by
                // lack of resources. Server should not crash.
                throw new RuntimeException("No localhost", e);
            }
            return inputSocket;
        }

        private void inputLoop() {
            var buf = new byte[ServoPacket.MAX_LEN];
            var datagram = new DatagramPacket(buf, 0);
            var pkt = new ServoPacket();
            for (;;) {
                try {
					inputSocket.receive(datagram);
                    pkt.parseDatagram(datagram);
				} catch (IOException e) {
                    log.info("IO error while receiving packet from [" + datagram.getAddress() + "]: " + e.getMessage());
                    continue;
				} catch (ServoPacket.ParseException e) {
                    log.info("Failed to parse packet from [" + datagram.getAddress() + "]: " + e.getMessage());
                    continue;
				}

                // TODO do something with framerate and packet sequence

                var pwm = ctrl.get().pwm;
                int i;
                for (i = 0; i < pkt.pwm.length; ++i)
                    pwm[i] = pkt.pwm[i];
                for (; i < pwm.length; ++i)
                    pwm[i] = 0;
            }
        }
    }

    private static class ServoPacket {
        static final short MAGIC_16 = 18458; // constant magic value
        static final short MAGIC_32 = 29569; // constant magic value

        static final int HDR_LEN = 64;
        static final int MIN_LEN = HDR_LEN + 16;
        static final int MAX_LEN = HDR_LEN + 32;

        short frame_rate;
        int frame_count;
        int[] pwm = new int[32];

        public void parseDatagram(DatagramPacket datagram) throws ParseException {
            ByteBuffer byteBuffer = ByteBuffer.wrap(datagram.getData());
            byteBuffer.order(ByteOrder.BIG_ENDIAN);

            final short magicValue = byteBuffer.getShort();
            final int pwmLength = switch (magicValue) {
                case MAGIC_16 -> 16;
                case MAGIC_32 -> 32;
                default -> throw new ParseException("Invalid Magic value");
            };

            if (pwmLength + HDR_LEN == datagram.getLength()) {
                this.frame_rate = byteBuffer.getShort();
                this.frame_count = byteBuffer.getInt();

                for (int i = 0; i < pwmLength; i++) {
                    this.pwm[i] = byteBuffer.getShort();
                }
            } else {
                throw new ParseException("Incorrect packet length");
            }
        }

        static class ParseException extends Exception {
            public ParseException() { }

            public ParseException(String message) {
                super(message);
            }
        }
    }

	@Override
	public float getMotorThrottle(int index) {
        // TODO this is from rover RC. Is this what we want?
        // also, these constants may depend on the model, so maybe we should do aways
        // with throttle, just return pwm and have methods in model to do convert to
        // force.
        return (pwm[index] - 1500) / 400.0f;
	}

	@Override
	public void destroy() {
        cleanable.clean();
	}
}
