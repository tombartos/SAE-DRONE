package fr.univtln.infomath.dronsim.server.simulation.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.dronefleet.mavlink.MavlinkConnection;
import io.dronefleet.mavlink.MavlinkMessage;
import io.dronefleet.mavlink.common.GpsInput;
import io.dronefleet.mavlink.common.GpsInputIgnoreFlags;
import io.dronefleet.mavlink.common.ServoOutputRaw;
import io.dronefleet.mavlink.util.EnumValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArduSubControler implements Controler {
    private static final Logger log = LoggerFactory.getLogger(ArduSubControler.class);

    Socket socket;
    int[] motorThrottle;
    int[] gpsPos;
    MavlinkConnection connection;

    public ArduSubControler(String ip) throws UnknownHostException, IOException {
        this.socket = new Socket(ip, 5762);
        this.motorThrottle = new int[8];
        this.gpsPos = new int[] { 47, 85, -10 };

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        this.connection = MavlinkConnection.create(in, out);

        Thread.ofVirtual().start(this::recvLoop);
        Thread.ofVirtual().start(this::sendLoop);
    }

    private void recvLoop() {
        log.info("Starting receiving thread");
        MavlinkMessage<?> message = null;
        try {
            while ((message = connection.next()) != null) {
                switch (message.getPayload()) {
                    case ServoOutputRaw payload -> handleServoOutputRaw(payload);
                    default -> {
                        /* unsupported; ignore */ }
                }
            }
        } catch (IOException e) {
            log.warn("Receiving thread got IO error: ", e);
        }
        log.info("Terminating receiving thread");
    }

    private void sendLoop() {
        log.info("Starting sending thread");
        try {
            for (;;) {
                Thread.sleep(200);
                sendPosition();
            }
        } catch (IOException e) {
            log.warn("Sending thread got IO error: ", e);
        } catch (InterruptedException e) {
            log.info("Sending thread interrupted");
        }
        log.info("Terminating sending thread");
    }

    private void handleServoOutputRaw(ServoOutputRaw payload) {
        log.debug("Got servo output packet");
        final ServoOutputRaw servoOutputRaw = payload;
        motorThrottle[0] = servoOutputRaw.servo1Raw();
        motorThrottle[1] = servoOutputRaw.servo2Raw();
        motorThrottle[2] = servoOutputRaw.servo3Raw();
        motorThrottle[3] = servoOutputRaw.servo4Raw();
        motorThrottle[4] = servoOutputRaw.servo5Raw();
        motorThrottle[5] = servoOutputRaw.servo6Raw();
        motorThrottle[6] = servoOutputRaw.servo7Raw();
        motorThrottle[7] = servoOutputRaw.servo8Raw();
    }

    private void sendPosition() throws IOException {
        log.debug("Sending position");
        GpsInput gpsInput = GpsInput.builder()
                .timeUsec(BigInteger.valueOf(System.currentTimeMillis() * 1000))
                .gpsId(0)
                .ignoreFlags(EnumValue.create(GpsInputIgnoreFlags.GPS_INPUT_IGNORE_FLAG_VEL_HORIZ,
                        GpsInputIgnoreFlags.GPS_INPUT_IGNORE_FLAG_VEL_VERT,
                        GpsInputIgnoreFlags.GPS_INPUT_IGNORE_FLAG_SPEED_ACCURACY)) // Flags indiquant quels champs à
                                                                                   // ignorer
                .timeWeekMs(0) // Temps GPS (millisecondes depuis le début de la semaine GPS)
                .timeWeek(0) // Numéro de la semaine GPS
                .fixType(3) // 0-1: pas de fix, 2: fix 2D, 3: fix 3D. 4: 3D avec DGPS. 5: 3D avec RTK
                .lat((int) (gpsPos[0] * 1E7)) // Latitude en degrés * 10^7
                .lon((int) (gpsPos[1] * 1E7)) // Longitude en degrés * 10^7
                .alt((int) (gpsPos[2])) // Altitude en mm
                .hdop(1) // scaled by 100
                .vdop(1)
                .vn(0) // Vitesse en m/s dans la direction NORD
                .ve(0) // Vitesse en m/s dans la direction EST
                .vd(0) // Vitesse en m/s dans la direction DESCENTE
                .speedAccuracy(0) // Précision de la vitesse en m/s
                .horizAccuracy(0) // Précision horizontale en m
                .horizAccuracy(0) // Précision verticale en m
                .satellitesVisible(7) // Nombre de satellites visibles
                .build();
        connection.send2(255, 0, gpsInput);
    }

    public static void main(String[] args) throws IOException {
        String ip = "127.0.0.1"; // Default IP
        if (args.length > 0) {
            ip = args[0];
        }
        log.info("Connecting to " + ip);
        var ctrl = new ArduSubControler(ip);
        var gps = new int[] { 47, 85, -10 };
        try {
            for (;;) {
                Thread.sleep(1000);
                System.out.println(IntStream.range(0, 8)
                        .mapToDouble(ctrl::getMotorThrottle)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.joining(" ")));
                ctrl.setSensorValue(0, gps);
            }
        } catch (InterruptedException e) {
            log.info("Main thread interrupted");
        }
    }

    @Override
    public float getMotorThrottle(int motorIndex) {
        return ((float) motorThrottle[motorIndex] - 1500f) / 400f;
    }

    @Override
    public void setSensorValue(int captorIndex, int[] values) {
        switch (captorIndex) {
            case 0:
                for (int i = 0; i < gpsPos.length && i < values.length; i++) {
                    gpsPos[i] = values[i];
                }
                break;

            default:
                log.warn("Invalid sensor index ", captorIndex);
                break;
        }
    }

    @Override
    public void setSensorValue(int captorIndex, float[] values) {
        // TODO convertir coordonnées euclidiennes en coordonnées polaires
        switch (captorIndex) {
            case 0:
                for (int i = 0; i < 3 && i < values.length; i++) {
                    gpsPos[i] = (int) (values[i] * 1e7);
                }
                for (int i = 3; i < 6 && i < values.length; i++) {
                    gpsPos[i] = (int) (values[i]);
                }
                break;

            default:
                log.warn("Invalid sensor index ", captorIndex);
                break;
        }
        throw new UnsupportedOperationException("Unimplemented method 'setSensorValue'");
    }

    @Override
    public void setSensorValue(int captorIndex, long[] values) {
        log.warn("No sensors with long type");
    }

    @Override
    public void destroy() {
        // TODO interrupt threads
    }
}
