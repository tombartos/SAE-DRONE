package fr.univtln.infomath.dronsim.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the response to a pilot
 * initialization request.
 * This class is used to transfer pilot initialization details from the server
 * to the client.
 *
 * @author Tom BARTIER
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PilotInitResp {
    private boolean success;
    private int clientId;
    private String jME_server_ip;

    /**
     * Returns whether the pilot initialization was successful.
     */
    public boolean getSuccess() {
        return success;
    }
}
