package cl.gob.ips.solicitudes_pago.config.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;

import cl.gob.ips.solicitudes_pago.service.JwtKeyProvider;

import com.nimbusds.jose.util.Base64URL;

import java.text.ParseException;

import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final JwtKeyProvider jwtKeyProvider;

    public JwtUtil(JwtKeyProvider jwtKeyProvider) {
        this.jwtKeyProvider = jwtKeyProvider;
    }


    public boolean verificarToken(String jwtToken) {
        /*try {
            // Construye la clave pública a partir de N y E en formato Base64URL
            RSAKey rsaKey = new RSAKey.Builder(new Base64URL(jwtKeyProvider.getN()), new Base64URL(jwtKeyProvider.getE())).build();

            SignedJWT signedJWT = SignedJWT.parse(jwtToken);

            // Verifica la firma con la clave pública
            return signedJWT.verify(new RSASSAVerifier(rsaKey));
        } catch (JOSEException | ParseException e) {
            e.printStackTrace();
            return false;
        }*/
    	return true;
    }
}
