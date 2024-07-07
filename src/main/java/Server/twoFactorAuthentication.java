package Server;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class twoFactorAuthentication {

    private GoogleAuthenticator googleAuthenticator;
    private String secretKey;
    private final String FILE_PATH = "src/main/java/Client/.cache/.QrCode.png";
    private final String APP_NAME = "MemoliYT";
    private String email;

    public twoFactorAuthentication(String email) throws IOException, WriterException {
        googleAuthenticator = new GoogleAuthenticator();
        generateSecretKey();

        String qrCodeData = "otpauth://totp/" + this.APP_NAME + ":" + email + "?secret=" + this.secretKey + "&issuer=" + this.APP_NAME;
        generateQRCodeImage(qrCodeData);
    }

    public twoFactorAuthentication(String secretKey , String email) {
        googleAuthenticator = new GoogleAuthenticator();
        this.secretKey = secretKey;
    }


    public void generateSecretKey() {
        GoogleAuthenticatorKey key = googleAuthenticator.createCredentials();
        this.secretKey = key.getKey();
    }

    public void generateQRCodeImage(String barcodeText) throws WriterException, IOException {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200, hints);

        Path path = FileSystems.getDefault().getPath(this.FILE_PATH);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }
}
