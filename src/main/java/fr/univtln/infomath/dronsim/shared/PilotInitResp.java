package fr.univtln.infomath.dronsim.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PilotInitResp {
    private boolean success;
    private int clientId;
    private String jME_server_ip;

    public boolean getSuccess() {
        return success;
    }
}
