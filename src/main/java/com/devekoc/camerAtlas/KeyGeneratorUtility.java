package com.devekoc.camerAtlas; // Votre package racine

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Base64;

public class KeyGeneratorUtility {

    // Pour exécuter cette méthode, faites clic droit sur cette classe
    // dans IntelliJ et choisissez "Run 'main()'"
    public static void main(String[] args) {

        // Bien que SignatureAlgorithm soit dépréciée pour la signature,
        // elle est utilisée ici pour spécifier le type de clé à générer (HS256 = 32 octets).
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());

        System.out.println("--- CLÉ SECRÈTE JWT (BASE64) ---");
        System.out.println(base64Key);
        System.out.println("--------------------------------");
        System.out.println("Collez cette valeur pour 'application.security.jwt.secret-key' dans votre fichier .env");
    }
}