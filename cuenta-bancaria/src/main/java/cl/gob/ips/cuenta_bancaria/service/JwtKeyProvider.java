package cl.gob.ips.cuenta_bancaria.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtKeyProvider {

    @Value("${jwt_public_n}")
    private String n;

    @Value("${jwt_public_e}")
    private String e;

    public String getN() {
        return n;
    }

    public String getE() {
        return e;
    }

}
